package com.microsoft.xbox.toolkit.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.microsoft.xbox.toolkit.XboxApplication;

public class CustomTypefaceTextView extends TextView {
    public CustomTypefaceTextView(Context context, String typeface) {
        super(context);
        applyCustomTypeface(context, typeface);
    }

    public CustomTypefaceTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, XboxApplication.Instance.getStyleableRValueArray("CustomTypeface"));
        String typeface = a.getString(XboxApplication.Instance.getStyleableRValue("CustomTypeface_typefaceSource"));
        String uppercaseText = a.getString(XboxApplication.Instance.getStyleableRValue("CustomTypeface_uppercaseText"));
        if (uppercaseText != null) {
            setText(uppercaseText.toUpperCase());
        }
        applyCustomTypeface(context, typeface);
        a.recycle();
    }

    public CustomTypefaceTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, XboxApplication.Instance.getStyleableRValueArray("CustomTypeface"));
        applyCustomTypeface(context, a.getString(XboxApplication.Instance.getStyleableRValue("CustomTypeface_typefaceSource")));
        a.recycle();
    }

    private void applyCustomTypeface(Context context, String typefaceSource) {
        if (typefaceSource != null) {
            setTypeface(FontManager.Instance().getTypeface(getContext(), typefaceSource));
        }
        setCursorVisible(false);
    }

    public void setOnClickListener(OnClickListener l) {
        throw new UnsupportedOperationException("If you want CustomTypefaceTextView to be clickable, use XLEButton instead.");
    }

    public void setClickable(boolean clickable) {
        if (clickable) {
            throw new UnsupportedOperationException("If you want CustomTypefaceTextView to be clickable, use XLEButton instead.");
        }
    }
}
