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

package org.creativecommons.thelist.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.squareup.picasso.Picasso;

import org.creativecommons.thelist.R;
import org.creativecommons.thelist.layouts.SpinnerObject;
import org.creativecommons.thelist.utils.ApiConstants;
import org.creativecommons.thelist.utils.ListApplication;
import org.creativecommons.thelist.utils.ListUser;
import org.creativecommons.thelist.utils.MessageHelper;
import org.creativecommons.thelist.utils.NetworkUtils;
import org.creativecommons.thelist.utils.PhotoConstants;
import org.creativecommons.thelist.utils.RequestMethods;
import org.creativecommons.thelist.utils.Uploader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddItemActivity extends AppCompatActivity {
    public static final String TAG = AddItemActivity.class.getSimpleName();

    private Context mContext;

    //Helpers
    private ListUser mCurrentUser;
    private MessageHelper mMessageHelper;
    private RequestMethods mRequestMethods;

    private Uri mMediaUri;
    //protected Uri mLinkUri; //TODO: re-add when links become a thing

    //Spinner List
    List<SpinnerObject> mSpinnerList = new ArrayList<>();
    String catId;

    //UI Elements
    private RelativeLayout mMakerItemProgressBar;
    private ImageButton mAddImage;
    private EditText mItemNameField;
    private EditText mDescriptionField;
    private String mDescription;
    private Spinner mCategorySpinner;
    private RelativeLayout mStickyFooterContainer;
    private Button mDoneButton;
    private FrameLayout mOverlay;

    private Boolean mPhotoAdded;

    // --------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        mContext = this;

        mCurrentUser = new ListUser(AddItemActivity.this);
        mMessageHelper = new MessageHelper(mContext);
        mRequestMethods = new RequestMethods(mContext);

        //Google Analytics Tracker
        ((ListApplication) getApplication()).getTracker(ListApplication.TrackerName.GLOBAL_TRACKER);


        //UI Elements
        mMakerItemProgressBar = (RelativeLayout) findViewById(R.id.makerItemProgressBar);
        mItemNameField = (EditText) findViewById(R.id.add_item_title);
        mDescriptionField = (EditText) findViewById(R.id.add_item_description);
        mDescription = null;
        mCategorySpinner = (Spinner) findViewById(R.id.category_spinner);
        mAddImage = (ImageButton) findViewById(R.id.add_item_example_image);
        mStickyFooterContainer = (RelativeLayout) findViewById(R.id.sticky_footer_container);
        mDoneButton = (Button) findViewById(R.id.add_item_button);

        mPhotoAdded = false;

        //Get Intent if it exists
        Intent receivedIntent = getIntent();
        Log.v(TAG, receivedIntent.toString());

        if(receivedIntent.getAction() != null){
            String receivedAction = receivedIntent.getAction();
            Uri receivedUri = (Uri)receivedIntent.getParcelableExtra(Intent.EXTRA_STREAM);

            if(receivedAction.equals(Intent.ACTION_SEND) && receivedUri != null){

                Picasso.with(mContext).load(receivedUri).into(mAddImage);

                mPhotoAdded = true;

            } else {
                mMessageHelper.showDialog(mContext, "Oops!", "There was a problem adding your image to suggestions");
            }
        }

        //Set Spinner Content
        mRequestMethods.getCategories(new NetworkUtils.ResponseCallback() {
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
                            (mContext,android.R.layout.simple_spinner_item, mSpinnerList);
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

        //Get data when spinner selection made
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

        //Example image click listener
        mAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                if(mPhotoAdded){
                    builder.setItems(R.array.photo_choices_remove, mDialogListener);
                } else {
                    builder.setItems(R.array.photo_choices, mDialogListener);
                }

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        //Done Button Listener
        mDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String itemName = mItemNameField.getText().toString().trim();
                final String itemDescription = mDescriptionField.getText().toString().trim();

                if(itemDescription.length() > 1){
                    mDescription = itemDescription;
                }

                //Has item name been added?
                if(itemName.isEmpty()){
                    mMessageHelper.showDialog(mContext, mContext.getString(R.string.oops_label),
                            mContext.getString(R.string.dialog_missing_item_name));
                    //Has category been selected?
                } else if(catId.equals("0")){
                    mMessageHelper.showDialog(mContext, mContext.getString(R.string.oops_label),
                            mContext.getString(R.string.dialog_missing_item_cat));
                } else {
                    startItemUpload(itemName, mDescription);
                }
            }
        });

    } //onCreate

    @Override
    protected void onResume() {
        super.onResume();

    }

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
                                Toast.makeText(mContext, R.string.error_external_storage,
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
                            Picasso.with(mContext).load(R.drawable.add_item_placeholder).into(mAddImage); //TODO: update with new image
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
                        String appName = getString(R.string.app_name);
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
                    mContext.sendBroadcast(mediaScanIntent);

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

    //Start Item Upload
    public void startItemUpload(final String title, final String description){ //TODO: re-add param: final Uri linkuri
        mCurrentUser.getAuthed(new ListUser.AuthCallback() {
            @Override
            public void onAuthed(String authtoken) {
                //mOverlay.setBackgroundColor(getResources().getColor(R.color.translucent_background));
                mMakerItemProgressBar.setVisibility(View.VISIBLE);

                //Disable button/editexts
                enableFields(false);

                Uploader uploader = new Uploader(mContext);

                //Add Item Request
                uploader.addMakerItem(title, catId, description, mMediaUri,
                        new NetworkUtils.RequestCallback() {
                            @Override
                            public void onSuccess() {
                                Log.v(TAG, "addMakerItem > onSuccess");

                                //Delay success response so user can process what’s going on
                                new android.os.Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(mContext, "Your request was successfully sent!",
                                                Toast.LENGTH_LONG).show();
                                        mMakerItemProgressBar.setVisibility(View.INVISIBLE);

                                        Intent intent = new Intent(AddItemActivity.this, MainActivity.class);
                                        startActivity(intent);
                                    }
                                }, 1500);
                            } //onAuthed

                            @Override
                            public void onFail() {
                                //mMessageHelper.notifyUploadFail(title);
                                Toast.makeText(mContext,
                                        "Looks like there was a problem sending your request. Try again!",
                                        Toast.LENGTH_LONG).show();

                                mMakerItemProgressBar.setVisibility(View.INVISIBLE);
                                //Enable button/editexts again
                                enableFields(true);

                            } //onFail

                            @Override
                            public void onCancelled(NetworkUtils.CancelResponse response) {
                                Log.v(TAG, "addMakerItem > onCancelled: " + response.toString());

                                mMakerItemProgressBar.setVisibility(View.INVISIBLE);
                                //Enable button/editexts again
                                enableFields(true);

                                switch (response) {
                                    case NETWORK_ERROR:
                                        mMessageHelper.networkFailMessage();
                                        break;
                                    case FILESIZE_ERROR:
                                        mMessageHelper.photoUploadSizeFailMessage();
                                        break;
                                }
                            }
                        });
            } //onAuthed

        });

    } //startItemUpload

    public void enableFields(boolean bol){
        mDoneButton.setEnabled(bol);
        mDescriptionField.setEnabled(bol);
        mItemNameField.setEnabled(bol);
        mCategorySpinner.setEnabled(bol);
    }

    @Override
    public void onStart(){
        super.onStart();
        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    }

    @Override
    public void onStop(){
        super.onStop();
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }

    //onBackPressed
    @Override
    public void onBackPressed() {
        Intent homeIntent = new Intent(AddItemActivity.this, MainActivity.class);
        startActivity(homeIntent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
