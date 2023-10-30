package com.microsoft.xbox.toolkit;

import android.content.Context;
import android.util.AttributeSet;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Switch;

public class XLESwitch extends Switch {
    public XLESwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void onPopulateAccessibilityEvent(AccessibilityEvent event) {
        if (getMeasuredHeight() > 0 && getMeasuredWidth() > 0) {
            super.onPopulateAccessibilityEvent(event);
        }
    }
}
