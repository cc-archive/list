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

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
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
    RequestMethods requestMethods;
    SharedPreferencesMethods sharedPreferencesMethods;
    //SharedPreferencesMethods sharedPreferencesMethods = new SharedPreferencesMethods(this);
    ListUser mCurrentUser = new ListUser(this);

    //GET Request
    protected JSONArray mRandomItemData;
    protected JSONObject mListItemData;
    String mMakerName;
    String mItemName;
    String mItemID;

    //PUT request (if user is logged in)
    protected JSONObject mPutResponse;

    //Handle Data
    private List<MainListItem> mItemList;
    //private ArrayList<String> mItemsViewed = new ArrayList<String>();

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
        requestMethods = new RequestMethods(mContext);
        sharedPreferencesMethods = new SharedPreferencesMethods(mContext);

        mItemList = new ArrayList<MainListItem>();
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
                    //Log.v("THIS IS ITEMLIST", mItemList.toString());
                    //Once yes has been hit 3 times, forward to
                    //TODO: Also condition that user is logged in

                    //If User is logged in…
                    if(mCurrentUser.isLoggedIn()) {
                        Log.v("LOGGED IN", "user is logged in");
                        //Add to UserList
                        mCurrentUser.addItemToUserList(mItemID); //includes confirmation toast

                        //Add items to ItemList
                        MainListItem listItem = new MainListItem();
                        listItem.setItemID(mItemID);
                        listItem.setItemName(mItemName);
                        listItem.setMakerName(mMakerName);
                        mItemList.add(listItem);
                    //or if user is not logged in…
                    } else {
                        //Create list for non-existent user
                        MainListItem listItem = new MainListItem();
                        listItem.setItemID(mItemID);
                        listItem.setItemName(mItemName);
                        listItem.setMakerName(mMakerName);
                        mItemList.add(listItem);

                        //Log.v("ITEM ID", mItemID);

                        //Toast: Confirm List Item has been added
                        final Toast toast = Toast.makeText(RandomActivity.this,
                                "Added to Your List", Toast.LENGTH_SHORT);
                        toast.show();
                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                toast.cancel();
                            }
                        }, 1500);
                    }
                    //If mItemList has less than 3 items
                    if(mItemList.size() < 3) {
                        //Show me a new item
                        updateView();
                    //otherwise: save the items to shared preferences and go to new task
                    } else {
                        //Get array of selected item IDS
                        List<String> userItemList = requestMethods.getItemIds(mItemList);
                        //Log.v(TAG,mItemList.toString());

                        //Save Array as String to sharedPreferences
                        sharedPreferencesMethods.SaveSharedPreference
                                (sharedPreferencesMethods.LIST_ITEM_PREFERENCE,
                                        userItemList.toString());

                        //Start MainActivity
                        Intent intent = new Intent(mContext, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                } //OnClick
            }); //YesButton.setOnClickListener

            //No Button Functionality
            NoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateView();
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
            //Log.v("NO BUTTON", "THIS IS THE ERROR ERROR ERROR");
        }
    } //onCreate

    private void updateView() {
        mProgressBar.setVisibility(View.INVISIBLE);
        if(mRandomItemData == null) {
            //TODO: better error message
            requestMethods.showErrorDialog(mContext, "Oops", "No data found. Please try again.");
        }
        else {
            try {
                if(itemPositionCount == mRandomItemData.length()) {
                    //If you run out of items, just go to MainActivity
                    Intent intent = new Intent(RandomActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    mListItemData = mRandomItemData.getJSONObject(itemPositionCount);
                    //Log.v(TAG, mListItemData.toString());
                    //Store values from response JSON Array
                    mItemID = mListItemData.getString(ApiConstants.ITEM_ID);
                    mItemName = mListItemData.getString(ApiConstants.ITEM_NAME);
                    mMakerName = mListItemData.getString(ApiConstants.MAKER_NAME);
                    //Log.v(TAG +" this is the maker name for this item", mMakerName);
                    //Update UI
                    mTextView.setText(mMakerName + " needs a picture of " + mItemName);

                    itemPositionCount++;
                }
            } catch (JSONException e) {
                Log.e(TAG,e.getMessage());
            }
        }
    } //updateView

//    ------------------------------------------------------------------------------------------
//    ------------------------------------------------------------------------------------------

    //GET Random Items from API
    private void getRandomItemRequest() {
        RequestQueue queue = Volley.newRequestQueue(this);
        //Genymotion Emulator
        String url = ApiConstants.GET_RANDOM_ITEMS;

        JsonArrayRequest randomItemRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //Handle Data
                        mRandomItemData = response;
                        //Log.v("HI RANDOM DATA", response.toString());
                        updateView();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.v("error", error.toString() + "THIS IS THE ERROR ERROR ERROR IN GET REQUEST");
                requestMethods.showErrorDialog(mContext,
                        getString(R.string.error_title),
                        getString(R.string.error_message));

            }
        });
        queue.add(randomItemRequest);
    } //getRandomItemRequest

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

    @Override
    public void onStop() {
        super.onStop();

        mItemList = null;
    }
}
