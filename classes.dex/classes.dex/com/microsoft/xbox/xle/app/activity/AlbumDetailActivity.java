package com.microsoft.xbox.xle.app.activity;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.xle.viewmodel.AlbumDetailsActivityViewModel;

public class AlbumDetailActivity extends ActivityBase {
    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        this.viewModel = new AlbumDetailsActivityViewModel();
    }

    public void onCreateContentView() {
        setContentView(R.layout.album_details_activity);
        setAppBarLayout(R.layout.appbar_refresh, false, true);
    }

    protected String getActivityName() {
        return "AlbumDetail";
    }

    protected String getChannelName() {
        return ActivityBase.detailsChannel;
    }
}
