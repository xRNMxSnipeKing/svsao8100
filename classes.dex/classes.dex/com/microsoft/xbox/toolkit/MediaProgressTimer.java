package com.microsoft.xbox.toolkit;

import java.util.Timer;
import java.util.TimerTask;

public class MediaProgressTimer {
    private static final int TIMER_INTERVAL_MS = 1000;
    private long currentDuration;
    private long currentPosition;
    private long currentRate;
    private OnMediaProgressUpdatedListener onMediaProgressUpdatedListener;
    private Timer progressTimer;

    public static abstract class OnMediaProgressUpdatedListener {
        public abstract void onUpdate(long j, long j2);
    }

    private class ProgressTask extends TimerTask {
        private ProgressTask() {
        }

        public void run() {
            ThreadManager.UIThreadPost(new Runnable() {
                public void run() {
                    MediaProgressTimer.this.onTimerTick();
                }
            });
        }
    }

    public void update(long position, long duration, float rate) {
        this.currentPosition = Math.min(position, duration);
        this.currentDuration = duration;
        this.currentRate = XBLSharedUtil.secondsToHundredNanoseconds(rate);
    }

    public void start() {
        if (this.progressTimer != null) {
            this.progressTimer.cancel();
        }
        this.progressTimer = new Timer();
        XLELog.Warning("MediaProgressTimer", "created ");
        if (this.currentRate != 0) {
            this.progressTimer.schedule(new ProgressTask(), 1000, 1000);
        }
        if (this.onMediaProgressUpdatedListener != null && getDurationInSeconds() > 0) {
            this.onMediaProgressUpdatedListener.onUpdate(getPositionInSeconds(), getDurationInSeconds());
        }
    }

    public void stop() {
        if (this.progressTimer != null) {
            this.progressTimer.cancel();
            XLELog.Warning("MediaProgressTimer", "cancelled");
            this.progressTimer = null;
        }
    }

    public void setOnPositionUpdatedRunnable(OnMediaProgressUpdatedListener listener) {
        this.onMediaProgressUpdatedListener = listener;
    }

    public long getPositionInSeconds() {
        return XBLSharedUtil.hundredNanosecondsToSeconds(this.currentPosition);
    }

    public long getDurationInSeconds() {
        return XBLSharedUtil.hundredNanosecondsToSeconds(this.currentDuration);
    }

    private void onTimerTick() {
        XLEAssert.assertTrue(ThreadManager.UIThread == Thread.currentThread());
        this.currentPosition += this.currentRate;
        if (this.currentPosition > this.currentDuration) {
            this.currentPosition = this.currentDuration;
            XLELog.Warning("MediaProgressTimer", "position exceeded, stop");
            stop();
        }
        if (this.onMediaProgressUpdatedListener != null) {
            this.onMediaProgressUpdatedListener.onUpdate(getPositionInSeconds(), getDurationInSeconds());
        }
    }
}
