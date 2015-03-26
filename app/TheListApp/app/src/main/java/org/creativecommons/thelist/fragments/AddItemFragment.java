package org.creativecommons.thelist.fragments;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.android.volley.VolleyError;
import com.squareup.picasso.Picasso;

import org.creativecommons.thelist.R;
import org.creativecommons.thelist.utils.ApiConstants;
import org.creativecommons.thelist.utils.MessageHelper;
import org.creativecommons.thelist.utils.PhotoConstants;
import org.creativecommons.thelist.utils.RequestMethods;
import org.creativecommons.thelist.utils.SpinnerObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddItemFragment extends android.support.v4.app.Fragment {
    public static final String TAG = AddItemFragment.class.getSimpleName();

    Context mContext;
    private MessageHelper mMessageHelper;
    private RequestMethods mRequestMethods;
    protected Uri mMediaUri;

    //Spinner List
    List<SpinnerObject> mSpinnerList = new ArrayList<>();

    //UI Elements
    private ImageButton mAddImage;
    private Spinner mCategorySpinner;
    // --------------------------------------------------------


    public AddItemFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_item, container, false);
    }

    @Override
    public void onStart(){
        super.onStart();

        mContext = getActivity();
        mMessageHelper = new MessageHelper(mContext);
        mRequestMethods = new RequestMethods(mContext);

        //UI Elements
        mCategorySpinner = (Spinner) getView().findViewById(R.id.category_spinner);

        //Set Spinner Content
        mRequestMethods.getCategories(new RequestMethods.ResponseCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                Log.v(TAG, "> getCategories > onResponse: " + response);

                if(response.length() > 0){
                    for(int i = 0; i < response.length(); i++){
                        try {
                            JSONObject jsonSingleCategory = response.getJSONObject(i);
                            String name = jsonSingleCategory.getString(ApiConstants.CATEGORY_NAME);
                            String id = jsonSingleCategory.getString(ApiConstants.CATEGORY_ID);

                            mSpinnerList.add(new SpinnerObject(name, id));

                        } catch (JSONException e) {
                            Log.e(TAG, e.getMessage());
                        }
                    }

                    //Update Spinner
                    ArrayAdapter<SpinnerObject> adapter = new ArrayAdapter<SpinnerObject>
                            (getActivity(),android.R.layout.simple_spinner_item, mSpinnerList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mCategorySpinner.setAdapter(adapter);
                }
            } //onSuccess
            @Override
            public void onFail(VolleyError error) {
                Log.v(TAG, "getCategories > onFail: " + error.getMessage());
                //TODO: add better error message
                mMessageHelper.showDialog(mContext, getString(R.string.error_title),
                        getString(R.string.error_message));
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();

        //UI Elements
        mAddImage = (ImageButton) getView().findViewById(R.id.add_item_example_image);

        //Click Listener to get Image
        mAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent choosePhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                choosePhotoIntent.setType("image/*");
                mMediaUri = getOutputMediaFileUri(PhotoConstants.MEDIA_TYPE_IMAGE);
                Log.v(TAG, "Media URI:" + mMediaUri);
                startActivityForResult(choosePhotoIntent, PhotoConstants.PICK_PHOTO_REQUEST);
            }

            private Uri getOutputMediaFileUri(int mediaType) {
                // To be safe, you should check that the SDCard is mounted
                // using Environment.getExternalStorageState() before doing this.
                if (isExternalStorageAvailable()) {
                    // get the URI

                    // 1. Get the external storage directory
                    String appName = getActivity().getString(R.string.app_name);
                    File mediaStorageDir = new File(
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                            appName);

                    // 2. Create our subdirectory
                    if (! mediaStorageDir.exists()) {
                        if (! mediaStorageDir.mkdirs()) {
                            Log.e(TAG, "Failed to create directory.");
                            return null;
                        }
                    }
                    // 3. Create a file name
                    // 4. Create the file
                    File mediaFile;
                    Date now = new Date();
                    String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(now);

                    String path = mediaStorageDir.getPath() + File.separator;
                    if (mediaType == PhotoConstants.MEDIA_TYPE_IMAGE) {
                        mediaFile = new File(path + "IMG_" + timestamp + ".jpg");
                    }
                    else {
                        return null;
                    }
                    Log.d(TAG, "File: " + Uri.fromFile(mediaFile));

                    // 5. Return the file's URI
                    return Uri.fromFile(mediaFile);
                }
                else {
                    return null;
                }
            }
            //Check if external storage is available
            private boolean isExternalStorageAvailable() {
                String state = Environment.getExternalStorageState();
                if (state.equals(Environment.MEDIA_MOUNTED)) {
                    return true;
                }
                else {
                    return false;
                }
            }
        });



    } //onResume


    //When photo has been selected from gallery
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PhotoConstants.PICK_PHOTO_REQUEST:
                if (resultCode == Activity.RESULT_OK) {

                    if (data == null) {
                        //Toast.makeText(this,getString(R.string.general_error),Toast.LENGTH_LONG).show();
                        Log.d(TAG, "> onActivityResult > data == null");
                    } else {
                        mMediaUri = data.getData();
                    }
                    Log.i(TAG, "Media URI:" + mMediaUri);

                    //TODO: set image resource
                    Picasso.with(mContext).load(mMediaUri).into(mAddImage);

                } //RESULT OK
                else if (resultCode != Activity.RESULT_CANCELED) { //result other than ok or cancelled
                    //Toast.makeText(this, R.string.general_error, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "> onActivityResult > resultCode != canceled");
                }
                break;
        } //switch
    } //onActivityResult


    @Override
    public void onDetach() {
        super.onDetach();
        //mCallback = null;
    }

} //AddItemFragment
