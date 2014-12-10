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

package org.creativecommons.thelist.misc;

import android.content.Context;

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
