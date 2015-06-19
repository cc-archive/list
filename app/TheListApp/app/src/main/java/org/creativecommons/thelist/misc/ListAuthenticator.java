/* Copyright 2013 Udi Cohen
 Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 this file except in compliance with the License. You may obtain a copy of the
 License at http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software distributed
 under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.
 */

package org.creativecommons.thelist.misc;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import org.creativecommons.thelist.activities.AccountActivity;
import org.creativecommons.thelist.authentication.AccountGeneral;
import org.creativecommons.thelist.authentication.AesCbcWithIntegrity;
import org.creativecommons.thelist.utils.ListUser;
import org.creativecommons.thelist.utils.SharedPreferencesMethods;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;

import static android.accounts.AccountManager.KEY_BOOLEAN_RESULT;
import static org.creativecommons.thelist.authentication.AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS;
import static org.creativecommons.thelist.authentication.AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS_LABEL;
import static org.creativecommons.thelist.authentication.AccountGeneral.AUTHTOKEN_TYPE_READ_ONLY;
import static org.creativecommons.thelist.authentication.AccountGeneral.AUTHTOKEN_TYPE_READ_ONLY_LABEL;
import static org.creativecommons.thelist.authentication.AesCbcWithIntegrity.decryptString;
import static org.creativecommons.thelist.authentication.AesCbcWithIntegrity.keys;


public class ListAuthenticator extends AbstractAccountAuthenticator {
    private String TAG = "ListAuthenticator";
    private final Context mContext;
    private Activity activity;
    private String authToken;

    public ListAuthenticator(Activity activity) {
        super(activity);

        // I hate you! Google - set mContext as protected! (TODO: Laugh at this…again)
        this.mContext = activity;
        this.activity = activity;
    }

    public ListAuthenticator(Context mc){
        super(mc);
        this.mContext = mc;
    }

    //Add account to your android device
    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
        Log.d("THE LIST", TAG + "> addAccount");

        final Intent intent = new Intent(mContext, AccountActivity.class);
        intent.putExtra(AccountGeneral.ARG_ACCOUNT_TYPE, accountType);
        intent.putExtra(AccountGeneral.ARG_AUTH_TYPE, authTokenType);
        intent.putExtra(AccountGeneral.ARG_IS_ADDING_NEW_ACCOUNT, true);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);

        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    //Request AuthToken
    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        Log.d("THE LIST", TAG + "> getAuthToken");

        // If the caller requested an authToken type we don't support, then
        // return an error
        if (!authTokenType.equals(AccountGeneral.AUTHTOKEN_TYPE_READ_ONLY) && !authTokenType.equals(AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS)) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ERROR_MESSAGE, "invalid authTokenType");
            return result;
        }

        // Extract the username and password from the Account Manager, and ask
        // the server for an appropriate AuthToken.
        final AccountManager am = AccountManager.get(mContext);
        authToken = am.peekAuthToken(account, authTokenType);

        Log.d("THE LIST", TAG + "> peekAuthToken returned - " + authToken);

        // Lets give another try to authenticate the user
        if (TextUtils.isEmpty(authToken)) {
            final String encryptedPass = am.getPassword(account);
            final String password;
            final AesCbcWithIntegrity.CipherTextIvMac civ;
            final AesCbcWithIntegrity.SecretKeys key;

            SharedPreferencesMethods sharedPref = new SharedPreferencesMethods(mContext);
            String keyStr = sharedPref.getKey();

            if(!keyStr.isEmpty()) { //is there is a key available
                try {
                    civ = new AesCbcWithIntegrity.CipherTextIvMac(encryptedPass);
                    key = keys(keyStr);
                    password = decryptString(civ, key);

                    if (password != null) {
                        try {
                            Log.d("THE LIST", TAG + "> re-authenticating with the existing password");
                            ListUser mCurrentUser = new ListUser(activity);
                            mCurrentUser.userSignIn(account.name, password, mCurrentUser.getAnonymousUserGUID(), authTokenType, new ListUser.AuthCallback() {
                                @Override
                                public void onAuthed(String userToken) {
                                    authToken = userToken;
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                } catch (GeneralSecurityException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

        } //If authtoken is empty, go get one.

        // If we get an authToken - we return it
        if (!TextUtils.isEmpty(authToken)) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
            return result;
        }

        // If we get here, then we couldn't access the user's password - so we
        // need to re-prompt them for their credentials. We do that by creating
        // an intent to display our AuthenticatorActivity.
        final Intent intent = new Intent(mContext, AccountActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        intent.putExtra(AccountGeneral.ARG_ACCOUNT_TYPE, account.type);
        intent.putExtra(AccountGeneral.ARG_AUTH_TYPE, authTokenType);
        intent.putExtra(AccountGeneral.ARG_ACCOUNT_NAME, account.name);
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        if (AUTHTOKEN_TYPE_FULL_ACCESS.equals(authTokenType))
            return AUTHTOKEN_TYPE_FULL_ACCESS_LABEL;
        else if (AUTHTOKEN_TYPE_READ_ONLY.equals(authTokenType))
            return AUTHTOKEN_TYPE_READ_ONLY_LABEL;
        else
            return authTokenType + " (Label)";
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {
        final Bundle result = new Bundle();
        result.putBoolean(KEY_BOOLEAN_RESULT, false);
        return result;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        return null;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        return null;
    }
} //ListAuthenticator
