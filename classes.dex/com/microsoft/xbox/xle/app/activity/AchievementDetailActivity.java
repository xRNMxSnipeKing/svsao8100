package com.microsoft.xbox.xle.app.activity;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.xle.viewmodel.AchievementDetailActivityViewModel;

public class AchievementDetailActivity extends ActivityBase {
    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        this.viewModel = new AchievementDetailActivityViewModel();
    }

    public void onCreateContentView() {
        setContentView(R.layout.achievementdetails_activity);
        setAppBarLayout(R.layout.appbar_refresh, false, false);
    }

    public void resume() {
    }

    public void onRestart() {
    }

    protected String getActivityName() {
        return "Achievement";
    }

    protected String getChannelName() {
        return ActivityBase.gamesChannel;
    }
}
