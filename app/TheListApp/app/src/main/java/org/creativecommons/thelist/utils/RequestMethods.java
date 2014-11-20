package org.creativecommons.thelist.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import org.creativecommons.thelist.R;
import org.creativecommons.thelist.adapters.MainListItem;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by damaris on 2014-11-07.
 */
public class RequestMethods {
    public static final String TAG = RequestMethods.class.getSimpleName();

    protected Context mContext;

    public RequestMethods(Context mContext) {
        this.mContext = mContext;
    }

    //May not need this
    public boolean isUser() {
        //TODO: Check if User exists
        return false;
    }

    //Maybe not need this: if you can get USERID, that means user is logged in?
    public boolean isLoggedIn() {
        //TODO: Check if User is logged in
        return true;
    }

    public String getUserID() {
        //TODO: Get Current UserID
        return String.valueOf(2);
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

    //TODO: ask WHYY
    public List<Integer> getItemIds(List<MainListItem> list){
        List<Integer>arrayList = new ArrayList<Integer>();
        for (int i = 0; i < list.size(); i++) {
            int singleID = list.get(i).getItemID();
            arrayList.add(singleID);
        }
        return arrayList;
    }

    public JSONObject createUploadPhotoObject(MainListItem mCurrentItem, Uri uri) {
        //Get Data from mCurrentItem to build JSONObject
        JSONObject photoObject = new JSONObject();

        //Convert photo file to byte[]
        byte[] fileBytes = FileHelper.getByteArrayFromFile(mContext, uri);
        if(fileBytes == null) {
            return null;
        } else {
            //reduce size for upload: may not be necessary
            //fileBytes = FileHelper.reduceImageForUpload(fileBytes);
            //String fileType = String.valueOf(PhotoConstants.MEDIA_TYPE_IMAGE);
            //String fileName = FileHelper.getFileName(this, mMediaUri, fileType);
            try {
                //TODO: Check mCurrentItem is what you think it is -_-
                photoObject.put(ApiConstants.PHOTO_ITEM_ID, mCurrentItem.getItemID());
                photoObject.put(ApiConstants.PHOTO_USER_ID, getUserID());
                photoObject.put(ApiConstants.PHOTO_BYTE_ARRAY, fileBytes);
            }
            catch (JSONException e) {
                Log.e(TAG, e.getMessage());
            }
            return photoObject;
        }
    }


}
