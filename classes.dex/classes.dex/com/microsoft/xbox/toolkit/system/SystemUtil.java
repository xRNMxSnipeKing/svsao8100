package com.microsoft.xbox.toolkit.system;

import android.os.Build.VERSION;
import android.os.Environment;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.XboxApplication;

public class SystemUtil {
    private static final int MAX_SD_SCREEN_PIXELS = 384000;

    public static int getSdkInt() {
        return VERSION.SDK_INT;
    }

    public static int DIPtoPixels(float dip) {
        return (int) TypedValue.applyDimension(1, dip, XboxApplication.Instance.getResources().getDisplayMetrics());
    }

    public static int getScreenWidth() {
        return XboxApplication.Resources.getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return XboxApplication.Resources.getDisplayMetrics().heightPixels;
    }

    public static float getScreenWidthInches() {
        return ((float) getScreenWidth()) / XboxApplication.Resources.getDisplayMetrics().xdpi;
    }

    public static float getScreenHeightInches() {
        return ((float) getScreenHeight()) / XboxApplication.Resources.getDisplayMetrics().ydpi;
    }

    public static float getYDPI() {
        return XboxApplication.Resources.getDisplayMetrics().ydpi;
    }

    public static int getRotation() {
        return getDisplay().getRotation();
    }

    public static int getOrientation() {
        int rotation = getRotation();
        if (rotation == 0 || rotation == 2) {
            XLELog.Diagnostic("SystemUrl", "Orientation is portait");
            return 1;
        }
        XLELog.Diagnostic("SystemUrl", "Orientation is landscape");
        return 2;
    }

    public static boolean isHDScreen() {
        return getScreenHeight() * getScreenWidth() > MAX_SD_SCREEN_PIXELS;
    }

    private static Display getDisplay() {
        return ((WindowManager) XboxApplication.Instance.getSystemService("window")).getDefaultDisplay();
    }

    public static boolean isSDCardAvailable() {
        return Environment.getExternalStorageState().equals("mounted");
    }

    public static float getScreenWidthHeightAspectRatio() {
        int screenWidth = XboxApplication.MainActivity.getScreenWidth();
        int screenHeight = XboxApplication.MainActivity.getScreenHeight();
        if (screenWidth <= 0 || screenHeight <= 0) {
            return 0.0f;
        }
        if (screenWidth > screenHeight) {
            return ((float) screenWidth) / ((float) screenHeight);
        }
        return ((float) screenHeight) / ((float) screenWidth);
    }
}
