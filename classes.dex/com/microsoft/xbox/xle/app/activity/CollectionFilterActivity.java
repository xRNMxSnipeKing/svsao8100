package com.microsoft.xbox.xle.app.activity;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.xle.viewmodel.CollectionFilterActivityViewModel;

public class CollectionFilterActivity extends ActivityBase {
    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        this.viewModel = new CollectionFilterActivityViewModel();
    }

    public void onCreateContentView() {
        setContentView(R.layout.search_filters_activity);
        setAppBarLayout(-1, true, false);
    }

    protected String getActivityName() {
        return "CollectionFilter";
    }

    protected String getChannelName() {
        return ActivityBase.homeChannel;
    }
}
