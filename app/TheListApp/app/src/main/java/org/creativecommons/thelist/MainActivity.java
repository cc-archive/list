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
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.creativecommons.thelist.adapters.FeedAdapter;
import org.creativecommons.thelist.adapters.MainListItem;
import org.creativecommons.thelist.utils.ApiConstants;
import org.creativecommons.thelist.utils.DividerItemDecoration;
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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fragments.CancelFragment;
import fragments.LoginFragment;
import fragments.TermsFragment;
import fragments.UploadFragment;
import swipedismiss.SwipeDismissRecyclerViewTouchListener;


public class MainActivity extends ActionBarActivity implements LoginFragment.LoginClickListener,
        TermsFragment.TermsClickListener, UploadFragment.UploadListener, CancelFragment.CancelListener {
    public static final String TAG = MainActivity.class.getSimpleName();
    protected Context mContext;
    private Menu menu;

    //Request Methods
    RequestMethods requestMethods;
    SharedPreferencesMethods sharedPreferencesMethods;
    ListUser mCurrentUser = new ListUser(this);

    protected JSONObject mCurrentUserObject;
    protected MainListItem mCurrentItem;
    protected int activeItemPosition;
    protected String userID;
    protected Uri mMediaUri;

    //RecyclerView
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mFeedAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<MainListItem> mItemList = new ArrayList<>();

    //UI Elements
    protected ProgressBar mProgressBar;
    protected FrameLayout mFrameLayout;

    //Fragments
    LoginFragment loginFragment = new LoginFragment();
    TermsFragment termsFragment = new TermsFragment();
    UploadFragment uploadFragment = new UploadFragment();
    CancelFragment cancelFragment = new CancelFragment();


    // --------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        sharedPreferencesMethods = new SharedPreferencesMethods(mContext);
        requestMethods = new RequestMethods(mContext);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        //Google Analytics Tracker
//        Tracker t = ((ListApplication) MainActivity.this.getApplication()).getTracker(
//                ListApplication.TrackerName.GLOBAL_TRACKER);
//
//        t.setScreenName(TAG);
//        t.send(new HitBuilders.AppViewBuilder().build());

        //Check if user is logged in
        userID = sharedPreferencesMethods.getUserId();
        if(userID == null) {
            mCurrentUser.setLogInState(false);
            Log.v("YO" + TAG, "NOT LOGGED IN");
        } else {
            mCurrentUser.setLogInState(true);
            mCurrentUser.setUserID(userID);
            Log.v("YO " + TAG, "LOGGED IN");
        }

        //Load UI Elements
        mProgressBar = (ProgressBar) findViewById(R.id.feed_progressBar);
        mFrameLayout = (FrameLayout)findViewById(R.id.overlay_fragment_container);

        //RecyclerView
        mRecyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
        mRecyclerView.addItemDecoration(itemDecoration);
        mLayoutManager = new LinearLayoutManager(this);
        mFeedAdapter = new FeedAdapter(mContext, mItemList, MainActivity.this);
        mRecyclerView.setAdapter(mFeedAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);

        initRecyclerView();

        //If Network Connection is available, get User’s Items (API, or local if not logged in)
        if(requestMethods.isNetworkAvailable(mContext)) {
            mProgressBar.setVisibility(View.VISIBLE);
            getUserListItems();
        }
        else {
            Toast.makeText(this, "Network is unavailable", Toast.LENGTH_LONG).show();
        }
    } //onCreate

    @Override
    public void onResume() {
        super.onResume();
        //Log.v("ON RESUME ", "IS BEING CALLED");
        if(mCurrentUser.isLoggedIn()){
            getUserListItems();
        } else {
            mRecyclerView.setVisibility(View.INVISIBLE);
            mFeedAdapter.notifyDataSetChanged();
        }

    } //onResume

    public void CheckComplete() {
        Log.v("Check complete", "start");
        for(int i = 0; i < mItemList.size(); i++) {
            if (!mItemList.get(i).completed) {
                return;
            }
        }
        mProgressBar.setVisibility(View.INVISIBLE);
        Collections.reverse(mItemList);
        mFeedAdapter.notifyDataSetChanged();
        mRecyclerView.setVisibility(View.VISIBLE);
    } //CheckComplete

    //GET USER LIST ITEMS
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
                        mItemList.clear();

                        for(int i=0; i < response.length(); i++) {
                            try {
                                JSONObject singleListItem = response.getJSONObject(i);
                                //Only show items in the user’s list that have not been completed
                                if (singleListItem.getString(ApiConstants.ITEM_COMPLETED) == null ||
                                        singleListItem.getString(ApiConstants.ITEM_COMPLETED).equals("null")) {
                                    MainListItem listItem = new MainListItem();
                                    listItem.setItemName(singleListItem.getString(ApiConstants.ITEM_NAME));
                                    listItem.setMakerName(singleListItem.getString(ApiConstants.MAKER_NAME));
                                    listItem.setItemID(singleListItem.getString(ApiConstants.ITEM_ID));
                                    mItemList.add(listItem);
                                } else {
                                    //TODO: Does THIS WORK?
                                   continue;
                                }
                            } catch (JSONException e) {
                                Log.v(TAG, e.getMessage());
                            }
                        }
                        mProgressBar.setVisibility(View.INVISIBLE);
                        Collections.reverse(mItemList);
                        mFeedAdapter.notifyDataSetChanged();
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
        else { //IF USER IS NOT LOGGED IN
            itemIds = sharedPreferencesMethods.RetrieveUserItemPreference();
            for(int i=0; i < itemIds.length(); i++) {
                    //TODO: do I need to set ItemID here?
                    MainListItem listItem = new MainListItem();
                try {
                    listItem.setItemID(String.valueOf(itemIds.getInt(i)));
                    listItem.setRequestMethods(requestMethods);
                    listItem.setMainActivity(MainActivity.this);
                    listItem.createNewUserListItem();
                } catch (JSONException e) {
                    Log.v(TAG,e.getMessage());
                }
                mItemList.add(listItem);
                //Log.v("HELLO ITEMS", mItemList.toString());
            }
            mFeedAdapter.notifyDataSetChanged();
        }
    } //getUserListItems

    private void initRecyclerView(){
        SwipeDismissRecyclerViewTouchListener touchListener =
                new SwipeDismissRecyclerViewTouchListener(
                        mRecyclerView,
                        new SwipeDismissRecyclerViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    // TODO: this is temp solution for preventing blinking item onDismiss
                                    mLayoutManager.findViewByPosition(position).setVisibility(View.GONE);
                                    mItemList.remove(position);
                                    mFeedAdapter.notifyItemRemoved(position);
                                    mFeedAdapter.notifyItemRangeChanged(position, mItemList.size());
                                    //TODO: remove item from user list

                                }
                            }
                        });
        mRecyclerView.setOnTouchListener(touchListener);
        // Setting this scroll listener is required to ensure that during ListView scrolling,
        // we don't look for swipes.
        mRecyclerView.setOnScrollListener(touchListener.makeScrollListener());
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this,
                new OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setItems(R.array.listItem_choices, mDialogListener);
                        AlertDialog dialog = builder.create();
                        dialog.show();

                        activeItemPosition = position;
                        mCurrentItem = mItemList.get(position);
                    }
                }));
    } //initRecyclerView

    //For Swipe to Dismiss
    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }
    //For Swipe to Dismiss
    public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
        private OnItemClickListener mListener;
        GestureDetector mGestureDetector;

        public RecyclerItemClickListener(Context context, OnItemClickListener listener) {
            mListener = listener;
            mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    //Log.v("HI", "ON SINGLE TAG UP CALLED");
                    return true;
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
            View childView = view.findChildViewUnder(e.getX(), e.getY());
            if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
                mListener.onItemClick(childView, view.getChildPosition(childView));
            }
            return false;
        }
        @Override
        public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {
            //Log.v("HI", "ON TOUCH EVENT CALLED");
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
                                // Display an error
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
                    mMediaUri = data.getData();
                }
            }
            Log.i(TAG,"Media URI:" + mMediaUri);

            //TODO: Check file size
            if(mCurrentUser.isLoggedIn()) {
                startPhotoUpload();

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

    //Start UploadFragment and upload photo
    public void startPhotoUpload(){
        Bundle b = new Bundle();
        b.putSerializable(getString(R.string.item_id_bundle_key), mCurrentItem.getItemID());
        b.putSerializable(getString(R.string.uri_bundle_key), mMediaUri.toString());
        uploadFragment.setArguments(b);

        //Load Upload Fragment
        getSupportFragmentManager().beginTransaction()
                .add(R.id.overlay_fragment_container,uploadFragment).commit();
        mFrameLayout.setClickable(true);
        getSupportActionBar().hide();

    }
    //When New User fills out sign up (save data locally)
    @Override
    public void UserCreated(String userData) {
        try {
            //Set current user data
            //TODO: get user password as well
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

    @Override
    public void UserLoggedIn(String userData) {
        //Create menu again (update login to logout)
        invalidateOptionsMenu();
        //Start UploadFragment and Upload photo
        startPhotoUpload();
    } //UserLoggedIn

    @Override
    public void CancelUpload() {
        //Show cancelledFragment
        getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.overlay_fragment_container, cancelFragment)
                .commit();
    } //CancelUpload

    @Override
    public void onTermsClicked() {
        //Start UploadFragment and Upload photo
        startPhotoUpload();
    } //onTermsClicked

    @Override
    public void onTermsCancelled() {
        //Show cancelledFragment
        getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.overlay_fragment_container, cancelFragment)
                .commit();
    }

    //When UploadFragment has gotten response from server
    @Override
    public void onUploadFinish() {
        //Refresh user list + remove item that has just been uploaded
        //TODO: do I still need this?
        getUserListItems();
        //mItemList.remove(activeItemPosition);
        mFeedAdapter.notifyDataSetChanged();

        //Show upload message for limited time
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getSupportFragmentManager().beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .remove(uploadFragment)
                        .commit();
            }
        }, 2800);

        //Delay action bar and ability to click MainFeed
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mFrameLayout.setClickable(false);
                getSupportActionBar().show();
            }
        }, 3000);

    } //onUploadFinish

    @Override
    public void onCancelStart() {
        //Remove cancelFragment after set period
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getSupportFragmentManager().beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .remove(cancelFragment)
                        .commit();
            }
        }, 2800);

        //Delay action bar and ability to click MainActivity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mFrameLayout.setClickable(false);
                getSupportActionBar().show();
            }
        }, 3000);
    } //onCancelStart

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        this.menu = menu;

        //Make login or logout visible based on login status
        updateMenuTitles();
        return true;
        //return super.onCreateOptionsMenu(menu);
    }

    private void updateMenuTitles(){
        MenuItem logOut = menu.findItem(R.id.logout);
        MenuItem logIn = menu.findItem(R.id.login);
        if(mCurrentUser.isLoggedIn()){
            Log.v(TAG, "YOU ARE LOGGED IN");
            logOut.setVisible(true);
            logIn.setVisible(false);
        } else {
            logOut.setVisible(false);
            logIn.setVisible(true);
            Log.v(TAG, "YOU ARE NOT LOGGED IN");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()){
            case R.id.login:
                Intent loginIntent = new Intent(MainActivity.this, AccountActivity.class);
                startActivity(loginIntent);
                return true;
            case R.id.logout:
                mCurrentUser.logOut();
                userID = null;
                return true;
            case R.id.action_random:
                Intent hitMeIntent = new Intent(MainActivity.this, RandomActivity.class);
                startActivity(hitMeIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
