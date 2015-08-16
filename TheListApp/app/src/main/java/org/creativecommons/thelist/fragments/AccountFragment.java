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

package org.creativecommons.thelist.fragments;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import org.creativecommons.thelist.utils.MessageHelper;

import static org.creativecommons.thelist.authentication.AccountGeneral.ARG_ACCOUNT_NAME;
import static org.creativecommons.thelist.authentication.AccountGeneral.ARG_ACCOUNT_TYPE;
import static org.creativecommons.thelist.authentication.AccountGeneral.ARG_AUTH_TYPE;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {
    public static final String TAG = AccountFragment.class.getSimpleName();

    private Context mContext;
    private Activity mActivity;

    //Helpers
    private MessageHelper mMessageHelper;

    //UI Elements
    private Button cancelButton;
    private Button loginButton;
    private Button signUpButton;
    private EditText accountEmailField;
    private EditText accountPasswordField;
    private TextView newAccountButton;


    //private final int REQ_SIGNUP = 1;
    private String mAuthTokenType;

    //Interface with Activity + ListUser
    public AuthListener mCallback;

    // --------------------------------------------------------

    //LISTENER
    public interface AuthListener {
        public void onUserLoggedIn(Bundle userData);
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mContext = getActivity();
        mActivity = getActivity();

        mMessageHelper = new MessageHelper(mContext);

        //Get account information
        String accountName = mActivity.getIntent().getStringExtra(ARG_ACCOUNT_NAME);
        final String accountType = mActivity.getIntent().getStringExtra(ARG_ACCOUNT_TYPE);
        mAuthTokenType = mActivity.getIntent().getStringExtra(ARG_AUTH_TYPE);
        if (mAuthTokenType == null) {
            mAuthTokenType = AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS;
        }
        if (accountName != null) {
            ((EditText) mActivity.findViewById(R.id.accountName)).setText(accountName);
        }

        //UI Elements
        cancelButton = (Button) mActivity.findViewById(R.id.cancelButton);
        loginButton = (Button) mActivity.findViewById(R.id.loginButton);
        //signUpButton = (Button) mActivity.findViewById(R.id.signUpButton);

        accountEmailField = (EditText)mActivity.findViewById(R.id.accountName);
        accountPasswordField = (EditText)mActivity.findViewById(R.id.accountPassword);
        accountPasswordField.setTypeface(Typeface.DEFAULT);

        newAccountButton = (TextView) mActivity.findViewById(R.id.signUp);

        //Try Login on Click
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String accountEmail = accountEmailField.getText().toString().trim();
                final String accountPassword = accountPasswordField.getText().toString().trim();

                if (accountEmail.isEmpty() || accountPassword.isEmpty()) {
                    mMessageHelper.showDialog(mContext,getString(R.string.login_error_title),
                            getString(R.string.login_error_message));
                } else {
                    ListUser mCurrentUser = new ListUser(mActivity);
                    String guid = mCurrentUser.getAnonymousUserGUID();

                    try {
                        mCurrentUser.userLogIn(accountEmail, accountPassword, guid, mAuthTokenType,
                                new ListUser.LogInCallback() {
                                    @Override
                                    public void onLoggedIn(String authtoken, String userID) {
                                        Log.v(TAG, "> userLogIn > onLoggedIn :" + authtoken);

                                        //TODO: authtoken stuff
                                        Bundle data = new Bundle();

                                        data.putString(AccountManager.KEY_ACCOUNT_NAME, accountEmail);
                                        data.putString(AccountManager.KEY_ACCOUNT_TYPE, accountType);
                                        data.putString(AccountManager.KEY_AUTHTOKEN, authtoken);

                                        data.putString(AccountGeneral.USER_ID, userID);
                                        data.putString(AccountGeneral.USER_PASS, accountPassword);

                                        //Create Bundle to create Account
                                        mCallback.onUserLoggedIn(data);
                                    }
                                });
                    } catch (Exception e) {
                        Log.d(TAG, e.getMessage());
                        //data.putString(KEY_ERROR_MESSAGE, e.getMessage());
                    }
                }
            }
        });

        //Actually I need an account --> show user Sign Up Button
        if(newAccountButton != null) {
            newAccountButton.setMovementMethod(LinkMovementMethod.getInstance());
        }

        //TODO: do when we have register user
//        signUpButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //TODO:userSignUp
//                final String accountEmail = accountEmailField.getText().toString().trim();
//                final String accountPassword = accountPasswordField.getText().toString().trim();
//
//                if (accountEmail.isEmpty() || accountPassword.isEmpty()) {
//                    mMessageHelper.showDialog(mContext, getString(R.string.login_error_title),
//                            getString(R.string.login_error_message));
//                } else {
//                    //TODO: Login User + save to sharedPreferences
//                    ListUser mCurrentUser = new ListUser(getActivity());
//                    try {
//                        mCurrentUser.userSignUp(accountEmail, accountPassword, mAuthTokenType, new ListUser.AuthCallback() {
//                            @Override
//                            public void onAuthed(String authtoken) {
//
//                                Bundle data = new Bundle();
//
//                                data.putString(AccountManager.KEY_ACCOUNT_NAME, accountEmail);
//                                data.putString(AccountManager.KEY_ACCOUNT_TYPE, accountType);
//                                data.putString(AccountManager.KEY_AUTHTOKEN, authtoken);
//                                data.putString(USER_PASS, accountPassword);
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

        //Cancel Activity
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onCancelLogin();
            }
        });

    } //onResume

    @Override
    public void onResume() {
        super.onResume();

    }//onResume

}//LoginFragment
