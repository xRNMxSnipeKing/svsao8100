package com.microsoft.xle.test.interop;

import android.webkit.WebView;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.ui.ScreenLayout;
import com.microsoft.xbox.toolkit.ui.appbar.ExpandedAppBar;
import com.microsoft.xle.test.interop.delegates.Action;
import com.microsoft.xle.test.interop.delegates.Action2;
import java.util.ArrayList;
import java.util.HashMap;

public class TestInterop {
    private static TestInteropState activityReady = TestInteropState.DISABLED;
    private static Object activityReadyMonitor = new Object();
    private static HashMap<Class<? extends ScreenLayout>, ArrayList<Integer>> allPivotPageStates = new HashMap();
    private static boolean allowDismissSoftKeyboard = false;
    private static Action2<WebView, Integer> automaticSignInDelegate = null;
    private static int automaticSigninUserIndex = 0;
    private static String clientId;
    private static int currentVer = -1;
    private static Action dismissSoftKeyboardCallback = null;
    private static boolean errorOnStart = false;
    private static boolean isChild = false;
    private static boolean isGold = false;
    private static int latestVer = -1;
    private static String loginReturnUrl;
    private static String loginStateError;
    private static int minVer = -1;
    private static boolean monitorLPS = false;
    private static Action onGetMediaStateAction = null;
    private static Action2<String, XLEException> onLoginStateChangedCallback = null;
    private static Action2<String, ServiceManagerActivityStateChange> onServiceManagerActivityCallback = null;
    private static boolean overrideChildSetting = false;
    private static boolean overrideLoginState = false;
    private static boolean overrideMembershipLevel = false;
    private static String pageToApplyError = null;
    private static boolean signinAutomatically = false;

    public enum ServiceManagerActivityStateChange {
        Started,
        Completed,
        Error
    }

    enum TestInteropState {
        DISABLED,
        WAITING,
        READY
    }

    public static void setMonitorLPS(boolean monitor) {
    }

    public static boolean getMonitorLPS() {
        return false;
    }

    public static void setAllowDismissSoftKeyboard(boolean allow) {
    }

    public static void setDismissSoftKeyboard(Action action) {
    }

    public static Action getDismissSoftKeyboard() {
        return null;
    }

    public static void setServiceManagerNotification(Action2<String, ServiceManagerActivityStateChange> action2) {
    }

    public static void onServiceManagerActivity(String url, ServiceManagerActivityStateChange change) {
    }

    public static void setNotReady() {
        synchronized (activityReadyMonitor) {
            activityReady = TestInteropState.WAITING;
        }
    }

    public static void waitForReady() {
        synchronized (activityReadyMonitor) {
            while (activityReady == TestInteropState.WAITING) {
                try {
                    activityReadyMonitor.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    public static void setReady() {
        synchronized (activityReadyMonitor) {
            activityReady = TestInteropState.READY;
            activityReadyMonitor.notifyAll();
        }
    }

    public static void setMembershipLevel(boolean overrideMembership, boolean newIsGold) {
    }

    public static boolean getMembershipLevel(boolean currentIsGold) {
        return currentIsGold;
    }

    public static void setUserIsChild(boolean enableOverride, boolean isChildAccount) {
    }

    public static boolean getUserChildSetting(boolean current) {
        return current;
    }

    public static ExpandedAppBar getExpandedAppBar() {
        return null;
    }

    public static void setTotalPageCount(int count) {
    }

    public static void setPageState(int page, int state) {
    }

    public static int getTotalPages() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        return 0;
    }

    public static ArrayList<Integer> getPageStates() {
        return null;
    }

    public static int getMinimumVersionRequired(int currentMinimumVersion) {
        return currentMinimumVersion;
    }

    public static int getLatestVersionAvailable(int currentLatestVersion) {
        return currentLatestVersion;
    }

    public static int getCurrentVersion(int currentVersion) {
        return currentVersion;
    }

    public static void setVersion(int minVersionRequired, int latestVersionAvailable, int currentVersion) {
    }

    public static void setClientId(String newClientId) {
        clientId = newClientId;
    }

    public static String getClientId() {
        return null;
    }

    public static void setLoginReturnUrl(String newReturnUrl) {
        loginReturnUrl = newReturnUrl;
    }

    public static String getLoginReturnUrl() {
        return null;
    }

    public static void setAutomaticSignin(boolean automaticSignin, int userIndex, Action2<WebView, Integer> signInDelegate) {
        signinAutomatically = automaticSignin;
        automaticSigninUserIndex = userIndex;
        automaticSignInDelegate = signInDelegate;
    }

    public static void DoAutomaticSignin(WebView view) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
    }

    private static Boolean AutoLoginManualOverride() {
        return Boolean.valueOf(false);
    }

    public static void setOnLoginStateChangedCallback(Action2<String, XLEException> action2) {
    }

    public static void onLoginStateChanged(String loginState, XLEException exception) {
    }

    public static void setLoginError(boolean overrideState, boolean errorOnPageStart, String errorState, String page) {
    }

    public static String getLoginErrorOnPageStart(String currentPage, String currentState) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        return currentState;
    }

    public static String getLoginErrorOnPageFinish(String currentPage, String currentState) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        return currentState;
    }

    public static void setOnGetMediaStateAction(Action action) {
    }

    public static boolean onGetMediaState() {
        return false;
    }
}
