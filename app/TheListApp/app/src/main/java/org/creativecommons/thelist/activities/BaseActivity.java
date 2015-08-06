package org.creativecommons.thelist.activities;

import android.accounts.Account;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.creativecommons.thelist.R;
import org.creativecommons.thelist.authentication.AccountGeneral;
import org.creativecommons.thelist.fragments.MyListFragment;
import org.creativecommons.thelist.utils.ListUser;

public class BaseActivity extends AppCompatActivity implements MyListFragment.LoginListener {
    public static final String TAG = BaseActivity.class.getSimpleName();

    private Context mContext;

    private ListUser mCurrentUser;

    // Navigation drawer
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;

    //Nav Menu
    private Menu mNavigationMenu;
    private MenuItem mAccountItem;
    private TextView mAccountName;

    //Toolbar
    private Toolbar mToolbar;

    private Boolean mLoggingIn = false;

    // Delay to launch nav drawer item, to allow close animation to play
    private static final int NAVDRAWER_LAUNCH_DELAY = 250;

    // Fade in and fade out durations for the main content when switch between nav items
    private static final int MAIN_CONTENT_FADEOUT_DURATION = 150;
    private static final int MAIN_CONTENT_FADEIN_DURATION = 250;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;
        mCurrentUser = new ListUser(this);

    } //onCreate

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        setUpToolbar();
        setUpNavigation();

        View mainContent = findViewById(R.id.main_content);
        if (mainContent != null) {
            mainContent.setAlpha(0);
            mainContent.animate().alpha(1).setDuration(MAIN_CONTENT_FADEIN_DURATION);
        } else {
            Log.d(TAG, "No main content view to fade in");
        }

    } //onPostCreate

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "ON RESUME, logging in: " + String.valueOf(mLoggingIn));

        if(mLoggingIn){
            updateDrawerHeader();
        }
    } //onResume

    // --------------------------------------------------------
    // Helpers
    // --------------------------------------------------------

    private void setUpToolbar(){
        mToolbar = (Toolbar) findViewById(R.id.app_bar);

        if (mToolbar != null) {
            setSupportActionBar(mToolbar);

            assert getSupportActionBar() != null;
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);

        }

    } //setUpToolbar

    private void setUpNavigation(){

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        mNavigationMenu = mNavigationView.getMenu();

        mAccountItem = mNavigationMenu.findItem(R.id.nav_item_account);
        mAccountName = (TextView) mNavigationView.findViewById(R.id.drawer_account_name);

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(final MenuItem menuItem) {

                Intent intent = null;

                switch (menuItem.getItemId()) {
                    case R.id.nav_item_home:
                        intent = new Intent(mContext, HomeActivity.class);
                        break;

                    case R.id.nav_item_photos:
                        Toast.makeText(mContext, "Go to PhotoActivity", Toast.LENGTH_SHORT).show();

                        //intent = new Intent(this, MyPhotosActivity.class);

                        break;
                    case R.id.nav_item_categories:
                        intent = new Intent(mContext, CategoryListActivity.class);
                        break;

                    case R.id.nav_item_requests:
                        intent = new Intent(mContext, AddItemActivity.class);
                        break;

                    case R.id.nav_item_about:
                        intent = new Intent(mContext, AboutActivity.class);
                        break;

                    //TODO: uncomment when survey is updated
//                    case R.id.nav_item_feedback:
//
//                        //Set survey taken
//                        mSharedPref.setSurveyTaken(true);
//
//                        intent = new Intent(Intent.ACTION_VIEW,
//                                Uri.parse(getString(R.string.dialog_survey_link)));
//
//                        break;
                    case R.id.nav_item_account:

                        intent = new Intent(mContext, HomeActivity.class);

                        if (mCurrentUser.isAnonymousUser()) {

                            handleUserAccount(new ListUser.AuthCallback() {
                                @Override
                                public void onAuthed(String authtoken) {

                                    mLoggingIn = true;

                                }
                            });
                        } else {
                            //Log out user
                            mCurrentUser.removeAccount(new ListUser.LogOutCallback() {
                                @Override
                                //TODO: probably should have its own callback w/out returned value (no authtoken anyway)
                                public void onLoggedOut() {

                                    Intent startIntent = new Intent(mContext, StartActivity.class);
                                    startActivity(startIntent);
                                }
                            });
                        }

                        break;
                } //switch

                if(intent != null){

                    final Intent finalIntent = intent;

                    new android.os.Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            menuItem.setChecked(true);

                            startActivity(finalIntent);
                            finish();
                        }
                    }, NAVDRAWER_LAUNCH_DELAY);

                    // Fade out the main content
                    View mainContent = findViewById(R.id.main_content);
                    if (mainContent != null) {
                        mainContent.animate().alpha(0).setDuration(MAIN_CONTENT_FADEOUT_DURATION);
                    }

                } else {
                    Log.d(TAG, "Problem loading activity: intent was null");
                }

                mDrawerLayout.closeDrawer(GravityCompat.START);

                return true;
            } //onNavigationItemSelected
        });

    } //setUpNavigation

    private void handleUserAccount(final ListUser.AuthCallback callback){
        //TODO: bring up account picker dialog w/ new option
        mCurrentUser.getAvailableFullAccounts(new ListUser.AvailableAccountCallback() {
            @Override
            public void onResult(Account[] availableAccounts) {

                if (availableAccounts.length > 0) {
                    mCurrentUser.showAccountPicker(availableAccounts, new ListUser.AuthCallback() {
                        @Override
                        public void onAuthed(String authtoken) {
                            Log.d(TAG, " > handleUserAccount > showAccountPicker > " +
                                    "got authtoken: " + authtoken);

                            updateDrawerHeader();

                            callback.onAuthed(authtoken);

                        }
                    });
                } else {
                    mCurrentUser.addNewFullAccount(AccountGeneral.ACCOUNT_TYPE,
                            AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, new ListUser.AuthCallback() {
                                @Override
                                public void onAuthed(String authtoken) {
                                    Log.d(TAG, " > handleUserAccount > addNewFullAccount > " +
                                            "got authtoken: " + authtoken);

                                    updateDrawerHeader();

                                    callback.onAuthed(authtoken);
                                }
                            });
                }
            }
        });

    } //handleUserAccount

    //Update drawer header (for when anon user logs in)
    public void updateDrawerHeader() {
        Log.v(TAG, "> updateDrawerHeader");

        if(!mCurrentUser.isAnonymousUser()){
            Log.v(TAG, "IS FULL USER DRAWER HEADER");

            mAccountItem.setTitle(R.string.log_out_nav_label);

            mAccountName.setText(mCurrentUser.getAccountName());
            mAccountItem.setIcon(R.drawable.ic_logout_grey600_24dp);
            mAccountName.setVisibility(View.VISIBLE);

        } else {
            Log.v(TAG, "IS ANONY USER DRAWER HEADER");
            mAccountName.setVisibility(View.GONE);

            mAccountItem.setTitle(R.string.log_in_nav_label);
            mAccountItem.setIcon(R.drawable.ic_login_grey600_24dp);

        }

    } //updateDrawerHeader

    // --------------------------------------------------------
    // Fragment Callbacks
    // --------------------------------------------------------

    @Override
    public void isLoggedIn() {
        updateDrawerHeader();
    }

    // --------------------------------------------------------
    // Menu
    // --------------------------------------------------------


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

} //BaseActivity
