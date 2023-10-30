package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.UpdateData;
import com.microsoft.xbox.service.model.UpdateType;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaItem;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaItemModel;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.network.ListState;
import com.microsoft.xbox.xle.app.ApplicationBarManager;
import com.microsoft.xbox.xle.app.XLEApplication;
import java.util.EnumSet;

public abstract class EDSV2MediaItemViewModel<T extends EDSV2MediaItemModel> extends ViewModelBase {
    protected T mediaModel;
    protected ListState viewModelState = ListState.LoadingState;

    protected abstract EDSV2MediaItem getCurrentScreenData();

    protected abstract int getErrorStringResourceId();

    public EDSV2MediaItemViewModel() {
        super(true, true);
        EDSV2MediaItem selectedMediaItem = XLEGlobalData.getInstance().getSelectedMediaItemData();
        XLEAssert.assertNotNull(selectedMediaItem);
        this.mediaModel = EDSV2MediaItemModel.getModel(selectedMediaItem);
        XLEAssert.assertNotNull(this.mediaModel);
    }

    public ListState getViewModelState() {
        return this.viewModelState;
    }

    public long getTitleId() {
        return this.mediaModel.getTitleId();
    }

    public boolean isBusy() {
        return this.mediaModel.getIsLoading();
    }

    public String getBlockingStatusText() {
        return XLEApplication.Instance.getString(R.string.details_starting);
    }

    public String getCanonicalId() {
        if (this.mediaModel != null) {
            return this.mediaModel.getCanonicalId();
        }
        return null;
    }

    public void load(boolean forceRefresh) {
        setUpdateTypesToCheck(EnumSet.of(UpdateType.MediaItemDetail));
        this.mediaModel.load(forceRefresh);
    }

    protected boolean isScreenDataEmpty() {
        return getCurrentScreenData() == null;
    }

    protected void onStartOverride() {
        XLEGlobalData.getInstance().setSelectedMediaItemData(getCurrentScreenData());
        this.mediaModel.addObserver(this);
        ApplicationBarManager.getInstance().setCurrentDetailIdentifier(this.mediaModel.getCanonicalId());
    }

    protected void onStopOverride() {
        this.mediaModel.removeObserver(this);
        ApplicationBarManager.getInstance().setCurrentDetailIdentifier(null);
    }

    public void updateOverride(AsyncResult<UpdateData> asyncResult) {
        UpdateType type = ((UpdateData) asyncResult.getResult()).getUpdateType();
        if (type != getDefaultUpdateType()) {
            XLELog.Diagnostic("SearchDetailsViewModel", "Unexpected update type " + type.toString());
        } else if (((UpdateData) asyncResult.getResult()).getIsFinal()) {
            if (asyncResult.getException() != null && this.viewModelState != ListState.ValidContentState) {
                this.viewModelState = ListState.ErrorState;
            } else if (asyncResult.getException() == null) {
                if (isScreenDataEmpty()) {
                    this.viewModelState = ListState.NoContentState;
                } else {
                    this.viewModelState = ListState.ValidContentState;
                    XLEGlobalData.getInstance().setSelectedMediaItemData(getCurrentScreenData());
                }
            }
            ApplicationBarManager.getInstance().setCurrentDetailIdentifier(this.mediaModel.getCanonicalId());
        }
    }

    public void onUpdateFinished() {
        if (checkErrorCode(getDefaultUpdateType(), getModelErrorCode())) {
            if (getViewModelState() == ListState.ValidContentState) {
                showError(getErrorStringResourceId());
            } else {
                this.viewModelState = ListState.ErrorState;
                this.adapter.updateView();
            }
        }
        super.onUpdateFinished();
    }

    protected long getModelErrorCode() {
        return XLEErrorCode.FAILED_TO_GET_MEDIA_ITEM_DETAILS;
    }

    protected UpdateType getDefaultUpdateType() {
        return UpdateType.MediaItemDetail;
    }
}
