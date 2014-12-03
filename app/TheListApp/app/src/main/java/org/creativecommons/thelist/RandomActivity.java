package org.creativecommons.thelist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.creativecommons.thelist.adapters.MainListItem;
import org.creativecommons.thelist.utils.ApiConstants;
import org.creativecommons.thelist.utils.ListUser;
import org.creativecommons.thelist.utils.RequestMethods;
import org.creativecommons.thelist.utils.SharedPreferencesMethods;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class RandomActivity extends Activity {
    public static final String TAG = RandomActivity.class.getSimpleName();
    protected Context mContext;

    //Helper Methods
    RequestMethods requestMethods = new RequestMethods(this);
    SharedPreferencesMethods sharedPreferencesMethods = new SharedPreferencesMethods(this);
    ListUser mCurrentUser = new ListUser(this);

    //GET Request
    protected JSONObject mRandomItemData;
    protected JSONObject mListItemData;
    String mMakerName;
    String mItemName;
    int mItemID;

    //PUT request (if user is logged in)
    protected JSONObject mPutResponse;

    //Handle Data
    protected JSONObject mUserListItems; //Store in object to putExtra to next intent?
    private List<MainListItem> mItemList = new ArrayList<MainListItem>();
    private ArrayList<Integer> mItemsViewed = new ArrayList<Integer>();

    //UI Elements
    TextView mTextView;
    ProgressBar mProgressBar;

    //Shared variables
    int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random);
        mContext = this;

        mTextView = (TextView) findViewById(R.id.item_text);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        //Picker Buttons
        Button YesButton = (Button) findViewById(R.id.YesButton);
        Button NoButton = (Button) findViewById(R.id.NoButton);
        Button CameraButton = (Button) findViewById(R.id.CameraButton);

        if(requestMethods.isNetworkAvailable(mContext)) {
            mProgressBar.setVisibility(View.VISIBLE);
            getRandomItemRequest();

            //Yes Button Listener
            YesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Visual Confirmation of add
                    Toast.makeText(RandomActivity.this, "Added to Your List", Toast.LENGTH_SHORT).show();

                    MainListItem listItem = new MainListItem();
                    listItem.setItemID(mItemID);
                    listItem.setItemName(mItemName);
                    listItem.setMakerName(mMakerName);
                    mItemList.add(listItem);

                    //Once yes has been hit 3 times, forward to
                    if(mItemList.size() < 3) {
                        getRandomItemRequest();
                    } else {

                        //If user is logged in, send chosen list items to DB
                        if(mCurrentUser.isLoggedIn()) {
                            putRandomItemsRequest();
                        }
                        else {
                            //Get array of selected item IDS
                            List<Integer> userItemList = requestMethods.getItemIds(mItemList);
                            //Log.v(TAG,mItemList.toString());

                            //Save Array as String to sharedPreferences
                            sharedPreferencesMethods.SaveSharedPreference
                                    (sharedPreferencesMethods.LIST_ITEM_PREFERENCE,
                                            sharedPreferencesMethods.LIST_ITEM_PREFERENCE_KEY,
                                            userItemList.toString(), mContext);
                        }

                        //Start MainActivity
                        Intent intent = new Intent(mContext, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }
            });
            //No Button Functionality
            NoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getRandomItemRequest();
                }
            });
            //Camera Button Functionality
            CameraButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: Camera Intent, (possibly remove if user is not logged in)
                }
            });
        } else {
            requestMethods.updateDisplayForError();
        }


    } //onCreate

    private void updateList() {
        mProgressBar.setVisibility(View.INVISIBLE);
        if(mRandomItemData == null) {
            //TODO: better error message
            requestMethods.showErrorDialog(mContext, "Oops", "No data found. Please try again.");
        }
        else {
            try {
                //Store values from response JSON Object
                mListItemData = mRandomItemData.getJSONObject(ApiConstants.RESPONSE_CONTENT);
                mItemID = mListItemData.getInt(ApiConstants.ITEM_ID);

                Log.v(TAG, mItemsViewed.toString() + " this is the id " + String.valueOf(mItemID));

                //If the user has seen the item before, select a new item
                //TODO: use this to prevent user of seeing repeat items in a single session?
                if(mItemsViewed.contains(mItemID) && mItemsViewed.size() <= ApiConstants.MAX_ITEMS_VIEWED){
                    Log.v(TAG, "this item has been viewed before");
                    getRandomItemRequest();
                } else {
                    Log.v(TAG, "this item is new to me");
                    //Add item ID to list of items user has seen
                    mItemsViewed.add(mItemID);
                    mItemName = mListItemData.getString(ApiConstants.ITEM_NAME);
                    mMakerName = mListItemData.getString(ApiConstants.MAKER_NAME);
                    //Update UI
                    mTextView.setText(mMakerName + " needs a picture of " + mItemName);
                }

            } catch (JSONException e) {
                Log.e(TAG,e.getMessage());
            }
        }
    } //updateList

    private void getRandomItemRequest() {
        RequestQueue queue = Volley.newRequestQueue(this);

        //Generate number to request random item by ID
        //TODO: Select item from random position in item array
        Random random = new Random();
        //TODO: Change max to length of response?
        int n = random.nextInt(5) + 1;
        String randomNumber = String.valueOf(n);

        //Genymotion Emulator
        String url = ApiConstants.GET_SINGLE_ITEM + randomNumber;
        //Android Default Emulator
        //String url = "http://10.0.2.2:3000/api/item/" + randomNumber;

        JsonObjectRequest randomItemRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                            //Log.v(TAG,response.toString());
                            mRandomItemData = response;
                            updateList();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse (VolleyError error){
                requestMethods.updateDisplayForError();
            }
        });
        queue.add(randomItemRequest);
    } //GetRandomItemRequest


    private void putRandomItemsRequest() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String userID = mCurrentUser.getUserID();
        //Genymotion Emulator
        String url = ApiConstants.UPDATE_USER + userID;
        //Android Default Emulator
        //String url = "http://10.0.2.2:3000/api/user";

        //Retrieve User list item preferences
        JSONArray userPreferences = sharedPreferencesMethods.RetrieveSharedPreference
                (sharedPreferencesMethods.LIST_ITEM_PREFERENCE,
                        sharedPreferencesMethods.LIST_ITEM_PREFERENCE_KEY, this);

        //Create Object to send
        JSONObject jso = new JSONObject();
        try {
            jso.put(ApiConstants.USER_ITEMS, userPreferences);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
        //Log.v(TAG,jso.toString());

        //Add items to user list
        JsonObjectRequest putItemsRequest = new JsonObjectRequest(Request.Method.PUT, url, jso,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //get Response
                        //Log.v(TAG,response.toString());
                        mPutResponse = response;
                        //TODO: do something with response?
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse (VolleyError error){
                requestMethods.updateDisplayForError();
            }
        });
        queue.add(putItemsRequest);
    } //putRandomItemsRequest

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_random, menu);
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
