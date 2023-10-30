package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.ActivityDetailModel;
import com.microsoft.xbox.service.model.UpdateData;
import com.microsoft.xbox.service.model.UpdateType;
import com.microsoft.xbox.service.model.edsv2.EDSV2ActivityItem;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaItem;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.network.ListState;
import com.microsoft.xbox.xle.app.adapter.AdapterFactory;
import java.net.URI;
import java.util.ArrayList;
import java.util.EnumSet;

public class ActivityGalleryActivityViewModel extends PivotViewModelBase {
    private EDSV2ActivityItem activityData = XLEGlobalData.getInstance().getSelectedActivityData();
    private ActivityDetailModel detailModel;
    private EDSV2MediaItem parentMediaItem = XLEGlobalData.getInstance().getActivityParentMediaItemData();
    private ListState viewModelState = ListState.LoadingState;

    public ActivityGalleryActivityViewModel() {
        XLEAssert.assertNotNull(this.activityData);
        XLEAssert.assertNotNull(this.parentMediaItem);
        this.adapter = AdapterFactory.getInstance().getActivityGalleryAdapter(this);
    }

    public ListState getViewModelState() {
        return this.viewModelState;
    }

    public String getTitle() {
        return this.activityData.getTitle();
    }

    public ArrayList<URI> getScreenshotUrls() {
        return this.activityData.getScreenshots();
    }

    public boolean isBusy() {
        return this.detailModel.getIsLoading();
    }

    public void load(boolean forceRefresh) {
        setUpdateTypesToCheck(EnumSet.of(UpdateType.ActivityDetail));
        this.detailModel.load(forceRefresh);
    }

    protected void onStartOverride() {
        this.detailModel = ActivityDetailModel.getModel(this.activityData, this.parentMediaItem);
        this.detailModel.addObserver(this);
        XLEGlobalData.getInstance().setSelectedActivityData(this.activityData);
    }

    protected void onStopOverride() {
        this.detailModel.removeObserver(this);
        this.detailModel = null;
    }

    public void onRehydrate() {
        this.adapter = AdapterFactory.getInstance().getActivityGalleryAdapter(this);
    }

    public void updateOverride(AsyncResult<UpdateData> asyncResult) {
        switch (((UpdateData) asyncResult.getResult()).getUpdateType()) {
            case ActivityDetail:
                if (((UpdateData) asyncResult.getResult()).getIsFinal()) {
                    if (asyncResult.getException() != null && this.detailModel.getActivityData() == null) {
                        this.viewModelState = ListState.ErrorState;
                        break;
                    }
                    this.activityData = this.detailModel.getActivityData();
                    if (this.activityData.getScreenshots() != null) {
                        if (this.activityData.getScreenshots().size() != 0) {
                            this.viewModelState = ListState.ValidContentState;
                            break;
                        } else {
                            this.viewModelState = ListState.NoContentState;
                            break;
                        }
                    }
                    this.viewModelState = ListState.LoadingState;
                    break;
                }
                break;
        }
        this.adapter.updateView();
    }

    protected void onUpdateFinished() {
        if (checkErrorCode(UpdateType.ActivityDetail, XLEErrorCode.FAILED_TO_GET_ACTIVITY_DETAIL)) {
            showError(R.string.toast_activity_overview_error);
        }
        super.onUpdateFinished();
    }
}
