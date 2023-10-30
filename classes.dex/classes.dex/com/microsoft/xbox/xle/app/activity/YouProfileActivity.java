package com.microsoft.xbox.xle.app.activity;

import android.content.Context;
import android.util.AttributeSet;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.xle.viewmodel.XLEGlobalData;
import com.microsoft.xbox.xle.viewmodel.YouProfileActivityViewModel;

public class YouProfileActivity extends ActivityBase {
    public YouProfileActivity(Context context, AttributeSet attrs) {
    }

    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        this.viewModel = new YouProfileActivityViewModel(XLEGlobalData.getInstance().getSelectedGamertag());
    }

    public void onCreateContentView() {
        setContentView(R.layout.you_profile_activity);
        setAppBarLayout(R.layout.you_profile_appbar, false, false);
    }

    protected boolean shouldTrackPageVisit() {
        return false;
    }

    protected String getActivityName() {
        return "YouProfile";
    }

    protected String getChannelName() {
        return ActivityBase.socialChannel;
    }
}
