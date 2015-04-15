/* The List powered by Creative Commons

   Copyright (C) 2014, 2015 Creative Commons

   This program is free software: you can redistribute it and/or modify
   it under the terms of the GNU Affero General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU Affero General Public License for more details.

   You should have received a copy of the GNU Affero General Public License
   along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/

package org.creativecommons.thelist.misc;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;

import com.google.android.gms.analytics.GoogleAnalytics;

import org.creativecommons.thelist.R;
import org.creativecommons.thelist.activities.ImageActivity;
import org.creativecommons.thelist.fragments.GalleryFragment;
import org.creativecommons.thelist.utils.ListApplication;

import java.util.ArrayList;

public class GalleryActivity extends ActionBarActivity implements GalleryFragment.GalleryListener {
    public static final String TAG = GalleryActivity.class.getSimpleName();
    protected Context mContext;

    //UI Elements
    FrameLayout mFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        mContext = this;

        //Google Analytics Tracker
        ((ListApplication) getApplication()).getTracker(ListApplication.TrackerName.GLOBAL_TRACKER);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        //UI Elements
        mFrameLayout = (FrameLayout) findViewById(R.id.gallery_fragment_container);

        //auto load loginFragment
        GalleryFragment galleryFragment = new GalleryFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.gallery_fragment_container,galleryFragment, "GALLERY_FRAGMENT") //TODO: get rid of frag tag
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
        mFrameLayout.setClickable(true);
    }

    @Override
    public void viewImage(ArrayList<String> urls, int position) {
        Intent intent = new Intent(GalleryActivity.this, ImageActivity.class);
        intent.putExtra("position", position);
        intent.putStringArrayListExtra("urls", urls);
        startActivity(intent);
    } //viewImage

    @Override
    protected void onStart(){
        super.onStart();
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    protected void onStop(){
        super.onStop();
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }

} //GalleryActivity

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_gallery, menu);
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
//}
