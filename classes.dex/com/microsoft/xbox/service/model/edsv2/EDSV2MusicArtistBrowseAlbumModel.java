package com.microsoft.xbox.service.model.edsv2;

import java.util.ArrayList;

public class EDSV2MusicArtistBrowseAlbumModel extends EDSV2MediaListBrowseModel<EDSV2MusicArtistMediaItem, EDSV2MusicAlbumMediaItem, EDSV2MusicAlbumMediaItem> {
    protected EDSV2MusicArtistBrowseAlbumModel(EDSV2MediaItem mediaItem) {
        super(mediaItem);
    }

    protected int getDesiredMediaItemType() {
        return EDSV2MediaType.MEDIATYPE_ALBUM;
    }

    protected EDSV2MusicArtistMediaItem createMediaItem(EDSV2MediaItem mediaItem) {
        return new EDSV2MusicArtistMediaItem(mediaItem);
    }

    public String getArtistName() {
        return ((EDSV2MusicArtistMediaItem) this.detailData).getArtistName();
    }

    public ArrayList<EDSV2MusicAlbumMediaItem> getAlbums() {
        return this.browseListData;
    }

    public int getMediaGroup() {
        return 6;
    }
}
