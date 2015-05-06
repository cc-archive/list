package org.creativecommons.thelist.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
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

public class ImageActivity extends AppCompatActivity {
    private static final String TAG = ImageActivity.class.getSimpleName();

    private Context mContext;
    private MessageHelper mMessageHelper;

    private ShareActionProvider miShareAction;

    private ImageAdapter adapter;
    private ViewPager viewPager;

    private ArrayList<GalleryItem> photoObjects;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        mContext = this;
        mMessageHelper = new MessageHelper(mContext);

        //Setup toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar_transparent);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Get incoming data
        Bundle b = getIntent().getExtras();
        photoObjects = b.getParcelableArrayList("photos");
        int position = b.getInt("position", 0);

        //View Pager
        viewPager = (ViewPager) findViewById(R.id.imagePager);
        adapter = new ImageAdapter(ImageActivity.this, photoObjects);
        viewPager.setAdapter(adapter);

        //displaying selected image first
        viewPager.setCurrentItem(position);
    } //onCreate

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_image, menu);

        MenuItem item = menu.findItem(R.id.action_share);
        // Fetch reference to the share action provider
        miShareAction = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

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

                //Access Image from View
                ImageView galleryImage = (ImageView)viewPager.findViewWithTag(viewPager.getCurrentItem());

                // Get access to the URI for the bitmap
                Uri bmpUri = FileHelper.getLocalBitmapUri(galleryImage);
                if (bmpUri != null) {
                    // Construct a ShareIntent with link to image
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                    shareIntent.setType("image/*");
                    // Launch sharing dialog for image
                    startActivity(Intent.createChooser(shareIntent, "Share Image"));
                } else {
                    Log.d(TAG, "Failed to find Bitmap; Uri was null");
                    // ...sharing failed, handle error
                }

                return true;
            case android.R.id.home:
                    finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

} //ImageActivity
