package com.microsoft.xbox.xle.ui;

import android.content.Context;
import android.util.AttributeSet;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.ui.ScreenLayout;
import com.microsoft.xbox.toolkit.ui.pivot.Pivot;
import com.microsoft.xbox.xle.viewmodel.DetailPivotPaneData;

public class DetailsPivot extends Pivot {
    private DetailPivotPaneData[] detailPanesData;

    public DetailsPivot(Context context) {
        super(context);
        throw new UnsupportedOperationException();
    }

    public DetailsPivot(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected ScreenLayout[] getInitialPanes() {
        return null;
    }

    public void setDetailPaneData(DetailPivotPaneData[] panesData) {
        this.detailPanesData = panesData;
        int index = 0;
        for (DetailPivotPaneData paneData : this.detailPanesData) {
            if (paneData.getIsDisplayed() && addDetailsPivotPane(paneData, index) != null) {
                index++;
            }
        }
    }

    public ScreenLayout addDetailsPivotPane(DetailPivotPaneData paneData, int index) {
        XLELog.Diagnostic("DetailsPivot", String.format("Adding details pivot pane '%s' at index %d", new Object[]{paneData.getPivotPaneClass().getSimpleName(), Integer.valueOf(index)}));
        ScreenLayout screen = null;
        try {
            screen = (ScreenLayout) paneData.getPivotPaneClass().getConstructor(new Class[0]).newInstance(new Object[0]);
        } catch (Exception e) {
            XLELog.Error("DetailsPivot", String.format("FIXME: Failed to create a screen of type '%s' with error: %s", new Object[]{paneData.getPivotPaneClass().getName(), e.toString()}));
        }
        if (screen == null) {
            return null;
        }
        addPivotPane(screen, index);
        return screen;
    }

    public ScreenLayout removeDetailsPivotPane(int index) {
        XLELog.Diagnostic("DetailsPivot", String.format("Removing details pivot pane at index %d", new Object[]{Integer.valueOf(index)}));
        return removePivotPane(index);
    }
}
