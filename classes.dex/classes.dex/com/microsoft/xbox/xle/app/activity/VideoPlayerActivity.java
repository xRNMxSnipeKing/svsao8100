package com.microsoft.xbox.xle.app.activity;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.xle.viewmodel.VideoPlayerActivityViewModel;

public class VideoPlayerActivity extends ActivityBase {
    public VideoPlayerActivity() {
        super(0);
    }

    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        this.viewModel = new VideoPlayerActivityViewModel();
    }

    public void onCreateContentView() {
        setContentView(R.layout.videoplayer_activity);
        setAppBarLayout(-1, true, false);
    }

    protected String getActivityName() {
        return "Video";
    }

    protected String getChannelName() {
        return ActivityBase.discoverChannel;
    }
}
