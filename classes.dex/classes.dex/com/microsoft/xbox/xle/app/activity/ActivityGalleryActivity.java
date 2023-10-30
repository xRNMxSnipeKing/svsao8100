package com.microsoft.xbox.xle.app.activity;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.xle.viewmodel.ActivityGalleryActivityViewModel;

public class ActivityGalleryActivity extends ActivityBase {
    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        this.viewModel = new ActivityGalleryActivityViewModel();
    }

    public void onCreateContentView() {
        setContentView(R.layout.activity_gallery_activity);
        setAppBarLayout(R.layout.appbar_refresh, false, false);
    }

    protected String getActivityName() {
        return "Gallery";
    }

    protected String getChannelName() {
        return "SGActivity";
    }
}
