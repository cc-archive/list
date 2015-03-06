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

import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class SharedPreferencesMethods {
    public static final String TAG = SharedPreferencesMethods.class.getSimpleName();

    protected Context mContext;
    protected AccountManager am;

    public SharedPreferencesMethods(Context context) {
        mContext = context;
        am = AccountManager.get(mContext);
    }

    //SharedPreferences Constants
    public static final String CATEGORY_PREFERENCE_KEY = "category";
    public static final String LIST_ITEM_PREFERENCE_KEY = "item";
    public static final String USER_ID_PREFERENCE_KEY = "id";
    public static final String USER_KEY = "ekey";
    public static final String GA_CHECK = "GA_check";
    public static final String ANALYTICS_OPTOUT = "analyticsOptOut";

    public static final String APP_PREFERENCES_KEY = "org.creativecommons.thelist.43493255t43";

    //----------------------------------------------------------
    //SET PREFERENCES
    //----------------------------------------------------------

    //Save Any Preference
    public void saveSharedPreference(String key, String value){
        SharedPreferences sharedPref = mContext.getSharedPreferences(APP_PREFERENCES_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
        Log.v("ADDED AND SAVED ITEM: ", value);
    }

    public void saveUserID(String id){
        SharedPreferences sharedPref = mContext.getSharedPreferences(APP_PREFERENCES_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(USER_ID_PREFERENCE_KEY, id);
        editor.apply();
        Log.v("ADDED AND SAVED ITEM: ", id);
    }

    @Deprecated
    //TODO: remove once permission exists
    public void setAnalyticsMessageViewed(){
        SharedPreferences sharedPref = mContext.getSharedPreferences(APP_PREFERENCES_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(GA_CHECK, true);
        editor.apply();
    }

    public void setAnalyticsOptOut(Boolean bol){
        SharedPreferences sharedPref = mContext.getSharedPreferences(APP_PREFERENCES_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(ANALYTICS_OPTOUT, bol);
        editor.apply();
    }

    public void saveKey(String key){
        saveSharedPreference(USER_KEY + getUserId(), key);
    }

    //----------------------------------------------------------
    //GET PREFERENCES
    //----------------------------------------------------------

    //Get User ID from SharedPreferences
    public String getUserId(){
        SharedPreferences sharedPref = mContext.getSharedPreferences(APP_PREFERENCES_KEY, Context.MODE_PRIVATE);

        if(sharedPref.contains(SharedPreferencesMethods.USER_ID_PREFERENCE_KEY)) {
            String userID = sharedPref.getString(USER_ID_PREFERENCE_KEY, null);
            return userID;
        } else {
            return null;
        }
    } //getUserId

    public String getKey(){
        SharedPreferences sharedPref = mContext.getSharedPreferences(APP_PREFERENCES_KEY, Context.MODE_PRIVATE);
        if(sharedPref.contains(SharedPreferencesMethods.USER_KEY)){
            return sharedPref.getString(USER_KEY + getUserId(), null);
        } else {
            return null;
        }
    } //getKey

    @Deprecated
    //Boolean if googleAnalytics message has been viewed TODO: remove when permission is available
    public Boolean getGaMessageViewed(){
        SharedPreferences sharedPref = mContext.getSharedPreferences(APP_PREFERENCES_KEY, Context.MODE_PRIVATE);
        return sharedPref.getBoolean(GA_CHECK, false);
    }

    public Boolean getAnalyticsOptOut(){
        SharedPreferences sharedPref = mContext.getSharedPreferences(APP_PREFERENCES_KEY, Context.MODE_PRIVATE);

        if(sharedPref.contains(SharedPreferencesMethods.ANALYTICS_OPTOUT)) {
            return sharedPref.getBoolean(ANALYTICS_OPTOUT, false); //defaults to false
        } else {
            return null;
        }
    }

    //getSharedPreferenceList (generic)
    public JSONArray getSharedPreferenceList(String key){
        SharedPreferences sharedPref = mContext.getSharedPreferences(APP_PREFERENCES_KEY, Context.MODE_PRIVATE);

        if(sharedPref.contains(key)){
            String value = sharedPref.getString(key, null);

            if(value != null){
                JsonParser parser = new JsonParser();
                JsonElement element = parser.parse(value);
                JsonArray array = element.getAsJsonArray();

                //Make usable as JSONArray
                List<String> list = new ArrayList<String>();
                for (int i = 0; i < array.size(); i++) {
                    list.add(array.get(i).getAsString());
                }
                return new JSONArray(list);
            } else {
                return null;
            }

        } else {
            return null;
        }
    } //getSharedPreferenceList


    //----------------------------------------------------------
    // LIST ITEM PREFERENCES
    //----------------------------------------------------------

    public JSONArray getUserItemPreference() {
        return getSharedPreferenceList(LIST_ITEM_PREFERENCE_KEY);
    } //getUserItemPreference


    //DELETE single value from Item Preferences
    public void deleteUserItemPreference(String itemID) {
        SharedPreferences sharedPref = mContext.getSharedPreferences(APP_PREFERENCES_KEY, Context.MODE_PRIVATE);
        String listOfValues = sharedPref.getString(LIST_ITEM_PREFERENCE_KEY, null);

        if(listOfValues != null && listOfValues.length() > 0){
            Log.v(TAG, "> deleteUserItemPreference, try to remove item: " +  itemID);
            //Convert from String to Array
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(listOfValues);
            JsonArray array = element.getAsJsonArray();
            Log.v(TAG, "> deleteUserItemPreference, array from SharedPref: " +  array.toString());

            for (int i = 0; i < array.size(); i++) {
                String singleItem = array.get(i).getAsString();
                if (singleItem.equals(itemID)) {
                    Log.v(TAG, "> deleteUserItemPreference, removing item: " +  itemID);
                    array.remove(i);
                }
            }
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(LIST_ITEM_PREFERENCE_KEY, array.toString());
            editor.apply();
        }
    } //deleteUserItemPreference

    //Non-logged in user
    public int getUserItemCount(){
        SharedPreferences sharedPref = mContext.getSharedPreferences(APP_PREFERENCES_KEY, Context.MODE_PRIVATE);

        if(sharedPref.contains(SharedPreferencesMethods.LIST_ITEM_PREFERENCE_KEY)){
            String listOfValues = sharedPref.getString(LIST_ITEM_PREFERENCE_KEY, null);

            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(listOfValues);
            JsonArray array = element.getAsJsonArray();
            int size = array.size();
            return size;
        } else{
            return 0;
        }
    } //getUserItemCount

    //----------------------------------------------------------
    // CATEGORY PREFERENCES
    //----------------------------------------------------------

    public JSONArray getCategorySharedPreference(){
        return getSharedPreferenceList(CATEGORY_PREFERENCE_KEY);
    } //getCategorySharedPreference

    //DELETE single value from Category Preferences
    public void deleteUserCategoryPreference(String catId) {
        SharedPreferences sharedPref = mContext.getSharedPreferences(APP_PREFERENCES_KEY, Context.MODE_PRIVATE);
        String listOfValues = sharedPref.getString(CATEGORY_PREFERENCE_KEY, null);
        Log.v("REMOVE ITEM ID: ", catId);
        //Convert from String to Array
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(listOfValues);
        JsonArray array = element.getAsJsonArray();
        Log.v("ARRAY FROM SHAREDPREF: ", array.toString());

        for (int i = 0; i < array.size(); i++) {
            String singleItem = array.get(i).getAsString();
            if (singleItem.equals(catId)) {
                Log.v("ITEM TO REMOVE IS: ", singleItem);
                array.remove(i);
            }
        }
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(CATEGORY_PREFERENCE_KEY, array.toString());
        editor.apply();
    } //deleteUserCategoryPreference

    //----------------------------------------------------------
    //CLEAR PREFERENCES
    //----------------------------------------------------------

    //Remove single key in Preferences
    public void ClearSharedPreference(String key) {
        SharedPreferences sharedPref = mContext.getSharedPreferences(APP_PREFERENCES_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(key);
        editor.apply();
    } //ClearSharedPreference

    //Clear Temporary Preferences (CAT + ITEMS)
    public void ClearTempPreferences(){
        ClearSharedPreference(CATEGORY_PREFERENCE_KEY);
        ClearSharedPreference(LIST_ITEM_PREFERENCE_KEY);
    } //ClearTempPreferences

    //Clear all sharedPreferences
    public void ClearAllSharedPreferences() {
        ClearSharedPreference(CATEGORY_PREFERENCE_KEY);
        ClearSharedPreference(LIST_ITEM_PREFERENCE_KEY);
        ClearSharedPreference(USER_ID_PREFERENCE_KEY);
    } //Clearall

} //SharedPreferenceMethods
