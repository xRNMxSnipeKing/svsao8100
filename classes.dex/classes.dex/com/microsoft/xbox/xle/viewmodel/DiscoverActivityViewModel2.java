package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.xbox.service.model.DiscoverModel2;
import com.microsoft.xbox.service.model.MeProfileModel;
import com.microsoft.xbox.service.model.UpdateData;
import com.microsoft.xbox.service.model.UpdateType;
import com.microsoft.xbox.service.model.edsv2.EDSV2DiscoverData;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaItem;
import com.microsoft.xbox.service.model.edsv2.EDSV2ProgrammingMediaItem;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.network.ListState;
import com.microsoft.xbox.xle.app.XboxMobileOmnitureTracking;
import com.microsoft.xbox.xle.app.activity.ActivitySummaryActivity;
import com.microsoft.xbox.xle.app.adapter.AdapterFactory;
import java.util.EnumSet;

public class DiscoverActivityViewModel2 extends PivotViewModelBase {
    private DiscoverModel2 model;
    private ListState viewModelState;

    public DiscoverActivityViewModel2() {
        this.viewModelState = ListState.LoadingState;
        this.adapter = AdapterFactory.getInstance().getDiscoverAdapter2(this);
        this.model = DiscoverModel2.getInstance();
    }

    public void onRehydrate() {
        this.adapter = AdapterFactory.getInstance().getDiscoverAdapter2(this);
    }

    public boolean isBusy() {
        return this.model.getIsLoading();
    }

    public EDSV2DiscoverData getDiscoverList() {
        return this.model.getDiscoverList();
    }

    public ListState getViewModelState() {
        return this.viewModelState;
    }

    public void load(boolean forceRefresh) {
        setUpdateTypesToCheck(EnumSet.of(UpdateType.DiscoverData));
        this.model.loadDiscoverList(forceRefresh);
    }

    protected void onStartOverride() {
        this.model.addObserver(this);
    }

    protected void onStopOverride() {
        this.model.removeObserver(this);
    }

    public void updateOverride(AsyncResult<UpdateData> asyncResult) {
        UpdateType type = ((UpdateData) asyncResult.getResult()).getUpdateType();
        switch (type) {
            case DiscoverData:
                if (!((UpdateData) asyncResult.getResult()).getIsFinal() || (asyncResult.getException() == null && this.model.getDiscoverList() != null)) {
                    if (this.model.getDiscoverList() != null) {
                        this.viewModelState = ListState.ValidContentState;
                        break;
                    } else {
                        this.viewModelState = ListState.LoadingState;
                        break;
                    }
                }
                this.viewModelState = ListState.ErrorState;
                break;
                break;
            default:
                XLELog.Diagnostic("DiscoverActivityViewModel", "Unexpceted update type " + type.toString());
                break;
        }
        this.adapter.updateView();
    }

    public void navigateToItemDetails(EDSV2MediaItem item) {
        XboxMobileOmnitureTracking.TrackDiscoverContentClick(item.getTitle(), MeProfileModel.getModel().getLegalLocale());
        if ((item instanceof EDSV2ProgrammingMediaItem) && item.getIsProgrammingOverride()) {
            navigateToAppOrMediaDetails(item, true, ActivitySummaryActivity.class);
        } else {
            navigateToAppOrMediaDetails(item);
        }
    }
}
