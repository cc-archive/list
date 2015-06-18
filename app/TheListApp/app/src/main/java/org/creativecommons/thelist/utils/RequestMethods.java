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
import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

public final class RequestMethods {
    public static final String TAG = RequestMethods.class.getSimpleName();
    protected Context mContext;

    //Helper Methods
    protected MessageHelper mMessageHelper;
    protected SharedPreferencesMethods mSharedPref;
    protected ListUser mCurrentUser;

    public RequestMethods(Context context) {
        mContext = context;
        mMessageHelper = new MessageHelper(context);
        mSharedPref = new SharedPreferencesMethods(context);
        mCurrentUser = new ListUser(context);
    }

    // --------------------------------------------------------
    // NETWORK CHECKS
    // --------------------------------------------------------

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

    public void getUserProfile(final NetworkUtils.ResponseCallback callback){
        if (!(isNetworkAvailable())) {
            mMessageHelper.galleryNetworkFailMessage();
            return;
        }
        //TODO: change to proper request for stats once it exists
        mCurrentUser.getToken(new ListUser.TokenCallback() {
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
    public void getCategories(final NetworkUtils.ResponseCallback callback) {
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
    public void getUserCategories(final NetworkUtils.ResponseCallback callback) {
        if(!(isNetworkAvailable())){
            mMessageHelper.networkFailMessage();
            return;
        }

        mCurrentUser.getToken(new ListUser.TokenCallback() {
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

    //Add Single Category to User Account
    public void addCategory(final String catId){

        if(!(isNetworkAvailable())){
            mMessageHelper.networkFailMessage();
            return;
        }

        mCurrentUser.getToken(new ListUser.TokenCallback() {
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
                                //Log.v(TAG, "A CATEGORY IS BEING ADDED");

                                Toast.makeText(mContext, 
                                        mContext.getString(R.string.add_category_toast_success), 
                                        Toast.LENGTH_SHORT).show();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, " > addCategory > onErrorResponse: " + error.getMessage());
                        //TODO: remove to MessageHelper class
                        mMessageHelper.categoryAddFail();
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

        if(!(isNetworkAvailable())){
            mMessageHelper.networkFailMessage();
            return;
        }

        mCurrentUser.getToken(new ListUser.TokenCallback() {
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
                                //Log.v(TAG, "A CATEGORY IS BEING REMOVED");
                                //TODO: do something with response?
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, " > removeCategory > onErrorResponse: " + error.getMessage());
                        //TODO: remove to MessageHelper class
                        mMessageHelper.categoryRemoveFail();
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
    public void getRandomItems(final NetworkUtils.ResponseCallback callback) {
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
    public void getUserItems(final NetworkUtils.UserListCallback callback){
        if(!(isNetworkAvailable())){
            mMessageHelper.networkFailMessage();
            callback.onUserOffline(mSharedPref.getOfflineUserList());
            return;
        }

        mCurrentUser.getToken(new ListUser.TokenCallback() {
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

    //Add SINGLE random item to user list
    public void addItemToUserList(final String itemID) {
        if(!(isNetworkAvailable())){
            mMessageHelper.networkFailMessage();
            return;
        }
        //Get sessionToken
        mCurrentUser.getToken(new ListUser.TokenCallback() {
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
                                //Log.v(TAG, "AN ITEM IS BEING ADDED");
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, " > addItemToUserList > onErrorResponse: " + error.getMessage());
                        //TODO: move to message helper and Add callback to request
                        Toast.makeText(mContext, mContext.getString(R.string.add_item_toast_error),
                                Toast.LENGTH_SHORT).show();

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
        if(!(isNetworkAvailable())){
            mMessageHelper.networkFailMessage();
            return;
        }

        //Get sessionToken
        mCurrentUser.getToken(new ListUser.TokenCallback() {
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
                                //Log.v(TAG, "AN ITEM IS BEING REMOVED");

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "> removeItemFromUserList > OnErrorResponse: " + error.getMessage());
                        //TODO: move to message helper and Add callback to request
                        Toast.makeText(mContext, mContext.getString(R.string.remove_item_toast_error),
                                Toast.LENGTH_SHORT).show();
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

    } //removeItemFromUserList


    // --------------------------------------------------------
    // MAKER LIST REQUESTS
    // --------------------------------------------------------

    //TODO: WAITING FOR ENDPOINT
    public void getMakerItems(final String itemName,
                              final String category, final NetworkUtils.ResponseCallback callback){

        if(!(isNetworkAvailable())){
            mMessageHelper.networkFailMessage();
            return;
        }

        mCurrentUser.getToken(new ListUser.TokenCallback() {
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


    // --------------------------------------------------------
    // USER PHOTO REQUESTS
    // --------------------------------------------------------


    public void getUserPhotos(final NetworkUtils.ResponseCallback callback) {
        if (!(isNetworkAvailable())) {
            mMessageHelper.galleryNetworkFailMessage();
            return;
        }

        mCurrentUser.getToken(new ListUser.TokenCallback() {
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

} //RequestMethods
