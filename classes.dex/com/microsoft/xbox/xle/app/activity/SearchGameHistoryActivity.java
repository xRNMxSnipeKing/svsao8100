package com.microsoft.xbox.xle.app.activity;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.xle.viewmodel.SearchGameHistoryActivityViewModel;

public class SearchGameHistoryActivity extends ActivityBase {
    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        this.viewModel = new SearchGameHistoryActivityViewModel();
    }

    public void onCreateContentView() {
        setContentView(R.layout.search_game_history_activity);
        setAppBarLayout(-1, true, false);
    }

    protected String getActivityName() {
        return "SearchGameHistory";
    }

    protected String getChannelName() {
        return ActivityBase.gamesChannel;
    }
}
