package com.microsoft.xbox.smartglass.canvas;

import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.microsoft.xbox.toolkit.XLELog;

public class CanvasWebViewClient extends WebViewClient {
    public void onPageFinished(WebView view, String url) {
        ((CanvasView) view).onLoadCompleted(url);
    }

    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        ((CanvasView) view).onNavigating(url);
    }

    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        XLELog.Diagnostic("CanvasView", String.format("onReceivedError: errorCode %d, description %s, failingUrl %s", new Object[]{Integer.valueOf(errorCode), description, failingUrl}));
        ((CanvasView) view).onNavigationFailed(failingUrl, errorCode, description);
    }
}
