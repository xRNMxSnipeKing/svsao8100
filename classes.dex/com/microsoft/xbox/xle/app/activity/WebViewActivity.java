package com.microsoft.xbox.xle.app.activity;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.xle.viewmodel.WebViewActivityViewModel;

public class WebViewActivity extends ActivityBase {
    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        this.viewModel = new WebViewActivityViewModel();
    }

    public void onCreateContentView() {
        setContentView(R.layout.webview_activity);
        setAppBarLayout(R.layout.webview_appbar, true, false);
    }

    public void onStart() {
        super.onStart();
    }

    protected String getActivityName() {
        return "WebView";
    }

    protected String getChannelName() {
        return ActivityBase.socialChannel;
    }
}
