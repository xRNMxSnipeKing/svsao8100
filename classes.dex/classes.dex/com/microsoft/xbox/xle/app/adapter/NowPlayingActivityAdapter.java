package com.microsoft.xbox.xle.app.adapter;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.MediaProgressTimer.OnMediaProgressUpdatedListener;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.toolkit.ui.appbar.AppBarMenuButton;
import com.microsoft.xbox.xle.viewmodel.AdapterBaseNormal;
import com.microsoft.xbox.xle.viewmodel.NowPlayingActivityViewModel;
import java.util.ArrayList;
import java.util.List;

public class NowPlayingActivityAdapter extends AdapterBaseNormal {
    private boolean isProgressTimerSetup;
    private TextView nowPlayingProgress;
    private NowPlayingActivityViewModel viewModel;

    public NowPlayingActivityAdapter(NowPlayingActivityViewModel nowPlayingActivityViewModel) {
        this.screenBody = findViewById(R.id.nowplaying_activity_body);
        this.content = findViewById(R.id.nowplaying_phone_module);
        if (this.content == null) {
            this.content = findViewById(R.id.nowplaying_tablet_module);
        }
        this.viewModel = nowPlayingActivityViewModel;
        this.nowPlayingProgress = (TextView) findViewById(R.id.now_playing_progress);
        this.isProgressTimerSetup = false;
        findAndInitializeModuleById(R.id.nowplaying_phone_module, nowPlayingActivityViewModel);
        findAndInitializeModuleById(R.id.nowplaying_tablet_module, nowPlayingActivityViewModel);
    }

    protected void onAppBarUpdated() {
        setAppBarButtonClickListener(R.id.appbar_refresh, new OnClickListener() {
            public void onClick(View arg0) {
                NowPlayingActivityAdapter.this.viewModel.load(true);
            }
        });
    }

    public void updateViewOverride() {
        updateLoadingIndicator(this.viewModel.isBusy());
        setCancelableBlocking(this.viewModel.isBlockingBusy(), XboxApplication.Resources.getString(R.string.loading), new Runnable() {
            public void run() {
                NowPlayingActivityAdapter.this.viewModel.cancelLaunch();
            }
        });
        if (this.nowPlayingProgress == null) {
            return;
        }
        if (this.viewModel.shouldShowMediaProgress()) {
            setAppBarMediaButtonVisibility(true);
            setupMediaUpdateListener();
            return;
        }
        this.nowPlayingProgress.setText(null);
        setAppBarMediaButtonVisibility(false);
        clearMediaUpdateListner();
    }

    protected List<AppBarMenuButton> getTestMenuButtons() {
        List<AppBarMenuButton> res = super.getTestMenuButtons();
        if (res == null) {
            return new ArrayList();
        }
        return res;
    }

    public void onPause() {
        clearMediaUpdateListner();
        super.onPause();
    }

    private void setupMediaUpdateListener() {
        if (!this.isProgressTimerSetup) {
            this.isProgressTimerSetup = true;
            this.viewModel.setOnMediaProgressUpdatedListener(new OnMediaProgressUpdatedListener() {
                public void onUpdate(long positionInSeconds, long durationInSeconds) {
                    if (NowPlayingActivityAdapter.this.getIsStarted()) {
                        String positionString = JavaUtil.getTimeStringMMSS(positionInSeconds);
                        String durationString = JavaUtil.getTimeStringMMSS(durationInSeconds);
                        if (NowPlayingActivityAdapter.this.nowPlayingProgress != null) {
                            NowPlayingActivityAdapter.this.nowPlayingProgress.setText(String.format("%s / %s", new Object[]{positionString, durationString}));
                        }
                    }
                }
            });
        }
    }

    private void clearMediaUpdateListner() {
        if (this.nowPlayingProgress != null) {
            this.viewModel.setOnMediaProgressUpdatedListener(null);
        }
        this.isProgressTimerSetup = false;
    }
}
