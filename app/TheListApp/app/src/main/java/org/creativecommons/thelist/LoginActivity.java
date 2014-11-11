package org.creativecommons.thelist;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.creativecommons.thelist.utils.RequestMethods;
import org.json.JSONObject;

import java.util.HashMap;

public class LoginActivity extends Activity {
    public static final String TAG = LoginActivity.class.getSimpleName();

    //Request Methods
    RequestMethods requestMethods = new RequestMethods(this);
    
    //For Request
    protected JSONObject mUserData;
    
    //UI Elements
    protected TextView mSignUpTextView;
    protected EditText mUsername;
    protected EditText mPassword;
    protected Button mLoginButton;
    protected ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mUsername = (EditText)findViewById(R.id.usernameField);
        mPassword = (EditText)findViewById(R.id.passwordField);
        mLoginButton = (Button)findViewById(R.id.loginButton);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mUsername.getText().toString();
                String password = mPassword.getText().toString();

                username = username.trim();
                password = password.trim();

                if(username.isEmpty() || password.isEmpty()) {
//                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
//                    builder.setMessage(R.string.login_error_message)
//                            .setTitle(R.string.login_error_title)
//                            .setPositiveButton(android.R.string.ok, null);
//                    AlertDialog dialog = builder.create();
//                    dialog.show();
                }
                else {
                    //userLogin
                    //If is user == true login, else create new user


                }

            }
        });



    } //OnCreate

    //TODO: Login existing user
    private void userLogin() {

        if(requestMethods.isUser()) {
            //Login the user

        } else {
          //Create new user

        }
    }

    private void createNewUser() {
        RequestQueue queue = Volley.newRequestQueue(this);
        //Genymotion Emulator
        //String url = "http://10.0.3.2:3000/api/user";

        //Android Default Emulator
        String url = "http://10.0.2.2:3000/api/user";

        //Data to be sent
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("token", "AbCdEfGh123456");

//        JsonObjectRequest newUserRequest = new JsonObjectRequest(Request.Method.POST, url, null,
//                new Response.Listener<JSONObject>() {
//
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        JSONArray jsonCategories = null;
//                        try {
//                            //Handle Data
//                            mUserData = response;
//
//                            mProgressBar.setVisibility(View.INVISIBLE);
//                            //TODO: Update UI
//
//                        } catch (JSONException e) {
//                            Log.e(TAG, e.getMessage());
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse (VolleyError error){
//                requestMethods.updateDisplayForError();
//            }
//        });
//        queue.add(newUserRequest);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
