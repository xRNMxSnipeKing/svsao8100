package com.microsoft.xbox.xle.app.activity;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.xle.viewmodel.TvSeriesRelatedActivityViewModel;

public class TvSeriesRelatedActivity extends ActivityBase {
    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        this.viewModel = new TvSeriesRelatedActivityViewModel();
    }

    public void onCreateContentView() {
        setAppBarLayout(R.layout.appbar_refresh, false, false);
        setContentView(R.layout.details_related_activity);
    }

    protected String getActivityName() {
        return "TVSeriesRelated";
    }

    protected String getChannelName() {
        return ActivityBase.detailsChannel;
    }
}
