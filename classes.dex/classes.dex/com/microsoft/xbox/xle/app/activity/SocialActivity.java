package com.microsoft.xbox.xle.app.activity;

import android.content.Context;
import android.util.AttributeSet;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.xle.viewmodel.SocialActivityViewModel;

public class SocialActivity extends ActivityBase {
    public SocialActivity(Context context, AttributeSet attrs) {
        super(7, true);
    }

    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        this.viewModel = new SocialActivityViewModel();
    }

    public void onCreateContentView() {
        setContentView(R.layout.social_activity);
        setAppBarLayout(R.layout.social_appbar, false, false);
    }

    protected boolean shouldTrackPageVisit() {
        return false;
    }

    protected String getActivityName() {
        return ActivityBase.profileChannel;
    }

    protected String getChannelName() {
        return ActivityBase.homeChannel;
    }
}
