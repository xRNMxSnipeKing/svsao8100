package com.microsoft.xbox.xle.app.activity;

import android.content.Context;
import android.util.AttributeSet;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.xle.viewmodel.CollectionActivityViewModel;

public class CollectionActivity extends ActivityBase {
    public CollectionActivity(Context context, AttributeSet attrs) {
    }

    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        this.viewModel = new CollectionActivityViewModel();
    }

    public void onCreateContentView() {
        setContentView(R.layout.collection_activity);
        setAppBarLayout(R.layout.appbar_searchrefresh, false, false);
    }

    protected boolean shouldTrackPageVisit() {
        return false;
    }

    protected String getActivityName() {
        return "Collection";
    }

    protected String getChannelName() {
        return ActivityBase.homeChannel;
    }
}
