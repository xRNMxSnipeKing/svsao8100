package com.microsoft.xbox.toolkit.ui.appbar;

import android.content.Context;
import android.util.AttributeSet;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.toolkit.ui.XLEButton;

public class AppBarMenuButton extends XLEButton {
    public AppBarMenuButton(Context context) {
        super(context, null, XboxApplication.Instance.getStyleRValue("app_bar_menu_item_style"));
    }

    public AppBarMenuButton(Context context, AttributeSet attrs) {
        super(context, attrs, XboxApplication.Instance.getStyleRValue("app_bar_menu_item_style"));
    }

    public AppBarMenuButton(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);
    }
}
