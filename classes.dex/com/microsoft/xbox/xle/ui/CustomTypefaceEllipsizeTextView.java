package com.microsoft.xbox.xle.ui;

import android.content.Context;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.util.AttributeSet;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.toolkit.ui.CustomTypefaceTextView;

public class CustomTypefaceEllipsizeTextView extends CustomTypefaceTextView {
    private final int DEFAULT_LINE_COUNT = XboxApplication.Resources.getInteger(R.integer.default_text_view_ellipsize_lines);
    private EllipsizeListener ellipsizeListener;
    private boolean isNeedResetLayout = true;
    private int lineCount;
    private int textExpandedHeight;

    interface EllipsizeListener {
        void onEllipsizeChange(boolean z);
    }

    public CustomTypefaceEllipsizeTextView(Context context, String typeface) {
        super(context, typeface);
    }

    public CustomTypefaceEllipsizeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomTypefaceEllipsizeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    protected void onTextChanged(CharSequence text, int start, int before, int after) {
        super.onTextChanged(text, start, before, after);
        this.isNeedResetLayout = true;
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (this.isNeedResetLayout) {
            StaticLayout layout = new StaticLayout(getText(), getPaint(), getWidth(), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            this.textExpandedHeight = layout.getHeight();
            this.lineCount = layout.getLineCount();
            if (this.ellipsizeListener != null) {
                ThreadManager.UIThreadPost(new Runnable() {
                    public void run() {
                        CustomTypefaceEllipsizeTextView.this.ellipsizeListener.onEllipsizeChange(CustomTypefaceEllipsizeTextView.this.lineCount > CustomTypefaceEllipsizeTextView.this.DEFAULT_LINE_COUNT);
                    }
                });
            }
            this.isNeedResetLayout = false;
        }
    }

    public void setAlwaysShowText(boolean showAllText) {
        if (!showAllText) {
            setLines(this.DEFAULT_LINE_COUNT);
        }
    }

    public int getTextExpandedHeight() {
        return this.textExpandedHeight;
    }

    public int getCollapsedLineCount() {
        return this.DEFAULT_LINE_COUNT;
    }

    public int getLineCount() {
        return this.lineCount;
    }

    public void setEllipsizeListener(EllipsizeListener ellipsizeListener) {
        this.ellipsizeListener = ellipsizeListener;
    }
}
