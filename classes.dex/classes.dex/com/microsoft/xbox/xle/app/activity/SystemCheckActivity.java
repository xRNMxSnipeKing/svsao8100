package com.microsoft.xbox.xle.app.activity;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.xle.viewmodel.SystemCheckActivityViewModel;

public class SystemCheckActivity extends ActivityBase {
    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        this.viewModel = new SystemCheckActivityViewModel();
    }

    public void onCreateContentView() {
        setContentView(R.layout.systemcheck_activity);
        setAppBarLayout(-1, true, false);
    }

    public void onStart() {
        super.onStart();
    }

    protected String getActivityName() {
        return "SystemCheck";
    }

    protected String getChannelName() {
        return ActivityBase.launchChannel;
    }

    protected boolean shouldTrackPageVisit() {
        return false;
    }
}
