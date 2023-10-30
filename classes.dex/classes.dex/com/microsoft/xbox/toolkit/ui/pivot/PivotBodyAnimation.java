package com.microsoft.xbox.toolkit.ui.pivot;

import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.Transformation;
import com.microsoft.xbox.toolkit.ThreadManager;

public class PivotBodyAnimation extends Animation {
    private PivotBody body;
    private float endX;
    private float startX;

    public PivotBodyAnimation(PivotBody body, float startX, float endX) {
        this.body = body;
        this.startX = startX;
        this.endX = endX;
    }

    protected void applyTransformation(float interpolatedTime, Transformation t) {
        this.body.setScrollX((int) (this.startX + (interpolatedTime * (this.endX - this.startX))));
    }

    public boolean willChangeBounds() {
        return true;
    }

    public void setAnimationEndPostRunnable(final Runnable r) {
        setAnimationListener(new AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                ThreadManager.UIThreadPost(r);
            }
        });
    }
}
