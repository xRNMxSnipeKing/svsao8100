package com.microsoft.xbox.xle.viewmodel;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import com.microsoft.xbox.toolkit.DialogManager;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAllocationTracker;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.toolkit.anim.XLEAnimation;
import com.microsoft.xbox.toolkit.ui.NavigationManager;
import com.microsoft.xbox.toolkit.ui.appbar.AppBarMenuButton;
import com.microsoft.xbox.xle.app.ApplicationBarManager;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.app.module.ScreenModuleLayout;
import com.microsoft.xle.test.interop.TestInterop;
import com.microsoft.xle.test.interop.delegates.Action;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public abstract class AdapterBase {
    public static String ALLOCATION_TAG = "ADAPTERBASE";
    private static HashMap<String, Integer> adapterCounter = new HashMap();
    protected boolean isActive = false;
    private boolean isStarted = false;
    private ArrayList<ScreenModuleLayout> screenModules = new ArrayList();

    protected abstract void updateViewOverride();

    protected boolean getIsStarted() {
        return this.isStarted;
    }

    public AdapterBase() {
        XLEAllocationTracker.getInstance().debugIncrement(ALLOCATION_TAG, getClass().getSimpleName());
        XLEAllocationTracker.getInstance().debugPrintOverallocated(ALLOCATION_TAG);
    }

    public void finalize() {
        XLEAllocationTracker.getInstance().debugDecrement(ALLOCATION_TAG, getClass().getSimpleName());
        XLEAllocationTracker.getInstance().debugPrintOverallocated(ALLOCATION_TAG);
    }

    public void updateView() {
        if (!NavigationManager.getInstance().isAnimating()) {
            updateViewOverride();
            Iterator i$ = this.screenModules.iterator();
            while (i$.hasNext()) {
                ((ScreenModuleLayout) i$.next()).updateView();
            }
        }
    }

    public void forceUpdateViewImmediately() {
        XLEAssert.assertIsUIThread();
        updateViewOverride();
        Iterator i$ = this.screenModules.iterator();
        while (i$.hasNext()) {
            ((ScreenModuleLayout) i$.next()).updateView();
        }
    }

    public ArrayList<XLEAnimation> getAnimateIn(boolean goingBack) {
        return null;
    }

    public ArrayList<XLEAnimation> getAnimateOut(boolean goingBack) {
        return null;
    }

    public void onPause() {
        Iterator i$ = this.screenModules.iterator();
        while (i$.hasNext()) {
            ((ScreenModuleLayout) i$.next()).onPause();
        }
    }

    public void onApplicationPause() {
        Iterator i$ = this.screenModules.iterator();
        while (i$.hasNext()) {
            ((ScreenModuleLayout) i$.next()).onApplicationPause();
        }
    }

    public void onApplicationResume() {
        Iterator i$ = this.screenModules.iterator();
        while (i$.hasNext()) {
            ((ScreenModuleLayout) i$.next()).onApplicationResume();
        }
    }

    public void onResume() {
        Iterator i$ = this.screenModules.iterator();
        while (i$.hasNext()) {
            ((ScreenModuleLayout) i$.next()).onResume();
        }
    }

    public void onDestroy() {
        Iterator i$ = this.screenModules.iterator();
        while (i$.hasNext()) {
            ((ScreenModuleLayout) i$.next()).onDestroy();
        }
        this.screenModules.clear();
    }

    public void onStart() {
        this.isStarted = true;
        dismissKeyboard();
        Iterator i$ = this.screenModules.iterator();
        while (i$.hasNext()) {
            ((ScreenModuleLayout) i$.next()).onStart();
        }
    }

    public void onStop() {
        this.isStarted = false;
        dismissKeyboard();
        Iterator i$ = this.screenModules.iterator();
        while (i$.hasNext()) {
            ((ScreenModuleLayout) i$.next()).onStop();
        }
    }

    protected void onAppBarUpdated() {
    }

    protected void onAppBarButtonsAdded() {
    }

    protected void dismissKeyboard() {
        if (XboxApplication.MainActivity != null) {
            XboxApplication.MainActivity.hideKeyboard();
        }
    }

    protected void showKeyboard(final View view) {
        ThreadManager.UIThreadPost(new Runnable() {
            public void run() {
                ((InputMethodManager) XboxApplication.Instance.getSystemService("input_method")).showSoftInput(view, 1);
                TestInterop.setDismissSoftKeyboard(new Action() {
                    public void invoke() {
                        AdapterBase.this.dismissKeyboard();
                    }
                });
            }
        });
    }

    public void onSetActive() {
        this.isActive = true;
        if (XLEApplication.getMainActivity() != null) {
            ApplicationBarManager.getInstance().setOnAnimationEndRunnable(new Runnable() {
                public void run() {
                    AdapterBase.this.onAppBarUpdated();
                    ThreadManager.UIThreadPost(new Runnable() {
                        public void run() {
                            if (AdapterBase.this.isStarted) {
                                AdapterBase.this.updateView();
                            }
                        }
                    });
                }
            });
            ApplicationBarManager.getInstance().setOnNewButtonsAddedRunnable(new Runnable() {
                public void run() {
                    AdapterBase.this.onAppBarButtonsAdded();
                    ThreadManager.UIThreadPost(new Runnable() {
                        public void run() {
                            if (AdapterBase.this.isStarted) {
                                AdapterBase.this.updateView();
                            }
                        }
                    });
                }
            });
        }
    }

    public void onSetInactive() {
        this.isActive = false;
        if (XLEApplication.getMainActivity() != null) {
            ApplicationBarManager.getInstance().setOnAnimationEndRunnable(null);
            ApplicationBarManager.getInstance().setOnNewButtonsAddedRunnable(null);
        }
    }

    protected List<AppBarMenuButton> getTestMenuButtons() {
        return null;
    }

    public View findViewById(int id) {
        return XLEApplication.getMainActivity().findViewById(id);
    }

    protected void findAndInitializeModuleById(int id, ViewModelBase vm) {
        View view = findViewById(id);
        if (view != null && (view instanceof ScreenModuleLayout)) {
            ScreenModuleLayout module = (ScreenModuleLayout) findViewById(id);
            module.setViewModel(vm);
            this.screenModules.add(module);
        }
    }

    protected void setBlocking(boolean visible, String blockingText) {
        DialogManager.getInstance().setBlocking(visible, blockingText);
    }

    protected void setCancelableBlocking(boolean visible, String blockingText, Runnable cancelRunnable) {
        DialogManager.getInstance().setCancelableBlocking(visible, blockingText, cancelRunnable);
    }

    protected void setAppBarButtonEnabled(int resId, boolean isEnabled) {
        ApplicationBarManager.getInstance().setButtonEnabled(resId, isEnabled);
    }

    protected void setAppBarButtonClickListener(int resId, OnClickListener listener) {
        ApplicationBarManager.getInstance().setButtonClickListener(resId, listener);
    }

    protected void setAppBarButtonVisibility(int resId, int visibility) {
        ApplicationBarManager.getInstance().setButtonVisibility(resId, visibility);
    }

    protected void setAppBarMediaButtonVisibility(boolean visible) {
        ApplicationBarManager.getInstance().setMediaButtonVisibility(visible);
    }
}
