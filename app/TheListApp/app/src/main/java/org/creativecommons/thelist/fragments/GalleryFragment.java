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

package org.creativecommons.thelist.fragments;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;

import org.creativecommons.thelist.R;
import org.creativecommons.thelist.adapters.FeedAdapter;
import org.creativecommons.thelist.adapters.GalleryAdapter;
import org.creativecommons.thelist.adapters.GalleryItem;
import org.creativecommons.thelist.adapters.MainListItem;
import org.creativecommons.thelist.utils.ApiConstants;
import org.creativecommons.thelist.utils.MessageHelper;
import org.creativecommons.thelist.utils.RequestMethods;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GalleryFragment extends Fragment {
    public static final String TAG = GalleryFragment.class.getSimpleName();

    Context mContext;
    private MessageHelper mMessageHelper;
    private RequestMethods mRequestMethods;

    //UI Elements
    private TextView mEmptyView;
    private ProgressBar mProgressBar;

    //RecyclerView
    private RecyclerView mRecyclerView;
    private GalleryAdapter mGalleryAdapter;
    private GridLayoutManager mGridLayoutManager;
    private List<GalleryItem> mPhotoList = new ArrayList<>();

    //Interface with Activity + ListUser
    public GalleryListener mCallback;


    // --------------------------------------------------------

    //LISTENERS
    public interface GalleryListener {
        //TODO: add callbacks
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
    public void onResume() {
        super.onResume();
        mContext = getActivity();
        mMessageHelper = new MessageHelper(mContext);
        mRequestMethods = new RequestMethods(mContext);

        //UI Elements
        mEmptyView = (TextView)getView().findViewById(R.id.empty_gallery_label);
        mProgressBar = (ProgressBar)getView().findViewById(R.id.gallery_progressBar);

        //RecyclerView
        mRecyclerView = (RecyclerView)getView().findViewById(R.id.galleryRecyclerView);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mGridLayoutManager = new GridLayoutManager(getActivity(), 3);
        mGalleryAdapter = new GalleryAdapter(mContext, mPhotoList);
        mRecyclerView.setAdapter(mGalleryAdapter);
        mRecyclerView.setLayoutManager(mGridLayoutManager);

        //TODO: get gallery items
        mRequestMethods.getUserPhotos(new RequestMethods.ResponseCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                Log.v(TAG, " > getUserPhotos > onSuccess" + response.toString());

                mPhotoList.clear();
                mEmptyView.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.GONE);

                for(int i = 0; i < response.length(); i++){
                    GalleryItem galleryItem = new GalleryItem();

                    try {
                        JSONObject singlePhotoItem = response.getJSONObject(i);
                        galleryItem.setUrl(singlePhotoItem.getString(ApiConstants.USER_PHOTO_URL));

                    } catch (JSONException e) {
                        Log.v(TAG, e.getMessage());
                    }
                    mPhotoList.add(galleryItem);
                }

                if(mPhotoList.size() == 0){
                    //TODO: show textView
                    mEmptyView.setVisibility(View.VISIBLE);

                } else {
                    //TODO: hide textView
                    mEmptyView.setVisibility(View.GONE);
                    Collections.reverse(mPhotoList);
                    mGalleryAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onFail(VolleyError error) {
                Log.d(TAG, "> getUserPhotos > onFail: " + error.toString());
            }
        });
    } //onResume

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

} //GalleryFragment
