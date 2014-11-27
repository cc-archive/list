package org.creativecommons.thelist.misc;

import android.content.Context;

/**
 * Created by damaris on 2014-11-16.
 */
public class ListItemMethods {

    protected Context mContext;

    public ListItemMethods(Context mContext) {
        this.mContext = mContext;
    }

    //TODO: Add DialogListener

    //GET All ListItems
//    private void getAllListItems() {
//        RequestQueue queue = Volley.newRequestQueue(this);
//
//        //Genymotion Emulator
//        String url ="http://10.0.3.2:3000/api/item";
//        //Android Default Emulator
//        //String url = "http://10.0.2.2:3000/api/item";
//
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                            mItemData = response;
//                            updateList();
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse (VolleyError error){
//                requestMethods.updateDisplayForError();
//            }
//        });
//        queue.add(jsonObjectRequest);
//    }

}
