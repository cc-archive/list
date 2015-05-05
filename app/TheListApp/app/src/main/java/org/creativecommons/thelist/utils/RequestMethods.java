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

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
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

    public RequestMethods(Context mc) {
        mContext = mc;
        mMessageHelper = new MessageHelper(mc);
        mSharedPref = new SharedPreferencesMethods(mc);
        mCurrentUser = new ListUser(mc);
    }

    //Callback for requests
    public interface RequestCallback {
        void onSuccess();
        void onFail();
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
    // APP REQUEST
    // --------------------------------------------------------

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
            public void onSuccess(final String authtoken) {
                RequestQueue queue = Volley.newRequestQueue(mContext);
                String url = ApiConstants.GET_USER_LIST + mCurrentUser.getUserID();

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


    // --------------------------------------------------------
    // MAKER LIST REQUESTS
    // --------------------------------------------------------

    public void getMakerItems(final String itemName,
                              final String category, final ResponseCallback callback){

        if(!(isNetworkAvailable())){
            mMessageHelper.networkFailMessage();
            return;
        }

        mCurrentUser.getToken(new ListUser.AuthCallback() {
            @Override
            public void onSuccess(final String authtoken) {
                RequestQueue queue = Volley.newRequestQueue(mContext);
                String url = ApiConstants.GET_MAKER_LIST + mCurrentUser.getUserID();
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
            mMessageHelper.networkFailMessage();
            return;
        }

        //Check for login: required for this request
        mCurrentUser.getToken(new ListUser.AuthCallback() {
            @Override
            public void onSuccess(final String authtoken) {
                RequestQueue queue = Volley.newRequestQueue(mContext);

                //TODO: add legit url
                String url = ApiConstants.ADD_MAKER_ITEM + '/' + mCurrentUser.getUserID();

                //Upload Request
                StringRequest addMakerItemRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.v(TAG, "addMakerItem > onResponse: " + response);
                                callback.onSuccess();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //TODO: add switch for all possible error codes
                        callback.onFail();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();

                        if(description != null) {
                            params.put(ApiConstants.MAKER_ITEM_DESCRIPTION, description);
                        }

                        if(photoUri != null){
                            String photoFile = FileHelper.createUploadPhotoObject(mContext, photoUri);
                            params.put(ApiConstants.POST_PHOTO_KEY, photoFile);
                        }

                        params.put(ApiConstants.MAKER_ITEM_NAME, title);
                        params.put(ApiConstants.MAKER_ITEM_CATEGORY, category);
                        params.put(ApiConstants.USER_TOKEN, authtoken);

                        return params;
                    }
                };
                queue.add(addMakerItemRequest);

            }
        });
    } //addMakerItem + photo


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
            public void onSuccess(final String authtoken) {
                RequestQueue queue = Volley.newRequestQueue(mContext);
                String url = ApiConstants.GET_USER_CATEGORIES + mCurrentUser.getUserID();

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
        if(!(RequestMethods.isNetworkAvailable(mContext))){
            mMessageHelper.networkFailMessage();
            return;
        }

        mCurrentUser.getToken(new ListUser.AuthCallback() {
            @Override
            public void onSuccess(final String authtoken) {
                RequestQueue queue = Volley.newRequestQueue(mContext);
                String url = ApiConstants.ADD_CATEGORY + mCurrentUser.getUserID() + "/" + catId;

                //Add single item to user list
                StringRequest postCategoryRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.v(TAG, "> addCategory > OnResponse: " + response);
                                Log.v(TAG, "A CATEGORY IS BEING ADDED");

                                mSharedPref.deleteUserCategoryPreference(catId);
                                Toast.makeText(mContext,"category added", Toast.LENGTH_SHORT).show();
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
            mSharedPref.deleteUserItemPreference(catId);
        } else { //If logged in, remove from DB

            if(!(RequestMethods.isNetworkAvailable(mContext))){
                mMessageHelper.networkFailMessage();
                return;
            }

            mCurrentUser.getToken(new ListUser.AuthCallback() {
                @Override
                public void onSuccess(final String authtoken) { //getToken and then start request
                    RequestQueue queue = Volley.newRequestQueue(mContext);
                    String url = ApiConstants.REMOVE_CATEGORY + mCurrentUser.getUserID() + "/" + catId;

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
        }
    } //removeCategory


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
            public void onSuccess(final String authtoken) {
                RequestQueue queue = Volley.newRequestQueue(mContext);
                String url = ApiConstants.GET_USER_PHOTOS + mCurrentUser.getUserID();
                Log.v(TAG, " > getUserPhotos, url: " + url);

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

    public void uploadPhoto(final String itemID, final Uri photoUri, final RequestCallback callback) {
        if(!(isNetworkAvailable())){
            mMessageHelper.photoUploadNetworkFailMessage();
            return;
        }

        if(FileHelper.getFileSize(photoUri) > 8){
            mMessageHelper.photoUploadSizeFailMessage();
            return;
        }

        if(!FileHelper.getFileType(mContext, photoUri).equals(PhotoConstants.FILE_TYPE)){
            mMessageHelper.photoUploadFileTypeFailMessage();
            return;
        }

        mCurrentUser.getToken(new ListUser.AuthCallback() {
            @Override
            public void onSuccess(final String authtoken) {
                RequestQueue queue = Volley.newRequestQueue(mContext);
                final String photoFile = FileHelper.createUploadPhotoObject(mContext, photoUri);
                String url = ApiConstants.ADD_PHOTO + mSharedPref.getUserId() + "/" + itemID;

                //Upload Request
                StringRequest uploadPhotoRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                //Get Response
                                Log.v(TAG, "uploadPhoto > onResponse: " + response);
                                //mMessageHelper.notifyUploadSuccess();
                                callback.onSuccess();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "uploadPhoto > onErrorResponse: " + error.getMessage());
                        //TODO: add switch for all possible error codes
                        //mMessageHelper.notifyUploadFail();
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
            }
        });
    } //uploadPhoto

    // --------------------------------------------------------
    // UPLOAD ASYNC
    // --------------------------------------------------------





} //RequestMethods
