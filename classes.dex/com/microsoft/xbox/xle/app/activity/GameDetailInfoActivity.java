package com.microsoft.xbox.xle.app.activity;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.xle.viewmodel.GameDetailInfoActivityViewModel;

public class GameDetailInfoActivity extends ActivityBase {
    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        this.viewModel = new GameDetailInfoActivityViewModel();
    }

    public void onCreateContentView() {
        setContentView(R.layout.gamedetail_info_activity);
        setAppBarLayout(R.layout.appbar_refresh, false, false);
    }

    protected String getActivityName() {
        return "GameDetailInfoActivity";
    }

    protected String getChannelName() {
        return ActivityBase.detailsChannel;
    }
}
