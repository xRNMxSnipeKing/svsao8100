package com.microsoft.xbox.service.model.edsv2;

import com.microsoft.xbox.service.model.JTitleType;
import com.microsoft.xbox.service.model.LaunchType;
import java.util.ArrayList;
import java.util.Date;

public class EDSV2GameDetailModel extends EDSV2MediaItemDetailModel<EDSV2GameMediaItem, EDSV2GameMediaItem> {
    protected EDSV2GameDetailModel(EDSV2MediaItem mediaItem) {
        super(mediaItem);
    }

    protected EDSV2GameMediaItem createMediaItem(EDSV2MediaItem mediaItem) {
        return new EDSV2GameMediaItem(mediaItem);
    }

    public int getMediaGroup() {
        return 1;
    }

    public LaunchType getLaunchType() {
        return LaunchType.GameLaunchType;
    }

    public JTitleType getTitleType() {
        return JTitleType.Standard;
    }

    public String getPublisher() {
        return ((EDSV2GameMediaItem) this.detailData).getPublisher();
    }

    public float getAverageUserRating() {
        return ((EDSV2GameMediaItem) this.detailData).getAverageUserRating();
    }

    public int getUserRatingCount() {
        return ((EDSV2GameMediaItem) this.detailData).getUserRatingCount();
    }

    public String getDeveloper() {
        return ((EDSV2GameMediaItem) this.detailData).getDeveloper();
    }

    public String getRatingId() {
        return ((EDSV2GameMediaItem) this.detailData).getRatingId();
    }

    public ArrayList<EDSV2RatingDescriptor> getRatingDescriptors() {
        return ((EDSV2GameMediaItem) this.detailData).getRatingDescriptors();
    }

    public Date getLastPlayedDate() {
        return ((EDSV2GameMediaItem) this.detailData).getLastPlayedDate();
    }

    protected int getRelatedMediaType() {
        return 1;
    }
}
