package com.microsoft.xbox.service.model.edsv2;

import com.microsoft.xbox.service.model.JTitleType;
import com.microsoft.xbox.service.model.LaunchType;
import java.util.ArrayList;

public class EDSV2GameContentDetailModel extends EDSV2MediaItemDetailModel<EDSV2GameContentMediaItem, EDSV2GameContentMediaItem> {
    protected EDSV2GameContentDetailModel(EDSV2MediaItem mediaItem) {
        super(mediaItem);
    }

    protected EDSV2GameContentMediaItem createMediaItem(EDSV2MediaItem mediaItem) {
        return new EDSV2GameContentMediaItem(mediaItem);
    }

    public int getMediaGroup() {
        return 1;
    }

    public LaunchType getLaunchType() {
        return LaunchType.GameContentLaunchType;
    }

    public JTitleType getTitleType() {
        return JTitleType.Standard;
    }

    public String getPublisher() {
        return ((EDSV2GameContentMediaItem) this.detailData).getPublisher();
    }

    public float getAverageUserRating() {
        return ((EDSV2GameContentMediaItem) this.detailData).getAverageUserRating();
    }

    public int getUserRatingCount() {
        return ((EDSV2GameContentMediaItem) this.detailData).getUserRatingCount();
    }

    public String getDeveloper() {
        return ((EDSV2GameContentMediaItem) this.detailData).getDeveloper();
    }

    public String getRatingId() {
        return ((EDSV2GameContentMediaItem) this.detailData).getRatingId();
    }

    public ArrayList<EDSV2RatingDescriptor> getRatingDescriptors() {
        return ((EDSV2GameContentMediaItem) this.detailData).getRatingDescriptors();
    }
}
