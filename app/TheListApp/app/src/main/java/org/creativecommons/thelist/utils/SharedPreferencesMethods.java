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

package org.creativecommons.thelist.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SharedPreferencesMethods {
    public static final String TAG = SharedPreferencesMethods.class.getSimpleName();

    protected Context mContext;

    public SharedPreferencesMethods(Context context) {
        mContext = context;
    }

    //SharedPreferences Constants
    public static final String CATEGORY_PREFERENCE = "category";
    //public static final String CATEGORY_PREFERENCE_KEY = "org.creativecommons.thelist.445329";
    public static final String LIST_ITEM_PREFERENCE = "item";
    //public static final String LIST_ITEM_PREFERENCE_KEY = "org.creativecommons.thelist.348914";
    public static final String USER_ID_PREFERENCE = "id";
    //public static final String USER_ID_PREFERENCE_KEY = "org.creativecommons.thelist.234958";

    public static final String APP_PREFERENCES_KEY = "org.creativecommons.thelist.434932";

    //Add username, id, session token, category preferences, user item preferences

    //Add any sharedPreference
    public void SaveSharedPreference (String key, String value){
        SharedPreferences sharedPref = mContext.getSharedPreferences(APP_PREFERENCES_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    //RetrieveSharedPreference
    public JSONArray RetrieveSharedPreference (String key){
        SharedPreferences sharedPref = mContext.getSharedPreferences(APP_PREFERENCES_KEY, Context.MODE_PRIVATE);
        String value = sharedPref.getString(key, null);

        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(value);
        JsonArray array = element.getAsJsonArray();

        //Make usable as JSONArray
        List<String> catIds = new ArrayList<String>();
        for (int i = 0; i < array.size(); i++) {
            catIds.add(array.get(i).getAsString());
        }

        return new JSONArray(catIds);
    }

    //Get User ID from SharedPreferences
    public String getUserId(){
        SharedPreferences sharedPref = mContext.getSharedPreferences(APP_PREFERENCES_KEY, Context.MODE_PRIVATE);
        String userID = sharedPref.getString(USER_ID_PREFERENCE, null);
        return userID;
    }

    //Remove single key in Preferences
    public void ClearSharedPreference(String key) {
        SharedPreferences sharedPref = mContext.getSharedPreferences(APP_PREFERENCES_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(key);
        editor.apply();
    }

    //Clear all sharedPreferences
    //TODO: add other keys like session token
    public void ClearAllSharedPreferences() {
        ClearSharedPreference(USER_ID_PREFERENCE);
        ClearSharedPreference(CATEGORY_PREFERENCE);
        ClearSharedPreference(LIST_ITEM_PREFERENCE);
    }

    //Create object to send in Category Volley Request
    public JSONObject createCategoryListObject() {
        //Create JSON Object
        JSONObject categoryListObject = new JSONObject();
        JSONArray userPreferences = RetrieveSharedPreference
                (CATEGORY_PREFERENCE);

        try {
            categoryListObject.put(CATEGORY_PREFERENCE, userPreferences);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
        return categoryListObject;
    }

    //Create object to send in User’s List Items Volley Request
    public JSONObject createUserItemsObject () {
        //Create JSON Object
        //TODO: remove this with real API
        JSONObject userItemObject = new JSONObject();
        JSONArray userPreferences = RetrieveSharedPreference
                (LIST_ITEM_PREFERENCE);
        JSONArray intPreferences = new JSONArray();

        for(int i = 0; i <userPreferences.length(); i++ ) {

            try {
                int item = Integer.valueOf(userPreferences.getString(i));
                intPreferences.put(i,item);

            } catch (JSONException e) {
                Log.v(TAG, e.getMessage());
            }
        }

        try {
            Log.v(TAG, intPreferences.toString());
            userItemObject.put(LIST_ITEM_PREFERENCE, intPreferences);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return userItemObject;
    }

    //Retrieve Shared preferences as JSONArray
    public JSONArray RetrieveCategorySharedPreference (){
        SharedPreferences sharedPref = mContext.getSharedPreferences(APP_PREFERENCES_KEY, Context.MODE_PRIVATE);
        String value = sharedPref.getString(CATEGORY_PREFERENCE, null);

        //TODO: Switch to json library (JSONNNN)
        //Convert from String to Array
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(value);
        JsonArray array = element.getAsJsonArray();

        //Make usable as JSONArray
        List<Integer> catIds = new ArrayList<Integer>();
        for (int i = 0; i < array.size(); i++) {
            catIds.add(array.get(i).getAsInt());
        }

        return new JSONArray(catIds);
    }

    //Retrieve User Item Preference
    public JSONArray RetrieveUserItemPreference() {
        SharedPreferences sharedPref = mContext.getSharedPreferences(APP_PREFERENCES_KEY, Context.MODE_PRIVATE);
        String value = sharedPref.getString(LIST_ITEM_PREFERENCE, null);

        if(value == null) {
            return null;
        } else {
            //TODO: Switch to json library (JSONNNN)
            //Convert from String to Array
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(value);
            JsonArray array = element.getAsJsonArray();

            //Make usable as JSONArray
            List<Integer> itemIds = new ArrayList<Integer>();
            for (int i = 0; i < array.size(); i++) {
                itemIds.add(array.get(i).getAsInt());
            }
            return new JSONArray(itemIds);
        }
    }

}
