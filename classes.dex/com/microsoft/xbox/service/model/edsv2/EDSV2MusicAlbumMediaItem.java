package com.microsoft.xbox.service.model.edsv2;

import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.XLEConstants;
import java.util.ArrayList;
import java.util.Iterator;

public class EDSV2MusicAlbumMediaItem extends EDSV2MediaItem {
    private String artistName;
    private String labelOwner;
    private ArrayList<EDSV2MusicTrackMediaItem> tracks;

    public EDSV2MusicAlbumMediaItem(EDSV2MediaItem source) {
        super(source);
        setMediaType(EDSV2MediaType.MEDIATYPE_ALBUM);
    }

    public String getArtistName() {
        return this.artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getLabelOwner() {
        return this.labelOwner;
    }

    public void setLabelOwner(String labelOwner) {
        this.labelOwner = labelOwner;
    }

    public ArrayList<EDSV2MusicTrackMediaItem> getTracks() {
        return this.tracks;
    }

    public void setTracks(ArrayList<EDSV2MusicTrackMediaItem> tracks) {
        this.tracks = tracks;
    }

    public ArrayList<EDSV2Provider> getProviders() {
        ArrayList<EDSV2Provider> providers = super.getProviders();
        if (providers != null && providers.size() > 0) {
            Iterator i$ = providers.iterator();
            while (i$.hasNext()) {
                EDSV2Provider provider = (EDSV2Provider) i$.next();
                if (provider.getTitleId() == XLEConstants.ZUNE_TITLE_ID && JavaUtil.isNullOrEmpty(provider.getCanonicalId())) {
                    provider.setCanonicalId(EDSV2MediaItemModel.getZuneCanonicalId());
                }
            }
        }
        return providers;
    }
}
