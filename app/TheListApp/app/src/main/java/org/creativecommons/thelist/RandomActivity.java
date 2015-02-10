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
import android.widget.Button;
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

    //Handle Data
    private List<MainListItem> mItemList;
    //private ArrayList<String> mItemsViewed = new ArrayList<String>();

    //UI Elements
    TextView mTextView;
    ProgressBar mProgressBar;
    Button mDoneButton;

    //Shared variables
    int itemPositionCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random);
        mContext = this;
        requestMethods = new RequestMethods(mContext);
        sharedPreferencesMethods = new SharedPreferencesMethods(mContext);

        //Google Analytics Tracker
//        Tracker t = ((ListApplication) RandomActivity.this.getApplication()).getTracker(
//                ListApplication.TrackerName.GLOBAL_TRACKER);
//
//        t.setScreenName(TAG);
//        t.send(new HitBuilders.AppViewBuilder().build());

        mItemList = new ArrayList<>();
        mTextView = (TextView) findViewById(R.id.account_text);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mDoneButton = (Button) findViewById(R.id.doneButton);

        //Picker Buttons
        ImageButton yesButton = (ImageButton) findViewById(R.id.YesButton);
        ImageButton noButton = (ImageButton) findViewById(R.id.NoButton);
        //TODO: add camera functionality?
        //ImageButton CameraButton = (ImageButton) findViewById(R.id.CameraButton);

        if(requestMethods.isNetworkAvailable(mContext)) {
            mProgressBar.setVisibility(View.VISIBLE);
            getRandomItemRequest();

            //Yes Button Listener
            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Add items to ItemList
                    MainListItem listItem = new MainListItem();
                    listItem.setItemID(mItemID);
                    listItem.setItemName(mItemName);
                    listItem.setMakerName(mMakerName);
                    mItemList.add(listItem);

                    //Toast: Confirm List Item has been added
                    final Toast toast = Toast.makeText(RandomActivity.this,
                            "Added to Your List", Toast.LENGTH_SHORT);
                    toast.show();
                    new android.os.Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            toast.cancel();
                        }
                    }, 1000);

                    //If logged in, add item to user’s list right away
                    if (!(mCurrentUser.getAuthed(RandomActivity.this).equals(ListUser.TEMP_USER))) {
                        Log.v("LOGGED IN", "user is logged in");
                        //Add to UserList
                        mCurrentUser.addItemToUserList(mItemID); //NB: includes confirmation toast
                    }
                    //Display a new item
                    updateView();
                    //show doneButton if user has selected at least 3 items
                    if (mItemList.size() == 3) { //once it has 3 items
                        mDoneButton.setVisibility(View.VISIBLE);
                    }
                } //OnClick
            }); //YesButton.setOnClickListener

            //No Button Functionality
            noButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateView();
                    if(itemPositionCount >= 7 && mDoneButton.getVisibility() == View.INVISIBLE){
                        mDoneButton.setVisibility(View.VISIBLE);
                    }
                }
            });

            //Done Button Functionality
            mDoneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Get array of selected item IDS
                    if((mCurrentUser.getAuthed(RandomActivity.this).equals(ListUser.TEMP_USER))){
                        List<String> userItemList = requestMethods.getItemIds(mItemList);
                        JSONArray oldItemArray = sharedPreferencesMethods.RetrieveUserItemPreference();
                        //Log.v(TAG,mItemList.toString());
                        for (int i = 0; i < oldItemArray.length(); i++) {
                            try {
                                userItemList.add(0, oldItemArray.getString(i));
                            } catch (JSONException e) {
                                Log.v(TAG, e.getMessage());
                            }
                        }
                        Log.v("LIST OF ALL ITEMS ADDED", userItemList.toString());
                        //Save Array as String to sharedPreferences
                        sharedPreferencesMethods.SaveSharedPreference
                                (SharedPreferencesMethods.LIST_ITEM_PREFERENCE_KEY,
                                        userItemList.toString());

                        String sharedPref = sharedPreferencesMethods.RetrieveSharedPreferenceList
                                (SharedPreferencesMethods.LIST_ITEM_PREFERENCE_KEY).toString();
                        Log.v("ALL ITEMS IN USER PREF", sharedPref);
                    }
                    Intent intent = new Intent(mContext, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }); //Done Button
        } else { //Display network error
            requestMethods.showErrorDialog(mContext,
                    getString(R.string.error_network_title),
                    getString(R.string.error_network_message));
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
                        Log.v("HI RANDOM DATA", response.toString());
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
        if (id == R.id.action_done) {
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
