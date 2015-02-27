package org.creativecommons.thelist.utils;

import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

public class MaterialInterpolator implements Interpolator {

    @Override
    public float getInterpolation(float input) {
        if(input<1./3f)
            return new AccelerateInterpolator(1f).getInterpolation(input);
        else
            return new DecelerateInterpolator(0.75f).getInterpolation(input);
    }

}
