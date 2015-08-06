package org.creativecommons.thelist.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import org.creativecommons.thelist.R;
import org.creativecommons.thelist.adapters.HomePagerAdapter;
import org.creativecommons.thelist.fragments.DiscoverFragment;
import org.creativecommons.thelist.fragments.MyListFragment;

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

        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mTabLayout.setupWithViewPager(viewPager);
    }

    public void setupViewPager(ViewPager viewPager){
        HomePagerAdapter homePagerAdapter =
                new HomePagerAdapter(getSupportFragmentManager());

        homePagerAdapter.addFragment(new DiscoverFragment(), "Discover");
        homePagerAdapter.addFragment(new MyListFragment(), "Contribute");
        viewPager.setAdapter(homePagerAdapter);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        mNavigationView.getMenu().findItem(R.id.nav_item_home).setChecked(true);
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
