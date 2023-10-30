package com.microsoft.xbox.xle.viewmodel;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.UpdateData;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.app.adapter.VideoPlayerActivityAdapter;

public class VideoPlayerActivityViewModel extends ViewModelBase implements OnCompletionListener, OnPreparedListener, OnErrorListener {
    private String dataSource = XLEGlobalData.getInstance().getSelectedDataSource();
    private boolean videoReady;

    public VideoPlayerActivityViewModel() {
        XLEAssert.assertTrue(this.dataSource != null);
        this.adapter = new VideoPlayerActivityAdapter(this);
    }

    public void onRehydrate() {
        this.adapter = new VideoPlayerActivityAdapter(this);
    }

    public String getDataSource() {
        return this.dataSource;
    }

    public boolean getVideoReady() {
        return this.videoReady;
    }

    public void updateOverride(AsyncResult<UpdateData> asyncResult) {
        this.adapter.updateView();
    }

    public void onPause() {
        this.videoReady = false;
        super.onPause();
    }

    public void load(boolean forceRefresh) {
    }

    protected void onStartOverride() {
    }

    protected void onStopOverride() {
    }

    public boolean isBusy() {
        return !this.videoReady;
    }

    public void onPrepared(MediaPlayer arg0) {
        XLELog.Diagnostic("VideoPlayerActivityVideoModel", "onPrepared called");
        this.videoReady = true;
        this.adapter.updateView();
    }

    public void onCompletion(MediaPlayer arg0) {
        XLELog.Diagnostic("VideoPlayerActivityViewModel", "onCompletion called");
        goBack();
    }

    public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
        XLELog.Diagnostic("VideoPlayerActivityViewModel", "onError called");
        showMustActDialog(XLEApplication.Resources.getString(R.string.error), XLEApplication.Resources.getString(R.string.dialog_cannot_play_video), XLEApplication.Resources.getString(R.string.OK), new Runnable() {
            public void run() {
                VideoPlayerActivityViewModel.this.goBack();
            }
        }, true);
        return true;
    }
}
