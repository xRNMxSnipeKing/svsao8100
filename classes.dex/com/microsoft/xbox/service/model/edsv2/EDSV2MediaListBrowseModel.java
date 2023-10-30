package com.microsoft.xbox.service.model.edsv2;

import com.microsoft.xbox.service.model.JTitleType;
import com.microsoft.xbox.service.model.LaunchType;
import com.microsoft.xbox.service.model.UpdateData;
import com.microsoft.xbox.service.model.UpdateType;
import com.microsoft.xbox.service.network.managers.ServiceManagerFactory;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.DataLoaderTask;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.network.IDataLoaderRunnable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;

public abstract class EDSV2MediaListBrowseModel<ParentT extends EDSV2MediaItem, ChildT extends EDSV2MediaItem, RelatedT extends EDSV2MediaItem> extends EDSV2MediaItemDetailModel<ParentT, RelatedT> {
    protected ArrayList<ChildT> browseListData;
    private BrowseEDSV2MediaItemListRunner browseRunner = new BrowseEDSV2MediaItemListRunner();
    private boolean isLoadingChild = false;
    private Date lastRefreshChildTime;

    private class BrowseEDSV2MediaItemListRunner extends IDataLoaderRunnable<ArrayList<ChildT>> {
        private BrowseEDSV2MediaItemListRunner() {
        }

        public void onPreExecute() {
        }

        public ArrayList<ChildT> buildData() throws XLEException {
            return ServiceManagerFactory.getInstance().getEDSServiceManager().browseMediaItemList(EDSV2MediaListBrowseModel.this.getCanonicalId(), EDSV2MediaListBrowseModel.this.getDesiredMediaItemType(), EDSV2MediaListBrowseModel.this.getParentMediaType(), null);
        }

        public void onPostExcute(AsyncResult<ArrayList<ChildT>> result) {
            EDSV2MediaListBrowseModel.this.onBrowseMediaItemListCompleted(result);
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_BROWSE_MEDIA_ITEM_LIST;
        }
    }

    protected abstract int getDesiredMediaItemType();

    protected EDSV2MediaListBrowseModel(EDSV2MediaItem mediaItem) {
        super(mediaItem);
    }

    public String getCanonicalId() {
        return this.detailData.getCanonicalId();
    }

    public String getPartnerMediaId() {
        return this.detailData.getPartnerMediaId();
    }

    public long getTitleId() {
        return this.detailData.getTitleId();
    }

    public String getTitle() {
        return this.detailData.getTitle();
    }

    public int getParentMediaType() {
        return this.detailData.getMediaType();
    }

    public URI getParentImageUrl() {
        return this.detailData.getImageUrl();
    }

    public ArrayList<ChildT> getMediaItemListData() {
        return this.browseListData;
    }

    public ParentT getParentMediaItemDetailData() {
        return this.detailData;
    }

    public LaunchType getLaunchType() {
        return LaunchType.UnknownLaunchType;
    }

    public JTitleType getTitleType() {
        return JTitleType.Standard;
    }

    public boolean getIsLoadingChild() {
        return this.isLoadingChild;
    }

    public void loadDetails(boolean forceRefresh) {
        super.load(forceRefresh);
    }

    public void load(boolean forceRefresh) {
        boolean z = true;
        if (this.isLoadingChild || !(forceRefresh || shouldRefreshChild())) {
            UpdateType updateType = UpdateType.MediaListBrowse;
            if (this.isLoadingChild) {
                z = false;
            }
            notifyObservers(new AsyncResult(new UpdateData(updateType, z), this, null));
            return;
        }
        this.isLoadingChild = true;
        new DataLoaderTask(0, this.browseRunner).execute();
        notifyObservers(new AsyncResult(new UpdateData(UpdateType.MediaListBrowse, false), this, null));
    }

    protected void onBrowseMediaItemListCompleted(AsyncResult<ArrayList<ChildT>> result) {
        this.isLoadingChild = false;
        if (result.getException() == null) {
            this.lastRefreshChildTime = new Date();
            ArrayList<ChildT> data = (ArrayList) result.getResult();
            if (data != null) {
                String partnerMediaId = this.detailData.getPartnerMediaId();
                updateWithNewData(data);
                EDSV2MediaItemModel.updateModelInCache(this, partnerMediaId);
            }
        }
        notifyObservers(new AsyncResult(new UpdateData(UpdateType.MediaListBrowse, true), this, result.getException()));
    }

    protected void updateWithNewData(ArrayList<ChildT> data) {
        this.lastRefreshTime = new Date();
        this.browseListData = data;
    }

    private boolean shouldRefreshChild() {
        if (this.lastRefreshChildTime == null || new Date().getTime() - this.lastRefreshChildTime.getTime() > this.lifetime) {
            return true;
        }
        XLELog.Info("EDSV2MediaListBrowseModel", "less than lifetime, should not refresh");
        return false;
    }
}
