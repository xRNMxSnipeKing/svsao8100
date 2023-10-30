package com.microsoft.xbox.toolkit;

import com.microsoft.xle.test.interop.CrashReporter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Date;

public class XLEUnhandledExceptionHandler implements UncaughtExceptionHandler {
    public static XLEUnhandledExceptionHandler Instance = new XLEUnhandledExceptionHandler();
    private UncaughtExceptionHandler oldExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();

    public void uncaughtException(Thread thread, Throwable ex) {
        XLELog.Error("XLEExceptionHandler", " *************** UNHANDLED EXCEPTION ***************");
        String exceptionMessage = ex.toString();
        if (exceptionMessage != null) {
            XLELog.Error("XLEExceptionHandler", exceptionMessage);
        } else {
            XLELog.Error("XLEExceptionHandler", "NO MESSAGE");
        }
        if (ex.getCause() != null) {
            XLELog.Error("XLEExceptionHandler", "CAUSE:");
            XLELog.Error("XLEExceptionHandler", "\t" + ex.getCause().toString());
            XLELog.Error("XLEExceptionHandler", "END CAUSE\n\n");
            XLELog.Error("XLEExceptionHandler", "CAUSE STACK TRACE:");
            printStackTrace(ex.getCause());
            XLELog.Error("XLEExceptionHandler", "CAUSE END CAUSE\n\n");
        }
        XLELog.Error("XLEExceptionHandler", "MAIN THREAD STACK TRACE:");
        printStackTrace(ex);
        XLELog.Error("XLEExceptionHandler", "MAIN THREAD END STACK TRACE\n\n");
        XLELog.Error("XLEExceptionHandler", " *************** END UNHANDLED EXCEPTION ***************");
        CrashReporter.handleThrowable(ex);
        this.oldExceptionHandler.uncaughtException(thread, ex);
    }

    private void printStackTrace(Throwable ex) {
        XLELog.File(new Date().toGMTString());
        XLELog.File("********** UNHANDLED EXCEPTION *************");
        XLELog.File(ex.toString());
        for (StackTraceElement elem : ex.getStackTrace()) {
            XLELog.Error("XLEExceptionHandler", "\t" + elem.toString());
            XLELog.File(elem.toString());
        }
    }
}
