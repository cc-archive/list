/* The List powered by Creative Commons

   Copyright (C) 2014, 2015 Creative Commons Corporation

   This program is free software: you can redistribute it and/or modify
   it under the terms of either the GNU Affero General Public License or
   the GNU General Public License as published by the
   Free Software Foundation, either version 3 of the Licenses, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

   You should have received a copy of the GNU General Public License and
   the GNU Affero General Public License along with this program.

   If not, see <http://www.gnu.org/licenses/>.

*/

package org.creativecommons.thelist.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.creativecommons.thelist.R;
import org.creativecommons.thelist.adapters.GalleryItem;
import org.creativecommons.thelist.authentication.AccountGeneral;
import org.creativecommons.thelist.fragments.GalleryFragment;
import org.creativecommons.thelist.utils.ListUser;

import java.util.ArrayList;

public class MyPhotosActivity extends BaseActivity implements GalleryFragment.GalleryListener {
    public static final String TAG = MyPhotosActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_photos);

        GalleryFragment galleryFragment = new GalleryFragment();
        //load default view
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, galleryFragment)
                .commit();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        //TODO: possibly delay this?
        mNavigationView.getMenu().findItem(R.id.nav_item_photos).setChecked(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_photos, menu);
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
    public void viewImage(ArrayList<GalleryItem> photoObjects, int position) {
        Bundle b = new Bundle();
        b.putParcelableArrayList("photos", photoObjects);
        b.putInt("position", position);

        //Start detailed view
        Intent intent = new Intent(MyPhotosActivity.this, ImageActivity.class);
        intent.putExtras(b);
        startActivity(intent);
    }

    @Override
    public void onLoginClick() {
        mCurrentUser.addNewFullAccount(AccountGeneral.ACCOUNT_TYPE,
                AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, new ListUser.AuthCallback() {
                    @Override
                    public void onAuthed(String authtoken) {
                        Log.d(TAG, " > addNewFullAccount > " +
                                "got authtoken: " + authtoken);

                        updateDrawerHeader();

                        GalleryFragment galleryFragment = new GalleryFragment();
                        //load default view
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction()
                                .replace(R.id.main_content_container, galleryFragment)
                                .commitAllowingStateLoss();

                    }
                });

    }

} //MyPhotosActivity
