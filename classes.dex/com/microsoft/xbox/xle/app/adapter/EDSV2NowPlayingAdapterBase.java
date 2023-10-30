package com.microsoft.xbox.xle.app.adapter;

import android.view.View;
import com.microsoft.xbox.toolkit.MediaProgressTimer.OnMediaProgressUpdatedListener;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.network.ListState;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.ui.DetailsProviderView2;
import com.microsoft.xbox.xle.ui.MediaProgressBar;
import com.microsoft.xbox.xle.viewmodel.AdapterBaseNormal;
import com.microsoft.xbox.xle.viewmodel.EDSV2MediaItemDetailViewModel;

public abstract class EDSV2NowPlayingAdapterBase<T extends EDSV2MediaItemDetailViewModel> extends AdapterBaseNormal {
    private boolean isProgressTimerSetup;
    protected MediaProgressBar mediaProgressBar;
    protected DetailsProviderView2 providersView2;
    protected View smartGlassEnabled;
    protected T viewModel;

    protected void setMediaProgressBar(MediaProgressBar mediaProgressBar) {
        this.mediaProgressBar = mediaProgressBar;
        boolean z = XLEApplication.Instance.getIsTablet() || this.mediaProgressBar != null;
        XLEAssert.assertTrue(z);
    }

    protected void setDetailsProviderView(DetailsProviderView2 providersView2) {
        this.providersView2 = providersView2;
        XLEAssert.assertTrue(this.providersView2 != null);
    }

    public void updateViewOverride() {
        int i = 0;
        if (this.viewModel.getViewModelState() == ListState.ValidContentState) {
            boolean z;
            if (this.viewModel.shouldShowMediaProgressBar() && this.viewModel.isMediaInProgress()) {
                hookMediaProgressUpdateEvent();
            } else if (this.mediaProgressBar != null) {
                this.mediaProgressBar.setVisibility(8);
                this.viewModel.setOnMediaProgressUpdatedListener(null);
                this.isProgressTimerSetup = false;
            }
            setAppBarMediaButtonVisibility(this.viewModel.shouldShowMediaTransportControls());
            if (!this.viewModel.shouldShowProviderButtons()) {
                this.providersView2.setVisibility(8);
            } else if (this.viewModel.getProviders() != null && this.viewModel.getProviders().size() > 0) {
                this.providersView2.setProviders(this.viewModel.getProviders(), this.viewModel.getMediaType());
                this.providersView2.setVisibility(0);
            }
            if (this.smartGlassEnabled != null) {
                z = true;
            } else {
                z = false;
            }
            XLEAssert.assertTrue(z);
            View view = this.smartGlassEnabled;
            if (!this.viewModel.getHasActivities()) {
                i = 8;
            }
            view.setVisibility(i);
        }
    }

    public void onPause() {
        if (this.mediaProgressBar != null) {
            this.viewModel.setOnMediaProgressUpdatedListener(null);
        }
        this.isProgressTimerSetup = false;
        super.onPause();
    }

    private void hookMediaProgressUpdateEvent() {
        if (this.mediaProgressBar != null) {
            this.mediaProgressBar.setVisibility(0);
            if (!this.isProgressTimerSetup) {
                this.isProgressTimerSetup = true;
                this.viewModel.setOnMediaProgressUpdatedListener(new OnMediaProgressUpdatedListener() {
                    public void onUpdate(long positionInSeconds, long durationInSeconds) {
                        if (EDSV2NowPlayingAdapterBase.this.getIsStarted()) {
                            EDSV2NowPlayingAdapterBase.this.mediaProgressBar.initialize(positionInSeconds, durationInSeconds);
                        }
                    }
                });
            }
        }
    }
}
