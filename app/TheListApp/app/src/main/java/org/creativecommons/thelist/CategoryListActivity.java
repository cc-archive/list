package org.creativecommons.thelist;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.creativecommons.thelist.utils.RequestMethods;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class CategoryListActivity extends ListActivity {
    public static final String TAG = CategoryListActivity.class.getSimpleName();

    //Request Methods
    RequestMethods requestMethods = new RequestMethods(this);

    //For API Request
    protected JSONObject mCategoryData;
    protected String[] mCategoryTitles;

    //UI Elements
    protected ProgressBar mProgressBar;
    protected Button mNextButton;
    protected ListView mListView;

    //TODO: Limit returned results
    //public static final int NUMBER_OF_POSTS = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);

        //Load UI Elements
        mListView = getListView();
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mNextButton = (Button) findViewById(R.id.nextButton);
        mNextButton.setVisibility(View.INVISIBLE);

        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        //TODO: mNextButton POST Category selection to Database
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //POST selection to Database [array of category ids]
                storeCategoriesRequest();

                //Navigate to Next Activity
                Intent intent = new Intent(CategoryListActivity.this, RandomActivity.class);
                startActivity(intent);
            }
        });

        //If Network Connection is available, Execute getDataTask
        if(requestMethods.isNetworkAvailable()) {
            mProgressBar.setVisibility(View.VISIBLE);
            getCategoriesRequest();
        }
        else {
            Toast.makeText(this, "Network is unavailable", Toast.LENGTH_LONG).show();
        }
    } //onCreate


    private void getCategoriesRequest() {

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://10.0.3.2:3000/api/category";

        JsonObjectRequest getCategoriesRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray jsonCategories = null;
                        try {
                            //Handle Data
                            mCategoryData = response;
                            jsonCategories = mCategoryData.getJSONArray("content");
                            mCategoryTitles = new String[jsonCategories.length()];

                            for(int i = 0; i<jsonCategories.length(); i++) {
                                JSONObject jsonCategory = jsonCategories.getJSONObject(i);
                                String categoryName = jsonCategory.getString("name");
                                categoryName = Html.fromHtml(categoryName).toString();
                                mCategoryTitles[i] = categoryName;
                            }

                            mProgressBar.setVisibility(View.INVISIBLE);
                            //Update UI
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_checked,mCategoryTitles);
                            setListAdapter(adapter);

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
        queue.add(getCategoriesRequest);

    } //getCategoriesRequest


    //Store categories for later use (could reuse this for future item selections//crete conditional like RandomActivity)
    private void storeCategoriesRequest() {
        //TODO: Create Object with Category choices

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://10.0.3.2:3000/api/user";

        JsonObjectRequest postCategoriesRequest = new JsonObjectRequest(Request.Method.POST, url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray jsonCategories = null;
                        try {
                            //Handle Data
                            mCategoryData = response;
                            jsonCategories = mCategoryData.getJSONArray("content");
                            mCategoryTitles = new String[jsonCategories.length()];

                            for(int i = 0; i<jsonCategories.length(); i++) {
                                JSONObject jsonCategory = jsonCategories.getJSONObject(i);
                                String categoryName = jsonCategory.getString("name");
                                categoryName = Html.fromHtml(categoryName).toString();
                                mCategoryTitles[i] = categoryName;
                            }

                            mProgressBar.setVisibility(View.INVISIBLE);
                            //Update UI
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_checked,mCategoryTitles);
                            setListAdapter(adapter);

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
        queue.add(postCategoriesRequest);

    } //storeCategoriesRequest


    //When Category Name is selected
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        //Count how many items are checked: if at least 3, show Next Button
        SparseBooleanArray positions = mListView.getCheckedItemPositions();
        int ItemsChecked = 0;
        if (positions != null) {
            int length = positions.size();
            for (int i = 0; i < length; i++) {
                if (positions.get(positions.keyAt(i))) {
                    ItemsChecked++;
                }
            }
        }

        if (ItemsChecked >= 3) {
            mNextButton.setVisibility(View.VISIBLE);
        }
        else {
            mNextButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_category_list, menu);
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



