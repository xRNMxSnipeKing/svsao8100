package com.microsoft.xle.test.interop;

import android.view.View;
import com.microsoft.xle.test.interop.delegates.Action1;

public class CrashReporter {
    private static Action1<Throwable> handleThrowableMethod = null;
    private static Action1<View> takeSnapshotMethod = null;

    public static void setThrowableHandler(Action1<Throwable> action) {
        handleThrowableMethod = action;
    }

    public static void handleThrowable(Throwable ex) {
    }

    public static void setTakeScreenshot(Action1<View> action) {
        takeSnapshotMethod = action;
    }

    public static void takeScreenshot() {
    }
}
