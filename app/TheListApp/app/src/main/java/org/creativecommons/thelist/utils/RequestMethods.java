package org.creativecommons.thelist.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.creativecommons.thelist.R;

/**
 * Created by damaris on 2014-11-07.
 */
public class RequestMethods {

    protected Context mContext;

    public RequestMethods(Context mContext) {
        this.mContext = mContext;
    }

    public boolean isUser() {
        //TODO: Check if User exists
        return false;
    }

    public boolean isLoggedIn() {
        //TODO: Check if User is logged in
        return false;
    }

    public String getUserID() {
        //TODO: Get Current UserID
        return String.valueOf(1);
    }

    //CHECK AVAILABILITY OF NETWORK
    public boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = false;
        if(networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }

    //UPDATE DISPLAY FOR ERROR METHOD
    public void updateDisplayForError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(mContext.getString(R.string.error_title));
        builder.setMessage(mContext.getString(R.string.error_message));
        builder.setPositiveButton(android.R.string.ok,null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //TODO: Update For Login Error
    public void updateDisplayForLoginError() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//        builder.setMessage(e.getMessage())
//                .setTitle(R.string.login_error_title)
//                .setPositiveButton(android.R.string.ok, null);
//        AlertDialog dialog = builder.create();
//        dialog.show();
    }



}
