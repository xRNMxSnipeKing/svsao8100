package com.microsoft.xbox.service.model.edsv2;

public class EDSV2TVEpisodeMediaItem extends EDSV2MediaItem {
    private int episodeNumber;
    private String seasonCanonicalId;
    private int seasonNumber;
    private String seasonTitle;
    private String seriesCanonicalId;
    private String seriesTitle;

    public EDSV2TVEpisodeMediaItem(EDSV2MediaItem source) {
        super(source);
        setMediaType(EDSV2MediaType.MEDIATYPE_TVEPISODE);
        if (source instanceof EDSV2TVEpisodeMediaItem) {
            this.seasonCanonicalId = ((EDSV2TVEpisodeMediaItem) source).getSeasonCanonicalId();
            this.seriesCanonicalId = ((EDSV2TVEpisodeMediaItem) source).getSeriesCanonicalId();
            this.seriesTitle = ((EDSV2TVEpisodeMediaItem) source).getSeriesTitle();
            this.seasonNumber = ((EDSV2TVEpisodeMediaItem) source).getSeasonNumber();
            this.seasonTitle = ((EDSV2TVEpisodeMediaItem) source).getSeasonTitle();
            this.episodeNumber = ((EDSV2TVEpisodeMediaItem) source).getEpisodeNumber();
        }
    }

    public int getSeasonNumber() {
        return this.seasonNumber;
    }

    public void setSeasonNumber(int seasonNumber) {
        this.seasonNumber = seasonNumber;
    }

    public int getEpisodeNumber() {
        return this.episodeNumber;
    }

    public void setEpisodeNumber(int episodeNumber) {
        this.episodeNumber = episodeNumber;
    }

    public String getSeriesTitle() {
        return this.seriesTitle;
    }

    public void setSeriesTitle(String seriesTitle) {
        this.seriesTitle = seriesTitle;
    }

    public String getSeasonTitle() {
        return this.seasonTitle;
    }

    public void setSeasonTitle(String seasonTitle) {
        this.seasonTitle = seasonTitle;
    }

    public String getSeasonCanonicalId() {
        return this.seasonCanonicalId;
    }

    public void setSeasonCanonicalId(String seasonCanonicalId) {
        this.seasonCanonicalId = seasonCanonicalId;
    }

    public String getSeriesCanonicalId() {
        return this.seriesCanonicalId;
    }

    public void setSeriesCanonicalId(String seriesCanonicalId) {
        this.seriesCanonicalId = seriesCanonicalId;
    }
}
