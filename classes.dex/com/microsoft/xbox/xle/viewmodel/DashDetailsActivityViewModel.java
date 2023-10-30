package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.xbox.service.model.NowPlayingGlobalModel;
import com.microsoft.xbox.service.model.NowPlayingGlobalModel.NowPlayingState;
import com.microsoft.xbox.service.model.UpdateData;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.MediaProgressTimer.OnMediaProgressUpdatedListener;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.app.adapter.DashDetailsActivityAdapter;

public class DashDetailsActivityViewModel extends ViewModelBase {
    private boolean wasNowPlaying;

    public DashDetailsActivityViewModel() {
        this.wasNowPlaying = false;
        this.adapter = new DashDetailsActivityAdapter(this);
    }

    public void onRehydrate() {
        this.adapter = new DashDetailsActivityAdapter(this);
    }

    public boolean shouldShowMediaProgressBar() {
        return NowPlayingGlobalModel.getInstance().getNowPlayingState() == NowPlayingState.ConnectedPlayingDashMedia;
    }

    public boolean shouldShowMediaTransportControls() {
        if (XLEApplication.Instance.getIsTablet()) {
            return NowPlayingGlobalModel.getInstance().isMediaInProgress();
        }
        return shouldShowMediaProgressBar() || (this.wasNowPlaying && NowPlayingGlobalModel.getInstance().isMediaInProgress());
    }

    public void setOnMediaProgressUpdatedListener(OnMediaProgressUpdatedListener listener) {
        NowPlayingGlobalModel.getInstance().setOnMediaProgressUpdatedRunnable(listener);
    }

    protected void onStartOverride() {
        NowPlayingGlobalModel.getInstance().addObserver(this);
    }

    protected void onStopOverride() {
        NowPlayingGlobalModel.getInstance().removeObserver(this);
        this.wasNowPlaying = false;
    }

    public boolean isBusy() {
        return false;
    }

    public void load(boolean forceRefresh) {
    }

    protected void updateOverride(AsyncResult<UpdateData> asyncResult) {
        if (NowPlayingGlobalModel.getInstance().getNowPlayingState() == NowPlayingState.ConnectedPlayingDashMedia) {
            this.wasNowPlaying = true;
        }
        this.adapter.updateView();
    }
}
