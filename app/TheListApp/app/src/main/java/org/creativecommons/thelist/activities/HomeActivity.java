package org.creativecommons.thelist.activities;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import org.creativecommons.thelist.R;
import org.creativecommons.thelist.adapters.HomePagerAdapter;
import org.creativecommons.thelist.fragments.ContributeFragment;
import org.creativecommons.thelist.fragments.DiscoverFragment;

public class HomeActivity extends BaseActivity {
    public static final String TAG = HomeActivity.class.getSimpleName();

    private AppBarLayout mAppBarLayout;
    private TabLayout mTabLayout;
    //private MultiSwipeRefreshLayout mSwipeRefreshLayout;
    private ViewPager mViewPager;

    private DiscoverFragment mDiscoverFragment;
    private ContributeFragment mContributeFragment;

    private int mCurrentTabPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mDiscoverFragment = new DiscoverFragment();
        mContributeFragment = new ContributeFragment();

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        toolbar.setTitle(R.string.app_name_short);

        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        setupViewPager(mViewPager);

        mAppBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);

        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mTabLayout.setupWithViewPager(mViewPager);

        //mSwipeRefreshLayout = (MultiSwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

        //Listen for which fragment is visible based on tab position
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
                mCurrentTabPosition = tab.getPosition();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //on refresh, use tab position to refresh the visible fragment
//        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//
//                switch(mCurrentTabPosition) {
//                    case 0:
//                        mDiscoverFragment.displayFeed();
//                        break;
//                    case 1:
//                        mContributeFragment.displayUserItems();
//                        break;
//                }
//
//                mSwipeRefreshLayout.setRefreshing(false);
//            }
//        });


//        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                if(positionOffset == 0){
//                    mSwipeRefreshLayout.setEnabled(true);
//                } else {
//                    mSwipeRefreshLayout.setEnabled(false);
//                }
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//                enableDisableSwipeRefresh(state == ViewPager.SCROLL_STATE_IDLE);
//            }
//        });

    } //onCreate

    public void setupViewPager(ViewPager viewPager){
        HomePagerAdapter homePagerAdapter =
                new HomePagerAdapter(getSupportFragmentManager());

        homePagerAdapter.addFragment(mDiscoverFragment, "Discover");
        homePagerAdapter.addFragment(mContributeFragment, "Contribute");
        viewPager.setAdapter(homePagerAdapter);
        viewPager.setOffscreenPageLimit(1);

    }

//    public void enableDisableSwipeRefresh(boolean enable) {
//        if (mSwipeRefreshLayout != null) {
//            mSwipeRefreshLayout.setEnabled(enable);
//        }
//    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        //TODO: possibly delay this?
        mNavigationView.getMenu().findItem(R.id.nav_item_home).setChecked(true);
    }

//    @Override
//    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
//        if (i == 0) {
//            mSwipeRefreshLayout.setEnabled(true);
//        } else {
//            mSwipeRefreshLayout.setEnabled(false);
//        }
//    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        mAppBarLayout.addOnOffsetChangedListener(this);
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        mAppBarLayout.removeOnOffsetChangedListener(this);
//    }

    // --------------------------------------------------------
    // Fragment Callbacks
    // --------------------------------------------------------

    //TODO: delete if none exist

    // --------------------------------------------------------
    // Menus
    // --------------------------------------------------------

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_home, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

} //HomeActivity
