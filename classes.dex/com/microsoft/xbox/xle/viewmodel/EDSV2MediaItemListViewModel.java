package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.xbox.service.model.UpdateType;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaItem;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaListBrowseModel;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.xle.app.XLEUtil;
import java.net.URI;

public abstract class EDSV2MediaItemListViewModel<T extends EDSV2MediaListBrowseModel> extends EDSV2MediaItemDetailViewModel<T> {
    public String getTitle() {
        return ((EDSV2MediaListBrowseModel) this.mediaModel).getTitle();
    }

    public URI getImageUrl() {
        return ((EDSV2MediaListBrowseModel) this.mediaModel).getParentImageUrl();
    }

    public int getDefaultImageRid() {
        return XLEUtil.getMediaItemDefaultRid(((EDSV2MediaListBrowseModel) this.mediaModel).getParentMediaType());
    }

    public boolean isBusy() {
        return ((EDSV2MediaListBrowseModel) this.mediaModel).getIsLoadingChild();
    }

    protected long getModelErrorCode() {
        return XLEErrorCode.FAILED_TO_BROWSE_MEDIA_ITEM_LIST;
    }

    protected EDSV2MediaItem getCurrentScreenData() {
        return ((EDSV2MediaListBrowseModel) this.mediaModel).getParentMediaItemDetailData();
    }

    protected boolean isScreenDataEmpty() {
        return ((EDSV2MediaListBrowseModel) this.mediaModel).getMediaItemListData() == null || ((EDSV2MediaListBrowseModel) this.mediaModel).getMediaItemListData().size() == 0;
    }

    protected UpdateType getDefaultUpdateType() {
        return UpdateType.MediaListBrowse;
    }
}
