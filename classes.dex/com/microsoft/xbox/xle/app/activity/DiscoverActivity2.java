package com.microsoft.xbox.xle.app.activity;

import android.content.Context;
import android.util.AttributeSet;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.xle.viewmodel.DiscoverActivityViewModel2;

public class DiscoverActivity2 extends ActivityBase {
    public DiscoverActivity2(Context context, AttributeSet attrs) {
    }

    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        this.viewModel = new DiscoverActivityViewModel2();
    }

    public void onCreateContentView() {
        setContentView(R.layout.discover_activity2);
        setAppBarLayout(R.layout.appbar_refresh, false, false);
    }

    protected boolean shouldTrackPageVisit() {
        return false;
    }

    protected String getActivityName() {
        return ActivityBase.discoverChannel;
    }

    protected String getChannelName() {
        return ActivityBase.discoverChannel;
    }
}
