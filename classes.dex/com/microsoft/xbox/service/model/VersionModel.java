package com.microsoft.xbox.service.model;

import com.microsoft.xbox.service.model.serialization.Version;
import com.microsoft.xbox.service.network.managers.IVersionCheckServiceManager;
import com.microsoft.xbox.service.network.managers.ServiceManagerFactory;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.network.IDataLoaderRunnable;
import com.microsoft.xle.test.interop.TestInterop;
import java.util.Date;

public class VersionModel extends ModelBase<Version> {
    private GetVersionRunner getVersionRunner;
    private int latestVersion;
    private String marketUrl;
    private int minVersion;
    private IVersionCheckServiceManager serviceManager;

    private static class VersionModelContainer {
        private static VersionModel instance = new VersionModel();

        private VersionModelContainer() {
        }
    }

    private class GetVersionRunner extends IDataLoaderRunnable<Version> {
        private VersionModel caller;

        public GetVersionRunner(VersionModel caller) {
            this.caller = caller;
        }

        public Version buildData() throws XLEException {
            return this.caller.serviceManager.getLatestVersion();
        }

        public void onPreExecute() {
        }

        public void onPostExcute(AsyncResult<Version> result) {
            this.caller.onGetVersionCompleted(result);
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_CHECK_UPDATE;
        }
    }

    private VersionModel() {
        this.minVersion = 0;
        this.latestVersion = 0;
        this.serviceManager = ServiceManagerFactory.getInstance().getVersionCheckServiceManager();
        this.getVersionRunner = new GetVersionRunner(this);
    }

    public static VersionModel getInstance() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        return VersionModelContainer.instance;
    }

    public boolean getHasUpdate(int currentVersionCode) {
        boolean z;
        if (Thread.currentThread() == ThreadManager.UIThread) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        return getLatestVersion() > currentVersionCode;
    }

    public boolean getMustUpdate(int currentVersionCode) {
        boolean z;
        if (Thread.currentThread() == ThreadManager.UIThread) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        return getMinimumVersion() > currentVersionCode;
    }

    public String getMarketUrl() {
        return this.marketUrl;
    }

    private int getLatestVersion() {
        return TestInterop.getLatestVersionAvailable(this.latestVersion);
    }

    private int getMinimumVersion() {
        return TestInterop.getMinimumVersionRequired(this.minVersion);
    }

    private void onGetVersionCompleted(AsyncResult<Version> result) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        if (result.getException() == null) {
            this.minVersion = ((Version) result.getResult()).min;
            this.latestVersion = ((Version) result.getResult()).latest;
            this.marketUrl = ((Version) result.getResult()).url;
            this.lastRefreshTime = new Date();
            XLELog.Diagnostic("VersionModel", "Version check completed " + this.latestVersion);
        } else {
            XLELog.Warning("VersionModel", "failed to get version");
        }
        this.isLoading = false;
        notifyObservers(new AsyncResult(new UpdateData(UpdateType.VersionData, true), this, result.getException()));
    }

    public void load(boolean forceRefresh) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        loadInternal(forceRefresh, UpdateType.VersionData, this.getVersionRunner);
    }
}
