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

import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import org.creativecommons.thelist.adapters.MainListItem;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public final class RequestMethods {
    public static final String TAG = RequestMethods.class.getSimpleName();
    protected Context mContext;

    //Set Context
    public RequestMethods(Context mContext) {
        this.mContext = mContext;
    }

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = false;
        if(networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }

    //CHECK AVAILABILITY OF NETWORK
//    public static boolean isNetworkAvailable() {
//        ConnectivityManager manager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
//
//        boolean isAvailable = false;
//        if(networkInfo != null && networkInfo.isConnected()) {
//            isAvailable = true;
//        }
//        return isAvailable;
//    }

    //UPDATE DISPLAY FOR ERROR METHOD
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
    public List<String> getItemIds(List<MainListItem> list){
        List<String>arrayList = new ArrayList<String>();
        for (int i = 0; i < list.size(); i++) {
            String singleID = list.get(i).getItemID();
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
    public String createUploadPhotoObject(Uri uri) {
        //Convert photo file to Base64 encoded string
        String fileString = FileHelper.getByteArrayFromFile(mContext, uri);
        Log.v("FILESTRING IS: ", fileString);
        if(fileString == null) {
            Log.v(TAG, "this is null");
        }
        return fileString;
    }

    //Create Upload Photo Object (in bytes) + return object with ID and userID
//    public JSONObject createUploadPhotoObject(MainListItem currentItem, Uri uri) {
//        ListUser listUser = new ListUser();
//
//        //Get Data from currentItem to build JSONObject
//        JSONObject photoObject = new JSONObject();
//
//        //Convert photo file to byte[]
//        String fileString = FileHelper.getByteArrayFromFile(mContext, uri);
//        if(fileString == null) {
//            return null;
//        } else {
//            //reduce size for upload: may not be necessary
//            //fileBytes = FileHelper.reduceImageForUpload(fileBytes);
//            //String fileType = String.valueOf(PhotoConstants.MEDIA_TYPE_IMAGE);
//            //String fileName = FileHelper.getFileName(this, mMediaUri, fileType);
//            try {
//                photoObject.put(ApiConstants.PHOTO_ITEM_ID, currentItem.getItemID());
//                photoObject.put(ApiConstants.PHOTO_USER_ID, listUser.getUserID());
//                photoObject.put(ApiConstants.PHOTO_BYTE_ARRAY, fileString);
//            }
//            catch (JSONException e) {
//                Log.e(TAG, e.getMessage());
//            }
//            return photoObject;
//        }
//    }






}
