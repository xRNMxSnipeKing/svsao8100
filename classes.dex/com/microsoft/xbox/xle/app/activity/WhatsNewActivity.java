package com.microsoft.xbox.xle.app.activity;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.xle.viewmodel.WhatsNewActivityViewModel;

public class WhatsNewActivity extends ActivityBase {
    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        this.viewModel = new WhatsNewActivityViewModel();
    }

    public void onCreateContentView() {
        setContentView(R.layout.whats_new_activity);
        setAppBarLayout(-1, true, false);
    }

    public void onStart() {
        super.onStart();
    }

    protected String getActivityName() {
        return "What's New";
    }

    protected String getChannelName() {
        return null;
    }

    public boolean getCanAutoLaunch() {
        return true;
    }
}
