package org.creativecommons.thelist.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Base64;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.creativecommons.thelist.R;
import org.creativecommons.thelist.activities.HomeActivity;
import org.creativecommons.thelist.activities.StartActivity;
import org.creativecommons.thelist.adapters.UserListItem;
import org.creativecommons.thelist.api.NetworkUtils;

public class Uploader {
    public static final String TAG = Uploader.class.getSimpleName();
    private Context mContext;

    private ListUser mCurrentUser;
    private MessageHelper mMessageHelper;
    private SharedPreferencesMethods mSharedPref;

    //Notifications
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;

    AsyncHttpClient client = new AsyncHttpClient();

    public Uploader(Context context){
        mContext = context;
        mCurrentUser = new ListUser(context);
        mMessageHelper = new MessageHelper(context);
        mSharedPref = new SharedPreferencesMethods(context);

        //Notifications
        mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(context);
    }


    // --------------------------------------------------------
    // NETWORK CHECKS
    // --------------------------------------------------------

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

    // --------------------------------------------------------
    // LIST ITEM PHOTO UPLOAD
    // --------------------------------------------------------

    public void uploadPhoto(final UserListItem listItem, final Uri photoUri, final NetworkUtils.UploadResponse callback){

        if(!(isNetworkAvailable())){
            callback.onCancelled(NetworkUtils.CancelResponse.NETWORK_ERROR);
            return;
        }

        if(FileHelper.getFileSize(photoUri) > 8){
            callback.onCancelled(NetworkUtils.CancelResponse.FILESIZE_ERROR);
            return;
        }

//        if(!FileHelper.getFileType(mContext, photoUri).equals(PhotoConstants.FILE_TYPE)){
//            mMessageHelper.photoUploadFileTypeFailMessage();
//            return;
//        }

        //Start notification
        final int notificationID = mMessageHelper.getNotificationID();
        startUploadNotification(notificationID, "“" + listItem.getItemName() + "”" + " is uploading…");

        //Set up progress bar updater
        final ProgressBarState state = new ProgressBarState();
        final AsyncTask updater =  new ProgressBarUpdater(notificationID).execute(state);

        mCurrentUser.getToken(new ListUser.TokenCallback() {
            @Override
            public void onAuthed(String authtoken) {
                String url = ApiConstants.ADD_PHOTO + mSharedPref.getUserId() + "/" + listItem.getItemID();

                //Set start state for uploader
                state.mState = ProgressBarState.State.START;

                //PARAMETERS
                RequestParams params = new RequestParams();

                //Format photo for upload
                byte[] image = FileHelper.getByteArrayFromFile(mContext, photoUri);
                image = FileHelper.reduceImageForUpload(image);
                String photoFile = new String(Base64.encode(image, Base64.DEFAULT));

                try {
                    params.put(ApiConstants.POST_PHOTO_KEY, photoFile);
                    params.put(ApiConstants.USER_TOKEN, authtoken);

                } catch (Exception e) {
                    Log.e("UPLOADER", e.getMessage());
                }

                //REQUEST
                client.setTimeout(60 * 1000);
                client.post(url, params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onStart() {
                        // called before request is started
                        state.mState = ProgressBarState.State.REQUEST_SENT;
                    }

                    public void onPreProcessResponse(ResponseHandlerInterface instance, HttpResponse response) {
                        // default action is to do nothing...

                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Log.v(TAG, "uploadPhoto > onSuccess: " + String.valueOf(statusCode));
                        state.mState = ProgressBarState.State.FINISHED;

                        updateNotificationSuccess(notificationID, listItem.getItemName() + " uploaded successfully");
                        callback.onSuccess();

                    } //onSuccess

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Log.d(TAG, "uploadPhoto > onFailure: " + String.valueOf(statusCode) + ", " + error.getMessage());
                        state.mState = ProgressBarState.State.ERROR;

                        updateNotificationFailure(notificationID, "“" + listItem.getItemName() + "”" + " failed to upload");
                        callback.onFail();
                    } //on Failure

                    public void onCancel() {
                        Log.d(TAG, "Request got cancelled");
                    }

                });
            } //onAuthed
        });
    } //uploadPhoto


    // --------------------------------------------------------
    // MAKER ITEM UPLOAD
    // --------------------------------------------------------

    public void addMakerItem(final String title, final String category, final String description,
                             final Uri photoUri, final NetworkUtils.RequestCallback callback){

        if(!(isNetworkAvailable())){
            callback.onCancelled(NetworkUtils.CancelResponse.NETWORK_ERROR);
            return;
        }

        //If photo is attached, check file size
        if(photoUri != null){
            if(FileHelper.getFileSize(photoUri) > 8){
                callback.onCancelled(NetworkUtils.CancelResponse.FILESIZE_ERROR);
                return;
            }
        }

        //Start notification
        final int notificationID = mMessageHelper.getNotificationID();
        startUploadNotification(notificationID, "“" + MessageHelper.capitalize(title) + "”" + " is uploading…");

        //Set up progress bar updater
        final ProgressBarState state = new ProgressBarState();
        final AsyncTask updater =  new ProgressBarUpdater(notificationID).execute(state);

        mCurrentUser.getToken(new ListUser.TokenCallback() {
            @Override
            public void onAuthed(String authtoken) {
                String url = ApiConstants.ADD_MAKER_ITEM + '/' + mSharedPref.getUserId();

                //Set start state for uploader
                state.mState = ProgressBarState.State.START;

                //PARAMETERS
                RequestParams params = new RequestParams();

                try {

                    params.put(ApiConstants.USER_TOKEN, authtoken);
                    params.put(ApiConstants.MAKER_ITEM_CATEGORY, category);

                    if (description != null) {
                        params.put(ApiConstants.MAKER_ITEM_DESCRIPTION, description);
                    }

                    if (photoUri != null) {
                        //Format photo for upload
                        byte[] image = FileHelper.getByteArrayFromFile(mContext, photoUri);
                        image = FileHelper.reduceImageForUpload(image);
                        String photoFile = new String(Base64.encode(image, Base64.DEFAULT));
                        params.put(ApiConstants.POST_PHOTO_KEY, photoFile);
                    }

                } catch (Exception e) {
                    Log.e("UPLOADER", e.getMessage());
                }

                //REQUEST
                client.setTimeout(60 * 1000);
                client.post(url, params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onStart() {
                        // called before request is started
                        state.mState = ProgressBarState.State.REQUEST_SENT;
                    }

                    public void onPreProcessResponse(ResponseHandlerInterface instance, HttpResponse response) {
                        // default action is to do nothing...

                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Log.v(TAG, "uploadPhoto > onSuccess: " + String.valueOf(statusCode));
                        state.mState = ProgressBarState.State.FINISHED;

                        updateNotificationSuccess(notificationID, "“" + title + "”" + " uploaded successfully");
                        callback.onSuccess();
                    } //onSuccess

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Log.d(TAG, "uploadPhoto > onFailure: " + String.valueOf(statusCode) + ", " + error.getMessage());
                        state.mState = ProgressBarState.State.ERROR;

                        updateNotificationFailure(notificationID, "“" + MessageHelper.capitalize(title) + "”" + " failed to upload");
                        callback.onFail();
                    } //on Failure

                    public void onCancel() {
                        Log.d(TAG, "Request got cancelled");

                    }
                });
            }
        });
    } //addMakerItem


    // --------------------------------------------------------
    // UPLOAD NOTIFICATION HELPERS
    // --------------------------------------------------------

    public void startUploadNotification(int notificationID, String contentText){
        mBuilder = new NotificationCompat.Builder(mContext);
        mBuilder.setContentTitle(mContext.getResources().getString(R.string.app_name_short))
                .setContentText(contentText)
                .setColor(mContext.getResources().getColor(R.color.colorSecondary))
                .setSmallIcon(R.drawable.ic_party_mode_white_24dp);

        Intent resultIntent = new Intent(mContext, HomeActivity.class);
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

        NotificationManager mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(notificationID, mBuilder.build());

    }

    public void updateNotificationSuccess(final int id, final String contentText) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mBuilder.setContentText(contentText);
                // Removes the progress bar
                mBuilder.setProgress(0, 0, false);
                mBuilder.setSmallIcon(R.drawable.ic_done_white_24dp);

                mNotifyManager.notify(id, mBuilder.build());
            }
        }, 1000);

    }

    public void updateNotificationFailure(final int id, final String contentText) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mBuilder.setContentText(contentText);
                // Removes the progress bar
                mBuilder.setProgress(0, 0, false);
                mBuilder.setSmallIcon(R.drawable.ic_report_problem_white_24dp);
                mBuilder.setColor(mContext.getResources().getColor(R.color.colorFail));

                mNotifyManager.notify(id, mBuilder.build());
            }
        }, 1000);

    }

    //PROGRESS BAR UPDATER
    public class ProgressBarUpdater extends AsyncTask<Object, Integer, Integer> {

        private ProgressBarState state;
        private int notificationID;

        public ProgressBarUpdater(int id){
            super();
            notificationID = id;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // Displays the progress bar for the first time.
            mBuilder.setProgress(100, 0, false);
            mNotifyManager.notify(notificationID, mBuilder.build());
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            // Update progress
            mBuilder.setProgress(100, values[0], false);
            mNotifyManager.notify(notificationID, mBuilder.build());
            super.onProgressUpdate(values);
        }

        @Override
        protected Integer doInBackground(Object... params) {
            state = (ProgressBarState) params[0];

            int currentVal = 0;
            int targetVal = 0;

            while(true){
                switch(state.mState){
                    case START:
                        targetVal = 30;

                        break;
                    case REQUEST_SENT:
                        targetVal = 85;

                        break;
                    case REQUEST_RECEIVED:
                        targetVal = 90;

                        break;
                    case FINISHED:
                        targetVal = 100;

                        break;
                    case ERROR:
                        cancel(true);
                        break;
                    case TIMEOUT:
                        cancel(true);
                        break;
                }

                if(currentVal < targetVal){
                    currentVal++;

//                    TODO: can I make indeterminate display until upload complete?
//                    if(currentVal > 80){
//                        mBuilder.setProgress(0, 0, true);
//                    }

                    if(currentVal == 100 || state.mState == ProgressBarState.State.FINISHED){
                        currentVal = 100;
                        publishProgress(currentVal, 100);
                        cancel(true);
                    }
                }

                // Sets the progress indicator completion percentage
                publishProgress(currentVal, 100);

                try {
                    // Sleep for 1 seconds
                    Thread.sleep(80);
                } catch (InterruptedException e) {
                    Log.d("ProgressBarUpdater", "sleep failure");
                }

                if(isCancelled()){
                    break;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
        }
    } //Progress Bar Updater

} //Uploader
