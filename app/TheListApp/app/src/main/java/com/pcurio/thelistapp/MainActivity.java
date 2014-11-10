package com.pcurio.thelistapp;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.pcurio.thelistapp.utils.PhotoConstants;
import com.pcurio.thelistapp.utils.RequestMethods;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends ListActivity {
    public static final String TAG = CategoryListActivity.class.getSimpleName();

    //Request Methods
    RequestMethods mRequestMethods = new RequestMethods(this);

    //For API Requests
    protected JSONObject mItemData;
    protected String[] mItemTitles;
    //protected String[] mListMakers;

    //TODO: Limit returned results to most recent
    //public static final int NUMBER_OF_POSTS = 5;

    //UI Elements
    protected ProgressBar mProgressBar;
    protected ListView mListView;

    //Uri for Photos
    protected Uri mMediaUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Load UI Elements
        mListView = getListView();
        mProgressBar = (ProgressBar) findViewById(R.id.feed_progressBar);

        //If Network Connection is available, Execute getDataTask
        if(mRequestMethods.isNetworkAvailable()) {
            mProgressBar.setVisibility(View.VISIBLE);
            makeJsonDataRequest();
        }
        else {
            Toast.makeText(this, "Network is unavailable", Toast.LENGTH_LONG).show();
        }
    }

    //UPDATE LIST WITH CONTENT
    private void updateList() {
        mProgressBar.setVisibility(View.INVISIBLE);
        if (mItemData == null) {
            mRequestMethods.updateDisplayForError();
        }
        else {
            JSONArray jsonItems = null;
            try {
                jsonItems = mItemData.getJSONArray("content");
                mItemTitles = new String[jsonItems.length()];

                for(int i = 0; i<jsonItems.length(); i++) {
                    JSONObject jsonCategory = jsonItems.getJSONObject(i);
                    String ItemName = jsonCategory.getString("name");
                    ItemName = Html.fromHtml(ItemName).toString();
                    mItemTitles[i] = ItemName;
                }
            } catch (JSONException e) {
                Log.e(TAG, "Exception Caught: ", e);
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, mItemTitles);
            setListAdapter(adapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //MAKE CALL TO API AND UPDATE LIST
    private void makeJsonDataRequest() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://10.0.3.2:3000/api/item";

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
                mRequestMethods.updateDisplayForError();
            }
        });
        queue.add(jsonObjectRequest);
    }


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

    //TODO: Custom List Adapter

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        //TODO: Start Upload Options + Save to List
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(R.array.listItem_choices, mDialogListener);
        AlertDialog dialog = builder.create();
        dialog.show();

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
