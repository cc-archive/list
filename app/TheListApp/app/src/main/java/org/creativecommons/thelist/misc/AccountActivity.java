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

package org.creativecommons.thelist.misc;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.creativecommons.thelist.R;
import org.creativecommons.thelist.utils.ListApplication;
import org.creativecommons.thelist.utils.ListUser;
import org.creativecommons.thelist.utils.RequestMethods;
import org.creativecommons.thelist.utils.SharedPreferencesMethods;


public class AccountActivity extends ActionBarActivity {
    public static final String TAG = AccountActivity.class.getSimpleName();
    protected Context mContext;

    //Request Methods
    RequestMethods requestMethods;
    SharedPreferencesMethods sharedPreferencesMethods;
    ListUser mCurrentUser;// = new ListUser(mContext);

    //UI Elements
    protected EditText mEmailLoginField;
    protected EditText mPasswordLoginField;
    protected Button mLoginButton;
    //protected ProgressBar mProgressBar;

    String mUsername;
    String mPassword;
    String mEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        mContext = AccountActivity.this;
        SharedPreferencesMethods sharedPref = new SharedPreferencesMethods(mContext);
        requestMethods = new RequestMethods(this);

        //Google Analytics Tracker
        Tracker t = ((ListApplication) AccountActivity.this.getApplication()).getTracker(
                ListApplication.TrackerName.GLOBAL_TRACKER);

        t.setScreenName(TAG);
        t.send(new HitBuilders.AppViewBuilder().build());

        sharedPref.ClearAllSharedPreferences();

        mCurrentUser = new ListUser(mContext);

        mEmailLoginField = (EditText)findViewById(R.id.emailLoginField);
        mPasswordLoginField = (EditText)findViewById(R.id.passwordLoginField);
        mLoginButton = (Button)findViewById(R.id.loginButton);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEmail = mEmailLoginField.getText().toString().trim();
                mPassword = mPasswordLoginField.getText().toString().trim();

                if(mPassword.isEmpty() || mEmail.isEmpty()) {
                    requestMethods.showErrorDialog(mContext, getString(R.string.login_error_title),
                            getString(R.string.login_error_message));
                }
                else {
                    //TODO: Login User + save to sharedPreferences
                    //mCurrentUser.logIn(mEmail, mPassword, mContext);

                    //1. pass it to the activity let MainActivity login and set CurrentUser/sharedPreferences
                    //2. Login now and pass the data to MainActivity to set things
                    //3. Set sharedPreferences in login() and retrieve them in MainActivity

//                    if(mCurrentUser.isLoggedIn()) {
//                        Intent intent = new Intent(mContext, RandomActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        startActivity(intent);
//                    }
                }
            }
        });

    } //onCreate

//    private void createNewUser() {
//        RequestQueue queue = Volley.newRequestQueue(this);
//        //Genymotion Emulator
//        String url = "http://10.0.3.2:3000/api/user";
//
//        //Android Default Emulator
//        //String url = "http://10.0.2.2:3000/api/user";
//
//        //Data to be sent
//        HashMap<String, String> params = new HashMap<String, String>();
//        params.put(ApiConstants.USER_NAME, mUsername);
//        params.put(ApiConstants.USER_EMAIL,mEmail);
//        params.put(ApiConstants.USER_PASSWORD,mPassword);
//
//        JsonObjectRequest newUserRequest = new JsonObjectRequest(url, new JSONObject(params),
//                new Response.Listener<JSONObject>() {
//
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        try {
//                            //Handle Data
//                            mUserData = response.getJSONObject(ApiConstants.RESPONSE_CONTENT);
//
//                            //mProgressBar.setVisibility(View.INVISIBLE);
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
//    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_account, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
////        if (id == R.id.action_settings) {
////            return true;
////        }
//
//        return super.onOptionsItemSelected(item);
//    }
}//AccountActivity
