package com.microsoft.xbox.xle.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.ui.CustomTypefaceTextView;

public class MediaProgressBar extends RelativeLayout {
    private static final String DEFAULT_TIME_STRING = "00:00";
    private static final int MAX_PROGRESS = 100;
    private CustomTypefaceTextView currentPositionView;
    private long currentPostionSeconds;
    private long durationSeconds;
    private ProgressBar mediaBar;
    private View mediaProgressBarLayout;
    private CustomTypefaceTextView totalTimeView;

    public interface OnChangePostionListener {
        void changePostion(long j);
    }

    public MediaProgressBar(Context context) {
        super(context);
        init(context, null);
    }

    public MediaProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MediaProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public void init(Context context, AttributeSet attrs) {
        int i = 0;
        ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R.layout.media_progress_bar, this, true);
        this.mediaProgressBarLayout = findViewById(R.id.media_progress_bar_layout);
        this.mediaBar = (ProgressBar) findViewById(R.id.media_progress_bar);
        this.mediaBar.setMax(MAX_PROGRESS);
        this.currentPositionView = (CustomTypefaceTextView) findViewById(R.id.media_bar_current_postion);
        this.totalTimeView = (CustomTypefaceTextView) findViewById(R.id.media_bar_total_time);
        this.currentPositionView.setText(DEFAULT_TIME_STRING);
        this.totalTimeView.setText(DEFAULT_TIME_STRING);
        if (attrs != null) {
            int i2;
            boolean showProgressText = context.obtainStyledAttributes(attrs, R.styleable.MediaProgressBar).getBoolean(0, true);
            CustomTypefaceTextView customTypefaceTextView = this.currentPositionView;
            if (showProgressText) {
                i2 = 0;
            } else {
                i2 = 8;
            }
            customTypefaceTextView.setVisibility(i2);
            CustomTypefaceTextView customTypefaceTextView2 = this.totalTimeView;
            if (!showProgressText) {
                i = 8;
            }
            customTypefaceTextView2.setVisibility(i);
        }
    }

    public void initialize(long currentPostionInSeconds, long durationInSeconds) {
        if (durationInSeconds <= 0) {
            this.mediaProgressBarLayout.setVisibility(8);
            return;
        }
        this.mediaProgressBarLayout.setVisibility(0);
        this.durationSeconds = durationInSeconds;
        this.totalTimeView.setText(getTimeString(this.durationSeconds));
        setCurrentPostion(currentPostionInSeconds);
    }

    public void setPositionInSeconds(long currentPostionInSeconds) {
        if (this.durationSeconds > 0 && this.currentPostionSeconds != currentPostionInSeconds) {
            setCurrentPostion(currentPostionInSeconds);
        }
    }

    private void setCurrentPostion(long currentPostion) {
        this.currentPostionSeconds = currentPostion;
        if (this.currentPostionSeconds == 0) {
            this.currentPositionView.setText(DEFAULT_TIME_STRING);
        } else {
            this.currentPositionView.setText(getTimeString(this.currentPostionSeconds));
        }
        if (this.durationSeconds == 0) {
            this.mediaBar.setProgress(0);
            return;
        }
        this.mediaBar.setProgress((int) ((double) ((this.currentPostionSeconds * 100) / this.durationSeconds)));
    }

    private String getTimeString(long time) {
        return JavaUtil.getTimeStringMMSS(time);
    }
}
