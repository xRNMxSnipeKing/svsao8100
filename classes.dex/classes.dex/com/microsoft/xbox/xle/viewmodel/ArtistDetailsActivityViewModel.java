package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.edsv2.EDSV2MusicAlbumMediaItem;
import com.microsoft.xbox.service.model.edsv2.EDSV2MusicArtistBrowseAlbumModel;
import com.microsoft.xbox.xle.app.adapter.AdapterFactory;
import java.util.ArrayList;

public class ArtistDetailsActivityViewModel extends EDSV2MediaItemListViewModel<EDSV2MusicArtistBrowseAlbumModel> {
    public ArtistDetailsActivityViewModel() {
        this.adapter = AdapterFactory.getInstance().getArtistDetailAdapter(this);
    }

    public void onRehydrate() {
        this.adapter = AdapterFactory.getInstance().getArtistDetailAdapter(this);
    }

    public ArrayList<EDSV2MusicAlbumMediaItem> getArtistAlbums() {
        return ((EDSV2MusicArtistBrowseAlbumModel) this.mediaModel).getMediaItemListData();
    }

    public String getArtistName() {
        return ((EDSV2MusicArtistBrowseAlbumModel) this.mediaModel).getArtistName();
    }

    public void NavigateToAlbumDetails(EDSV2MusicAlbumMediaItem item) {
        navigateToAppOrMediaDetails(item);
    }

    protected int getErrorStringResourceId() {
        return R.string.toast_artist_albums_list_error;
    }
}
