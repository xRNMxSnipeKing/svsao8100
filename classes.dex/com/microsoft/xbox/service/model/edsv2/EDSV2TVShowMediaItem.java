package com.microsoft.xbox.service.model.edsv2;

public class EDSV2TVShowMediaItem extends EDSV2TVEpisodeMediaItem {
    public EDSV2TVShowMediaItem(EDSV2MediaItem source) {
        super(source);
        setMediaType(EDSV2MediaType.MEDIATYPE_TVSHOW);
    }
}
