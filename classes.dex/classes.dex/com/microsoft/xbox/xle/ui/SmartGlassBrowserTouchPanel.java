package com.microsoft.xbox.xle.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;

public class SmartGlassBrowserTouchPanel extends RelativeLayout {
    private boolean intercept = false;
    private OnTouchListener listener = null;

    public SmartGlassBrowserTouchPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setOnTouchListener(OnTouchListener listener) {
        this.listener = listener;
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean rv = this.intercept;
        this.intercept = false;
        return rv;
    }

    public boolean onTouchEvent(MotionEvent ev) {
        processTouchEvent(ev);
        return true;
    }

    public void setIntercept(boolean intercept) {
        this.intercept = intercept;
    }

    private void processTouchEvent(MotionEvent ev) {
        if (this.listener != null) {
            this.listener.onTouch(this, ev);
        }
    }
}
