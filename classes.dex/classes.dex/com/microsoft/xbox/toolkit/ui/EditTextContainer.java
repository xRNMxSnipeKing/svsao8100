package com.microsoft.xbox.toolkit.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.KeyEvent.DispatcherState;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xle.test.interop.TestInterop;
import com.microsoft.xle.test.interop.delegates.Action;
import java.util.ArrayList;
import java.util.Iterator;

public class EditTextContainer extends RelativeLayout {
    private static final float MOVE_DIPS_DISTANCE_SQUARED = 10.0f;
    private ArrayList<View> children;
    private boolean grabBackButton;
    private Runnable keyboardDismissedRunnable;
    private float startX;
    private float startY;
    private FrameLayout unfocused;

    public EditTextContainer(Context context) {
        super(context);
        this.keyboardDismissedRunnable = null;
        this.grabBackButton = false;
        ScreenLayout.addViewThatCausesAndroidLeaks(this);
    }

    public EditTextContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.keyboardDismissedRunnable = null;
        this.grabBackButton = false;
        this.children = new ArrayList();
        this.unfocused = new FrameLayout(getContext());
        this.unfocused.setFocusable(true);
        this.unfocused.setFocusableInTouchMode(true);
        addView(this.unfocused);
        ScreenLayout.addViewThatCausesAndroidLeaks(this);
    }

    private void processTouchEvent(MotionEvent ev) {
        switch (ev.getAction() & 255) {
            case 0:
                XLELog.Diagnostic("EditTextContainer", "key down");
                this.startX = ev.getRawX();
                this.startY = ev.getRawY();
                return;
            case 1:
                XLELog.Diagnostic("EditTextContainer", "key up");
                if (distanceSquare(ev.getRawX(), this.startX, ev.getRawY(), this.startY) >= MOVE_DIPS_DISTANCE_SQUARED) {
                    XLELog.Diagnostic("EditTextContainer", "moved or keyboard not shown , ignore");
                    return;
                } else if (isPointInChildren(this.startX, this.startY)) {
                    XLELog.Diagnostic("EditTextContainer", "click on another child don't dismiss keyboard");
                    return;
                } else {
                    XLELog.Diagnostic("EditTextContainer", "click on edittext container, hide keyboard ");
                    hideSoftKeyboard();
                    return;
                }
            default:
                return;
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        processTouchEvent(ev);
        return false;
    }

    public boolean onTouchEvent(MotionEvent ev) {
        processTouchEvent(ev);
        switch (ev.getAction() & 255) {
            case 0:
                return true;
            default:
                return false;
        }
    }

    public void setKeyboardShown() {
        XLELog.Diagnostic("EditTextContainer", "keyboard shown");
        TestInterop.setDismissSoftKeyboard(new Action() {
            public void invoke() {
                EditTextContainer.this.hideSoftKeyboard();
            }
        });
    }

    public void addChild(View child) {
        if (!this.children.contains(child)) {
            this.children.add(child);
        }
    }

    public void setKeyboardDismissedRunnable(Runnable r) {
        this.keyboardDismissedRunnable = r;
    }

    public boolean dispatchKeyEventPreIme(KeyEvent event) {
        if (this.grabBackButton && event.getKeyCode() == 4) {
            DispatcherState state = getKeyDispatcherState();
            if (state != null) {
                if (event.getAction() == 0 && event.getRepeatCount() == 0) {
                    state.startTracking(event, this);
                    return true;
                } else if (event.getAction() == 1 && !event.isCanceled() && state.isTracking(event)) {
                    onKeyboardDismissed();
                    return true;
                }
            }
        }
        return super.dispatchKeyEventPreIme(event);
    }

    public void setGrabBackButton(boolean v) {
        this.grabBackButton = v;
    }

    public void unfocusText() {
        this.unfocused.requestFocus();
    }

    private void hideSoftKeyboard() {
        XboxApplication.MainActivity.hideKeyboard();
        XboxApplication.MainActivity.hideKeyboard();
        onKeyboardDismissed();
        XLELog.Diagnostic("EditTextContainer", "keyboard hidden now ");
        TestInterop.setDismissSoftKeyboard(null);
        requestFocus();
    }

    private void onKeyboardDismissed() {
        unfocusText();
        if (this.keyboardDismissedRunnable != null) {
            this.keyboardDismissedRunnable.run();
        }
    }

    private float distanceSquare(float x, float x1, float y, float y1) {
        return ((x - x1) * (x - x1)) + ((y - y1) * (y - y1));
    }

    private boolean isPointInChildren(float x, float y) {
        Iterator i$ = this.children.iterator();
        while (i$.hasNext()) {
            if (JavaUtil.isTouchPointInsideView(x, y, (View) i$.next())) {
                return true;
            }
        }
        return false;
    }
}
