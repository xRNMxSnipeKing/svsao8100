package com.microsoft.xbox.toolkit.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;
import com.microsoft.xbox.toolkit.XboxApplication;

public class XLEWebView extends WebView {
    public XLEWebView(Context context, AttributeSet attrs) {
        super(XboxApplication.MainActivity, attrs);
        ScreenLayout.addViewThatCausesAndroidLeaks(this);
    }
}
