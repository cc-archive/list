package org.creativecommons.thelist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.creativecommons.thelist.adapters.MainListItem;
import org.creativecommons.thelist.utils.ApiConstants;
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

    //Helper Methods
    RequestMethods requestMethods = new RequestMethods(this);
    SharedPreferencesMethods sharedPreferencesMethods = new SharedPreferencesMethods(this);

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

    //UI Elements
    TextView mTextView;
    ProgressBar mProgressBar;

    //Shared variables
    int count;
    //String countString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random);

        mTextView = (TextView) findViewById(R.id.text);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        //Picker Buttons
        Button YesButton = (Button) findViewById(R.id.YesButton);
        Button NoButton = (Button) findViewById(R.id.NoButton);
        Button CameraButton = (Button) findViewById(R.id.CameraButton);

        if(requestMethods.isNetworkAvailable()) {
            mProgressBar.setVisibility(View.VISIBLE);
            count = 1;
            getRandomItemRequest();
            //Yes Button Listener
            YesButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //TODO: Store Item object in local JSONArray
                    MainListItem listItem = new MainListItem();
                    listItem.setItemID(mItemID);
                    listItem.setItemName(mItemName);
                    listItem.setMakerName(mMakerName);
                    mItemList.add(listItem);



                    //Once yes has been hit 3 times, forward to
                    if(count < 3) {
                        count ++;
                        getRandomItemRequest();
                    } else {

                        //Pass mItemList to next activity

                        //If user is logged in, send chosen list items to DB
                        if(requestMethods.isLoggedIn()) {
                            putRandomItemsRequest();
                        }
                        else {
                            //Get array of selected item ids
                            List<Integer> userItemList = requestMethods.getItemIds(mItemList);
                            Log.v(TAG,mItemList.toString());

                            //Save Array as String to sharedPreferences
                            sharedPreferencesMethods.SaveSharedPreference
                                    (sharedPreferencesMethods.LIST_ITEM_PREFERENCE,
                                            sharedPreferencesMethods.LIST_ITEM_PREFERENCE_KEY, userItemList.toString());
                        }

                        //Start MainActivity
                        Intent intent = new Intent(RandomActivity.this, MainActivity.class);
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
            requestMethods.updateDisplayForError();
        }
        else {
            try {
                //Store values from response JSON Object
                mListItemData = mRandomItemData.getJSONObject(ApiConstants.RESPONSE_CONTENT);
                mItemName = mListItemData.getString(ApiConstants.ITEM_NAME);
                mMakerName = mListItemData.getString(ApiConstants.MAKER_NAME);
                mItemID = mListItemData.getInt(ApiConstants.ITEM_ID);

                //Update UI
                mTextView.setText(mMakerName + " needs a picture of " + mItemName);
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
        String url ="http://10.0.3.2:3000/api/item/" + randomNumber + "/maker";
        //Android Default Emulator
        //String url = "http://10.0.2.2:3000/api/item/" + randomNumber + "/maker";

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
        //TODO: POST Selected Items to User’s List
        RequestQueue queue = Volley.newRequestQueue(this);
        String userID = requestMethods.getUserID();
        //Genymotion Emulator
        String url = "http://10.0.3.2:3000/api/user/" + userID;
        //Android Default Emulator
        //String url = "http://10.0.2.2:3000/api/user";

        //Retrieve User list item preferences
        JSONArray userPreferences = sharedPreferencesMethods.RetrieveSharedPreference
                (sharedPreferencesMethods.LIST_ITEM_PREFERENCE,
                        sharedPreferencesMethods.LIST_ITEM_PREFERENCE_KEY);

        //Create Object to send
        JSONObject jso = new JSONObject();
        try {
            jso.put(ApiConstants.USER_ITEMS, userPreferences);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
        Log.v(TAG,jso.toString());

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
