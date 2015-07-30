package org.creativecommons.thelist.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.creativecommons.thelist.R;
import org.creativecommons.thelist.adapters.MainPagerAdapter;


public class FeedFragment extends android.support.v4.app.Fragment {
    public static final String TAG = FeedFragment.class.getSimpleName();

    private Activity mActivity;

    //Tab Layout
    private TabLayout mTabLayout;

    public FeedFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_feed, container, false);

        mActivity = getActivity();

        final ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        setupViewPager(viewPager);

        mTabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        mTabLayout.setupWithViewPager(viewPager);


        //TODO: remove when bug is fixed
        if (ViewCompat.isLaidOut(mTabLayout)) {
            mTabLayout.setupWithViewPager(viewPager);
        } else {
            mTabLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                    mTabLayout.setupWithViewPager(viewPager);
                    mTabLayout.removeOnLayoutChangeListener(this);
                }
            });
        }

        return view;

    } //onCreateView

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public void setupViewPager(ViewPager viewPager){
        MainPagerAdapter mainPagerAdapter =
                new MainPagerAdapter(getChildFragmentManager());

        mainPagerAdapter.addFragment(new DiscoverFragment(), "Discover");
        mainPagerAdapter.addFragment(new MyListFragment(), "Contribute");
        viewPager.setAdapter(mainPagerAdapter);
    }
}
