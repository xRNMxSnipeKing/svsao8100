package com.microsoft.xbox.toolkit.anim;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import com.microsoft.xbox.toolkit.XLELog;

public class HeightAnimation extends Animation {
    private int fromValue;
    private int toValue;
    private View view;

    public HeightAnimation(int from, int to) {
        this.fromValue = from;
        this.toValue = to;
    }

    public void setTargetView(View targetView) {
        this.view = targetView;
        this.fromValue = targetView.getHeight();
    }

    protected void applyTransformation(float interpolatedTime, Transformation t) {
        int newDelta = (int) (((float) (this.toValue - this.fromValue)) * interpolatedTime);
        this.view.getLayoutParams().height = this.fromValue + newDelta;
        this.view.requestLayout();
        XLELog.Diagnostic("HeightAnimation", "New height: " + Integer.toString(this.view.getLayoutParams().height));
    }

    public boolean willChangeBounds() {
        return true;
    }
}
