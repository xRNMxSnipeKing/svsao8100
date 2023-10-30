package com.microsoft.xbox.toolkit;

public class XLEThread extends Thread {
    public XLEThread(Runnable runnable, String name) {
        super(runnable, name);
        setUncaughtExceptionHandler(XLEUnhandledExceptionHandler.Instance);
    }
}
