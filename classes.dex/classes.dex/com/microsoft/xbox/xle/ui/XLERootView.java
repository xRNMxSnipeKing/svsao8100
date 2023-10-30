package com.microsoft.xbox.xle.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.FPSTool;
import com.microsoft.xbox.toolkit.XLELog;

public class XLERootView extends RelativeLayout {
    private View activityBody;
    private int activityBodyIndex;
    private String headerName;
    private boolean isTopLevel = false;
    private long lastFps = 0;
    private long lastMs = 0;
    private boolean showTitleBar = true;

    public XLERootView(Context context) {
        super(context);
        throw new UnsupportedOperationException();
    }

    public XLERootView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.XLERootView);
        this.activityBodyIndex = a.getResourceId(0, -1);
        this.isTopLevel = a.getBoolean(2, false);
        this.showTitleBar = a.getBoolean(1, true);
        if (this.activityBodyIndex < 0) {
            throw new IllegalArgumentException("Invalid activity body attributes");
        }
        this.headerName = a.getString(3);
    }

    public boolean getIsTopLevel() {
        return this.isTopLevel;
    }

    public boolean getShowTitleBar() {
        return this.showTitleBar;
    }

    public String getHeaderName() {
        return this.headerName;
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        initialize();
    }

    private void initialize() {
        this.activityBody = findViewById(this.activityBodyIndex);
        LayoutParams activityParams = new LayoutParams((LayoutParams) this.activityBody.getLayoutParams());
        activityParams.width = -1;
        activityParams.height = -1;
        activityParams.addRule(10);
        removeView(this.activityBody);
        addView(this.activityBody, activityParams);
    }

    protected void dispatchDraw(Canvas canvas) {
        long deltaMs = System.currentTimeMillis() - this.lastMs;
        this.lastMs = System.currentTimeMillis();
        if (deltaMs > 0) {
            int fps = (int) (1000.0f / ((float) deltaMs));
            this.lastFps = (long) ((int) ((((float) this.lastFps) * 0.9f) + (((float) fps) * 0.1f)));
            FPSTool.getInstance().addFPS(fps);
        }
        super.dispatchDraw(canvas);
    }

    public void setBottomMargin(int marginBottom) {
        XLELog.Diagnostic("XLERootView", "Adjusting bottom margin: " + marginBottom);
        LayoutParams activityParams = new LayoutParams((LayoutParams) this.activityBody.getLayoutParams());
        activityParams.width = -1;
        activityParams.height = -1;
        activityParams.addRule(10);
        activityParams.bottomMargin = marginBottom;
        this.activityBody.setLayoutParams(activityParams);
    }
}
