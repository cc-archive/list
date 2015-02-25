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

package org.creativecommons.thelist.fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.creativecommons.thelist.R;
import org.creativecommons.thelist.utils.ApiConstants;
import org.creativecommons.thelist.utils.ListUser;
import org.creativecommons.thelist.utils.RequestMethods;
import org.creativecommons.thelist.utils.SharedPreferencesMethods;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class UploadFragment extends Fragment {
    public static final String TAG = UploadFragment.class.getSimpleName();
    RequestMethods requestMethods;
    SharedPreferencesMethods sharedPreferencesMethods;
    ListUser mCurrentUser;
    Context mContext;

    protected TextView mTitle;
    protected TextView mText;
    protected ImageView mIcon;

    protected RelativeLayout mProgressLayout;
    protected RelativeLayout mResultMessage;

    //Interface with Activity
    UploadListener mCallback;

    public interface UploadListener {
        public void onUploadSuccess();
        public void onUploadFail();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Google Analytics Tracker
//        Tracker t = ((ListApplication) getActivity().getApplication()).getTracker(
//                ListApplication.TrackerName.GLOBAL_TRACKER);
//
//        t.setScreenName("Confirm Fragment");
//        t.send(new HitBuilders.AppViewBuilder().build());

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upload, container, false);
    } //onCreateView

    @Override
    public void onResume() {
        super.onResume();

        mContext = getActivity();
        sharedPreferencesMethods = new SharedPreferencesMethods(mContext);
        requestMethods = new RequestMethods(mContext);
        mCurrentUser = new ListUser(getActivity());

        mProgressLayout = (RelativeLayout)getView().findViewById(R.id.upload_loading);
        mResultMessage = (RelativeLayout)getView().findViewById(R.id.result_message);

        mTitle = (TextView)getView().findViewById(R.id.account_title);
        mText = (TextView)getView().findViewById(R.id.account_text);
        mIcon = (ImageView)getView().findViewById(R.id.confirm_icon);

        if(requestMethods.isNetworkAvailable()){
            uploadPhoto();
        } else {
            displayNetworkFailMessage();
            mCallback.onUploadFail();
        }
    } //onResume

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (UploadListener) activity;
        } catch(ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + activity.getString(R.string.confirm_callback_exception_message));
        }
    }

    public void addConfirmText(String title, String text) {
        mTitle.setText(title);
        mText.setText(text);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    //Upload Photo to DB
    protected void uploadPhoto() {

          //TODO: handle upload retry
//        if(!(requestMethods.isNetworkAvailable())){
//            requestMethods.showDialog(mContext, getString(R.string.error_network_title),
//                    getString(R.string.error_network_message));
//            //TODO: networkfail callback to actvitiy? + go to retry upload screen or something?
//            return;
//        }

        RequestQueue queue = Volley.newRequestQueue(getActivity());

        //Get itemID + Uri from MainActivity
        Bundle b = getArguments();
        String itemID = (String)b.getSerializable(getString(R.string.item_id_bundle_key));
        Uri uri = Uri.parse((String) b.getSerializable(getString(R.string.uri_bundle_key)));
        final String token = (String)b.getSerializable(getString(R.string.token_bundle_key));

        //Test file size
        if(testFileSize(uri) > 8){
            displayFileSizeFailMessage();
        } else {
            //Get Photo as Base64 encoded String
            final String photoFile = requestMethods.createUploadPhotoObject(uri);
            String url = ApiConstants.ADD_PHOTO + mCurrentUser.getUserID() + "/" + itemID;

            //Upload Photo
            StringRequest uploadPhotoRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //Get Response
                            Log.v(TAG, "uploadPhoto > onResponse: " + response);
                            //TODO: add conditions? What happens when photo upload fails?

                            displaySuccessMessage();
                            //Send notice to activity (will execute timed close of this fragment)
                            mCallback.onUploadSuccess();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "uploadPhoto > onErrorResponse: " + error.getMessage());
                    //TODO: add switch for all possible error codes
                    displayFailMessage();
                    mCallback.onUploadFail();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(ApiConstants.POST_PHOTO_KEY, photoFile);
                    params.put(ApiConstants.USER_TOKEN, token);
                    return params;
                }
            };
            queue.add(uploadPhotoRequest);

        }
    } //uploadPhoto

    //Check file size for upload
    public long testFileSize(Uri uri){
        File photo = new File(uri.getPath());
        long size = photo.length()/(1024*1024);
        return size;
    }


    //DISPLAY RETRY
    //TODO: add retry button

    //DISPLAY MESSAGES
    public void displaySuccessMessage(){
        addConfirmText(getString(R.string.upload_success_title),
                getString(R.string.upload_success_text));
        mIcon.setImageResource(R.drawable.confirm_checkmark);
        //Hide progress bar and show result of upload attempt
        mProgressLayout.setVisibility(View.GONE);
        mResultMessage.setVisibility(View.VISIBLE);
    }

    public void displayFailMessage(){
        addConfirmText(getString(R.string.upload_failed_title),
                getString(R.string.upload_failed_text));
        mIcon.setImageResource(R.drawable.confirm_x);
        mProgressLayout.setVisibility(View.GONE);
        mResultMessage.setVisibility(View.VISIBLE);
    }

    public void displayNetworkFailMessage(){
        addConfirmText(getString(R.string.upload_failed_title_network),
                getString(R.string.upload_failed_text_network));
        mIcon.setImageResource(R.drawable.confirm_x);
        mProgressLayout.setVisibility(View.GONE);
        mResultMessage.setVisibility(View.VISIBLE);
    }

    public void displayFileSizeFailMessage(){
        addConfirmText(getString(R.string.upload_failed_title_filesize),
                getString(R.string.upload_failed_text_filesize));
        mIcon.setImageResource(R.drawable.confirm_x);
        mProgressLayout.setVisibility(View.GONE);
        mResultMessage.setVisibility(View.VISIBLE);
    }

} //UploadFragment
