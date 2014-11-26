package org.creativecommons.thelist.utils;

import android.content.Context;

import org.creativecommons.thelist.adapters.MainListItem;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by damaris on 2014-11-25.
 */
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

    }

    public void logIn() {
        //Also Set login state to true?
        this.logInState = true;

    }

}
