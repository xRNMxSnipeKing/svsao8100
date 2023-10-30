package com.microsoft.xbox.xle.app.activity;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.anim.XLEAnimationPackage;
import com.microsoft.xbox.toolkit.ui.ScreenLayout;
import com.microsoft.xbox.toolkit.ui.pivot.Pivot;
import com.microsoft.xbox.xle.app.ApplicationBarManager;
import com.microsoft.xbox.xle.ui.DetailsPivot;
import com.microsoft.xbox.xle.viewmodel.DetailPageHelper;
import com.microsoft.xbox.xle.viewmodel.DetailPivotPaneData;
import com.microsoft.xbox.xle.viewmodel.DetailsPivotActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.XLEGlobalData;
import junit.framework.Assert;

public class DetailsPivotActivity extends PivotActivity {
    private String detailDisplayTitle;
    private DetailPivotPaneData[] pivotData;

    public void onCreate() {
        super.onCreate();
        this.pivotData = XLEGlobalData.getInstance().getDetailPivotData();
        Assert.assertNotNull(this.pivotData);
        if (DetailPageHelper.isActivityDetailsPivotPaneData(this.pivotData)) {
            this.detailDisplayTitle = XLEGlobalData.getInstance().getSelectedActivityData().getDisplayTitle();
        } else {
            this.detailDisplayTitle = XLEGlobalData.getInstance().getSelectedMediaItemData().getDisplayTitle();
        }
        setContentView(R.layout.details_pivot_activity);
        this.pivot = (Pivot) findViewById(R.id.details_pivot);
        Assert.assertTrue(this.pivot instanceof DetailsPivot);
        ((DetailsPivot) this.pivot).setDetailPaneData(this.pivotData);
        this.pivot.onCreate();
        this.viewModel = new DetailsPivotActivityViewModel();
    }

    public void onStart() {
        XLEGlobalData.getInstance().setPivotTitle(this.detailDisplayTitle);
        super.onStart();
        XLEGlobalData.getInstance().setDetailPivotData(this.pivotData);
    }

    public void addPivotPane(Class<? extends ActivityBase> pivotPaneClass) {
        XLELog.Diagnostic("DetailsPivotActivity", String.format("Adding pivot pane class '%s'", new Object[]{pivotPaneClass.getSimpleName()}));
        XLEAssert.assertTrue(getIsStarted());
        int index = 0;
        for (DetailPivotPaneData paneData : this.pivotData) {
            if (paneData.getIsDisplayed()) {
                index++;
            } else if (paneData.getPivotPaneClass().equals(pivotPaneClass)) {
                ScreenLayout screen = ((DetailsPivot) this.pivot).addDetailsPivotPane(paneData, index);
                if (screen != null) {
                    screen.onCreate();
                    screen.onStart();
                    screen.onResume();
                    paneData.setIsDisplayed(true);
                    ApplicationBarManager.getInstance().setTotalPageCount(this.pivot.getTotalPaneCount());
                    if (this.pivot.getCurrentPivotPaneIndex() >= index) {
                        this.pivot.setCurrentPivotPaneIndex(this.pivot.getCurrentPivotPaneIndex() + 1);
                    } else {
                        this.pivot.setCurrentPivotPaneIndex(this.pivot.getCurrentPivotPaneIndex());
                    }
                    XLELog.Diagnostic("DetailsPivotActivity", String.format("Successfully added pivot pane class '%s'", new Object[]{pivotPaneClass.getSimpleName()}));
                    return;
                }
                XLELog.Error("DetailsPivotActivity", String.format("Failed to add pivot pane class '%s'", new Object[]{pivotPaneClass.getSimpleName()}));
                XLELog.Diagnostic("DetailsPivotActivity", String.format("Pivot pane class '%s' is not added. Either it is already displayed or not defined correctly in DetailPageHelper.", new Object[]{pivotPaneClass.getSimpleName()}));
            }
        }
        XLELog.Diagnostic("DetailsPivotActivity", String.format("Pivot pane class '%s' is not added. Either it is already displayed or not defined correctly in DetailPageHelper.", new Object[]{pivotPaneClass.getSimpleName()}));
    }

    public void removePivotPane(Class<? extends ActivityBase> pivotPaneClass) {
        XLELog.Diagnostic("DetailsPivotActivity", String.format("Removing pivot pane class '%s'", new Object[]{pivotPaneClass.getSimpleName()}));
        XLEAssert.assertTrue(getIsStarted());
        int index = 0;
        for (DetailPivotPaneData paneData : this.pivotData) {
            if (paneData.getIsDisplayed() && paneData.getPivotPaneClass().equals(pivotPaneClass)) {
                ScreenLayout screen = ((DetailsPivot) this.pivot).removeDetailsPivotPane(index);
                if (screen != null) {
                    screen.onPause();
                    screen.onStop();
                    screen.onDestroy();
                    paneData.setIsDisplayed(false);
                    ApplicationBarManager.getInstance().setTotalPageCount(this.pivot.getTotalPaneCount());
                    if (this.pivot.getCurrentPivotPaneIndex() >= index) {
                        this.pivot.setCurrentPivotPaneIndex(this.pivot.getCurrentPivotPaneIndex() - 1);
                    } else {
                        this.pivot.setCurrentPivotPaneIndex(this.pivot.getCurrentPivotPaneIndex());
                    }
                    XLELog.Diagnostic("DetailsPivotActivity", String.format("Successfully removed pivot pane class '%s'", new Object[]{pivotPaneClass.getSimpleName()}));
                    return;
                }
                XLELog.Error("DetailsPivotActivity", String.format("Failed to remove pivot pane class '%s'", new Object[]{pivotPaneClass.getSimpleName()}));
                XLELog.Diagnostic("DetailsPivotActivity", String.format("Pivot pane class '%s' is not removed. Either it is already removed or not defined correctly in DetailPageHelper.", new Object[]{pivotPaneClass.getSimpleName()}));
            }
            index++;
        }
        XLELog.Diagnostic("DetailsPivotActivity", String.format("Pivot pane class '%s' is not removed. Either it is already removed or not defined correctly in DetailPageHelper.", new Object[]{pivotPaneClass.getSimpleName()}));
    }

    public XLEAnimationPackage getAnimateIn(boolean goingBack) {
        if (this.viewModel != null) {
            return this.viewModel.getAnimateIn(goingBack);
        }
        return null;
    }

    public XLEAnimationPackage getAnimateOut(boolean goingBack) {
        if (this.viewModel != null) {
            return this.viewModel.getAnimateOut(goingBack);
        }
        return null;
    }

    protected String getChannelName() {
        return ActivityBase.detailsChannel;
    }
}
