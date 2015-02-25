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
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.afollestad.materialdialogs.MaterialDialog;

import org.creativecommons.thelist.R;
import org.creativecommons.thelist.activities.MainActivity;
import org.creativecommons.thelist.activities.StartActivity;
import org.creativecommons.thelist.adapters.MainListItem;

import java.util.ArrayList;
import java.util.List;

public final class RequestMethods {
    //TODO: probably make this obsolete
    public static final String TAG = RequestMethods.class.getSimpleName();
    protected Context mContext;

    //Set Context
    public RequestMethods(Context mc) {
        mContext = mc;
    }

    //Check if thar be internets
    public boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = false;
        if(networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }

    //Material Design Dialog
    public void showMaterialDialog(Context context, String title, String message){
        new MaterialDialog.Builder(mContext)
                .title(title)
                .content(message)
                .positiveText(R.string.ok_label)
                //.negativeText(R.string.disagree)
                .show();
    }

    //Generic Error Dialog Builder
    public void showDialog(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
               .setMessage(message)
               .setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    } //showDialog

    //Send Android System Notifications
    public void sendNotification(Context context, String title, String message, String ticker, Integer id){
        //Bitmap largeIcon = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_launcher);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder (context)
                .setSmallIcon(R.drawable.ic_camera_alt_white_24dp)
                .setContentTitle(title)
                .setContentText(message)
                .setTicker(ticker);

        Intent resultIntent = new Intent(mContext, MainActivity.class);
        android.support.v4.app.TaskStackBuilder stackBuilder = android.support.v4.app.TaskStackBuilder.create(mContext);
        stackBuilder.addParentStack(StartActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(id, mBuilder.build());
    }

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
