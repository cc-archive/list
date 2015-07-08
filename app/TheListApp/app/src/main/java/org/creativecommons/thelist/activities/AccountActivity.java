/* The List powered by Creative Commons

   Copyright (C) 2014, 2015 Creative Commons Corporation

   This program is free software: you can redistribute it and/or modify
   it under the terms of either the GNU Affero General Public License or
   the GNU General Public License as published by the
   Free Software Foundation, either version 3 of the Licenses, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  

   You should have received a copy of the GNU General Public License and
   the GNU Affero General Public License along with this program.  

   If not, see <http://www.gnu.org/licenses/>.

*/

package org.creativecommons.thelist.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.google.android.gms.analytics.GoogleAnalytics;

import org.creativecommons.thelist.R;
import org.creativecommons.thelist.authentication.AccountGeneral;
import org.creativecommons.thelist.authentication.AesCbcWithIntegrity;
import org.creativecommons.thelist.fragments.AccountFragment;
import org.creativecommons.thelist.fragments.TermsFragment;
import org.creativecommons.thelist.utils.ListApplication;
import org.creativecommons.thelist.utils.ListUser;
import org.creativecommons.thelist.utils.SharedPreferencesMethods;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

import static org.creativecommons.thelist.authentication.AccountGeneral.ARG_ACCOUNT_NAME;
import static org.creativecommons.thelist.authentication.AccountGeneral.ARG_AUTH_TYPE;
import static org.creativecommons.thelist.authentication.AccountGeneral.ARG_IS_ADDING_NEW_ACCOUNT;
import static org.creativecommons.thelist.authentication.AccountGeneral.PARAM_USER_PASS;
import static org.creativecommons.thelist.authentication.AesCbcWithIntegrity.encrypt;
import static org.creativecommons.thelist.authentication.AesCbcWithIntegrity.generateKey;


public class AccountActivity extends org.creativecommons.thelist.authentication.AccountAuthenticatorActivity
        implements AccountFragment.AuthListener, TermsFragment.TermsClickListener {
    private final String TAG = this.getClass().getSimpleName();
    Context mContext;

    private AccountManager mAccountManager;
    private String mAuthTokenType;
    private ListUser mCurrentUser;
    private SharedPreferencesMethods mSharedPref;
    private Bundle newUserBundle;

    //UI Elements
    FrameLayout mFrameLayout;
    EditText mPasswordTextField;

    //Fragments
    //TODO: agree to terms
    //TermsFragment termsFragment = new TermsFragment();

    // --------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        mContext = this;
        mAccountManager = AccountManager.get(getBaseContext());
        mCurrentUser = new ListUser(AccountActivity.this);
        mSharedPref = new SharedPreferencesMethods(mContext);

        //Google Analytics Tracker
        ((ListApplication) getApplication()).getTracker(ListApplication.TrackerName.GLOBAL_TRACKER);

        //UI Elements
        mFrameLayout = (FrameLayout) findViewById(R.id.fragment_container);
        //Get account information (intent is coming from ListAuthenticator…always call AuthToken)
        String accountName = getIntent().getStringExtra(ARG_ACCOUNT_NAME);
        mAuthTokenType = getIntent().getStringExtra(ARG_AUTH_TYPE);
        if (mAuthTokenType == null)
            mAuthTokenType = AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS;

        //Prepopulate accountName if it exists…I think
        if (accountName != null) {
            ((EditText) findViewById(R.id.accountName)).setText(accountName);
        }

        //auto load loginFragment
        AccountFragment accountFragment = new AccountFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container,accountFragment)
                .commit();
                mFrameLayout.setClickable(true);
    } //OnCreate

    @Override
    protected void onStart(){
        super.onStart();
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    protected void onStop(){
        super.onStop();
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }

    @Override
    public void onUserSignedIn(Bundle userData) {
        final Intent res = new Intent();
        res.putExtras(userData);
        finishLogin(res);
    } //onUserSignedIn

    //Pass bundle constructed on request success
    private void finishLogin(Intent intent) {
        Log.d("THE LIST", TAG + "> finishLogin");

        String accountEmail = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        String accountPassword = intent.getStringExtra(PARAM_USER_PASS);
        final Account account = new Account(accountEmail, intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));


        //TODO: is this working or skipping to setPassword?
        if (getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false)) {
            Log.d(TAG, "> finishLogin > addAccountExplicitly");
            String authtoken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
            String authtokenType = mAuthTokenType;

            // Creating the account on the device and setting the auth token we got
            // (Not setting the auth token will cause another call to the server to authenticate the user)

            //Generate Key
            AesCbcWithIntegrity.SecretKeys key;
            try {
                key = generateKey();
                AesCbcWithIntegrity.CipherTextIvMac cryptoPass = encrypt(accountPassword, key);

                //Create new account
                mAccountManager.addAccountExplicitly(account, cryptoPass.toString(), null);
                mAccountManager.setAuthToken(account, authtokenType, authtoken);
                mAccountManager.setUserData(account, AccountGeneral.USER_ID, mCurrentUser.getUserID());
                mAccountManager.setUserData(account, AccountGeneral.ANALYTICS_OPTOUT,
                        String.valueOf(mSharedPref.getAnalyticsOptOut()));
                Log.v(TAG, "> finishLogin > setUserData, token: " + authtoken);

                //Save Key for later use
                SharedPreferencesMethods sharedPref = new SharedPreferencesMethods(mContext);
                sharedPref.saveKey(key.toString());

                //Add items chosen before login to userlist
                mCurrentUser.addSavedItemsToUserList();
                //TODO: also add category preferences (+ callback?) for these two?
                //mCurrentUser.addSavedCategoriesToUserList();

            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        } else {
            Log.d(TAG, "> finishLogin > setPassword");
            mAccountManager.setPassword(account, accountPassword);
        }

        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    } //finishLogin

    @Override
    public void onCancelLogin() {
        setAccountAuthenticatorResult(null);
        setResult(RESULT_CANCELED, null);
        finish(); //this should take you to previous activity
    }

    @Override
    public void onTermsClicked() {
        //Start UploadFragment and Upload photo
        //Sign in User and
//        String email;
//        String password;
//        String authType;
//
//        mCurrentUser.userSignUp();
//
//        final Intent res = new Intent();
//        res.putExtras(userData);
//        finishLogin(res);
    } //onTermsClicked

    @Override
    public void onTermsCancelled() {
        setAccountAuthenticatorResult(null);
        setResult(RESULT_CANCELED, null);
        finish();
    }

    @Override
    public void onUserSignedUp(Bundle userData) {
        //TODO: do something with bundle when user agrees to terms
        newUserBundle = userData;
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_account_authenticator, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
} //AuthenticatorActivity
