package com.microsoft.xbox.service.model.edsv2;

import java.util.ArrayList;

public class EDSV2TVSeasonMediaItem extends EDSV2MediaItem {
    private ArrayList<EDSV2TVEpisodeMediaItem> episodes;
    private String seasonId;
    private String seasonName;
    private int seasonNumber;
    private String seriesCanonicalId;
    private String seriesTitle;

    public EDSV2TVSeasonMediaItem(EDSV2MediaItem source) {
        super(source);
        setMediaType(EDSV2MediaType.MEDIATYPE_TVSEASON);
        if (source instanceof EDSV2TVSeasonMediaItem) {
            setSeasonNumber(((EDSV2TVSeasonMediaItem) source).getSeasonNumber());
            setSeriesTitle(((EDSV2TVSeasonMediaItem) source).getSeriesTitle());
            setSeriesCanonicalId(((EDSV2TVSeasonMediaItem) source).getSeriesCanonicalId());
        }
    }

    public String getSeasonId() {
        return this.seasonId;
    }

    public void setSeasonId(String seasonId) {
        this.seasonId = seasonId;
    }

    public String getSeasonName() {
        return this.seasonName;
    }

    public void setSeasonName(String seasonName) {
        this.seasonName = seasonName;
    }

    public ArrayList<EDSV2TVEpisodeMediaItem> getEpisodes() {
        return this.episodes;
    }

    public void setEpisodes(ArrayList<EDSV2TVEpisodeMediaItem> episodes) {
        this.episodes = episodes;
    }

    public int getSeasonNumber() {
        return this.seasonNumber;
    }

    public void setSeasonNumber(int seasonNumber) {
        this.seasonNumber = seasonNumber;
    }

    public String getSeriesTitle() {
        return this.seriesTitle;
    }

    public String getDisplayTitle() {
        return this.seriesTitle;
    }

    public void setSeriesTitle(String seriesTitle) {
        this.seriesTitle = seriesTitle;
    }

    public String getSeriesCanonicalId() {
        return this.seriesCanonicalId;
    }

    public void setSeriesCanonicalId(String seriesCanonicalId) {
        this.seriesCanonicalId = seriesCanonicalId;
    }
}
