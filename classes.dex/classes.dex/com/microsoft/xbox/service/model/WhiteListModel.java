package com.microsoft.xbox.service.model;

import com.microsoft.xbox.service.network.managers.IWhiteListServiceManager;
import com.microsoft.xbox.service.network.managers.ServiceManagerFactory;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.network.IDataLoaderRunnable;
import java.util.Date;
import java.util.HashSet;

public class WhiteListModel extends ModelBase<HashSet<String>> {
    private HashSet<String> allowedGamertagHash;
    private GetWhiteListRunner runner;
    private IWhiteListServiceManager serviceManager;

    private static class WhiteListModelContainer {
        private static WhiteListModel instance = new WhiteListModel();

        private WhiteListModelContainer() {
        }
    }

    private class GetWhiteListRunner extends IDataLoaderRunnable<HashSet<String>> {
        private WhiteListModel caller;

        public GetWhiteListRunner(WhiteListModel caller) {
            this.caller = caller;
        }

        public void onPreExecute() {
        }

        public HashSet<String> buildData() throws XLEException {
            return this.caller.serviceManager.getWhiteList();
        }

        public void onPostExcute(AsyncResult<HashSet<String>> result) {
            this.caller.onGetWhiteListComplted(result);
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_GET_WHITELIST;
        }
    }

    private WhiteListModel() {
        this.allowedGamertagHash = null;
        this.serviceManager = ServiceManagerFactory.getInstance().getWhiteListServiceManager();
        this.runner = new GetWhiteListRunner(this);
    }

    public static WhiteListModel getInstance() {
        return WhiteListModelContainer.instance;
    }

    public boolean isGamertagAllowed(String gamertag) {
        throw new UnsupportedOperationException();
    }

    private String byteArrayToHexString(byte[] array) {
        StringBuilder builder = new StringBuilder();
        for (int i : array) {
            builder.append(Integer.toString((i & 255) + 256, 16).substring(1));
        }
        return builder.toString().toUpperCase();
    }

    private void onGetWhiteListComplted(AsyncResult<HashSet<String>> result) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        if (result.getException() == null) {
            this.allowedGamertagHash = (HashSet) result.getResult();
            this.lastRefreshTime = new Date();
            XLELog.Diagnostic("WhitelistModel", "get whitelist completed");
        } else {
            XLELog.Error("WhitelistModel", "failed to get whitelist");
        }
        this.isLoading = false;
        notifyObservers(new AsyncResult(new UpdateData(UpdateType.WhiteListData, true), this, result.getException()));
    }

    public void load(boolean forceRefresh) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        loadInternal(forceRefresh, UpdateType.WhiteListData, this.runner);
    }
}
