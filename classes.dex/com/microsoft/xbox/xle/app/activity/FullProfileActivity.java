package com.microsoft.xbox.xle.app.activity;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.xle.viewmodel.FullProfileActivityViewModel;

public class FullProfileActivity extends ActivityBase {
    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        this.viewModel = new FullProfileActivityViewModel();
    }

    public void onCreateContentView() {
        setContentView(R.layout.full_profile_activity);
        setAppBarLayout(R.layout.full_profile_appbar, false, false);
    }

    protected String getActivityName() {
        return "FullProfile";
    }

    protected String getChannelName() {
        return ActivityBase.profileChannel;
    }
}
