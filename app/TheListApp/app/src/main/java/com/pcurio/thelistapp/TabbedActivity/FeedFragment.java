package com.pcurio.thelistapp.TabbedActivity;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pcurio.thelistapp.R;

/**
 * Created by damaris on 2014-11-06.
 */
public class FeedFragment extends ListFragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_feed, container, false);
            return rootView;
        }
}
