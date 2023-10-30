package com.microsoft.xbox.xle.app.activity;

import android.content.Context;
import android.util.AttributeSet;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.xle.viewmodel.NowPlayingActivityViewModel;

public class NowPlayingActivity extends ActivityBase {
    public NowPlayingActivity(Context context, AttributeSet attrs) {
    }

    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        this.viewModel = new NowPlayingActivityViewModel();
    }

    public void onCreateContentView() {
        setContentView(R.layout.nowplaying_activity);
        setAppBarLayout(R.layout.appbar_refresh, false, true);
    }

    protected String getActivityName() {
        return ActivityBase.nowPlayingChannel;
    }

    protected String getChannelName() {
        return ActivityBase.nowPlayingChannel;
    }
}
