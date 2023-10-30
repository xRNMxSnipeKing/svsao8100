package com.microsoft.xbox.toolkit.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import com.microsoft.xbox.toolkit.XboxApplication;

public class SwitchPanel extends LinearLayout {
    private final int INVALID_STATE_ID = -1;
    private int selectedState;

    public SwitchPanel(Context context) {
        super(context);
        throw new UnsupportedOperationException();
    }

    public SwitchPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.selectedState = context.obtainStyledAttributes(attrs, XboxApplication.Instance.getStyleableRValueArray("SwitchPanel")).getInteger(XboxApplication.Instance.getStyleableRValue("SwitchPanel_selectedState"), -1);
        if (this.selectedState < 0) {
            throw new IllegalArgumentException("You must specify the selectedState attribute in the xml, and the value must be positive.");
        }
        setLayoutParams(new LayoutParams(-1, -1));
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        updateVisibility();
    }

    public void setState(int newState) {
        if (newState < 0) {
            throw new IllegalArgumentException("New state must be a positive value.");
        } else if (this.selectedState != newState) {
            this.selectedState = newState;
            updateVisibility();
        }
    }

    private void updateVisibility() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View v = getChildAt(i);
            if (v.getClass() != SwitchPanelItem.class) {
                throw new UnsupportedOperationException("All children of SwitchPanel must be of SwitchPanelItem type. All other types are not supported and should be removed.");
            }
            if (((SwitchPanelItem) v).getState() == this.selectedState) {
                v.setVisibility(0);
            } else {
                v.setVisibility(8);
            }
        }
        requestLayout();
    }
}
