package com.microsoft.xbox.xle.app.activity;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.xle.viewmodel.CanvasWebViewActivityViewModel;

public class CanvasWebViewActivity extends ActivityBase {
    public void onCreate() {
        super.onCreate();
        setContentView(R.layout.canvas_webview_activity);
        setAppBarLayout(R.layout.canvas_appbar, false, false);
        this.viewModel = new CanvasWebViewActivityViewModel();
    }

    public void onCreateContentView() {
    }

    public void onTombstone() {
        this.isTombstoned = true;
        XLELog.Diagnostic("CanvasWebViewActivity", "onTombstone called, but we are not removing views for canvas");
    }

    public void onStart() {
        super.onStart();
    }

    protected String getActivityName() {
        return "Canvas";
    }

    protected String getChannelName() {
        return "Activity";
    }
}
