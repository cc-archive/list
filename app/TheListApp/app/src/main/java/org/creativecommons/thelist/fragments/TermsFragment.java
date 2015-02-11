
/* The List powered by Creative Commons

   Copyright (C) 2014 Creative Commons

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

package org.creativecommons.thelist.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.creativecommons.thelist.R;
import org.creativecommons.thelist.utils.ListApplication;
import org.creativecommons.thelist.utils.RequestMethods;

public class TermsFragment extends Fragment {
    public static final String TAG = TermsFragment.class.getSimpleName();
    RequestMethods requestMethods = new RequestMethods(getActivity());
//    SharedPreferencesMethods sharedPreferencesMethods = new SharedPreferencesMethods(getActivity());
//    ListUser mCurrentUser = new ListUser();

    protected Button mNextButton;
    protected CheckBox mCheckBox;
    protected TextView mLearnMoreButton;
    protected TextView mCancelButton;

    //Interface with Activity
    TermsClickListener mCallback;

    //LISTENER
    public interface TermsClickListener {
        public void onTermsClicked();
        public void onTermsCancelled();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Google Analytics Tracker
        Tracker t = ((ListApplication) getActivity().getApplication()).getTracker(
                ListApplication.TrackerName.GLOBAL_TRACKER);

        t.setScreenName("Terms Fragment");
        t.send(new HitBuilders.AppViewBuilder().build());

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_terms, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        mCheckBox = (CheckBox)getView().findViewById(R.id.checkBox);
        mNextButton = (Button)getView().findViewById(R.id.nextButton);
        mCancelButton = (TextView)getView().findViewById(R.id.cancelButton);
        mLearnMoreButton = (TextView)getView().findViewById(R.id.learnMoreButton);

        mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCheckBox.isChecked()) {
                    mNextButton.setVisibility(View.VISIBLE);
                } else {
                    mNextButton.setVisibility(View.INVISIBLE);
                }
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onTermsClicked();
            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onTermsCancelled();
            }
        });

        if(mLearnMoreButton != null){
            mLearnMoreButton.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (TermsClickListener) activity;
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
}
