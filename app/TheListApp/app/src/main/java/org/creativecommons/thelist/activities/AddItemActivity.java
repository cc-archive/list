package org.creativecommons.thelist.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import org.creativecommons.thelist.R;
import org.creativecommons.thelist.fragments.AddItemFragment;
import org.creativecommons.thelist.utils.ListApplication;

public class AddItemActivity extends ActionBarActivity {
    public static final String TAG = AddItemActivity.class.getSimpleName();
    protected Context mContext;

    //UI Elements
    FrameLayout mFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        mContext = this;

        //Google Analytics Tracker
        ((ListApplication) getApplication()).getTracker(ListApplication.TrackerName.GLOBAL_TRACKER);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        //UI Elements
        mFrameLayout = (FrameLayout) findViewById(R.id.add_item_fragment_container);

        //auto load loginFragment
        AddItemFragment addItemFragment = new AddItemFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.add_item_fragment_container, addItemFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
        mFrameLayout.setClickable(true);
    } //onCreate


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_item, menu);
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
}
