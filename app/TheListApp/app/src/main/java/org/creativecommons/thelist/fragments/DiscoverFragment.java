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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.creativecommons.thelist.R;
import org.creativecommons.thelist.adapters.DiscoverAdapter;
import org.creativecommons.thelist.api.ListApi;
import org.creativecommons.thelist.api.ListService;
import org.creativecommons.thelist.models.Photo;
import org.creativecommons.thelist.utils.MessageHelper;
import org.creativecommons.thelist.utils.RecyclerViewUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class DiscoverFragment extends android.support.v4.app.Fragment implements RecyclerViewUtils.cardSelectionListener  {
    public static final String TAG = DiscoverFragment.class.getSimpleName();

    private Context mContext;
    private Activity mActivity;
    private MessageHelper mMessageHelper;

    //API
    private ListApi api;
    private ListService list;

    //RecyclerView
    private RecyclerView mDiscoverRecyclerView;
    private RecyclerView.Adapter mDiscoverAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerViewUtils.cardSelectionListener mCardSelectionListener;

    private List<Photo> mPhotoList = new ArrayList<>();

    // --------------------------------------------------------


    public DiscoverFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_discover, container, false);


        mContext = getActivity();
        mActivity = getActivity();

        //API
        api = new ListApi();
        list = api.getService();

        //RecyclerView
        mDiscoverRecyclerView = (RecyclerView)view.findViewById(R.id.discoverRecyclerView);
        mCardSelectionListener = this;
        mLayoutManager = new LinearLayoutManager(mContext);
        mDiscoverRecyclerView.setLayoutManager(mLayoutManager);
        mDiscoverRecyclerView.setHasFixedSize(true);

        mDiscoverAdapter = new DiscoverAdapter(mActivity, mPhotoList, mCardSelectionListener);
        mDiscoverRecyclerView.setAdapter(mDiscoverAdapter);

        return view;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //TODO: check for network connection

        //Request items for Discover feed
        list.getPhotoFeed(new Callback<List<Photo>>() {
            @Override
            public void success(List<Photo> photos, Response response) {
                Log.d(TAG, "getPhotoFeed > success: " + response.toString());

                mPhotoList = photos;
                mDiscoverAdapter.notifyDataSetChanged();

            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "getPhotoFeed > failure: " + error.getMessage());

                //TODO: show error message
                mMessageHelper.networkFailMessage();
            }
        });
    }

    @Override
    public void onFlag(String photoID) {
        //TODO: item flagged
    }

    @Override
    public void onLike(String photoID) {
        //TODO: on item liked

    }

    @Override
    public void onBookmark(String photoID) {
        //TODO: on item bookmarked

    }

    @Override
    public void onContribute(String itemID) {
        //TODO: on item add to contribute
    }


} //DiscoverFragment
