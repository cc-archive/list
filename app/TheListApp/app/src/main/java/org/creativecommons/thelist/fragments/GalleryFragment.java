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

package org.creativecommons.thelist.fragments;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;

import org.creativecommons.thelist.R;
import org.creativecommons.thelist.adapters.GalleryAdapter;
import org.creativecommons.thelist.adapters.GalleryItem;
import org.creativecommons.thelist.authentication.AccountGeneral;
import org.creativecommons.thelist.utils.ApiConstants;
import org.creativecommons.thelist.utils.ListUser;
import org.creativecommons.thelist.utils.MessageHelper;
import org.creativecommons.thelist.utils.NetworkUtils;
import org.creativecommons.thelist.utils.RecyclerItemClickListener;
import org.creativecommons.thelist.utils.RequestMethods;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

public class GalleryFragment extends Fragment {
    public static final String TAG = GalleryFragment.class.getSimpleName();

    private Context mContext;

    private ListUser mCurrentUser;
    private MessageHelper mMessageHelper;
    private RequestMethods mRequestMethods;

    //UI Elements
    private ProgressBar mProgressBar;
    private TextView mEmptyView;
    private Button mLoginButton;

    //RecyclerView
    private RecyclerView mRecyclerView;
    private GalleryAdapter mGalleryAdapter;
    private GridLayoutManager mGridLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ArrayList<GalleryItem> mPhotoList = new ArrayList<>();

    //Interface with Activity + ListUser
    public GalleryListener mCallback;

    // --------------------------------------------------------

    //LISTENERS
    public interface GalleryListener {
        void viewImage(ArrayList<GalleryItem> photoObjects, int position);
        void onLoginClick();
    }

    public GalleryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (GalleryListener) activity;
        } catch(ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + activity.getString(R.string.gallery_callback_exception_message));
        }
    } //onAttach

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gallery, container, false);
    } //onCreateView

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mContext = getActivity();
        Activity activity = getActivity();

        //Helpers
        mCurrentUser = new ListUser(mContext);
        mMessageHelper = new MessageHelper(mContext);
        mRequestMethods = new RequestMethods(mContext);

        //UI Elements
        mProgressBar = (ProgressBar) activity.findViewById(R.id.gallery_progressBar);
        mEmptyView = (TextView) activity.findViewById(R.id.empty_gallery_label);
        mLoginButton = (Button) activity.findViewById(R.id.gallery_login_button);

        //RecyclerView
        mSwipeRefreshLayout = (SwipeRefreshLayout)activity.findViewById(R.id.gallerySwipeRefresh);
        mRecyclerView = (RecyclerView)activity.findViewById(R.id.galleryRecyclerView);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.hasFixedSize();
        mGridLayoutManager = new GridLayoutManager(activity, 3);
        mGalleryAdapter = new GalleryAdapter(mContext, mPhotoList);
        mRecyclerView.setAdapter(mGalleryAdapter);
        mRecyclerView.setLayoutManager(mGridLayoutManager);

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(),
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        if(!mRequestMethods.isNetworkAvailable()){
                            mMessageHelper.toastNeedInternet();
                            return;
                        }

                        mCallback.viewImage(mPhotoList, position);

                    }
                }));

        //Show user Photos
        refreshItems();

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                mCurrentUser.addNewFullAccount(AccountGeneral.ACCOUNT_TYPE,
                        AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, new ListUser.AuthCallback() { //addNewFullAccount
                            @Override
                            public void onAuthed(String authtoken) {
                                Log.v(TAG, "> addNewFullAccount > onAuthed, authtoken: " + authtoken);

                            }

                        });

                refreshItems();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onLoginClick();
            }
        });

    } //onActivityCreated

    @Override
    public void onResume() {
        super.onResume();
    } //onResume

    public void refreshItems(){

        if(mCurrentUser.isAnonymousUser()){

            mLoginButton.setVisibility(View.VISIBLE);

            mProgressBar.setVisibility(View.INVISIBLE);
            mEmptyView.setText(mContext.getString(R.string.empty_gallery_label_temp));
            mEmptyView.setVisibility(View.VISIBLE);

            return;
        }

        mLoginButton.setVisibility(View.GONE);

        mRequestMethods.getUserPhotos(new NetworkUtils.ResponseCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                Log.v(TAG, " > getUserPhotos > onAuthed" + response.toString());
                mPhotoList.clear();

                for(int i = 0; i < response.length(); i++){
                    GalleryItem galleryItem = new GalleryItem();

                    try {
                        JSONObject singlePhotoItem = response.getJSONObject(i);
                        String photoUrl = singlePhotoItem.getString(ApiConstants.USER_PHOTO_URL);
                        String itemName = singlePhotoItem.getString(ApiConstants.USER_PHOTO_TITLE);
                        String makerName = singlePhotoItem.getString(ApiConstants.USER_PHOTO_MAKER); //TODO: set this in ApiConstants when it exists

                        galleryItem.setItemName(itemName);
                        galleryItem.setMakerName("Creative Commons");
                        galleryItem.setUrl(photoUrl);

                        mPhotoList.add(galleryItem);
                    } catch (JSONException e) {
                        Log.v(TAG, e.getMessage());
                    }
                }

                Log.v(TAG, "PHOTOLIST RESPONSE " + mPhotoList);

                mProgressBar.setVisibility(View.INVISIBLE);

                if(mPhotoList.size() == 0){
                    //TODO: show textView
                    mEmptyView.setText(R.string.empty_gallery_label_logged_in);
                    mEmptyView.setVisibility(View.VISIBLE);
                    Log.v(TAG, "VIEW IS EMPTY");
                } else {
                    //TODO: hide textView
                    mEmptyView.setVisibility(View.GONE);
                    Log.v(TAG, "VIEW HAS PHOTO ITEMS");
                    Collections.reverse(mPhotoList);
                    mGalleryAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFail(VolleyError error) {
                Log.d(TAG, "> getUserPhotos > onFail: " + error.toString());
                mProgressBar.setVisibility(View.INVISIBLE);
                mEmptyView.setText("Problem loading your photos.\nTry reloading the page!");
                mEmptyView.setVisibility(View.VISIBLE);
            }
        });

    } //refreshItems

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
        mPhotoList.clear();
    }

} //GalleryFragment
