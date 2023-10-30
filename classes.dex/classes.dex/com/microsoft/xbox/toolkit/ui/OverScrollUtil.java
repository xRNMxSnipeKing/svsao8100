package com.microsoft.xbox.toolkit.ui;

import android.graphics.drawable.Drawable;
import android.view.View;
import com.microsoft.xbox.toolkit.XLELog;
import java.lang.reflect.Method;

public class OverScrollUtil {
    public static void disableOverScroll(View targetView) {
        try {
            Method method = targetView.getClass().getMethod("setOverScrollMode", new Class[]{Integer.TYPE});
            if (method != null) {
                int OVER_SCROLL_NEVER = ((Integer) targetView.getClass().getField("OVER_SCROLL_NEVER").get(targetView)).intValue();
                method.invoke(targetView, new Object[]{Integer.valueOf(OVER_SCROLL_NEVER)});
            }
        } catch (Exception e) {
            XLELog.Warning("OverScrollUtil", "setOverScrollMode error: " + e.toString());
        }
    }

    public static void removeOverScrollFooter(View targetView) {
        try {
            Method method = targetView.getClass().getMethod("setOverscrollFooter", new Class[]{Drawable.class});
            if (method != null) {
                method.invoke(targetView, new Object[]{null});
            }
        } catch (Exception e) {
            XLELog.Warning("OverScrollUtil", "setOverscrollFooter error: " + e.toString());
        }
    }
}
