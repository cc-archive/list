package org.creativecommons.thelist.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import org.creativecommons.thelist.R;
import org.creativecommons.thelist.adapters.HomePagerAdapter;
import org.creativecommons.thelist.fragments.ContributeFragment;
import org.creativecommons.thelist.fragments.DiscoverFragment;

public class HomeActivity extends BaseActivity {
    public static final String TAG = HomeActivity.class.getSimpleName();

    //Tab Layout
    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        toolbar.setTitle(R.string.app_name_short);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        setupViewPager(viewPager);

//        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float v, int i1) {
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
//        } );

        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mTabLayout.setupWithViewPager(viewPager);
    }

    public void setupViewPager(ViewPager viewPager){
        HomePagerAdapter homePagerAdapter =
                new HomePagerAdapter(getSupportFragmentManager());

        homePagerAdapter.addFragment(new DiscoverFragment(), "Discover");
        homePagerAdapter.addFragment(new ContributeFragment(), "Contribute");
        viewPager.setAdapter(homePagerAdapter);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        //TODO: possibly delay this?
        mNavigationView.getMenu().findItem(R.id.nav_item_home).setChecked(true);
    }

    //    protected void enableDisableSwipeRefresh(boolean enable) {
//        if (mSwipeRefreshLayout != null) {
//            mSwipeRefreshLayout.setEnabled(enable);
//        }
//    }


//    public void setUpSwipeRefresh(){
//        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
//        if (mSwipeRefreshLayout != null) { //if exists in view tree
//
//            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//                @Override
//                public void onRefresh() {
//                    //requestDataRefresh(); //TODO: make it do stuff
//
//                }
//            });
//
//            if (mSwipeRefreshLayout instanceof MultiSwipeRefreshLayout) {
//                MultiSwipeRefreshLayout mswrl = (MultiSwipeRefreshLayout) mSwipeRefreshLayout;
//                mswrl.setCanChildScrollUpCallback(this);
//            }
//        }
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
