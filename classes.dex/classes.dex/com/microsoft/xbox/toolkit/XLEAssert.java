package com.microsoft.xbox.toolkit;

public class XLEAssert {
    public static void assertTrue(String message, boolean condition) {
        if (!condition) {
            XboxApplication.Instance.trackError(getCallerLocation());
        }
    }

    public static void assertIsUIThread() {
        assertTrue(Thread.currentThread() == ThreadManager.UIThread);
    }

    public static void assertIsNotUIThread() {
        assertTrue(Thread.currentThread() != ThreadManager.UIThread);
    }

    public static void assertTrue(boolean condition) {
        assertTrue(null, condition);
    }

    public static void assertNotNull(Object object) {
        assertTrue(null, object != null);
    }

    public static void assertNotNull(String message, Object object) {
        assertTrue(message, object != null);
    }

    public static void assertFalse(String message, boolean condition) {
        assertTrue(message, !condition);
    }

    private static String getCallerLocation() {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        int depth = 0;
        while (depth < elements.length && (!elements[depth].getClassName().equals(XLEAssert.class.getName()) || !elements[depth].getMethodName().equals("getCallerLocation"))) {
            depth++;
        }
        while (depth < elements.length && elements[depth].getClassName().equals(XLEAssert.class.getName())) {
            depth++;
        }
        if (depth < elements.length) {
            return elements[depth].toString();
        }
        return "unknown";
    }
}
