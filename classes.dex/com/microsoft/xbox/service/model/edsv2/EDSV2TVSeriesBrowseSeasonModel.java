package com.microsoft.xbox.service.model.edsv2;

public class EDSV2TVSeriesBrowseSeasonModel extends EDSV2MediaListBrowseModel<EDSV2TVSeriesMediaItem, EDSV2TVSeasonMediaItem, EDSV2TVSeriesMediaItem> {
    protected EDSV2TVSeriesBrowseSeasonModel(EDSV2MediaItem mediaItem) {
        super(mediaItem);
    }

    protected int getDesiredMediaItemType() {
        return EDSV2MediaType.MEDIATYPE_TVSEASON;
    }

    protected EDSV2TVSeriesMediaItem createMediaItem(EDSV2MediaItem mediaItem) {
        return new EDSV2TVSeriesMediaItem(mediaItem);
    }

    public int getMediaGroup() {
        return 4;
    }

    protected int getRelatedMediaType() {
        return EDSV2MediaType.MEDIATYPE_TVSERIES;
    }

    public String getNetworkName() {
        return ((EDSV2TVSeriesMediaItem) this.detailData).getNetworkName();
    }
}
