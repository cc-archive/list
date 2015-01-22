package org.creativecommons.thelist.utils;

import android.content.Context;

import com.nispok.snackbar.Snackbar;

public class SnackbarAnim {
    private Context mContext;
    private Snackbar mSnackbar;

    private SnackbarAnim(Context context) {
        mContext = context;
    }


//    SnackbarManager.show(
//            //also includes duration: SHORT, LONG, INDEFINITE,
//            Snackbar.with(mContext)
//            .text("Item deleted") //text to display
//    .actionColor(getResources().getColor(R.color.colorSecondary)) //action colour
//            .actionLabel("undo".toUpperCase())
//            .actionListener(new ActionClickListener() {
//        @Override
//        public void onActionClicked(Snackbar snackbar) {
//            //TODO: store deleted item position + deleted item
//        }
//    }) //action button’s listener
//            .eventListener(new EventListener() {
//
//        XmlPullParser parser = getResources().getAnimation(R.anim.sb__decelerate_cubic);
//        AttributeSet attributes = Xml.asAttributeSet(parser);
//        android.view.animation.Interpolator interpolator = new AccelerateInterpolator(mContext, attributes);
//
//
//        @Override
//        public void onShow(Snackbar snackbar) {
//            Log.v("FAB Y BEFORE: ", String.valueOf(mFab.getY()));
//            animate(mFab)
//                    .translationY(-(snackbar.getHeight()))
//                    .alpha(1)
//                    .setDuration(300)
//                    .setInterpolator(interpolator)
//                    .setListener(null);
//            Log.v("FAB Y AFTER: ", String.valueOf(mFab.getY()));
//        }
//        @Override
//        public void onShown(Snackbar snackbar) {
//        }
//        @Override
//        public void onDismiss(Snackbar snackbar) {
//            XmlPullParser parser = getResources().getAnimation(R.anim.sb__accelerate_cubic);
//            AttributeSet attributes = Xml.asAttributeSet(parser);
//            android.view.animation.Interpolator interpolator = new AccelerateInterpolator(mContext, attributes);
//            Log.v("FAB Y DISMISS BEFORE: ", String.valueOf(mFab.getY()));
//            animate(mFab)
//                    .translationY(0)
//                    .alpha(1)
//                    .setDuration(300)
//                    .setInterpolator(interpolator)
//                    .setListener(null);
//
//            Log.v("FAB Y DISMISS AFTER: ", String.valueOf(mFab.getY()));
//        }
//        @Override
//        public void onDismissed(Snackbar snackbar) {
//        }
//    }) //event listener
//            , MainActivity.this);



}

