package com.microsoft.xbox.service.model;

import com.microsoft.xbox.service.model.edsv2.EDSV2DiscoverData;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaItem;
import com.microsoft.xbox.service.model.edsv2.EDSV2ProgrammingMediaItem;
import com.microsoft.xbox.service.model.serialization.ProgrammingContentManifest;
import com.microsoft.xbox.service.model.serialization.ProgrammingSlotGroup;
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
import java.util.List;

public class DiscoverModel2 extends ModelBase<EDSV2DiscoverData> {
    private XLEException discoverException;
    private EDSV2DiscoverData discoverList;
    private GetDiscoverListRunner getDiscoverListRunner;
    private GetProgrammingOverrideRunner getProgrammingOverrideRunner;
    private boolean isLoadingProgrammingMediaItem;
    private Date lastRefreshProgrammingMediaItemTime;
    private EDSV2DiscoverData mergedList;
    private XLEException programmingException;
    private EDSV2ProgrammingMediaItem programmingMediaItem;

    private static class DiscoverModelHolder {
        private static DiscoverModel2 instance = new DiscoverModel2();

        private DiscoverModelHolder() {
        }

        private static void reset() {
            XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
            DiscoverModel2.getInstance().clearObserver();
            instance = new DiscoverModel2();
        }
    }

    private class GetDiscoverListRunner extends IDataLoaderRunnable<EDSV2DiscoverData> {
        private DiscoverModel2 caller;

        public GetDiscoverListRunner(DiscoverModel2 caller) {
            this.caller = caller;
        }

        public EDSV2DiscoverData buildData() throws XLEException {
            return ServiceManagerFactory.getInstance().getEDSServiceManager().getProgrammingItems2();
        }

        public void onPreExecute() {
        }

        public void onPostExcute(AsyncResult<EDSV2DiscoverData> result) {
            this.caller.updateWithNewData(result);
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_GET_DISCOVER;
        }
    }

    private class GetProgrammingOverrideRunner extends IDataLoaderRunnable<EDSV2ProgrammingMediaItem> {
        private DiscoverModel2 caller;

        public GetProgrammingOverrideRunner(DiscoverModel2 caller) {
            this.caller = caller;
        }

        public EDSV2ProgrammingMediaItem buildData() throws XLEException {
            ProgrammingContentManifest manifest = ServiceManagerFactory.getInstance().getProgrammingServiceManager().getProgrammingContentManifest();
            if (!(manifest == null || manifest.Content == null || manifest.Content.size() <= 0)) {
                ProgrammingSlotGroup slotGroup = (ProgrammingSlotGroup) manifest.Content.get(0);
                if (slotGroup.Slot != null) {
                    return new EDSV2ProgrammingMediaItem(slotGroup.Slot);
                }
            }
            throw new XLEException(getDefaultErrorCode(), "Failed to parse the programing override");
        }

        public void onPreExecute() {
        }

        public void onPostExcute(AsyncResult<EDSV2ProgrammingMediaItem> result) {
            this.caller.updateWithNewProgrammingContentManifest(result);
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_GET_PROGRAMMINGCONTENT;
        }
    }

    private DiscoverModel2() {
        this.isLoadingProgrammingMediaItem = false;
        this.lastRefreshProgrammingMediaItemTime = null;
        this.discoverList = null;
        this.isLoading = false;
        this.getDiscoverListRunner = new GetDiscoverListRunner(this);
        this.getProgrammingOverrideRunner = new GetProgrammingOverrideRunner(this);
    }

    public static DiscoverModel2 getInstance() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        return DiscoverModelHolder.instance;
    }

    public static void reset() {
        DiscoverModelHolder.reset();
    }

    public EDSV2DiscoverData getDiscoverList() {
        return this.mergedList;
    }

    public boolean getIsLoading() {
        return this.isLoading || this.isLoadingProgrammingMediaItem;
    }

    public void loadDiscoverList(boolean forceRefresh) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        if (forceRefresh) {
            this.discoverList = null;
            this.programmingMediaItem = null;
            this.discoverException = null;
            this.programmingException = null;
        }
        loadInternal(forceRefresh, UpdateType.DiscoverData, this.getDiscoverListRunner);
        loadProgrammingMediaItem(forceRefresh);
    }

    public void loadProgrammingMediaItem(boolean forceRefresh) {
        boolean z = true;
        if (this.isLoadingProgrammingMediaItem || !(forceRefresh || shouldRefreshProgrammingMediaItem())) {
            UpdateType updateType = UpdateType.DiscoverData;
            if (getIsLoading()) {
                z = false;
            }
            notifyObservers(new AsyncResult(new UpdateData(updateType, z), this, null));
            return;
        }
        this.isLoadingProgrammingMediaItem = true;
        new DataLoaderTask(0, this.getProgrammingOverrideRunner).execute();
        notifyObservers(new AsyncResult(new UpdateData(UpdateType.DiscoverData, false), this, null));
    }

    private boolean shouldRefreshProgrammingMediaItem() {
        if (this.lastRefreshProgrammingMediaItemTime == null || new Date().getTime() - this.lastRefreshProgrammingMediaItemTime.getTime() > this.lifetime) {
            return true;
        }
        XLELog.Info("DiscoverModelWithProgrammingMediaItem", "less than lifetime, should not refresh");
        return false;
    }

    public void updateWithNewData(AsyncResult<EDSV2DiscoverData> asyncResult) {
        super.updateWithNewData(asyncResult);
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        this.discoverException = asyncResult.getException();
        this.discoverList = (EDSV2DiscoverData) asyncResult.getResult();
        XLELog.Info("DiscoverModelWithProgrammingMediaItem", new StringBuilder().append("updateWithNewData: discover list is ").append(this.discoverList).toString() == null ? "null" : "not null");
        mergeListData();
    }

    public void updateWithNewProgrammingContentManifest(AsyncResult<EDSV2ProgrammingMediaItem> asyncResult) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        this.isLoadingProgrammingMediaItem = false;
        this.programmingException = asyncResult.getException();
        this.programmingMediaItem = (EDSV2ProgrammingMediaItem) asyncResult.getResult();
        if (this.programmingException == null && this.programmingMediaItem != null) {
            this.lastRefreshTime = new Date();
        }
        XLELog.Info("DiscoverModelWithProgrammingMediaItem", new StringBuilder().append("updateWithNewData: program override is ").append(this.programmingMediaItem).toString() == null ? "null" : "not null");
        mergeListData();
    }

    private void mergeListData() {
        boolean discoverLoaded;
        if (this.discoverList == null && this.discoverException == null) {
            discoverLoaded = false;
        } else {
            discoverLoaded = true;
        }
        boolean programmingLoaded;
        if (this.programmingMediaItem == null && this.programmingException == null) {
            programmingLoaded = false;
        } else {
            programmingLoaded = true;
        }
        if (discoverLoaded && programmingLoaded) {
            if (!(this.discoverException != null || this.discoverList == null || this.discoverList.getBrowseItems() == null)) {
                this.mergedList = this.discoverList;
                if (this.programmingException == null && this.programmingMediaItem != null) {
                    List<EDSV2MediaItem> featureItems = this.mergedList.getBrowseItems();
                    if (featureItems.size() > 0) {
                        featureItems.remove(0);
                    }
                    featureItems.add(0, this.programmingMediaItem);
                    XLELog.Info("DiscoverModelWithProgrammingMediaItem", "mergeListData: program override replaced");
                }
            }
            XLELog.Info("DiscoverModelWithProgrammingMediaItem", "mergeListData: load complete");
            notifyObservers(new AsyncResult(new UpdateData(UpdateType.DiscoverData, true), this, this.discoverException));
            return;
        }
        XLELog.Info("DiscoverModelWithProgrammingMediaItem", "mergeListData: Both are not loaded yet");
        notifyObservers(new AsyncResult(new UpdateData(UpdateType.DiscoverData, false), this, null));
    }
}
