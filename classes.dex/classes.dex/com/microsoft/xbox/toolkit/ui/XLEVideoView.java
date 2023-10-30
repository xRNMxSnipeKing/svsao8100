package com.microsoft.xbox.toolkit.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;
import com.microsoft.xbox.toolkit.XboxApplication;

public class XLEVideoView extends VideoView {
    public XLEVideoView(Context context, AttributeSet attrs) {
        super(XboxApplication.MainActivity, attrs);
    }
}
