package com.microsoft.xbox.xle.app.activity;

import android.content.Context;
import android.util.AttributeSet;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.xle.viewmodel.RecentGamesActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.XLEGlobalData;

public class RecentGamesActivity extends ActivityBase {
    public RecentGamesActivity(Context context, AttributeSet attrs) {
    }

    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        this.viewModel = new RecentGamesActivityViewModel(XLEGlobalData.getInstance().getSelectedGamertag());
    }

    public void onCreateContentView() {
        setContentView(R.layout.you_recent_games_activity);
        setAppBarLayout(R.layout.appbar_refresh, false, false);
    }

    protected boolean shouldTrackPageVisit() {
        return false;
    }

    protected String getActivityName() {
        return "RecentGames";
    }

    protected String getChannelName() {
        return ActivityBase.homeChannel;
    }
}
