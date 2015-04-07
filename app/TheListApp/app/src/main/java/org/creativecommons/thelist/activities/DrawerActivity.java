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

package org.creativecommons.thelist.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import org.creativecommons.thelist.R;
import org.creativecommons.thelist.authentication.AccountGeneral;
import org.creativecommons.thelist.fragments.AddItemFragment;
import org.creativecommons.thelist.fragments.GalleryFragment;
import org.creativecommons.thelist.fragments.MyListFragment;
import org.creativecommons.thelist.fragments.NavigationDrawerFragment;
import org.creativecommons.thelist.utils.ListUser;
import org.creativecommons.thelist.utils.SharedPreferencesMethods;

import java.util.ArrayList;

public class DrawerActivity extends ActionBarActivity implements
        NavigationDrawerFragment.NavigationDrawerListener, GalleryFragment.GalleryListener {
    public static final String TAG = MyListFragment.class.getSimpleName();

    private Context mContext;
    private ListUser mCurrentUser;
    private SharedPreferencesMethods mSharedPref;

    //UI Elements
    private DrawerLayout mDrawerLayout;
    private View mDrawerView;
    private String[] mDrawerTitles;
    private Menu menu;

    // --------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        mContext = this;
        mCurrentUser = new ListUser(DrawerActivity.this);
        mSharedPref = new SharedPreferencesMethods(mContext);

        //UI Elements
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerView = findViewById(R.id.navigation_drawer);
        mDrawerTitles = getResources().getStringArray(R.array.drawer_navigation_labels);

        Toolbar toolbar = (Toolbar)findViewById(R.id.app_bar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        drawerFragment.setUp(R.id.navigation_drawer,
                (DrawerLayout)findViewById(R.id.drawer_layout), toolbar);
        mDrawerLayout.closeDrawer(GravityCompat.START);


        //TODO: direct to gallery if intent comes from notification (2.0)
//        Intent notificationIntent = getIntent();
//
//        if(notificationIntent != null){
//            try{
//                String action = notificationIntent.getAction().toUpperCase();
//
//                if (action.equals(MessageHelper.INTENT_ACTION)) {
//                    GalleryFragment galleryFragment = new GalleryFragment();
//                    //load gallery view
//                    FragmentManager fragmentManager = getSupportFragmentManager();
//                    fragmentManager.beginTransaction()
//                            .replace(R.id.main_content_container, galleryFragment)
//                            .commit();
//
//                    // update selected item and title
//                    getSupportActionBar().setTitle(mDrawerTitles[1]);
//                    return;
//                }
//                else{
//                    Log.d(TAG, "Intent was null");
//                }
//            }catch(Exception e) {
//                Log.e(TAG, "Problem consuming action from intent", e);
//                MyListFragment listFragment = new MyListFragment();
//                //load default view
//                FragmentManager fragmentManager = getSupportFragmentManager();
//                fragmentManager.beginTransaction()
//                        .replace(R.id.main_content_container, listFragment)
//                        .commit();
//                return;
//            }
//        }

        //If there is no savedInstanceState, load in default fragment
        if(savedInstanceState == null){
            MyListFragment listFragment = new MyListFragment();
            //load default view
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_content_container, listFragment)
                    .commit();

        }
    } //onCreate

    @Override
    public void onResume() {
        super.onResume();
    } //onResume

    // --------------------------------------------------------
    //Drawer Fragment
    // --------------------------------------------------------

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public void onDrawerClicked(int position) {
        Fragment fragment = null;
        switch(position) {
            case 0: //My List
                fragment = new MyListFragment();
                break;
            case 1: //My Photos
                fragment = new GalleryFragment();
                break;
            case 2: //My Categories
                Intent catIntent = new Intent(DrawerActivity.this, CategoryListActivity.class);
                startActivity(catIntent);
                break;
            case 3: //Request An Item
                fragment = new AddItemFragment();
                break;
            case 4: //About The App
                Intent aboutIntent = new Intent(DrawerActivity.this, AboutActivity.class);
                startActivity(aboutIntent);
                break;
            case 5: //Give Feedback
                //Set survey taken
                mSharedPref.setSurveyTaken(true);

                //Go to Google Form
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(getString(R.string.dialog_survey_link)));
                startActivity(browserIntent);
                break;
            default:
                break;
        } //switch

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_content_container, fragment)
                    .commit();

            // update selected item and title, then close the drawer
            getSupportActionBar().setTitle(mDrawerTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerView);
        } else {
            // error in creating fragment
            Log.e(TAG, "Error in creating fragment");
        }
    } //onDrawerClicked

    @Override
    public void onAccountClicked() {
        if(mCurrentUser.isTempUser() || mCurrentUser.getAccount() == null){
            handleUserAccount();
        } else {
            //Log out user
            mCurrentUser.removeAccounts(new ListUser.AuthCallback() {
                @Override
                //TODO: probably should have its own callback w/out returned value (no authtoken anyway)
                public void onSuccess(String authtoken) {
                    mSharedPref.ClearAllSharedPreferences();
                    Intent startIntent = new Intent(DrawerActivity.this, StartActivity.class);
                    startActivity(startIntent);
                }
            });
        }
    } //onAccountClicked

    // --------------------------------------------------------
    // Gallery Fragment
    // --------------------------------------------------------

    @Override
    public void viewImage(ArrayList<String> urls, int position) {
        //Start detailed view
        Intent intent = new Intent(DrawerActivity.this, ImageActivity.class);
        intent.putExtra("position", position);
        intent.putStringArrayListExtra("urls", urls);
        startActivity(intent);
    } //viewImage


    // --------------------------------------------------------
    // Main Menu + Helpers
    // --------------------------------------------------------

    private void handleUserAccount(){
        //TODO: bring up account picker dialog w/ new option
        if(mCurrentUser.getAccountCount() > 0){
            mCurrentUser.showAccountPicker(new ListUser.AuthCallback() {
                @Override
                public void onSuccess(String authtoken) {
                    Log.d(TAG, " > switch_accounts MenuItem > showAccountPicker > " +
                            "got authtoken: " + authtoken);
                }
            });
        } else {
            mCurrentUser.addNewAccount(AccountGeneral.ACCOUNT_TYPE,
                    AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, new ListUser.AuthCallback() {
                        @Override
                        public void onSuccess(String authtoken) {
                            Log.d(TAG, " > switch_accounts MenuItem > addNewAccount > " +
                                    "got authtoken: " + authtoken);

                            mDrawerLayout.closeDrawer(GravityCompat.START);

                            MyListFragment listFragment = new MyListFragment();
                            //load default view
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            fragmentManager.beginTransaction()
                                    .replace(R.id.main_content_container, listFragment)
                                    .commit();
                        }
                    });
        }
    } //handleUserAccount

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_drawer, menu);
        this.menu = menu; //keep this in case of later use

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(item.getItemId()) {
            case R.id.about_theapp:
                Intent aboutAppIntent = new Intent(DrawerActivity.this, AboutActivity.class);
                startActivity(aboutAppIntent);
                return true;
            case R.id.remove_accounts:
                if (mCurrentUser.isTempUser()) {
                    mSharedPref.ClearAllSharedPreferences();
                    Intent startIntent = new Intent(DrawerActivity.this, StartActivity.class);
                    startActivity(startIntent);
                } else {
                    mCurrentUser.removeAccounts(new ListUser.AuthCallback() {
                        @Override
                        //TODO: probably should have its own callback w/out returned value (no authtoken anyway)
                        public void onSuccess(String authtoken) {
                            mSharedPref.ClearAllSharedPreferences();
                            Intent startIntent = new Intent(DrawerActivity.this, StartActivity.class);
                            startActivity(startIntent);
                        }
                    });
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
