package com.microsoft.xbox.toolkit.ui;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import com.microsoft.xbox.toolkit.XboxApplication;

public class CancellableBlockingScreen extends Dialog {
    private XLEButton cancelButton = ((XLEButton) findViewById(XboxApplication.Instance.getIdRValue("blocking_dialog_cancel")));
    private View container = findViewById(XboxApplication.Instance.getIdRValue("blocking_dialog_container"));
    private TextView statusText = ((TextView) findViewById(XboxApplication.Instance.getIdRValue("blocking_dialog_status_text")));

    public CancellableBlockingScreen(Context context) {
        super(context, XboxApplication.Instance.getStyleRValue("cancellable_dialog_style"));
        setCancelable(false);
        setOnCancelListener(null);
        setContentView(XboxApplication.Instance.getLayoutRValue("cancellable_blocking_dialog"));
    }

    public void show(Context context, CharSequence statusText) {
        boolean previouslyVisible = isShowing();
        setMessage(statusText);
        show();
        if (!previouslyVisible) {
            AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
            animation.setFillAfter(true);
            animation.setStartOffset(1000);
            animation.setDuration(1000);
            this.container.startAnimation(animation);
        }
    }

    public void setMessage(CharSequence statusText) {
        this.statusText.setText(statusText);
    }

    public void setCancelButtonAction(OnClickListener listener) {
        if (listener != null) {
            this.cancelButton.setOnClickListener(null);
        }
        this.cancelButton.setOnClickListener(listener);
    }
}
