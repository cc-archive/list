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

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.analytics.GoogleAnalytics;

import org.creativecommons.thelist.R;
import org.creativecommons.thelist.authentication.AccountGeneral;
import org.creativecommons.thelist.fragments.AccountFragment;
import org.creativecommons.thelist.fragments.ExplainerFragment;
import org.creativecommons.thelist.utils.ListApplication;
import org.creativecommons.thelist.utils.ListUser;
import org.creativecommons.thelist.utils.MessageHelper;
import org.creativecommons.thelist.utils.SharedPreferencesMethods;


public class StartActivity extends FragmentActivity implements ExplainerFragment.OnClickListener,
        AccountFragment.AuthListener {
    public static final String TAG = StartActivity.class.getSimpleName();
    protected Context mContext;
    protected ListUser mCurrentUser;
    protected SharedPreferencesMethods mSharedPref;
    protected MessageHelper mMessageHelper;
    private AccountManager am;

    //UI Elements
    protected Button mAccountButton;
    protected FrameLayout mFrameLayout;
    protected Button mStartButton;
    protected TextView mTermsLink;

    //Fragment
    ExplainerFragment explainerFragment = new ExplainerFragment();
    AccountFragment loginFragment = new AccountFragment();

    // --------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        mContext = this;
        mSharedPref = new SharedPreferencesMethods(mContext);
        mMessageHelper = new MessageHelper(mContext);
        mCurrentUser = new ListUser(StartActivity.this);
        am = AccountManager.get(getBaseContext());

        //Google Analytics Tracker
        ((ListApplication) getApplication()).getTracker(ListApplication.TrackerName.GLOBAL_TRACKER);

        //Create App SharedPreferences
        SharedPreferences sharedPref = mContext.getSharedPreferences
                (SharedPreferencesMethods.APP_PREFERENCES_KEY, Context.MODE_PRIVATE);

        //UI Elements
        mFrameLayout = (FrameLayout)findViewById(R.id.fragment_container);
        mStartButton = (Button) findViewById(R.id.startButton);
        mAccountButton = (Button) findViewById(R.id.accountButton);
        mTermsLink = (TextView) findViewById(R.id.cc_logo_label);

        //“I’m new to the list”
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Load explainerFragment
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container,explainerFragment)
                        .commit();
                mFrameLayout.setClickable(true);
            }
        }); //StartButton ClickListener

        //“I already have an account”
        mAccountButton.setOnClickListener(new View.OnClickListener() {
            //If you have accounts > show picker; if not, show login
            @Override
            public void onClick(View v) {
                Account availableAccounts[] = am.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);

                //TODO: switch getAuthed: login to first account if there if only one, if there is more than more go to accountPicker
                if(availableAccounts.length > 1){
                    mCurrentUser.showAccountPicker(new ListUser.AuthCallback() {
                        @Override
                        public void onSuccess(String authtoken) {
                            Log.d(TAG, "I have an account > Got an authtoken");
                            //TODO: is this actually needed?
                            Intent intent = new Intent(StartActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    });
                } else {
                    mCurrentUser.getAuthed(new ListUser.AuthCallback() {
                        @Override
                        public void onSuccess(String authtoken) {
                            Log.d(TAG, "I have an account + I re-authenticated > Got an authtoken");
                            //TODO: is this actually needed?
                            Intent intent = new Intent(StartActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    });
                }
            }
        }); //accountButton

        //Enable links
        if(mTermsLink != null){
            mTermsLink.setMovementMethod(LinkMovementMethod.getInstance());
        }
    } //OnCreate

    @Override
    protected void onRestart(){
        super.onRestart();
    } //onRestart

    @Override
    protected void onStart(){
        super.onStart();
        if(!(mCurrentUser.isTempUser())) {
            //Redirect to MainActivity
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            //Display Google Analytics Message
            //TODO: check if this will return false
            Boolean OptOut = mSharedPref.getAnalyticsOptOut();
            Log.v(TAG, "OPTOUT" + String.valueOf(OptOut));
            if(OptOut == null){
                //Request Permissions
                mMessageHelper.enableFeatureDialog(mContext, getString(R.string.dialog_ga_title),
                        getString(R.string.dialog_ga_message),
                        new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                super.onPositive(dialog);
                                //Set boolean opt-in
                                mSharedPref.setAnalyticsOptOut(false);
                                GoogleAnalytics.getInstance(mContext).setAppOptOut(false);
                            }
                            @Override
                            public void onNegative(MaterialDialog dialog) {
                                super.onNegative(dialog);
                                //Set boolean opt-out
                                mSharedPref.setAnalyticsOptOut(true);
                                GoogleAnalytics.getInstance(mContext).setAppOptOut(true);

                            }
                        });
                //mSharedPref.setAnalyticsMessageViewed(); TODO: remove
            }
        }

        GoogleAnalytics.getInstance(this).reportActivityStart(this);
        Log.v(TAG, "ON START CALLED – AFTER GA CALLED");
    } //onStart

    @Override
    protected void onStop(){
        super.onStop();
        Log.v(TAG, "ON STOP CALLED");
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
        Log.v(TAG, "ON STOP CALLED – AFTER GA CALLED");
    } //onStop

    @Override
    public void onNextClicked() {
        Intent intent = new Intent(StartActivity.this, CategoryListActivity.class);
        startActivity(intent);
    }

    @Override
    public void onUserSignedIn(Bundle userData) {
        Intent intent = new Intent(StartActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onUserSignedUp(Bundle userData) {
        Intent intent = new Intent(StartActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onCancelLogin() {
        getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .remove(loginFragment)
                .commit();
        mFrameLayout.setClickable(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_start, menu);
        return true;
    } //onCreateOptionsMenu

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_done) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    } //onOptionsItemSelected
} //StartActivity
