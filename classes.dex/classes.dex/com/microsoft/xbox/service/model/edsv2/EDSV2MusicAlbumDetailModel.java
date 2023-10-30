package com.microsoft.xbox.service.model.edsv2;

import com.microsoft.xbox.service.model.JTitleType;
import com.microsoft.xbox.service.model.LaunchType;
import java.util.ArrayList;

public class EDSV2MusicAlbumDetailModel extends EDSV2MediaItemDetailModel<EDSV2MusicAlbumMediaItem, EDSV2MusicAlbumMediaItem> {
    protected EDSV2MusicAlbumDetailModel(EDSV2MediaItem mediaItem) {
        super(mediaItem);
    }

    protected EDSV2MusicAlbumMediaItem createMediaItem(EDSV2MediaItem mediaItem) {
        return new EDSV2MusicAlbumMediaItem(mediaItem);
    }

    public int getMediaGroup() {
        return 5;
    }

    public LaunchType getLaunchType() {
        return LaunchType.AppLaunchType;
    }

    public JTitleType getTitleType() {
        return JTitleType.Application;
    }

    public String getArtistName() {
        return ((EDSV2MusicAlbumMediaItem) this.detailData).getArtistName();
    }

    public String getLabelOwner() {
        return ((EDSV2MusicAlbumMediaItem) this.detailData).getLabelOwner();
    }

    public ArrayList<EDSV2MusicTrackMediaItem> getTracks() {
        return ((EDSV2MusicAlbumMediaItem) this.detailData).getTracks();
    }

    public boolean shouldGetProviderActivities() {
        return true;
    }
}
