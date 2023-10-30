package com.microsoft.xbox.toolkit.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.RelativeLayout.LayoutParams;
import com.microsoft.xbox.toolkit.XboxApplication;

public class SwitchPanelItem extends FrameLayout {
    private final int INVALID_STATE_ID = -1;
    private int state;

    public SwitchPanelItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.state = context.obtainStyledAttributes(attrs, XboxApplication.Instance.getStyleableRValueArray("SwitchPanelItem")).getInteger(XboxApplication.Instance.getStyleableRValue("SwitchPanelItem_state"), -1);
        if (this.state < 0) {
            throw new IllegalArgumentException("You must specify the state attribute in the xml, and the value must be positive.");
        }
        setLayoutParams(new LayoutParams(-1, -1));
    }

    public int getState() {
        return this.state;
    }
}
