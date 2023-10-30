package com.microsoft.xbox.service.model.edsv2;

public class EDSV2TVSeasonBrowseEpisodeModel extends EDSV2MediaListBrowseModel<EDSV2TVSeasonMediaItem, EDSV2TVEpisodeMediaItem, EDSV2TVSeriesMediaItem> {
    protected EDSV2TVSeasonBrowseEpisodeModel(EDSV2MediaItem mediaItem) {
        super(mediaItem);
    }

    protected int getDesiredMediaItemType() {
        return EDSV2MediaType.MEDIATYPE_TVEPISODE;
    }

    protected EDSV2TVSeasonMediaItem createMediaItem(EDSV2MediaItem mediaItem) {
        return new EDSV2TVSeasonMediaItem(mediaItem);
    }

    public int getSeasonNumber() {
        return ((EDSV2TVSeasonMediaItem) this.detailData).getSeasonNumber();
    }

    public String getSeriesTitle() {
        return ((EDSV2TVSeasonMediaItem) this.detailData).getSeriesTitle();
    }

    public int getMediaGroup() {
        return 4;
    }

    protected int getRelatedMediaType() {
        return EDSV2MediaType.MEDIATYPE_TVSERIES;
    }

    protected String getRelatedCanonicalId() {
        return ((EDSV2TVSeasonMediaItem) this.detailData).getSeriesCanonicalId();
    }
}
