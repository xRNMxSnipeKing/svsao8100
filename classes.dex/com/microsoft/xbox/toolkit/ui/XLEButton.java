package com.microsoft.xbox.toolkit.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.FrameLayout.LayoutParams;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.toolkit.ui.ButtonStateHandler.ButtonStateHandlerRunnable;

public class XLEButton extends Button {
    private boolean alwaysClickable;
    protected boolean disableSound = false;
    private int disabledTextColor;
    private int enabledTextColor;
    protected ButtonStateHandler stateHandler = new ButtonStateHandler();

    public XLEButton(Context context) {
        super(context);
        setSoundEffectsEnabled(false);
    }

    public XLEButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setSoundEffectsEnabled(false);
        TypedArray a = context.obtainStyledAttributes(attrs, XboxApplication.Instance.getStyleableRValueArray("XLEButton"));
        this.stateHandler.setDisabled(a.getBoolean(XboxApplication.Instance.getStyleableRValue("XLEButton_disabled"), false));
        this.stateHandler.setDisabledImageHandle(a.getResourceId(XboxApplication.Instance.getStyleableRValue("XLEButton_disabledImage"), -1));
        this.stateHandler.setEnabledImageHandle(a.getResourceId(XboxApplication.Instance.getStyleableRValue("XLEButton_enabledImage"), -1));
        this.stateHandler.setPressedImageHandle(a.getResourceId(XboxApplication.Instance.getStyleableRValue("XLEButton_pressedImage"), -1));
        this.disableSound = a.getBoolean(XboxApplication.Instance.getStyleableRValue("XLEButton_disableSound"), false);
        a.recycle();
        setLayoutParams(new LayoutParams(-2, -2));
        String typeface = context.obtainStyledAttributes(attrs, XboxApplication.Instance.getStyleableRValueArray("CustomTypeface")).getString(XboxApplication.Instance.getStyleableRValue("CustomTypeface_typefaceSource"));
        if (typeface != null && typeface.length() > 0) {
            applyCustomTypeface(context, typeface);
        }
        this.enabledTextColor = getCurrentTextColor();
        this.disabledTextColor = a.getColor(XboxApplication.Instance.getStyleableRValue("XLEButton_disabledTextColor"), this.enabledTextColor);
        this.alwaysClickable = a.getBoolean(XboxApplication.Instance.getStyleableRValue("XLEButton_alwaysClickable"), false);
        if (this.alwaysClickable) {
            super.setEnabled(true);
            super.setClickable(true);
        }
    }

    public XLEButton(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);
        setSoundEffectsEnabled(false);
        TypedArray a = context.obtainStyledAttributes(attrs, XboxApplication.Instance.getStyleableRValueArray("XLEButton"));
        this.enabledTextColor = getCurrentTextColor();
        this.disabledTextColor = a.getColor(XboxApplication.Instance.getStyleableRValue("XLEButton_disabledTextColor"), this.enabledTextColor);
    }

    private void applyCustomTypeface(Context context, String typefaceSource) {
        if (typefaceSource != null) {
            setTypeface(FontManager.Instance().getTypeface(getContext(), typefaceSource));
        }
    }

    public void setEnabled(boolean enabled) {
        if (!this.alwaysClickable) {
            super.setEnabled(enabled);
        }
        if (this.stateHandler == null) {
            this.stateHandler = new ButtonStateHandler();
        }
        this.stateHandler.setEnabled(enabled);
        updateImage();
        updateTextColor();
    }

    protected void onFinishInflate() {
        updateImage();
        setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                boolean handled = XLEButton.this.stateHandler.onTouch(event);
                XLEButton.this.updateImage();
                return handled;
            }
        });
    }

    public void setOnClickListener(OnClickListener listener) {
        if (this.disableSound) {
            super.setOnClickListener(listener);
        } else {
            super.setOnClickListener(TouchUtil.createOnClickListener(listener));
        }
    }

    public void setOnLongClickListener(OnLongClickListener listener) {
        if (this.disableSound) {
            super.setOnLongClickListener(listener);
        } else {
            super.setOnLongClickListener(TouchUtil.createOnLongClickListener(listener));
        }
    }

    public void setPressedStateRunnable(ButtonStateHandlerRunnable runnable) {
        this.stateHandler.setPressedStateRunnable(runnable);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        boolean loadedNewImage = false;
        if (hasSize()) {
            loadedNewImage = this.stateHandler.onSizeChanged(getWidth(), getHeight());
        }
        if (loadedNewImage) {
            updateImage();
        }
    }

    private boolean hasSize() {
        return getWidth() > 0 && getHeight() > 0;
    }

    protected void updateImage() {
        if (this.stateHandler.getImageDrawable() != null) {
            setBackgroundDrawable(this.stateHandler.getImageDrawable());
        }
    }

    protected void updateTextColor() {
        if (this.enabledTextColor != this.disabledTextColor) {
            setTextColor(this.stateHandler.getDisabled() ? this.disabledTextColor : this.enabledTextColor);
        }
    }
}
