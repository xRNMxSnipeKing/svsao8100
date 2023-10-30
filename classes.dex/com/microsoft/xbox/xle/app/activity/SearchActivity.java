package com.microsoft.xbox.xle.app.activity;

import android.content.Context;
import android.util.AttributeSet;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.xle.viewmodel.SearchActivityViewModel;

public class SearchActivity extends ActivityBase {
    public SearchActivity(Context context, AttributeSet attrs) {
    }

    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        this.viewModel = new SearchActivityViewModel();
    }

    public void onCreateContentView() {
        setContentView(R.layout.search_activity);
        setAppBarLayout(-1, false, false);
    }

    protected boolean shouldTrackPageVisit() {
        return false;
    }

    protected String getActivityName() {
        return ActivityBase.searchChannel;
    }

    protected String getChannelName() {
        return ActivityBase.searchChannel;
    }
}
