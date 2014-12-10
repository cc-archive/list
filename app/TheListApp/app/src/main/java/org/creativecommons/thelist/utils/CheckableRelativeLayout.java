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

package org.creativecommons.thelist.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.CheckedTextView;
import android.widget.RelativeLayout;

/**
 * Created by damaris on 2014-12-05.
 */
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

