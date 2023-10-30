package com.microsoft.xbox.xle.app.activity;

import android.content.Context;
import android.util.AttributeSet;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.xle.viewmodel.XLEGlobalData;
import com.microsoft.xbox.xle.viewmodel.YouBioActivityViewModel;

public class YouBioActivity extends ActivityBase {
    public YouBioActivity(Context context, AttributeSet attrs) {
    }

    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        this.viewModel = new YouBioActivityViewModel(XLEGlobalData.getInstance().getSelectedGamertag());
    }

    public void onCreateContentView() {
        setContentView(R.layout.you_bio_activity);
        setAppBarLayout(R.layout.appbar_refresh, false, false);
    }

    protected boolean shouldTrackPageVisit() {
        return false;
    }

    protected String getActivityName() {
        return "YouBio";
    }

    protected String getChannelName() {
        return ActivityBase.socialChannel;
    }
}
