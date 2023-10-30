package com.microsoft.xbox.service.model.edsv2;

import com.microsoft.xbox.service.model.serialization.ProgrammingAction;
import com.microsoft.xbox.service.model.serialization.ProgrammingSlot;
import com.microsoft.xbox.toolkit.UrlUtil;

public class EDSV2ProgrammingMediaItem extends EDSV2MediaItem {
    private String type;

    public EDSV2ProgrammingMediaItem(EDSV2MediaItem source) {
        super(source);
    }

    public EDSV2ProgrammingMediaItem(ProgrammingSlot data) {
        this.type = data.Action.type;
        if (this.type.equals(ProgrammingAction.Game)) {
            setMediaType(1);
        } else if (this.type.equals(ProgrammingAction.Movie)) {
            setMediaType(EDSV2MediaType.MEDIATYPE_MOVIE);
        } else if (this.type.equals(ProgrammingAction.TV)) {
            setMediaType(EDSV2MediaType.MEDIATYPE_TVSERIES);
        } else if (this.type.equals(ProgrammingAction.Album)) {
            setMediaType(EDSV2MediaType.MEDIATYPE_ALBUM);
        } else if (this.type.equals(ProgrammingAction.App)) {
            setMediaType(61);
        } else {
            setMediaType(0);
        }
        setTitle(data.Title);
        setDescription(data.Description);
        setImageUrl(UrlUtil.getEncodedUri(data.ImageUrl));
        setIsProgrammingOverride(true);
        setCanonicalId(data.Action.Target);
    }
}
