/* The List powered by Creative Commons

   Copyright (C) 2014, 2015 Creative Commons

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

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.creativecommons.thelist.R;
import org.creativecommons.thelist.activities.MainActivity;
import org.creativecommons.thelist.activities.StartActivity;
import org.creativecommons.thelist.adapters.ProgressBarState;
import org.creativecommons.thelist.adapters.UserListItem;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class RequestMethods {
    public static final String TAG = RequestMethods.class.getSimpleName();
    protected Context mContext;

    //Helper Methods
    protected MessageHelper mMessageHelper;
    protected SharedPreferencesMethods mSharedPref;
    protected ListUser mCurrentUser;

    //Notifications
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;

    //Request Cancelled
    public enum CancelResponse {
        NETWORK_ERROR,
        FILESIZE_ERROR
    }

    public RequestMethods(Context mc) {
        mContext = mc;
        mMessageHelper = new MessageHelper(mc);
        mSharedPref = new SharedPreferencesMethods(mc);
        mCurrentUser = new ListUser(mc);

        //Notifications
        mNotifyManager = (NotificationManager) mc.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(mc);

    }

    // --------------------------------------------------------
    // CALLBACKS
    // --------------------------------------------------------

    //Callback for requests
    public interface RequestCallback {
        void onSuccess();
        void onFail();
        void onCancelled(CancelResponse response);
    }

    public interface ResponseCallback {
        void onSuccess(JSONArray response);
        void onFail(VolleyError error);
    }

    public interface UserListCallback {
        void onSuccess(JSONArray response);
        void onFail(VolleyError error);
        void onUserOffline(List<UserListItem> response);
    }

    // --------------------------------------------------------
    // NETWORK CHECKS
    // --------------------------------------------------------

    //Check if thar be internets (public helper)
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = false;
        if(networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }

        return isAvailable;
    }

    //Check if thar be internets
    public boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = false;
        if(networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }

        return isAvailable;
    }


    // --------------------------------------------------------
    // HELPERS
    // --------------------------------------------------------

    public void startUploadNotification(int notificationID, String contentText){
        mBuilder = new NotificationCompat.Builder(mContext);
        mBuilder.setContentTitle(mContext.getResources().getString(R.string.app_name_short))
                .setContentText(contentText)
                .setColor(mContext.getResources().getColor(R.color.colorSecondary))
                .setSmallIcon(R.drawable.ic_camera_alt_white_24dp);

        Intent resultIntent = new Intent(mContext, MainActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        android.support.v4.app.TaskStackBuilder stackBuilder = android.support.v4.app.TaskStackBuilder.create(mContext);
        stackBuilder.addParentStack(StartActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_CANCEL_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(notificationID, mBuilder.build());

    }


    // --------------------------------------------------------
    // APP REQUEST
    // --------------------------------------------------------

    //TODO: create real request when endpoint exists
    public void getAppVersion(Response.Listener<JSONArray> response, Response.ErrorListener error){
        if(!(isNetworkAvailable())){
            mMessageHelper.networkFailMessage();
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(mContext);
        String url = ApiConstants.GET_CURRENT_APP_VERSION;

        JsonArrayRequest appVersionRequest = new JsonArrayRequest(url,
                response, error);
        queue.add(appVersionRequest);
    }

    // --------------------------------------------------------
    // CHECK USER STATS
    // --------------------------------------------------------

    public void getUserProfile(final ResponseCallback callback){
        if (!(isNetworkAvailable())) {
            mMessageHelper.galleryNetworkFailMessage();
            return;
        }
        //TODO: change to proper request for stats once it exists
        mCurrentUser.getToken(new ListUser.AuthCallback() {
            @Override
            public void onAuthed(final String authtoken) {
                RequestQueue queue = Volley.newRequestQueue(mContext);
                String url = ApiConstants.GET_USER_PHOTOS + mSharedPref.getUserId();
                Log.v(TAG, " > getUserPhotos, url: " + url);

                JsonArrayRequest getUserPhotosRequest = new JsonArrayRequest(url,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                callback.onSuccess(response);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onFail(error);
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put(ApiConstants.USER_TOKEN, authtoken);
                        return params;
                    }
                };
                queue.add(getUserPhotosRequest);
            }
        });
    } //getUserProfile

    // --------------------------------------------------------
    // CATEGORY REQUESTS
    // --------------------------------------------------------

    //GET List of All Categories
    public void getCategories(final ResponseCallback callback) {
        if(!(isNetworkAvailable())){
            mMessageHelper.networkFailMessage();
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(mContext);
        String url = ApiConstants.GET_CATEGORIES;

        JsonArrayRequest categoriesRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //Log.v(TAG, "> getCategories > onResponse: " + response);
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.d(TAG, "> getCategories > onErrorResponse: " + error.getMessage());
                callback.onFail(error);
            }
        });
        queue.add(categoriesRequest);
    } //getCategories

    //GET User Selected Categories from API
    public void getUserCategories(final ResponseCallback callback) {
        if(!(isNetworkAvailable())){
            mMessageHelper.networkFailMessage();
            return;
        }

        mCurrentUser.getToken(new ListUser.AuthCallback() {
            @Override
            public void onAuthed(final String authtoken) {
                RequestQueue queue = Volley.newRequestQueue(mContext);
                String url = ApiConstants.GET_USER_CATEGORIES + mSharedPref.getUserId();

                JsonArrayRequest userCategoriesRequest = new JsonArrayRequest(url,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                callback.onSuccess(response);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onFail(error);
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put(ApiConstants.USER_TOKEN, authtoken);
                        return params;
                    }
                };
                queue.add(userCategoriesRequest);
            }
        });
    } //getUserCategories

    //Add all categories to User Account (during login)
    public void addTempCategoriesToUser(){
        if(!(RequestMethods.isNetworkAvailable(mContext))){
            mMessageHelper.networkFailMessage();
            return;
        }

        Log.v(TAG," > addTempCategoriesToUser, started");
        JSONArray listCategoryPref;
        listCategoryPref = mSharedPref.getCategorySharedPreference();

        try{
            if (listCategoryPref != null && listCategoryPref.length() > 0) {
                Log.v("LIST ITEM PREF: ", listCategoryPref.toString());
                for (int i = 0; i < listCategoryPref.length(); i++) {
                    Log.v("ITEMS", "ARE BEING ADDED");
                    addCategory(listCategoryPref.getString(i));
                }
            }
        } catch(JSONException e){
            Log.d(TAG, "> addTempCategoriesToUser: " + e.getMessage());
        }
    } //addTempCategoriesToUser

    //Add Single Category to User Account
    public void addCategory(final String catId){

        if(mCurrentUser.isTempUser()){ //TEMP USER
            mSharedPref.addUserCategoryPreference(catId);
            Log.v(TAG, "TEMP CAT ADDED: " + catId);
            return;
        }

        if(!(RequestMethods.isNetworkAvailable(mContext))){
            mMessageHelper.networkFailMessage();
            return;
        }

        mCurrentUser.getToken(new ListUser.AuthCallback() {
            @Override
            public void onAuthed(final String authtoken) {
                RequestQueue queue = Volley.newRequestQueue(mContext);
                String url = ApiConstants.ADD_CATEGORY + mSharedPref.getUserId() + "/" + catId;

                //Add single item to user list
                StringRequest postCategoryRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.v(TAG, "> addCategory > OnResponse: " + response);
                                Log.v(TAG, "A CATEGORY IS BEING ADDED");

                                mSharedPref.deleteUserCategoryPreference(catId);
                                Toast.makeText(mContext, "category added", Toast.LENGTH_SHORT).show();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, " > addCategory > onErrorResponse: " + error.getMessage());
                        //TODO: Add “not successful“ toast
                        mMessageHelper.showDialog(mContext,
                                mContext.getString(R.string.error_title),
                                mContext.getString(R.string.error_message));
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put(ApiConstants.USER_TOKEN, authtoken);
                        return params;
                    }
                };
                queue.add(postCategoryRequest);
            }
        });
    } //addCategory

    public void removeCategory(final String catId){

        if(mCurrentUser.isTempUser()){ //TEMP USER
            mSharedPref.deleteUserCategoryPreference(catId);
            Log.v(TAG, "TEMP CAT DELETED: " + catId);
            return;
        }

        if(!(RequestMethods.isNetworkAvailable(mContext))){
            mMessageHelper.networkFailMessage();
            return;
        }

        mCurrentUser.getToken(new ListUser.AuthCallback() {
            @Override
            public void onAuthed(final String authtoken) { //getToken and then start request
                RequestQueue queue = Volley.newRequestQueue(mContext);
                String url = ApiConstants.REMOVE_CATEGORY + mSharedPref.getUserId() + "/" + catId;

                StringRequest deleteCategoryRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                //get Response
                                Log.v(TAG, "> removeCategory > onResponse: " + response);
                                Log.v(TAG, "A CATEGORY IS BEING REMOVED");
                                //TODO: do something with response?
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //TODO: Add “not successful“ toast
                        Log.d(TAG, " > removeCategory > onErrorResponse: " + error.getMessage());
                        //TODO: REMOVE FROM HELPER CLASS
                        mMessageHelper.showDialog(mContext, mContext.getString(R.string.error_title),
                                mContext.getString(R.string.error_message));
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put(ApiConstants.USER_TOKEN, authtoken);
                        return params;
                    }
                };
                queue.add(deleteCategoryRequest);
            }
        });
    } //removeCategory

    // --------------------------------------------------------
    // USER LIST REQUESTS
    // --------------------------------------------------------

    //GET Random List Items
    public void getRandomItems(final ResponseCallback callback) {
        if(!(isNetworkAvailable())){
            mMessageHelper.networkFailMessage();
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(mContext);
        String url = ApiConstants.GET_RANDOM_ITEMS;

        JsonArrayRequest randomItemsRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onFail(error);
            }
        });
        queue.add(randomItemsRequest);
    } //getRandomItemRequest

    //GET User List Items
    public void getUserItems(final UserListCallback callback){
        if(!(isNetworkAvailable())){
            callback.onUserOffline(mSharedPref.getOfflineUserList());
            mMessageHelper.networkFailMessage();
            return;
        }

        mCurrentUser.getToken(new ListUser.AuthCallback() {
            @Override
            public void onAuthed(final String authtoken) {
                RequestQueue queue = Volley.newRequestQueue(mContext);
                String url = ApiConstants.GET_USER_LIST + mSharedPref.getUserId();

                JsonArrayRequest userItemsRequest = new JsonArrayRequest(url,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                callback.onSuccess(response);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG , "> requestUserListItems > onErrorResponse: " + error.getMessage());
                        callback.onFail(error);
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put(ApiConstants.USER_TOKEN, authtoken);
                        return params;
                    }
                };
                queue.add(userItemsRequest);
            }
        });
    } //getUserListItems

    //Add all list items to userlist (previously temp stored items)
    public void addSavedItemsToUserList(){
        Log.v(TAG," > addSavedItemsToUserList, started");
        JSONArray listItemPref;
        listItemPref = mSharedPref.getUserItemPreference();

        try{
            if (listItemPref != null && listItemPref.length() > 0) {

                for (int i = 0; i < listItemPref.length(); i++) {
                    addItemToUserList(listItemPref.getString(i));
                }

            }
        } catch(JSONException e){
            Log.d(TAG, "> addSavedItemsToUserList: " + e.getMessage());
        }
    } //addSavedItemsToUserList

    //Add SINGLE random item to user list
    public void addItemToUserList(final String itemID) {
        if(!(RequestMethods.isNetworkAvailable(mContext))){
            mMessageHelper.networkFailMessage();
            return;
        }
        //Get sessionToken
        mCurrentUser.getToken(new ListUser.AuthCallback() {
            @Override
            public void onAuthed(final String authtoken) {
                RequestQueue queue = Volley.newRequestQueue(mContext);
                String url = ApiConstants.ADD_ITEM + mSharedPref.getUserId() + "/" + itemID;

                //Add single item to user list
                StringRequest postItemRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                Log.v(TAG, "> addItemToUserList > OnResponse: " + response);
                                Log.v(TAG, "AN ITEM IS BEING ADDED");

//                                //TODO: on success remove the item from the sharedPreferences
//                                mSharedPref.deleteUserItemPreference(itemID);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //TODO: Add “not successful“ toast
                        mMessageHelper.showDialog(mContext,
                                mContext.getString(R.string.error_title),
                                mContext.getString(R.string.error_message));
                        Log.d(TAG, " > addItemToUserList > onErrorResponse: " + error.getMessage());
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put(ApiConstants.USER_TOKEN, authtoken);
                        return params;
                    }
                };
                queue.add(postItemRequest);
            }
        });

    } //addItemToUserList

    //REMOVE SINGLE item from user list
    public void removeItemFromUserList(final String itemID){

        if(mCurrentUser.isTempUser()){
            //If not logged in, remove item from sharedPreferences
            mSharedPref.deleteUserItemPreference(itemID);

        } else { //If logged in, remove from DB
            if(!(RequestMethods.isNetworkAvailable(mContext))){
                mMessageHelper.showDialog(mContext,mContext.getString(R.string.error_network_title),
                        mContext.getString(R.string.error_network_message));
                return;
            }

            //Get sessionToken
            mCurrentUser.getToken(new ListUser.AuthCallback() {
                @Override
                public void onAuthed(final String authtoken) { //getToken and then start request
                    RequestQueue queue = Volley.newRequestQueue(mContext);
                    String url = ApiConstants.REMOVE_ITEM + mSharedPref.getUserId() + "/" + itemID;

                    StringRequest deleteItemRequest = new StringRequest(Request.Method.POST, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    //get Response
                                    Log.v(TAG, "> removeItemFromUserList > OnResponse: " + response);
                                    Log.v(TAG, "AN ITEM IS BEING REMOVED");
                                    //TODO: do something with response?
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d(TAG, "> removeItemFromUserList > OnErrorResponse: " + error.getMessage());
                            mMessageHelper.showDialog(mContext, mContext.getString(R.string.error_title),
                                    "There was a problem deleting your item.");
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put(ApiConstants.USER_TOKEN, authtoken);
                            return params;
                        }
                    };
                    queue.add(deleteItemRequest);
                }
            });
        }
    } //removeItemFromUserList


    // --------------------------------------------------------
    // MAKER LIST REQUESTS
    // --------------------------------------------------------

    //TODO: WAITING FOR ENDPOINT
    public void getMakerItems(final String itemName,
                              final String category, final ResponseCallback callback){

        if(!(isNetworkAvailable())){
            mMessageHelper.networkFailMessage();
            return;
        }

        mCurrentUser.getToken(new ListUser.AuthCallback() {
            @Override
            public void onAuthed(final String authtoken) {
                RequestQueue queue = Volley.newRequestQueue(mContext);
                String url = ApiConstants.GET_MAKER_LIST + mSharedPref.getUserId();
                Log.v(TAG, " > getMakerItems, url: " + url);

                JsonArrayRequest getMakerItemsRequest = new JsonArrayRequest(url,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                callback.onSuccess(response);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onFail(error);
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put(ApiConstants.USER_TOKEN, authtoken);
                        return params;
                    }
                };
                queue.add(getMakerItemsRequest);
            }
        });
    } //getMakerItems

    public void addMakerItem(final String title, final String category, final String description,
                            final Uri photoUri, final RequestCallback callback){

        if(!(isNetworkAvailable())){
            callback.onCancelled(CancelResponse.NETWORK_ERROR);
            return;
        }

        //If photo is attached, check file size
        if(photoUri != null){
            if(FileHelper.getFileSize(photoUri) > 8){
                callback.onCancelled(CancelResponse.FILESIZE_ERROR);
                return;
            }
        }

        //Start notification
        final int notificationID = mMessageHelper.getNotificationID();
        startUploadNotification(notificationID, "“" + MessageHelper.capitalize(title) + "”" + " is uploading…");

        //Set up progress bar updater
        final ProgressBarState state = new ProgressBarState();
        final AsyncTask updater =  new ProgressBarUpdater(notificationID).execute(state);

        mCurrentUser.getToken(new ListUser.AuthCallback() {
            @Override
            public void onAuthed(final String authtoken) {
                RequestQueue queue = Volley.newRequestQueue(mContext);
                String url = ApiConstants.ADD_MAKER_ITEM + '/' + mSharedPref.getUserId();

                state.mState = ProgressBarState.State.START;

                //Upload Request
                StringRequest addMakerItemRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.v(TAG, "addMakerItem > onResponse: " + response);

                                state.mState = ProgressBarState.State.FINISHED;

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        mBuilder.setContentText("“" + title + "”" + " uploaded successfully");
                                        // Removes the progress bar
                                        mBuilder.setProgress(0, 0, false);
                                        mNotifyManager.notify(notificationID, mBuilder.build());
                                    }
                                }, 1000);

                                callback.onSuccess();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.v(TAG, "addMakerItem > OnErrorResponse: " + error.getMessage());

                        state.mState = ProgressBarState.State.ERROR;

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mBuilder.setContentText("“" + MessageHelper.capitalize(title) + "”" + " failed to upload");
                                // Removes the progress bar
                                mBuilder.setProgress(0, 0, false);
                                mNotifyManager.notify(notificationID, mBuilder.build());
                            }
                        }, 1000);

                        callback.onFail();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();

                        if (description != null) {
                            params.put(ApiConstants.MAKER_ITEM_DESCRIPTION, description);
                        }

                        if (photoUri != null) {
                            byte [] image = FileHelper.getByteArrayFromFile(mContext, photoUri);
                            image = FileHelper.reduceImageForUpload(image);

                            String photoFile = new String(Base64.encode(image, Base64.DEFAULT));
                            params.put(ApiConstants.POST_PHOTO_KEY, photoFile);
                        }

                        params.put(ApiConstants.MAKER_ITEM_NAME, title);
                        params.put(ApiConstants.MAKER_ITEM_CATEGORY, category);
                        params.put(ApiConstants.USER_TOKEN, authtoken);

                        return params;
                    }
                };
                queue.add(addMakerItemRequest);
                state.mState = ProgressBarState.State.REQUEST_SENT;

            }
        });
    } //addMakerItem + photo


    // --------------------------------------------------------
    // USER PHOTO REQUESTS
    // --------------------------------------------------------


    public void getUserPhotos(final ResponseCallback callback) {
        if (!(isNetworkAvailable())) {
            mMessageHelper.galleryNetworkFailMessage();
            return;
        }

        mCurrentUser.getToken(new ListUser.AuthCallback() {
            @Override
            public void onAuthed(final String authtoken) {
                RequestQueue queue = Volley.newRequestQueue(mContext);
                String url = ApiConstants.GET_USER_PHOTOS + mSharedPref.getUserId();

                JsonArrayRequest getUserPhotosRequest = new JsonArrayRequest(url,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                //Log.v(TAG, "> getUserCategories > onResponse: " + response);
                                callback.onSuccess(response);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Log.d(TAG, "> getUserCategories > onErrorResponse: " + error.getMessage());
                        callback.onFail(error);
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put(ApiConstants.USER_TOKEN, authtoken);
                        return params;
                    }
                };
                queue.add(getUserPhotosRequest);
            }
        });
    } //getUserPhotos

    public void uploadPhoto(final UserListItem listItem, final Uri photoUri, final RequestCallback callback) {
        if(!(isNetworkAvailable())){
            callback.onCancelled(CancelResponse.NETWORK_ERROR);
            return;
        }

        if(FileHelper.getFileSize(photoUri) > 8){
            callback.onCancelled(CancelResponse.FILESIZE_ERROR);
            return;
        }

//        if(!FileHelper.getFileType(mContext, photoUri).equals(PhotoConstants.FILE_TYPE)){
//            mMessageHelper.photoUploadFileTypeFailMessage();
//            return;
//        }

        //Start notification
        final int notificationID = mMessageHelper.getNotificationID();
        startUploadNotification(notificationID, "“" + listItem.getItemName() + "”" + " is uploading…");

        //Set up progress bar updater
        final ProgressBarState state = new ProgressBarState();
        final AsyncTask updater =  new ProgressBarUpdater(notificationID).execute(state);

        mCurrentUser.getToken(new ListUser.AuthCallback() {
            @Override
            public void onAuthed(final String authtoken) {
                RequestQueue queue = Volley.newRequestQueue(mContext);
                final String photoFile = FileHelper.createUploadPhotoObject(mContext, photoUri);
                String url = ApiConstants.ADD_PHOTO + mSharedPref.getUserId() + "/" + listItem.getItemID();

                state.mState = ProgressBarState.State.START;

                //Upload Request
                StringRequest uploadPhotoRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                //Get Response
                                Log.v(TAG, "uploadPhoto > onResponse: " + response);
                                state.mState = ProgressBarState.State.FINISHED;

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        mBuilder.setContentText(listItem.getItemName() + " uploaded successfully");
                                        // Removes the progress bar
                                        mBuilder.setProgress(0, 0, false);
                                        mNotifyManager.notify(notificationID, mBuilder.build());
                                    }
                                }, 1000);

                                callback.onSuccess();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "uploadPhoto > onErrorResponse: " + error.getMessage());

                        state.mState = ProgressBarState.State.ERROR;

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mBuilder.setContentText("“" + listItem.getItemName() + "”" + " failed to upload");
                                // Removes the progress bar
                                mBuilder.setProgress(0, 0, false);
                                mNotifyManager.notify(notificationID, mBuilder.build());
                            }
                        }, 1000);

                        callback.onFail();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put(ApiConstants.POST_PHOTO_KEY, photoFile);
                        params.put(ApiConstants.USER_TOKEN, authtoken);
                        return params;
                    }
                };
                queue.add(uploadPhotoRequest);
                state.mState = ProgressBarState.State.REQUEST_SENT;
            }
        });

    } //uploadPhoto

    //Helper Classes
    public class ProgressBarUpdater extends AsyncTask<Object, Integer, Integer> {

        private ProgressBarState state;
        private int notificationID;

        public ProgressBarUpdater(int id){
            super();
            notificationID = id;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // Displays the progress bar for the first time.
            mBuilder.setProgress(100, 0, false);
            mNotifyManager.notify(notificationID, mBuilder.build());
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            // Update progress
            mBuilder.setProgress(100, values[0], false);
            mNotifyManager.notify(notificationID, mBuilder.build());
            super.onProgressUpdate(values);
        }

        @Override
        protected Integer doInBackground(Object... params) {
            state = (ProgressBarState) params[0];

            int currentVal = 0;
            int targetVal = 0;

            while(true){
                switch(state.mState){
                    case START:
                        targetVal = 30;

                        break;
                    case REQUEST_SENT:
                        targetVal = 85;

                        break;
                    case REQUEST_RECEIVED:
                        targetVal = 90;

                        break;
                    case FINISHED:
                        targetVal = 100;

                        break;
                    case ERROR:
                        cancel(true);
                        break;
                    case TIMEOUT:
                        cancel(true);
                        break;
                }

                if(currentVal < targetVal){
                    currentVal++;

                    if(currentVal > 80){
                        mBuilder.setProgress(0, 0, true);
                    }

                    if(currentVal == 100 || state.mState == ProgressBarState.State.FINISHED){
                        currentVal = 100;
                        publishProgress(currentVal, 100);
                        cancel(true);
                    }
                }

                // Sets the progress indicator completion percentage
                publishProgress(currentVal, 100);

                try {
                    // Sleep for 1 seconds
                    Thread.sleep(80);
                } catch (InterruptedException e) {
                    Log.d("ProgressBarUpdater", "sleep failure");
                }

                if(isCancelled()){
                    break;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
        }
    } //Uploader

} //RequestMethods
