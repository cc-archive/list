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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.android.volley.VolleyError;
import com.google.android.gms.analytics.GoogleAnalytics;

import org.creativecommons.thelist.R;
import org.creativecommons.thelist.adapters.CategoryAdapter;
import org.creativecommons.thelist.adapters.CategoryListItem;
import org.creativecommons.thelist.utils.ApiConstants;
import org.creativecommons.thelist.utils.ListApplication;
import org.creativecommons.thelist.utils.ListUser;
import org.creativecommons.thelist.utils.MessageHelper;
import org.creativecommons.thelist.utils.NetworkUtils;
import org.creativecommons.thelist.utils.RequestMethods;
import org.creativecommons.thelist.utils.SharedPreferencesMethods;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class CategoryListActivity extends AppCompatActivity {
    public static final String TAG = CategoryListActivity.class.getSimpleName();

    private Context mContext;

    //Helpers
    private ListUser mCurrentUser;
    private MessageHelper mMessageHelper;
    private RequestMethods mRequestMethods;
    private SharedPreferencesMethods mSharedPref;

    //GET Request
    private JSONArray mCategoryData;
    private List<Integer> mUserCategories = new ArrayList<>();

    //RecyclerView
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mCategoryAdapter;
    private GridLayoutManager mGridLayoutManager;
    private List<CategoryListItem> mCategoryList = new ArrayList<>();

    //UI Elements
    private ProgressBar mProgressBar;
    private Menu menu;

    // --------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);

        mContext = this;

        mCurrentUser = new ListUser(CategoryListActivity.this);
        mMessageHelper = new MessageHelper(mContext);
        mRequestMethods = new RequestMethods(mContext);
        mSharedPref = new SharedPreferencesMethods(mContext);

        //Google Analytics Tracker
        ((ListApplication) getApplication()).getTracker(ListApplication.TrackerName.GLOBAL_TRACKER);

        //RecyclerView
        mRecyclerView = (RecyclerView) findViewById(R.id.category_list_grid);
        mGridLayoutManager = new GridLayoutManager(mContext, 2);
        mCategoryAdapter = new CategoryAdapter(this, mCategoryList);
        mRecyclerView.setAdapter(mCategoryAdapter);
        mRecyclerView.setLayoutManager(mGridLayoutManager);

        //UI Elements
        mProgressBar = (ProgressBar) findViewById(R.id.category_progress_bar);
        mProgressBar.setVisibility(View.VISIBLE);


        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(false);
        }

        //Set up Helper Message if new user
        if(!mSharedPref.getCategoryHelperViewed()){

            final View helperMessage = findViewById(R.id.category_helper_message);
            ImageButton helperCloseButton = (ImageButton) findViewById(R.id.helper_close_button);

            helperCloseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    helperMessage.setVisibility(View.GONE);
                    mSharedPref.setCategoryHelperViewed(true);
                }
            });

            helperMessage.setVisibility(View.VISIBLE);
        }

        //Get Categories
        mRequestMethods.getCategories(new NetworkUtils.ResponseCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                Log.v(TAG, "> getCategories > onSuccess: " + response);
                mCategoryData = response;

                mRequestMethods.getUserCategories(new NetworkUtils.ResponseCallback() {
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
                                    //Log.v(TAG, "pre-existing catgory added: " + singleCat.getInt("categoryid"));

                                } catch (JSONException e) {
                                    Log.e(TAG, e.getMessage());
                                }
                            }
                            Log.v(TAG, "user’s category list: " + mUserCategories.toString());
                        }
                        updateList();
                    } //getUserCategories > onSuccess
                    @Override
                    public void onFail(VolleyError error) { //could not get user’s categories
                        Log.v(TAG, "> getUserCategories > onFail " + error.toString());
                        mMessageHelper.showDialog(mContext, getString(R.string.error_title),
                                getString(R.string.error_message));
                    }
                });
            } //getCategories > onSuccess
            @Override
            public void onFail(VolleyError error) {
                Log.d(TAG, "> getCategories > onFail: " + error.getMessage());
                mMessageHelper.showDialog(mContext, getString(R.string.error_title),
                        getString(R.string.error_message));
            }
        });
    } //onCreate


    //UPDATE LIST WITH CONTENT
    private void updateList() {
        Log.v(TAG, "> updateList");

        mProgressBar.setVisibility(View.GONE);

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

                    //Add to array list to be adapted
                    mCategoryList.add(categoryListItem);
                }
            } catch (JSONException e) {
                Log.e(TAG, "Exception Caught: ", e);
            }

            //if category has been previously selected by user, set item in gridview as checked
            if(mUserCategories != null && mUserCategories.size() > 0) {
                for(int i = 0; i < mCategoryList.size(); i++){
                    CategoryListItem checkItem = mCategoryList.get(i);
                    if(mUserCategories.contains(checkItem.getCategoryID())){

                        ((CategoryAdapter) mCategoryAdapter).setState(i, true);
                        Log.v(TAG, checkItem.getCategoryName() + "IS CHECKED");
                        //Log.v(TAG, checkItem.getCategoryName() + " is true");
                    }
                }
            }

            mCategoryAdapter.notifyDataSetChanged();

            MenuItem doneButton = menu.findItem(R.id.action_done);
            doneButton.setVisible(true);
        }
    } //updateList

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_category_list, menu);
        this.menu = menu;
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

            Intent intent;

            if(mCurrentUser.isAnonymousUser()){

                //TODO: get user item count (if not zero, send to MainActivity) --> part of profile endpoint?
                intent = new Intent(CategoryListActivity.this, RandomActivity.class);
                startActivity(intent);

            } else {
                intent = new Intent(CategoryListActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

} //CategoryListActivity