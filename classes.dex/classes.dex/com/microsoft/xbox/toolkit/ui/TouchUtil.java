package com.microsoft.xbox.toolkit.ui;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import com.microsoft.xbox.toolkit.SoundManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XboxApplication;

public class TouchUtil {
    public static OnClickListener createOnClickListener(final OnClickListener listener) {
        if (listener == null) {
            return null;
        }
        XLEAssert.assertNotNull("Original listener is null.", listener);
        if (XboxApplication.Instance.supportsButtonSounds()) {
            return new OnClickListener() {
                public void onClick(View v) {
                    XLEAssert.assertNotNull("Trying to click on a view that is null!?!", v);
                    SoundManager.getInstance().playSound(XboxApplication.Instance.getRawRValue("sndbuttonselectandroid"));
                    listener.onClick(v);
                }
            };
        }
        return listener;
    }

    public static OnItemClickListener createOnItemClickListener(final OnItemClickListener listener) {
        if (listener == null) {
            return null;
        }
        XLEAssert.assertNotNull("Original listener is null.", listener);
        if (XboxApplication.Instance.supportsButtonSounds()) {
            return new OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    XLEAssert.assertNotNull("Trying to click on a view that is null!?!", view);
                    SoundManager.getInstance().playSound(XboxApplication.Instance.getRawRValue("sndbuttonselectandroid"));
                    listener.onItemClick(parent, view, position, id);
                }
            };
        }
        return listener;
    }

    public static OnLongClickListener createOnLongClickListener(final OnLongClickListener listener) {
        if (listener == null) {
            return null;
        }
        XLEAssert.assertNotNull("Original listener is null.", listener);
        if (XboxApplication.Instance.supportsButtonSounds()) {
            return new OnLongClickListener() {
                public boolean onLongClick(View v) {
                    XLEAssert.assertNotNull("Trying to click on a view that is null!?!", v);
                    SoundManager.getInstance().playSound(XboxApplication.Instance.getRawRValue("sndbuttonselectandroid"));
                    return listener.onLongClick(v);
                }
            };
        }
        return listener;
    }
}
