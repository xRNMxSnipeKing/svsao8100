package com.microsoft.xbox.toolkit.ui.pivot;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.toolkit.ui.ScreenLayout;
import java.util.ArrayList;
import java.util.Iterator;

public class PivotBody extends FrameLayout {
    private int activepane = 0;
    private LinearLayout linearLayout = null;
    private ArrayList<ScreenLayout> panes = null;

    public PivotBody(Context context) {
        super(context);
    }

    public PivotBody(Context context, AttributeSet attrs) {
        super(context, attrs);
        throw new UnsupportedOperationException();
    }

    public void initialize(ScreenLayout[] panes) {
        this.panes = new ArrayList();
        this.linearLayout = new LinearLayout(getContext());
        this.linearLayout.setOrientation(0);
        if (panes != null) {
            for (int i = 0; i < panes.length; i++) {
                addPivotPane(panes[i], i);
            }
        }
        addView(this.linearLayout, size() * XboxApplication.MainActivity.getScreenWidth(), -1);
        setScrollX(0);
    }

    public void addPivotPane(ScreenLayout pane, int index) {
        pane.setDrawingCacheEnabled(false);
        pane.setVisibility(0);
        pane.setIsPivotPane(true);
        this.panes.add(index, pane);
        this.linearLayout.addView(pane, index, new LayoutParams(XboxApplication.MainActivity.getScreenWidth(), -1));
        this.linearLayout.setLayoutParams(new LayoutParams(size() * XboxApplication.MainActivity.getScreenWidth(), -1));
    }

    public ScreenLayout removePivotPane(int index) {
        if (index < 0 || index >= this.panes.size()) {
            return null;
        }
        ScreenLayout removedScreen = (ScreenLayout) this.panes.get(index);
        this.linearLayout.removeView(removedScreen);
        this.panes.remove(removedScreen);
        return removedScreen;
    }

    public void setScrollX(int x) {
        scrollTo(x, 0);
    }

    public int size() {
        return this.panes.size();
    }

    public void onCreate() {
        Iterator i$ = this.panes.iterator();
        while (i$.hasNext()) {
            ((ScreenLayout) i$.next()).onCreate();
        }
    }

    public void onStart() {
        Iterator i$ = this.panes.iterator();
        while (i$.hasNext()) {
            ((ScreenLayout) i$.next()).onStart();
        }
    }

    public void onStop() {
        Iterator i$ = this.panes.iterator();
        while (i$.hasNext()) {
            ((ScreenLayout) i$.next()).onStop();
        }
    }

    protected void onPause() {
        Iterator i$ = this.panes.iterator();
        while (i$.hasNext()) {
            ((ScreenLayout) i$.next()).onPause();
        }
    }

    protected void onResume() {
        Iterator i$ = this.panes.iterator();
        while (i$.hasNext()) {
            ((ScreenLayout) i$.next()).onResume();
        }
    }

    public void onAnimateInStarted() {
        ((ScreenLayout) this.panes.get(this.activepane)).forceUpdateViewImmediately();
    }

    public void onAnimateInCompleted() {
        Iterator i$ = this.panes.iterator();
        while (i$.hasNext()) {
            ((ScreenLayout) i$.next()).forceUpdateViewImmediately();
        }
    }

    protected void onDestroy() {
        Iterator i$ = this.panes.iterator();
        while (i$.hasNext()) {
            ((ScreenLayout) i$.next()).onDestroy();
        }
    }

    protected void onTombstone() {
        Iterator i$ = this.panes.iterator();
        while (i$.hasNext()) {
            ((ScreenLayout) i$.next()).onTombstone();
        }
    }

    protected void onRehydrate() {
        Iterator i$ = this.panes.iterator();
        while (i$.hasNext()) {
            ((ScreenLayout) i$.next()).onRehydrate();
        }
    }

    public ScreenLayout getPivotPane(int index) {
        if (index < this.panes.size()) {
            return (ScreenLayout) this.panes.get(index);
        }
        return null;
    }

    public void setActivePivotPane(int activePivotPaneIndex) {
        this.activepane = activePivotPaneIndex;
        for (int i = 0; i < this.panes.size(); i++) {
            if (i != activePivotPaneIndex) {
                ((ScreenLayout) this.panes.get(i)).onSetInactive();
            }
        }
        ((ScreenLayout) this.panes.get(activePivotPaneIndex)).onSetActive();
    }

    public void setInactive() {
        Iterator i$ = this.panes.iterator();
        while (i$.hasNext()) {
            ((ScreenLayout) i$.next()).onSetInactive();
        }
    }

    public void adjustBottomMargin(int bottomMargin) {
        Iterator i$ = this.panes.iterator();
        while (i$.hasNext()) {
            ((ScreenLayout) i$.next()).adjustBottomMargin(bottomMargin);
        }
    }

    public void resetBottomMargin() {
        Iterator i$ = this.panes.iterator();
        while (i$.hasNext()) {
            ((ScreenLayout) i$.next()).resetBottomMargin();
        }
    }

    public int getIndexOfScreen(Class<? extends ScreenLayout> screenClass) {
        for (int i = 0; i < this.panes.size(); i++) {
            if (((ScreenLayout) this.panes.get(i)).getClass().equals(screenClass)) {
                return i;
            }
        }
        XLELog.Error("PivotBody", "can't find index for screen class " + screenClass.getName());
        return -1;
    }
}
