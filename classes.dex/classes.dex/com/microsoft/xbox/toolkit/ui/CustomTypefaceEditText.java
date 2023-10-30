package com.microsoft.xbox.toolkit.ui;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.XboxApplication;

public class CustomTypefaceEditText extends EditText {
    private EditTextContainer container;
    private Runnable textOrSelectionChangedRunnable = null;

    public CustomTypefaceEditText(Context context) {
        super(context);
        hookTextChangedEvent();
        ScreenLayout.addViewThatCausesAndroidLeaks(this);
    }

    public CustomTypefaceEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomTypeface(context, context.obtainStyledAttributes(attrs, XboxApplication.Instance.getStyleableRValueArray("CustomTypeface")).getString(XboxApplication.Instance.getStyleableRValue("CustomTypeface_typefaceSource")));
        hookTextChangedEvent();
        ScreenLayout.addViewThatCausesAndroidLeaks(this);
    }

    public CustomTypefaceEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        applyCustomTypeface(context, context.obtainStyledAttributes(attrs, XboxApplication.Instance.getStyleableRValueArray("CustomTypeface")).getString(XboxApplication.Instance.getStyleableRValue("CustomTypeface_typefaceSource")));
        hookTextChangedEvent();
        ScreenLayout.addViewThatCausesAndroidLeaks(this);
    }

    public void setContainer(EditTextContainer parent) {
        if (this.container != null) {
            XLELog.Error("EditViewFixedLength", "container is set multiple times");
            return;
        }
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.container = parent;
        this.container.addChild(this);
    }

    public void setTextOrSelectionChangedRunnable(Runnable r) {
        this.textOrSelectionChangedRunnable = r;
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        setOnFocusChangeListener(new OnFocusChangeListener() {
            public void onFocusChange(View arg0, boolean hasFocus) {
                if (hasFocus && CustomTypefaceEditText.this.container != null) {
                    CustomTypefaceEditText.this.container.setKeyboardShown();
                }
            }
        });
    }

    protected void onSelectionChanged(int start, int end) {
        super.onSelectionChanged(start, end);
        textOrSelectionChangedEvent();
    }

    private void hookTextChangedEvent() {
        addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
                CustomTypefaceEditText.this.textOrSelectionChangedEvent();
            }
        });
    }

    private void textOrSelectionChangedEvent() {
        if (this.textOrSelectionChangedRunnable != null) {
            this.textOrSelectionChangedRunnable.run();
        }
    }

    private void applyCustomTypeface(Context context, String typefaceSource) {
        if (typefaceSource != null) {
            setTypeface(FontManager.Instance().getTypeface(getContext(), typefaceSource));
        }
    }
}
