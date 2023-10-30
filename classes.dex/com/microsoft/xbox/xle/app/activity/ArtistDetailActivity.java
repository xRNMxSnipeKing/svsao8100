package com.microsoft.xbox.xle.app.activity;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.xle.viewmodel.ArtistDetailsActivityViewModel;

public class ArtistDetailActivity extends ActivityBase {
    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        this.viewModel = new ArtistDetailsActivityViewModel();
    }

    public void onCreateContentView() {
        setContentView(R.layout.artist_detail_activity);
        setAppBarLayout(R.layout.appbar_refresh, false, false);
    }

    protected String getActivityName() {
        return "ArtistDetail";
    }

    protected String getChannelName() {
        return ActivityBase.detailsChannel;
    }
}
