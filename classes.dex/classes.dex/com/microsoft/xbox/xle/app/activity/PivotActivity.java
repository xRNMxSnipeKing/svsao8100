package com.microsoft.xbox.xle.app.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.anim.XLEAnimationPackage;
import com.microsoft.xbox.toolkit.ui.ScreenLayout;
import com.microsoft.xbox.toolkit.ui.pivot.Pivot;
import com.microsoft.xbox.xle.app.ApplicationBarManager;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.viewmodel.XLEGlobalData;

public class PivotActivity extends ActivityBase {
    protected Pivot pivot;

    public ScreenLayout getCurrentPivotPane() {
        return this.pivot.getCurrentPivotPane();
    }

    public void onStart() {
        super.onStart();
        if (this.pivot != null) {
            this.pivot.onStart();
            this.pivot.setOnCurrentPivotPaneChangedRunnable(new Runnable() {
                public void run() {
                    ApplicationBarManager.getInstance().setCurrentPage(PivotActivity.this.pivot.getCurrentPivotPaneIndex());
                }
            });
        }
    }

    public void onStop() {
        super.onStop();
        if (this.pivot != null) {
            this.pivot.onStop();
            this.pivot.setOnCurrentPivotPaneChangedRunnable(null);
        }
    }

    public void onPause() {
        super.onPause();
        if (this.pivot != null) {
            this.pivot.onPause();
            ApplicationBarManager.getInstance().setTotalPageCount(0);
        }
    }

    public void onResume() {
        super.onResume();
        if (this.pivot != null) {
            this.pivot.onResume();
            ApplicationBarManager.getInstance().setTotalPageCount(this.pivot.getTotalPaneCount());
        }
    }

    public void onSetActive() {
        super.onSetActive();
        if (this.pivot != null) {
            Class<? extends ScreenLayout> screenClass = XLEGlobalData.getInstance().getAndResetActivePivotPaneClass(getClass());
            if (screenClass != null) {
                this.pivot.onSetActive(getIndexOfScreen(screenClass));
                return;
            }
            this.pivot.onSetActive();
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (this.pivot != null) {
            this.pivot.onDestroy();
        }
    }

    public void onTombstone() {
        if (this.viewModel != null) {
            this.viewModel.onTombstone();
        }
        if (this.pivot != null) {
            this.pivot.onTombstone();
        }
        this.isTombstoned = true;
    }

    public void onRehydrate() {
        super.onRehydrate();
        if (this.pivot != null) {
            XLELog.Diagnostic("PivotActivity", "onRehydrate called");
            this.pivot.onRehydrate();
        }
    }

    public void onCreateContentView() {
    }

    public void onConfigurationChanged(Configuration newConfig) {
    }

    public XLEAnimationPackage getAnimateIn(boolean goingBack) {
        if (this.pivot != null) {
            return this.pivot.getAnimateIn(goingBack);
        }
        return null;
    }

    public XLEAnimationPackage getAnimateOut(boolean goingBack) {
        if (this.pivot != null) {
            return this.pivot.getAnimateOut(goingBack);
        }
        return null;
    }

    public void onAnimateInStarted() {
        if (this.pivot != null) {
            this.pivot.onAnimateInStarted();
        }
    }

    public void onAnimateInCompleted() {
        super.onAnimateInCompleted();
        if (this.pivot != null) {
            this.pivot.onAnimateInCompleted();
        }
    }

    public void forceUpdateViewImmediately() {
        XLEAssert.assertTrue(false);
    }

    public void onSaveInstanceState(Bundle outState) {
        if (outState != null && this.pivot != null) {
            XLEGlobalData.getInstance().setActivePivotPane(getClass(), this.pivot.getCurrentPivotPane().getClass());
            XLELog.Diagnostic("PivotActivity", "Saving current pivot pane screen: " + this.pivot.getCurrentPivotPane().getClass().getSimpleName());
        }
    }

    public int getIndexOfScreen(Class<? extends ScreenLayout> screenClass) {
        return this.pivot.getIndexOfScreen(screenClass);
    }

    protected String getActivityName() {
        return "DefaultPivotActivity";
    }

    protected String getChannelName() {
        return "DefaultPivotChannel";
    }

    public void adjustBottomMargin(int bottomMargin) {
        bottomMargin += XLEApplication.Resources.getDimensionPixelSize(R.dimen.applicationBarPageIndicatorMarginBottom);
        if (this.pivot != null) {
            this.pivot.adjustBottomMargin(bottomMargin);
        }
    }

    public void removeBottomMargin() {
        if (this.pivot != null) {
            this.pivot.adjustBottomMargin(0);
        }
    }

    public void resetBottomMargin() {
        if (this.pivot != null) {
            this.pivot.resetBottomMargin();
        }
    }

    public void setActivePivotPane(Class<? extends ActivityBase> screen) {
        int currentIndex = this.pivot.getCurrentPivotPaneIndex();
        int newIndex = getIndexOfScreen(screen);
        if (currentIndex != newIndex && currentIndex >= 0 && newIndex >= 0) {
            this.pivot.switchToPivotPane(newIndex);
        }
    }
}
