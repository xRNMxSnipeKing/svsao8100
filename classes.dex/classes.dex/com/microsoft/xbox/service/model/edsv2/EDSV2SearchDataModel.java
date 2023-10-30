package com.microsoft.xbox.service.model.edsv2;

import com.microsoft.xbox.service.model.ModelBase;
import com.microsoft.xbox.service.model.UpdateData;
import com.microsoft.xbox.service.model.UpdateType;
import com.microsoft.xbox.service.network.managers.ServiceManagerFactory;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.network.IDataLoaderRunnable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EDSV2SearchDataModel extends ModelBase<EDSV2SearchResult> {
    private static final int DEFAULT_MAX_RESULT_COUNT = 25;
    private static final int MAX_SEARCH_ENCODED_TEXT_LENGTH = 250;
    private String currentSearchTag;
    private GetSearchResultMoreRunner getSearchResultMoreRunner;
    private GetSearchResultRunner getSearchResultRunner;
    private boolean isNeedResetTypeAllFiterResultCount;
    private int pageCount;
    private EDSV2SearchResult searchResultData;
    private EDSV2SearchFilterType selectedFilterType;
    private ArrayList<EDSV2SearchFilterCount> typeAllFiterResultCount;

    private static class EDSV2SearchDataModelHolder {
        private static EDSV2SearchDataModel instance = new EDSV2SearchDataModel();

        private EDSV2SearchDataModelHolder() {
        }

        private static void reset() {
            XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
            instance = new EDSV2SearchDataModel();
        }
    }

    private class GetSearchResultMoreRunner extends IDataLoaderRunnable<EDSV2SearchResult> {
        private EDSV2SearchDataModel caller;
        private boolean isCanceled = false;

        public GetSearchResultMoreRunner(EDSV2SearchDataModel caller) {
            this.caller = caller;
        }

        public void cancel() {
            this.isCanceled = true;
        }

        public EDSV2SearchResult buildData() throws XLEException {
            return ServiceManagerFactory.getInstance().getEDSServiceManager().searchMediaItems(EDSV2SearchDataModel.this.currentSearchTag, EDSV2SearchDataModel.this.selectedFilterType.getValue(), EDSV2SearchDataModel.this.searchResultData.getContinuationToken(), EDSV2SearchDataModel.DEFAULT_MAX_RESULT_COUNT);
        }

        public void onPreExecute() {
        }

        public void onPostExcute(AsyncResult<EDSV2SearchResult> result) {
            if (!this.isCanceled) {
                this.caller.onGetSearchDataMoreFeedCompleted(result);
            }
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_GET_MORE_SEARCH_SUMMARY_DATA;
        }
    }

    private class GetSearchResultRunner extends IDataLoaderRunnable<EDSV2SearchResult> {
        private EDSV2SearchDataModel caller;
        private boolean isCanceled = false;

        public GetSearchResultRunner(EDSV2SearchDataModel caller, String searchTag, EDSV2SearchFilterType selectedFilterType) {
            this.caller = caller;
        }

        public void cancel() {
            this.isCanceled = true;
        }

        public EDSV2SearchResult buildData() throws XLEException {
            EDSV2SearchResult eDSV2SearchResult = null;
            try {
                if (EDSV2SearchDataModel.this.currentSearchTag.getBytes("UTF-8").length < EDSV2SearchDataModel.MAX_SEARCH_ENCODED_TEXT_LENGTH) {
                    eDSV2SearchResult = ServiceManagerFactory.getInstance().getEDSServiceManager().searchMediaItems(EDSV2SearchDataModel.this.currentSearchTag, EDSV2SearchDataModel.this.selectedFilterType.getValue(), "", EDSV2SearchDataModel.DEFAULT_MAX_RESULT_COUNT);
                }
            } catch (Exception e) {
            }
            return eDSV2SearchResult;
        }

        public void onPreExecute() {
        }

        public void onPostExcute(AsyncResult<EDSV2SearchResult> result) {
            if (!this.isCanceled) {
                this.caller.onGetSearchDatasFeedCompleted(result);
            }
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_GET_SEARCH_SUMMARY_DATA;
        }
    }

    private EDSV2SearchDataModel() {
        this.searchResultData = null;
        this.isLoading = false;
        this.pageCount = 0;
    }

    public static EDSV2SearchDataModel getInstance() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        return EDSV2SearchDataModelHolder.instance;
    }

    public static void reset() {
        getInstance().clearObserver();
        EDSV2SearchDataModelHolder.reset();
    }

    private void cancel() {
        if (this.getSearchResultRunner != null) {
            this.getSearchResultRunner.cancel();
        }
        if (this.getSearchResultMoreRunner != null) {
            this.getSearchResultMoreRunner.cancel();
        }
    }

    public List<EDSV2SearchResultItem> getSearchResult() {
        if (this.searchResultData == null || this.searchResultData.getItems() == null) {
            return null;
        }
        return this.searchResultData.getItems();
    }

    public int getSearchFilterCount(EDSV2SearchFilterType filter) {
        return this.searchResultData == null ? 0 : this.searchResultData.getFilterTypeCount(filter);
    }

    public int getPageCount() {
        return this.pageCount;
    }

    public List<EDSV2SearchFilterCount> getFilterCount() {
        return this.typeAllFiterResultCount;
    }

    public String getImpressionGuid() {
        return this.searchResultData == null ? null : this.searchResultData.getImpressionGuid();
    }

    public void doSearch(boolean forceRefresh, String searchTag, EDSV2SearchFilterType selectedFilterType) {
        boolean z;
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        if (searchTag == null || searchTag.length() <= 0) {
            z = false;
        } else {
            z = true;
        }
        XLEAssert.assertTrue(z);
        if (forceRefresh || !searchTag.equalsIgnoreCase(this.currentSearchTag) || this.selectedFilterType != selectedFilterType || this.searchResultData == null) {
            if (searchTag.equalsIgnoreCase(this.currentSearchTag)) {
                this.isNeedResetTypeAllFiterResultCount = false;
            } else {
                this.isNeedResetTypeAllFiterResultCount = true;
            }
            this.searchResultData = null;
            this.currentSearchTag = searchTag;
            this.selectedFilterType = selectedFilterType;
            cancel();
            this.isLoading = false;
            this.getSearchResultRunner = new GetSearchResultRunner(this, searchTag, selectedFilterType);
            loadInternal(true, UpdateType.SearchSummaryResult, this.getSearchResultRunner);
            return;
        }
        notifyObservers(new AsyncResult(new UpdateData(UpdateType.SearchSummaryResult, true), this, null));
    }

    public void loadMoreSearchResults() {
        boolean z = false;
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        if (this.searchResultData != null) {
            z = true;
        }
        XLEAssert.assertTrue(z);
        if (JavaUtil.isNullOrEmpty(this.searchResultData.getContinuationToken())) {
            notifyObservers(new AsyncResult(new UpdateData(UpdateType.MoreSearchSummaryResult, true), this, null));
            return;
        }
        this.getSearchResultMoreRunner = new GetSearchResultMoreRunner(this);
        loadInternal(true, UpdateType.MoreSearchSummaryResult, this.getSearchResultMoreRunner);
    }

    private void onGetSearchDatasFeedCompleted(AsyncResult<EDSV2SearchResult> asyncResult) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        if (asyncResult.getException() == null && asyncResult.getResult() != null) {
            this.searchResultData = (EDSV2SearchResult) asyncResult.getResult();
            this.pageCount = 1;
            if (this.isNeedResetTypeAllFiterResultCount || this.selectedFilterType == EDSV2SearchFilterType.SEARCHFILTERTYPE_ALL) {
                this.typeAllFiterResultCount = this.searchResultData == null ? null : this.searchResultData.getFilterResultCount();
            }
            this.lastRefreshTime = new Date();
        }
        this.isLoading = false;
        notifyObservers(new AsyncResult(new UpdateData(UpdateType.SearchSummaryResult, true), this, asyncResult.getException()));
    }

    private void onGetSearchDataMoreFeedCompleted(AsyncResult<EDSV2SearchResult> asyncResult) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        if (asyncResult.getException() == null && asyncResult.getResult() != null) {
            EDSV2SearchResult searchResultMoreData = (EDSV2SearchResult) asyncResult.getResult();
            if (!(searchResultMoreData == null || searchResultMoreData.getItems() == null)) {
                this.searchResultData.getItems().addAll(searchResultMoreData.getItems());
                this.pageCount++;
                XLELog.Diagnostic("EdsV2SearchDataModel", "loaded page " + this.pageCount);
            }
            this.searchResultData.setContinuationToken(searchResultMoreData.getContinuationToken());
            this.lastRefreshTime = new Date();
        }
        this.isLoading = false;
        notifyObservers(new AsyncResult(new UpdateData(UpdateType.MoreSearchSummaryResult, true), this, asyncResult.getException()));
    }
}
