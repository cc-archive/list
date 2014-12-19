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

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.creativecommons.thelist.R;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ListUser {
    public static final String TAG = ListUser.class.getSimpleName();
    private String userName;
    private String userID;
    private boolean logInState;
    private Context mContext;
    private RequestMethods requestMethods;
    //private ArrayList<MainListItem> userItems;
    //private ArrayList<MainListItem> userCategories;

    public ListUser(Context mc) {
        mContext = mc;
        requestMethods = new RequestMethods(mContext);
    }

    public ListUser() {
    }

    public ListUser(String name, String id) {
        this.userName = name;
        this.userID = id;
        this.logInState = false;
    }

    public boolean isUser() {
        //TODO: Check if User exists
        return false;
    }

    public boolean isLoggedIn() {
        //TODO: Check if User is logged in
        userID = SharedPreferencesMethods.getUserId(mContext);

        if(userID == null) {
            logInState = false;
        } else {
            logInState = true;
        }

        return logInState;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String name) {
        this.userName = name;
    }

//    public int getUserID() {
//        return 2;
//    }
    //TODO: get rid of this
    public void setLogInState(boolean bol) {
        logInState = bol;
    }

    public String getUserID() {
        //TODO: actually get ID
        userID = SharedPreferencesMethods.getUserId(mContext);

        if(userID == null) {
            Log.v(TAG, "You don’t got no userID, man");
            return null;
        } else {
            return userID;
        }

    }

    public void setUserID(String id) {
        this.userID = id;
    }

    public JSONObject getCurrentUser() {
        //TODO: getCurrentUser (from sharedPreferences?)
        JSONObject userObject = new JSONObject();
        return userObject;
    }

    public void logOut() {
        //TODO: Figure out what logOut even means…
        //LogOut User
        //Destroy mCurrentUser
        userName = null;
        userID = null;
        logInState = false;

       //TODO: take you back to startActivity?
    }


    public void logIn(final String username, final String password) {
        RequestQueue queue = Volley.newRequestQueue(mContext);
        String url = ApiConstants.LOGIN_USER;

        //Login User
        StringRequest logInUserRequest = new StringRequest(Request.Method.POST, url,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //Get Response
                    if(response.equals("null")) {
                       requestMethods.showErrorDialog(mContext, "YOU SHALL NOT PASS",
                               "Sure you got your email/password combo right?");
                    } else {
                        Log.v(TAG, response.toString());
                        try {
                            JSONObject res = new JSONObject(response);
                            userID = res.getString(ApiConstants.USER_ID);
                        } catch (JSONException e) {
                            Log.v(TAG,e.getMessage());
                        }
                        //TODO: Save session token in sharedPreferences

                        //TODO: Get any list item preferences and add them to userlist
                        //Add items chosen before login to userlist
                        SharedPreferencesMethods.RetrieveCategorySharedPreference(mContext);


                        //Save userID in sharedPreferences
                        SharedPreferencesMethods.SaveSharedPreference
                                (SharedPreferencesMethods.USER_ID_PREFERENCE,
                                        SharedPreferencesMethods.USER_ID_PREFERENCE_KEY,
                                        userID, mContext);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.v(TAG, "THERE WAS AN ERROR");
                    requestMethods.showErrorDialog(mContext,
                            mContext.getString(R.string.error_title),
                            mContext.getString(R.string.error_message));
                }
            }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put(ApiConstants.USER_PASSWORD, password);

                return params;
            }
        };
        queue.add(logInUserRequest);
    } //logIn User

    //Add SINGLE random item to user list
    public void addItemToUserList (final String itemID) {
        RequestQueue queue = Volley.newRequestQueue(mContext);

        //TODO: session token will know which user this is?
        String url = ApiConstants.ADD_ITEM + userID + "/" + itemID;

        //Add single item to user list
        StringRequest postItemsRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //get Response
                        Log.v(TAG,"AN ITEM IS BEING ADDED");
                        //TODO: do something with response?


                        //Toast: Confirm List Item has been added
                        final Toast toast = Toast.makeText(mContext,
                                "Added to Your List", Toast.LENGTH_SHORT);
                        toast.show();
                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                toast.cancel();
                            }
                        }, 1500);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse (VolleyError error){
                //TODO: Add “not successful“ toast
                requestMethods.showErrorDialog(mContext,
                        mContext.getString(R.string.error_title),
                        mContext.getString(R.string.error_message));
                Log.v("HELLO", "THIS IS THE ERROR BEING DISPLAYED");
            }
        });
        queue.add(postItemsRequest);
    } //addItemToUserList

} //ListUser
