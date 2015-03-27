package org.creativecommons.thelist.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;

import org.creativecommons.thelist.R;
import org.creativecommons.thelist.adapters.ImageAdapter;

import java.util.ArrayList;

public class ImageActivity extends ActionBarActivity {

    private ImageAdapter adapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        viewPager = (ViewPager) findViewById(R.id.imagePager);

        Intent i = getIntent();
        ArrayList<String> urls = i.getStringArrayListExtra("urls");
        int position = i.getIntExtra("position", 0);

        adapter = new ImageAdapter(ImageActivity.this, urls);

        viewPager.setAdapter(adapter);

        //displaying selected image first
        viewPager.setCurrentItem(position);
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_image, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
