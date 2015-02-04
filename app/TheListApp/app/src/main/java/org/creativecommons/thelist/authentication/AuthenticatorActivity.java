package org.creativecommons.thelist.authentication;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.FrameLayout;

import org.creativecommons.thelist.MainActivity;
import org.creativecommons.thelist.R;
import org.creativecommons.thelist.utils.RequestMethods;
import org.creativecommons.thelist.utils.SharedPreferencesMethods;

import fragments.LoginFragment;
import static org.creativecommons.thelist.authentication.AccountGeneral.*;


public class AuthenticatorActivity extends org.creativecommons.thelist.authentication.AccountAuthenticatorActivity implements LoginFragment.AuthListener {
    private RequestMethods requestMethods;
    private SharedPreferencesMethods sharedPreferencesMethods;
    //private ListUser mCurrentUser;
    Context mContext;

    private final int REQ_SIGNUP = 1;

    private AccountManager mAccountManager;
    private String mAuthTokenType;

    private final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_authenticator);
        mContext = this;

        mAccountManager = AccountManager.get(getBaseContext());

        //Get account information
        String accountName = getIntent().getStringExtra(ARG_ACCOUNT_NAME);
        mAuthTokenType = getIntent().getStringExtra(ARG_AUTH_TYPE);
        if (mAuthTokenType == null)
            mAuthTokenType = AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS;

        if (accountName != null) {
            ((EditText) findViewById(R.id.accountName)).setText(accountName);
        }

        //auto load loginFragment
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.fragment_container);
        LoginFragment loginFragment = new LoginFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container,loginFragment)
                .commit();
                frameLayout.setClickable(true);
    } //OnCreate

    @Override
    public void onUserSignedIn(Bundle userData) {
        final Intent res = new Intent();
        res.putExtras(userData);
        finishLogin(res);
    } //onUserSignedIn

    //Pass bundle constructed on request success
    private void finishLogin(Intent intent) {
        Log.d("THE LIST", TAG + "> finishLogin");

        String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        String accountPassword = intent.getStringExtra(PARAM_USER_PASS);
        final Account account = new Account(accountName, intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));

        if (getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false)) {
            Log.d("THE LIST", TAG + "> finishLogin > addAccountExplicitly");
            String authtoken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
            String authtokenType = mAuthTokenType;

            // Creating the account on the device and setting the auth token we got
            // (Not setting the auth token will cause another call to the server to authenticate the user)
            //TODO: encrypt password!

            mAccountManager.addAccountExplicitly(account, accountPassword, null);
            mAccountManager.setAuthToken(account, authtokenType, authtoken);
        } else {
            Log.d("THE LIST", TAG + "> finishLogin > setPassword");
            mAccountManager.setPassword(account, accountPassword);
        }

        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    } //finishLogin

//    public void submit() {
//        final String userName = ((EditText) findViewById(R.id.accountName)).getText().toString();
//        final String userPass = ((EditText) findViewById(R.id.accountPassword)).getText().toString();
//        final String accountType = getIntent().getStringExtra(ARG_ACCOUNT_TYPE);
//
//        ListUser mCurrentUser = new ListUser(mContext);
//        String authtoken;
//        Bundle data = new Bundle();
//
//        try {
//            mCurrentUser.userSignIn(userName, userPass, mAuthTokenType,);
//
//            data.putString(AccountManager.KEY_ACCOUNT_NAME, userName);
//            data.putString(AccountManager.KEY_ACCOUNT_TYPE, accountType);
//            data.putString(AccountManager.KEY_AUTHTOKEN, authToken);
//            data.putString(PARAM_USER_PASS, userPass);
//
//        } catch (Exception e) {
//            data.putString(KEY_ERROR_MESSAGE, e.getMessage());
//        }
//
//        final Intent res = new Intent();
//        res.putExtras(data);
//
//        finishLogin(res);
//    }


    @Override
    public void onCancelLogin() {
        //TODO: If there are items in sharedPref head to MainActivity, if not: take to StartActivity?)
        Intent intent = new Intent(AuthenticatorActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_account_authenticator, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
} //AuthenticatorActivity
