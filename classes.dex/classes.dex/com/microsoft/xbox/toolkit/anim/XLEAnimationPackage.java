package com.microsoft.xbox.toolkit.anim;

import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import java.util.Iterator;
import java.util.LinkedList;

public class XLEAnimationPackage {
    private LinkedList<XLEAnimationEntry> animations = new LinkedList();
    private Runnable onAnimationEndRunnable;
    private boolean running = false;

    private class XLEAnimationEntry {
        public XLEAnimation animation;
        public boolean done = false;
        public int iterationID = 0;

        public XLEAnimationEntry(XLEAnimation animation) {
            this.animation = animation;
            animation.setOnAnimationEnd(new Runnable(XLEAnimationPackage.this) {
                public void run() {
                    XLEAnimationEntry.this.onAnimationEnded();
                }
            });
        }

        private void onAnimationEnded() {
            boolean z;
            boolean z2 = true;
            if (Thread.currentThread() == ThreadManager.UIThread) {
                z = true;
            } else {
                z = false;
            }
            XLEAssert.assertTrue(z);
            if (XLEAnimationPackage.this.onAnimationEndRunnable == null) {
                z2 = false;
            }
            XLEAssert.assertTrue(z2);
            final int finishIterationID = this.iterationID;
            ThreadManager.UIThreadPost(new Runnable() {
                public void run() {
                    if (finishIterationID == XLEAnimationEntry.this.iterationID) {
                        XLEAnimationEntry.this.finish();
                    }
                }
            });
        }

        private void finish() {
            this.done = true;
            XLEAnimationPackage.this.tryFinishAll();
        }

        public void startAnimation() {
            this.animation.start();
        }

        public void clearAnimation() {
            this.iterationID++;
            this.animation.clear();
        }
    }

    private void tryFinishAll() {
        if (getRemainingAnimations() == 0) {
            XLEAssert.assertTrue(this.running);
            this.running = false;
            this.onAnimationEndRunnable.run();
        }
    }

    private int getRemainingAnimations() {
        int rv = 0;
        Iterator i$ = this.animations.iterator();
        while (i$.hasNext()) {
            if (!((XLEAnimationEntry) i$.next()).done) {
                rv++;
            }
        }
        return rv;
    }

    public void setOnAnimationEndRunnable(Runnable runnable) {
        this.onAnimationEndRunnable = runnable;
    }

    public void startAnimation() {
        XLEAssert.assertTrue(!this.running);
        this.running = true;
        Iterator i$ = this.animations.iterator();
        while (i$.hasNext()) {
            ((XLEAnimationEntry) i$.next()).startAnimation();
        }
    }

    public void clearAnimation() {
        Iterator i$ = this.animations.iterator();
        while (i$.hasNext()) {
            ((XLEAnimationEntry) i$.next()).clearAnimation();
        }
    }

    public void add(XLEAnimation animation) {
        this.animations.add(new XLEAnimationEntry(animation));
    }
}
