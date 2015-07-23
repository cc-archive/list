//TODO:

package org.creativecommons.thelist.layouts;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.CheckedTextView;
import android.widget.RelativeLayout;

public class CheckableRelativeLayout extends RelativeLayout implements Checkable {

    private boolean checked = false;

    public CheckableRelativeLayout(Context context) {
        super(context, null);
    }

    public CheckableRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private static final int[] CheckedStateSet = {
            android.R.attr.state_checked
    };

    @Override
    protected void dispatchSetPressed(boolean pressed)
    {
        super.dispatchSetPressed(pressed);
        setChecked(pressed);
    }

    @Override
    public void setChecked(boolean checked) {
        this.checked = checked;
        for (int index = 0; index < getChildCount(); index++)
        {
            View view = getChildAt(index);
            if (view.getClass().toString().equals(CheckedTextView.class.toString()))
            {
                CheckedTextView checkable = (CheckedTextView)view;
                checkable.setChecked(checked);
                checkable.refreshDrawableState();
            }
        }
        refreshDrawableState();
    }

    public boolean isChecked() {
        return checked;
    }

    public void toggle() {
        checked = !checked;
        refreshDrawableState();
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(drawableState, CheckedStateSet);
        }
        return drawableState;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }
}

