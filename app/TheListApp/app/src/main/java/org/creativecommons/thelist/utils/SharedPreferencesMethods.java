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

    public SharedPreferencesMethods(Context mContext) {
        this.mContext = mContext;

    }

    //SharedPreferences Constants
    public static final String CATEGORY_PREFERENCE = "category";
    public static final String CATEGORY_PREFERENCE_KEY = "org.creativecommons.thelist.445329";
    public static final String LIST_ITEM_PREFERENCE = "item";
    public static final String LIST_ITEM_PREFERENCE_KEY = "org.creativecommons.thelist.348914";
    public static final String USER_ID_PREFERENCE = "id";
    public static final String USER_ID_PREFERENCE_KEY = "org.creativecommons.thelist.234958";

    //Add Array to SharedPreferences
    public static void SaveSharedPreference (String preferenceName, String key, String value, Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static JSONArray RetrieveSharedPreference (String preferenceName, String preferenceKey, Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        String value = sharedPref.getString(preferenceKey, null);

        //TODO: Switch to json library (JSONNNN)
        //JSONArray catIds = null;

//        try {
//            catIds = new JSONArray(value);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        //Convert from String to Array
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(value);
        JsonArray array = element.getAsJsonArray();
//
        //Make usable as JSONArray
        List<Integer> catIds = new ArrayList<Integer>();
        for (int i = 0; i < array.size(); i++) {
            catIds.add(array.get(i).getAsInt());
        }

        return new JSONArray(catIds);
    }
    //Get User ID from SharedPreferences
    public static String getUserId(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(USER_ID_PREFERENCE, Context.MODE_PRIVATE);
        String userID = sharedPref.getString(USER_ID_PREFERENCE_KEY, null);
        return userID;
    }

    public static void ClearSharedPreferences(String preferenceName, String preferenceKey, Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.apply();
    }

    //Create object to send in Category Volley Request
    public static JSONObject createCategoryListObject(String key, Context context) {
        //Create JSON Object
        JSONObject categoryListObject = new JSONObject();
        JSONArray userPreferences = RetrieveSharedPreference
                (CATEGORY_PREFERENCE,CATEGORY_PREFERENCE_KEY, context);

        try {
            categoryListObject.put(key, userPreferences);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
        return categoryListObject;
    }

    //Create object to send in User’s List Items Volley Request
    public static JSONObject createUserItemsObject (String key, Context context) {
        //Create JSON Object
        JSONObject userItemObject = new JSONObject();
        JSONArray userPreferences = RetrieveSharedPreference
                (LIST_ITEM_PREFERENCE,LIST_ITEM_PREFERENCE_KEY, context);

        try {
            userItemObject.put(key, userPreferences);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
        return userItemObject;
    }


    //Retrieve Shared preferences as JSONArray
    public static JSONArray RetrieveCategorySharedPreference (Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(CATEGORY_PREFERENCE, Context.MODE_PRIVATE);
        String value = sharedPref.getString(CATEGORY_PREFERENCE_KEY, null);

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
    public static JSONArray RetrieveUserItemPreference(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(LIST_ITEM_PREFERENCE, Context.MODE_PRIVATE);
        String value = sharedPref.getString(LIST_ITEM_PREFERENCE_KEY, null);

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
