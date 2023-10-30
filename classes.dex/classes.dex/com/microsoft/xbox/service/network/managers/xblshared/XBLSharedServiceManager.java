package com.microsoft.xbox.service.network.managers.xblshared;

import android.os.SystemClock;
import com.microsoft.xbox.toolkit.ApplicationSettingManager;
import com.microsoft.xbox.toolkit.Ready;
import com.microsoft.xbox.toolkit.StreamUtil;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.toolkit.network.XLEThreadPool;
import com.microsoft.xbox.toolkit.network.XboxLiveEnvironment.Environment;
import java.io.IOException;
import java.io.InputStream;

public class XBLSharedServiceManager {
    private static boolean boolRv;

    public static native boolean nativeDoVeyronExclusiveModeStressTest();

    public static native boolean nativeDoVeyronJoinSessionStressTest();

    public static native boolean nativeDoVeyronPasswordSetTest();

    public static native boolean nativeDoVeyronSelectionDoesntWorkTest();

    public static native boolean nativeDoVeyronSmallChunkSetTextTest();

    public static native boolean nativeDoXBLSharedUnitTestsPass();

    public static native int nativeGetGlobalEnvironment();

    public static native void nativeInitializeSingletons();

    public static native void nativeInitializeXML(String str);

    public static native void nativeSetGlobalEnvironment(int i);

    public static native void nativeSetServiceSetting(String str, int i);

    public static native void nativeSetTestSettings(boolean z);

    public static void initialize() {
        try {
            final String settingsString = StreamUtil.ReadAsString(XboxApplication.AssetManager.open("XBLShared/XboxLiveSettings.xml"));
            final Ready done = new Ready();
            XLEThreadPool.nativeOperationsThreadPool.run(new Runnable() {
                public void run() {
                    XBLSharedServiceManager.nativeInitializeSingletons();
                    XBLSharedServiceManager.nativeInitializeXML(settingsString);
                    done.setReady();
                }
            });
            done.waitForReady();
        } catch (IOException e) {
            XLEAssert.assertTrue("Couldn't load the XboxLiveSettings for XBLShared", false);
        }
    }

    public static void initializeSingletons() {
        final Ready done = new Ready();
        XLEThreadPool.nativeOperationsThreadPool.run(new Runnable() {
            public void run() {
                XBLSharedServiceManager.nativeInitializeSingletons();
                done.setReady();
            }
        });
        done.waitForReady();
    }

    public static byte[] getResourceAsByteArray(String pathOfResource, String resourceType) {
        XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
        String filename = "XBLShared/" + pathOfResource + "." + resourceType;
        XLELog.Diagnostic("XBLSharedServiceManager", "getResourceAsString: " + filename);
        try {
            XLELog.Diagnostic("XBLSharedServiceManager", "opening stream");
            InputStream settingsStream = XboxApplication.AssetManager.open(filename);
            XLELog.Diagnostic("XBLSharedServiceManager", "opening stream complete");
            byte[] rv = StreamUtil.CreateByteArray(settingsStream);
            XLELog.Diagnostic("XBLSharedServiceManager", "opening stream read");
            XLELog.Diagnostic("XBLSharedServiceManager", "stream is: " + rv);
            return rv;
        } catch (IOException e) {
            XLELog.Diagnostic("XBLSharedServiceManager", "getResourceAsString finished, returning");
            return null;
        }
    }

    public static String getDeviceId() {
        String guid = ApplicationSettingManager.getInstance().getGUID();
        XLEAssert.assertNotNull(guid);
        return guid;
    }

    public static int getPlatformType() {
        if (XboxApplication.Instance.getIsTablet()) {
            return PlatformType.PlatformType_AndroidSlate.getValue();
        }
        return PlatformType.PlatformType_AndroidPhone.getValue();
    }

    public static void setEnvironment(final Environment environment) {
        final Ready done = new Ready();
        XLEThreadPool.nativeOperationsThreadPool.run(new Runnable() {
            public void run() {
                XBLSharedServiceManager.nativeSetGlobalEnvironment(environment.ordinal());
                done.setReady();
            }
        });
        done.waitForReady();
    }

    public static boolean doXBLSharedUnitTestsPass() {
        final Ready done = new Ready();
        XLEThreadPool.nativeOperationsThreadPool.run(new Runnable() {
            public void run() {
                XBLSharedServiceManager.boolRv = XBLSharedServiceManager.nativeDoXBLSharedUnitTestsPass();
                done.setReady();
            }
        });
        done.waitForReady();
        return boolRv;
    }

    public static boolean doVeyronJoinSessionStressTest() {
        return nativeDoVeyronJoinSessionStressTest();
    }

    public static boolean doVeyronExclusiveModeStressTest() {
        return nativeDoVeyronExclusiveModeStressTest();
    }

    public static void setTestSettings(final boolean testEDSStub) {
        final Ready done = new Ready();
        XLEThreadPool.nativeOperationsThreadPool.run(new Runnable() {
            public void run() {
                XBLSharedServiceManager.nativeSetTestSettings(testEDSStub);
                done.setReady();
            }
        });
        done.waitForReady();
    }

    public static long getUptimeMillis() {
        return SystemClock.uptimeMillis();
    }
}
