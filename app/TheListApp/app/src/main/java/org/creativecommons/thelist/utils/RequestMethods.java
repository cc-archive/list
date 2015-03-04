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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.creativecommons.thelist.R;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public final class RequestMethods {
    public static final String TAG = RequestMethods.class.getSimpleName();
    protected Context mContext;
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
    // USER LIST REQUESTS
    // --------------------------------------------------------

    //GET Random Items from API
    public void getRandomItems(final ResponseCallback callback) {
        if(!(isNetworkAvailable())){
            mMessageHelper.networkFailMessage();
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(mContext);
        String url = ApiConstants.GET_RANDOM_ITEMS;

        JsonArrayRequest randomItemRequest = new JsonArrayRequest(url,
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
        queue.add(randomItemRequest);
    } //getRandomItemRequest




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

        JsonArrayRequest getCategoriesRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.v(TAG, "> getCategories > onResponse: " + response);
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "> getCategories > onErrorResponse: " + error.getMessage());
                callback.onFail(error);
            }
        });
        queue.add(getCategoriesRequest);
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

                JsonArrayRequest getCategoriesRequest = new JsonArrayRequest(url,
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
                queue.add(getCategoriesRequest);
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
        listCategoryPref = mSharedPref.RetrieveCategorySharedPreference();

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
                StringRequest postCategory = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.v(TAG, "> addCategory > OnResponse: " + response);
                                Log.v(TAG, "A CATEGORY IS BEING ADDED");

                                mSharedPref.RemoveUserCategoryPreference(catId);
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
                queue.add(postCategory);
            }
        });
    } //addCategory

    public void removeCategory(final String catId){

        if(mCurrentUser.isTempUser()){ //TEMP USER

            //If not logged in, remove item from sharedPreferences
            mSharedPref.RemoveUserItemPreference(catId);

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

                    StringRequest deleteItemRequest = new StringRequest(Request.Method.POST, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    //get Response
                                    Log.v(TAG, "> removeCategory > onResponse: " + response);
                                    Log.v(TAG, "AN ITEM IS BEING REMOVED");
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
                    queue.add(deleteItemRequest);
                }
            });
        }
    } //removeCategory


    // --------------------------------------------------------
    // USER PHOTO REQUESTS
    // --------------------------------------------------------

    public void uploadPhoto(String itemID, Uri photoUri, final RequestCallback callback) {

        if(!(isNetworkAvailable())){
            mMessageHelper.photoNetworkFailMessage();
            return;
        }

        if(FileHelper.getFileSize(photoUri) > 8){
            mMessageHelper.photoSizeFailMessage();
            return;
        }

        //Get Photo as Base64 encoded String
        final String photoFile = FileHelper.createUploadPhotoObject(mContext, photoUri);
        final String url = ApiConstants.ADD_PHOTO + mSharedPref.getUserId() + "/" + itemID;

        mCurrentUser.getToken(new ListUser.AuthCallback() {
            @Override
            public void onSuccess(final String authtoken) {
                RequestQueue queue = Volley.newRequestQueue(mContext);

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

} //RequestMethods
