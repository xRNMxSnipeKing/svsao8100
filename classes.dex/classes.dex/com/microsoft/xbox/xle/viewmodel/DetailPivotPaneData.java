package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.xbox.xle.app.activity.ActivityBase;

public class DetailPivotPaneData {
    private boolean displayed;
    private final Class<? extends ActivityBase> pivotPaneClass;

    public DetailPivotPaneData(Class<? extends ActivityBase> pivotPaneClass, boolean displayed) {
        this.displayed = displayed;
        this.pivotPaneClass = pivotPaneClass;
    }

    public boolean getIsDisplayed() {
        return this.displayed;
    }

    public void setIsDisplayed(boolean displayed) {
        this.displayed = displayed;
    }

    public Class<? extends ActivityBase> getPivotPaneClass() {
        return this.pivotPaneClass;
    }
}
