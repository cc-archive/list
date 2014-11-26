package org.creativecommons.thelist.utils;

import android.content.Context;

/**
 * Created by damaris on 2014-11-25.
 */
public class UserMethods {
    public static final String TAG = UserMethods.class.getSimpleName();
    private String userName;
    private int userID;

    protected Context mContext;
    public UserMethods (Context mContext) {
        this.mContext = mContext;
    }

    public UserMethods() {
    }
    public UserMethods(String name, int id) {
        this.userName = name;
        this.userID = id;
    }

    public boolean isUser() {
        //TODO: Check if User exists
        return false;
    }

    public boolean isLoggedIn() {
        //TODO: Check if User is logged in
        return false;
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
    }

    public void setUserID(int id) {
        this.userID = id;
    }
}
