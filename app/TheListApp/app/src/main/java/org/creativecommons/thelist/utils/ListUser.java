/* The List powered by Creative Commons

   Copyright (C) 2014 Creative Commons

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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.creativecommons.thelist.R;
import org.creativecommons.thelist.activities.RandomActivity;
import org.creativecommons.thelist.authentication.AccountGeneral;
import org.creativecommons.thelist.authentication.ServerAuthenticate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ListUser implements ServerAuthenticate {
    public static final String TAG = ListUser.class.getSimpleName();

    private RequestMethods requestMethods;
    private SharedPreferencesMethods sharedPreferencesMethods;
    private Context mContext;
    private Activity mActivity;

    //TODO: clean up if unecessary
    private AccountManager am;
    public AlertDialog mAlertDialog;

    public ListUser(Context mc){
        mContext = mc;
        requestMethods = new RequestMethods(mContext);
        sharedPreferencesMethods = new SharedPreferencesMethods(mContext);
        am = AccountManager.get(mContext);
    }

    public ListUser(Activity a) {
        mActivity = a;
        mContext = a;
        requestMethods = new RequestMethods(mContext);
        sharedPreferencesMethods = new SharedPreferencesMethods(mContext);
        am = AccountManager.get(mContext);
    }

    //Callback for account signin/login
    public interface AuthCallback {
        void onSuccess(final String authtoken);
    }

    public boolean isTempUser() {
        //TODO: Check if User account exists in AccountManager <== Do we need this?
        SharedPreferences sharedPref = mContext.getSharedPreferences
                (SharedPreferencesMethods.APP_PREFERENCES_KEY, Context.MODE_PRIVATE);

        if(sharedPreferencesMethods.getUserId() == null) {
            return true;
        } else {
            return false;
        }
    } //isTempUser

//    public void setUserID(String id) {
//        sharedPreferencesMethods.saveUserID(id);
//    }

    public String getUserID() {
        return sharedPreferencesMethods.getUserId();
    } //getUserID

    // --------------------------------------------------------
    // ACCOUNT HELPER METHODS
    // --------------------------------------------------------

    public String getUserIDFromAccount(Account ac){
        return am.getUserData(ac, AccountGeneral.USER_ID);
    } //getUserIDFromAccount

    public Account getAccount(String userid){
        Account matchingAccount = null;
        Account availableAccounts[] = am.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);

        //Check if account matches ID
        String userID = sharedPreferencesMethods.getUserId();

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

    /**
     * Get auth token for existing account, if the account doesn’t exist, create new CCID account
     * You must already have a valid ID
     */
    public void getAuthed(final AuthCallback callback){
        Log.d(TAG, "Getting session token");
        //sessionComplete = false;

        if(isTempUser()){
            addNewAccount(AccountGeneral.ACCOUNT_TYPE, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, new AuthCallback() {
                @Override
                public void onSuccess(String authtoken) {
                    Log.v(TAG, "> getAuthed > addNewAccount token: " + authtoken);
                    callback.onSuccess(authtoken);
                }
            });

        } else {
            Account account = getAccount(getUserID());

            if(account == null){
                Log.v(TAG, "getToken > getAccount > account is null");
                return;
            }

            am.getAuthToken(account, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, null, mActivity,
                    new AccountManagerCallback<Bundle>() {
                        @Override
                        public void run(AccountManagerFuture<Bundle> future) {
                            try {
                                Bundle bundle = future.getResult();
                                String authtoken = bundle.getString(AccountManager.KEY_AUTHTOKEN);
                                Log.v(TAG, "> getAuthed > getAuthToken from existing account: " + authtoken);
                                callback.onSuccess(authtoken);
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
     */
    public void getToken(final AuthCallback callback) {
        Log.d(TAG, "getToken > getting session token");
        //sessionComplete = false;
        //TODO: match userID to account with the same userID store in AccountManager
        Account account = getAccount(getUserID());

        if(account == null){
            Log.v(TAG, "getToken > getAccount > account is null");
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
                            callback.onSuccess(authtoken);
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
    public void showAccountPicker(final AuthCallback callback) {
        //@param final boolean invalidate, final String authTokenType // mInvalidate = invalidate;
        final Account availableAccounts[] = am.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);

        if (availableAccounts.length == 0) {
            //TODO: Show other dialog to add account
            addNewAccount(AccountGeneral.ACCOUNT_TYPE,
                    AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, new AuthCallback() {
                @Override
                public void onSuccess(String authtoken) {
                    Log.d(TAG, " > showAccountPicker (no accounts) > addNewAccount, " +
                            "token received: " + authtoken);
                }
            });
        } else {
            String name[] = new String[availableAccounts.length];
            for (int i = 0; i < availableAccounts.length; i++) {
                name[i] = availableAccounts[i].name;
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

//                    TODO: what is this for?
//                    if(invalidate)
//                        invalidateAuthToken(availableAccounts[which], authTokenType);
//                    else
//                        getExistingAccountAuthToken(availableAccounts[which], authTokenType);

                    am.getAuthToken(availableAccounts[which], AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, null, mActivity,
                            new AccountManagerCallback<Bundle>() {
                                @Override
                                public void run(AccountManagerFuture<Bundle> future) {
                                    try {
                                        Bundle bundle = future.getResult();
                                        String authtoken = bundle.getString(AccountManager.KEY_AUTHTOKEN);
                                        Log.v(TAG, " > showAccountPicker > getAuthToken from existing account: " + authtoken);
                                        callback.onSuccess(authtoken);
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
        }
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
            addNewAccount(AccountGeneral.ACCOUNT_TYPE, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, new AuthCallback() {
                @Override
                public void onSuccess(String authtoken) {
                    Log.d(TAG, " > showAccountPicker DialogInterface > addNewAccount, token received: " + authtoken);
                }
            });
        }
    } //for account picker

    //AddNewAccount
    public void addNewAccount(String accountType, String authTokenType, final AuthCallback callback) {

        final AccountManagerFuture<Bundle> future = am.addAccount(accountType, authTokenType, null, null, mActivity, new AccountManagerCallback<Bundle>() {
            @Override
            public void run(AccountManagerFuture<Bundle> future) {
                try {
                    Bundle bnd = future.getResult();
                    Log.d(TAG, " > addNewAccount Bundle received: " + bnd);
                    callback.onSuccess(bnd.getString(AccountManager.KEY_AUTHTOKEN));

                } catch (Exception e) {
                    e.printStackTrace();
                    //Log.d(TAG, e.getMessage());
                }
            }
        }, null);
    }

    //Helper for testing (TODO: remove in public releases)
    public void removeAccounts(final AuthCallback callback){
        Account[] availableAccounts = am.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);
        for(Account account:availableAccounts){
            am.removeAccount(account, new AccountManagerCallback<Boolean>() {
                @Override
                public void run(AccountManagerFuture<Boolean> future) {
                    Log.d(TAG, " > removeAccounts, accounts removed");
                    callback.onSuccess("removedAccounts");
                }
            }, null);
        }
    } //removeAccounts (for testing)


    // --------------------------------------------------------
    // LOG REQUESTS
    // --------------------------------------------------------

    //LOG IN USER
    @Override
    public void userSignIn(final String email, final String pass, String authType, final AuthCallback callback){
        if(!(requestMethods.isNetworkAvailable())){
            requestMethods.showDialog(mContext, mActivity.getString(R.string.error_network_title),
                    mActivity.getString(R.string.error_network_message));
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(mContext);
        String url = ApiConstants.LOGIN_USER;
        StringRequest userSignInRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Get Response
                        if(response == null || response.equals("null")) {
                            Log.v("RESPONSE NULL HERE: ", response);
                            requestMethods.showDialog(mContext,"YOU SHALL NOT PASS",
                                    "Sure you got your email/password combo right?");
                        } else {
                            Log.v("RESPONSE FOR LOGIN: ", response);
                            try {
                                JSONObject res = new JSONObject(response);
                                String sessionToken = res.getString(ApiConstants.USER_TOKEN);
                                String userID = res.getString(ApiConstants.USER_ID);
                                //Save userID in sharedPreferences
                                Log.d(TAG, " > USER SIGN IN > setting userid: " + userID);
                                sharedPreferencesMethods.saveUserID(userID);
                                //Add items chosen before login to userlist

                                //TODO: also add category preferences + callback for these two?
                                addSavedItemsToUserList();

                                //pass authtoken back to activity
                                callback.onSuccess(sessionToken);

                            } catch (JSONException e) {
                                Log.v(TAG,e.getMessage());
                                //TODO: add proper error message
                                requestMethods.showDialog(mContext, mContext.getString
                                                (R.string.login_error_exception_title),
                                        mContext.getString(R.string.login_error_exception_message));
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                requestMethods.showDialog(mContext, mContext.getString(R.string.login_error_title),
                        mContext.getString(R.string.login_error_message));
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", email);
                params.put(ApiConstants.USER_PASSWORD, pass);
                return params;
            }
        };
        queue.add(userSignInRequest);
    } //userSignIn

    //TODO: SIGN UP USER
    @Override
    public void userSignUp(String email, String pass, String authType, final AuthCallback callback) throws Exception {
        if(!(requestMethods.isNetworkAvailable())){
            requestMethods.showDialog(mContext, mActivity.getString(R.string.error_network_title),
                    mActivity.getString(R.string.error_network_message));
            return;
        }

        //TODO: actually register user
    }


    //TODO: do we need this?
//    public void logOut() {
//        //Clear session-relevant sharedPreferences
//        //TODO:this is just userid, new user profile data will need to be cleared if added
//        sharedPreferencesMethods.ClearAllSharedPreferences();
//
//        //TODO: take you back to startActivity?
//        Intent intent = new Intent(mContext, StartActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        mContext.startActivity(intent);
//    }

    // --------------------------------------------------------
    // USER LIST REQUESTS
    // --------------------------------------------------------

    //Add all list items to userlist (previously temp stored items)
    public void addSavedItemsToUserList(){
        JSONArray listItemPref;
        listItemPref = sharedPreferencesMethods.RetrieveUserItemPreference();

        try{
            if (listItemPref != null && listItemPref.length() > 0) {
                Log.v("LIST ITEM PREF: ", listItemPref.toString());
                for (int i = 0; i < listItemPref.length(); i++) {
                    Log.v("ITEMS", "ARE BEING ADDED");
                    addItemToUserList(listItemPref.getString(i));
                }
            }
        } catch(JSONException e){
            Log.d(TAG, "> addSavedItemsToUserList: " + e.getMessage());
        }
    } //addSavedItemsToUserList

    //Add SINGLE random item to user list
    public void addItemToUserList(final String itemID) {
        if(!(requestMethods.isNetworkAvailable())){
            requestMethods.showDialog(mContext,mActivity.getString(R.string.error_network_title),
                    mActivity.getString(R.string.error_network_message));
            return;
        }
        //Get sessionToken
        getToken(new AuthCallback() {
            @Override
            public void onSuccess(final String authtoken) {
                RequestQueue queue = Volley.newRequestQueue(mContext);
                String url = ApiConstants.ADD_ITEM + getUserID() + "/" + itemID;

                //Add single item to user list
                StringRequest postItemRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                //get Response
                                Log.v("Response: ", response);
                                Log.v(TAG,"AN ITEM IS BEING ADDED");
                                //TODO: on success remove the item from the sharedPreferences
                                sharedPreferencesMethods.RemoveUserItemPreference(itemID);

                                //Toast: Confirm List Item has been added if it is RandomActivity
                                if(mActivity.getClass().getSimpleName()
                                        .equals(RandomActivity.class.getSimpleName())){
                                    final Toast toast = Toast.makeText(mContext,
                                            "Added to Your List", Toast.LENGTH_SHORT);
                                    toast.show();
                                    new android.os.Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            toast.cancel();
                                        }
                                    }, 1500);
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse (VolleyError error){
                        //TODO: Add “not successful“ toast
                        requestMethods.showDialog(mContext,
                                mContext.getString(R.string.error_title),
                                mContext.getString(R.string.error_message));
                        Log.d(TAG, " > addItemToUserList > onErrorResponse: " + error.getMessage());
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put(ApiConstants.USER_TOKEN, authtoken);
                        return params;
                    }
                };
                queue.add(postItemRequest);
            }
        });

    } //addItemToUserList

    //REMOVE SINGLE item from user list
    //TODO: FILL IN WITH REAL API INFO
    public void removeItemFromUserList(final String itemID){

        if(isTempUser()){
            //If not logged in, remove item from sharedPreferences
            sharedPreferencesMethods.RemoveUserItemPreference(itemID);

        } else { //If logged in, remove from DB
            if(!(requestMethods.isNetworkAvailable())){
                requestMethods.showDialog(mContext,mActivity.getString(R.string.error_network_title),
                        mActivity.getString(R.string.error_network_message));
                return;
            }

            //Get sessionToken
            getToken(new AuthCallback() {
                @Override
                public void onSuccess(final String authtoken) { //getToken and then start request
                    RequestQueue queue = Volley.newRequestQueue(mContext);
                    String url = ApiConstants.REMOVE_ITEM + getUserID() + "/" + itemID;

                    StringRequest deleteItemRequest = new StringRequest(Request.Method.POST, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    //get Response
                                    Log.v("Response: ", response);
                                    Log.v(TAG, "AN ITEM IS BEING REMOVED");
                                    //TODO: do something with response?
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //TODO: Add “not successful“ toast
                            Log.d("Delete Item Failed: ", error.getMessage());
                            requestMethods.showDialog(mContext, mContext.getString(R.string.error_title),
                                    mContext.getString(R.string.error_message));
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put(ApiConstants.USER_TOKEN, authtoken);
                            return params;
                        }
                    };
                    queue.add(deleteItemRequest);
                }
            });

        }
    } //removeItemFromUserList

    // --------------------------------------------------------
    // USER CATEGORY REQUESTS
    // --------------------------------------------------------

    //Add all categories to userlist
    public void addSavedCategoriesToUserAccount(){
        //TODO: make request when endpoint is available
    }

} //ListUser
