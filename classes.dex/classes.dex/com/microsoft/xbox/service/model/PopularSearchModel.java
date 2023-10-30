package com.microsoft.xbox.service.model;

import com.microsoft.xbox.service.network.managers.ServiceManagerFactory;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.network.IDataLoaderRunnable;
import java.util.Date;
import java.util.List;

public class PopularSearchModel extends ModelBase<List<SearchTermData>> {
    private List<SearchTermData> popularNows;

    private static class PopularSearchModelHolder {
        private static PopularSearchModel instance = new PopularSearchModel();

        private PopularSearchModelHolder() {
        }

        private static void reset() {
            XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
            instance = new PopularSearchModel();
        }
    }

    private class GetPopularSearchTermsRunner extends IDataLoaderRunnable<List<SearchTermData>> {
        private PopularSearchModel caller;

        public GetPopularSearchTermsRunner(PopularSearchModel caller) {
            this.caller = caller;
        }

        public List<SearchTermData> buildData() throws XLEException {
            return ServiceManagerFactory.getInstance().getEDSServiceManager().GetPopularSearchTerms();
        }

        public void onPreExecute() {
        }

        public void onPostExcute(AsyncResult<List<SearchTermData>> result) {
            this.caller.onGetPopularSearchTermsFeedCompleted(result);
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_GET_POPULAR_SEARCH_DATA;
        }
    }

    private PopularSearchModel() {
        this.popularNows = null;
        this.isLoading = false;
    }

    public static PopularSearchModel getInstance() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        return PopularSearchModelHolder.instance;
    }

    public static void reset() {
        getInstance().clearObserver();
        PopularSearchModelHolder.reset();
    }

    public List<SearchTermData> getPopularNow() {
        return this.popularNows;
    }

    public void loadPopularNowData(boolean forceRefresh) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        loadInternal(true, UpdateType.PopularNow, new GetPopularSearchTermsRunner(this));
    }

    private void onGetPopularSearchTermsFeedCompleted(AsyncResult<List<SearchTermData>> asyncResult) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        if (asyncResult.getException() == null && asyncResult.getResult() != null) {
            this.popularNows = (List) asyncResult.getResult();
            this.lastRefreshTime = new Date();
        }
        this.isLoading = false;
        notifyObservers(new AsyncResult(new UpdateData(UpdateType.PopularNow, true), this, asyncResult.getException()));
    }
}
