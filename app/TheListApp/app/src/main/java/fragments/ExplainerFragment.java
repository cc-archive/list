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

package fragments;


import android.app.Activity;
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

    protected Button mNextButton;
    protected TextView mTextView;
    protected ImageView mImageView;

    //Interface with Activity
    OnClickListener mCallback;

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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_explainer, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        mNextButton = (Button)getView().findViewById(R.id.nextButton);
        mTextView = (TextView)getView().findViewById(R.id.explainer_text);
        //TODO: if clickCount < array.length(), show next item in array else, send onNextClicked()

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = 0;

                //Text/Image Resources
                String [] explainerText = getResources().getStringArray
                        (R.array.onboarding_explainers);
                String [] explainerButtonText = getResources().getStringArray
                        (R.array.onboarding_button_text);
                //TODO: check this works
                String imageName = "R.drawable.explainer" + String.valueOf(count+1);

                if(count < explainerText.length) {
                    //Set Image/Text Resources
                    int id = getResources().getIdentifier(imageName, null, null);
                    mImageView.setImageResource(id);
                    mTextView.setText(explainerText[count]);
                    mNextButton.setText(explainerButtonText[count]);

                    count ++;
                } else {
                    //Notify Activity
                    mCallback.onNextClicked();
                }



            }
        });
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

}
