package com.microsoft.xbox.service.model.edsv2;

import com.microsoft.xbox.service.model.JTitleType;
import com.microsoft.xbox.service.model.LaunchType;
import com.microsoft.xbox.toolkit.AsyncResult;

public class EDSV2MusicTrackDetailModel extends EDSV2MediaItemDetailModel<EDSV2MusicTrackMediaItemWithAlbum, EDSV2MusicTrackMediaItemWithAlbum> {
    protected EDSV2MusicTrackDetailModel(EDSV2MediaItem mediaItem) {
        super(mediaItem);
    }

    protected EDSV2MusicTrackMediaItemWithAlbum createMediaItem(EDSV2MediaItem mediaItem) {
        return new EDSV2MusicTrackMediaItemWithAlbum(mediaItem);
    }

    public int getMediaGroup() {
        return 5;
    }

    public LaunchType getLaunchType() {
        return LaunchType.UnknownLaunchType;
    }

    public JTitleType getTitleType() {
        return JTitleType.Unknown;
    }

    protected void onGetMediaItemDetailCompleted(AsyncResult<EDSV2MusicTrackMediaItemWithAlbum> result) {
        if (!(result.getException() != null || result.getResult() == null || ((EDSV2MusicTrackMediaItemWithAlbum) result.getResult()).getAlbum() == null)) {
            EDSV2MusicAlbumMediaItem albumData = ((EDSV2MusicTrackMediaItemWithAlbum) result.getResult()).getAlbum();
            ((EDSV2MusicAlbumDetailModel) EDSV2MediaItemModel.createModel(albumData)).onGetMediaItemDetailCompleted(new AsyncResult(albumData, this, null));
        }
        super.onGetMediaItemDetailCompleted(result);
    }

    public String getArtistName() {
        return ((EDSV2MusicTrackMediaItemWithAlbum) this.detailData).getArtistName();
    }

    public String getAlbumName() {
        return ((EDSV2MusicTrackMediaItemWithAlbum) this.detailData).getAlbumName();
    }

    public int getTrackNumber() {
        return ((EDSV2MusicTrackMediaItemWithAlbum) this.detailData).getTrackNumber();
    }
}
