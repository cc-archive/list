package org.creativecommons.thelist.fragments;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.creativecommons.thelist.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ImageFragment extends android.support.v4.app.Fragment {
    public static final String TAG = ImageFragment.class.getSimpleName();
    private Context mContext;

    //UI Elements
    private ImageView galleryImage;

    //Interface with Activity + ListUser
    //public ImageListener mCallback;


    // --------------------------------------------------------

    public ImageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

//        try {
//            mCallback = (GalleryListener) activity;
//        } catch(ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + activity.getString(R.string.gallery_callback_exception_message));
//        }
    } //onAttach


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_image, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        mContext = getActivity();
        galleryImage = (ImageView) getView().findViewById(R.id.gallery_full_image);

        Bundle b = getArguments();
        String url = b.getString("url");

        Picasso.with(mContext)
                .load(url)
                .placeholder(R.drawable.progress_view)
                .error(R.drawable.error_view)
                .into(galleryImage);

    } //onResume

} //ImageFragment
