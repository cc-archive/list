package org.creativecommons.thelist.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.creativecommons.thelist.R;
import org.creativecommons.thelist.adapters.MainPagerAdapter;
import org.creativecommons.thelist.fragments.DiscoverFragment;
import org.creativecommons.thelist.fragments.MyListFragment;

public class FeedActivity extends AppCompatActivity implements MyListFragment.LoginListener {
    public static final String TAG = FeedActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        setupViewPager(viewPager);


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

    } //onCreate

    public void setupViewPager(ViewPager viewPager){
        MainPagerAdapter mainPagerAdapter =
                new MainPagerAdapter(getSupportFragmentManager());

        mainPagerAdapter.addFragment(new DiscoverFragment(), "Discover");
        mainPagerAdapter.addFragment(new MyListFragment(), "Contribute");
        viewPager.setAdapter(mainPagerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_feed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void isLoggedIn() {

    }
}
