package com.microsoft.xbox.avatar.view;

import android.view.View;
import android.view.animation.Interpolator;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.anim.XLEAnimation;

public class XLEAvatarAnimation extends XLEAnimation {
    private XLEAvatarAnimationAction action;
    private AvatarViewEditor avatarView;
    private Runnable endRunnable;

    public XLEAvatarAnimation(XLEAvatarAnimationAction action) {
        this.action = action;
    }

    public void start() {
        this.avatarView.setOnAnimationCompletedRunnable(new Runnable() {
            public void run() {
                XLEAvatarAnimation.this.onAnimationCompleted();
            }
        });
        this.avatarView.animate(this.action);
    }

    private void onAnimationCompleted() {
        if (this.endRunnable != null) {
            this.endRunnable.run();
        }
    }

    public void setOnAnimationEnd(Runnable onEnd) {
        this.endRunnable = onEnd;
    }

    public void clear() {
    }

    public void setTargetView(View targetView) {
        XLEAssert.assertNotNull(targetView);
        XLEAssert.assertTrue(targetView instanceof AvatarViewEditor);
        this.avatarView = (AvatarViewEditor) targetView;
    }

    public void setInterpolator(Interpolator interpolator) {
    }
}
