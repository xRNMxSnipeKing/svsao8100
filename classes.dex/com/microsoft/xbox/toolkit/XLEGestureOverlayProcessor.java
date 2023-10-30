package com.microsoft.xbox.toolkit;

import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import java.util.Timer;
import java.util.TimerTask;

public class XLEGestureOverlayProcessor {
    private static final int REPEAT_GESTURE_MS = 250;
    private int deadZoneRadius;
    private GestureDetector gestureDetector;
    private boolean gestureProcessed;
    private float lastx;
    private float lasty;
    private OnGestureRunnable onGestureEvent;
    private Timer repeatGestureTimer;
    private float startx;
    private float starty;

    public enum GestureType {
        TAP,
        SWIPE_LEFT,
        SWIPE_RIGHT,
        SWIPE_UP,
        SWIPE_DOWN
    }

    public static abstract class OnGestureRunnable {
        public abstract void onGesture(GestureType gestureType);
    }

    private class SmartGlassGestureListener extends SimpleOnGestureListener {
        private SmartGlassGestureListener() {
        }

        public boolean onSingleTapUp(MotionEvent e) {
            XLEGestureOverlayProcessor.this.sendGesture(GestureType.TAP);
            return true;
        }

        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            XLEGestureOverlayProcessor.this.lastx = e2.getRawX();
            XLEGestureOverlayProcessor.this.lasty = e2.getRawY();
            return true;
        }

        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }

        public boolean onDown(MotionEvent e) {
            XLEGestureOverlayProcessor.this.lastx = XLEGestureOverlayProcessor.this.startx = e.getRawX();
            XLEGestureOverlayProcessor.this.lasty = XLEGestureOverlayProcessor.this.starty = e.getRawY();
            return false;
        }
    }

    private class SmartGlassGestureTimerTask extends TimerTask {
        private SmartGlassGestureTimerTask() {
        }

        public void run() {
            ThreadManager.UIThreadSend(new Runnable() {
                public void run() {
                    XLEGestureOverlayProcessor.this.processGesture();
                }
            });
        }
    }

    public XLEGestureOverlayProcessor(int deadZoneRadius) {
        this.repeatGestureTimer = null;
        this.gestureDetector = null;
        this.deadZoneRadius = 0;
        this.gestureProcessed = false;
        this.onGestureEvent = null;
        this.gestureDetector = new GestureDetector(new SmartGlassGestureListener());
        this.gestureDetector.setIsLongpressEnabled(false);
        this.deadZoneRadius = deadZoneRadius;
    }

    public void setOnSendGestureEvent(OnGestureRunnable callable) {
        this.onGestureEvent = callable;
    }

    public boolean onTouch(MotionEvent event) {
        switch (event.getActionMasked()) {
            case 0:
                onGestureBegin(event.getRawX(), event.getRawY());
                break;
            case 1:
            case 3:
                onGestureEnd();
                break;
        }
        this.gestureDetector.onTouchEvent(event);
        return true;
    }

    public void cancel() {
        this.gestureProcessed = false;
        if (this.repeatGestureTimer != null) {
            this.repeatGestureTimer.cancel();
            this.repeatGestureTimer = null;
        }
    }

    private void onGestureBegin(float x, float y) {
        this.gestureProcessed = false;
        this.repeatGestureTimer = new Timer();
        this.repeatGestureTimer.scheduleAtFixedRate(new SmartGlassGestureTimerTask(), 250, 250);
    }

    private void onGestureEnd() {
        if (this.repeatGestureTimer != null) {
            this.repeatGestureTimer.cancel();
            this.repeatGestureTimer = null;
            processGestureIfNotAlreadyProcessed();
        }
    }

    private void processGestureIfNotAlreadyProcessed() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        if (!this.gestureProcessed) {
            processGesture();
        }
    }

    private void processGesture() {
        float dx = this.lastx - this.startx;
        float dy = this.lasty - this.starty;
        if ((dx * dx) + (dy * dy) >= ((float) (this.deadZoneRadius * this.deadZoneRadius))) {
            GestureType rv;
            if (Math.abs(dy) > Math.abs(dx)) {
                if (dy > 0.0f) {
                    rv = GestureType.SWIPE_DOWN;
                } else {
                    rv = GestureType.SWIPE_UP;
                }
            } else if (dx > 0.0f) {
                rv = GestureType.SWIPE_RIGHT;
            } else {
                rv = GestureType.SWIPE_LEFT;
            }
            sendGesture(rv);
        }
    }

    private void sendGesture(GestureType type) {
        this.gestureProcessed = true;
        if (this.onGestureEvent != null) {
            this.onGestureEvent.onGesture(type);
        }
    }
}
