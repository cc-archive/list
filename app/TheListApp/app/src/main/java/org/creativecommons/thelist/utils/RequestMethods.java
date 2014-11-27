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

    //Set Context
    public RequestMethods(Context mContext) {
        this.mContext = mContext;
    }

    //CHECK AVAILABILITY OF NETWORK
    public static boolean isNetworkAvailable() {
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
    //TODO: Replace above with showErrorDialog
    //Generic Error Dialog Builder
    public void showErrorDialog(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
               .setMessage(message)
               .setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //TODO: ask WHYY
    //Parse List Objects of List Items and return list of Item IDS
    public List<Integer> getItemIds(List<MainListItem> list){
        List<Integer>arrayList = new ArrayList<Integer>();
        for (int i = 0; i < list.size(); i++) {
            int singleID = list.get(i).getItemID();
            arrayList.add(singleID);
        }
        return arrayList;
    }

    //Create Upload User Object + return object with name, email, password
    public JSONObject createLoginUserObject(String jsonAsString) {
        //JSONObject loginUserObject;

        //Convert from String to JSONObject
//        JsonParser parser = new JsonParser();
//        JsonElement element = parser.parse(jsonAsString);
//        JsonObject object = element.getAsJsonObject();
//
//        Log.v(TAG, object.toString());
//        try {
//
//            return loginUserObject;
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        return null;
    }


    //Create Upload Photo Object (in bytes) + return object with ID and userID
    public JSONObject createUploadPhotoObject(MainListItem currentItem, Uri uri) {
        ListUser listUser = new ListUser();

        //Get Data from currentItem to build JSONObject
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
                photoObject.put(ApiConstants.PHOTO_ITEM_ID, currentItem.getItemID());
                photoObject.put(ApiConstants.PHOTO_USER_ID, listUser.getUserID());
                photoObject.put(ApiConstants.PHOTO_BYTE_ARRAY, fileBytes);
            }
            catch (JSONException e) {
                Log.e(TAG, e.getMessage());
            }
            return photoObject;
        }
    }
}
