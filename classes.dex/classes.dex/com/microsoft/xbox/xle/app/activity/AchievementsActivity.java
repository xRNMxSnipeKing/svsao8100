package com.microsoft.xbox.xle.app.activity;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.xle.viewmodel.AchievementsActivityViewModel;

public class AchievementsActivity extends ActivityBase {
    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        this.viewModel = new AchievementsActivityViewModel();
    }

    public void onCreateContentView() {
        setContentView(R.layout.achievements_activity);
        setAppBarLayout(R.layout.appbar_refresh, false, false);
    }

    protected String getActivityName() {
        return "Detail";
    }

    protected String getChannelName() {
        return ActivityBase.gamesChannel;
    }
}
