package com.microsoft.xbox.xle.app.activity;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.xle.viewmodel.AboutActivityViewModel;

public class AboutActivity extends ActivityBase {
    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        this.viewModel = new AboutActivityViewModel();
    }

    public void onCreateContentView() {
        setContentView(R.layout.about_activity);
        setAppBarLayout(-1, true, false);
    }

    protected String getActivityName() {
        return ActivityBase.aboutChannel;
    }

    protected String getChannelName() {
        return ActivityBase.aboutChannel;
    }
}
