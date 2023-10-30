package com.microsoft.xbox.service.model.edsv2;

public class EDSV2MusicTrackMediaItem extends EDSV2MediaItem {
    private String albumCanonicalId;
    private String albumName;
    private String artistCanonicalId;
    private String artistName;
    private String labelOwner;
    private int trackNumber;

    public EDSV2MusicTrackMediaItem(EDSV2MediaItem source) {
        super(source);
        setMediaType(EDSV2MediaType.MEDIATYPE_TRACK);
    }

    public String getArtistName() {
        return this.artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getArtistCanonicalId() {
        return this.artistCanonicalId;
    }

    public void setArtistCanonicalId(String artistCanonicalId) {
        this.artistCanonicalId = artistCanonicalId;
    }

    public String getAlbumName() {
        return this.albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getAlbumCanonicalId() {
        return this.albumCanonicalId;
    }

    public void setAlbumCanonicalId(String albumCanonicalId) {
        this.albumCanonicalId = albumCanonicalId;
    }

    public String getLabelOwner() {
        return this.labelOwner;
    }

    public void setLabelOwner(String labelOwner) {
        this.labelOwner = labelOwner;
    }

    public int getTrackNumber() {
        return this.trackNumber;
    }

    public void setTrackNumber(int trackNumber) {
        this.trackNumber = trackNumber;
    }
}
