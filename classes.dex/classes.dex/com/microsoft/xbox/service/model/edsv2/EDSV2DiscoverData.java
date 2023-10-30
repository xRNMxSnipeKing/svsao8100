package com.microsoft.xbox.service.model.edsv2;

import java.util.List;

public class EDSV2DiscoverData {
    private List<EDSV2MediaItem> browseItems;
    private List<EDSV2MediaItem> picksForYou;

    public void setBrowseItems(List<EDSV2MediaItem> items) {
        this.browseItems = items;
    }

    public void setPicksForYou(List<EDSV2MediaItem> items) {
        this.picksForYou = items;
    }

    public List<EDSV2MediaItem> getBrowseItems() {
        return this.browseItems;
    }

    public List<EDSV2MediaItem> getPicksForYou() {
        return this.picksForYou;
    }
}
