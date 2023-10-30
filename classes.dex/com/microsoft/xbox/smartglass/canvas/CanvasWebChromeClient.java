package com.microsoft.xbox.smartglass.canvas;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class CanvasWebChromeClient extends WebChromeClient {
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        AlertDialog alertDialog = new Builder(view.getContext()).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage(message);
        alertDialog.setButton(-1, "OK", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
        result.confirm();
        return true;
    }
}
