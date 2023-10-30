package com.microsoft.xbox.xle.app.activity;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.xle.viewmodel.TvSeasonRelatedActivityViewModel;

public class TvSeasonRelatedActivity extends ActivityBase {
    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        this.viewModel = new TvSeasonRelatedActivityViewModel();
    }

    protected String getActivityName() {
        return "TVSeasonRelated";
    }

    protected String getChannelName() {
        return ActivityBase.detailsChannel;
    }

    public void onCreateContentView() {
        setAppBarLayout(R.layout.appbar_refresh, false, false);
        setContentView(R.layout.details_related_activity);
    }
}
