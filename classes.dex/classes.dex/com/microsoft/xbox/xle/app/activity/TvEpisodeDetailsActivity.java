package com.microsoft.xbox.xle.app.activity;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.xle.viewmodel.TvEpisodeDetailsActivityViewModel;

public class TvEpisodeDetailsActivity extends ActivityBase {
    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        this.viewModel = new TvEpisodeDetailsActivityViewModel();
    }

    public void onCreateContentView() {
        setContentView(R.layout.tv_episode_details_activity);
        setAppBarLayout(R.layout.appbar_refresh, false, true);
    }

    protected String getActivityName() {
        return "DetailsTvEpisode";
    }

    protected String getChannelName() {
        return ActivityBase.detailsChannel;
    }
}
