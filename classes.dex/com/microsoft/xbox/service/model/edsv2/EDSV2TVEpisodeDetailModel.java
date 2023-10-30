package com.microsoft.xbox.service.model.edsv2;

import com.microsoft.xbox.service.model.JTitleType;
import com.microsoft.xbox.service.model.LaunchType;

public class EDSV2TVEpisodeDetailModel extends EDSV2MediaItemDetailModel<EDSV2TVEpisodeMediaItem, EDSV2TVSeriesMediaItem> {
    protected EDSV2TVEpisodeDetailModel(EDSV2MediaItem mediaItem) {
        super(mediaItem);
    }

    protected EDSV2TVEpisodeMediaItem createMediaItem(EDSV2MediaItem mediaItem) {
        return new EDSV2TVEpisodeMediaItem(mediaItem);
    }

    public int getMediaGroup() {
        return 4;
    }

    public LaunchType getLaunchType() {
        return LaunchType.AppLaunchType;
    }

    public JTitleType getTitleType() {
        return JTitleType.Application;
    }

    public String getSeriesTitle() {
        return ((EDSV2TVEpisodeMediaItem) this.detailData).getSeriesTitle();
    }

    public int getSeasonNumber() {
        return ((EDSV2TVEpisodeMediaItem) this.detailData).getSeasonNumber();
    }

    public int getEpisodeNumber() {
        return ((EDSV2TVEpisodeMediaItem) this.detailData).getEpisodeNumber();
    }

    public String getSeasonTitle() {
        return ((EDSV2TVEpisodeMediaItem) this.detailData).getSeasonTitle();
    }

    public String geSeasonCanonicalId() {
        return ((EDSV2TVEpisodeMediaItem) this.detailData).getSeasonCanonicalId();
    }

    public String getSeriesCanonicalId() {
        return ((EDSV2TVEpisodeMediaItem) this.detailData).getSeriesCanonicalId();
    }

    protected String getRelatedCanonicalId() {
        return ((EDSV2TVEpisodeMediaItem) this.detailData).getSeriesCanonicalId();
    }

    protected int getRelatedMediaType() {
        return EDSV2MediaType.MEDIATYPE_TVSERIES;
    }

    public boolean shouldGetProviderActivities() {
        return true;
    }

    public void load(boolean forceRefresh) {
        forceRefresh = forceRefresh || drainShouldLoadFullDetail();
        super.load(forceRefresh);
    }
}
