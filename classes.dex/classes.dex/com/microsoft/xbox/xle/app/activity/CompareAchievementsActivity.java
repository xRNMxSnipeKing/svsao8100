package com.microsoft.xbox.xle.app.activity;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.xle.viewmodel.CompareAchievementsActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.XLEGlobalData;

public class CompareAchievementsActivity extends ActivityBase {
    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        this.viewModel = new CompareAchievementsActivityViewModel(XLEGlobalData.getInstance().getSelectedGamertag(), XLEGlobalData.getInstance().getSelectedGame());
    }

    public void onCreateContentView() {
        setContentView(R.layout.compare_achievements_activity);
        setAppBarLayout(R.layout.appbar_refresh, false, false);
    }

    protected String getActivityName() {
        return "CompareAchievements";
    }

    protected String getChannelName() {
        return ActivityBase.gamesChannel;
    }
}
