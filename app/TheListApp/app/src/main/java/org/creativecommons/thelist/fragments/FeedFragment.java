package org.creativecommons.thelist.fragments;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.creativecommons.thelist.R;
import org.creativecommons.thelist.api.ListApi;
import org.creativecommons.thelist.api.ListService;
import org.creativecommons.thelist.models.Photo;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class FeedFragment extends android.support.v4.app.Fragment {
    public static final String TAG = FeedFragment.class.getSimpleName();

    private ListApi api;
    private ListService list;


    public FeedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feed, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        api = new ListApi();
        list = api.getService();

    }

    @Override
    public void onResume() {
        super.onResume();

        list.getPhotoFeed(new Callback<List<Photo>>() {
            @Override
            public void success(List<Photo> photos, Response response) {
                Log.d(TAG, "getPhotoFeed > success: " + response.toString());

                for (Photo photo : photos) {
                    Log.v(TAG, photo.url);
                }

            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "getPhotoFeed > failure: " + error.getMessage());
            }
        });
    }
}
