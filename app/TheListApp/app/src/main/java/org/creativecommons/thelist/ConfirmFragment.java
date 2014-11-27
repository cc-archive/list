package org.creativecommons.thelist;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.creativecommons.thelist.utils.ListUser;
import org.creativecommons.thelist.utils.RequestMethods;
import org.creativecommons.thelist.utils.SharedPreferencesMethods;


public class ConfirmFragment extends Fragment {
    public static final String TAG = ConfirmFragment.class.getSimpleName();
    RequestMethods requestMethods = new RequestMethods(getActivity());
    SharedPreferencesMethods sharedPreferencesMethods = new SharedPreferencesMethods(getActivity());
    ListUser mCurrentUser = new ListUser();


    //Interface with Activity
    ConfirmListener mCallback;

    public interface ConfirmListener {
        public void onConfirmFinish();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_confirm, container, false);

    }

    @Override
    public void onResume() {
        super.onResume();
        mCallback.onConfirmFinish();
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (ConfirmListener) activity;
        } catch(ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + activity.getString(R.string.confirm_callback_exception_message));
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }
}
