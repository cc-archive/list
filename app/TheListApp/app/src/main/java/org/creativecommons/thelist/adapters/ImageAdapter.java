/* The List powered by Creative Commons

   Copyright (C) 2014, 2015 Creative Commons

   This program is free software: you can redistribute it and/or modify
   it under the terms of the GNU Affero General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU Affero General Public License for more details.

   You should have received a copy of the GNU Affero General Public License
   along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/

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

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.creativecommons.thelist.R;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageAdapter extends PagerAdapter {
    public static final String TAG = ImageAdapter.class.getSimpleName();

    final private Context mContext;
    final private Activity activity;
    final private ArrayList<GalleryItem> photoObjects;
    private PhotoViewAttacher mAttacher;
    private LayoutInflater inflater;

    // constructor
    public ImageAdapter(Activity activity,
                                  ArrayList<GalleryItem> photoObjects) {
        this.mContext = activity.getApplicationContext();
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

        //Track with tags for share intent
        mImgDisplay.setTag(position);

        final android.support.v7.widget.Toolbar galleryCaption = (android.support.v7.widget.Toolbar)
                viewLayout.findViewById(R.id.gallery_caption_container);


        final GalleryItem g = photoObjects.get(position);
        final String photoUrl = g.getUrl() + "/600";

        final TextView itemName = (TextView) viewLayout.findViewById(R.id.gallery_item_name);
        final TextView makerName = (TextView) viewLayout.findViewById(R.id.gallery_maker_name);

        final String captionText = activity.getString(R.string.image_adapter_caption_text);

        itemName.setText(g.getItemName());
        makerName.setText(captionText + " " + g.getMakerName());
        //Log.v(TAG, g.getItemName() + " " + photoUrl + " " + g.getMakerName());

        Picasso.with(mContext)
                .load(photoUrl)
                .fit()
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .centerInside()
                //.placeholder() TODO: add placeholder
                .error(R.drawable.progress_view_large)
                .into(mImgDisplay, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        Log.v(TAG, "Successfully loaded image into PhotoView");

                        mAttacher = new PhotoViewAttacher(mImgDisplay);

                        mAttacher.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
                            @Override
                            public void onViewTap(View view, float x, float y) {
                                //Log.v(TAG, "TAPPED THAT VIEW");

                                if (galleryCaption.getVisibility() == View.INVISIBLE) {
                                    galleryCaption.setVisibility(View.VISIBLE);
                                } else {
                                    galleryCaption.setVisibility(View.INVISIBLE);
                                }
                            }
                        });

                    } //onAuthed

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
        object = null;

    }
} //ImageAdapter
