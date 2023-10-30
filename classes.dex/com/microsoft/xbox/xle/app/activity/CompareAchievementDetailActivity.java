package com.microsoft.xbox.xle.app.activity;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.xle.viewmodel.CompareAchievementDetailActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.XLEGlobalData;

public class CompareAchievementDetailActivity extends ActivityBase {
    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        this.viewModel = new CompareAchievementDetailActivityViewModel(XLEGlobalData.getInstance().getSelectedGamertag(), XLEGlobalData.getInstance().getSelectedGame(), XLEGlobalData.getInstance().getSelectedAchievementKey());
    }

    public void onCreateContentView() {
        setContentView(R.layout.compare_achievementdetails_activity);
        setAppBarLayout(R.layout.appbar_refresh, false, false);
    }

    protected String getActivityName() {
        return "CompareAchievementsDetail";
    }

    protected String getChannelName() {
        return ActivityBase.gamesChannel;
    }
}
