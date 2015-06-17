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

package org.creativecommons.thelist.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.creativecommons.thelist.R;

import java.util.HashMap;


@ReportsCrashes(
        //formKey = "",
        formUri = "https://thelist.creativecommons.org/app/acra/index.php",
        httpMethod = org.acra.sender.HttpSender.Method.POST,
        customReportContent = {
                ReportField.APP_VERSION_CODE,
                ReportField.APP_VERSION_NAME,
                ReportField.ANDROID_VERSION,
                ReportField.PACKAGE_NAME,
                ReportField.REPORT_ID,
                ReportField.BUILD,
                ReportField.STACK_TRACE
        },
        mode = ReportingInteractionMode.TOAST,
        forceCloseDialogAfterToast = true, // optional, default false
        resToastText = R.string.crash_toast_text
)

public class ListApplication extends Application {
    private static final String TAG = "ListApp";
    private static final String PROPERTY_ID = "UA-2010376-31";

    public ListApplication() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(TAG, "LIST ON CREATE");
        ListUser listUser = new ListUser(getApplicationContext());
        SharedPreferencesMethods sharedPref = new SharedPreferencesMethods(getApplicationContext());

        //Create App SharedPreferences
        SharedPreferences sharedPrefCreate = getApplicationContext().getSharedPreferences
                (SharedPreferencesMethods.APP_PREFERENCES_KEY, Context.MODE_PRIVATE);

        //Set Survey Count
        sharedPref.setSurveyCount(0);

        //If user has id, but no account valid account, reset sharedPreferences (fully log user out)
        if(!(listUser.isAnonymousUser()) && listUser.getAccount() == null){
            sharedPref.ClearAllSharedPreferences();
        }

        //Check OptOut status (must happen once per app open/restart)
        if(!(listUser.isAnonymousUser()) && listUser.getAccount() != null){ //Logged in
            Log.v(TAG, "LIST ON CREATE: LOGGED IN");
            //Get optOut value from the account (if there is no value this should return null)
            Boolean optOut = listUser.getAnalyticsOptOut();

            if(optOut == null){
                sharedPref.setAnalyticsOptOut(null); //this will trigger dialog in StartActivity
            } else if(optOut == true){ //if user has opt-ted out (true)
                //Set app opt-out
                GoogleAnalytics.getInstance(this).setAppOptOut(true);
                sharedPref.setAnalyticsViewed(true);
                Log.v(TAG, "> isAnonymousUser = false > setOptOut, true");
            }
        } else { //Anonyous User (to be)
            Boolean optOut = sharedPref.getAnalyticsOptOut();
            Log.v(TAG, "anonUser optOut is: " + String.valueOf(optOut));
            if(Boolean.TRUE.equals(optOut)){
                GoogleAnalytics.getInstance(this).setAppOptOut(true);
                Log.v(TAG, "> isAnonymousUser = true > setOptOut, true");
            }
        }

        // The following line triggers the initialization of ACRA
        ACRA.init(this);

    } //onCreate

    public enum TrackerName {
        GLOBAL_TRACKER,
        APP_TRACKER, //not used currently
    }

    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    public synchronized Tracker getTracker(TrackerName trackerId) {
        Log.d(TAG, "getTracker()");
        if (!mTrackers.containsKey(trackerId)) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);

            //analytics.getLogger().setLogLevel(Logger.LogLevel.INFO);
            analytics.getLogger().setLogLevel(Logger.LogLevel.VERBOSE);

            // Global GA Settings
            // <!-- Google Analytics SDK V4 BUG20141213 Using a GA global xml freezes the app! -->
            analytics.setDryRun(false);
            analytics.enableAutoActivityReports(this);
            analytics.setLocalDispatchPeriod(30);

            // Create a new tracker
            Tracker t = (trackerId == TrackerName.GLOBAL_TRACKER) ? analytics.newTracker(R.xml.global_tracker) : null;
            if (t != null) {
                //t.enableAdvertisingIdCollection(true);
                t.setSampleRate(100.0);
                t.setSessionTimeout(300);
                t.setAnonymizeIp(true);
                t.enableExceptionReporting(true);
                t.enableAutoActivityTracking(true);
            }
            mTrackers.put(trackerId, t);
            Log.v(TAG, "put mTrackers: " + trackerId.toString());
        }
        Log.v(TAG, "return mTrackers");
        return mTrackers.get(trackerId);
    }
} //ListApplication


