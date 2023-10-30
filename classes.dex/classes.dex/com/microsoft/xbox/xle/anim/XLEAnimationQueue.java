package com.microsoft.xbox.xle.anim;

import com.microsoft.xbox.toolkit.anim.XLEAnimationPackage;
import java.util.LinkedList;
import java.util.Queue;

public class XLEAnimationQueue {
    private boolean animating = false;
    private Queue<XLEAnimationQueueItem> items = new LinkedList();

    public static class XLEAnimationQueueItem {
        public Runnable afterRunnable;
        public Runnable beforeRunnable;
        public XLEAnimationPackage pack;
    }

    public void clear() {
        this.items.clear();
    }

    public void push(XLEAnimationQueueItem item) {
        if (item != null && item.pack != null) {
            this.items.add(item);
        }
    }

    public void startNext() {
        if (!this.animating && !this.items.isEmpty()) {
            final XLEAnimationQueueItem next = (XLEAnimationQueueItem) this.items.remove();
            next.pack.setOnAnimationEndRunnable(new Runnable() {
                public void run() {
                    if (next.afterRunnable != null) {
                        next.afterRunnable.run();
                    }
                    XLEAnimationQueue.this.animating = false;
                    XLEAnimationQueue.this.startNext();
                }
            });
            if (next.beforeRunnable != null) {
                next.beforeRunnable.run();
            }
            this.animating = true;
            next.pack.startAnimation();
        }
    }
}
