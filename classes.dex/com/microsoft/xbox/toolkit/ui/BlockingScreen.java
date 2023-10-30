package com.microsoft.xbox.toolkit.ui;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;
import com.microsoft.xbox.toolkit.XboxApplication;

public class BlockingScreen extends Dialog {
    public BlockingScreen(Context context) {
        super(context, XboxApplication.Instance.getStyleRValue("blocking_dialog_style"));
    }

    public void show(Context context, CharSequence statusText) {
        setCancelable(false);
        setOnCancelListener(null);
        setContentView(XboxApplication.Instance.getLayoutRValue("blocking_dialog"));
        setMessage(statusText);
        show();
    }

    public void setMessage(CharSequence statusText) {
        ((TextView) findViewById(XboxApplication.Instance.getIdRValue("blocking_dialog_status_text"))).setText(statusText);
    }
}
