package com.microsoft.xbox.toolkit.ui.appbar;

import android.app.Dialog;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout.LayoutParams;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.XboxApplication;

public class ExpandedAppBar extends Dialog {
    private ApplicationBarView appBarView;

    public ExpandedAppBar(ApplicationBarView appBarView, Context context) {
        super(context, XboxApplication.Instance.getStyleRValue("expanded_app_bar_style"));
        XLEAssert.assertTrue("Make sure to remove the ApplicationBarView from its parent before passing it into ExpandedAppBar.", appBarView.getParent() == null);
        setCancelable(true);
        setOnCancelListener(null);
        LayoutParams params = new LayoutParams(-1, -2);
        params.gravity = 80;
        setContentView(appBarView, params);
        this.appBarView = appBarView;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == 1 && !JavaUtil.isTouchPointInsideView(event.getRawX(), event.getRawY(), this.appBarView)) {
            dismiss();
        }
        return super.onTouchEvent(event);
    }

    public void show() {
        this.appBarView.getMenuOptionButton().setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                XLELog.Diagnostic("ExpandedAppBar", "Menu Clicked");
                ExpandedAppBar.this.dismiss();
            }
        });
        super.show();
    }

    public void dismiss() {
        this.appBarView.getMenuOptionButton().setOnClickListener(null);
        super.dismiss();
    }
}
