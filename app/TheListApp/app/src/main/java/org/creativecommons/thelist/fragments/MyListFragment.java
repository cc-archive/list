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

package org.creativecommons.thelist.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.VolleyError;
import com.google.android.gms.analytics.GoogleAnalytics;

import org.creativecommons.thelist.R;
import org.creativecommons.thelist.activities.RandomActivity;
import org.creativecommons.thelist.adapters.UserListAdapter;
import org.creativecommons.thelist.adapters.UserListItem;
import org.creativecommons.thelist.authentication.AccountGeneral;
import org.creativecommons.thelist.layouts.DividerItemDecoration;
import org.creativecommons.thelist.swipedismiss.SwipeDismissRecyclerViewTouchListener;
import org.creativecommons.thelist.utils.ApiConstants;
import org.creativecommons.thelist.utils.ListUser;
import org.creativecommons.thelist.utils.MessageHelper;
import org.creativecommons.thelist.utils.NetworkUtils;
import org.creativecommons.thelist.utils.PhotoConstants;
import org.creativecommons.thelist.utils.RecyclerItemClickListener;
import org.creativecommons.thelist.utils.RequestMethods;
import org.creativecommons.thelist.utils.SharedPreferencesMethods;
import org.creativecommons.thelist.utils.Uploader;
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

public class MyListFragment extends Fragment {
    public static final String TAG = MyListFragment.class.getSimpleName();
    private Context mContext;

    //Helper Methods
    private RequestMethods mRequestMethods;
    private SharedPreferencesMethods mSharedPref;
    private MessageHelper mMessageHelper;
    private ListUser mCurrentUser;

    //Item Management
    private UserListItem mCurrentItem;
    private int activeItemPosition;

    private UserListItem mItemToBeUploaded;
    private int uploadItemPosition;

    private UserListItem mLastDismissedItem;
    private int lastDismissedItemPosition;
    private Uri mMediaUri;

    //RecyclerView
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mFeedAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ViewGroup snackbarContainer;
    private List<UserListItem> mItemList = new ArrayList<>();

    //Upload Elements
    //private RelativeLayout mUploadProgressBarContainer;
    private ProgressBar mUploadProgressBar;
    private TextView mUploadText;

    //UI Elements
    private FloatingActionButton mFab;
    protected ProgressBar mProgressBar;
    protected TextView mEmptyView;

    // --------------------------------------------------------


    public MyListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mContext = getActivity();
        Activity activity = getActivity();

        //Helpers
        mMessageHelper = new MessageHelper(mContext);
        mRequestMethods = new RequestMethods(mContext);
        mSharedPref = new SharedPreferencesMethods(mContext);
        mCurrentUser = new ListUser(getActivity());

        //Load UI Elements
        snackbarContainer = (ViewGroup) activity.findViewById(R.id.snackbar_container);
        mProgressBar = (ProgressBar) activity.findViewById(R.id.feedProgressBar);
        //mUploadProgressBarContainer = (RelativeLayout) activity.findViewById(R.id.photoProgressBar);
        mUploadProgressBar = (ProgressBar) activity.findViewById(R.id.uploadProgressBar);
        //mUploadText = (TextView) activity.findViewById(R.id.upload_text);

        mEmptyView = (TextView) activity.findViewById(R.id.empty_list_label);

        mFab = (FloatingActionButton) activity.findViewById(R.id.fab);
        //mFab.setVisibility(View.GONE);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!NetworkUtils.isNetworkAvailable(mContext)){
                    mMessageHelper.toastNeedInternet();
                    return;
                }

                Intent hitMeIntent = new Intent(getActivity(), RandomActivity.class);
                startActivity(hitMeIntent);

            } //onClick
        });

        //RecyclerView
        mSwipeRefreshLayout = (SwipeRefreshLayout)activity.findViewById(R.id.feedSwipeRefresh);
        mRecyclerView = (RecyclerView)activity.findViewById(R.id.feedRecyclerView);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //TODO: Try dividers in layout instead?
        RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL_LIST);
        mRecyclerView.addItemDecoration(itemDecoration);
        mLayoutManager = new LinearLayoutManager(mContext);
        mFeedAdapter = new UserListAdapter(mContext, mItemList);
        mRecyclerView.setAdapter(mFeedAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        initRecyclerView();

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                displayUserItems();
            }
        });

    } //onActivityCreated

    @Override
    public void onStart(){
        super.onStart();

        if(!mCurrentUser.isAnonymousUser() && !mSharedPref.getAnalyticsViewed()){
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
        }
    } //onStart

    @Override
    public void onResume() {
        super.onResume();

        if(mRequestMethods.isNetworkAvailable() && mSharedPref.getUploadCount() > 4
                && !mSharedPref.getSurveyTaken()){
            int surveyCount = mSharedPref.getSurveyCount();

            //Check if should display survey item
            if(surveyCount % 10 == 0){
                mMessageHelper.takeSurveyDialog(mContext, getString(R.string.dialog_survey_title),
                        getString(R.string.dialog_survey_message),
                        new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                super.onPositive(dialog);

                                //Set survey taken
                                mSharedPref.setSurveyTaken(true);

                                //Go to link
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                                        Uri.parse(getString(R.string.dialog_survey_link)));
                                startActivity(browserIntent);
                                dialog.dismiss();
                            }

                            @Override
                            public void onNegative(MaterialDialog dialog) {
                                super.onNegative(dialog);
                                dialog.dismiss();
                            }
                        });
            } //survey check

            //Increase count
            mSharedPref.setSurveyCount(surveyCount + 1);
        } //surveyTaken

        if(mItemToBeUploaded != null && mRequestMethods.isNetworkAvailable()){
            return;
        }

        if(!(mCurrentUser.isAnonymousUser())) { //if this is not a temp user
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

        if(!(mCurrentUser.isAnonymousUser())) { //IF USER IS NOT A TEMP

            mRequestMethods.getUserItems(new NetworkUtils.UserListCallback() {
                @Override
                public void onSuccess(JSONArray response) {
                    Log.v(TAG , "> getUserItems > onSuccess: " + response.toString());

                    mItemList.clear();

                    for(int i=0; i < response.length(); i++) {
                        try {
                            JSONObject singleListItem = response.getJSONObject(i);
                            //Only show items in the user’s list that have not been completed
                            if (singleListItem.getInt(ApiConstants.ITEM_COMPLETED) == 0) {
                                UserListItem listItem = new UserListItem();
                                listItem.setItemName
                                        (MessageHelper.capitalize(singleListItem.getString(ApiConstants.ITEM_NAME)));
                                listItem.setMakerName
                                        (singleListItem.getString(ApiConstants.MAKER_NAME));
                                listItem.setItemID
                                        (singleListItem.getString(ApiConstants.ITEM_ID));
                                mItemList.add(listItem);
                            } else if(singleListItem.getInt(ApiConstants.ITEM_COMPLETED) == 1) {
                                UserListItem listItem = new UserListItem();
                                listItem.setItemName
                                        (MessageHelper.capitalize(singleListItem.getString(ApiConstants.ITEM_NAME)));
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
                    //mFab.setVisibility(View.VISIBLE);

                    if(mItemList.size() == 0){
                        //TODO: show textView
                        mEmptyView.setText(mContext.getString(R.string.my_list_empty_label));
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
                    //mMessageHelper.loadUserItemsFailMessage();
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mEmptyView.setText(mContext.getString(R.string.mylist_empty_loading_error_label));
                    mEmptyView.setVisibility(View.VISIBLE);
                }
                @Override
                public void onUserOffline(List<UserListItem> response) {

                    if(response == null){
                        Log.v(TAG, "RESPONSE IS NULL");
                        return;
                    }

                    mItemList.clear();

                    for(UserListItem m : response){
                        mItemList.add(m);
                    }

                    mProgressBar.setVisibility(View.INVISIBLE);
                    //mFab.show();
                    //mFab.setVisibility(View.VISIBLE);

                    if(mItemList.size() == 0 || mItemList == null){
                        mEmptyView.setText(mContext.getString(R.string.my_list_empty_label));
                        mEmptyView.setVisibility(View.VISIBLE);
                    }

                    mFeedAdapter.notifyDataSetChanged();
                    mSwipeRefreshLayout.setRefreshing(false);
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
                    UserListItem listItem = new UserListItem();
                    try {
                        listItem.setItemID(String.valueOf(itemIds.getInt(i)));
                        listItem.setMessageHelper(mMessageHelper);
                        listItem.setMainListActivity(getActivity());
                        listItem.setMyListFragment(this);
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
                mEmptyView.setText(mContext.getString(R.string.my_list_empty_label));
                mEmptyView.setVisibility(View.VISIBLE);
                //mFab.show();
                //mFab.setVisibility(View.VISIBLE);
            }
        }
    } //displayUserItems

    //For temp users displayUserItems:
    // Check if all items have been returned from API before displaying list
    public void CheckComplete() {
        Log.d(TAG, " > CheckComplete");
        for(int i = 0; i < mItemList.size(); i++) {
            if (!mItemList.get(i).completed) {
                return;
            }
        }
        mProgressBar.setVisibility(View.INVISIBLE);
        //mFab.show();
        //mFab.setVisibility(View.VISIBLE);
        mFeedAdapter.notifyDataSetChanged();
        mRecyclerView.setVisibility(View.VISIBLE);
    } //CheckComplete

    //----------------------------------------------
    //RECYCLERVIEW – LIST ITEM INTERACTION
    //----------------------------------------------

    private void initRecyclerView(){

        SwipeDismissRecyclerViewTouchListener touchListener = new SwipeDismissRecyclerViewTouchListener(
                mRecyclerView, mSwipeRefreshLayout, new SwipeDismissRecyclerViewTouchListener.DismissCallbacks() {
            @Override
            public boolean canDismiss(int position) {
                return true;
            }
            @Override
            public void onDismiss(RecyclerView recyclerView, int[] reverseSortedPositions) {
                for (int position : reverseSortedPositions) {
                    // TODO: this is temp solution for preventing blinking item onDismiss <-- OMG DEATH

                    if(!mRequestMethods.isNetworkAvailable()){
                        mMessageHelper.toastNeedInternet();
                        mFeedAdapter.notifyDataSetChanged();

                        if(mItemList.size() == 0){
                            mEmptyView.setText(mContext.getString(R.string.mylist_offline_empty_label));
                            mEmptyView.setVisibility(View.VISIBLE);
                        }

                        return;
                    }

                    mLayoutManager.findViewByPosition(position).setVisibility(View.GONE);
                    //Get item details for UNDO
                    lastDismissedItemPosition = position;
                    mLastDismissedItem = mItemList.get(position);

                    //What happens when item is swiped offscreen
                    mItemList.remove(mLastDismissedItem);
                    mRequestMethods.removeItemFromUserList(mLastDismissedItem.getItemID()); //TODO: onFail + on Succeed

                    // do not call notifyItemRemoved for every item, it will cause gaps on deleting items
                    mFeedAdapter.notifyDataSetChanged();

                    //Snackbar message
                    Snackbar.make(snackbarContainer, "Item Deleted", Snackbar.LENGTH_LONG)
                            .setAction("Undo", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    mItemList.add(0, mLastDismissedItem);
                                    //re-add item to user’s list in DB
                                    mRequestMethods.addItemToUserList(mLastDismissedItem.getItemID());
                                    mFeedAdapter.notifyDataSetChanged();
                                    mLayoutManager.scrollToPosition(0);

                                    if(mEmptyView.getVisibility() == View.VISIBLE){
                                        mEmptyView.setVisibility(View.INVISIBLE);
                                    }

                                }
                            }).show();

                    if(mItemList.size() == 0){
                        mEmptyView.setText(mContext.getString(R.string.my_list_empty_label));
                        mEmptyView.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        mRecyclerView.setOnTouchListener(touchListener);
        // Setting this scroll listener is required to ensure that during ListView scrolling,
        // we don't look for swipes.
        LinearLayoutManager llm = (LinearLayoutManager)mRecyclerView.getLayoutManager();
        mRecyclerView.setOnScrollListener(touchListener.makeScrollListener(llm));

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(),
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setItems(R.array.listItem_choices, mDialogListener);
                        AlertDialog dialog = builder.create();
                        dialog.show();

                        //Get item details for photo upload
                        activeItemPosition = position;
                        mCurrentItem = mItemList.get(position);

                    }
                }));
    } //initRecyclerView

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
                    return (state.equals(Environment.MEDIA_MOUNTED)); //boolean
                }
            };

    //Once photo taken or selected then do this:
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK){
            prepForPhotoUpload(data);

            final Resources res = getResources();
            final String [] choices = res.getStringArray(R.array.single_choice_terms_array);
            String title = null;
            String content = res.getString(R.string.single_choice_terms_content);

            switch(requestCode){
                case PhotoConstants.PICK_PHOTO_REQUEST:
                    title = res.getString(R.string.single_choice_choose_photo_title);
                    break;
                case PhotoConstants.TAKE_PHOTO_REQUEST:
                    title = res.getString(R.string.single_choice_take_photo_title);
                    break;
            } //switch

            mMessageHelper.showSingleChoiceDialog(mContext, title, content, choices,
                    new MaterialDialog.ListCallbackSingleChoice() {
                        @Override
                        public boolean onSelection (MaterialDialog materialDialog, View view,
                                                    int i,CharSequence charSequence) {

                            if(charSequence == (choices[0])){
                                //Positive response
                                startPhotoUpload();

                            } else {
                                //Negative response
                                Snackbar.make
                                        (snackbarContainer, "Upload Cancelled",
                                                Snackbar.LENGTH_LONG).show();

                                //showUploadCancelledSnackbar();
                            }

                            return true;
                        }
                    });

        } else if(resultCode != Activity.RESULT_CANCELED) { //result other than ok or cancelled
            // Toast.makeText(this, R.string.general_error, Toast.LENGTH_SHORT).show();
            Log.d(TAG, "> onActivityResult > resultCode != canceled");
        }

    } //onActivityResult

    public void prepForPhotoUpload(Intent data){
        mItemToBeUploaded = mCurrentItem;
        uploadItemPosition = activeItemPosition;

        if(data == null) {
            //Toast.makeText(this,getString(R.string.general_error),Toast.LENGTH_LONG).show();
            Log.d(TAG, "> onActivityResult > data == null");
        }
        else {
            mMediaUri = data.getData();
        }

        Log.v(TAG, "Media URI:" + mMediaUri);

        //Add photo to the Gallery (listen for broadcast and let gallery take action)
        final Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(mMediaUri);
        getActivity().sendBroadcast(mediaScanIntent);

    }

    //----------------------------------------------
    //PHOTO UPLOAD
    //----------------------------------------------

    //Start Upload + Respond
    public void startPhotoUpload(){

        if(!(mCurrentUser.isAnonymousUser())){ //IF NOT TEMP USER
            mCurrentUser.getToken(new ListUser.TokenCallback() { //getToken
                @Override
                public void onAuthed(String authtoken) {
                    Log.v(TAG, "> startPhotoUpload > getToken, token received: " + authtoken);

                    mItemList.remove(mItemToBeUploaded);
                    mFeedAdapter.notifyDataSetChanged();
                    performPhotoUpload();
                }
            });
        } else {
            mCurrentUser.addNewAccount(AccountGeneral.ACCOUNT_TYPE,
                    AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, new ListUser.AuthCallback() { //addNewAccount
                        @Override
                        public void onAuthed(String authtoken) {
                            Log.d(TAG, "> addNewAccount > onAuthed, authtoken: " + authtoken);
                            try {
                                mItemList.remove(mItemToBeUploaded);
                                mFeedAdapter.notifyDataSetChanged();
                                performPhotoUpload();
                            } catch (Exception e) {
                                Log.d(TAG,"addAccount > " + e.getMessage());
                            }
                        }

                    });
        }
    } //startPhotoUpload

    public void performPhotoUpload(){
        //Set upload count
        int uploadCount = mSharedPref.getUploadCount();
        mSharedPref.setUploadCount(uploadCount + 1);

        //mUploadText.setText("Uploading " + mItemToBeUploaded.getItemName() + "…");
        mUploadProgressBar.setVisibility(View.VISIBLE);
        Log.v(TAG, "UPLOAD BAR VISIBLES");

        //Hide progress bar if it takes too much time to upload
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mUploadProgressBar.getVisibility() == View.VISIBLE) {
                    mUploadProgressBar.setVisibility(View.GONE);
                }
            }
        }, 3000);

        Uploader uploader = new Uploader(mContext);
        uploader.uploadPhoto(mItemToBeUploaded, mMediaUri, new NetworkUtils.UploadResponse() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "uploadPhoto > onSuccess");

                //TODO: add upload notification bar (below actionbar)

                displayUserItems();

                mUploadProgressBar.setVisibility(View.GONE);
                mItemToBeUploaded = null;

                //Show snackbar confirmation
                Snackbar.make(mRecyclerView, "Photo Uploaded", Snackbar.LENGTH_LONG).show();
//                        .setAction("dismiss", new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                mFab.setEnabled(false);
//
//                                new Handler().postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        //mFab.show();
//                                        mFab.setEnabled(true);
//                                    }
//                                }, 500);
//                            }
//                        }).show();
            } //onSuccess
            @Override
            public void onFail() {
                Log.d(TAG, "uploadPhoto > onFail");

                //TODO: add upload notification bar (below actionbar)

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mUploadProgressBar.setVisibility(View.GONE);
                        displayUserItems();
                    }
                }, 500);

                Snackbar.make(snackbarContainer, "Upload Failed", Snackbar.LENGTH_LONG)
                        .setAction("dismiss", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                performPhotoUpload();
                            }
                        }).show();

            } //onFail
            @Override
            public void onCancelled(NetworkUtils.CancelResponse response) {
                Log.v(TAG, "uploadPhoto > onCancelled: " + response.toString());

                mUploadProgressBar.setVisibility(View.GONE);

                switch(response) {
                    case NETWORK_ERROR:
                        mMessageHelper.networkFailMessage();
                        break;
                    case FILESIZE_ERROR:
                        mMessageHelper.photoUploadSizeFailMessage();
                        break;
                }
            }
        });
    } //performPhotoUpload


    @Override
    public void onPause() {
        //Save most recent list to sharedPreferences
        mSharedPref.saveOfflineUserList(mItemList);
        super.onPause();
    }


} //MyListFragment
