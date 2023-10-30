package com.microsoft.xbox.toolkit.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.microsoft.xbox.toolkit.XboxApplication;

public class ToggleTypefaceTextView extends TextView {
    private boolean isPositive = true;
    private String negativeTypeface;
    private String positiveTypeface;

    public ToggleTypefaceTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, XboxApplication.Instance.getStyleableRValueArray("ToggleTypeface"));
        this.positiveTypeface = a.getString(XboxApplication.Instance.getStyleableRValue("ToggleTypeface_positiveTypeface"));
        this.negativeTypeface = a.getString(XboxApplication.Instance.getStyleableRValue("ToggleTypeface_negativeTypeface"));
        this.isPositive = a.getBoolean(XboxApplication.Instance.getStyleableRValue("ToggleTypeface_initialState"), true);
        if (this.positiveTypeface == null || this.negativeTypeface == null) {
            throw new IllegalArgumentException("We need typefaces for both states");
        }
        updateTypeface();
        a.recycle();
        setCursorVisible(false);
    }

    private void updateTypeface() {
        Typeface tf;
        if (this.isPositive) {
            tf = FontManager.Instance().getTypeface(getContext(), this.positiveTypeface);
        } else {
            tf = FontManager.Instance().getTypeface(getContext(), this.negativeTypeface);
        }
        setTypeface(tf);
    }

    public void setIsPositive(boolean isPositive) {
        if (this.isPositive != isPositive) {
            this.isPositive = isPositive;
            updateTypeface();
        }
    }

    public void setOnClickListener(OnClickListener l) {
        throw new UnsupportedOperationException("Click operation is not supported on this view type");
    }

    public void setClickable(boolean clickable) {
        if (clickable) {
            throw new UnsupportedOperationException("Click operation is not supported on this view type");
        }
    }
}
