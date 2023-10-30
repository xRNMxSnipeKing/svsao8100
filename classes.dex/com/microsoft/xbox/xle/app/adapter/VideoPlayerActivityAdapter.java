package com.microsoft.xbox.xle.app.adapter;

import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.widget.MediaController;
import android.widget.VideoView;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.xle.viewmodel.AdapterBaseNormal;
import com.microsoft.xbox.xle.viewmodel.VideoPlayerActivityViewModel;
import java.util.Timer;
import java.util.TimerTask;

public class VideoPlayerActivityAdapter extends AdapterBaseNormal {
    private SurfaceHolderCallback callback;
    private MediaController controlWidget;
    private Timer playStatePullTimer;
    private SurfaceHolder surfaceHolder;
    private boolean surfaceReady;
    private VideoView videoView;
    private VideoPlayerActivityViewModel viewModel;

    private class ProgressTask extends TimerTask {
        private ProgressTask() {
        }

        public void run() {
            ThreadManager.UIThreadPost(new Runnable() {
                public void run() {
                    VideoPlayerActivityAdapter.this.onTimerTick();
                }
            });
        }
    }

    private class SurfaceHolderCallback implements Callback {
        private SurfaceHolderCallback() {
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }

        public void surfaceCreated(SurfaceHolder holder) {
            XLELog.Diagnostic("VideoPlayerActivityAdapter", "VideoSurface created");
            VideoPlayerActivityAdapter.this.initializeVideoView();
            VideoPlayerActivityAdapter.this.surfaceReady = true;
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            XLELog.Diagnostic("VideoPlayerActivityAdapter", "VideoSurface destroyed");
            VideoPlayerActivityAdapter.this.surfaceReady = false;
        }
    }

    public VideoPlayerActivityAdapter(VideoPlayerActivityViewModel playerViewModel) {
        this.surfaceReady = false;
        this.screenBody = findViewById(R.id.videoplayer_activity_body);
        this.viewModel = playerViewModel;
        this.videoView = (VideoView) findViewById(R.id.video_surface);
        this.surfaceReady = false;
    }

    public void updateViewOverride() {
        if (this.viewModel.getVideoReady()) {
            startVideoPlayback();
        }
    }

    public void onResume() {
        XLELog.Diagnostic("VideoPlayerActivityAdapter", "onResume called, register callback, create surface, initialize");
        this.callback = new SurfaceHolderCallback();
        this.surfaceHolder = this.videoView.getHolder();
        this.surfaceHolder.addCallback(this.callback);
        if (this.surfaceReady) {
            initializeVideoView();
        }
    }

    public void onPause() {
        XLELog.Diagnostic("VideoPlayerActivityAdapter", "onPause called, remove callback");
        this.videoView.stopPlayback();
        this.videoView.setOnPreparedListener(null);
        this.videoView.setOnCompletionListener(null);
        this.videoView.setOnErrorListener(null);
        if (this.playStatePullTimer != null) {
            this.playStatePullTimer.cancel();
            this.playStatePullTimer = null;
        }
        super.onPause();
    }

    public void onSetActive() {
        super.onSetActive();
        updateLoadingIndicator(true);
    }

    public void onStop() {
        XLELog.Diagnostic("VideoPlayerActivityAdapter", "onStop called, stop playing back");
        this.surfaceHolder.removeCallback(this.callback);
        super.onStop();
    }

    private void initializeVideoView() {
        this.controlWidget = new MediaController(XboxApplication.MainActivity);
        this.controlWidget.setMediaPlayer(this.videoView);
        this.videoView.setMediaController(this.controlWidget);
        this.videoView.requestFocus();
        this.videoView.setOnPreparedListener(this.viewModel);
        this.videoView.setOnCompletionListener(this.viewModel);
        this.videoView.setOnErrorListener(this.viewModel);
        this.videoView.setVideoPath(this.viewModel.getDataSource());
    }

    private void startVideoPlayback() {
        boolean z = false;
        XLELog.Info("VideoPlayerActivityAdapter", "startVideoPlayback");
        this.controlWidget.show(0);
        this.videoView.start();
        if (this.playStatePullTimer == null) {
            z = true;
        }
        XLEAssert.assertTrue(z);
        this.playStatePullTimer = new Timer();
        this.playStatePullTimer.schedule(new ProgressTask(), 1000, 1000);
    }

    private void onTimerTick() {
        if (this.videoView != null && this.videoView.getCurrentPosition() > 0) {
            this.controlWidget.hide();
            updateLoadingIndicator(false);
            if (this.playStatePullTimer != null) {
                this.playStatePullTimer.cancel();
                this.playStatePullTimer = null;
            }
        }
    }
}
