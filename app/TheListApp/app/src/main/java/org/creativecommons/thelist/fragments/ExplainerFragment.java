/* The List powered by Creative Commons

   Copyright (C) 2014, 2015 Creative Commons Corporation

   This program is free software: you can redistribute it and/or modify
   it under the terms of either the GNU Affero General Public License or
   the GNU General Public License as published by the
   Free Software Foundation, either version 3 of the Licenses, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  

   You should have received a copy of the GNU General Public License and
   the GNU Affero General Public License along with this program.  

   If not, see <http://www.gnu.org/licenses/>.

*/

package org.creativecommons.thelist.fragments;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.creativecommons.thelist.R;

public class ExplainerFragment extends Fragment {
    public static final String TAG = ExplainerFragment.class.getSimpleName();

    private Context mContext;

    protected Button mNextButton;
    protected TextView mTextView;
    protected ImageView mImageView;
    protected int count;

    //Text/Image Resources
    String[] explainerText;
    String[] explainerButtonText;

    //Interface with Activity
    OnClickListener mCallback;

    // --------------------------------------------------------

    //LISTENER
    public interface OnClickListener {
        public void onNextClicked();
    }

    public ExplainerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_explainer, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mContext = getActivity();
        Activity activity = getActivity();

        //UI Elements
        mNextButton = (Button) activity.findViewById(R.id.nextButton);
        mTextView = (TextView) activity.findViewById(R.id.explainer_text);
        mImageView = (ImageView) activity.findViewById(R.id.explainer_image);

        explainerText = activity.getResources().getStringArray
                (R.array.onboarding_explainers);
        explainerButtonText = activity.getResources().getStringArray
                (R.array.onboarding_button_text);

        //TODO: if clickCount < array.length(), show next item in array else, send onNextClicked()
        count = 0;
        //Get the first explainer (at [0])
        setExplainerContent();

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(count < explainerText.length) {
                    setExplainerContent();
                } else {
                    mCallback.onNextClicked();
                }
            }
        });

    } //onActivityCreated

    @Override
    public void onResume() {
        super.onResume();
    }

    public void setExplainerContent(){
        //Set Image/Text Resources
        int id = getResources().getIdentifier("explainer" + String.valueOf(count + 1), "drawable",
                getActivity().getPackageName());

        mImageView.setImageResource(id);
        mTextView.setText(explainerText[count]);
        mNextButton.setText(explainerButtonText[count]);
        count ++;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (OnClickListener) activity;
        } catch(ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + activity.getString(R.string.terms_callback_exception_message));
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

} //ExplainerFragment
