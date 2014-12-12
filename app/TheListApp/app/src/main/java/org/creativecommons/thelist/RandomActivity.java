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

package org.creativecommons.thelist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.creativecommons.thelist.adapters.MainListItem;
import org.creativecommons.thelist.utils.ApiConstants;
import org.creativecommons.thelist.utils.ListUser;
import org.creativecommons.thelist.utils.RequestMethods;
import org.creativecommons.thelist.utils.SharedPreferencesMethods;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RandomActivity extends Activity {
    public static final String TAG = RandomActivity.class.getSimpleName();
    protected Context mContext;

    //Helper Methods
    RequestMethods requestMethods = new RequestMethods(this);
    //SharedPreferencesMethods sharedPreferencesMethods = new SharedPreferencesMethods(this);
    ListUser mCurrentUser = new ListUser(this);

    //GET Request
    protected JSONArray mRandomItemData;
    protected JSONObject mListItemData;
    protected JSONObject mMakerNameData;
    String mMakerName;
    String mMakerID;
    String mItemName;
    String mItemID;

    //PUT request (if user is logged in)
    protected JSONObject mPutResponse;

    //Handle Data
    private List<MainListItem> mItemList = new ArrayList<MainListItem>();
    private ArrayList<String> mItemsViewed = new ArrayList<String>();

    //UI Elements
    TextView mTextView;
    ProgressBar mProgressBar;

    //Shared variables
    int itemAddedCount;
    int itemPositionCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random);
        mContext = this;

        mTextView = (TextView) findViewById(R.id.item_text);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        //Picker Buttons
        ImageButton YesButton = (ImageButton) findViewById(R.id.YesButton);
        ImageButton NoButton = (ImageButton) findViewById(R.id.NoButton);
        ImageButton CameraButton = (ImageButton) findViewById(R.id.CameraButton);

        if(requestMethods.isNetworkAvailable(mContext)) {
            mProgressBar.setVisibility(View.VISIBLE);
            getRandomItemRequest();

            //Yes Button Listener
            YesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Once yes has been hit 3 times, forward to
                    //TODO: Also condition that user is logged in
                    if(mItemList.size() < 3) {
                        if(mCurrentUser.isLoggedIn()) {
                            //TODO: DO PUT REQUEST TO USER LIST

                        } else {
                            //TODO: Save item id to list
                            //Create list of items
                            MainListItem listItem = new MainListItem();
                            listItem.setItemID(mItemID);
                            listItem.setItemName(mItemName);
                            listItem.setMakerName(mMakerName);
                            mItemList.add(listItem);
                        }

                        //Toast: Confirm List Item has been added
                        final Toast toast = Toast.makeText(RandomActivity.this, "Added to Your List", Toast.LENGTH_SHORT);
                        toast.show();
                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                toast.cancel();
                            }
                        }, 1500);

                        updateList();

                    } else {

                        //TODO: SAVE LIST TO PREFERENCES AND GO TO NEW ACTIVITY
                        //If user is logged in, send chosen list items to DB
                        if(mCurrentUser.isLoggedIn()) {
                            putRandomItemsRequest();
                        }
                        else {
                            //Get array of selected item IDS
                            List<String> userItemList = requestMethods.getItemIds(mItemList);
                            //Log.v(TAG,mItemList.toString());

                            //Save Array as String to sharedPreferences
                            SharedPreferencesMethods.SaveSharedPreference
                                    (SharedPreferencesMethods.LIST_ITEM_PREFERENCE,
                                            SharedPreferencesMethods.LIST_ITEM_PREFERENCE_KEY,
                                            userItemList.toString(), mContext);
                        }

                        //Start MainActivity
                        Intent intent = new Intent(mContext, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }
            });
            //No Button Functionality
            NoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO:THIS ELSE NEEDS TO UPDATE LIST
                    updateList();
                }
            });

            //TODO: Camera Intent, (possibly remove if user is not logged in)
            //Camera Button Functionality
//            CameraButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                }
//            });
        } else {
            //Display network error
            requestMethods.showErrorDialog(mContext,
                    getString(R.string.error_title),
                    getString(R.string.error_message));
        }
    } //onCreate

    private void updateList() {
        mProgressBar.setVisibility(View.INVISIBLE);
        if(mRandomItemData == null) {
            //TODO: better error message
            requestMethods.showErrorDialog(mContext, "Oops", "No data found. Please try again.");
        }
        else {
            try {

                mListItemData = mRandomItemData.getJSONObject(itemPositionCount);
                //Store values from response JSON Array
                mItemID = mListItemData.getString(ApiConstants.ITEM_ID);
                mMakerID = mListItemData.getString(ApiConstants.MAKER_ID);

                getMakerRequest(mMakerID); //Updates UI as well

                itemPositionCount++;

//                //If the user has seen the item before, select a new item
//                //TODO: use this to prevent user of seeing repeat items in a single session?
//                if(mItemsViewed.contains(mItemID) && mItemsViewed.size() < 20){
//                    Log.v(TAG, "this item has been viewed before");
//                    getRandomItemRequest();
//                } else {
//                    Log.v(TAG, "this item is new to me");
//                    //Add item ID to list of items user has seen
//                    mItemsViewed.add(mItemID);
//                    mItemName = mListItemData.getString(ApiConstants.ITEM_NAME);
//                    mMakerName = mListItemData.getString(ApiConstants.MAKER_NAME);
//                    //Update UI
//                    mTextView.setText(mMakerName + " needs a picture of " + mItemName);
//                }
            } catch (JSONException e) {
                Log.e(TAG,e.getMessage());
            }
        }
    } //updateList

//    ------------------------------------------------------------------------------------------
//    ------------------------------------------------------------------------------------------

    //GET Random Items from API
    private void getRandomItemRequest() {
        RequestQueue queue = Volley.newRequestQueue(this);
        //Genymotion Emulator
        String url = ApiConstants.GET_CATEGORIES;
        //Android Default Emulator
        //String url = "http://10.0.2.2:3000/api/category";

        JsonArrayRequest randomItemRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //Handle Data
                        mRandomItemData = response;

                        try {
                            mMakerID = response.getJSONObject(itemPositionCount).getString("makerid");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error", error.toString());
                requestMethods.showErrorDialog(mContext,
                        getString(R.string.error_title),
                        getString(R.string.error_message));
            }
        });
        queue.add(randomItemRequest);
    } //getRandomItemRequest

    private void getMakerRequest(String makerID) {
        RequestQueue queue = Volley.newRequestQueue(this);
        //Genymotion Emulator
        String url = ApiConstants.GET_MAKER_NAME + makerID;

        JsonArrayRequest makerRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //Handle Data
                        mRandomItemData = response;
                        try {
                            mItemName = mListItemData.getString(ApiConstants.ITEM_NAME);
                            mMakerName = mMakerNameData.getString(ApiConstants.MAKER_NAME);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //Update UI
                        mTextView.setText(mMakerName + " needs a picture of " + mItemName);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error", error.toString());
                requestMethods.showErrorDialog(mContext,
                        getString(R.string.error_title),
                        getString(R.string.error_message));
            }
        });
        queue.add(makerRequest);
    } //getRandomItemRequest

    //Add random item to user list
    private void putRandomItemsRequest() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String userID = mCurrentUser.getUserID();
        //Genymotion Emulator
        String url = ApiConstants.UPDATE_USER + userID;

        //Retrieve User list item preferences
        JSONArray userPreferences = SharedPreferencesMethods.RetrieveSharedPreference
                (SharedPreferencesMethods.LIST_ITEM_PREFERENCE,
                        SharedPreferencesMethods.LIST_ITEM_PREFERENCE_KEY, this);

        //Create Object to send
        JSONObject jso = new JSONObject();
        try {
            jso.put(ApiConstants.USER_ITEMS, userPreferences);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
        //Log.v(TAG,jso.toString());

        //Add items to user list
        JsonObjectRequest putItemsRequest = new JsonObjectRequest(Request.Method.POST, url, jso,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //get Response
                        //Log.v(TAG,response.toString());
                        mPutResponse = response;
                        //TODO: do something with response?
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse (VolleyError error){
                requestMethods.showErrorDialog(mContext,
                        getString(R.string.error_title),
                        getString(R.string.error_message));
            }
        });
        queue.add(putItemsRequest);
    } //putRandomItemsRequest


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_random, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
