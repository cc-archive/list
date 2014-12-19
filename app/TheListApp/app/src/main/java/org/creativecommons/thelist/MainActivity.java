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
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import fragments.ConfirmFragment;
import fragments.LoginFragment;
import fragments.TermsFragment;


public class MainActivity extends ActionBarActivity implements LoginFragment.LoginClickListener,
        TermsFragment.TermsClickListener, ConfirmFragment.ConfirmListener {
    public static final String TAG = MainActivity.class.getSimpleName();
    protected Context mContext;

    //Request Methods
    RequestMethods requestMethods = new RequestMethods(this);
    //SharedPreferencesMethods sharedPreferencesMethods = new SharedPreferencesMethods(this);
    ListUser mCurrentUser = new ListUser(this);

    protected JSONObject mCurrentUserObject;
    protected String userID;

    //For API Requests + Response
    //protected JSONArray mItemData;

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

        //Check if user is logged in
        userID = SharedPreferencesMethods.getUserId(mContext);
        if(userID == null) {
            mCurrentUser.setLogInState(false);
            Log.v("YO" + TAG, "NOT LOGGED IN");
        } else {
            mCurrentUser.setLogInState(true);
            Log.v("YO" + TAG, "LOGGED IN");
        }

        //Load UI Elements
        mProgressBar = (ProgressBar) findViewById(R.id.feed_progressBar);
        mListView = (ListView)findViewById(R.id.list);
        mFrameLayout = (FrameLayout)findViewById(R.id.overlay_fragment_container);

        feedAdapter = new MainListAdapter(this, mItemList);
        //mListView.setAdapter(feedAdapter);

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
            getUserListItems();
        }
        else {
            Toast.makeText(this, "Network is unavailable", Toast.LENGTH_LONG).show();
        }
    } //onCreate

    public void CheckComplete() {
        Log.v("Check complete", "start");
        for(int i = 0; i < mItemList.size(); i++) {
            if (mItemList.get(i).completed == false) {
                return;
            }
        }
        mProgressBar.setVisibility(View.INVISIBLE);
        mListView.setAdapter(feedAdapter);
        feedAdapter.notifyDataSetChanged();
    }

    private void getUserListItems() {
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest userListRequest;
        String itemRequesturl;
        JSONArray itemIds;

        //IF USER IS LOGGED IN
        if(mCurrentUser.isLoggedIn()) {
            Log.v("HELLO", "this user is logged in");

            itemRequesturl = ApiConstants.GET_USER_LIST + userID;

            userListRequest = new JsonArrayRequest(itemRequesturl,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.v("RESPONSE", response.toString());
                        //Handle Data
                        for(int i=0; i < response.length(); i++) {
                            try {
                                MainListItem listItem = new MainListItem();
                                JSONObject singleListItem = response.getJSONObject(i);
                                listItem.setItemName(singleListItem.getString(ApiConstants.ITEM_NAME));
                                listItem.setMakerName(singleListItem.getString(ApiConstants.MAKER_NAME));
                                listItem.setItemID(singleListItem.getString(ApiConstants.ITEM_ID));

                                mItemList.add(listItem);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                        feedAdapter = new MainListAdapter(MainActivity.this, mItemList);
                        mProgressBar.setVisibility(View.INVISIBLE);
                        mListView.setAdapter(feedAdapter);
                        Log.v("What the hell?", "?");
                        for (int i = 0; i < mItemList.size(); i++) {
                            Log.v("ITEM: ", mItemList.get(i).getItemName());
                        }
                        //feedAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("error", error.toString());
                    requestMethods.showErrorDialog(mContext,
                            getString(R.string.error_title),
                            getString(R.string.error_message));
                }
            });
            queue.add(userListRequest);
        }
        //IF USER IS NOT LOGGED IN
        else {
            itemIds = SharedPreferencesMethods.RetrieveUserItemPreference(mContext);

            for(int i=0; i < itemIds.length(); i++) {
                    //TODO: do I need to set ItemID here?
                    MainListItem listItem = new MainListItem();
                try {
                    listItem.setItemID(String.valueOf(itemIds.getInt(i)));
                    listItem.setRequestMethods(requestMethods);
                    listItem.setMainActivity(this);
                    listItem.createNewUserListItem();
                    Log.v(TAG, "Item added");
                } catch (JSONException e) {
                    Log.v(TAG,e.getMessage());
                }
                //Adding to array of List Items
                mItemList.add(listItem);
            }
        }
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
                            //mMediaUri = getOutputMediaFileUri(PhotoConstants.MEDIA_TYPE_IMAGE);
                            startActivityForResult(choosePhotoIntent,PhotoConstants.PICK_PHOTO_REQUEST);
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

    //Once photo taken or selected then do this:
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            if(requestCode == PhotoConstants.PICK_PHOTO_REQUEST) {
                if(data == null) {
                    Toast.makeText(this,getString(R.string.general_error),Toast.LENGTH_LONG).show();
                }
                else {
                    Log.v("DATA", data.toString());
                    mMediaUri = data.getData();
                }
            }

            Log.i(TAG,"Media URI:" + mMediaUri);

            //TODO: Check file size
            if(mCurrentUser.isLoggedIn()) {
                //Create and upload photo as Base64 encoded string
                uploadPhoto(mMediaUri);
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
    protected void uploadPhoto(Uri uri) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = ApiConstants.ADD_PHOTO + mCurrentUser.getUserID() + "/" + mCurrentItem.getItemID();

        Log.v("URL IS: ", url);

        //Get Photo Object

        Log.v("URI TEST", uri.toString());
        final String photoFile = requestMethods.createUploadPhotoObject(uri);

        //Upload Photo
        StringRequest uploadPhotoRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Get Response
                        Log.v("PHOTO UPLOADED", response.toString());

                        //SUCCESS text in confirmFragment
                        Bundle b = new Bundle();
                        b.putSerializable("status", ConfirmFragment.STATUS.SUCCESS);
                        confirmFragment.setArguments(b);

                        //Start photo confirmation fragment
                        getSupportFragmentManager().beginTransaction()
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                .replace(R.id.overlay_fragment_container, confirmFragment)
                                .commit();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v(TAG, "THERE WAS AN ERROR");
                requestMethods.showErrorDialog(mContext,
                        mContext.getString(R.string.error_title),
                        mContext.getString(R.string.error_message));
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(ApiConstants.POST_PHOTO_KEY, photoFile);
                return params;
            }
        };
        queue.add(uploadPhotoRequest);
    } //uploadPhoto

    //When New User fills out sign up (save data locally)
    @Override
    public void UserCreated(String userData) {
        try {
            //Set current user data
            mCurrentUserObject = new JSONObject(userData);
            mCurrentUser.setUserID(mCurrentUserObject.getString(ApiConstants.USER_ID));
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
            mCurrentUser.setUserID(mCurrentUserObject.getString(ApiConstants.USER_ID));
            mCurrentUser.setUserName(mCurrentUserObject.getString(ApiConstants.USER_NAME));
            //TODO: set user token
        } catch (JSONException e) {
            Log.e(TAG,e.getMessage());
        }
        uploadPhoto(mMediaUri);
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
        uploadPhoto(mMediaUri);
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

        //TODO: Hide this if logged out + show logged in (or just change text to log out or log in)
        if (id == R.id.logout) {
            mCurrentUser = null;
            SharedPreferencesMethods.ClearAllSharedPreferences(mContext);

            Intent intent = new Intent(MainActivity.this, StartActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        //Start Random Item Activity
        if (id == R.id.action_random) {
            Intent intent = new Intent(MainActivity.this, RandomActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
