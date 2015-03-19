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
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.VolleyError;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.melnykov.fab.FloatingActionButton;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.ActionClickListener;
import com.nispok.snackbar.listeners.EventListener;

import org.creativecommons.thelist.R;
import org.creativecommons.thelist.adapters.FeedAdapter;
import org.creativecommons.thelist.adapters.MainListItem;
import org.creativecommons.thelist.authentication.AccountGeneral;
import org.creativecommons.thelist.swipedismiss.SwipeDismissRecyclerViewTouchListener;
import org.creativecommons.thelist.utils.ApiConstants;
import org.creativecommons.thelist.utils.ListApplication;
import org.creativecommons.thelist.utils.ListUser;
import org.creativecommons.thelist.utils.MaterialInterpolator;
import org.creativecommons.thelist.utils.MessageHelper;
import org.creativecommons.thelist.utils.PhotoConstants;
import org.creativecommons.thelist.utils.RecyclerItemClickListener;
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


public class MainActivity extends ActionBarActivity {
    public static final String TAG = MainActivity.class.getSimpleName();
    protected Context mContext;

    //Helper Methods
    RequestMethods mRequestMethods;
    SharedPreferencesMethods mSharedPref;
    MessageHelper mMessageHelper;
    ListUser mCurrentUser;

    protected MainListItem mCurrentItem;
    protected int activeItemPosition;

    protected MainListItem mItemToBeUploaded;
    protected int uploadItemPosition;

    protected MainListItem mLastDismissedItem;
    protected int lastDismissedItemPosition;
    protected Uri mMediaUri;

    //RecyclerView
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mFeedAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<MainListItem> mItemList = new ArrayList<>();

    //UI Elements
    private Menu menu;
    private FloatingActionButton mFab;
    protected ProgressBar mProgressBar;
    protected RelativeLayout mUploadProgressBar;
    protected FrameLayout mFrameLayout;
    protected TextView mEmptyView;

    //Checks
    //TODO: check if still needed
    //private boolean photoToBeUploaded;
    //private boolean menuLogin;

    // --------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        mMessageHelper = new MessageHelper(mContext);
        mRequestMethods = new RequestMethods(mContext);
        mSharedPref = new SharedPreferencesMethods(mContext);
        mCurrentUser = new ListUser(MainActivity.this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        //Google Analytics Tracker
        ((ListApplication) getApplication()).getTracker(ListApplication.TrackerName.GLOBAL_TRACKER);

        //Load UI Elements
        mProgressBar = (ProgressBar) findViewById(R.id.feedProgressBar);
        mUploadProgressBar = (RelativeLayout) findViewById(R.id.photoProgressBar);
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
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.feedSwipeRefresh);
        mRecyclerView = (RecyclerView)findViewById(R.id.feedRecyclerView);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //TODO: Try dividers in layout instead?
//        RecyclerView.ItemDecoration itemDecoration =
//                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
//        mRecyclerView.addItemDecoration(itemDecoration);
        mLayoutManager = new LinearLayoutManager(this);
        mFeedAdapter = new FeedAdapter(mContext, mItemList);
        mRecyclerView.setAdapter(mFeedAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
        initRecyclerView();

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                displayUserItems();
            }
        });

    } //onCreate

    @Override
    protected void onRestart(){
        super.onRestart();
        //Log.v(TAG, "ON RESTART CALLED");
        if(!mCurrentUser.isTempUser() && mCurrentUser.getAnalyticsOptOut() != null){
            mSharedPref.setAnalyticsViewed(true);
        }
    }

    @Override
    protected void onStart(){
        super.onStart();

        if(!mCurrentUser.isTempUser() && !mSharedPref.getAnalyticsViewed()){
            //TODO: check app version
            //If user is logged in but has not opted into/out of GA
            Log.v(TAG, "logged in without opt out response");
            mMessageHelper.enableFeatureDialog(mContext, getString(R.string.dialog_ga_title),
                    getString(R.string.dialog_ga_message),
                    new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            super.onPositive(dialog);
                            mCurrentUser.setAnalyticsOptOut(false);
                            GoogleAnalytics.getInstance(mContext).setAppOptOut(false);
                            dialog.dismiss();
                        }
                        @Override
                        public void onNegative(MaterialDialog dialog) {
                            super.onNegative(dialog);
                            mCurrentUser.setAnalyticsOptOut(true);
                            GoogleAnalytics.getInstance(mContext).setAppOptOut(true);
                            dialog.dismiss();

                        }
                    });
            mSharedPref.setAnalyticsViewed(true);
            Log.v(TAG, "SET ANALYTICS VIEWED TRUE");
        }

        GoogleAnalytics.getInstance(this).reportActivityStart(this);
    } //onStart

    @Override
    protected void onStop(){
        super.onStop();
        GoogleAnalytics.getInstance(this).reportActivityStop(this);
    }

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

        if(mItemToBeUploaded != null){
            return;
        }

        if(!(mCurrentUser.isTempUser())) { //if this is not a temp user
            Log.v(TAG, " > User is logged in");
            displayUserItems();
        } else { //if user is a temp
            Log.v(TAG, " > User is not logged in");
            if(mItemList.size() == 0){
                mRecyclerView.setVisibility(View.INVISIBLE);
                displayUserItems();
            } else {
                mFeedAdapter.notifyDataSetChanged();
                mRecyclerView.setVisibility(View.VISIBLE);
            }
        }
    } //onResume

    //----------------------------------------------
    //LIST ITEM REQUEST + UPDATE VIEW
    //----------------------------------------------

    private void displayUserItems() {
        JSONArray itemIds;

        if(!(mCurrentUser.isTempUser())) { //IF USER IS NOT A TEMP
            mRequestMethods.getUserItems(new RequestMethods.ResponseCallback() {
                @Override
                public void onSuccess(JSONArray response) {
                    Log.v(TAG , "> getUserItems > onSuccess: " + response.toString());
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
                                MainListItem listItem = new MainListItem();
                                listItem.setItemName
                                        (singleListItem.getString(ApiConstants.ITEM_NAME));
                                listItem.setMakerName
                                        (singleListItem.getString(ApiConstants.MAKER_NAME));
                                listItem.setItemID
                                        (singleListItem.getString(ApiConstants.ITEM_ID));
                                listItem.setError(true);
                                //TODO: QA (add error items to the top)
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
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                } //onSuccess
                @Override
                public void onFail(VolleyError error) {
                    Log.d(TAG , "> getUserItems > onFail: " + error.toString());
                }
            });
        }
        else { //IF USER IS A TEMP
            mItemList.clear();
            mEmptyView.setVisibility(View.GONE);
            //Get items selected from SharedPref
            itemIds = mSharedPref.getUserItemPreference();

            if (itemIds != null && itemIds.length() > 0) {
                for (int i = 0; i < itemIds.length(); i++) {
                    //TODO: do I need to set ItemID here?
                    MainListItem listItem = new MainListItem();
                    try {
                        listItem.setItemID(String.valueOf(itemIds.getInt(i)));
                        listItem.setMessageHelper(mMessageHelper);
                        listItem.setMainActivity(MainActivity.this);
                        listItem.createNewUserListItem();
                    } catch (JSONException e) {
                        Log.v(TAG, e.getMessage());
                    }
                    mItemList.add(listItem);
                }
                Collections.reverse(mItemList);
                mFeedAdapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
            } else {
                mProgressBar.setVisibility(View.INVISIBLE);
                mEmptyView.setVisibility(View.VISIBLE);
                mFab.show();
                mFab.setVisibility(View.VISIBLE);
            }
        }
    } //displayUserItems

    //For temp users displayUserItems:
    // Check if all items have been returned from API before displaying list
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


    //----------------------------------------------
    //RECYCLERVIEW – LIST ITEM INTERACTION
    //----------------------------------------------

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
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setItems(R.array.listItem_choices, mDialogListener);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        //Get item details for photo upload
                        activeItemPosition = position;
                        mCurrentItem = mItemList.get(position);
                        //Log.v(TAG + "CURRENT ITEM", mCurrentItem.toString());
                    }
                }));
    } //initRecyclerView


    //----------------------------------------------
    //SNACKBAR – UNDO ITEM DELETION
    //----------------------------------------------

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
                                //TODO: QA
                                //If no more items
                                if(mItemList.isEmpty()){
                                    mEmptyView.setVisibility(View.VISIBLE);
                                }
                                //If fab is hidden (bug fix?)
                                if(!mFab.isVisible()){
                                    mFab.show();
                                }
                            }
                        }) //event listener
                , MainActivity.this);
    } //showSnackbar


    //----------------------------------------------
    //TAKING PHOTO/PHOTO SELECTION
    //----------------------------------------------

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
        switch(requestCode){
            case PhotoConstants.PICK_PHOTO_REQUEST:
            case PhotoConstants.TAKE_PHOTO_REQUEST:
                if(resultCode == RESULT_OK) {
                    //photoToBeUploaded = true;
                    mItemToBeUploaded = mCurrentItem;
                    uploadItemPosition = activeItemPosition;

                    if(data == null) {
                        //Toast.makeText(this,getString(R.string.general_error),Toast.LENGTH_LONG).show();
                        Log.d(TAG, "> onActivityResult > data == null");
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
                else if(resultCode != RESULT_CANCELED) { //result other than ok or cancelled
                    //Toast.makeText(this, R.string.general_error, Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "> onActivityResult > resultCode != canceled");
                }
                break;
        } //switch
    } //onActivityResult


    //----------------------------------------------
    //PHOTO UPLOAD
    //----------------------------------------------

    //Start Upload + Respond
    public void startPhotoUpload(){
        if(!(mCurrentUser.isTempUser())){ //IF NOT TEMP USER
            mCurrentUser.getToken(new ListUser.AuthCallback() { //getToken
                @Override
                public void onSuccess(String authtoken) {
                    Log.v(TAG, "> startPhotoUpload > getToken, token received: " + authtoken);

                    mItemList.remove(mItemToBeUploaded);
                    mFeedAdapter.notifyDataSetChanged();
                    performUpload();
                }
            });
        } else {
            mCurrentUser.addNewAccount(AccountGeneral.ACCOUNT_TYPE,
                    AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, new ListUser.AuthCallback() { //addNewAccount
                        @Override
                        public void onSuccess(String authtoken) {
                            Log.d(TAG, "> addNewAccount > onSuccess, authtoken: " + authtoken);
                            try {
                                mItemList.remove(mItemToBeUploaded);
                                mFeedAdapter.notifyDataSetChanged();
                                performUpload();
                            } catch (Exception e) {
                                Log.d(TAG,"addAccount > " + e.getMessage());
                            }
                        }
                    });
        }
    } //startPhotoUpload

    public void performUpload(){
        mUploadProgressBar.setVisibility(View.VISIBLE);
        mRequestMethods.uploadPhoto(mItemToBeUploaded.getItemID(), mMediaUri,
                new RequestMethods.RequestCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "On Upload Success");

                        mMessageHelper.notifyUploadSuccess(mItemToBeUploaded.getItemName());
                        mItemToBeUploaded = null;
                        //photoToBeUploaded = false;
                        displayUserItems();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mUploadProgressBar.setVisibility(View.GONE);
                            }
                        }, 500); //could add a time check from visible to invisible, heh.
                    }
                    @Override
                    public void onFail() {
                        Log.d(TAG, "On Upload Fail");

                        mMessageHelper.notifyUploadFail(mItemToBeUploaded.getItemName());
                        //photoToBeUploaded = false;
                        //mItemlist.add(uploadItemPosition);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mUploadProgressBar.setVisibility(View.GONE);
                                displayUserItems();
                                //TODO: add visual indication that item failed
                            }
                        }, 500);
                    }
                });
    } //performUpload

    //----------------------------------------------
    //MENU + MENU HELPER METHODS
    //----------------------------------------------

    private void updateMenuTitles(){
        MenuItem switchAccounts = menu.findItem(R.id.switch_accounts);

        if(mCurrentUser.getAccountCount() > 0){
            switchAccounts.setVisible(false);
            //TODO: uncomment when Switch Accounts works
            //switchAccounts.setTitle("Switch Accounts");
        } else {
            switchAccounts.setTitle("Add Account");
        }
    } //updateMenuTitles

    private void handleUserAccount(){
        //TODO: bring up account picker dialog w/ new option
        if(mCurrentUser.getAccountCount() > 0){
            mCurrentUser.showAccountPicker(new ListUser.AuthCallback() {
                @Override
                public void onSuccess(String authtoken) {
                    Log.d(TAG, " > switch_accounts MenuItem > showAccountPicker > " +
                            "got authtoken: " + authtoken);
                }
            });
        } else {
            mCurrentUser.addNewAccount(AccountGeneral.ACCOUNT_TYPE,
                AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, new ListUser.AuthCallback() {
                    @Override
                    public void onSuccess(String authtoken) {
                        Log.d(TAG, " > switch_accounts MenuItem > addNewAccount > " +
                                "got authtoken: " + authtoken);
                    }
                });
        }
    } //handleUserAccount

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        this.menu = menu;

        //Show add or switch account based on login status + available accounts
        updateMenuTitles();
        return true;
        //return super.onCreateOptionsMenu(menu);
    } //onCreateOptionsMenu

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()){
            case R.id.switch_accounts:
                    handleUserAccount();
                return true;
            case R.id.gallery:
                Intent galleryIntent = new Intent(MainActivity.this, GalleryActivity.class);
                startActivity(galleryIntent);
                return true;
            case R.id.pick_categories:
                Intent pickCategoriesIntent = new Intent(MainActivity.this, CategoryListActivity.class);
                startActivity(pickCategoriesIntent);
                return true;
            case R.id.about_theapp:
                Intent aboutAppIntent = new Intent(MainActivity.this,AboutActivity.class);
                startActivity(aboutAppIntent);
                return true;
            case R.id.remove_accounts:
                if(mCurrentUser.isTempUser()){
                    mSharedPref.ClearAllSharedPreferences();
                    Intent startIntent = new Intent(MainActivity.this, StartActivity.class);
                    startActivity(startIntent);
                } else {
                    mCurrentUser.removeAccounts(new ListUser.AuthCallback() {
                        @Override
                        //TODO: probably should have its own callback w/out returned value (no authtoken anyway)
                        public void onSuccess(String authtoken) {
                            mSharedPref.ClearAllSharedPreferences();
                            Intent startIntent = new Intent(MainActivity.this, StartActivity.class);
                            startActivity(startIntent);
                        }
                    });
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    } //onOptionsItemsSelected
} //MainActivity
