package com.microsoft.xbox.xle.app.activity;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.xle.viewmodel.SettingsActivityViewModel;

public class SettingsActivity extends ActivityBase {
    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        this.viewModel = new SettingsActivityViewModel();
    }

    public void onCreateContentView() {
        setContentView(R.layout.settings_activity);
        setAppBarLayout(-1, true, false);
    }

    protected String getActivityName() {
        return ActivityBase.settingChannel;
    }

    protected String getChannelName() {
        return ActivityBase.settingChannel;
    }
}
