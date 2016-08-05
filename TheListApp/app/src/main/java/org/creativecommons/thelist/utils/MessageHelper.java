/* The List powered by Creative Commons

   Copyright (C) 2014, 2015 Creative Commons Corporation

   This program is free software: you can redistribute it and/or modify
   it under the terms of either the GNU Affero General Public License or
   the GNU General Public License as published by the
   Free Software Foundation, either version 3 of the Licenses, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  

   You should have received a copy of the GNU General Public License and
   the GNU Affero General Public License along with this program.  

   If not, see <http://www.gnu.org/licenses/>.

*/

package org.creativecommons.thelist.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.VolleyError;

import org.creativecommons.thelist.R;
import org.creativecommons.thelist.activities.HomeActivity;
import org.creativecommons.thelist.activities.StartActivity;
import org.creativecommons.thelist.api.NetworkUtils;
import org.creativecommons.thelist.api.RequestMethods;
import org.json.JSONArray;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class MessageHelper {
    public static final String TAG = RequestMethods.class.getSimpleName();

    private Context mContext;
    private Resources res;

    //Make Users feel special
    private String [] gratitudeMessages;
    private Random random = new Random();

    public static final Integer[] MESSAGE_INTERVALS = new Integer[]{5, 10, 15, 20, 25};

    //Notifications
    final AtomicInteger notificationID = new AtomicInteger(0);
    public static final String INTENT_ACTION = "OPEN_GALLERY";

    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;

    //Set Context
    public MessageHelper(Context mc) {
        mContext = mc;

        //Gratitude Message Strings
        res = mContext.getResources();
        gratitudeMessages = res.getStringArray(R.array.gratitude_messages);

        //Notifications
        mNotifyManager = (NotificationManager) mc.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    // --------------------------------------------------------
    // HELPERS
    // --------------------------------------------------------

    public static String capitalize(final String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }

    // --------------------------------------------------------
    // DIALOGS GENERIC
    // --------------------------------------------------------

    //Generic Dialog
    public void showDialog(Context context, String title, String message){
        new MaterialDialog.Builder(context)
                .title(title)
                .content(message)
                .positiveText(R.string.ok_label)
                 //.negativeText(R.string.disagree)
                .show();
    }

    //Enable Feature Dialog
    public void enableFeatureDialog(Context context, String title, String message,
                                    MaterialDialog.ButtonCallback callback){
                    new MaterialDialog.Builder(context)
                            .title(title)
                            .content(message)
                            .positiveText(context.getString(R.string.general_positive_text))
                            .autoDismiss(false)
                            .negativeText(context.getString(R.string.general_negative_text))
                            .callback(callback)
                            .show();
    } //enableFeatureDialog

    //Take Survey Dialog
    public void takeSurveyDialog(Context context, String title, String message,
                                 MaterialDialog.ButtonCallback callback){
                    new MaterialDialog.Builder(context)
                            .title(title)
                            .content(message)
                            .positiveText("I’ll help out")
                            .negativeText("I’ll pass")
                            //.autoDismiss(false)
                            .callback(callback)
                            .show();
    } //takeSurveyDialog

    //Single Input Dialog
    public void singleInputDialog
    (Context context, String title, String message, MaterialDialog.InputCallback callback) {
        new MaterialDialog.Builder(context)
                .title(title)
                .content(message)
                .input("add a url", "", callback)
                .show();
    } //Single Input Dialog

    //Single Choice Dialog
    public void showSingleChoiceDialog(Context context, String title, String content, String[] items,
                                       MaterialDialog.ListCallbackSingleChoice callback){
        new MaterialDialog.Builder(context)
                .title(title)
                .content(content)
                .items(items)
                .itemsCallbackSingleChoice(-1, callback)
                .positiveText(context.getString(R.string.single_choice_text))
                .show();
    } //showSingleChoiceDialog

    // --------------------------------------------------------
    // NOTIFICATIONS GENERIC
    // --------------------------------------------------------

    //Send Android System Notifications
    public void sendNotification(Context context, String title, String message, String ticker){
        //Bitmap largeIcon = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_launcher);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder (context)
                .setSmallIcon(R.drawable.ic_camera_alt_white_24dp)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setTicker(ticker);

        Intent resultIntent = new Intent(mContext, HomeActivity.class);
        resultIntent.setAction(INTENT_ACTION);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        android.support.v4.app.TaskStackBuilder stackBuilder = android.support.v4.app.TaskStackBuilder.create(mContext);
        stackBuilder.addParentStack(StartActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_CANCEL_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(getNotificationID(), mBuilder.build());
    }

    //Create unique notification ID
    public int getNotificationID(){
        return notificationID.incrementAndGet();
    }

    // --------------------------------------------------------
    // GENERAL MESSAGES
    // --------------------------------------------------------

    //TODO: replace frequently used messages w/ pre-created showDialogs
    public void networkFailMessage(){
        Toast.makeText(mContext,
                mContext.getString(R.string.error_network_message), Toast.LENGTH_SHORT).show();
//        showDialog(mContext, mContext.getString(R.string.error_network_title),
//                mContext.getString(R.string.error_network_message));
    }

    public void loadUserItemsFailMessage(){
        showDialog(mContext, mContext.getString(R.string.error_network_title),
                mContext.getString(R.string.error_load_message));
    }


    // --------------------------------------------------------
    // LIST ITEM MESSAGES
    // --------------------------------------------------------

    public void noItemsFound(){
        showDialog(mContext,"Oops!",
                "We couldn’t find any items for you");
        //used in RandomActivity
    }

    // --------------------------------------------------------
    // GALLERY MESSAGES
    // --------------------------------------------------------

    public void galleryNetworkFailMessage(){
        showDialog(mContext, mContext.getString(R.string.upload_failed_title_network),
                "The gallery is only available online");
    }

    // --------------------------------------------------------
    //  CATEGORY MESSAGES
    // --------------------------------------------------------

    public void categoryAddFail(){
        Toast.makeText(mContext,mContext.getString(R.string.add_category_toast_error),
                Toast.LENGTH_SHORT).show();
    }

    public void categoryRemoveFail(){
        Toast.makeText(mContext,mContext.getString(R.string.remove_category_toast_error),
                Toast.LENGTH_SHORT).show();
    }


    // --------------------------------------------------------
    // PHOTO UPLOAD MESSAGES
    // --------------------------------------------------------

    //NOTIFICATIONS

//    public void createUploadNotification(){
//
//        //Start notification
//        mNotifyManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
//        mBuilder = new NotificationCompat.Builder(mContext);
//        mBuilder.setContentTitle("Download")
//                .setContentText("Download in progress")
//                .setSmallIcon(R.drawable.ic_camera_alt_white_24dp);
//    }


    public void notifyUploadSuccess(String itemName){
        sendNotification(mContext, "The List",
                itemName + " has been uploaded successfully!",
                itemName + " uploaded successfully");
    }

    public void notifyUploadFail(String itemName){
        sendNotification(mContext, "The List",
                "There was a problem uploading" + itemName ,
                itemName + " upload failed");
    }

    public void notifyMakerItemUploadSuccess(String itemName){

    }

    public void notifyMakerItemUploadFail(String itemName){

    }

    //DIALOGS
    public void photoUploadNetworkFailMessage(){
        showDialog(mContext, mContext.getString(R.string.upload_failed_title_network),
                mContext.getString(R.string.upload_failed_text_network));
    }

    public void photoUploadSizeFailMessage(){
        showDialog(mContext, mContext.getString(R.string.upload_failed_title_filesize),
                mContext.getString(R.string.upload_failed_text_filesize));
    }

    public void photoUploadFileTypeFailMessage(){
        showDialog(mContext, "Are you sure this is a jpeg?", "Currently The List only accepts " +
                "jpeg images. Try converting your photo or uploading a different image!");
    }

    public void photoFailMessage(){

    }

    //TOASTS
    public void toastNeedInternet(){
        Toast.makeText(mContext, "You need internet to access this feature",
                Toast.LENGTH_SHORT).show();
    }

    // --------------------------------------------------------
    // MAKE USERS FEEL SPECIAL MESSAGES
    // --------------------------------------------------------

    public void makerUsersFeelAllSpecialAndWhatNot(int count){
        String title = String.format(res.getString(R.string.gratitude_title), String.valueOf(count));

        int randomIndex = random.nextInt(gratitudeMessages.length);
        String message = (gratitudeMessages[randomIndex]);

        showDialog(mContext, title, message);
    }


    //User Messaging
    public void getUserMessaging(){
        RequestMethods mRequestMethods = new RequestMethods(mContext);

        //make request and do something based on user stats
        mRequestMethods.getUserProfile(new NetworkUtils.ResponseCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                //TODO: check if message should be displayed based on user stats
                //what are the rules for this? Only 1 message per “run” of this? What is the priority level of each type of message?
                //For now: achievement gets top priority (uploadCount)

                //TODO: get upload count
                int uploadCount = response.length();
                Log.v(TAG, "USER UPLOAD COUNT: " + String.valueOf(uploadCount));

                if (Arrays.asList(MESSAGE_INTERVALS).contains(uploadCount)) {
                    makerUsersFeelAllSpecialAndWhatNot(uploadCount);

                    return;
                }

                //TODO: include other values in the check:

            }

            @Override
            public void onFail(VolleyError error) {

            }
        });
    } //userMessaging

} //MessageHelper
