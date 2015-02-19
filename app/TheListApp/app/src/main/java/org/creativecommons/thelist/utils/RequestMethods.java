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

import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import org.creativecommons.thelist.adapters.MainListItem;

import java.util.ArrayList;
import java.util.List;

public final class RequestMethods {
    //TODO: probably make this obsolete
    public static final String TAG = RequestMethods.class.getSimpleName();
    protected Context mContext;
    protected AlertDialog mAlertDialog;

    //Set Context
    public RequestMethods(Context mc) {
        mContext = mc;

    }

    //Check if thar be internets
    public boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) mContext.getSystemService(mContext.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = false;
        if(networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }

    //Generic Error Dialog Builder
    public void showDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(title)
               .setMessage(message)
               .setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    } //showDialog

    //Parse List Objects of List Items and return list of Item IDS
    public List<String> getItemIds(List<MainListItem> list){
        List<String>arrayList = new ArrayList<String>();
        for (int i = 0; i < list.size(); i++) {
            String singleID = list.get(i).getItemID();
            arrayList.add(singleID);
        }
        return arrayList;
    } //getItemIds

    //Create Upload Photo Object (in bytes) + return object with ID and userID
    public String createUploadPhotoObject(Uri uri) {
        //Convert photo file to Base64 encoded string
        String fileString = FileHelper.getByteArrayFromFile(mContext, uri);
        return fileString;
    }
} //RequestMethods
