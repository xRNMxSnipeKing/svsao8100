package com.microsoft.xbox.xle.app.activity;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.xle.viewmodel.TvSeasonDetailsActivityViewModel;

public class TvSeasonDetailsActivity extends ActivityBase {
    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        this.viewModel = new TvSeasonDetailsActivityViewModel();
    }

    public void onCreateContentView() {
        setContentView(R.layout.tv_season_details_activity);
        setAppBarLayout(R.layout.appbar_refresh, false, false);
    }

    protected String getActivityName() {
        return "DetailsTvSeason";
    }

    protected String getChannelName() {
        return ActivityBase.detailsChannel;
    }
}
