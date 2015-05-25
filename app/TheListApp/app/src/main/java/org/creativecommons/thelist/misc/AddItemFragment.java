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

package org.creativecommons.thelist.misc;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.squareup.picasso.Picasso;

import org.creativecommons.thelist.R;
import org.creativecommons.thelist.layouts.SpinnerObject;
import org.creativecommons.thelist.utils.ApiConstants;
import org.creativecommons.thelist.utils.ListUser;
import org.creativecommons.thelist.utils.MessageHelper;
import org.creativecommons.thelist.utils.PhotoConstants;
import org.creativecommons.thelist.utils.RequestMethods;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddItemFragment extends android.support.v4.app.Fragment {
    public static final String TAG = AddItemFragment.class.getSimpleName();

    private Context mContext;
    private Activity mActivity;

    //Helpers
    private ListUser mCurrentUser;
    private MessageHelper mMessageHelper;
    private RequestMethods mRequestMethods;

    //Spinner List
    List<SpinnerObject> mSpinnerList = new ArrayList<>();
    String catId;

    //UI Elements
    private ImageButton mAddImage;
    private EditText mItemNameField;
    private EditText mDescriptionField;
    private String mDescription;
    private Spinner mCategorySpinner;
    private RelativeLayout mStickyFooter;

    protected Uri mMediaUri;
    protected Uri mLinkUri;

    private Boolean mPhotoAdded;

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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mContext = getActivity();
        mActivity = getActivity();

        mCurrentUser = new ListUser(mContext);
        mMessageHelper = new MessageHelper(mContext);
        mRequestMethods = new RequestMethods(mContext);

        //UI Elements
        mItemNameField = (EditText) mActivity.findViewById(R.id.add_item_title);
        mDescriptionField = (EditText) mActivity.findViewById(R.id.add_item_description);
        mDescription = null;
        mCategorySpinner = (Spinner) mActivity.findViewById(R.id.category_spinner);
        mStickyFooter = (RelativeLayout) mActivity.findViewById(R.id.add_item_sticky_footer);

        mPhotoAdded = false;

//        mBottomToolbar.setOnMenuItemClickListener(new android.support.v7.widget.Toolbar.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                int id = item.getItemId();
//
//                if (id == R.id.action_item_done) {
//                    //TODO: Start Item Upload
//                    Log.v(TAG, "DONE WITH ITEM");
//
//                    final String itemName = mItemNameField.getText().toString().trim();
//                    final String itemDescription = mDescriptionField.getText().toString().trim();
//
//                    if(itemDescription.length() > 1){
//                        mDescription = itemDescription;
//                    }
//
//                    //Has item name been added?
//                    if(itemName.isEmpty()){
//                        mMessageHelper.showDialog(mContext, mContext.getString(R.string.oops_label),
//                                mContext.getString(R.string.dialog_missing_item_name));
//                        return true;
//                      //Has category been selected?
//                    } else if(catId.equals("0")){
//                        mMessageHelper.showDialog(mContext, mContext.getString(R.string.oops_label),
//                                mContext.getString(R.string.dialog_missing_item_cat));
//                        return true;
//                    } else {
//                        startItemUpload(itemName, mDescription, mLinkUri);
//
//                        return true;
//                    }
//                } //if action done
//                return true;
//            }
//        });


        //Set Spinner Content
        mRequestMethods.getCategories(new RequestMethods.ResponseCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                Log.v(TAG, "> getCategories > onResponse: " + response);
                mSpinnerList.clear();

                if(response.length() > 0){
                    mSpinnerList.add(new SpinnerObject("Select category", "0"));

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
            } //onAuthed
            @Override
            public void onFail(VolleyError error) {
                Log.v(TAG, "getCategories > onFail: " + error.getMessage());
                //TODO: add better error message
                mMessageHelper.showDialog(mContext, getString(R.string.error_title),
                        getString(R.string.error_message));
            }
        });

        mCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SpinnerObject catObject = (SpinnerObject) mCategorySpinner.getSelectedItem();

                //Set id for POST request
                catId = catObject.getTag().toString();
                Log.v(TAG, "> onItemSelected, catId: " + catId);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    } //onActivityCreated

    //Show dialog when List Item is tapped
    public DialogInterface.OnClickListener mDialogListener =
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch(which) {
                        case 0:
                            Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            mMediaUri = getOutputMediaFileUri(PhotoConstants.MEDIA_TYPE_IMAGE);
                            if (mMediaUri == null) {
                                // Display an error
                                Toast.makeText(getActivity(), R.string.error_external_storage,
                                        Toast.LENGTH_LONG).show();
                            }
                            else {
                                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
                                startActivityForResult(takePhotoIntent, PhotoConstants.TAKE_PHOTO_REQUEST);
                            }
                            break;
                        case 1: // Choose picture
                            Intent choosePhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                            choosePhotoIntent.setType("image/*");
                            startActivityForResult(choosePhotoIntent,PhotoConstants.PICK_PHOTO_REQUEST);
                            break;
//                        case 2:
//                            mMessageHelper.singleInputDialog(mContext, "Add a link", "Add an example image via url", new MaterialDialog.InputCallback() {
//                                @Override
//                                public void onInput(MaterialDialog materialDialog, CharSequence charSequence) {
//
//                                    mLinkUri = Uri.parse(charSequence.toString());
//
//                                    //Do something with input
//                                    //TODO: add placeholder for failed image
//                                    Picasso.with(mContext).load(mLinkUri)
//                                            .placeholder(R.drawable.progress_view_large) //failed to find image
//                                            .into(mAddImage);
//                                }
//                            });
                        case 2: //TODO: make case 3
                            Picasso.with(mContext).load(R.drawable.progress_view).into(mAddImage); //TODO: update with new image
                            mPhotoAdded = false;
                            mMediaUri = null;
                    }
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
            };

    //When photo has been selected from gallery
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case PhotoConstants.PICK_PHOTO_REQUEST:
            case PhotoConstants.TAKE_PHOTO_REQUEST:
                if(resultCode == Activity.RESULT_OK) {
                    if(data == null) {
                        //Toast.makeText(this,getString(R.string.general_error),Toast.LENGTH_LONG).show();
                        Log.d(TAG, "> onActivityResult > data == null");
                    }
                    else {
                        mMediaUri = data.getData();
                    }
                    Log.i(TAG,"Media URI:" + mMediaUri);

                    //Add photo to the Gallery (listen for broadcast and let gallery take action)
                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    mediaScanIntent.setData(mMediaUri);
                    getActivity().sendBroadcast(mediaScanIntent);

                    Picasso.with(mContext).load(mMediaUri).into(mAddImage);
                    mPhotoAdded = true;
                } //RESULT OK
                else if(resultCode != Activity.RESULT_CANCELED) { //result other than ok or cancelled
                    //Toast.makeText(this, R.string.general_error, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "> onActivityResult > resultCode != canceled");
                }
                break;
        } //switch
    } //onActivityResult


    public void startItemUpload(final String itemName, final String description, final Uri linkuri){

        mRequestMethods.addMakerItem(itemName, catId, description, mMediaUri,
                new RequestMethods.RequestCallback() {
                    @Override
                    public void onSuccess() {
                        //TODO: if request succeeds
                        mMessageHelper.notifyUploadSuccess(itemName);
                    }

                    @Override
                    public void onFail() {
                        //TODO: if request fails
                        mMessageHelper.notifyUploadSuccess(itemName);

//                        mMessageHelper.showDialog(mContext,
//                                mContext.getString(R.string.oops_label),
//                                mContext.getString(R.string.dialog_item_upload_fail));
                    }

                    @Override
                    public void onCancelled(RequestMethods.CancelResponse response) {

                    }
                });

    } //startItemUpload

    @Override
    public void onDetach() {
        super.onDetach();
        //mCallback = null;
        mPhotoAdded = false;
    }

} //AddItemFragment
