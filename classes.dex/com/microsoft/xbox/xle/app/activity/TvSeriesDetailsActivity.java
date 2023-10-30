package com.microsoft.xbox.xle.app.activity;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.xle.viewmodel.TvSeriesDetailsViewModel;

public class TvSeriesDetailsActivity extends ActivityBase {
    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        this.viewModel = new TvSeriesDetailsViewModel();
    }

    public void onCreateContentView() {
        setContentView(R.layout.tv_series_details_activity);
        setAppBarLayout(R.layout.appbar_refresh, false, false);
    }

    protected String getActivityName() {
        return "DetailsTvSeries";
    }

    protected String getChannelName() {
        return ActivityBase.detailsChannel;
    }
}
