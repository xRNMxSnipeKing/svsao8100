package com.microsoft.xbox.toolkit.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.TimeSample;
import com.microsoft.xbox.toolkit.TimeTool;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.toolkit.anim.XLEAnimationPackage;
import com.microsoft.xle.test.interop.TestInterop;
import java.util.ArrayList;
import java.util.Iterator;

public abstract class ScreenLayout extends FrameLayout {
    private static ArrayList<View> badList = new ArrayList();
    private boolean isActive;
    private boolean isEditable;
    private boolean isReady;
    private boolean isStarted;
    protected boolean isTombstoned;
    private Runnable onLayoutChangedListener;
    private int orientation;
    private boolean usesAccelerometer;

    public abstract void forceUpdateViewImmediately();

    public abstract void onAnimateInCompleted();

    public abstract void onAnimateInStarted();

    public abstract void onRehydrateOverride();

    public abstract void setIsPivotPane(boolean z);

    public ScreenLayout() {
        this(XboxApplication.MainActivity);
    }

    public ScreenLayout(Context context) {
        this(context, 7);
    }

    public ScreenLayout(Context context, int orientation) {
        this(context, orientation, false);
    }

    public ScreenLayout(Context context, int orientation, boolean usesAccelerometer) {
        super(context);
        this.onLayoutChangedListener = null;
        this.isEditable = false;
        this.isReady = false;
        this.isActive = false;
        this.isStarted = false;
        this.orientation = orientation;
        this.usesAccelerometer = usesAccelerometer;
    }

    protected void setContentView(int screenLayoutId) {
        ((LayoutInflater) XboxApplication.Instance.getSystemService("layout_inflater")).inflate(screenLayoutId, this, true);
    }

    public void onRestart() {
    }

    public void onCreate() {
    }

    public void onStart() {
        if (getRequestedOrientation() != this.orientation) {
            setRequestedOrientation(this.orientation);
        }
        this.isStarted = true;
    }

    public void onResume() {
        this.isReady = true;
        if (this.usesAccelerometer) {
            XboxApplication.Accelerometer.onResume();
        }
    }

    public void onApplicationResume() {
    }

    public void onApplicationPause() {
    }

    public void onPause() {
        this.isReady = false;
        if (this.usesAccelerometer) {
            XboxApplication.Accelerometer.onPause();
        }
    }

    public void onStop() {
        this.isStarted = false;
    }

    public void onDestroy() {
        removeAllViewsAndWorkaroundAndroidLeaks();
    }

    public void onTombstone() {
        this.isTombstoned = true;
        XLELog.Diagnostic("ScreenLayout", "onTombstone removing all views");
        removeAllViewsAndWorkaroundAndroidLeaks();
    }

    public void onRehydrate() {
        this.isTombstoned = false;
        onRehydrateOverride();
    }

    public String getLocalClassName() {
        return getClass().getName();
    }

    public void setRequestedOrientation(int requestedOrientation) {
        XboxApplication.MainActivity.setRequestedOrientation(requestedOrientation);
    }

    public int getRequestedOrientation() {
        return XboxApplication.MainActivity.getRequestedOrientation();
    }

    public void onBackButtonPressed() {
    }

    protected void onSaveInstanceState(Bundle outState) {
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    }

    public boolean getIsTombstoned() {
        return this.isTombstoned;
    }

    public boolean getIsReady() {
        return this.isReady;
    }

    public XLEAnimationPackage getAnimateOut(boolean goingBack) {
        return null;
    }

    public XLEAnimationPackage getAnimateIn(boolean goingBack) {
        return null;
    }

    public boolean getIsActive() {
        return this.isActive;
    }

    public boolean getIsStarted() {
        return this.isStarted;
    }

    public void onSetActive() {
        this.isActive = true;
    }

    public void onSetInactive() {
        this.isActive = false;
    }

    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        TimeSample sample = null;
        if (TestInterop.getMonitorLPS()) {
            sample = TimeTool.getInstance().start();
        }
        super.onLayout(changed, left, top, right, bottom);
        if (this.onLayoutChangedListener != null) {
            this.onLayoutChangedListener.run();
        }
        if (TestInterop.getMonitorLPS() && sample != null) {
            sample.setFinished();
        }
    }

    public void setOnLayoutChangedListener(Runnable r) {
        this.onLayoutChangedListener = r;
    }

    public boolean onContextItemSelected(MenuItem item) {
        return false;
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    }

    public void adjustBottomMargin(int bottomMargin) {
    }

    public void resetBottomMargin() {
    }

    public void removeBottomMargin() {
    }

    public boolean getIsEditable() {
        return this.isEditable;
    }

    public boolean getCanAutoLaunch() {
        return !this.isEditable;
    }

    public boolean getShouldShowAppbar() {
        return !this.isEditable;
    }

    public void setIsEditable(boolean isEditable) {
        this.isEditable = isEditable;
    }

    public static void addViewThatCausesAndroidLeaks(View v) {
        badList.add(v);
    }

    private void removeAllViewsAndWorkaroundAndroidLeaks() {
        boolean z;
        if (Thread.currentThread() == ThreadManager.UIThread) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        removeAllViews();
        XLELog.Error("ScreenLayout", String.format("removing %d problematic views", new Object[]{Integer.valueOf(badList.size())}));
        Iterator i$ = badList.iterator();
        while (i$.hasNext()) {
            removeViewAndWorkaroundAndroidLeaks((View) i$.next());
        }
        badList.clear();
    }

    public static void removeViewAndWorkaroundAndroidLeaks(View v) {
        boolean z = true;
        if (v != null) {
            ViewParent viewparent = v.getParent();
            if (viewparent instanceof ViewGroup) {
                boolean z2;
                ((ViewGroup) viewparent).removeAllViews();
                if (v.getParent() == null) {
                    z2 = true;
                } else {
                    z2 = false;
                }
                XLEAssert.assertTrue(z2);
            }
            if (v instanceof ViewGroup) {
                ViewGroup view = (ViewGroup) v;
                view.removeAllViews();
                view.destroyDrawingCache();
                if (view.getChildCount() != 0) {
                    z = false;
                }
                XLEAssert.assertTrue(z);
            }
        }
    }
}
