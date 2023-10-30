package com.microsoft.xbox.service.model.edsv2;

import com.microsoft.xbox.service.model.JTitleType;
import com.microsoft.xbox.service.model.LaunchType;
import com.microsoft.xbox.toolkit.XLEConstants;
import java.util.ArrayList;

public class EDSV2AppDetailModel extends EDSV2MediaItemDetailModel<EDSV2AppMediaItem, EDSV2AppMediaItem> {
    protected EDSV2AppDetailModel(EDSV2MediaItem mediaItem) {
        super(mediaItem);
    }

    protected EDSV2AppMediaItem createMediaItem(EDSV2MediaItem mediaItem) {
        return new EDSV2AppMediaItem(mediaItem);
    }

    public int getMediaGroup() {
        return 2;
    }

    public LaunchType getLaunchType() {
        return LaunchType.AppLaunchType;
    }

    public JTitleType getTitleType() {
        return JTitleType.Application;
    }

    public ArrayList<EDSV2Provider> getProviders() {
        return getTitleId() == XLEConstants.ZUNE_TITLE_ID ? new ArrayList() : super.getProviders();
    }
}
