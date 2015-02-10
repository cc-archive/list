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

import org.creativecommons.thelist.adapters.FeedAdapter;
import org.creativecommons.thelist.adapters.MainListItem;
import org.creativecommons.thelist.fragments.AccountFragment;
import org.creativecommons.thelist.fragments.CancelFragment;
import org.creativecommons.thelist.fragments.TermsFragment;
import org.creativecommons.thelist.fragments.UploadFragment;
import org.creativecommons.thelist.misc.MaterialInterpolator;
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

import swipedismiss.SwipeDismissRecyclerViewTouchListener;


public class MainActivity extends ActionBarActivity implements AccountFragment.LoginClickListener,
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
    protected MainListItem mLastDismissedItem;
    protected int lastDismissedItemPosition;
    protected String userID;
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
    TermsFragment termsFragment = new TermsFragment();
    UploadFragment uploadFragment = new UploadFragment();
    CancelFragment cancelFragment = new CancelFragment();

    //Checks
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
        menuLogin = false;

        Log.v(TAG, "MAINACTIVITY ON CREATE");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

//        GoogleAnalytics instance = GoogleAnalytics.getInstance(this);
//        instance.setAppOptOut(true);

        //Google Analytics Tracker
//        Tracker t = ((ListApplication) MainActivity.this.getApplication()).getTracker(
//                ListApplication.TrackerName.GLOBAL_TRACKER);
//
//        t.setScreenName(TAG);
//        t.send(new HitBuilders.AppViewBuilder().build());

        //TODO: clean this up: should not need this anymore
        //Check if user is logged in
//        userID = sharedPreferencesMethods.getUserId();
//        if(userID == null) {
//            mCurrentUser.setTempUser(true);
//            Log.v("YO" + TAG, "NOT LOGGED IN");
//        } else {
//            mCurrentUser.setTempUser(false);
//            mCurrentUser.setUserID(userID);
//            Log.v("YO " + TAG, "LOGGED IN");
//        }

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
        RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
        mRecyclerView.addItemDecoration(itemDecoration);
        mLayoutManager = new LinearLayoutManager(this);
        mFeedAdapter = new FeedAdapter(mContext, mItemList);
        mRecyclerView.setAdapter(mFeedAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
        initRecyclerView();

        //If Network Connection is available, get User’s Items (API, or local if not logged in)
//        if(requestMethods.isNetworkAvailable(mContext)) {
//            mProgressBar.setVisibility(View.VISIBLE);
//            Log.v("Get user", "On create IS network available");
//            getUserListItems();
//        }
//        else {
            //TODO This is always useful, move to getuserlists
//            Toast.makeText(this, "Network is unavailable", Toast.LENGTH_LONG).show();
//        }
    } //onCreate

    @Override
    public void onResume() {
        super.onResume();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mFab.setEnabled(true);
            }
        }, 500);
        String auth = mCurrentUser.getAuthed(this);

        if(!(auth.equals(ListUser.TEMP_USER))) { //if this is not a temp user
            getUserListItems();
            Log.v("NOT A TEMP: ", "legit user");

        } else { //if user is a temp
            Log.v("I R A TEMP: ", "not logged in");
            if(mItemList.size() == 0){
                Log.v("THERE ARE NO ITEMS: ", "Annd not logged in");
                mRecyclerView.setVisibility(View.INVISIBLE);
                getUserListItems();
            } else {
                Log.v("THERE ARE ITEMS: ", "Annd not logged in");
                mFeedAdapter.notifyDataSetChanged();
                mRecyclerView.setVisibility(View.VISIBLE);
            }
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
        mFab.show();
        mFab.setVisibility(View.VISIBLE);
        mFeedAdapter.notifyDataSetChanged();
        mRecyclerView.setVisibility(View.VISIBLE);
    } //CheckComplete

    //GET USER LIST ITEMS
    private void getUserListItems() {
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest userListRequest;
        String itemRequesturl;
        JSONArray itemIds;

        String auth = mCurrentUser.getAuthed(this);

        //IF USER IS LOGGED IN
        if(!(auth.equals(ListUser.TEMP_USER))) {
            Log.v("HELLO", "this user is logged in");
            itemRequesturl = ApiConstants.GET_USER_LIST + mCurrentUser.getUserID();

            userListRequest = new JsonArrayRequest(itemRequesturl,
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
                                    listItem.setItemName(singleListItem.getString(ApiConstants.ITEM_NAME));
                                    listItem.setMakerName(singleListItem.getString(ApiConstants.MAKER_NAME));
                                    listItem.setItemID(singleListItem.getString(ApiConstants.ITEM_ID));
                                    mItemList.add(listItem);
                                } else if(singleListItem.getInt(ApiConstants.ITEM_COMPLETED) == 1) {
                                    //TODO: Does this work? (add error items to the top)
                                    MainListItem listItem = new MainListItem();
                                    listItem.setItemName(singleListItem.getString(ApiConstants.ITEM_NAME));
                                    listItem.setMakerName(singleListItem.getString(ApiConstants.MAKER_NAME));
                                    listItem.setItemID(singleListItem.getString(ApiConstants.ITEM_ID));
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
            });
            queue.add(userListRequest);
        }
        else { //IF USER IS A TEMP
            mItemList.clear();
            mEmptyView.setVisibility(View.GONE);

            itemIds = sharedPreferencesMethods.RetrieveUserItemPreference();

            if (!(itemIds.length() == 0)) {
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
    } //getUserListItems

    private void initRecyclerView(){
        SwipeDismissRecyclerViewTouchListener touchListener =
                new SwipeDismissRecyclerViewTouchListener(
                        mRecyclerView,
                        new SwipeDismissRecyclerViewTouchListener.OnDismissCallback() {
                            @Override
                            public void onDismiss(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    // TODO: this is temp solution for preventing blinking item onDismiss <-- OMG DEATH
                                    mLayoutManager.findViewByPosition(position).setVisibility(View.GONE);
                                    //Get item details for UNDO
//                                    lastDismissedItemPosition = position;
//                                    mLastDismissedItem = mItemList.get(position);
//                                    //What happens when item is swiped offscreen
//                                    //mItemList.remove(position);
//                                    mItemList.remove(mLastDismissedItem);
//                                    mFeedAdapter.notifyItemRemoved(lastDismissedItemPosition);
//                                    mFeedAdapter.notifyItemRangeChanged(lastDismissedItemPosition, mItemList.size());
                                    mCurrentUser.removeItemFromUserList(mLastDismissedItem.getItemID());

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
                                mFeedAdapter.notifyItemInserted(0);
                                mFeedAdapter.notifyItemRangeChanged(lastDismissedItemPosition, 1);
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

        if(resultCode == RESULT_OK) {
            photoToBeUploaded = true;

            if(requestCode == PhotoConstants.PICK_PHOTO_REQUEST) {
                Log.v("RESULTCODE OK ", (String.valueOf(resultCode)));
                if(data == null) {
                    Toast.makeText(this,getString(R.string.general_error),Toast.LENGTH_LONG).show();
                }
                else {
                    mMediaUri = data.getData();
                }
            }
            Log.i(TAG,"Media URI:" + mMediaUri);

            if(!(mCurrentUser.getAuthed(MainActivity.this).equals(ListUser.TEMP_USER))) {
                startPhotoUpload();
            } else {
                Bundle b = new Bundle();
                b.putSerializable(getString(R.string.menu_login_bundle_key), menuLogin);
                accountFragment.setArguments(b);

                //Load accountFragment
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.overlay_fragment_container, accountFragment).commit();
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
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.overlay_fragment_container,uploadFragment).commit();
        mFrameLayout.setClickable(true);
        getSupportActionBar().hide();
    } //startUploadPhoto

    @Override
    public void UserCreated(String userData) {
        try {
            //Set current user data
            //TODO: get user password as well
            mCurrentUserObject = new JSONObject(userData);
            mCurrentUser.setUserID(mCurrentUserObject.getString(ApiConstants.USER_ID));
            //TODO: set user token
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
        //Set user ID
        mCurrentUser.setUserID(userData);

        menuLogin = false;
        //Create menu again (update login to logout)
        invalidateOptionsMenu();
        if(photoToBeUploaded){
            //Start UploadFragment and Upload photo
            startPhotoUpload();
        } else {
            //TODO: login confirmation
            removeFragment(accountFragment, true);
        }
    } //UserLoggedIn

    @Override
    public void CancelLogin() {
        if(menuLogin){
            removeFragment(accountFragment, false);
            menuLogin = false;
        } else {
            //Show cancelledFragment
            getSupportFragmentManager().beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .replace(R.id.overlay_fragment_container, cancelFragment)
                    .commit();
        }
    } //CancelLogin

    @Override
    public void onTermsClicked() {
        //Start UploadFragment and Upload photo
        if(photoToBeUploaded){
            startPhotoUpload();
            photoToBeUploaded = false;
        } else{
            //TODO: confirm user has been logged in
            removeFragment(termsFragment, false);
        }
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
        photoToBeUploaded = false;

        getUserListItems();
        mFeedAdapter.notifyDataSetChanged();
        //Show upload message for limited time
        removeFragment(uploadFragment, true);

    } //onUploadFinish

    @Override
    public void onCancelStart() {
        getUserListItems();
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
        updateMenuTitles();
        return true;
        //return super.onCreateOptionsMenu(menu);
    } //onCreateOptionsMenu

    private void updateMenuTitles(){
        MenuItem logOut = menu.findItem(R.id.logout);
        MenuItem logIn = menu.findItem(R.id.login);
        //TODO: turn this back on
        if(!(mCurrentUser.getAuthed(MainActivity.this).equals(ListUser.TEMP_USER))){
            logOut.setVisible(true);
            logIn.setVisible(false);
        } else {
            logOut.setVisible(false);
            logIn.setVisible(true);
        }
    } //updateMenuTitles

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()){
            case R.id.login:
                menuLogin = true;

                Bundle b = new Bundle();
                b.putSerializable(getString(R.string.menu_login_bundle_key), menuLogin);
                accountFragment.setArguments(b);

                getSupportFragmentManager().beginTransaction()
                        .add(R.id.overlay_fragment_container, accountFragment).commit();
                mFrameLayout.setClickable(true);
                getSupportActionBar().hide();
                return true;
            case R.id.logout:
                mCurrentUser.logOut();
                userID = null;
                return true;
            case R.id.pick_categories:
                Intent pickCategoriesIntent = new Intent(MainActivity.this, CategoryListActivity.class);
                startActivity(pickCategoriesIntent);
                return true;
            case R.id.about_theapp:
                //TODO: go to scrollview of app things
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    } //onOptionsItemsSelected
} //MainActivity
