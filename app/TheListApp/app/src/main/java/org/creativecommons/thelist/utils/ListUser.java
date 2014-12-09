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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.creativecommons.thelist.adapters.MainListItem;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ListUser {
    public static final String TAG = ListUser.class.getSimpleName();
    private String userName;
    private int userID;
    private boolean logInState;
    private ArrayList<MainListItem> userItems;
    private ArrayList<MainListItem> userCategories;

    protected Context mContext;
    public ListUser(Context mContext) {
        this.mContext = mContext;
    }

    public ListUser() {
    }
    public ListUser(String name, int id) {
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
        return logInState;
    }

//    public void setLogInState(boolean bol) {
//        this.logInState = bol;
//    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String name) {
        this.userName = name;
    }

//    public int getUserID() {
//        return 2;
//    }

    public String getUserID() {
        //TODO: actually get ID
        return String.valueOf(2);
        //return userID;
    }

    public void setUserID(int id) {
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
        //setLoginState to true (aka a token exists)
    }

//    public void logIn(String name, String email, String password) {
//        //Also Set login state to true?
//        this.logInState = true;
//
//        //Get User Preferences for List Items + Category Items
//
//        if(name == null) {
//            //PUT + add category Items
//            //else, just login User?
//        } else {
//            //Use POST method + createNewUser
//
//        }
//    }

    public void logIn(String email, String password) {
        final RequestMethods requestMethods = new RequestMethods(mContext);
        RequestQueue queue = Volley.newRequestQueue(mContext);
        //Genymotion Emulator
        String url = ApiConstants.LOGIN_USER;
        //Android Default Emulator
        //String url = "http://10.0.2.2:3000/api/user";

        //Create Object to send
        JSONObject jso = new JSONObject();
        try {
            jso.put(ApiConstants.USER_EMAIL, email);
            jso.put(ApiConstants.USER_PASSWORD, password);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
        Log.v(TAG,jso.toString());

        //Add items to user list
        JsonObjectRequest logInUserRequest = new JsonObjectRequest(Request.Method.PUT, url, jso,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //get Response
                        //Log.v(TAG,response.toString());
                        JSONObject responseData = response;
                        //TODO: set token in ListUser
                        //TODO: Save token in sharedPreferences
                        logInState = true;
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse (VolleyError error){
                requestMethods.updateDisplayForError();
            }
        });
        queue.add(logInUserRequest);
    } //logIn User

}
