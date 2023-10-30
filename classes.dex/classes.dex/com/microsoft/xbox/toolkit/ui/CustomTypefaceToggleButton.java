package com.microsoft.xbox.toolkit.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ToggleButton;
import com.microsoft.xbox.toolkit.XboxApplication;

public class CustomTypefaceToggleButton extends ToggleButton {
    public CustomTypefaceToggleButton(Context context) {
        super(context);
    }

    public CustomTypefaceToggleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomTypeface(context, context.obtainStyledAttributes(attrs, XboxApplication.Instance.getStyleableRValueArray("CustomTypeface")).getString(XboxApplication.Instance.getStyleableRValue("CustomTypeface_typefaceSource")));
    }

    public CustomTypefaceToggleButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        applyCustomTypeface(context, context.obtainStyledAttributes(attrs, XboxApplication.Instance.getStyleableRValueArray("CustomTypeface")).getString(XboxApplication.Instance.getStyleableRValue("CustomTypeface_typefaceSource")));
    }

    private void applyCustomTypeface(Context context, String typefaceSource) {
        if (typefaceSource != null) {
            setTypeface(FontManager.Instance().getTypeface(getContext(), typefaceSource));
        }
    }
}
