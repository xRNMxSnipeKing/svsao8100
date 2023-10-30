package com.microsoft.xbox.toolkit.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ProgressBar;

public class FastProgressBar extends ProgressBar {
    private boolean isEnabled;
    private int visibility;

    public FastProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEnabled(true);
        setVisibility(0);
    }

    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        postInvalidateDelayed(33);
    }

    public void setEnabled(boolean enabled) {
        if (this.isEnabled != enabled) {
            this.isEnabled = enabled;
            if (this.isEnabled) {
                super.setVisibility(this.visibility);
                return;
            }
            this.visibility = getVisibility();
            super.setVisibility(8);
        }
    }

    public void setVisibility(int v) {
        if (this.isEnabled) {
            super.setVisibility(v);
        } else {
            this.visibility = v;
        }
    }
}
