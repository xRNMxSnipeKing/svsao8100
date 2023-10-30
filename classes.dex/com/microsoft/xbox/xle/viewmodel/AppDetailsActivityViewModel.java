package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.NowPlayingGlobalModel;
import com.microsoft.xbox.service.model.edsv2.EDSV2AppDetailModel;
import com.microsoft.xbox.xle.app.adapter.AdapterFactory;

public class AppDetailsActivityViewModel extends EDSV2MediaItemDetailViewModel<EDSV2AppDetailModel> {
    public AppDetailsActivityViewModel() {
        this.adapter = AdapterFactory.getInstance().getAppDetailsAdapter(this);
    }

    public void onRehydrate() {
        this.adapter = AdapterFactory.getInstance().getAppDetailsAdapter(this);
    }

    protected int getErrorStringResourceId() {
        return R.string.toast_app_details_list_error;
    }

    public boolean shouldShowMediaProgressBar() {
        return NowPlayingGlobalModel.getInstance().isAppPlayingMedia(getTitleId());
    }

    public boolean shouldShowProviderButtons() {
        return !NowPlayingGlobalModel.getInstance().isAppNowPlaying(getTitleId());
    }

    protected boolean shouldAddActivitiesPane() {
        return getHasActivities();
    }
}
