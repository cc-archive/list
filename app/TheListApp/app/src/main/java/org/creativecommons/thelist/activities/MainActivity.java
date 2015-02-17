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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
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
import android.view.animation.Interpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.melnykov.fab.FloatingActionButton;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.ActionClickListener;
import com.nispok.snackbar.listeners.EventListener;

import org.creativecommons.thelist.R;
import org.creativecommons.thelist.adapters.FeedAdapter;
import org.creativecommons.thelist.adapters.MainListItem;
import org.creativecommons.thelist.authentication.AccountGeneral;
import org.creativecommons.thelist.fragments.CancelFragment;
import org.creativecommons.thelist.fragments.UploadFragment;
import org.creativecommons.thelist.misc.AccountFragment;
import org.creativecommons.thelist.misc.MaterialInterpolator;
import org.creativecommons.thelist.swipedismiss.SwipeDismissRecyclerViewTouchListener;
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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class MainActivity extends ActionBarActivity implements UploadFragment.UploadListener, CancelFragment.CancelListener {
    public static final String TAG = MainActivity.class.getSimpleName();
    protected Context mContext;
    private Menu menu;

    //Request Methods
    RequestMethods requestMethods;
    SharedPreferencesMethods sharedPreferencesMethods;
    ListUser mCurrentUser;

    protected MainListItem mCurrentItem;
    protected int activeItemPosition;
    protected MainListItem mLastDismissedItem;
    protected int lastDismissedItemPosition;
    protected Uri mMediaUri;

    //RecyclerView
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mFeedAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<MainListItem> mItemList = new ArrayList<>();

    //UI Elements
    private FloatingActionButton mFab;
    protected ProgressBar mProgressBar;
    protected FrameLayout mFrameLayout;
    protected TextView mEmptyView;

    //Fragments
    AccountFragment accountFragment = new AccountFragment();
    UploadFragment uploadFragment = new UploadFragment();
    CancelFragment cancelFragment = new CancelFragment();

    //Checks
    //TODO: check if still needed
    private boolean photoToBeUploaded;
    private boolean menuLogin;

    // --------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        sharedPreferencesMethods = new SharedPreferencesMethods(mContext);
        requestMethods = new RequestMethods(mContext);
        mCurrentUser = new ListUser(MainActivity.this);
        menuLogin = false;

        Log.v(TAG, "MAINACTIVITY ON CREATE");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

//        GoogleAnalytics instance = GoogleAnalytics.getInstance(this);
//        instance.setAppOptOut(true);
//
//        //Google Analytics Tracker
//        Tracker t = ((ListApplication) MainActivity.this.getApplication()).getTracker(
//                ListApplication.TrackerName.GLOBAL_TRACKER);
//
//        t.setScreenName(TAG);
//        t.send(new HitBuilders.AppViewBuilder().build());

        //Load UI Elements
        mProgressBar = (ProgressBar) findViewById(R.id.feed_progressBar);
        mEmptyView = (TextView) findViewById(R.id.empty_list_label);
        mFrameLayout = (FrameLayout)findViewById(R.id.overlay_fragment_container);
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setEnabled(false);
        mFab.setVisibility(View.GONE);
        mFab.hide();

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent hitMeIntent = new Intent(MainActivity.this, RandomActivity.class);
                startActivity(hitMeIntent);
            }
        });

        //RecyclerView
        mRecyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
//        RecyclerView.ItemDecoration itemDecoration =
//                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
//        mRecyclerView.addItemDecoration(itemDecoration);
        mLayoutManager = new LinearLayoutManager(this);
        mFeedAdapter = new FeedAdapter(mContext, mItemList);
        mRecyclerView.setAdapter(mFeedAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
        initRecyclerView();
    } //onCreate

    @Override
    public void onResume() {
        super.onResume();

        //Update menu for login/logout options
        invalidateOptionsMenu();

        if(!mFab.isVisible()){
            mFab.show();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mFab.setEnabled(true);
            }
        }, 500);

        if(!(mCurrentUser.isTempUser())) { //if this is not a temp user
            displayUserListItems();
            Log.v("NOT A TEMP: ", "legit user");

        } else { //if user is a temp
            Log.v("I R A TEMP: ", "not logged in");
            if(mItemList.size() == 0){
                Log.v("THERE ARE NO ITEMS: ", "Annd not logged in");
                mRecyclerView.setVisibility(View.INVISIBLE);
                displayUserListItems();
            } else {
                Log.v("THERE ARE ITEMS: ", "Annd not logged in");
                mFeedAdapter.notifyDataSetChanged();
                mRecyclerView.setVisibility(View.VISIBLE);
            }
        }
    } //onResume

    //For temp users displayUserListItems:
    // Checks if all items have been returned from API before displaying list
    public void CheckComplete() {
        Log.v("Check complete", "start");
        for(int i = 0; i < mItemList.size(); i++) {
            if (!mItemList.get(i).completed) {
                return;
            }
        }
        mProgressBar.setVisibility(View.INVISIBLE);
        mFab.show();
        mFab.setVisibility(View.VISIBLE);
        mFeedAdapter.notifyDataSetChanged();
        mRecyclerView.setVisibility(View.VISIBLE);
    } //CheckComplete

    //GET USER LIST ITEMS
    private void displayUserListItems() {
        JSONArray itemIds;

        if(!(mCurrentUser.isTempUser())) { //IF USER IS NOT A TEMP
            Log.v("HELLO", "this user is logged in");
            if(!(requestMethods.isNetworkAvailable())){
                requestMethods.showErrorDialog(mContext, getString(R.string.error_network_title),
                        getString(R.string.error_network_message));
                return;
            }

            //Get usertoken to start request
            mCurrentUser.getToken(new ListUser.AuthCallback() {
                @Override
                public void onSuccess(String authtoken) {
                    requestUserListItems(authtoken);
                }
            });
        }
        else { //IF USER IS A TEMP
            mItemList.clear();
            mEmptyView.setVisibility(View.GONE);
            //Get items selected from SharedPref
            itemIds = sharedPreferencesMethods.RetrieveUserItemPreference();

            if (itemIds != null || !(itemIds.length() == 0)) {
                for (int i = 0; i < itemIds.length(); i++) {
                    //TODO: do I need to set ItemID here?
                    MainListItem listItem = new MainListItem();
                    try {
                        listItem.setItemID(String.valueOf(itemIds.getInt(i)));
                        listItem.setRequestMethods(requestMethods);
                        listItem.setMainActivity(MainActivity.this);
                        listItem.createNewUserListItem();
                    } catch (JSONException e) {
                        Log.v(TAG, e.getMessage());
                    }
                    mItemList.add(listItem);
                    Log.v("HELLO ITEMS", mItemList.toString());
                }
                Collections.reverse(mItemList);
                mFeedAdapter.notifyDataSetChanged();
            } else {
                mProgressBar.setVisibility(View.INVISIBLE);
                mEmptyView.setVisibility(View.VISIBLE);
                mFab.show();
                mFab.setVisibility(View.VISIBLE);
            }
        }
    } //displayUserListItems

    private void requestUserListItems(String authtoken){
        RequestQueue queue = Volley.newRequestQueue(this);
        final String auth = authtoken;
        String url = ApiConstants.GET_USER_LIST + mCurrentUser.getUserID();

        final JsonArrayRequest userListRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.v("Get USER LIST RESPONSE", response.toString());
                        mItemList.clear();

                        for(int i=0; i < response.length(); i++) {
                            try {
                                JSONObject singleListItem = response.getJSONObject(i);
                                //Only show items in the user’s list that have not been completed
                                if (singleListItem.getInt(ApiConstants.ITEM_COMPLETED) == 0) {
                                    MainListItem listItem = new MainListItem();
                                    listItem.setItemName
                                            (singleListItem.getString(ApiConstants.ITEM_NAME));
                                    listItem.setMakerName
                                            (singleListItem.getString(ApiConstants.MAKER_NAME));
                                    listItem.setItemID
                                            (singleListItem.getString(ApiConstants.ITEM_ID));
                                    mItemList.add(listItem);
                                } else if(singleListItem.getInt(ApiConstants.ITEM_COMPLETED) == 1) {
                                    //TODO: Does this work? (add error items to the top)
                                    MainListItem listItem = new MainListItem();
                                    listItem.setItemName
                                            (singleListItem.getString(ApiConstants.ITEM_NAME));
                                    listItem.setMakerName
                                            (singleListItem.getString(ApiConstants.MAKER_NAME));
                                    listItem.setItemID
                                            (singleListItem.getString(ApiConstants.ITEM_ID));
                                    listItem.setError(true);
                                    mItemList.add(0, listItem);
                                } else {
                                    continue;
                                }
                            } catch (JSONException e) {
                                Log.v(TAG, e.getMessage());
                            }
                        }
                        mProgressBar.setVisibility(View.INVISIBLE);
                        mFab.show();
                        mFab.setVisibility(View.VISIBLE);

                        if(mItemList.size() == 0){
                            //TODO: show textView
                            mEmptyView.setVisibility(View.VISIBLE);

                        } else {
                            //TODO: hide textView
                            mEmptyView.setVisibility(View.GONE);
                            Collections.reverse(mItemList);
                            mFeedAdapter.notifyDataSetChanged();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error", error.toString());
                requestMethods.showErrorDialog(mContext,
                        getString(R.string.error_title),
                        getString(R.string.error_message));
            }
        }) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();
                params.put(ApiConstants.USER_TOKEN, auth);
                return params;
            }
        };
        queue.add(userListRequest);
    } //requestUserListItems

    private void initRecyclerView(){
        SwipeDismissRecyclerViewTouchListener touchListener = new SwipeDismissRecyclerViewTouchListener(
                        mRecyclerView, new SwipeDismissRecyclerViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }
                            @Override
                            public void onDismiss(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    // TODO: this is temp solution for preventing blinking item onDismiss <-- OMG DEATH
                                    mLayoutManager.findViewByPosition(position).setVisibility(View.GONE);
                                    //Get item details for UNDO
                                    lastDismissedItemPosition = position;
                                    mLastDismissedItem = mItemList.get(position);

                                    //What happens when item is swiped offscreen
                                    mItemList.remove(mLastDismissedItem);
                                    //TODO: should this be after snackbar removal?
                                    mCurrentUser.removeItemFromUserList(mLastDismissedItem.getItemID());

                                    // do not call notifyItemRemoved for every item, it will cause gaps on deleting items
                                    mFeedAdapter.notifyDataSetChanged();

                                    //Snackbar message
                                    showSnackbar();
                                }
                            }
                        });
        mRecyclerView.setOnTouchListener(touchListener);
        // Setting this scroll listener is required to ensure that during ListView scrolling,
        // we don't look for swipes.
        LinearLayoutManager llm = (LinearLayoutManager)mRecyclerView.getLayoutManager();
        mRecyclerView.setOnScrollListener(touchListener.makeScrollListener(llm, mFab));

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this,
                new OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setItems(R.array.listItem_choices, mDialogListener);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        //Get item details for photo upload
                        activeItemPosition = position;
                        mCurrentItem = mItemList.get(position);
                        Log.v(TAG + "CURRENT ITEM", mCurrentItem.toString());
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
    } //RecyclerItemClickListener

    public void showSnackbar(){
        SnackbarManager.show(
                //also includes duration: SHORT, LONG, INDEFINITE
                Snackbar.with(mContext)
                        .text("Item deleted") //text to display
                        .actionColor(getResources().getColor(R.color.colorSecondary))
                        .actionLabel("undo".toUpperCase())
                        .actionListener(new ActionClickListener() {
                            @Override
                            public void onActionClicked(Snackbar snackbar) {
                                /*NOTE: item does not need to be re-added here because it is only
                                removed when the snackbar is actually dismissed*/

                                //What happens when item is swiped offscreen
                                mItemList.add(0, mLastDismissedItem);
                                //re-add item to user’s list in DB
                                mCurrentUser.addItemToUserList(mLastDismissedItem.getItemID());
                                mFeedAdapter.notifyDataSetChanged();
                                mLayoutManager.scrollToPosition(0);
                                mFab.show();
                            }
                        }) //action button’s listener
                        .eventListener(new EventListener() {
                            Interpolator interpolator = new MaterialInterpolator();
                            @Override
                            public void onShow(Snackbar snackbar) {
                                TranslateAnimation tsa = new TranslateAnimation(0, 0, 0,
                                        -snackbar.getHeight());
                                tsa.setInterpolator(interpolator);
                                tsa.setFillAfter(true);
                                tsa.setFillEnabled(true);
                                tsa.setDuration(300);
                                mFab.startAnimation(tsa);
                            }
                            @Override
                            public void onShown(Snackbar snackbar) {
                            }
                            @Override
                            public void onDismiss(Snackbar snackbar) {

                                TranslateAnimation tsa2 = new TranslateAnimation(0, 0,
                                        -snackbar.getHeight(), 0);
                                tsa2.setInterpolator(interpolator);
                                tsa2.setFillAfter(true);
                                tsa2.setFillEnabled(true);
                                tsa2.setStartOffset(100);
                                tsa2.setDuration(300);
                                mFab.startAnimation(tsa2);
                            }
                            @Override
                            public void onDismissed(Snackbar snackbar) {
                            }
                        }) //event listener
                , MainActivity.this);
    } //showSnackbar

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

        Log.v("RESULTCODE: ", (String.valueOf(resultCode)));
        switch(requestCode){
            case PhotoConstants.PICK_PHOTO_REQUEST:
            case PhotoConstants.TAKE_PHOTO_REQUEST:
                if(resultCode == RESULT_OK) {
                    photoToBeUploaded = true;

                    Log.v("RESULTCODE OK ", (String.valueOf(resultCode)));
                    if(data == null) {
                        Toast.makeText(this,getString(R.string.general_error),Toast.LENGTH_LONG).show();
                    }
                    else {
                        mMediaUri = data.getData();
                    }
                    Log.i(TAG,"Media URI:" + mMediaUri);

                    //TODO: make sure for sure auth will exist for this to happen
                    //Add photo to the Gallery (listen for broadcast and let gallery take action)
                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    mediaScanIntent.setData(mMediaUri);
                    sendBroadcast(mediaScanIntent);

                    startPhotoUpload();
                } //RESULT OK
                else if(resultCode != RESULT_CANCELED) { //result cancelled
                    Toast.makeText(this, R.string.general_error, Toast.LENGTH_SHORT).show();
                }
                break;
        } //switch
    } //onActivityResult

    //Start UploadFragment and upload photo
    public void startPhotoUpload(){
        Log.d(TAG, "Starting photo upload");

        if(!(mCurrentUser.isTempUser())){ //IF NOT TEMP USER
            mCurrentUser.getToken(new ListUser.AuthCallback() { //getToken
                @Override
                public void onSuccess(String authtoken) {
                    Log.d("THIS IS AUTH IN SPU", authtoken);
                    Log.d("USER ID: ", mCurrentUser.getUserID());

                    Bundle b = new Bundle();
                    b.putSerializable(getString(R.string.item_id_bundle_key), mCurrentItem.getItemID());
                    b.putSerializable(getString(R.string.uri_bundle_key), mMediaUri.toString());
                    b.putSerializable(getString(R.string.token_bundle_key), authtoken);
                    uploadFragment.setArguments(b);

                    //Load Upload Fragment
                    getSupportFragmentManager().beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .add(R.id.overlay_fragment_container, uploadFragment).commit();
                    mFrameLayout.setClickable(true);
                    getSupportActionBar().hide();
                }
            });
        } //not temp user
        else { //IF IS TEMP USER
            Log.d(TAG, "IS TEMP USER TRYING TO CREATE ACCOUNT");
            mCurrentUser.addNewAccount(AccountGeneral.ACCOUNT_TYPE,
                    AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, new ListUser.AuthCallback() { //addNewAccount
                        @Override
                        public void onSuccess(String authtoken) {
                            Log.d(TAG, "IS TEMP USER RETURNING BUNDLE ");
                            try {
                                Bundle b = new Bundle();
                                Log.d("THE LIST", "AddNewAccount Bundle is " + b);
                                b.putSerializable(getString(R.string.item_id_bundle_key), mCurrentItem.getItemID());
                                b.putSerializable(getString(R.string.uri_bundle_key), mMediaUri.toString());
                                b.putSerializable(getString(R.string.token_bundle_key), authtoken);
                                uploadFragment.setArguments(b);
                                Log.d(TAG, "uploadFragment setting arguments");

                                //Load Upload Fragment
                                getSupportFragmentManager().beginTransaction()
                                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                        .add(R.id.overlay_fragment_container,uploadFragment).commit();
                                mFrameLayout.setClickable(true);
                                getSupportActionBar().hide();
                                Log.d(TAG, "uploadFragment put in fragment container");

                            } catch (Exception e) {
                                Log.d(TAG,"addAccount > " + e.getMessage());
                            }
                        }
                    });
        }
    } //startUploadPhoto

    //When UploadFragment has gotten response from server
    @Override
    public void onUploadFinish() {
        Log.d(TAG, "On Upload Finish");
        //Refresh user list + remove item that has just been uploaded
        //TODO: create new check for this (invalidate when photo is uploaded?)
        photoToBeUploaded = false;
        displayUserListItems();

        //TODO: why does this need to be here?
        mFeedAdapter.notifyDataSetChanged();
        //Show upload message for limited time
        removeFragment(uploadFragment, true);
    } //onUploadFinish

    @Override
    public void onCancelStart() {
        Log.d(TAG, "On Cancel Start");
        displayUserListItems();
        //Remove cancelFragment after set period
        removeFragment(cancelFragment, true);
    } //onCancelStart

    //REMOVE FRAGMENT HELPER
    public void removeFragment(final Fragment f, boolean delay){
        if(delay){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getSupportFragmentManager().beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .remove(f)
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
        } else {
            getSupportFragmentManager().beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .remove(f)
                    .commit();
            mFrameLayout.setClickable(false);
            getSupportActionBar().show();
        }
    } //removeFragment

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        this.menu = menu;

        //Make login or logout visible based on login status
        //TODO: update when createAccount is possible
        //updateMenuTitles();
        return true;
        //return super.onCreateOptionsMenu(menu);
    } //onCreateOptionsMenu

    //private void updateMenuTitles(){
        //MenuItem switchAccounts = menu.findItem(R.id.switch_accounts);
        //MenuItem createAccount = menu.findItem(R.id.login);
        //MenuItem logOut = menu.findItem(R.id.logout);
        //TODO: turn this back on
        //if(!(mCurrentUser.isTempUser())){
            //logOut.setVisible(true);
            //switchAccounts.setVisible(true);
            //createAccount.setVisible(false);
        //} else {
            //logOut.setVisible(false);
            //switchAccounts.setVisible(false);
            //createAccount.setVisible(true);
        //}
    //} //updateMenuTitles

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()){
            case R.id.switch_accounts:
                //TODO: bring up account picker dialog w/ new option
                mCurrentUser.showAccountPicker(new ListUser.AuthCallback() {
                    @Override
                    public void onSuccess(String authtoken) {
                        Log.d(TAG, " > switch_accounts MenuItem > showAccountPicker > got authtoken: " + authtoken);
                    }
                });
                return true;
            case R.id.pick_categories:
                Intent pickCategoriesIntent = new Intent(MainActivity.this, CategoryListActivity.class);
                startActivity(pickCategoriesIntent);
                return true;
            case R.id.about_theapp:
                //TODO: go to scrollview of app things
                return true;
            case R.id.remove_accounts:
                sharedPreferencesMethods.ClearAllSharedPreferences();
                mCurrentUser.removeAccounts(new ListUser.AuthCallback() {
                    @Override
                    public void onSuccess(String authtoken) {
                        Intent startIntent = new Intent(MainActivity.this, StartActivity.class);
                        startActivity(startIntent);
                    }
                });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    } //onOptionsItemsSelected
} //MainActivity
