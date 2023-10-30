package com.microsoft.xbox.service.model.edsv2;

import com.microsoft.xbox.service.model.JTitleType;
import com.microsoft.xbox.service.model.LaunchType;

public class EDSV2MovieDetailModel extends EDSV2MediaItemDetailModel<EDSV2MovieMediaItem, EDSV2MovieMediaItem> {
    protected EDSV2MovieDetailModel(EDSV2MediaItem mediaItem) {
        super(mediaItem);
    }

    protected EDSV2MovieMediaItem createMediaItem(EDSV2MediaItem mediaItem) {
        return new EDSV2MovieMediaItem(mediaItem);
    }

    public int getMediaGroup() {
        if (getMediaType() == EDSV2MediaType.MEDIATYPE_MUSICVIDEO) {
            return 5;
        }
        return 3;
    }

    public LaunchType getLaunchType() {
        return LaunchType.AppLaunchType;
    }

    public JTitleType getTitleType() {
        return JTitleType.Application;
    }

    public String getStudio() {
        return ((EDSV2MovieMediaItem) this.detailData).getStudio();
    }

    public float getMetaCriticReviewScore() {
        return ((EDSV2MovieMediaItem) this.detailData).getMetaCriticReviewScore();
    }

    protected int getRelatedMediaType() {
        if (getMediaType() == EDSV2MediaType.MEDIATYPE_MUSICVIDEO) {
            return 0;
        }
        return EDSV2MediaType.MEDIATYPE_MOVIE;
    }

    public boolean shouldGetProviderActivities() {
        return true;
    }

    public void load(boolean forceRefresh) {
        if (getMediaType() != EDSV2MediaType.MEDIATYPE_MUSICVIDEO) {
            forceRefresh = forceRefresh || drainShouldLoadFullDetail();
        }
        super.load(forceRefresh);
    }
}
