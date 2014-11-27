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

/**
 * Created by damaris on 2014-11-18.
 */
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

    //TODO:Methods for adding extracting array

    //Add Array to SharedPreferences
    public static void SaveSharedPreference (String preferenceName, String key, String value, Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static JSONArray RetrieveSharedPreference (String preferenceName, String preferenceKey, Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        String value = sharedPref.getString(preferenceKey, null);

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

    //Create object to send in Category Volley Request
    public JSONObject createCategoryListObject(String key, Context context) {
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
    public JSONObject createUserItemsObject (String key, Context context) {
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

}
