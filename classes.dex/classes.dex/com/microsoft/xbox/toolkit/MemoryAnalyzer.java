package com.microsoft.xbox.toolkit;

import android.os.Debug;
import android.os.Debug.MemoryInfo;
import java.io.File;

public class MemoryAnalyzer {
    private static final String HPROF_BASE = "/sdcard/bishop/hprof/";

    public static int getTotalMemoryUsed(MemoryInfo before, MemoryInfo after) {
        int dalvikKbUsed = after.dalvikPss - before.dalvikPss;
        return ((after.nativePss + after.dalvikPss) + after.otherPss) - ((before.nativePss + before.dalvikPss) + before.otherPss);
    }

    public static MemoryInfo getMemoryInfo() {
        MemoryInfo info = new MemoryInfo();
        Debug.getMemoryInfo(info);
        return info;
    }

    public static void dumpHPROF(String name) throws Exception {
        String filename = String.format("%s%s.hprof", new Object[]{HPROF_BASE, name});
        File dirobj = new File(HPROF_BASE);
        if (dirobj.exists() || dirobj.mkdirs()) {
            XLELog.Info("MemoryAnalyzer", "Saving..." + filename);
            Debug.dumpHprofData(filename);
            return;
        }
        throw new RuntimeException("couldn't build directory");
    }
}
