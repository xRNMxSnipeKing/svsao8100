package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.UpdateData;
import com.microsoft.xbox.service.model.UpdateType;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaItem;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaItemDetailModel;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaItemModel;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.network.ListState;
import java.util.EnumSet;

public abstract class AbstractRelatedActivityViewModel<T extends EDSV2MediaItemDetailModel> extends PivotViewModelBase {
    protected T mediaModel;
    protected ListState viewModelState = ListState.LoadingState;

    public abstract Object getRelated();

    public AbstractRelatedActivityViewModel() {
        EDSV2MediaItem selectedMediaItem = XLEGlobalData.getInstance().getSelectedMediaItemData();
        XLEAssert.assertNotNull(selectedMediaItem);
        this.mediaModel = (EDSV2MediaItemDetailModel) EDSV2MediaItemModel.getModel(selectedMediaItem);
        XLEAssert.assertNotNull(this.mediaModel);
    }

    public ListState getViewModelState() {
        return this.viewModelState;
    }

    public boolean isBusy() {
        return this.mediaModel.getIsLoadingRelated();
    }

    public void load(boolean forceRefresh) {
        setUpdateTypesToCheck(EnumSet.of(UpdateType.MediaItemDetailRelated));
        this.mediaModel.loadRelated(forceRefresh);
    }

    protected void onStartOverride() {
        this.mediaModel.addObserver(this);
    }

    protected void onStopOverride() {
        this.mediaModel.removeObserver(this);
    }

    public void updateOverride(AsyncResult<UpdateData> asyncResult) {
        UpdateType type = ((UpdateData) asyncResult.getResult()).getUpdateType();
        XLELog.Diagnostic("RelatedViewModel", "update received " + type);
        switch (type) {
            case MediaItemDetailRelated:
                if (!((UpdateData) asyncResult.getResult()).getIsFinal()) {
                    if (this.viewModelState != ListState.ValidContentState) {
                        if (!isBusy()) {
                            this.viewModelState = ListState.NoContentState;
                            break;
                        } else {
                            this.viewModelState = ListState.LoadingState;
                            break;
                        }
                    }
                } else if (asyncResult.getException() == null || this.viewModelState == ListState.ValidContentState) {
                    if (asyncResult.getException() == null) {
                        if (!hasRelated()) {
                            this.viewModelState = ListState.NoContentState;
                            break;
                        } else {
                            this.viewModelState = ListState.ValidContentState;
                            break;
                        }
                    }
                } else {
                    this.viewModelState = ListState.ErrorState;
                    break;
                }
                break;
            case MediaItemDetail:
                if (((UpdateData) asyncResult.getResult()).getIsFinal() && !isBusy()) {
                    if (asyncResult.getException() == null || this.viewModelState == ListState.ValidContentState) {
                        if (asyncResult.getException() == null) {
                            if (!hasRelated()) {
                                this.viewModelState = ListState.NoContentState;
                                break;
                            } else {
                                this.viewModelState = ListState.ValidContentState;
                                break;
                            }
                        }
                    }
                    this.viewModelState = ListState.NoContentState;
                    break;
                }
                break;
            default:
                XLELog.Diagnostic("RelatedViewModel", "Unexpceted update type " + type.toString());
                break;
        }
        this.adapter.updateView();
    }

    public final void onUpdateFinished() {
        if (checkErrorCode(UpdateType.MediaItemDetailRelated, XLEErrorCode.FAILED_TO_GET_RELATED)) {
            if (getViewModelState() == ListState.ValidContentState) {
                showError(R.string.details_related_list_error);
            } else {
                this.viewModelState = ListState.ErrorState;
                this.adapter.updateView();
            }
        }
        super.onUpdateFinished();
    }

    public String getTitle() {
        return this.mediaModel.getTitle();
    }

    public void NavigateToRelatedItemDetails(EDSV2MediaItem item) {
        navigateToAppOrMediaDetails(item);
    }

    private boolean hasRelated() {
        return this.mediaModel.getRelated() != null && this.mediaModel.getRelated().size() > 0;
    }
}
