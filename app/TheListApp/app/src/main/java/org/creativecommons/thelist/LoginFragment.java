package org.creativecommons.thelist;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.creativecommons.thelist.utils.ApiConstants;
import org.creativecommons.thelist.utils.RequestMethods;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class LoginFragment extends Fragment {
    public static final String TAG = AccountActivity.class.getSimpleName();

    //Request Methods
    RequestMethods requestMethods = new RequestMethods(this);

    //For Request
    protected JSONObject mUserData;

    //UI Elements
    protected EditText mUsernameField;
    protected EditText mEmailField;
    protected EditText mPasswordField;
    protected Button mLoginButton;
    //protected ProgressBar mProgressBar;

    String mUsername;
    String mPassword;
    String mEmail;

    //@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_login_fragment, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        mUsernameField = (EditText)getView().findViewById(R.id.nameField);
        mEmailField = (EditText)getView().findViewById(R.id.emailField);
        mPasswordField = (EditText)getView().findViewById(R.id.passwordField);
        mLoginButton = (Button)getView().findViewById(R.id.loginButton);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    createNewUser();
                    if(requestMethods.isUser()) {
                        loginUser();
                    } else {
                        createNewUser();
                    }
                }
            }
        });

    }

    //TODO: Login existing user
    private void loginUser() {
        //Verify user identity
    }

    private void createNewUser() {
        RequestQueue queue = Volley.newRequestQueue(this);
        //Genymotion Emulator
        String url = "http://10.0.3.2:3000/api/user";

        //Android Default Emulator
        //String url = "http://10.0.2.2:3000/api/user";

        //Data to be sent
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(ApiConstants.USER_NAME, mUsername);
        params.put(ApiConstants.USER_EMAIL,mEmail);
        params.put(ApiConstants.USER_PASSWORD,mPassword);

        JsonObjectRequest newUserRequest = new JsonObjectRequest(url, new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //Handle Data
                            mUserData = response.getJSONObject(ApiConstants.RESPONSE_CONTENT);

                            //mProgressBar.setVisibility(View.INVISIBLE);
                            //TODO: Update UI

                        } catch (JSONException e) {
                            Log.e(TAG, e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse (VolleyError error){
                requestMethods.updateDisplayForError();
            }
        });
        queue.add(newUserRequest);
    }

}
