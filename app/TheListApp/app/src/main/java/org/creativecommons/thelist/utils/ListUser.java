/* The List powered by Creative Commons

   Copyright (C) 2014, 2015 Creative Commons

   This program is free software: you can redistribute it and/or modify
   it under the terms of the GNU Affero General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU Affero General Public License for more details.

   You should have received a copy of the GNU Affero General Public License
   along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/

package org.creativecommons.thelist.utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.creativecommons.thelist.R;
import org.creativecommons.thelist.authentication.AccountGeneral;
import org.creativecommons.thelist.authentication.ServerAuthenticate;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ListUser implements ServerAuthenticate {
    public static final String TAG = ListUser.class.getSimpleName();

    private MessageHelper mMessageHelper;
    private SharedPreferencesMethods mSharedPref;
    private Context mContext;
    private Activity mActivity;

    private AccountManager am;
    public AlertDialog mAlertDialog;

    public ListUser(Context mc){
        mContext = mc;
        mSharedPref = new SharedPreferencesMethods(mContext);
        mMessageHelper = new MessageHelper(mContext);
        am = AccountManager.get(mContext);
    }

    public ListUser(Activity a) {
        mActivity = a;
        mContext = a;
        mSharedPref = new SharedPreferencesMethods(mContext);
        mMessageHelper = new MessageHelper(mContext);
        am = AccountManager.get(mContext);
    }

    //Callback for account signin/login
    public interface AuthCallback {
        void onAuthed(final String authtoken);
    }

    public interface LogInCallback {
        void onLoggedIn(final String authtoken, String userID);
    }

    public interface LogOutCallback {
        void onLoggedOut();
    }

    public interface TokenCallback {
        void onAuthed(final String authtoken);
    }

    public interface AnonymousUserCallback {
        void onSuccess();
        void onAccountFail(Boolean bol); //fail to add account
        void onFail(VolleyError error); //api request fail
    }

    public Boolean isAnonymousUser() {
        Account account = getAccount();

        if(account != null){
            String password = am.getPassword(account);
            if(password == null){
                return true;
            } else {
                return false;
            }

        } else {
            return true;
        }
    } //isAnonymousUser

    public String getAnonymousUserGUID(){

        //TODO: throw exception instead of check?
        if(isAnonymousUser()){
            Account account = getAccount();

            if(account != null){
                return account.name;
            } else {
                return null;
            }
        }

        return null;
    }

    @Deprecated
    public boolean isTempUser() {
        SharedPreferences sharedPref = mContext.getSharedPreferences
                (SharedPreferencesMethods.APP_PREFERENCES_KEY, Context.MODE_PRIVATE);

        if(mSharedPref.getUserId() == null) {
            return true;
        } else {
            return false;
        }
    } //isTempUser

    public String getUserID() {
        return mSharedPref.getUserId();
    } //getUserID

    // --------------------------------------------------------
    // ACCOUNT HELPER METHODS
    // --------------------------------------------------------

    public interface AvailableAccountCallback {
        void onResult(Account[] availableAccounts);
    }

    public String getUserIDFromAccount(Account ac){
        return am.getUserData(ac, AccountGeneral.USER_ID);
    } //getUserIDFromAccount

    public Boolean getAnalyticsOptOut(){
        Account account = getAccount();

        if(account != null){
            return Boolean.valueOf(am.getUserData(account, AccountGeneral.ANALYTICS_OPTOUT));
        } else {
            return false;
        }
    }

    public void setAnalyticsOptOut(Boolean bol){
        Account ac = getAccount();
        am.setUserData(ac, AccountGeneral.ANALYTICS_OPTOUT, bol.toString());
    }

    public Account getAccount(){
        Account matchingAccount = null;
        Account availableAccounts[] = am.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);

        //Check if account matches ID
        String userID = mSharedPref.getUserId();

        for(Account currentAccount: availableAccounts){
            String testID = getUserIDFromAccount(currentAccount);

            if(testID.equals(userID)) {
                matchingAccount = currentAccount;
                Log.v(TAG, "getAccount: Account Match Found");
                break;
            }
        }
        return matchingAccount;
    } //getAccount matching userID

    public int getAccountCount(){
        Account availableAccounts[] = am.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);
        return availableAccounts.length;
    }

    public String getAccountName(){
        Account account = getAccount();

        if(account != null){
            return account.name;
        } else {
            return mContext.getString(R.string.drawer_account_name_not_found);
        }
    }

    public String getAccountPassword(){
        Account account = getAccount();

        if(account != null){
            return am.getPassword(account);
        } else {
            return null;
        }
    }

    //Get available full accounts (all accounts non-anonymous)
    public void getAvailableFullAccounts(final AvailableAccountCallback callback){
        am.getAccountsByTypeAndFeatures(AccountGeneral.ACCOUNT_TYPE, AccountGeneral.FULL_ACCOUNT_FEATURES,
                new AccountManagerCallback<Account[]>() {
                    @Override
                     public void run(AccountManagerFuture<Account[]> accountManagerFuture) {
                        try {
                            callback.onResult(accountManagerFuture.getResult());

                        } catch (OperationCanceledException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (AuthenticatorException e) {
                            e.printStackTrace();
                        }
                    }
                }, null);

    } //getAvailableFullAccounts

    /**
     * Get auth token for existing account, if the account doesn’t exist, create new CCID account
     * You must already have a valid ID
     */
    public void getAuthed(final AuthCallback callback){
        Log.d(TAG, "Getting session token");

        if(isAnonymousUser()){
            Log.v(TAG, "IS ANON USER TRUE");
            addNewFullAccount(AccountGeneral.ACCOUNT_TYPE, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, new AuthCallback() {
                @Override
                public void onAuthed(String authtoken) {
                    Log.v(TAG, "> getAuthed > addNewFullAccount token: " + authtoken);
                    callback.onAuthed(authtoken);
                }
            });

        } else {
            Account account = getAccount();

            if(account == null){
                Log.v(TAG, "getAuthed > getAccount > account is null");

                addNewFullAccount(AccountGeneral.ACCOUNT_TYPE, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, new AuthCallback() {
                    @Override
                    public void onAuthed(String authtoken) {
                        Log.v(TAG, "> getAuthed > addNewFullAccount token: " + authtoken);
                        callback.onAuthed(authtoken);
                    }
                });
            }

            am.getAuthToken(account, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, null, mActivity,
                    new AccountManagerCallback<Bundle>() {
                        @Override
                        public void run(AccountManagerFuture<Bundle> future) {
                            try {
                                Bundle bundle = future.getResult();
                                String authtoken = bundle.getString(AccountManager.KEY_AUTHTOKEN);
                                Log.v(TAG, "> getAuthed > getAuthToken from existing account: " + authtoken);
                                callback.onAuthed(authtoken);
                            } catch (OperationCanceledException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (AuthenticatorException e) {
                                e.printStackTrace();
                            }
                        }
                    }, null);
        }
    } //GetAuthed

    /**
     * Get auth token for existing account (assumes pre-existing account)
     * @param callback
     */
    public void getToken(final TokenCallback callback) {
        Log.d(TAG, "getToken > getting session token");
        Account account = getAccount();

        if(account == null){
            Log.d(TAG, "getToken > getAccount > account is null");
            return;
        }

        am.getAuthToken(account, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, null, mActivity,
                new AccountManagerCallback<Bundle>() {
                    @Override
                    public void run(AccountManagerFuture<Bundle> future) {
                        try {
                            Bundle bundle = future.getResult();
                            String authtoken = bundle.getString(AccountManager.KEY_AUTHTOKEN);
                            Log.v(TAG, "> getToken, token received: " + authtoken);
                            callback.onAuthed(authtoken);
                        } catch (OperationCanceledException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (AuthenticatorException e) {
                            e.printStackTrace();
                        }
                    }
                }, null);
    } //getToken

    /**
     * Show all the accounts registered on the account manager. Request an auth token upon user select.
     */
    public void showAccountPicker(final Account[] availableAccounts, final AuthCallback callback) {

        String name[] = new String[availableAccounts.length];

        for (int i = 0; i < availableAccounts.length; i++) {
            name[i] = availableAccounts[i].name;
            Log.v(TAG, "ACCOUNT NAME: " + name[i]);
        }

        // Account picker
        mAlertDialog = new AlertDialog.Builder(mContext).setTitle("Pick Account")
                .setCancelable(true)
                .setPositiveButton("Add New", new OkOnClickListener())
                .setNegativeButton("Cancel", new CancelOnClickListener())
                .setAdapter(new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, name),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                am.getAuthToken(availableAccounts[which],
                                        AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS,
                                        null, mActivity,
                                        new AccountManagerCallback<Bundle>() {
                                            @Override
                                            public void run(AccountManagerFuture<Bundle> future) {
                                                try {
                                                    Bundle bundle = future.getResult();
                                                    String authtoken = bundle.getString(AccountManager.KEY_AUTHTOKEN);
                                                    Log.v(TAG, " > showAccountPicker > getAuthToken from existing account: " + authtoken);
                                                    callback.onAuthed(authtoken);
                                                } catch (OperationCanceledException e) {
                                                    e.printStackTrace();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                } catch (AuthenticatorException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }, null);
                            }
                        }).create();
        mAlertDialog.show();
    } //showAccountPicker

    //Listeners for AccountPicker Dialog
    private final class CancelOnClickListener implements
            DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialog, int which) {
            mAlertDialog.dismiss();
        }
    } //for account picker

    private final class OkOnClickListener implements
            DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialog, int which) {
            addNewFullAccount(AccountGeneral.ACCOUNT_TYPE, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, new AuthCallback() {
                @Override
                public void onAuthed(String authtoken) {
                    Log.d(TAG, " > showAccountPicker DialogInterface > addNewFullAccount, token received: " + authtoken);
                }
            });
        }
    } //for account picker

    //AddFullNewAccount
    public void addNewFullAccount(String accountType, String authTokenType, final AuthCallback callback) {
        final AccountManagerFuture<Bundle> future = am.addAccount(accountType, authTokenType,
                AccountGeneral.FULL_ACCOUNT_FEATURES,
                null, mActivity, new AccountManagerCallback<Bundle>() {
                    @Override
                    public void run(AccountManagerFuture<Bundle> future) {
                        try {
                            Bundle bnd = future.getResult();

                            Log.d(TAG, " > addNewFullAccount Bundle received: " + bnd);
                            callback.onAuthed(bnd.getString(AccountManager.KEY_AUTHTOKEN));

                        } catch (Exception e) {
                            Log.d(TAG, "addNewFullAccount > Error adding new account: " + e.getMessage());
                            //TODO:
                        }
                    }
                }, null);

    } //addNewFullAccount


    //Helper for testing
    public void removeAccount(final LogOutCallback callback){

        am.removeAccount(getAccount(), new AccountManagerCallback<Boolean>() {
            @Override
            public void run(AccountManagerFuture<Boolean> future) {
                Log.d(TAG, " > removeAccount, accounts removed");
                mSharedPref.ClearAllSharedPreferences();
                callback.onLoggedOut();
            }
        }, null);

    } //removeAccount (for testing)


    // --------------------------------------------------------
    // LOG REQUESTS
    // --------------------------------------------------------

    //Create Anonymous User
    public void createAnonymousUser(final AnonymousUserCallback callback){
        if(!(NetworkUtils.isNetworkAvailable(mContext))){
            mMessageHelper.networkFailMessage();
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(mContext);
        String url = ApiConstants.LOGIN_USER;

        StringRequest createAnonUserRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v(TAG, "> createAnonymousUser > onResponse: " + response);

                        if(response != null){

                            try {
                                JSONObject res = new JSONObject(response);
                                String guid = res.getString(ApiConstants.USER_EMAIL);
                                String authtoken = res.getString(ApiConstants.USER_TOKEN);
                                String userID = res.getString(ApiConstants.USER_ID);

                                mSharedPref.setUserID(userID);

                                final Account account = new Account(guid, AccountGeneral.ACCOUNT_TYPE);

                                Bundle userData = new Bundle();
                                userData.putString(AccountGeneral.ANALYTICS_OPTOUT,
                                        mSharedPref.getAnalyticsOptOut().toString());
                                userData.putString(AccountGeneral.USER_ID, userID);

                                Boolean accountAdded = am.addAccountExplicitly(account, null, userData);

                                if(accountAdded){
                                    am.setAuthToken(account, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, authtoken);
                                    callback.onSuccess();
                                } else {
                                    callback.onAccountFail(accountAdded);
                                }

                            } catch (JSONException e) {
                                //TODO: add error toast
//                                mMessageHelper.showDialog(mContext, mContext.getString
//                                                (R.string.oops_label),
//                                        mContext.getString(R.string.general_error));
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.v(TAG, "> createAnonymousUser > onErrorResponse: " + error.getMessage());
                callback.onFail(error);
                //TODO: callback or other error message
            }
        });
        queue.add(createAnonUserRequest);
    } //createAnonymousUser

    //LOG IN USER with credentials
    @Override
    public void userLogIn(final String email, final String pass, final String guid, String authType, final LogInCallback callback){
        if(!(NetworkUtils.isNetworkAvailable(mContext))){
            mMessageHelper.networkFailMessage();
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(mContext);
        String url = ApiConstants.LOGIN_USER;

        StringRequest userLogInRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Get Response
                        if(response == null || response.equals("null")) {
                            mMessageHelper.showDialog(mContext, "YOU SHALL NOT PASS",
                                    "Sure you got your email/password combo right?");
                        } else {
                            Log.v("RESPONSE FOR LOGIN: ", response);
                            try {
                                JSONObject res = new JSONObject(response);
                                String sessionToken = res.getString(ApiConstants.USER_TOKEN);
                                String userID = res.getString(ApiConstants.USER_ID);
                                //Save userID in sharedPreferences
                                Log.d(TAG, " > USER SIGN IN > setting userid: " + userID);

                                //Pass authtoken back to activity
                                callback.onLoggedIn(sessionToken, userID);

                            } catch (JSONException e) {
                                Log.v(TAG,e.getMessage());
                                mMessageHelper.showDialog(mContext, mContext.getString
                                                (R.string.login_error_exception_title),
                                        mContext.getString(R.string.login_error_exception_message));
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mMessageHelper.showDialog(mContext, mContext.getString(R.string.login_error_title),
                        mContext.getString(R.string.login_error_message));
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                if(guid != null){
                    params.put(ApiConstants.USER_GUID, guid);
                }

                params.put(ApiConstants.USER_NAME, email);
                params.put(ApiConstants.USER_PASSWORD, pass);
                return params;
            }
        };
        queue.add(userLogInRequest);
    } //userLogIn

    //TODO: SIGN UP USER
    @Override
    public void userSignUp(String email, String pass, String guid, String authType, final AuthCallback callback) throws Exception {
        if(!(NetworkUtils.isNetworkAvailable(mContext))){
            mMessageHelper.showDialog(mContext, mActivity.getString(R.string.error_network_title),
                    mActivity.getString(R.string.error_network_message));
            return;
        }

        //TODO: actually register user
    }

} //ListUser
