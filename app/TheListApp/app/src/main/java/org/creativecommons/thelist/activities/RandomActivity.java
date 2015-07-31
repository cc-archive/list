/* The List powered by Creative Commons

   Copyright (C) 2014, 2015 Creative Commons Corporation

   This program is free software: you can redistribute it and/or modify
   it under the terms of either the GNU Affero General Public License or
   the GNU General Public License as published by the
   Free Software Foundation, either version 3 of the Licenses, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  

   You should have received a copy of the GNU General Public License and
   the GNU Affero General Public License along with this program.  

   If not, see <http://www.gnu.org/licenses/>.


*/

package org.creativecommons.thelist.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.gms.analytics.GoogleAnalytics;

import org.creativecommons.thelist.R;
import org.creativecommons.thelist.layouts.AutoResizeTextView;
import org.creativecommons.thelist.utils.ApiConstants;
import org.creativecommons.thelist.utils.ListApplication;
import org.creativecommons.thelist.utils.MessageHelper;
import org.creativecommons.thelist.utils.NetworkUtils;
import org.creativecommons.thelist.utils.RequestMethods;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RandomActivity extends Activity {
    public static final String TAG = RandomActivity.class.getSimpleName();

    private Context mContext;

    //Helper Methods
    private MessageHelper mMessageHelper;
    private RequestMethods mRequestMethods;

    //Handle Data
    private JSONArray mRandomItemData;
    private String mItemID;

    int itemPositionCount = 0;

    //UI Elements
    private AutoResizeTextView mTextView;
    private ProgressBar mProgressBar;
    private Button mDoneButton;
    private ImageButton mYesButton;
    private ImageButton mNoButton;
    private RelativeLayout mBackground;

    // --------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random);

        mContext = this;

        mMessageHelper = new MessageHelper(mContext);
        mRequestMethods = new RequestMethods(mContext);

        //Google Analytics Tracker
        ((ListApplication) getApplication()).getTracker(ListApplication.TrackerName.GLOBAL_TRACKER);

        //UI Elements
        mBackground = (RelativeLayout) findViewById(R.id.random_item_background);
        mTextView = (AutoResizeTextView) findViewById(R.id.random_item_text);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mDoneButton = (Button) findViewById(R.id.doneButton);

        mYesButton = (ImageButton) findViewById(R.id.YesButton);
        mNoButton = (ImageButton) findViewById(R.id.NoButton);
        //ImageButton CameraButton = (ImageButton) findViewById(R.id.CameraButton);

        mRequestMethods.getRandomItems(new NetworkUtils.ResponseCallback() {
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
                mProgressBar.setVisibility(View.INVISIBLE);
                //TODO: Take user elsewhere if items don’t load?
                //Dialog with option to go to main screen or retry?

            }
        });

        //Add list item to my list
        mYesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

                mRequestMethods.addItemToUserList(mItemID);

                //Display a new item
                updateView();
                //show doneButton if user has selected at least x items
                if (itemPositionCount >= 1) {
                    mDoneButton.setVisibility(View.VISIBLE);
                }
            } //OnClick
        }); //YesButton.setOnClickListener

        //Decline adding list item to my list
        mNoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateView();
                if(itemPositionCount >= 1 && mDoneButton.getVisibility() == View.INVISIBLE){
                    mDoneButton.setVisibility(View.VISIBLE);
                }
            }
        });

        //I’m done with picking items
        mDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

                //TODO: if coming from CategoryListActivity, go to MainActivity.class
//                Intent intent = new Intent(mContext, MainActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent);

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

                    Intent intent = new Intent(RandomActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                } else {

                    JSONObject listItemData = mRandomItemData.getJSONObject(itemPositionCount);

                    mItemID = listItemData.getString(ApiConstants.ITEM_ID);

                    String itemName = listItemData.getString(ApiConstants.ITEM_NAME);
                    String makerName = listItemData.getString(ApiConstants.MAKER_NAME);
                    String categoryID = listItemData.getString(ApiConstants.ITEM_CATEGORY);

                    //Update UI
                    switch(Integer.valueOf(categoryID)){
                        case ApiConstants.PEOPLE:

                            setButtonTheme(mYesButton, R.drawable.check_default_orange,
                                    R.drawable.check_pressed_orange, R.drawable.check_pressed_orange);
                            setButtonTheme(mNoButton, R.drawable.x_default_orange,
                                    R.drawable.x_pressed_orange, R.drawable.x_pressed_orange);

                            mTextView.setTextColor(getResources().getColor(R.color.people_100));
                            mDoneButton.setTextColor(getResources().getColor(R.color.people_100));
                            mBackground.setBackgroundColor(getResources().getColor(R.color.people_500));
                            break;

                        case ApiConstants.PLACES:
                            setButtonTheme(mYesButton, R.drawable.check_default_red,
                                    R.drawable.check_pressed_red, R.drawable.check_pressed_red);
                            setButtonTheme(mNoButton, R.drawable.x_default_red,
                                    R.drawable.x_pressed_red, R.drawable.x_pressed_red);

                            mTextView.setTextColor(getResources().getColor(R.color.places_100));
                            mDoneButton.setTextColor(getResources().getColor(R.color.places_100));
                            mBackground.setBackgroundColor(getResources().getColor(R.color.places_500));
                            break;

                        case ApiConstants.CLOTHING:
                            setButtonTheme(mYesButton, R.drawable.check_default_indigo,
                                    R.drawable.check_pressed_indigo, R.drawable.check_pressed_indigo);
                            setButtonTheme(mNoButton, R.drawable.x_default_indigo,
                                    R.drawable.x_pressed_indigo, R.drawable.x_pressed_indigo);

                            mTextView.setTextColor(getResources().getColor(R.color.clothing_100));
                            mDoneButton.setTextColor(getResources().getColor(R.color.clothing_100));
                            mBackground.setBackgroundColor(getResources().getColor(R.color.clothing_500));
                            break;

                        case ApiConstants.NATURE:
                            setButtonTheme(mYesButton, R.drawable.check_default_green,
                                    R.drawable.check_pressed_green, R.drawable.check_pressed_green);
                            setButtonTheme(mNoButton, R.drawable.x_default_green,
                                    R.drawable.x_pressed_green, R.drawable.x_pressed_green);

                            mTextView.setTextColor(getResources().getColor(R.color.nature_100));
                            mDoneButton.setTextColor(getResources().getColor(R.color.nature_100));
                            mBackground.setBackgroundColor(getResources().getColor(R.color.nature_500));
                            break;

                        case ApiConstants.FOOD:
                            setButtonTheme(mYesButton, R.drawable.check_default_blue,
                                    R.drawable.check_pressed_blue, R.drawable.check_pressed_blue);
                            setButtonTheme(mNoButton, R.drawable.x_default_blue,
                                    R.drawable.x_pressed_blue, R.drawable.x_pressed_blue);

                            mTextView.setTextColor(getResources().getColor(R.color.food_100));
                            mDoneButton.setTextColor(getResources().getColor(R.color.food_100));
                            mBackground.setBackgroundColor(getResources().getColor(R.color.food_500));
                            break;

                        case ApiConstants.OBJECTS:
                            setButtonTheme(mYesButton, R.drawable.check_default_pink,
                                    R.drawable.check_pressed_pink, R.drawable.check_pressed_pink);
                            setButtonTheme(mNoButton, R.drawable.x_default_pink,
                                    R.drawable.x_pressed_pink, R.drawable.x_pressed_pink);

                            mTextView.setTextColor(getResources().getColor(R.color.objects_100));
                            mDoneButton.setTextColor(getResources().getColor(R.color.objects_100));
                            mBackground.setBackgroundColor(getResources().getColor(R.color.objects_500));
                            break;
                        default:
                            setButtonTheme(mYesButton, R.drawable.check_default_default,
                                    R.drawable.check_pressed_default, R.drawable.check_pressed_default);
                            setButtonTheme(mNoButton, R.drawable.x_default_default,
                                    R.drawable.x_pressed_default, R.drawable.x_pressed_default);

                            mTextView.setTextColor(getResources().getColor(R.color.default_100));
                            mDoneButton.setTextColor(getResources().getColor(R.color.default_100));
                            mBackground.setBackgroundColor(getResources().getColor(R.color.default_500));
                            break;
                    }

                    mTextView.setText(makerName + " needs a picture of " + itemName);
                    mYesButton.setVisibility(View.VISIBLE);
                    mNoButton.setVisibility(View.VISIBLE);

                    itemPositionCount++;
                }
            } catch (JSONException e) {
                Log.e(TAG,e.getMessage());
            }
        }
    } //updateView

    public void setButtonTheme(ImageButton button, int normalState, int pressedState, int focusedState){
        StateListDrawable states = new StateListDrawable();
        states.addState(new int[] {android.R.attr.state_pressed},
                getResources().getDrawable(pressedState));
        states.addState(new int[] {android.R.attr.state_focused},
                getResources().getDrawable(focusedState));
        states.addState(new int[] { },
                getResources().getDrawable(normalState));
        button.setImageDrawable(states);
    } //setButtonTheme


//    ------------------------------------------------------------------------------------------
//    ------------------------------------------------------------------------------------------

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
