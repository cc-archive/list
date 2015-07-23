package org.creativecommons.thelist.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import org.creativecommons.thelist.R;
import org.creativecommons.thelist.adapters.GalleryItem;
import org.creativecommons.thelist.adapters.ImageAdapter;
import org.creativecommons.thelist.utils.FileHelper;
import org.creativecommons.thelist.utils.MessageHelper;

import java.util.ArrayList;
import java.util.List;

public class ImageActivity extends AppCompatActivity {
    private static final String TAG = ImageActivity.class.getSimpleName();

    private Context mContext;

    //Helpers
    private MessageHelper mMessageHelper;

    private ImageAdapter adapter;
    private ViewPager viewPager;

    private ArrayList<GalleryItem> photoObjects;

    //Share Photo
    private boolean isIntentSafe = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        mContext = this;

        mMessageHelper = new MessageHelper(mContext);

        //Setup toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar_transparent);

        if(toolbar != null){
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //Get incoming data
        Bundle b = getIntent().getExtras();
        photoObjects = b.getParcelableArrayList("photos");
        int position = b.getInt("position", 0);

        //View Pager
        viewPager = (ViewPager) findViewById(R.id.imagePager);

        if(android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR2){
            viewPager.setOffscreenPageLimit(2);
        } else {
            viewPager.setOffscreenPageLimit(3);
        }

        adapter = new ImageAdapter(ImageActivity.this, photoObjects);
        viewPager.setAdapter(adapter);

        //Displaying selected image first
        viewPager.setCurrentItem(position);

    } //onCreate

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_image, menu);

        if(!isIntentSafe){
            //TODO: hide share button
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (item.getItemId()) {
            case R.id.action_share:

                //TODO: add check to see if image loaded (check for placeholder…)

                //Access Image from View
                ImageView galleryImage = (ImageView)viewPager.findViewWithTag(viewPager.getCurrentItem());

                // Get access to the URI for the bitmap
                Uri bmpUri = FileHelper.getLocalBitmapUri(galleryImage);

                //Create intent
                Intent shareIntent = new Intent();

                if(bmpUri != null){
                    // Construct a ShareIntent with link to image
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                    shareIntent.setType("image/*");

                    //Check if any activities can handle the intent
                    PackageManager packageManager = getPackageManager();
                    List activities = packageManager.queryIntentActivities(shareIntent,
                            PackageManager.MATCH_DEFAULT_ONLY);
                    isIntentSafe = activities.size() > 0;

                } else {
                    Log.v(TAG, "bmpUri is null > problem loading photo");
                }

                if(isIntentSafe){
                    // Launch sharing dialog for image
                    startActivity(Intent.createChooser(shareIntent, "Share Image"));
                } else {
                    Log.v(TAG, "No apps to receive intent");
                    //TODO: add message: Looks like you have no apps to share to
                    mMessageHelper.showDialog(mContext, "Oops!", "Looks like you don’t have any apps to share to.");
                }

                return true;
            case android.R.id.home:
                    finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //TODO: investigate further
//    @Override
//    public void onTrimMemory(int level) {
//        switch(level){
//            case TRIM_MEMORY_UI_HIDDEN:
//
//            break;
//
//            case TRIM_MEMORY_RUNNING_LOW:
//            break;
//
//            case TRIM_MEMORY_RUNNING_CRITICAL:
//                break;
//        }
//
//        super.onTrimMemory(level);
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy");
    }
} //ImageActivity
