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

package org.creativecommons.thelist.fragments;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.creativecommons.thelist.R;
import org.creativecommons.thelist.authentication.AccountGeneral;
import org.creativecommons.thelist.utils.ListUser;
import org.creativecommons.thelist.utils.RequestMethods;

import static org.creativecommons.thelist.authentication.AccountGeneral.ARG_ACCOUNT_NAME;
import static org.creativecommons.thelist.authentication.AccountGeneral.ARG_ACCOUNT_TYPE;
import static org.creativecommons.thelist.authentication.AccountGeneral.ARG_AUTH_TYPE;
import static org.creativecommons.thelist.authentication.AccountGeneral.PARAM_USER_PASS;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends android.support.v4.app.Fragment {
    private RequestMethods requestMethods;
    Context mContext;

    //private final int REQ_SIGNUP = 1;
    private String mAuthTokenType;

    //Interface with Activity + ListUser
    public AuthListener mCallback;

    // --------------------------------------------------------

    //LISTENERS
    public interface AuthListener {
        public void onUserSignedIn(Bundle userData);
        public void onUserSignedUp(Bundle userData);
        public void onCancelLogin();
    }

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (AuthListener) activity;
        } catch(ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + activity.getString(R.string.login_callback_exception_message));
        }
    } //onAttach

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        mContext = getActivity();
        requestMethods = new RequestMethods(mContext);

        //Get account information
        String accountName = getActivity().getIntent().getStringExtra(ARG_ACCOUNT_NAME);
        final String accountType = getActivity().getIntent().getStringExtra(ARG_ACCOUNT_TYPE);
        mAuthTokenType = getActivity().getIntent().getStringExtra(ARG_AUTH_TYPE);
        if (mAuthTokenType == null)
            mAuthTokenType = AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS;

        if (accountName != null) {
            ((EditText) getView().findViewById(R.id.accountName)).setText(accountName);
        }

        //UI Elements
        final Button cancelButton = (Button) getView().findViewById(R.id.cancelButton);
        final Button loginButton = (Button) getView().findViewById(R.id.loginButton);
        final Button signUpButton = (Button) getView().findViewById(R.id.signUpButton);
        final EditText accountEmailField = (EditText)getView().findViewById(R.id.accountName);
        final EditText accountPasswordField = (EditText)getView().findViewById(R.id.accountPassword);
        accountPasswordField.setTypeface(Typeface.DEFAULT);
        final TextView newAccountButton = (TextView) getView().findViewById(R.id.signUp);

        //Try Login on Click
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String accountEmail = accountEmailField.getText().toString().trim();
                final String accountPassword = accountPasswordField.getText().toString().trim();

                if (accountEmail.isEmpty() || accountPassword.isEmpty()) {
                    requestMethods.showDialog(getString(R.string.login_error_title),
                            getString(R.string.login_error_message));
                } else {
                    ListUser mCurrentUser = new ListUser(getActivity());
                    try {
                        mCurrentUser.userSignIn(accountEmail, accountPassword, mAuthTokenType,
                                new ListUser.AuthCallback() {
                                    @Override
                                    public void onSuccess(String authtoken) {
                                        //TODO: authtoken stuff
                                        Bundle data = new Bundle();

                                        data.putString(AccountManager.KEY_ACCOUNT_NAME, accountEmail);
                                        data.putString(AccountManager.KEY_ACCOUNT_TYPE, accountType);
                                        data.putString(AccountManager.KEY_AUTHTOKEN, authtoken);
                                        data.putString(PARAM_USER_PASS, accountPassword);

                                        //Create Bundle to create Account
                                        mCallback.onUserSignedIn(data);
                                    }
                                });
                    } catch (Exception e) {
                        Log.d("LoginFragment", e.getMessage());
                        //data.putString(KEY_ERROR_MESSAGE, e.getMessage());
                    }
                }
            }
        });

        //Actually I need an account --> show user Sign Up Button
        if(newAccountButton != null) {
            newAccountButton.setMovementMethod(LinkMovementMethod.getInstance());
        }
        //TODO: hide loginButton and show signUpButton
//        newAccountButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//                //loginButton.setVisibility(View.GONE);
//                //signUpButton.setVisibility(View.VISIBLE);
//            }
//        });

        //Cancel Activity
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onCancelLogin();
            }
        });

        //TODO: do when we have register user
//        signUpButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //TODO:userSignUp
//                final String accountEmail = accountEmailField.getText().toString().trim();
//                final String accountPassword = accountPasswordField.getText().toString().trim();
//
//                if (accountEmail.isEmpty() || accountPassword.isEmpty()) {
//                    requestMethods.showDialog(mContext, getString(R.string.login_error_title),
//                            getString(R.string.login_error_message));
//                } else {
//                    //TODO: Login User + save to sharedPreferences
//                    ListUser mCurrentUser = new ListUser(getActivity());
//                    try {
//                        mCurrentUser.userSignUp(accountEmail, accountPassword, mAuthTokenType, new ListUser.AuthCallback() {
//                            @Override
//                            public void onSuccess(String authtoken) {
//                                //TODO: fill this in to create user
//
//                                Bundle data = new Bundle();
//
//                                data.putString(AccountManager.KEY_ACCOUNT_NAME, accountEmail);
//                                data.putString(AccountManager.KEY_ACCOUNT_TYPE, accountType);
//                                data.putString(AccountManager.KEY_AUTHTOKEN, authtoken);
//                                data.putString(PARAM_USER_PASS, accountPassword);
//
//                                mCallback.onUserSignedUp(data);
//                            }
//                        });
//                    } catch (Exception e) {
//                        Log.d("LoginFragment", e.getMessage());
//                    }
//                }
//            }
//        });

    }//onResume
}//LoginFragment
