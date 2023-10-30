package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.NowPlayingGlobalModel;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaItem;
import com.microsoft.xbox.service.model.edsv2.EDSV2MusicAlbumDetailModel;
import com.microsoft.xbox.service.model.edsv2.EDSV2MusicTrackMediaItem;
import com.microsoft.xbox.service.model.edsv2.EDSV2MusicTrackMediaItemWithAlbum;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.xle.app.adapter.AdapterFactory;
import java.util.ArrayList;
import java.util.Iterator;

public class AlbumDetailsActivityViewModel extends EDSV2MediaItemDetailViewModel<EDSV2MusicAlbumDetailModel> {
    private boolean autoShowSongList;

    public AlbumDetailsActivityViewModel() {
        this.autoShowSongList = true;
        this.adapter = AdapterFactory.getInstance().getAlbumDetailAdapter(this);
    }

    public void onRehydrate() {
        this.adapter = AdapterFactory.getInstance().getAlbumDetailAdapter(this);
    }

    public String getArtist() {
        if (this.mediaModel == null || JavaUtil.isNullOrEmpty(((EDSV2MusicAlbumDetailModel) this.mediaModel).getArtistName())) {
            return null;
        }
        return ((EDSV2MusicAlbumDetailModel) this.mediaModel).getArtistName();
    }

    public String getAlbumYearAndStudio() {
        return JavaUtil.concatenateStringsWithDelimiter(getReleaseYear(), getLabelOwner(), null, XboxApplication.Resources.getString(R.string.comma_delimiter), false);
    }

    public String getLabelOwner() {
        return ((EDSV2MusicAlbumDetailModel) this.mediaModel).getLabelOwner();
    }

    public ArrayList<EDSV2MusicTrackMediaItem> getTracks() {
        return ((EDSV2MusicAlbumDetailModel) this.mediaModel).getTracks();
    }

    public boolean isAutoShowSongList() {
        return this.autoShowSongList;
    }

    public void setAutoShowSongList(boolean flag) {
        this.autoShowSongList = flag;
    }

    public int getNowPlayingTrack() {
        EDSV2MediaItem nowPlayingMediaItem = NowPlayingGlobalModel.getInstance().getNowPlayingMediaItem();
        if (nowPlayingMediaItem != null && (nowPlayingMediaItem instanceof EDSV2MusicTrackMediaItemWithAlbum)) {
            String currentTrackCanonicalId = nowPlayingMediaItem.getCanonicalId();
            if (getTracks() != null) {
                Iterator i$ = getTracks().iterator();
                while (i$.hasNext()) {
                    EDSV2MusicTrackMediaItem track = (EDSV2MusicTrackMediaItem) i$.next();
                    if (JavaUtil.stringsEqualCaseInsensitive(track.getCanonicalId(), currentTrackCanonicalId)) {
                        return track.getTrackNumber();
                    }
                }
            }
        }
        return -1;
    }

    protected int getErrorStringResourceId() {
        return R.string.toast_album_details_list_error;
    }

    protected boolean shouldLoadActivities() {
        return true;
    }
}
