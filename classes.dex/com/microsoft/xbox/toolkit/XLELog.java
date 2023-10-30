package com.microsoft.xbox.toolkit;

import android.os.Environment;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class XLELog {
    private static File logFile = new File(Environment.getExternalStorageDirectory() + "/myxboxlive.log");

    public static void Diagnostic(String tag, String text) {
    }

    public static void Info(String tag, String text) {
    }

    public static void Warning(String tag, String text) {
    }

    public static void Error(String tag, String text) {
    }

    public static void File(String text) {
    }

    private static synchronized void appendLog(String text) {
        synchronized (XLELog.class) {
            if (!logFile.exists()) {
                try {
                    logFile.createNewFile();
                } catch (IOException e) {
                    Error("XLELog", "can't create log file");
                }
            }
            try {
                BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
                buf.append(text);
                buf.append("\r\n");
                buf.flush();
                buf.close();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
    }
}
