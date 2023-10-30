package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.xbox.service.model.MessageModel;
import com.microsoft.xbox.service.model.PopularSearchModel;
import com.microsoft.xbox.service.model.SearchTermData;
import com.microsoft.xbox.service.model.UpdateData;
import com.microsoft.xbox.service.model.UpdateType;
import com.microsoft.xbox.service.model.edsv2.EDSV2SearchFilterType;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.network.ListState;
import com.microsoft.xbox.xle.app.XboxMobileOmnitureTracking;
import com.microsoft.xbox.xle.app.activity.SearchResultsActivity;
import com.microsoft.xbox.xle.app.adapter.AdapterFactory;
import java.util.EnumSet;
import java.util.List;

public class SearchActivityViewModel extends PivotViewModelBase {
    private PopularSearchModel model;
    private String searchTag;
    private ListState viewModelState;

    public SearchActivityViewModel() {
        this.viewModelState = ListState.LoadingState;
        this.adapter = AdapterFactory.getInstance().getSearchDataAdapter(this);
        this.model = PopularSearchModel.getInstance();
    }

    public void onRehydrate() {
        this.adapter = AdapterFactory.getInstance().getSearchDataAdapter(this);
    }

    public boolean isBusy() {
        return this.model.getIsLoading();
    }

    public List<SearchTermData> getPopularNow() {
        return this.model.getPopularNow();
    }

    public String getSearchTag() {
        return this.searchTag;
    }

    public void setSearchTag(String searchTag) {
        this.searchTag = searchTag;
    }

    public ListState getViewModelState() {
        return this.viewModelState;
    }

    public void load(boolean forceRefresh) {
        setUpdateTypesToCheck(EnumSet.of(UpdateType.PopularNow));
        this.model.loadPopularNowData(forceRefresh);
        MessageModel.getInstance().loadMessageList(forceRefresh);
    }

    protected void onStartOverride() {
        this.model.addObserver(this);
        this.searchTag = XLEGlobalData.getInstance().getSearchTag();
    }

    protected void onStopOverride() {
        this.model.removeObserver(this);
    }

    public void updateOverride(AsyncResult<UpdateData> asyncResult) {
        UpdateType type = ((UpdateData) asyncResult.getResult()).getUpdateType();
        switch (type) {
            case PopularNow:
                if (asyncResult.getException() == null || !((UpdateData) asyncResult.getResult()).getIsFinal() || this.model.getPopularNow() != null) {
                    if (this.model.getPopularNow() != null) {
                        if (this.model.getPopularNow() != null && this.model.getPopularNow().size() != 0) {
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
                this.viewModelState = ListState.ErrorState;
                break;
                break;
            default:
                XLELog.Diagnostic("SearchDataActivityViewModel", "Unexpceted update type " + type.toString());
                break;
        }
        this.adapter.updateView();
    }

    public void NavigateToSearchResultDetails(String searchTag) {
        if (SearchHelper.checkValidSearchTag(searchTag)) {
            XboxMobileOmnitureTracking.TrackSearchClick();
            if (!searchTag.equalsIgnoreCase(XLEGlobalData.getInstance().getSearchTag())) {
                XLEGlobalData.getInstance().setSelectedFilter(EDSV2SearchFilterType.SEARCHFILTERTYPE_ALL);
            }
            XLEGlobalData.getInstance().setSearchTag(searchTag);
            NavigateTo(SearchResultsActivity.class);
        }
    }

    public void onSearchBarClear() {
        this.searchTag = "";
    }
}
