package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.UpdateData;
import com.microsoft.xbox.service.model.UpdateType;
import com.microsoft.xbox.service.model.edsv2.EDSV2SearchDataModel;
import com.microsoft.xbox.service.model.edsv2.EDSV2SearchFilterCount;
import com.microsoft.xbox.service.model.edsv2.EDSV2SearchFilterType;
import com.microsoft.xbox.service.model.edsv2.EDSV2SearchResultItem;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.network.ListState;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.app.XboxMobileOmnitureTracking;
import com.microsoft.xbox.xle.app.activity.SearchFilterActivity;
import com.microsoft.xbox.xle.app.adapter.AdapterFactory;
import java.util.EnumSet;
import java.util.List;

public class SearchResultsActivityViewModel extends ViewModelBase {
    public static final int MAX_SEARCH_RESULT_ITEMS = 500;
    private boolean isLoadMoreFinished;
    private EDSV2SearchDataModel model;
    private String searchTag;
    private EDSV2SearchFilterType selectedFilter;
    private ListState viewModelState;

    public SearchResultsActivityViewModel() {
        boolean z = false;
        this.viewModelState = ListState.LoadingState;
        this.adapter = AdapterFactory.getInstance().getSearchDataResultAdapter(this);
        this.model = EDSV2SearchDataModel.getInstance();
        this.searchTag = XLEGlobalData.getInstance().getSearchTag();
        this.selectedFilter = XLEGlobalData.getInstance().getSelectedFilter();
        this.isLoadMoreFinished = false;
        if (this.searchTag != null) {
            z = true;
        }
        XLEAssert.assertTrue(z);
    }

    public void onRehydrate() {
        this.adapter = AdapterFactory.getInstance().getSearchDataResultAdapter(this);
    }

    public boolean isBusy() {
        return this.model.getIsLoading();
    }

    public boolean isLoadMoreFinished() {
        return this.isLoadMoreFinished;
    }

    public List<EDSV2SearchResultItem> getSearchResult() {
        return this.model.getSearchResult();
    }

    public String getSearchTag() {
        return this.searchTag;
    }

    public String getSearchResultTitle() {
        return String.format(XLEApplication.Resources.getString(R.string.search_result_title_tablet), new Object[]{getSearchTag()});
    }

    public String getImpressionGuid() {
        return this.model.getImpressionGuid();
    }

    public String getSearchDataCount() {
        return SearchHelper.formatSearchFilterCountString(this.selectedFilter, this.selectedFilter == EDSV2SearchFilterType.SEARCHFILTERTYPE_MUSIC ? this.model.getSearchFilterCount(this.selectedFilter) + this.model.getSearchFilterCount(EDSV2SearchFilterType.SEARCHFILTERTYPE_MUSICARTIST) : this.model.getSearchFilterCount(this.selectedFilter), true);
    }

    public String getSearchFilterTypeString() {
        return SearchHelper.getSearchFilterString(this.selectedFilter);
    }

    public ListState getViewModelState() {
        return this.viewModelState;
    }

    public void load(boolean forceRefresh) {
        setUpdateTypesToCheck(EnumSet.of(UpdateType.SearchSummaryResult));
        this.model.doSearch(forceRefresh, JavaUtil.urlEncode(this.searchTag), this.selectedFilter);
    }

    protected void onStartOverride() {
        if (this.selectedFilter != XLEGlobalData.getInstance().getSelectedFilter()) {
            this.selectedFilter = XLEGlobalData.getInstance().getSelectedFilter();
            setListPosition(0, 0);
        }
        this.model.addObserver(this);
    }

    protected void onStopOverride() {
        this.model.removeObserver(this);
    }

    public void updateOverride(AsyncResult<UpdateData> asyncResult) {
        UpdateType type = ((UpdateData) asyncResult.getResult()).getUpdateType();
        switch (type) {
            case SearchSummaryResult:
                if (asyncResult.getException() == null || !((UpdateData) asyncResult.getResult()).getIsFinal() || this.model.getSearchResult() != null) {
                    if (!this.model.getIsLoading() || this.model.getSearchResult() != null) {
                        if (this.model.getSearchResult() != null && this.model.getSearchResult().size() != 0) {
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
            case MoreSearchSummaryResult:
                if (((UpdateData) asyncResult.getResult()).getIsFinal() && this.model.getSearchResult() != null) {
                    this.isLoadMoreFinished = true;
                    break;
                } else {
                    this.isLoadMoreFinished = false;
                    break;
                }
                break;
            default:
                XLELog.Diagnostic("SearchDataResultActivityViewModel", "Unexpceted update type " + type.toString());
                break;
        }
        this.adapter.updateView();
    }

    protected void onUpdateFinished() {
        if (checkErrorCode(UpdateType.MoreSearchSummaryResult, XLEErrorCode.FAILED_TO_GET_MORE_SEARCH_SUMMARY_DATA) && this.model.getSearchResult() != null) {
            showError(R.string.toast_search_data_result_error);
        }
        super.onUpdateFinished();
    }

    public void NavigateToSearchResultDetails(EDSV2SearchResultItem item) {
        item.setImpressionGuid(getImpressionGuid());
        navigateToAppOrMediaDetails(item);
    }

    public void NavigateToSearchFilter() {
        if (this.selectedFilter != EDSV2SearchFilterType.SEARCHFILTERTYPE_ALL || this.model.getSearchFilterCount(EDSV2SearchFilterType.SEARCHFILTERTYPE_ALL) > 0) {
            XLEGlobalData.getInstance().setSearchResultFilterCountList(getFilterCount());
            NavigateTo(SearchFilterActivity.class);
        }
    }

    public List<EDSV2SearchFilterCount> getFilterCount() {
        return this.model.getFilterCount();
    }

    public void loadMore() {
        setUpdateTypesToCheck(EnumSet.of(UpdateType.MoreSearchSummaryResult));
        this.model.loadMoreSearchResults();
        if (!this.model.getIsLoading()) {
            XboxMobileOmnitureTracking.TrackSearchViewMore(Integer.toString(this.model.getPageCount()));
        }
    }

    public void search(String searchTag) {
        if (isNeedStartNewSearch(searchTag)) {
            this.selectedFilter = XLEGlobalData.getInstance().getSelectedFilter();
            this.searchTag = searchTag;
            if (SearchHelper.checkValidSearchTag(this.searchTag)) {
                XLEGlobalData.getInstance().setSearchTag(this.searchTag);
                XLEGlobalData.getInstance().setSelectedFilter(this.selectedFilter);
                this.model.doSearch(true, JavaUtil.urlEncode(this.searchTag), this.selectedFilter);
            }
        }
    }

    private boolean isNeedStartNewSearch(String searchTag) {
        if (this.viewModelState != ListState.ErrorState && !JavaUtil.isNullOrEmpty(searchTag) && this.searchTag.equalsIgnoreCase(searchTag) && this.selectedFilter == XLEGlobalData.getInstance().getSelectedFilter()) {
            return false;
        }
        return true;
    }

    public boolean isNeedLoadMore() {
        if (getSearchResult() != null && getSearchResult().size() < this.model.getSearchFilterCount(this.selectedFilter) && getSearchResult().size() < MAX_SEARCH_RESULT_ITEMS) {
            return true;
        }
        return false;
    }
}
