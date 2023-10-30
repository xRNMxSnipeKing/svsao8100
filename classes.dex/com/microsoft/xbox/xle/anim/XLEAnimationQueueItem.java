package com.microsoft.xbox.xle.anim;

import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.anim.XLEAnimationPackage;

public class XLEAnimationQueueItem {
    private XLEAnimationPackage animation;
    private Runnable completedRunnable;

    public XLEAnimationQueueItem(XLEAnimationPackage animation) {
    }

    public void setOnCompletedRunnable(Runnable r) {
        this.completedRunnable = r;
    }

    public void start() {
        XLELog.Diagnostic("ApplicationBar", "Animation state updated: start");
        this.animation.setOnAnimationEndRunnable(new Runnable() {
            public void run() {
                XLEAnimationQueueItem.this.onAnimationEnd();
            }
        });
        this.animation.startAnimation();
    }

    private void onAnimationEnd() {
        XLELog.Diagnostic("ApplicationBar", "Animation state updated: end");
        if (this.completedRunnable != null) {
            this.completedRunnable.run();
        }
    }
}
