package org.creativecommons.thelist;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.creativecommons.thelist.adapters.MainListAdapter;
import org.creativecommons.thelist.adapters.MainListItem;
import org.creativecommons.thelist.utils.ApiConstants;
import org.creativecommons.thelist.utils.ListUser;
import org.creativecommons.thelist.utils.PhotoConstants;
import org.creativecommons.thelist.utils.RequestMethods;
import org.creativecommons.thelist.utils.SharedPreferencesMethods;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MainActivity extends ActionBarActivity implements LoginFragment.LoginClickListener,
        TermsFragment.TermsClickListener, ConfirmFragment.ConfirmListener {
    public static final String TAG = MainActivity.class.getSimpleName();
    protected Context mContext;

    //Request Methods
    RequestMethods requestMethods = new RequestMethods(this);
    SharedPreferencesMethods sharedPreferencesMethods = new SharedPreferencesMethods(this);
    ListUser mCurrentUser = new ListUser(this);

    protected JSONObject mCurrentUserObject;

    //For API Requests + Response
    protected JSONObject mItemData;
    protected JSONArray mJsonItems;

    //Lists to be adapted
    private List<MainListItem> mItemList = new ArrayList<MainListItem>();
    //private List<MainListItem> mUserItemList = new ArrayList<MainListItem>();

    //Adapters
    protected MainListAdapter feedAdapter;
    //TODO: figure out adapter for other lists in the feed

    //UI Elements
    protected ProgressBar mProgressBar;
    protected ListView mListView;
    protected FrameLayout mFrameLayout;

    //Photo Variables
    protected Uri mMediaUri;
    protected MainListItem mCurrentItem;

    //Fragments
    LoginFragment loginFragment = new LoginFragment();
    TermsFragment termsFragment = new TermsFragment();
    ConfirmFragment confirmFragment = new ConfirmFragment();


    // --------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        //Load UI Elements
        mProgressBar = (ProgressBar) findViewById(R.id.feed_progressBar);
        mListView = (ListView)findViewById(R.id.list);
        mFrameLayout = (FrameLayout)findViewById(R.id.overlay_fragment_container);

        feedAdapter = new MainListAdapter(this, mItemList);
        mListView.setAdapter(feedAdapter);

        //Show Dialog on List Item Click
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Show Dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setItems(R.array.listItem_choices, mDialogListener);
                AlertDialog dialog = builder.create();
                dialog.show();

                //Store ListItem in variable
                mCurrentItem = (MainListItem) mListView.getItemAtPosition(position);
                //Log.v(TAG, mCurrentItem.toString());
            }
        });

        //If Network Connection is available, get User’s Items (API, or local if not logged in)
        if(requestMethods.isNetworkAvailable(mContext)) {
            mProgressBar.setVisibility(View.VISIBLE);

            if(mCurrentUser.isLoggedIn()) {
                getUserListItems();
            } else {
                getUserSelectedItems();
            }
            //TODO: Get other content for feed
            //getCategoriesList();
           // getAllListItems();
        }
        else {
            Toast.makeText(this, "Network is unavailable", Toast.LENGTH_LONG).show();
        }
    } //onCreate

    //UPDATE LIST WITH CONTENT
    private void updateList() {
        mProgressBar.setVisibility(View.INVISIBLE);
        if (mItemData == null) {
            //TODO: User Error Message in dialog (updateDisplay for Error: update so you can pass in JSON response)
            requestMethods.updateDisplayForError();
        }
        else {
            try {
                mJsonItems = mItemData.getJSONArray(ApiConstants.RESPONSE_CONTENT);

                for(int i = 0; i < mJsonItems.length(); i++) {
                    JSONObject jsonSingleItem = mJsonItems.getJSONObject(i);
                    MainListItem listItem = new MainListItem();
                    listItem.setItemName(jsonSingleItem.getString(ApiConstants.ITEM_NAME));
                    listItem.setMakerName(jsonSingleItem.getString(ApiConstants.MAKER_NAME));
                    listItem.setItemID(jsonSingleItem.getInt(ApiConstants.ITEM_ID));

                    //Adding to array of List Items
                    mItemList.add(listItem);
                }
            } catch (JSONException e) {
                Log.e(TAG, "Exception Caught: ", e);
            }
            feedAdapter.notifyDataSetChanged();
        }
    } //updateList

    //TODO: Create single method for user list items (logged in/not logged in) ~API
    //GET ALL of USER‘S LIST ITEMS (NOT logged in) (limit # eventually)
    private void getUserSelectedItems() {
        RequestQueue queue = Volley.newRequestQueue(this);

        //Genymotion Emulator
        String url = ApiConstants.GET_MULTIPLE_ITEMS;
        //Android Default Emulator
        //String url = "http://10.0.2.2:3000/api/items";

        //TODO: if logged in, get user’s list
        //Create Object of List Item IDs to send
        JSONObject UserItemObject = sharedPreferencesMethods.createUserItemsObject(ApiConstants.USER_ITEMS, this);

        JsonObjectRequest getUserItemsRequest = new JsonObjectRequest(Request.Method.PUT, url, UserItemObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        mItemData = response;
                        //Log.v(TAG,response.toString());
                        updateList();
                    }
                }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse (VolleyError error){
                    requestMethods.updateDisplayForError();
                }
        });
        queue.add(getUserItemsRequest);
    } //Get All items in the user’s list


    //Get User’s List items if logged in
    private void getUserListItems() {
        RequestQueue queue = Volley.newRequestQueue(this);

        //Genymotion Emulator
        String url = ApiConstants.GET_ALL_USER_ITEMS + mCurrentUser.getUserID();
        //Android Default Emulator
        //String url = "http://10.0.2.2:3000/api/user" + mCurrentUser.getUserID();

        JsonObjectRequest getUserItemsRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        mItemData = response;
                        //Log.v(TAG,response.toString());
                        updateList();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse (VolleyError error){
                requestMethods.updateDisplayForError();
            }
        });
        queue.add(getUserItemsRequest);
    }


    //DIALOG FOR LIST ITEM ACTION
    public DialogInterface.OnClickListener mDialogListener =
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch(which) {
                        case 0:
                            Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            mMediaUri = getOutputMediaFileUri(PhotoConstants.MEDIA_TYPE_IMAGE);
                            if (mMediaUri == null) {
                                // display an error
                                Toast.makeText(MainActivity.this, R.string.error_external_storage,
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
//                        case 2: // Save Item to My List
//                            //TODO: POST Data to save list item
//                            //If logged in: add to array
//                            //If not logged in: add to sharedPreference array
//                            break;
                    }
                }
                private Uri getOutputMediaFileUri(int mediaType) {
                    // To be safe, you should check that the SDCard is mounted
                    // using Environment.getExternalStorageState() before doing this.
                    if (isExternalStorageAvailable()) {
                        // get the URI

                        // 1. Get the external storage directory
                        String appName = MainActivity.this.getString(R.string.app_name);
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


    //Once photo taken or selected then do this:
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            if(mCurrentUser.isLoggedIn()) {
                //Create and send photo object
                //Note on server side create relationship between user (creator) and photo
                uploadPhoto();
            } else {
                //Load Login Fragment
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.overlay_fragment_container,loginFragment).commit();
                mFrameLayout.setClickable(true);
                getSupportActionBar().hide();
            }

            //Add photo to the Gallery (listen for broadcast and let gallery take action)
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(mMediaUri);
            sendBroadcast(mediaScanIntent);

        }
        else if(resultCode != RESULT_CANCELED) {
            Toast.makeText(this, R.string.general_error, Toast.LENGTH_SHORT).show();
        }
    } //onActivityResult

    //Upload Photo to DB
    protected void uploadPhoto() {
        RequestQueue queue = Volley.newRequestQueue(this);
        //Genymotion Emulator
        String url = ApiConstants.POST_PHOTO;
        //Android Default Emulator
        //String url = "http://10.0.2.2:3000/api/photo";

        //Get Photo Object
        JSONObject photoObject = requestMethods.createUploadPhotoObject(mCurrentItem, mMediaUri);
        //Log.v(TAG,photoObject.toString());

        //Volley Request
        JsonObjectRequest postPhotoRequest = new JsonObjectRequest(Request.Method.POST, url, photoObject,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //TODO: Check response code + display error
//                            if(responseCode != 200), get response data + show error

                            //Handle Data
                            //TODO: Handle Response
                            JSONObject postResponse = response.getJSONObject(ApiConstants.RESPONSE_CONTENT);
                            //Log.v(TAG, postResponse.toString());

                            //TODO: if status is NOT ok, Change TextView in confirmFragment
                            //SUCCESS text in confirmFragment
                            Bundle b = new Bundle();
                            b.putSerializable("status", ConfirmFragment.STATUS.SUCCESS);
                            confirmFragment.setArguments(b);

                            //Start photo confirmation fragment
                            getSupportFragmentManager().beginTransaction()
                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                    .replace(R.id.overlay_fragment_container, confirmFragment)
                                    .commit();

                            //send request to API
                            //TODO: Find out why this does this twice (is it because the view is re-inflated?)
                            //getUserSelectedItems();

                        } catch (JSONException e) {
                            Log.e(TAG, e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse (VolleyError error){
                requestMethods.updateDisplayForError();
                //TODO: is this where error responses will be returned from API?
            }
        });
        queue.add(postPhotoRequest);
    } //uploadPhoto

    //When New User fills out sign up (save data locally)
    @Override
    public void UserCreated(String userData) {
        try {
            //Set current user data
            mCurrentUserObject = new JSONObject(userData);
            mCurrentUser.setUserID(mCurrentUserObject.getInt(ApiConstants.USER_ID));
            mCurrentUser.setUserName(mCurrentUserObject.getString(ApiConstants.USER_NAME));
            //TODO: set user token
//            Log.v(TAG, mCurrentUser.getUserName());
//            Log.v(TAG,mCurrentUser.getUserID());
        } catch (JSONException e) {
            Log.v(TAG,e.getMessage());
        }

        //Start terms fragment (must agree to terms before account is created)
        getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.overlay_fragment_container, termsFragment)
                .commit();
    } //UserCreated

    //When User has been logged in
    @Override
    public void UserLoggedIn(String userData) {
        try {
            //Set current user Data
            mCurrentUserObject = new JSONObject(userData);
            mCurrentUser.setUserID(mCurrentUserObject.getInt(ApiConstants.USER_ID));
            mCurrentUser.setUserName(mCurrentUserObject.getString(ApiConstants.USER_NAME));
            //TODO: set user token
        } catch (JSONException e) {
            Log.e(TAG,e.getMessage());
        }
        uploadPhoto();
    } //UserLoggedIn

    //User has cancelled an upload
    @Override
    public void CancelUpload() {
        //Change Text in confirmFragment
        Bundle b = new Bundle();
        b.putSerializable("status", ConfirmFragment.STATUS.CANCEL);
        confirmFragment.setArguments(b);
        //Switch to confirmFragment
        getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.overlay_fragment_container, confirmFragment)
                .commit();
    } //CancelUpload

    //When account Confirmation Received
    @Override
    public void onTermsClicked() {
        //Upload + take user to success screen
        uploadPhoto();
    }

    @Override
    public void onTermsCancelled() {
        Bundle b = new Bundle();
        b.putSerializable("status", ConfirmFragment.STATUS.CANCEL);
        confirmFragment.setArguments(b);

        getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.overlay_fragment_container, confirmFragment)
                .commit();
    }

    //When ConfirmFragment has been inflated
    @Override
    public void onConfirmFinish() {
        //Show upload success for limited time
        new Handler().postDelayed(new Runnable() {
        @Override
        public void run() {
            getSupportFragmentManager().beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .remove(confirmFragment)
                    .commit();
            mFrameLayout.setClickable(false);
            getSupportActionBar().show();
        }
    }, 3600);
    } //onConfirmFinish

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
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
        //Start Random Item Activity
        if (id == R.id.action_random) {
            Intent intent = new Intent(MainActivity.this, RandomActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
