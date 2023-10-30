package com.microsoft.xbox.service.model;

import com.microsoft.xbox.service.model.sls.ConsolePresenceInfo;
import com.microsoft.xbox.service.network.managers.ServiceManagerFactory;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.DataLoaderTask;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.network.IDataLoaderRunnable;
import java.util.Date;

public class ConsolePresenceModel extends ModelBase<ConsolePresenceInfo> {
    private boolean isConsoleOnline = false;
    private String mediaId = null;
    private long titleId = 0;

    private static class ConsolePresenceModelModelHolder {
        private static ConsolePresenceModel instance = new ConsolePresenceModel();

        private ConsolePresenceModelModelHolder() {
        }

        private static void reset() {
            XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
            instance = new ConsolePresenceModel();
        }
    }

    private class LoadPresenceRunner extends IDataLoaderRunnable<ConsolePresenceInfo> {
        private ConsolePresenceModel caller;

        public LoadPresenceRunner(ConsolePresenceModel caller) {
            this.caller = caller;
        }

        public void onPreExecute() {
        }

        public ConsolePresenceInfo buildData() throws XLEException {
            return ServiceManagerFactory.getInstance().getSLSServiceManager().getConsolePresence();
        }

        public void onPostExcute(AsyncResult<ConsolePresenceInfo> result) {
            this.caller.onGetPresenceInfoCompleted(result);
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_GET_CONSOLE_PRESENCE;
        }
    }

    public static ConsolePresenceModel getInstance() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        return ConsolePresenceModelModelHolder.instance;
    }

    public static void reset() {
        getInstance().clearObserver();
        ConsolePresenceModelModelHolder.reset();
    }

    public String getMediaId() {
        return this.mediaId;
    }

    public long getTitleId() {
        return this.titleId;
    }

    public boolean getIsConsoleOnline() {
        return this.isConsoleOnline;
    }

    public boolean getIsLoading() {
        return this.isLoading;
    }

    public void loadConsolePresence() {
        if (this.isLoading) {
            XLELog.Error("ConsolePresenceModel", "already loading, should not happen!");
            return;
        }
        this.isLoading = true;
        XLELog.Diagnostic("ConsolePresenceModel", "load started");
        new DataLoaderTask(0, new LoadPresenceRunner(this)).execute();
    }

    private void onGetPresenceInfoCompleted(AsyncResult<ConsolePresenceInfo> result) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        this.isLoading = false;
        if (result.getException() == null && result.getResult() != null) {
            ConsolePresenceInfo info = (ConsolePresenceInfo) result.getResult();
            this.mediaId = info.getMediaId();
            this.titleId = info.getTitleId();
            this.isConsoleOnline = info.getIsOnline();
            this.lastRefreshTime = new Date();
            XLELog.Diagnostic("ConsolePresenceModel", "data loaded, titleId " + this.titleId);
            XLELog.Diagnostic("ConsolePresenceModel", "data loaded, mediaId " + this.mediaId);
        }
        notifyObservers(new AsyncResult(new UpdateData(UpdateType.ConsolePresence, true), this, result.getException()));
    }
}
