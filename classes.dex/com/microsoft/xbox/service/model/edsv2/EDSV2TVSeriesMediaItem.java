package com.microsoft.xbox.service.model.edsv2;

import java.util.ArrayList;

public class EDSV2TVSeriesMediaItem extends EDSV2MediaItem {
    private float metaCriticReviewScore;
    private String networkName;
    private ArrayList<EDSV2TVSeasonMediaItem> seasons;
    private String seriesId;
    private String seriesName;

    public EDSV2TVSeriesMediaItem(EDSV2MediaItem source) {
        super(source);
        setMediaType(EDSV2MediaType.MEDIATYPE_TVSERIES);
    }

    public String getSeriesId() {
        return this.seriesId;
    }

    public void setSeriesId(String seriesId) {
        this.seriesId = seriesId;
    }

    public String getSeriesName() {
        return this.seriesName;
    }

    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }

    public ArrayList<EDSV2TVSeasonMediaItem> getSeasons() {
        return this.seasons;
    }

    public void setSeasons(ArrayList<EDSV2TVSeasonMediaItem> seasons) {
        this.seasons = seasons;
    }

    public String getNetworkName() {
        return this.networkName;
    }

    public void setNetworkName(String networkName) {
        this.networkName = networkName;
    }

    public float getMetaCriticReviewScore() {
        return this.metaCriticReviewScore;
    }

    public void setMetaCriticReviewScore(float metaCriticReviewScore) {
        this.metaCriticReviewScore = metaCriticReviewScore;
    }
}
