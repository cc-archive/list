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

    public static final String TAB_POSITION = "TAB_POS";

    private AppBarLayout mAppBarLayout;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private DiscoverFragment mDiscoverFragment;
    private ContributeFragment mContributeFragment;

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

        //Listen for which fragment is visible based on tab position
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    } //onCreate

    public void setupViewPager(ViewPager viewPager){
        HomePagerAdapter homePagerAdapter =
                new HomePagerAdapter(getSupportFragmentManager());

        homePagerAdapter.addFragment(mDiscoverFragment, "Discover");
        homePagerAdapter.addFragment(mContributeFragment, "Contribute");
        viewPager.setAdapter(homePagerAdapter);
        viewPager.setOffscreenPageLimit(1); //TODO: does this work? I think not

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        //TODO: possibly delay this?
        mNavigationView.getMenu().findItem(R.id.nav_item_home).setChecked(true);
    }

    //Save and restore instance state: remember which tab was selected

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(TAB_POSITION, mTabLayout.getSelectedTabPosition());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mViewPager.setCurrentItem(savedInstanceState.getInt(TAB_POSITION));
    }

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
