package org.creativecommons.thelist;

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

import org.creativecommons.thelist.authentication.AccountGeneral;
import org.creativecommons.thelist.authentication.AesCbcWithIntegrity;
import org.creativecommons.thelist.fragments.AccountFragment;
import org.creativecommons.thelist.fragments.LoginFragment;
import org.creativecommons.thelist.fragments.TermsFragment;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

import static org.creativecommons.thelist.authentication.AccountGeneral.ARG_ACCOUNT_NAME;
import static org.creativecommons.thelist.authentication.AccountGeneral.ARG_AUTH_TYPE;
import static org.creativecommons.thelist.authentication.AccountGeneral.ARG_IS_ADDING_NEW_ACCOUNT;
import static org.creativecommons.thelist.authentication.AccountGeneral.PARAM_USER_PASS;
import static org.creativecommons.thelist.authentication.AesCbcWithIntegrity.encrypt;
import static org.creativecommons.thelist.authentication.AesCbcWithIntegrity.generateKey;


public class AccountActivity extends org.creativecommons.thelist.authentication.AccountAuthenticatorActivity implements LoginFragment.AuthListener {
    private final String TAG = this.getClass().getSimpleName();
    Context mContext;
    //private final int REQ_SIGNUP = 1;
    private AccountManager mAccountManager;
    private String mAuthTokenType;


    //UI Elements
    FrameLayout mFrameLayout;

    //Fragments
    AccountFragment accountFragment = new AccountFragment();
    TermsFragment termsFragment = new TermsFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        mContext = this;
        mAccountManager = AccountManager.get(getBaseContext());

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
        LoginFragment loginFragment = new LoginFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container,loginFragment)
                .commit();
                mFrameLayout.setClickable(true);
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

        String accountEmail = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        String accountPassword = intent.getStringExtra(PARAM_USER_PASS);
        final Account account = new Account(accountEmail, intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));

        if (getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false)) {
            Log.d("THE LIST", TAG + "> finishLogin > addAccountExplicitly");
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

            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        } else {
            Log.d("THE LIST", TAG + "> finishLogin > setPassword");
            mAccountManager.setPassword(account, accountPassword);
        }

        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    } //finishLogin

    @Override
    public void onCancelLogin() {
        //TODO: If there are items in sharedPref head to MainActivity, if not: take to StartActivity?)
        //TODO: put extra: this was a cancelled login so activity can act
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED);
        finish();

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
