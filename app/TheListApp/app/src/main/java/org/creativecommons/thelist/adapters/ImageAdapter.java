package org.creativecommons.thelist.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.creativecommons.thelist.R;
import org.creativecommons.thelist.layouts.TouchImageView;

import java.util.ArrayList;

public class ImageAdapter extends PagerAdapter {
    public static final String TAG = ImageAdapter.class.getSimpleName();

    private Activity activity;
    private ArrayList<GalleryItem> photoObjects;
    private LayoutInflater inflater;

    // constructor
    public ImageAdapter(Activity activity,
                                  ArrayList<GalleryItem> photoObjects) {
        this.activity = activity;
        this.photoObjects = photoObjects;
    }

    @Override
    public int getCount() {
        return this.photoObjects.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        TouchImageView imgDisplay;
        TextView itemName;
        TextView makerName;

        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.layout_fullscreen_image, container,
                false);

        imgDisplay = (TouchImageView) viewLayout.findViewById(R.id.imgDisplay);
        itemName = (TextView) viewLayout.findViewById(R.id.gallery_item_name);
        makerName = (TextView) viewLayout.findViewById(R.id.gallery_maker_name);

        GalleryItem g = photoObjects.get(position);

        Log.v(TAG, g.getItemName() + " " + g.getUrl() + " " + g.getMakerName());

        itemName.setText(g.getItemName());
        makerName.setText("requested by " + g.getMakerName());

        String photoUrl = g.getUrl() + "/800";

        Picasso.with(activity)
                .load(photoUrl)
                .placeholder(R.drawable.progress_view_large) //TODO: switch drawable
                .error(R.drawable.progress_view_large)
                .into(imgDisplay);

        ((ViewPager) container).addView(viewLayout);

        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);

    }
}
