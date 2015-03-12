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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.android.volley.VolleyError;
import com.google.android.gms.analytics.GoogleAnalytics;

import org.creativecommons.thelist.R;
import org.creativecommons.thelist.adapters.CategoryListAdapter;
import org.creativecommons.thelist.adapters.CategoryListItem;
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


public class CategoryListActivity extends ActionBarActivity {
    public static final String TAG = CategoryListActivity.class.getSimpleName();
    protected Context mContext;

    //Helper Methods
    RequestMethods mRequestMethods;
    SharedPreferencesMethods mSharedPref;
    MessageHelper mMessageHelper;
    ListUser mCurrentUser;

    //GET Request
    protected JSONArray mCategoryData;
    protected List<Integer> mUserCategories = new ArrayList<>();

    //RecyclerView
//    private RecyclerView mRecyclerView;
//    private RecyclerView.Adapter mCategoryAdapter;
//    private RecyclerView.LayoutManager mLayoutManager;
//    private List<CategoryListItem> mCategoryList = new ArrayList<>();

    //GridView
    protected GridView mGridView;
    private List<CategoryListItem> mCategoryList = new ArrayList<>();
    protected CategoryListAdapter adapter;
    protected ImageView mCheckmarkView;

    //UI Elements
    protected ProgressBar mProgressBar;
    protected Button mNextButton;

    // --------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);
        mContext = this;
        mMessageHelper = new MessageHelper(mContext);
        mRequestMethods = new RequestMethods(mContext);
        mSharedPref = new SharedPreferencesMethods(mContext);
        mCurrentUser = new ListUser(CategoryListActivity.this);

        //Google Analytics Tracker
        ((ListApplication) getApplication()).getTracker(ListApplication.TrackerName.GLOBAL_TRACKER);

        //Load UI Elements
        mNextButton = (Button) findViewById(R.id.nextButton);
        mNextButton.setVisibility(View.GONE);

        //Set List Adapter
        mGridView = (GridView) findViewById(R.id.categoryGrid);
        adapter = new CategoryListAdapter(this,mCategoryList);

        //Get Categories
        mRequestMethods.getCategories(new RequestMethods.ResponseCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                Log.v(TAG, "> getCategories > onSuccess: " + response);
                mCategoryData = response;

                //Get user’s pre-selected categories
                if(!(mCurrentUser.isTempUser())){
                    //If user is logged in, request any pre-selected categories
                    mRequestMethods.getUserCategories(new RequestMethods.ResponseCallback() {
                        @Override
                        public void onSuccess(JSONArray response) {
                            Log.v(TAG, "> getUserCategories > onSuccess " + response.toString());

                            //Create list of category ids
                            if(response.length() > 0) {

                                //Get array of catIds
                                for(int i = 0; i < response.length(); i++){
                                    try {
                                        JSONObject singleCat = response.getJSONObject(i);
                                        mUserCategories.add(i, singleCat.getInt("categoryid"));
                                        Log.v("USERCATS", "add: " + singleCat.getInt("categoryid"));
                                    } catch (JSONException e) {
                                        Log.e(TAG, e.getMessage());
                                    }
                                }
                                Log.v(TAG, "user’s category list: " + mUserCategories.toString());
                            }
                            updateList();
                        } //onSuccess
                        @Override
                        public void onFail(VolleyError error) {
                            Log.v(TAG, "> getUserCategories > onFail " + error.toString());
                        }
                    });
                } else {

                    JSONArray tempUserCategories = mSharedPref.getCategorySharedPreference();

                    if(tempUserCategories != null){

                        //Convert to list + add to mUserCategories
                        for(int i = 0; i > tempUserCategories.length(); i++){
                            try {
                                mUserCategories.add(i, tempUserCategories.getInt(i));
                                Log.v(TAG, " TEMPUSER CATEGORIES: " + mUserCategories.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    updateList();
                }

            } //onSuccess
            @Override
            public void onFail(VolleyError error) {
                Log.d(TAG, "> getCategories > onFail: " + error.getMessage());
                mMessageHelper.showDialog(mContext, getString(R.string.error_title),
                        getString(R.string.error_message));
            }
        });


        //When Category is tapped
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageView checkmarkView = (ImageView)view.findViewById(R.id.checkmark);
                //Get item clicked + its category id
                CategoryListItem item = (CategoryListItem) mGridView.getItemAtPosition(position);
                String catId = String.valueOf(item.getCategoryID());

                if(mGridView.isItemChecked(position)) {
                    checkmarkView.setVisibility(View.VISIBLE);
                    item.setCategoryChecked(true);
                    mRequestMethods.addCategory(catId);
                    Log.v(TAG, "ADDED " + catId);
                } else {
                    checkmarkView.setVisibility(View.GONE);
                    item.setCategoryChecked(false);
                    mRequestMethods.removeCategory(catId);
                    Log.v(TAG, "REMOVED " + catId);
                }
                //Count how many items are checked: if at least 3, show Next Button
                SparseBooleanArray positions = mGridView.getCheckedItemPositions();
                int length = positions.size();
                int ItemsChecked = 0;
                if (positions.size() > 0) {
                    for (int i = 0; i < length; i++) {
                        if (positions.get(positions.keyAt(i))) {
                            ItemsChecked++;
                        }
                    }
                }
                if (ItemsChecked >= 3) {
                    mNextButton.setVisibility(View.VISIBLE);
                }
                else {
                    mNextButton.setVisibility(View.GONE);
                }
            }
        }); //setOnItemClickListener

        //Next Button: handle User’s Category Preferences
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SparseBooleanArray positions = mGridView.getCheckedItemPositions();
                int length = positions.size();
                //Array of user selected categories
                List<Integer> userCategories = new ArrayList<>();
                //Boolean TempUser = mCurrentUser.isTempUser();

                for(int i = 0; i < length; i++) {
                    int itemPosition = positions.keyAt(i);
                    CategoryListItem item = (CategoryListItem) mGridView.getItemAtPosition(itemPosition);
                    int id = item.getCategoryID();
                    userCategories.add(id);
                }

                if(mCurrentUser.isTempUser()){ //TEMP USER
                    //Save user categories to shared preferences
                    mSharedPref.saveSharedPreference
                            (SharedPreferencesMethods.CATEGORY_PREFERENCE_KEY, userCategories.toString());
                }

                //Navigate to Random Activity if temp has low item count
                if(mCurrentUser.isTempUser() && mSharedPref.getUserItemCount() < 3){
                    Intent intent = new Intent(CategoryListActivity.this, RandomActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(CategoryListActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });
    } //onCreate

    @Override
    public void onStart(){
        super.onStart();
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    public void onStop(){
        super.onStop();
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
        private OnItemClickListener mListener;
        GestureDetector mGestureDetector;

        public RecyclerItemClickListener(Context context, OnItemClickListener listener) {
            mListener = listener;
            mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    //Log.v("HI", "ON SINGLE TAG UP CALLED");
                    return true;
                }
            });
        }
        @Override
        public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
            View childView = view.findChildViewUnder(e.getX(), e.getY());
            if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
                mListener.onItemClick(childView, view.getChildPosition(childView));
            }
            return false;
        }
        @Override
        public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {
            //Log.v("HI", "ON TOUCH EVENT CALLED");
        }
    } //RecyclerItemClickListener


    //UPDATE LIST WITH CONTENT
    private void updateList() {
        Log.v(TAG, "> updateList");
        //mProgressBar.setVisibility(View.INVISIBLE);
        if (mCategoryData == null) {
            mMessageHelper.showDialog(mContext, getString(R.string.error_title),
                    getString(R.string.error_message));
        }
        else {
            try {
                for(int i = 0; i < mCategoryData.length(); i++) {
                    JSONObject jsonSingleCategory = mCategoryData.getJSONObject(i);
                    CategoryListItem categoryListItem = new CategoryListItem();
                    categoryListItem.setCategoryName(jsonSingleCategory.getString(ApiConstants.CATEGORY_NAME));
                    categoryListItem.setCategoryID(jsonSingleCategory.getInt(ApiConstants.CATEGORY_ID));
                    categoryListItem.setCategoryColour("#" + jsonSingleCategory.getString(ApiConstants.CATEGORY_COLOUR));
                    Log.v(TAG, "mCategoryData to add to list: " + categoryListItem.getCategoryColour());
                    Log.v(TAG, "THIS IS USER CATEGORIES IN LOOP: " + mUserCategories.toString());

                    //Add to array list to be adapted
                    mCategoryList.add(categoryListItem);
                    Log.v(TAG, "ITEM ADDED TO CATEGORY LIST");

                }
            } catch (JSONException e) {
                Log.e(TAG, "Exception Caught: ", e);
            }
            mGridView.setAdapter(adapter);

            //if category has been previously selected by user, set item in gridview as checked
            for(int i = 0; i < mCategoryList.size(); i++){
                CategoryListItem checkItem = mCategoryList.get(i);
                if(mUserCategories.contains(checkItem.getCategoryID())){
                    checkItem.setCategoryChecked(true);
                    mGridView.setItemChecked(i, true);
                }
            }
            adapter.notifyDataSetChanged();
        }
    } //updateList

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_category_list, menu);
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
}