package com.microsoft.xbox.toolkit;

import android.app.ActivityManager;
import android.app.Application;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Process;
import com.microsoft.xbox.toolkit.network.XboxLiveEnvironment.Environment;
import com.microsoft.xbox.toolkit.ui.ApplicationActivity;
import com.microsoft.xle.test.interop.TestInterop;

public abstract class XboxApplication extends Application {
    public static XLEAccelerometer Accelerometer;
    public static ActivityManager ActivityManager;
    public static Ready ApplicationReady = new Ready();
    public static AssetManager AssetManager;
    public static XboxApplication Instance;
    public static ApplicationActivity MainActivity;
    public static String PackageName;
    public static Resources Resources;
    private static int VersionCode;

    protected abstract Class getColorRClass();

    protected abstract Class getDimenRClass();

    protected abstract Class getDrawableRClass();

    protected abstract Class getIdRClass();

    protected abstract Class getLayoutRClass();

    protected abstract Class getRawRClass();

    protected abstract Class getStringRClass();

    protected abstract Class getStyleRClass();

    protected abstract Class getStyleableRClass();

    static {
        try {
            System.loadLibrary("xml2");
            System.loadLibrary("stlport_shared");
            System.loadLibrary("xbl-common");
        } catch (UnsatisfiedLinkError e) {
        }
    }

    public static int getVersionCode() {
        return TestInterop.getCurrentVersion(VersionCode);
    }

    public void onCreate() {
        super.onCreate();
        Instance = this;
        Resources = getResources();
        AssetManager = getAssets();
        ActivityManager = (ActivityManager) getSystemService("activity");
        PackageName = getPackageName();
        Accelerometer = new XLEAccelerometer(Instance);
        try {
            VersionCode = getPackageManager().getPackageInfo(PackageName, 0).versionCode;
        } catch (Exception e) {
            XLELog.Error("Application", "failed to get version code. default to 0");
        }
        ThreadManager.UIThread = Thread.currentThread();
        ThreadManager.Handler = new Handler();
        ThreadManager.UIThread.setUncaughtExceptionHandler(XLEUnhandledExceptionHandler.Instance);
        XLELog.Diagnostic("Application", "package name is " + PackageName);
        XLELog.Diagnostic("Application", "Veresion code is " + Integer.toString(VersionCode));
        appInitializationCode();
        ApplicationReady.setReady();
    }

    public int getStringRValue(String name) {
        try {
            return getStringRClass().getDeclaredField(name).getInt(null);
        } catch (Exception e) {
            XLEAssert.assertTrue("Can't find " + name, false);
            return -1;
        }
    }

    public int getDrawableRValue(String name) {
        try {
            return getDrawableRClass().getDeclaredField(name).getInt(null);
        } catch (Exception e) {
            XLEAssert.assertTrue("Can't find " + name, false);
            return -1;
        }
    }

    public int getRawRValue(String name) {
        try {
            return getRawRClass().getDeclaredField(name).getInt(null);
        } catch (Exception e) {
            XLEAssert.assertTrue("Can't find " + name, false);
            return -1;
        }
    }

    public int getIdRValue(String name) {
        try {
            return getIdRClass().getDeclaredField(name).getInt(null);
        } catch (Exception e) {
            XLEAssert.assertTrue("Can't find " + name, false);
            return -1;
        }
    }

    public int getStyleRValue(String name) {
        try {
            return getStyleRClass().getDeclaredField(name).getInt(null);
        } catch (Exception e) {
            XLEAssert.assertTrue("Can't find " + name, false);
            return -1;
        }
    }

    public int[] getStyleableRValueArray(String name) {
        try {
            return (int[]) getStyleableRClass().getDeclaredField(name).get(null);
        } catch (Exception e) {
            XLEAssert.assertTrue("Can't find " + name, false);
            return null;
        }
    }

    public int getStyleableRValue(String name) {
        try {
            return getStyleableRClass().getDeclaredField(name).getInt(null);
        } catch (Exception e) {
            XLEAssert.assertTrue("Can't find " + name, false);
            return -1;
        }
    }

    public int getLayoutRValue(String name) {
        try {
            return getLayoutRClass().getDeclaredField(name).getInt(null);
        } catch (Exception e) {
            XLEAssert.assertTrue("Can't find " + name, false);
            return -1;
        }
    }

    public int getDimenRValue(String name) {
        try {
            return getDimenRClass().getDeclaredField(name).getInt(null);
        } catch (Exception e) {
            XLEAssert.assertTrue("Can't find " + name, false);
            return -1;
        }
    }

    public int getColorRValue(String name) {
        try {
            return getColorRClass().getDeclaredField(name).getInt(null);
        } catch (Exception e) {
            XLEAssert.assertTrue("Can't find " + name, false);
            return -1;
        }
    }

    public void killApp(boolean killSafely) {
        XLELog.Warning("XboxApplication", "killing the application");
        if (killSafely) {
            System.runFinalizersOnExit(true);
            System.exit(0);
            return;
        }
        Process.killProcess(Process.myPid());
    }

    public void trackError(String errorMessage) {
    }

    public void setEnvironment(Environment environment) {
    }

    public void appInitializationCode() {
    }

    public boolean supportsButtonSounds() {
        return false;
    }

    public boolean getIsTablet() {
        return false;
    }

    public boolean isAspectRatioLong() {
        return false;
    }
}
