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

package org.creativecommons.thelist.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.creativecommons.thelist.R;
import org.creativecommons.thelist.fragments.MyListFragment;
import org.creativecommons.thelist.utils.ApiConstants;
import org.creativecommons.thelist.utils.MessageHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainListItem {
    private String itemName, makerName, itemID, categoryID;
    private boolean error, progress;
    private Context mContext;
    private Activity mActivity;
    private MyListFragment myListFragment;
    private MessageHelper mMessageHelper;
    public boolean completed = false;

    public MainListItem() {
    }

    public MainListItem(String id, String name, String maker) {
        this.itemID = itemID;
        this.itemName = name;
        this.makerName = maker;
        this.error =  false;
        this.progress = false;
    }

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String id) {
        this.itemID = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String name) {
        this.itemName = name;
    }

    public String getMakerName() {
        return makerName;
    }

    public void setMakerName(String maker) {
        this.makerName = maker;
    }

    public boolean getError(){
        return error;
    }

    public void setError(boolean bol){
        this.error = bol;
    }

    public boolean getProgress(){
        return progress;
    }

    public void setProgress(boolean bol){
        this.progress = bol;
    }

    public String getCategoryID(){
        return categoryID;

    }

    public void setCategoryID(String id){
        categoryID = id;
    }

    public void setContext(Context c) {
        mContext = c;
    }

    public void setMessageHelper(MessageHelper mh){
        mMessageHelper = mh;
    }

    public void setMyListFragment(MyListFragment m){
        myListFragment = m;
    }

    public void setMainListActivity(Activity a) {
        mActivity = a;
        mContext = a;
    }

    public void createNewUserListItem() {
        RequestQueue queue = Volley.newRequestQueue(mContext);
        String itemRequesturl = ApiConstants.GET_SINGLE_ITEM + String.valueOf(itemID);

        JsonArrayRequest newUserListRequest = new JsonArrayRequest(itemRequesturl,
            new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    //Handle Data
                    try {
                        //Log.v("CREATE ITEM RESPONSE", response.toString());
                        JSONObject jsonObject = response.getJSONObject(0);
                        setItemName(MyListFragment.capitalize(jsonObject.getString(ApiConstants.ITEM_NAME)));
                        setMakerName(jsonObject.getString(ApiConstants.MAKER_NAME));
                        setItemID(String.valueOf(jsonObject.getInt(ApiConstants.ITEM_ID)));
                        Log.v("ITEM ADDED NAME: ", getItemName());
                        completed = true;
                        myListFragment.CheckComplete();
                    } catch (JSONException e) {
                        Log.v("MainListItem", e.getMessage());
                    }

                }
            }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error", error.toString());
                mMessageHelper.showDialog(mContext,mContext.getString(R.string.error_title),
                        mContext.getString(R.string.error_message));
            }
        });
        queue.add(newUserListRequest);
    }

} //MainListItem
