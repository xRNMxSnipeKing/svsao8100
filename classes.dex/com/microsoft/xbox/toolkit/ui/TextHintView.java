package com.microsoft.xbox.toolkit.ui;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XboxApplication;

public class TextHintView extends CustomTypefaceTextView {
    private static final int DEFAULT_COUNT_DOWN_INTERVAL = 1000;
    private CountDownTimer counter;
    private int currentIndex = 0;
    private String[] hints;

    public TextHintView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public TextHintView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        boolean z = true;
        int sourceId = context.obtainStyledAttributes(attrs, XboxApplication.Instance.getStyleableRValueArray("TextHintView")).getResourceId(XboxApplication.Instance.getStyleableRValue("TextHintView_hints"), -1);
        XLEAssert.assertTrue(sourceId != -1);
        this.hints = context.getResources().getStringArray(sourceId);
        if (this.hints == null || this.hints.length <= 0) {
            z = false;
        }
        XLEAssert.assertTrue(z);
    }

    public void setVisibility(int visibility) {
        if (visibility != 0) {
            dimissCountDownTimer();
        } else if (this.hints != null && this.hints.length > 0) {
            this.currentIndex = 0;
            createCountDownTimer();
            this.counter.start();
        }
        super.setVisibility(visibility);
    }

    public void dimissCountDownTimer() {
        if (this.counter != null) {
            this.counter.cancel();
        }
        this.counter = null;
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (getVisibility() == 0 && this.hints != null && this.hints.length > 0) {
            this.currentIndex = 0;
            createCountDownTimer();
            this.counter.start();
        }
    }

    private void createCountDownTimer() {
        if (this.counter != null) {
            this.counter.cancel();
            this.counter = null;
        }
        this.counter = new CountDownTimer(2147483647L, 1000) {
            public void onFinish() {
            }

            public void onTick(long millisUntilFinished) {
                TextHintView.this.setText(TextHintView.this.hints[TextHintView.this.currentIndex % TextHintView.this.hints.length]);
                TextHintView.this.currentIndex = TextHintView.this.currentIndex + 1;
            }
        };
    }
}
