package com.microsoft.xbox.xle.app.activity;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.xle.viewmodel.SearchResultsActivityViewModel;

public class SearchResultsActivity extends ActivityBase {
    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        this.viewModel = new SearchResultsActivityViewModel();
    }

    public void onCreateContentView() {
        setContentView(R.layout.search_results_activity);
        setAppBarLayout(-1, false, false);
    }

    protected String getActivityName() {
        return ActivityBase.searchResultsChannel;
    }

    protected String getChannelName() {
        return ActivityBase.searchResultsChannel;
    }
}
