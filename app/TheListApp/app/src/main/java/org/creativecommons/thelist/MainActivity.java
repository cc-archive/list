package org.creativecommons.thelist;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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


public class MainActivity extends ActionBarActivity {
    public static final String TAG = MainActivity.class.getSimpleName();
    //Request Methods
    RequestMethods requestMethods = new RequestMethods(this);
    SharedPreferencesMethods sharedPreferencesMethods = new SharedPreferencesMethods(this);

    //For API Requests + Response
    protected JSONObject mItemData;
    protected JSONArray mJsonItems;
    //TODO: Limit returned results to most recent
    //public static final int NUMBER_OF_POSTS = 5;

    //Lists to be adapted
    private List<MainListItem> mItemList = new ArrayList<MainListItem>();
    private List<MainListItem> mUserItemList = new ArrayList<MainListItem>();

    //Adapters
    protected MainListAdapter feedAdapter;
    protected MainListAdapter userListAdapter;

    //UI Elements
    protected ProgressBar mProgressBar;
    protected ListView mListView;
    protected ListView mUserListView;

    //Photo Variables
    protected Uri mMediaUri;
    protected JSONObject mPhotoObject;
    protected MainListItem mCurrentItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Load UI Elements
        mProgressBar = (ProgressBar) findViewById(R.id.feed_progressBar);
        mListView = (ListView)findViewById(R.id.list);
        //mUserListView = (ListView)

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
                //TODO: Can I move this?
                mCurrentItem = (MainListItem) mListView.getItemAtPosition(position);
                Log.v(TAG, mCurrentItem.toString());
            }
        });

        //If Network Connection is available, Execute getDataTask
        if(requestMethods.isNetworkAvailable()) {
            mProgressBar.setVisibility(View.VISIBLE);
            //getUserListItems();
            //getCategoriesList();
            getAllListItems();
        }
        else {
            Toast.makeText(this, "Network is unavailable", Toast.LENGTH_LONG).show();
        }
    } //onCreate

    //UPDATE LIST WITH CONTENT
    private void updateList() {
        mProgressBar.setVisibility(View.INVISIBLE);
        if (mItemData == null) {
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

    //GET All ListItems
    private void getAllListItems() {
        RequestQueue queue = Volley.newRequestQueue(this);

        //Genymotion Emulator
        String url ="http://10.0.3.2:3000/api/item";
        //Android Default Emulator
        //String url = "http://10.0.2.2:3000/api/item";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                            mItemData = response;
                            updateList();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse (VolleyError error){
                requestMethods.updateDisplayForError();
            }
        });
        queue.add(jsonObjectRequest);
    }

    //GET All user list items
//    private void getUserListItems() {
//        RequestQueue queue = Volley.newRequestQueue(this);
//
//        //Genymotion Emulator
//        String url ="http://10.0.3.2:3000/api/items";
//        //Android Default Emulator
//        //String url = "http://10.0.2.2:3000/api/items";
//
//        //Retrieve User category preferences
//        JSONArray userPreferences = sharedPreferencesMethods.RetrieveSharedPreference
//                (sharedPreferencesMethods.LIST_ITEM_PREFERENCE,
//                        sharedPreferencesMethods.LIST_ITEM_PREFERENCE_KEY);
//
//        //Create Object to send
//        JSONObject jso = new JSONObject();
//        try {
//            jso.put(ApiConstants.USER_CATEGORIES, userPreferences);
//        } catch (JSONException e) {
//            Log.e(TAG, e.getMessage());
//        }
//
//        JsonObjectRequest getUserItemsRequest = new JsonObjectRequest(Request.Method.GET, url, null,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        mItemData = response;
//                        updateList();
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse (VolleyError error){
//                requestMethods.updateDisplayForError();
//            }
//        });
//        queue.add(getUserItemsRequest);
//    } //Get All User List Items


    //DIALOG FOR LIST ITEM ACTION
    protected DialogInterface.OnClickListener mDialogListener =
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
                        case 2: // Save Item to My List
                            //TODO: POST Data to save list item
                            break;
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

    //Upload Photo to DB
    protected void uploadPhoto() {
        RequestQueue queue = Volley.newRequestQueue(this);
        //Genymotion Emulator
        String url = "http://10.0.3.2:3000/api/photo";
        //Android Default Emulator
        //String url = "http://10.0.2.2:3000/api/photo";

        //Get Photo Object
        JSONObject photoObject = requestMethods.createUploadPhotoObject(mCurrentItem, mMediaUri);
        Log.v(TAG,photoObject.toString());

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
                            Log.v(TAG, postResponse.toString());

                            mProgressBar.setVisibility(View.INVISIBLE);

                        } catch (JSONException e) {
                            Log.e(TAG, e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse (VolleyError error){
                requestMethods.updateDisplayForError();
            }
        });
        queue.add(postPhotoRequest);
    } //storeCategoriesRequest

    //Once photo taken or selected then do this:
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            if(requestMethods.isLoggedIn()) {
                //Create and send photo object
                //TODO: Note on server side create relationship between user and photoID
                uploadPhoto();
            } else {

                Log.v(TAG, "User is not logged in and we should start Login Activity");
                //startActivityForResult
                //start Login Activity + return user object (get userID)

                //onActivityResult
                //Once logged in:
                //update userloggedin and userID
                //uploadPhoto();
            }



            //Add photo to the Gallery (listen for broadcast and let gallery take action)
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(mMediaUri);
            sendBroadcast(mediaScanIntent);

            //TODO: Go to Login Activity (or Fragment?)
        }
        else if(resultCode != RESULT_CANCELED) {
            Toast.makeText(this, R.string.general_error, Toast.LENGTH_LONG).show();
        }

    }

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

        if (id == R.id.action_random) {
            Intent intent = new Intent(MainActivity.this, RandomActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
