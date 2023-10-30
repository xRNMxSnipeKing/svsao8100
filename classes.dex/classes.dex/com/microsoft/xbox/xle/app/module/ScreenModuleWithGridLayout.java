package com.microsoft.xbox.xle.app.module;

import android.content.Context;
import android.util.AttributeSet;
import com.microsoft.xbox.toolkit.ui.AbstractGridLayout;
import com.microsoft.xbox.toolkit.ui.SwitchPanel;

public abstract class ScreenModuleWithGridLayout extends ScreenModuleLayout {
    protected abstract AbstractGridLayout getGridLayout();

    protected abstract SwitchPanel getSwitchPanel();

    public ScreenModuleWithGridLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setState(int listStateOrdinal) {
        if (getSwitchPanel() != null) {
            getSwitchPanel().setState(listStateOrdinal);
        }
        getGridLayout().getGridAdapter().setGridLayoutModelState(listStateOrdinal);
        getGridLayout().notifyDataChanged();
    }
}
