package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.xbox.service.model.UpdateData;
import com.microsoft.xbox.service.model.UpdateType;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaItemModel;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaListBrowseModel;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.xle.app.adapter.AdapterFactory;
import java.net.URI;

public class DetailsPivotActivityViewModel extends ViewModelBase {
    protected EDSV2MediaItemModel mediaModel;

    public DetailsPivotActivityViewModel() {
        this.mediaModel = null;
        this.adapter = AdapterFactory.getInstance().getDetailsPivotActivityAdapter(this);
        this.mediaModel = EDSV2MediaItemModel.getModel(XLEGlobalData.getInstance().getSelectedMediaItemData());
        XLEAssert.assertNotNull(this.mediaModel);
    }

    public void onRehydrate() {
        this.adapter = AdapterFactory.getInstance().getDetailsPivotActivityAdapter(this);
    }

    protected void onStartOverride() {
        this.mediaModel.addObserver(this);
    }

    protected void onStopOverride() {
        this.mediaModel.removeObserver(this);
    }

    public boolean isBusy() {
        return false;
    }

    public void load(boolean forceRefresh) {
        if (this.mediaModel instanceof EDSV2MediaListBrowseModel) {
            ((EDSV2MediaListBrowseModel) this.mediaModel).loadDetails(forceRefresh);
        } else {
            this.mediaModel.load(forceRefresh);
        }
    }

    public void updateOverride(AsyncResult<UpdateData> asyncResult) {
        if (((UpdateData) asyncResult.getResult()).getUpdateType() == UpdateType.MediaItemDetail && ((UpdateData) asyncResult.getResult()).getIsFinal()) {
            this.adapter.updateView();
        }
    }

    public URI getBackgroundImageUrl() {
        return this.mediaModel.getBackgroundImageUrl();
    }

    public boolean shouldShowBackground() {
        return true;
    }
}
