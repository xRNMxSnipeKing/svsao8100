package com.microsoft.xbox.toolkit;

import android.os.Debug;
import android.os.Debug.MemoryInfo;

public class MemoryMonitor {
    public static final int KB_TO_BYTES = 1024;
    public static final int MB_TO_BYTES = 1048576;
    public static final int MB_TO_KB = 1024;
    private static MemoryMonitor instance = new MemoryMonitor();
    private MemoryInfo memoryInfo = new MemoryInfo();

    public static MemoryMonitor instance() {
        return instance;
    }

    public synchronized int getDalvikFreeMb() {
        return getDalvikFreeKb() / 1024;
    }

    public synchronized int getDalvikFreeKb() {
        Debug.getMemoryInfo(this.memoryInfo);
        return (XboxApplication.ActivityManager.getMemoryClass() * 1024) - getDalvikUsedKb();
    }

    public synchronized int getDalvikUsedKb() {
        Debug.getMemoryInfo(this.memoryInfo);
        return this.memoryInfo.dalvikPss;
    }

    public synchronized int getUsedKb() {
        Debug.getMemoryInfo(this.memoryInfo);
        return this.memoryInfo.dalvikPss + this.memoryInfo.nativePss;
    }

    public int getMemoryClass() {
        return XboxApplication.ActivityManager.getLargeMemoryClass();
    }

    private MemoryMonitor() {
    }
}
