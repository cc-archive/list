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

//public class AccountAuthenticator extends AbstractAccountAuthenticator {
//    protected Context mContext;
//
//    public AccountAuthenticator(Context context) {
//        super(context);
//        this.mContext = context;
//
//    }
//
//    @Override
//    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
//        return null;
//    }
//
//    @Override
//    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
//        final Intent intent = new Intent(mContext, AuthenticatorActivity.class);
//        intent.putExtra(AuthenticatorActivity.ARG_ACCOUNT_TYPE, accountType);
//        intent.putExtra(AuthenticatorActivity.ARG_AUTH_TYPE, authTokenType);
//        intent.putExtra(AuthenticatorActivity.ARG_IS_ADDING_NEW_ACCOUNT, true);
//        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
//        final Bundle bundle = new Bundle();
//        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
//        return bundle;
//    }
//
//    @Override
//    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) throws NetworkErrorException {
//        return null;
//    }
//
//    @Override
//    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
//        // Extract the username and password from the Account Manager, and ask
//        // the server for an appropriate AuthToken.
//        final AccountManager am = AccountManager.get(mContext);
//
//        String authToken = am.peekAuthToken(account, authTokenType);
//
//        // Lets give another try to authenticate the user
//        if (TextUtils.isEmpty(authToken)) {
//            final String password = am.getPassword(account);
//            if (password != null) {
//                authToken = sServerAuthenticate.userSignIn(account.name, password, authTokenType);
//            }
//        }
//
//        // If we get an authToken - we return it
//        if (!TextUtils.isEmpty(authToken)) {
//            final Bundle result = new Bundle();
//            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
//            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
//            result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
//            return result;
//        }
//
//        // If we get here, then we couldn't access the user's password - so we
//        // need to re-prompt them for their credentials. We do that by creating
//        // an intent to display our AuthenticatorActivity.
//        final Intent intent = new Intent(mContext, AuthenticatorActivity.class);
//        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
//        intent.putExtra(AuthenticatorActivity.ARG_ACCOUNT_TYPE, account.type);
//        intent.putExtra(AuthenticatorActivity.ARG_AUTH_TYPE, authTokenType);
//        final Bundle bundle = new Bundle();
//        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
//        return bundle;
//    }
//
//    @Override
//    public String getAuthTokenLabel(String authTokenType) {
//        return null;
//    }
//
//    @Override
//    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
//        return null;
//    }
//
//    @Override
//    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {
//        return null;
//    }
//}
