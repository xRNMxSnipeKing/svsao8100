package com.microsoft.xbox.xle.app.activity;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.xle.viewmodel.SearchGamerActivityViewModel;

public class SearchGamerActivity extends ActivityBase {
    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        this.viewModel = new SearchGamerActivityViewModel();
    }

    public void onCreateContentView() {
        setContentView(R.layout.search_gamer_activity);
        setAppBarLayout(R.layout.search_gamer_appbar, false, false);
    }

    protected String getActivityName() {
        return "SearchGamer";
    }

    protected String getChannelName() {
        return ActivityBase.socialChannel;
    }
}
