package com.microsoft.xbox.service.model.edsv2;

import java.net.URI;

public class EDSV2MusicTrackMediaItemWithAlbum extends EDSV2MusicTrackMediaItem {
    private EDSV2MusicAlbumMediaItem album;

    public EDSV2MusicTrackMediaItemWithAlbum(EDSV2MediaItem source) {
        super(source);
        setMediaType(EDSV2MediaType.MEDIATYPE_TRACK);
    }

    public EDSV2MusicAlbumMediaItem getAlbum() {
        return this.album;
    }

    public void setAlbum(EDSV2MusicAlbumMediaItem data) {
        this.album = data;
    }

    public URI getImageUrl() {
        if (this.album != null) {
            return this.album.getImageUrl();
        }
        return super.getImageUrl();
    }
}
