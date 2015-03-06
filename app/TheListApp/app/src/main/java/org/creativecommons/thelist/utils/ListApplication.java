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
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;

import org.creativecommons.thelist.R;

import java.util.HashMap;

public class ListApplication extends Application {
    private static final String TAG = "ListApp";
    private static final String PROPERTY_ID = "UA-2010376-31";

    public ListApplication() {
        super();
    }

    public void onCreate() {
        super.onCreate();
        Log.v(TAG, "LIST ON CREATE");
        ListUser listUser = new ListUser(getApplicationContext());
        SharedPreferencesMethods sharedPref = new SharedPreferencesMethods(getApplicationContext());

        //Check OptOut status (on app open
        if(!(listUser.isTempUser())){ //Logged in
            Log.v(TAG, "LIST ON CREATE: LOGGED IN");
            Boolean optOut = listUser.getAnalyticsOptOut();

            if(optOut == null){
                sharedPref.setAnalyticsOptOut(null); //this will trigger dialog onStart
            }
            if(listUser.getAnalyticsOptOut()){ //if user has opt-ted out
                //Set app opt-out
                GoogleAnalytics.getInstance(this).setAppOptOut(true);
                Log.v(TAG, "> isTempUser = false > setOptOut, true");
            }
        } else { //Temp User

            Boolean optOut = sharedPref.getAnalyticsOptOut();
            Log.v(TAG, String.valueOf(optOut));
            if(Boolean.TRUE.equals(sharedPref.getAnalyticsOptOut())){
                GoogleAnalytics.getInstance(this).setAppOptOut(true);
                Log.v(TAG, "> isTempUser = true > setOptOut, true");
            }
        }
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
            analytics.setDryRun(true);
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


