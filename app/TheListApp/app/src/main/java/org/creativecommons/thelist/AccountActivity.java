package org.creativecommons.thelist;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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


public class AccountActivity extends ActionBarActivity {
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        mUsernameField = (EditText)findViewById(R.id.nameField);
        mEmailField = (EditText)findViewById(R.id.emailField);
        mPasswordField = (EditText)findViewById(R.id.passwordField);
        mLoginButton = (Button)findViewById(R.id.nextButton);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUsername = mUsernameField.getText().toString().trim();
                mPassword = mPasswordField.getText().toString().trim();
                mEmail = mEmailField.getText().toString().trim();

                if(mUsername.isEmpty() || mPassword.isEmpty() || mEmail.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AccountActivity.this);
                    builder.setMessage(R.string.login_error_message)
                            .setTitle(R.string.login_error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else {
                    //Login user or Create account
//                    createNewUser();
//                    if(requestMethods.isUser()) {
//                        loginUser();
//                    } else {
//                        createNewUser();
//                    }
                }
            }
        });

    } //onCreate

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_account, menu);
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
}
