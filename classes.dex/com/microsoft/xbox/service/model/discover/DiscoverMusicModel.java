package com.microsoft.xbox.service.model.discover;

import com.microsoft.xbox.service.model.ModelBase;
import com.microsoft.xbox.service.model.UpdateData;
import com.microsoft.xbox.service.model.UpdateType;
import com.microsoft.xbox.service.network.managers.ServiceManagerFactory;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.DataLoaderTask;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.network.IDataLoaderRunnable;
import java.util.Date;

public class DiscoverMusicModel extends ModelBase<DiscoverAllMusic> {
    private DiscoverAllMusic discoverMusicData;

    private static class AllMusicInfoModelContainer {
        private static DiscoverMusicModel instance = new DiscoverMusicModel();

        private AllMusicInfoModelContainer() {
        }

        private static void reset() {
            instance = new DiscoverMusicModel();
        }
    }

    private class DiscoverMusicRunner extends IDataLoaderRunnable<DiscoverAllMusic> {
        private DiscoverMusicModel caller;

        public DiscoverMusicRunner(DiscoverMusicModel caller) {
            this.caller = caller;
        }

        public DiscoverAllMusic buildData() throws XLEException {
            return ServiceManagerFactory.getInstance().getDiscoverServiceManager().getAllMusicData();
        }

        public void onPreExecute() {
        }

        public void onPostExcute(AsyncResult<DiscoverAllMusic> result) {
            this.caller.onGetAllMusicDataCompleted(result);
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_GET_ALLMUSIC;
        }
    }

    public static DiscoverMusicModel getInstance() {
        return AllMusicInfoModelContainer.instance;
    }

    public void reset() {
        XLEAssert.assertIsUIThread();
        clearObserver();
        AllMusicInfoModelContainer.reset();
    }

    public DiscoverAllMusic getMusicInfo() {
        return this.discoverMusicData;
    }

    public void getAllMusicData(boolean forceLoad) {
        if (shouldRefresh() || forceLoad) {
            XLELog.Diagnostic("AllMusicInfoModel", "requesting music data ...");
            this.isLoading = true;
            new DataLoaderTask(0, new DiscoverMusicRunner(this)).execute();
        }
        notifyObservers(new AsyncResult(new UpdateData(UpdateType.DiscoverMusicData, this.isLoading), this, null));
    }

    private void onGetAllMusicDataCompleted(AsyncResult<DiscoverAllMusic> result) {
        XLELog.Diagnostic("DisocverMusicModel", "onGetAllMusicDataCompleted called");
        if (result.getException() == null) {
            this.discoverMusicData = (DiscoverAllMusic) result.getResult();
            this.lastRefreshTime = new Date();
            this.discoverMusicData.retrievedTime = this.lastRefreshTime;
        } else {
            XLELog.Diagnostic("DisocverMusicModel", "GetAllMusicData failed with exception " + result.getException().toString());
        }
        this.isLoading = false;
        notifyObservers(new AsyncResult(new UpdateData(UpdateType.DiscoverMusicData, true), this, result.getException()));
    }
}
