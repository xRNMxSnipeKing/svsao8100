package com.microsoft.xbox.xle.app.adapter;

import android.view.View;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.MediaProgressTimer.OnMediaProgressUpdatedListener;
import com.microsoft.xbox.toolkit.ui.XLEUniformImageView;
import com.microsoft.xbox.xle.ui.MediaProgressBar;
import com.microsoft.xbox.xle.viewmodel.AdapterBaseNormal;
import com.microsoft.xbox.xle.viewmodel.DashDetailsActivityViewModel;

public class DashDetailsActivityAdapter extends AdapterBaseNormal {
    protected MediaProgressBar mediaProgressBar;
    private XLEUniformImageView tileImageView;
    private DashDetailsActivityViewModel viewModel;

    public DashDetailsActivityAdapter(DashDetailsActivityViewModel vm) {
        this.viewModel = vm;
        this.screenBody = findViewById(R.id.dash_details_activity_body);
        View view = findViewById(R.id.dash_details_progress_bar);
        if (view != null) {
            this.mediaProgressBar = (MediaProgressBar) view;
        }
        this.tileImageView = (XLEUniformImageView) findViewById(R.id.dash_details_tile_image);
    }

    public void updateViewOverride() {
        this.tileImageView.setImageURI2(null, R.drawable.dash_now_playing_tile);
        if (this.viewModel.shouldShowMediaProgressBar()) {
            hookMediaProgressUpdateEvent();
        } else if (this.mediaProgressBar != null) {
            this.mediaProgressBar.setVisibility(8);
            this.viewModel.setOnMediaProgressUpdatedListener(null);
        }
        setAppBarMediaButtonVisibility(this.viewModel.shouldShowMediaTransportControls());
    }

    private void hookMediaProgressUpdateEvent() {
        if (this.mediaProgressBar != null) {
            this.mediaProgressBar.setVisibility(0);
            this.viewModel.setOnMediaProgressUpdatedListener(new OnMediaProgressUpdatedListener() {
                public void onUpdate(long positionInSeconds, long durationInSeconds) {
                    if (DashDetailsActivityAdapter.this.getIsStarted()) {
                        DashDetailsActivityAdapter.this.mediaProgressBar.initialize(positionInSeconds, durationInSeconds);
                    }
                }
            });
        }
    }

    public void onPause() {
        if (this.mediaProgressBar != null) {
            this.viewModel.setOnMediaProgressUpdatedListener(null);
        }
        super.onPause();
    }
}
