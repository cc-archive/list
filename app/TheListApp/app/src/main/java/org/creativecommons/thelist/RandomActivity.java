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
import org.creativecommons.thelist.utils.RequestMethods;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;


public class RandomActivity extends Activity {
    public static final String TAG = RandomActivity.class.getSimpleName();

    //Request Methods
    RequestMethods requestMethods = new RequestMethods(this);

    //For API Request
    protected JSONObject mRandomItemData;
    String mMakerName;
    String mItemName;

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
            //mProgressBar.setVisibility(View.VISIBLE);
            count = 1;
//            countString = String.valueOf(count);
//
//            Log.v(TAG, countString);
            getRandomItemRequest();

            //Yes Button Listener
            YesButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //TODO: Post Item to the User’s List

                    //Once yes has been hit 3 times, forward to
                    if(count < 3) {
                        count ++;
                        getRandomItemRequest();
                    } else {
                        //Direct to main feed
                        Intent intent = new Intent(RandomActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
            });

            NoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getRandomItemRequest();
                }
            });

            CameraButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: Camera Intent
                }
            });
        } else {
            requestMethods.updateDisplayForError();
        }

    } //onCreate

    private void getRandomItemRequest() {

        RequestQueue queue = Volley.newRequestQueue(this);

        Random random = new Random();
        int n = random.nextInt(5) + 1;
        String randomNumber = String.valueOf(n);

        //TODO: Filter results by User Picked Categories

        //Genymotion Emulator
        //String url ="http://10.0.3.2:3000/api/item/" + randomNumber + "/maker";

        //Android Default Emulator
        String url = "http://10.0.2.2:3000/api/item/" + randomNumber + "/maker";


        Log.v(TAG, url);

        JsonObjectRequest randomItemRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            mRandomItemData = response.getJSONObject("content");
                            mItemName = mRandomItemData.getString("item");
                            mMakerName = mRandomItemData.getString("maker");

                            //mProgressBar.setVisibility(View.INVISIBLE);

                            //Update UI
                            mTextView.setText(mMakerName + " needs a picture of " + mItemName);

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
        queue.add(randomItemRequest);
    } //GetRandomItemRequest

    private void putRandomItemsRequest() {

        //TODO: POST Selected Items to User’s List
        RequestQueue queue = Volley.newRequestQueue(this);

        //TODO: Filter results by User Picked Categories
        String url ="http://10.0.3.2:3000/api/user";
        Log.v(TAG, url);

        //If the user exists in the database, add items to his/her list
//        if(requestMethods.isUser()) {
//            //TODO: POST saved items to database
//
////            JsonObjectRequest storeItemsRequest = new JsonObjectRequest(Request.Method.PUT, url, null,
////                    new Response.Listener<JSONObject>() {
////
////                        @Override
////                        public void onResponse(JSONObject response) {
////                            try {
////                                //mProgressBar.setVisibility(View.INVISIBLE);
////
////                                //User Feedback
////                                //Show Toast that item has been added
////
////                            } catch (JSONException e) {
////                                Log.e(TAG, e.getMessage());
////                            }
////                        }
////                    }, new Response.ErrorListener() {
////                @Override
////                public void onErrorResponse (VolleyError error){
////                    requestMethods.updateDisplayForError();
////                }
////            });
////            queue.add(storeItemsRequest);
////        }
//        //If User is new, add items to local object to later be added to his/her User Object
//        else {
//         //TODO: Store in local object
//
//        }


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
