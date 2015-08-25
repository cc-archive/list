package org.creativecommons.thelist.fragments;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.android.volley.VolleyError;

import org.creativecommons.thelist.R;
import org.creativecommons.thelist.adapters.CategoryAdapter;
import org.creativecommons.thelist.adapters.CategoryListItem;
import org.creativecommons.thelist.api.NetworkUtils;
import org.creativecommons.thelist.api.RequestMethods;
import org.creativecommons.thelist.utils.ApiConstants;
import org.creativecommons.thelist.utils.MessageHelper;
import org.creativecommons.thelist.utils.SharedPreferencesMethods;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class CategoryFragment extends Fragment {
    public static final String TAG = CategoryFragment.class.getSimpleName();

    private Context mContext;

    //Helpers
    private MessageHelper mMessageHelper;
    private RequestMethods mRequestMethods;
    private SharedPreferencesMethods mSharedPref;

    //GET Request
    private JSONArray mCategoryData;
    private List<Integer> mUserCategories = new ArrayList<>();

    //UI Elements
    @Bind(R.id.category_progressBar)ProgressBar mProgressBar;
    @Bind(R.id.categoryRecyclerView) RecyclerView mRecyclerView;

    private Menu menu;

    private RecyclerView.Adapter mCategoryAdapter;
    private LinearLayoutManager mLayoutManager;
    private List<CategoryListItem> mCategoryList = new ArrayList<>();


    // --------------------------------------------------------

    public CategoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        mContext = getActivity();
        Activity activity = getActivity();

        ButterKnife.bind(this, view);

        mMessageHelper = new MessageHelper(mContext);
        mRequestMethods = new RequestMethods(mContext);
        mSharedPref = new SharedPreferencesMethods(mContext);

        mLayoutManager = new LinearLayoutManager(mContext);
        mCategoryAdapter = new CategoryAdapter(activity, mCategoryList);
        mRecyclerView.setAdapter(mCategoryAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);

        getCategories();

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Set up Helper Message if new user
        if(mSharedPref.getCategoryHelperViewed()){

            final View helperMessage = getActivity().findViewById(R.id.category_helper_message);
            Button helperCloseButton = (Button) helperMessage.findViewById(R.id.helper_close_button);

            helperCloseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    helperMessage.setVisibility(View.GONE);
                    mSharedPref.setCategoryHelperViewed(true);
                }
            });

            helperMessage.setVisibility(View.VISIBLE);
        }
    } //onActivityCreated

    public void getCategories(){
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

    } //getCategories

    //Update RecyclerView
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

            //if category has been previously selected by user, set item in list as checked
            if(mUserCategories != null && mUserCategories.size() > 0) {
                for(int i = 0; i < mCategoryList.size(); i++){
                    CategoryListItem checkItem = mCategoryList.get(i);
                    if(mUserCategories.contains(checkItem.getCategoryID())){

                        ((CategoryAdapter) mCategoryAdapter).setState(i, true);

                    }
                }
            }

            mCategoryAdapter.notifyDataSetChanged();

        }
    } //updateList

} //CategoryFragment
