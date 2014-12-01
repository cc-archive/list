package org.creativecommons.thelist;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.creativecommons.thelist.utils.ApiConstants;
import org.creativecommons.thelist.utils.ListUser;
import org.creativecommons.thelist.utils.RequestMethods;
import org.creativecommons.thelist.utils.SharedPreferencesMethods;
import org.json.JSONException;
import org.json.JSONObject;


public class LoginFragment extends Fragment {
    public static final String TAG = LoginFragment.class.getSimpleName();
    RequestMethods requestMethods = new RequestMethods(getActivity());
    SharedPreferencesMethods sharedPreferencesMethods = new SharedPreferencesMethods(getActivity());
    ListUser mCurrentUser = new ListUser();
    Context mContext;

    //For Request
    protected JSONObject mUserData;

    //Login/SignUp Layouts
    protected RelativeLayout mLoginFields;
    protected RelativeLayout mSignUpFields;

    //Sign Up Elements
    protected EditText mUsernameField;
    protected EditText mEmailField;
    protected EditText mPasswordField;
    protected Button mSignUpButton;
    protected TextView mExistingAccount;
    //Login Elements
    protected EditText mEmailLoginField;
    protected EditText mPasswordLoginField;
    protected Button mLoginButton;
    protected TextView mNewAccount;

   //Other UI
    protected TextView mCancelButton;

    //Sign Up Strings
    String mUsername;
    String mPassword;
    String mEmail;
    //Login Strings
    String mLoginEmail;
    String mLoginPassword;

    //Interface with Activity
    LoginClickListener mCallback;

    // --------------------------------------------------------

    //LISTENERS
    public interface LoginClickListener {
        public void UserLoggedIn(String userData);
        public void UserCreated(String userData);
        public void CancelUpload();

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (LoginClickListener) activity;
        } catch(ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + activity.getString(R.string.login_callback_exception_message));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }


    @Override
    public void onResume() {
        super.onResume();
        mContext = getActivity();

        //SignUp/Login Layouts
        mSignUpFields = (RelativeLayout)getView().findViewById(R.id.signup_fields);
        mLoginFields = (RelativeLayout)getView().findViewById(R.id.login_fields);

        //Other UI
        mCancelButton = (TextView)getView().findViewById(R.id.cancelButton);

        //Sign Up UI
        mUsernameField = (EditText)getView().findViewById(R.id.nameField);
        mUsernameField.requestFocus();
        mEmailField = (EditText)getView().findViewById(R.id.emailField);
        mPasswordField = (EditText)getView().findViewById(R.id.passwordField);
        mPasswordField.setTypeface(Typeface.DEFAULT);
        mSignUpButton = (Button)getView().findViewById(R.id.signUpButton);
        mExistingAccount = (TextView)getView().findViewById(R.id.existingAccount);

        //Login UI
        mEmailLoginField = (EditText)getView().findViewById(R.id.emailLoginField);
        mEmailLoginField.requestFocus();
        mPasswordLoginField = (EditText)getView().findViewById(R.id.passwordLoginField);
        mPasswordLoginField.setTypeface(Typeface.DEFAULT);
        mLoginButton = (Button)getView().findViewById(R.id.loginButton);
        mNewAccount = (TextView)getView().findViewById(R.id.newAccount);


        //Create New Account
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Try to create new user
                mUsername = mUsernameField.getText().toString().trim();
                mPassword = mPasswordField.getText().toString().trim();
                mEmail = mEmailField.getText().toString().trim();

                //Create account
                if (mUsername.isEmpty() || mPassword.isEmpty() || mEmail.isEmpty()) {
                    //Show error
                    requestMethods.showErrorDialog(mContext,
                            mContext.getString(R.string.login_error_title),
                            mContext.getString(R.string.login_error_message));
                } else {
                    Log.v(TAG, "new user created");
                    createNewUser();
                }
            }
        });

        //Log In User
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Switch out field layout
                mLoginEmail = mEmailLoginField.getText().toString().trim();
                mLoginPassword = mPasswordLoginField.getText().toString().trim();

                if (mLoginEmail.isEmpty() || mLoginPassword.isEmpty()) {
                    requestMethods.showErrorDialog(mContext,
                            mContext.getString(R.string.login_error_title),
                            mContext.getString(R.string.login_error_message));
                } else {
                    //mCurrentUser.logIn(mLoginEmail, mLoginPassword);
                    mCallback.UserLoggedIn("User Logged In");
                }
            }
        });

        //I have an account
        mExistingAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Switch out field Layout
                mSignUpFields.setVisibility(View.GONE);
                mLoginFields.setVisibility(View.VISIBLE);
            }
        });
        //I actually need to sign up
        mNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Switch out field Layout
                mSignUpFields.setVisibility(View.VISIBLE);
                mLoginFields.setVisibility(View.GONE);
            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.CancelUpload();
            }
        });


    } //onResume

    private void createNewUser() {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        //Genymotion Emulator
        String url = ApiConstants.CREATE_NEW_USER;
        //Android Default Emulator
        //String url = "http://10.0.2.2:3000/api/user";

        //Combine login data with user preferences
        JSONObject categoryListObject = sharedPreferencesMethods.createCategoryListObject
                (ApiConstants.USER_CATEGORIES, getActivity());
        //Log.v(TAG,categoryListObject.toString());
        JSONObject userItemObject = sharedPreferencesMethods.createUserItemsObject
                (ApiConstants.USER_ITEMS, getActivity());
        //Log.v(TAG,userItemObject.toString());

        final JSONObject userObject = new JSONObject();

        try {
            userObject.put(ApiConstants.USER_EMAIL,mEmail);
            userObject.put(ApiConstants.USER_PASSWORD,mPassword);
            userObject.put(ApiConstants.USER_NAME, mUsername);
            userObject.put(ApiConstants.USER_CATEGORIES,categoryListObject.getJSONArray(ApiConstants.USER_CATEGORIES));
            userObject.put(ApiConstants.USER_ITEMS,userItemObject.getJSONArray(ApiConstants.USER_ITEMS));
        } catch (JSONException e) {
            Log.v(TAG,e.getMessage());
        }
        Log.v(TAG, userObject.toString());
        //Data to be sent

        //Send new user object
        JsonObjectRequest newUserRequest = new JsonObjectRequest(Request.Method.POST, url, userObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //Handle Data
                            mUserData = response.getJSONObject(ApiConstants.RESPONSE_CONTENT);
                            Log.v("this is the API response", mUserData.toString());
                            JSONObject data = response.getJSONObject(ApiConstants.RESPONSE_CONTENT);
                            //Log.v(TAG,response.toString());

                            //TODO: Handle Errors
                            mCallback.UserCreated(mUserData.toString());

                        } catch (JSONException e) {
                            Log.e(TAG, e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse (VolleyError error){
                //requestMethods.updateDisplayForError();
                //TODO: Where does the app currently take you?
            }
        });
        queue.add(newUserRequest);
    } //createNewUser


    public void changeText(){
        //this textview should be bound in the fragment onCreate as a member variable
        TextView frv =(TextView) getView().findViewById(R.id.confirm_title);
        frv.setText(getString(R.string.upload_cancelled_text));
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }
}






