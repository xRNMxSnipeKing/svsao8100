package com.microsoft.xbox.xle.app.activity;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.xle.viewmodel.SearchFilterActivityViewModel;

public class SearchFilterActivity extends ActivityBase {
    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        this.viewModel = new SearchFilterActivityViewModel();
    }

    public void onCreateContentView() {
        setContentView(R.layout.search_filters_activity);
        setAppBarLayout(-1, true, false);
    }

    protected String getActivityName() {
        return ActivityBase.searchFilterChannel;
    }

    protected String getChannelName() {
        return ActivityBase.searchFilterChannel;
    }
}
