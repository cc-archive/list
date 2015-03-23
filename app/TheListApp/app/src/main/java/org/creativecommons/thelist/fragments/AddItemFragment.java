package org.creativecommons.thelist.fragments;


import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.creativecommons.thelist.R;
import org.creativecommons.thelist.utils.MessageHelper;
import org.creativecommons.thelist.utils.RequestMethods;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddItemFragment extends android.support.v4.app.Fragment {
    public static final String TAG = AddItemFragment.class.getSimpleName();

    Context mContext;
    private MessageHelper mMessageHelper;
    private RequestMethods mRequestMethods;

    // --------------------------------------------------------


    public AddItemFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_item, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        mContext = getActivity();
        mMessageHelper = new MessageHelper(mContext);
        mRequestMethods = new RequestMethods(mContext);

    } //onResume

    @Override
    public void onDetach() {
        super.onDetach();
        //mCallback = null;
    }

} //AddItemFragment
