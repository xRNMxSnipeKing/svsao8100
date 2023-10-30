package com.microsoft.xbox.toolkit.ui;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import com.microsoft.xbox.toolkit.ApplicationSettingManager;
import com.microsoft.xbox.toolkit.BackgroundThreadWaitor;
import com.microsoft.xbox.toolkit.BackgroundThreadWaitor.WaitType;
import com.microsoft.xbox.toolkit.DialogManager;
import com.microsoft.xbox.toolkit.SoundManager;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.toolkit.system.SystemUtil;
import com.microsoft.xle.test.interop.TestInterop;

public class ApplicationActivity extends Activity {
    private static final int NAVIGATION_BLOCK_TIMEOUT_MS = 5000;
    protected static int SCREEN_HEIGHT = SystemUtil.getScreenHeight();
    protected static int SCREEN_WIDTH = SystemUtil.getScreenWidth();
    private boolean animationBlocking = false;
    private boolean needToRestoreState;
    protected boolean paused = false;
    protected Class startupScreenClass = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        XLELog.Diagnostic("ApplicationActivity", "onCreate called. ");
        XboxApplication.MainActivity = this;
        this.needToRestoreState = savedInstanceState == null;
        getWindow().getAttributes().format = 1;
        this.paused = false;
    }

    protected void onStart() {
        super.onStart();
        this.paused = false;
        DialogManager.getInstance().setEnabled(true);
        XLELog.Diagnostic("ApplicationActivity", "onStart called. ");
        try {
            if (NavigationManager.getInstance().getCurrentActivity() == null) {
                XLELog.Warning("ApplicationActivity", "Start a new instance because get current activity is null");
                if (onStartOverride(true)) {
                    NavigationManager.getInstance().PushScreen(getStartupScreenClass());
                }
            } else if (!onStartOverride(false)) {
            } else {
                if (this.needToRestoreState) {
                    Bundle outState = new Bundle();
                    NavigationManager.getInstance().getCurrentActivity().onSaveInstanceState(outState);
                    NavigationManager.getInstance().RestartCurrentScreen(false);
                    NavigationManager.getInstance().getCurrentActivity().onRestoreInstanceState(outState);
                    return;
                }
                NavigationManager.getInstance().RestartCurrentScreen(false);
            }
        } catch (XLEException e) {
            XLELog.Error("ApplicationActivity", "Failed to start activity: " + e.toString());
        }
    }

    protected boolean onStartOverride(boolean isNewLaunch) {
        return true;
    }

    protected void onRestart() {
        XLELog.Diagnostic("ApplicationActivity", "onRestart called. ");
        super.onRestart();
    }

    protected void onResume() {
        boolean z = true;
        DialogManager.getInstance().setEnabled(true);
        SoundManager.getInstance().setEnabled(ApplicationSettingManager.getInstance().getSoundStatus());
        XLELog.Diagnostic("ApplicationActivity", "onResume called. ");
        if (this.paused) {
            if (NavigationManager.getInstance().getCurrentActivity() == null) {
                z = false;
            }
            XLEAssert.assertTrue(z);
            NavigationManager.getInstance().getCurrentActivity().onResume();
            NavigationManager.getInstance().getCurrentActivity().onSetActive();
        }
        NavigationManager.getInstance().onApplicationResume();
        super.onResume();
    }

    protected void onPause() {
        XLELog.Diagnostic("ApplicationActivity", "onPause called. ");
        if (NavigationManager.getInstance().getCurrentActivity() != null) {
            NavigationManager.getInstance().getCurrentActivity().onSetInactive();
            NavigationManager.getInstance().getCurrentActivity().onPause();
        }
        NavigationManager.getInstance().onApplicationPause();
        DialogManager.getInstance().setEnabled(false);
        DialogManager.getInstance().forceDismissAll();
        SoundManager.getInstance().setEnabled(false);
        this.paused = true;
        super.onPause();
    }

    protected void onStop() {
        super.onStop();
        XLELog.Diagnostic("ApplicationActivity", "onStop called. ");
    }

    protected void onBeforeApplicationExit() {
    }

    public void goBack() {
        boolean shouldCloseApp = NavigationManager.getInstance().ShouldBackCloseApp();
        try {
            if (XboxApplication.Instance.supportsButtonSounds()) {
                SoundManager.getInstance().playSound(XboxApplication.Instance.getRawRValue("sndbuttonbackandroid"));
            }
            if (shouldCloseApp) {
                NavigationManager.getInstance().PopScreensAndReplace(1, null, false, true);
            } else {
                NavigationManager.getInstance().GoBack();
            }
        } catch (XLEException e) {
            XLELog.Error("ApplicationActivity", "Error attempting to goBack");
        }
        if (shouldCloseApp) {
            onBeforeApplicationExit();
            finish();
        }
    }

    public boolean isBlocking() {
        return DialogManager.getInstance().getIsBlocking() || this.animationBlocking;
    }

    public Class getStartupScreenClass() {
        if (this.startupScreenClass == null) {
            XLELog.Error("ApplicationActivity", "StartupClass is not initialized");
        }
        return this.startupScreenClass;
    }

    public void onBackPressed() {
        if (!isBlocking()) {
            NavigationManager.getInstance().OnBackButtonPressed();
        }
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        return super.onPrepareOptionsMenu(menu);
    }

    public boolean onContextItemSelected(MenuItem item) {
        if (NavigationManager.getInstance().getCurrentActivity() != null) {
            return NavigationManager.getInstance().getCurrentActivity().onContextItemSelected(item);
        }
        return super.onContextItemSelected(item);
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (NavigationManager.getInstance().getCurrentActivity() != null) {
            NavigationManager.getInstance().getCurrentActivity().onCreateContextMenu(menu, v, menuInfo);
        }
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (isBlocking()) {
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    public void setAnimationBlocking(boolean blocking) {
        if (this.animationBlocking != blocking) {
            this.animationBlocking = blocking;
            if (this.animationBlocking) {
                BackgroundThreadWaitor.getInstance().setBlocking(WaitType.Navigation, 5000);
            } else {
                BackgroundThreadWaitor.getInstance().clearBlocking(WaitType.Navigation);
            }
        }
    }

    public void hideKeyboard() {
        final ScreenLayout currentScreen = NavigationManager.getInstance().getCurrentActivity();
        if (currentScreen != null) {
            ThreadManager.UIThreadPost(new Runnable() {
                public void run() {
                    ((InputMethodManager) XboxApplication.Instance.getSystemService("input_method")).hideSoftInputFromWindow(currentScreen.getWindowToken(), 0);
                    TestInterop.setDismissSoftKeyboard(null);
                }
            });
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (NavigationManager.getInstance().getCurrentActivity() != null) {
            NavigationManager.getInstance().getCurrentActivity().onSaveInstanceState(outState);
        }
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (NavigationManager.getInstance().getCurrentActivity() != null) {
            NavigationManager.getInstance().getCurrentActivity().onRestoreInstanceState(savedInstanceState);
        }
    }

    public View findViewByString(String viewName) {
        return null;
    }

    public int findDimensionIdByName(String name) {
        return -1;
    }

    public void onBeforeNavigatingIn() {
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        XLELog.Diagnostic("ApplicationActivity XLE", "configuration changed");
        SCREEN_WIDTH = SystemUtil.getScreenWidth();
        SCREEN_HEIGHT = SystemUtil.getScreenHeight();
    }

    public int getScreenWidth() {
        return SCREEN_WIDTH;
    }

    public int getScreenHeight() {
        return SCREEN_HEIGHT;
    }
}
