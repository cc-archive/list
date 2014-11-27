package org.creativecommons.thelist;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.creativecommons.thelist.utils.ApiConstants;
import org.creativecommons.thelist.utils.ListUser;
import org.creativecommons.thelist.utils.SharedPreferencesMethods;
import org.json.JSONException;
import org.json.JSONObject;


public class LoginFragment extends Fragment {
    public static final String TAG = LoginFragment.class.getSimpleName();
    //RequestMethods requestMethods = new RequestMethods(getActivity());
    SharedPreferencesMethods sharedPreferencesMethods = new SharedPreferencesMethods(getActivity());
    ListUser mCurrentUser = new ListUser();

    //For Request
    protected JSONObject mUserData;

    //UI Elements
    protected EditText mUsernameField;
    protected EditText mEmailField;
    protected EditText mPasswordField;
    protected Button mLoginButton;
    protected TextView mExistingAccount;
    //protected ProgressBar mProgressBar;

    String mUsername;
    String mPassword;
    String mEmail;

    //Interface with Activity
    LoginClickListener mCallback;
//    LogInListener mLogInCallback;
//    SignUpListener mSignUpCallback;
    

    //LISTENERS
    public interface LoginClickListener {
        public void UserLoggedIn(String userData);
        public void UserCreated(String userData);

    }

//    public void onLoginClicked(String userData);

//    public interface LogInListener {
//        public void UserLoggedIn(String userData);
//    }
//    public void interface SignUpListener {
//        public void UserCreated(String userData);
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);

    }

    @Override
    public void onResume() {
        super.onResume();

        mUsernameField = (EditText)getView().findViewById(R.id.nameField);
        mEmailField = (EditText)getView().findViewById(R.id.emailField);
        mPasswordField = (EditText)getView().findViewById(R.id.passwordField);
        mPasswordField.setTypeface(Typeface.DEFAULT);
        mLoginButton = (Button)getView().findViewById(R.id.loginButton);
        mUsernameField.requestFocus();

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  //TODO: Do stuff
                //Try to create new user
                mUsername = mUsernameField.getText().toString().trim();
                mPassword = mPasswordField.getText().toString().trim();
                mEmail = mEmailField.getText().toString().trim();

                if(mUsername.isEmpty() || mPassword.isEmpty() || mEmail.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(R.string.login_error_message)
                            .setTitle(R.string.login_error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else {
                    //Login user or Create account
                    Log.v(TAG, "Login user or create account here");
                    //If user exists login, else create new user

                    //TODO: use different check (e.g.: is I have an account button clicked?)
                    if(mCurrentUser.isUser()) {
                        mCallback.UserLoggedIn(mUserData.toString());
                        mCurrentUser.logIn(mEmail, mPassword);
                    } else {
                        createNewUser();
                        //mCallback.UserCreated(mUserData.toString());
                    }
                }
            }
        });
    }

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
                //TODO: Where will a login error take you?
            }
        });
        queue.add(newUserRequest);
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
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }
}






