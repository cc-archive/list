/* The List powered by Creative Commons

   Copyright (C) 2014 Creative Commons

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

import org.creativecommons.thelist.R;
import org.creativecommons.thelist.authentication.AccountGeneral;
import org.creativecommons.thelist.fragments.AccountFragment;
import org.creativecommons.thelist.fragments.ExplainerFragment;
import org.creativecommons.thelist.misc.AccountFragment_old;
import org.creativecommons.thelist.utils.ListUser;
import org.creativecommons.thelist.utils.RequestMethods;
import org.creativecommons.thelist.utils.SharedPreferencesMethods;


public class StartActivity extends FragmentActivity implements ExplainerFragment.OnClickListener,
        AccountFragment.AuthListener {
    public static final String TAG = StartActivity.class.getSimpleName();
    ListUser mCurrentUser;
    protected Button mStartButton;
    protected Button mAccountButton;
    protected TextView mTermsLink;
    protected Context mContext;
    protected SharedPreferencesMethods sharedPreferencesMethods;
    private AccountManager am;
    protected FrameLayout mFrameLayout;

    //Fragment
    ExplainerFragment explainerFragment = new ExplainerFragment();
    AccountFragment loginFragment = new AccountFragment();

    // ---------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        mContext = this;
        mCurrentUser = new ListUser(StartActivity.this);
        sharedPreferencesMethods = new SharedPreferencesMethods(mContext);
        am = AccountManager.get(getBaseContext());

        Log.v(TAG, "STARTACTIVITY ON CREATE");

        //TODO: check for google analytics opt-in
        //The beta version of this app uses google analytics message
//        RequestMethods rqm = new RequestMethods(mContext);
//        rqm.showDialog(mContext, "Just letting you know!", "The List beta uses Google Analytics help us learn " +
//                "how to make the app better." + "We don’t collect your personal info!");


//        GoogleAnalytics instance = GoogleAnalytics.getInstance(this);
//        instance.setAppOptOut(true);

        //Google Analytics Tracker
//        Tracker t = ((ListApplication) StartActivity.this.getApplication()).getTracker(
//                ListApplication.TrackerName.GLOBAL_TRACKER);
//
//        t.setScreenName(TAG);
//        t.send(new HitBuilders.AppViewBuilder().build());

        //Create sharedPreferences
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
                Intent intent = new Intent(StartActivity.this, CategoryListActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }); //startButton

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
        }); //accountButon

        if(mTermsLink != null){
            mTermsLink.setMovementMethod(LinkMovementMethod.getInstance());
        }

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
    } //OnCreate

    @Override
    protected void onResume(){
        super.onResume();

        //TODO: Check if user token is valid, redirect to MainActivity if yes
        if(!(mCurrentUser.isTempUser())) {
            Log.v(TAG, "START: USER IS LOGGED IN");
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            Log.v(TAG, "START: USER IS NOT LOGGED IN");
        }

        //If explainer is still there, get rid of it
        Log.d(TAG, "On Resume, removing Fragment");
        getSupportFragmentManager().beginTransaction()
                .remove(explainerFragment)
                .commit();
        mFrameLayout.setClickable(false);
        Log.d(TAG, "On Resume, removed Fragment");
    }

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
