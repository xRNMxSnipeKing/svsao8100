package com.microsoft.xbox.xle.app.activity;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.xle.viewmodel.ActivityOverviewActivityViewModel;

public class ActivityOverviewActivity extends ActivityBase {
    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        this.viewModel = new ActivityOverviewActivityViewModel();
    }

    public void onCreateContentView() {
        setContentView(R.layout.activity_overview_activity);
        setAppBarLayout(R.layout.appbar_refresh, false, false);
    }

    protected String getActivityName() {
        return "Detail";
    }

    protected String getChannelName() {
        return "SGActivity";
    }
}
