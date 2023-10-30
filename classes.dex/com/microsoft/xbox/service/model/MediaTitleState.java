package com.microsoft.xbox.service.model;

import com.microsoft.xbox.toolkit.XBLSharedUtil;

public class MediaTitleState {
    public static final int TICKS_IN_MILLISECOND = 1000000;
    public static final int TransportState_Buffering = 5;
    public static final int TransportState_Invalid = 0;
    public static final int TransportState_NoMedia = -1;
    public static final int TransportState_Paused = 4;
    public static final int TransportState_Playing = 3;
    public static final int TransportState_Starting = 2;
    public static final int TransportState_Stopped = 1;
    public long Duration;
    public long MaxSeek;
    public String MediaAssetId;
    public long MinSeek;
    public long Position;
    public float Rate;
    public long TitleId;
    public int TransportCapabilities;
    public int TransportState;

    public long getTitleId() {
        return this.TitleId;
    }

    public void setTitleId(long titleId) {
        this.TitleId = titleId;
    }

    public long getDuration() {
        return this.Duration;
    }

    public long getDurationInSeconds() {
        return XBLSharedUtil.hundredNanosecondsToSeconds(getDuration());
    }

    public void setDuration(long duration) {
        this.Duration = duration;
    }

    public long getPosition() {
        return this.Position;
    }

    public long getPositionInSeconds() {
        return XBLSharedUtil.hundredNanosecondsToSeconds(getPosition());
    }

    public void setPosition(long position) {
        this.Position = position;
    }

    public long getMinSeek() {
        return this.MinSeek;
    }

    public void setMinSeek(long minSeek) {
        this.MinSeek = minSeek;
    }

    public long getMaxSeek() {
        return this.MaxSeek;
    }

    public void setMaxSeek(long maxSeek) {
        this.MaxSeek = maxSeek;
    }

    public float getRate() {
        return this.Rate;
    }

    public void setRate(float rate) {
        this.Rate = rate;
    }

    public int getTransportState() {
        return this.TransportState;
    }

    public void setTransportState(int transportState) {
        this.TransportState = transportState;
    }

    public int getTransportCapabilities() {
        return this.TransportCapabilities;
    }

    public void setTransportCapabilities(int transportCapabilities) {
        this.TransportCapabilities = transportCapabilities;
    }

    public String getMediaAssetId() {
        return this.MediaAssetId;
    }

    public void setMediaAssetId(String mediaAssetId) {
        this.MediaAssetId = mediaAssetId;
    }

    public boolean isMediaInProgress() {
        switch (this.TransportState) {
            case -1:
            case 0:
            case 1:
                return false;
            default:
                return this.Duration == 0 || this.Duration - this.Position > 1000000;
        }
    }
}
