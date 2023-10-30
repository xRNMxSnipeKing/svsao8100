package com.microsoft.xbox.xle.app.activity;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.xle.viewmodel.GameContentDetailActivityViewModel;

public class GameContentDetailActivity extends ActivityBase {
    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        this.viewModel = new GameContentDetailActivityViewModel();
    }

    public void onCreateContentView() {
        setContentView(R.layout.game_content_detail_activity);
        setAppBarLayout(R.layout.appbar_refresh, false, false);
    }

    protected String getActivityName() {
        return "GameContent";
    }

    protected String getChannelName() {
        return ActivityBase.detailsChannel;
    }
}
