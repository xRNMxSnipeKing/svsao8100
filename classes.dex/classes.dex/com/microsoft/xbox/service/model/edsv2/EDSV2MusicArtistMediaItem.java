package com.microsoft.xbox.service.model.edsv2;

public class EDSV2MusicArtistMediaItem extends EDSV2MediaItem {
    private String artistName;

    public EDSV2MusicArtistMediaItem(EDSV2MediaItem source) {
        super(source);
        setMediaType(EDSV2MediaType.MEDIATYPE_MUSICARTIST);
    }

    public String getArtistName() {
        return this.artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }
}
