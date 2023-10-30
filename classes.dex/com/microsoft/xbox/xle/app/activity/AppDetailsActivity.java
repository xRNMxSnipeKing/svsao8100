package com.microsoft.xbox.xle.app.activity;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.xle.viewmodel.AppDetailsActivityViewModel;

public class AppDetailsActivity extends ActivityBase {
    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        this.viewModel = new AppDetailsActivityViewModel();
    }

    public void onCreateContentView() {
        setContentView(R.layout.discover_details_item_app_activity);
        setAppBarLayout(R.layout.appbar_refresh, false, true);
    }

    protected String getActivityName() {
        return "AppDetails";
    }

    protected String getChannelName() {
        return ActivityBase.detailsChannel;
    }
}
