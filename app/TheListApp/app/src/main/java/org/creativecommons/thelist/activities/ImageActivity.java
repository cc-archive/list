package org.creativecommons.thelist.activities;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import org.creativecommons.thelist.R;
import org.creativecommons.thelist.adapters.GalleryItem;
import org.creativecommons.thelist.adapters.ImageAdapter;

import java.util.ArrayList;

public class ImageActivity extends ActionBarActivity {
    private static final String TAG = ImageActivity.class.getSimpleName();

    private ImageAdapter adapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        //Setup toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar_transparent);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Get incoming data
        Bundle b = getIntent().getExtras();
        ArrayList<GalleryItem> photoObjects = b.getParcelableArrayList("photos");
        int position = b.getInt("position", 0);

        //View Pager
        viewPager = (ViewPager) findViewById(R.id.imagePager);
        adapter = new ImageAdapter(ImageActivity.this, photoObjects);
        viewPager.setAdapter(adapter);

        //displaying selected image first
        viewPager.setCurrentItem(position);
    } //onCreate

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_image, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (item.getItemId()) {
            case android.R.id.home:
                    finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

} //ImageActivity
