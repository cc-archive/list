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

package org.creativecommons.thelist.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.creativecommons.thelist.R;
import org.creativecommons.thelist.activities.MainActivity;
import org.creativecommons.thelist.activities.StartActivity;

import java.util.HashMap;
import java.util.Map;

public final class RequestMethods {
    public static final String TAG = RequestMethods.class.getSimpleName();
    protected Context mContext;
    protected MessageHelper mMessageHelper;
    protected SharedPreferencesMethods mSharedPref;
    protected ListUser mCurrentUser;

    public RequestMethods(Context mc) {
        mContext = mc;
        mMessageHelper = new MessageHelper(mc);
        mSharedPref = new SharedPreferencesMethods(mc);
        mCurrentUser = new ListUser(mc);
    }

    //Callback for requests
    public interface RequestCallback {
        void onSuccess();
        void onFail();
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

    // --------------------------------------------------------
    // USER LIST REQUESTS
    // --------------------------------------------------------



    // --------------------------------------------------------
    // USER PHOTO REQUESTS
    // --------------------------------------------------------

    public void uploadPhoto(String itemID, Uri photoUri, final RequestCallback callback) {

        if(!(isNetworkAvailable())){
            mMessageHelper.showDialog(mContext, mContext.getString(R.string.error_network_title),
                    mContext.getString(R.string.error_network_message));
            return;
        }

        if(FileHelper.getFileSize(photoUri) > 8){
            mMessageHelper.showDialog(mContext,
                    mContext.getString(R.string.upload_failed_title_filesize),
                    mContext.getString(R.string.upload_failed_text_filesize));
            return;
        }

        //Get Photo as Base64 encoded String
        final String photoFile = FileHelper.createUploadPhotoObject(mContext, photoUri);
        final String url = ApiConstants.ADD_PHOTO + mSharedPref.getUserId() + "/" + itemID;

        mCurrentUser.getToken(new ListUser.AuthCallback() {
            @Override
            public void onSuccess(final String authtoken) {
                RequestQueue queue = Volley.newRequestQueue(mContext);

                //Upload Request
                StringRequest uploadPhotoRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                //Get Response
                                Log.v(TAG, "uploadPhoto > onResponse: " + response);
                                //TODO: add conditions? What happens when photo upload fails?

                                mMessageHelper.notifyUploadSuccess();
                                //Send notice to activity (will execute timed close of this fragment)
                                callback.onSuccess();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "uploadPhoto > onErrorResponse: " + error.getMessage());
                        //TODO: add switch for all possible error codes
                        mMessageHelper.notifyUploadFail();
                        callback.onFail();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put(ApiConstants.POST_PHOTO_KEY, photoFile);
                        params.put(ApiConstants.USER_TOKEN, authtoken);
                        return params;
                    }
                };
                queue.add(uploadPhotoRequest);
            }
        });
    } //uploadPhoto

} //RequestMethods
