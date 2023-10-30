package com.microsoft.xbox.xle.app.activity;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.xle.viewmodel.CollectionGalleryActivityViewModel;

public class CollecitonGalleryActivity extends ActivityBase {
    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        this.viewModel = new CollectionGalleryActivityViewModel();
    }

    public void onCreateContentView() {
        setContentView(R.layout.collection_gallery_activity);
        setAppBarLayout(R.layout.appbar_searchrefresh, false, false);
    }

    protected boolean shouldTrackPageVisit() {
        return false;
    }

    protected String getActivityName() {
        return "CollectionGallery";
    }

    protected String getChannelName() {
        return ActivityBase.homeChannel;
    }
}
