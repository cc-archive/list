package org.creativecommons.thelist.utils;

import android.app.NotificationManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Base64;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.creativecommons.thelist.adapters.ProgressBarState;
import org.creativecommons.thelist.adapters.UserListItem;

import java.util.HashMap;
import java.util.Map;

public class Uploader {

    private Context mContext;
    private ListUser mCurrentUser;
    private SharedPreferencesMethods mSharedPref;

    //Notifications
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;

    AsyncHttpClient client = new AsyncHttpClient();

    public Uploader(Context context){
        mContext = context;
        mCurrentUser = new ListUser(context);
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

    public void uploadPhoto(UserListItem listItem, Uri photoUri, NetworkUtils.RequestCallback callback){

        if(!(isNetworkAvailable())){
            callback.onCancelled(NetworkUtils.CancelResponse.NETWORK_ERROR);
            return;
        }

        if(FileHelper.getFileSize(photoUri) > 8){
            callback.onCancelled(NetworkUtils.CancelResponse.FILESIZE_ERROR);
            return;
        }

        mCurrentUser.getToken(new ListUser.AuthCallback() {
            @Override
            public void onAuthed(String authtoken) {

            }
        });

        //Add params
        RequestParams params = new RequestParams();

        //Format photo for upload
        byte [] image = FileHelper.getByteArrayFromFile(mContext, photoUri);
        image = FileHelper.reduceImageForUpload(image);
        String photoFile = new String(Base64.encode(image, Base64.DEFAULT));
        String url = ApiConstants.ADD_PHOTO + mSharedPref.getUserId() + "/" + listItem.getItemID();

        try{
            params.put("filedata", photoFile);
            params.put(ApiConstants.POST_PHOTO_KEY, photoFile);
            params.put(ApiConstants.USER_TOKEN, authtoken);

        } catch (Exception e){
            Log.e("UPLOADER", e.getMessage());
        }

        client.setTimeout(60 * 1000);

        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    public void uploadPhoto(final UserListItem listItem, final Uri photoUri, final RequestMethods.RequestCallback callback) {



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

        mCurrentUser.getToken(new ListUser.AuthCallback() {
            @Override
            public void onAuthed(final String authtoken) {
                RequestQueue queue = Volley.newRequestQueue(mContext);
                final String photoFile = FileHelper.createUploadPhotoObject(mContext, photoUri);
                String url = ApiConstants.ADD_PHOTO + mSharedPref.getUserId() + "/" + listItem.getItemID();

                state.mState = ProgressBarState.State.START;

                //Upload Request
                final StringReq uploadPhotoRequest = new StringReq(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                //Get Response
                                Log.v(TAG, "uploadPhoto > onResponse: " + response);
                                state.mState = ProgressBarState.State.FINISHED;

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        mBuilder.setContentText(listItem.getItemName() + " uploaded successfully");
                                        // Removes the progress bar
                                        mBuilder.setProgress(0, 0, false);
                                        mNotifyManager.notify(notificationID, mBuilder.build());
                                    }
                                }, 1000);

                                callback.onSuccess();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "uploadPhoto > onErrorResponse: " + error.getMessage());

                        state.mState = ProgressBarState.State.ERROR;

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mBuilder.setContentText("“" + listItem.getItemName() + "”" + " failed to upload");
                                // Removes the progress bar
                                mBuilder.setProgress(0, 0, false);
                                mNotifyManager.notify(notificationID, mBuilder.build());
                            }
                        }, 1000);

                        callback.onFail();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();

                        byte [] image = FileHelper.getByteArrayFromFile(mContext, photoUri);
                        image = FileHelper.reduceImageForUpload(image);

                        String photoFile = new String(Base64.encode(image, Base64.DEFAULT));


                        params.put(ApiConstants.POST_PHOTO_KEY, photoFile);
                        params.put(ApiConstants.USER_TOKEN, authtoken);
                        return params;
                    }
                };

                uploadPhotoRequest.setRetryPolicy(new DefaultRetryPolicy
                        (DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 12, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                queue.add(uploadPhotoRequest);
                state.mState = ProgressBarState.State.REQUEST_SENT;
            }
        });

    } //uploadPhoto

}
