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

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageAdapter extends PagerAdapter {
    public static final String TAG = ImageAdapter.class.getSimpleName();

    private Activity activity;
    private ArrayList<GalleryItem> photoObjects;
    private PhotoViewAttacher mAttacher;
    //private PhotoView mImgDisplay;
    //private android.support.v7.widget.Toolbar mGalleryCaption;
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

        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.layout_fullscreen_image, container,
                false);

        //Photoview Elements
        final PhotoView mImgDisplay = (PhotoView) viewLayout.findViewById(R.id.imgDisplay);

        final android.support.v7.widget.Toolbar galleryCaption = (android.support.v7.widget.Toolbar)
                viewLayout.findViewById(R.id.gallery_caption_container);


        GalleryItem g = photoObjects.get(position);
        String photoUrl = g.getUrl() + "/800";

        TextView itemName = (TextView) viewLayout.findViewById(R.id.gallery_item_name);
        TextView makerName = (TextView) viewLayout.findViewById(R.id.gallery_maker_name);

        itemName.setText(g.getItemName());
        makerName.setText("requested by " + g.getMakerName());

        Log.v(TAG, g.getItemName() + " " + photoUrl + " " + g.getMakerName());

        Picasso.with(activity)
                .load(photoUrl)
                .placeholder(R.drawable.progress_view_large)
                .error(R.drawable.progress_view_large)
                .into(mImgDisplay, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        Log.v(TAG, "Successful image load into PhotoView");

                        mAttacher = new PhotoViewAttacher(mImgDisplay);

                        mAttacher.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
                            @Override
                            public void onViewTap(View view, float x, float y) {
                                Log.v(TAG, "TAPPED THAT VIEW");

                                if(galleryCaption.getVisibility() == View.INVISIBLE){
                                    galleryCaption.setVisibility(View.VISIBLE);
                                } else {
                                    galleryCaption.setVisibility(View.INVISIBLE);
                                }
                            }
                        });

                        Log.v(TAG, "NEW ATTACHER CREATED");
                    //}
                    } //onSuccess

                    @Override
                    public void onError() {
                        Log.v(TAG, "Error loading image into PhotoView");
                    } //onError
                });

                ((ViewPager) container).addView(viewLayout);

        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);

    }
} //ImageAdapter
