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

package org.creativecommons.thelist.activities;

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

import com.android.volley.VolleyError;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import org.creativecommons.thelist.R;
import org.creativecommons.thelist.adapters.MainListItem;
import org.creativecommons.thelist.utils.ApiConstants;
import org.creativecommons.thelist.utils.ListApplication;
import org.creativecommons.thelist.utils.ListUser;
import org.creativecommons.thelist.utils.MessageHelper;
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
    RequestMethods mRequestMethods;
    SharedPreferencesMethods mSharedPref;
    MessageHelper mMessageHelper;
    ListUser mCurrentUser;

    //Handle Data
    protected JSONArray mRandomItemData;
    protected JSONObject mListItemData;
    String mMakerName;
    String mItemName;
    String mItemID;

    private List<MainListItem> mItemList;

    int itemPositionCount = 0;

    //UI Elements
    TextView mTextView;
    ProgressBar mProgressBar;
    Button mDoneButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random);
        mContext = this;
        mRequestMethods = new RequestMethods(mContext);
        mSharedPref = new SharedPreferencesMethods(mContext);
        mMessageHelper = new MessageHelper(mContext);
        mCurrentUser = new ListUser(RandomActivity.this);

        //Google Analytics Tracker
        ((ListApplication) getApplication()).getTracker(ListApplication.TrackerName.GLOBAL_TRACKER);

        //UI Elements
        mItemList = new ArrayList<>();
        mTextView = (TextView) findViewById(R.id.account_text);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mDoneButton = (Button) findViewById(R.id.doneButton);

        ImageButton yesButton = (ImageButton) findViewById(R.id.YesButton);
        ImageButton noButton = (ImageButton) findViewById(R.id.NoButton);
        //TODO: add camera functionality?
        //ImageButton CameraButton = (ImageButton) findViewById(R.id.CameraButton);

        mRequestMethods.getRandomItems(new RequestMethods.ResponseCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                Log.v(TAG, "> getRandomListItems > onSuccess: " + response);
                mRandomItemData = response;
                updateView();
            }

            @Override
            public void onFail(VolleyError error) {
                Log.d(TAG, "> getRandomListItems > onFail: " + error.getMessage());
                mMessageHelper.noItemsFound();
                //TODO: Take user elsewhere if items don’t load
            }
        });

        //Add list item to my list
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
                //TODO: add this to addItemToUserList callback
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
                if (!(mCurrentUser.isTempUser())) {
                    Log.v(TAG, "> isTempUser, user is logged in");
                    mCurrentUser.addItemToUserList(mItemID);
                }
                //Display a new item
                updateView();
                //show doneButton if user has selected at least 3 items
                if (mItemList.size() == 3) { //once it has 3 items
                    mDoneButton.setVisibility(View.VISIBLE);
                }
            } //OnClick
        }); //YesButton.setOnClickListener

        //Decline adding list item to my list
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateView();
                if(itemPositionCount >= 7 && mDoneButton.getVisibility() == View.INVISIBLE){
                    mDoneButton.setVisibility(View.VISIBLE);
                }
            }
        });

        //I’m done with picking items
        mDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get array of selected item IDS
                if(mCurrentUser.isTempUser()){
                    saveTempUserItems();
                }

                //Clear ItemList
                mItemList.clear();

                Intent intent = new Intent(mContext, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }); //Done Button
    } //onCreate

    @Override
    protected void onStart(){
        super.onStart();
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    protected void onStop(){
        super.onStop();
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }

    private void updateView() {
        mProgressBar.setVisibility(View.INVISIBLE);
        if(mRandomItemData == null) {
            //TODO: better error message
            mMessageHelper.showDialog(mContext,"Oops", "No data found. Please try again.");
        }
        else {
            try {
                if(itemPositionCount == mRandomItemData.length()) {
                    //If you run out of items, just go to MainActivity
                    if(mCurrentUser.isTempUser()){
                        saveTempUserItems();
                    }

                    //Clear ItemList
                    mItemList.clear();

                    Intent intent = new Intent(RandomActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                } else {
                    mListItemData = mRandomItemData.getJSONObject(itemPositionCount);
                    mItemID = mListItemData.getString(ApiConstants.ITEM_ID);
                    mItemName = mListItemData.getString(ApiConstants.ITEM_NAME).toLowerCase();
                    mMakerName = mListItemData.getString(ApiConstants.MAKER_NAME);

                    //Update UI
                    mTextView.setText(mMakerName + " needs a picture of " + mItemName);

                    itemPositionCount++;
                }
            } catch (JSONException e) {
                Log.e(TAG,e.getMessage());
            }
        }
    } //updateView

    public void saveTempUserItems(){
        List<String> userItemList = getItemIds(mItemList);

        JSONArray oldItemArray = mSharedPref.getUserItemPreference();
        if(oldItemArray != null) {
            for (int i = 0; i < oldItemArray.length(); i++) {
                try {
                    userItemList.add(0, oldItemArray.getString(i));
                } catch (JSONException e) {
                    Log.v(TAG, e.getMessage());
                }
            }
        }
        Log.v("LIST OF ALL ITEMS ADDED", userItemList.toString());
        //Save Array as String to sharedPreferences
        mSharedPref.saveSharedPreference
                (SharedPreferencesMethods.LIST_ITEM_PREFERENCE_KEY,
                        userItemList.toString());

        String sharedPref = mSharedPref.getSharedPreferenceList
                (SharedPreferencesMethods.LIST_ITEM_PREFERENCE_KEY).toString();
        Log.v("ALL ITEMS IN USER PREF", sharedPref);
    }

//    ------------------------------------------------------------------------------------------
//    ------------------------------------------------------------------------------------------

    //Parse List Objects of List Items and return list of Item IDS
    public List<String> getItemIds(List<MainListItem> list){
        List<String>arrayList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            String singleID = list.get(i).getItemID();
            arrayList.add(singleID);
        }
        return arrayList;
    } //getItemIds

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
} //RandomActivity
