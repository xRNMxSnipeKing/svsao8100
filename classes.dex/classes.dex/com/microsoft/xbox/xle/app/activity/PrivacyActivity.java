package com.microsoft.xbox.xle.app.activity;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.xle.viewmodel.PrivacyActivityViewModel;

public class PrivacyActivity extends ActivityBase {
    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        this.viewModel = new PrivacyActivityViewModel();
    }

    public void onCreateContentView() {
        setContentView(R.layout.privacy_activity);
        setAppBarLayout(R.layout.appbar_savecancel, true, false);
    }

    protected String getActivityName() {
        return "Privacy";
    }

    protected String getChannelName() {
        return ActivityBase.profileChannel;
    }
}
