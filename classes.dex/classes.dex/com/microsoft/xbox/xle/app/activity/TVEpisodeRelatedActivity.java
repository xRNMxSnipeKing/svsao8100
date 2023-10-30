package com.microsoft.xbox.xle.app.activity;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.xle.viewmodel.TVEpisodeRelatedActivityViewModel;

public class TVEpisodeRelatedActivity extends ActivityBase {
    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        this.viewModel = new TVEpisodeRelatedActivityViewModel();
    }

    public void onCreateContentView() {
        setContentView(R.layout.details_related_activity);
        setAppBarLayout(R.layout.appbar_refresh, false, false);
    }

    protected String getActivityName() {
        return "TVRelated";
    }

    protected String getChannelName() {
        return null;
    }
}
