package com.microsoft.xbox.toolkit.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

public class XLEClickableLayout extends RelativeLayout {
    public XLEClickableLayout(Context context) {
        super(context);
        setSoundEffectsEnabled(false);
    }

    public XLEClickableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setSoundEffectsEnabled(false);
    }

    public void setOnClickListener(OnClickListener listener) {
        super.setOnClickListener(TouchUtil.createOnClickListener(listener));
    }
}
