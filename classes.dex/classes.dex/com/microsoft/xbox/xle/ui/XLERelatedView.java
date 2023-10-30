package com.microsoft.xbox.xle.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.BaseAdapter;
import com.microsoft.xbox.toolkit.ui.XLEGridView;

public class XLERelatedView extends XLEGridView {
    public XLERelatedView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setSoundEffectsEnabled(false);
        setSelector(17170445);
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            GridViewItemLayout.initItemLayout(getNumColumns(), ((BaseAdapter) getAdapter()).getCount());
        }
        super.onLayout(changed, l, t, r, b);
    }
}
