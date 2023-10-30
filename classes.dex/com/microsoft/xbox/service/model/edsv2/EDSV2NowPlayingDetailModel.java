package com.microsoft.xbox.service.model.edsv2;

import com.microsoft.xbox.service.model.JTitleType;
import com.microsoft.xbox.service.model.LaunchType;
import com.microsoft.xbox.service.model.UpdateData;
import com.microsoft.xbox.service.model.UpdateType;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.XLEObserver;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;

public class EDSV2NowPlayingDetailModel extends EDSV2MediaItemDetailModel<EDSV2MediaItem, EDSV2MediaItem> {
    private EDSV2MediaItemDetailModel internalModel;

    protected EDSV2NowPlayingDetailModel(EDSV2MediaItem mediaItem, EDSV2MediaItemDetailModel internalModel) {
        super(mediaItem);
        this.internalModel = internalModel;
    }

    public URI getImageUrl() {
        if (this.internalModel != null) {
            return this.internalModel.getImageUrl();
        }
        return super.getImageUrl();
    }

    public String getTitle() {
        if (this.internalModel != null) {
            return this.internalModel.getTitle();
        }
        return super.getTitle();
    }

    public int getMediaType() {
        if (this.internalModel != null) {
            return this.internalModel.getMediaType();
        }
        return super.getMediaType();
    }

    public String getDescription() {
        if (this.internalModel != null) {
            return this.internalModel.getDescription();
        }
        return super.getDescription();
    }

    public String getCanonicalId() {
        if (this.internalModel != null) {
            return this.internalModel.getCanonicalId();
        }
        return super.getCanonicalId();
    }

    public EDSV2MediaItem getMediaItemDetailData() {
        if (this.internalModel != null) {
            return this.internalModel.getMediaItemDetailData();
        }
        return super.getMediaItemDetailData();
    }

    public boolean getShouldCheckActivity() {
        if (this.internalModel != null) {
            return this.internalModel.getShouldCheckActivity();
        }
        return super.getShouldCheckActivity();
    }

    public boolean getIsLoading() {
        if (this.internalModel != null) {
            return this.internalModel.getIsLoading();
        }
        return super.getIsLoading();
    }

    public boolean getIsLoadingRelated() {
        if (this.internalModel != null) {
            return this.internalModel.getIsLoadingRelated();
        }
        return super.getIsLoadingRelated();
    }

    protected EDSV2MediaItem createMediaItem(EDSV2MediaItem mediaItem) {
        return mediaItem;
    }

    public int getMediaGroup() {
        return 0;
    }

    public LaunchType getLaunchType() {
        return LaunchType.UnknownLaunchType;
    }

    public JTitleType getTitleType() {
        return JTitleType.Unknown;
    }

    public ArrayList<EDSV2MediaItem> getRelated() {
        return this.internalModel == null ? null : this.internalModel.getRelated();
    }

    public boolean isGameType() {
        if (this.internalModel != null) {
            return this.internalModel.isGameType();
        }
        return false;
    }

    public EDSV2MediaItemDetailModel getInternalModel() {
        return this.internalModel;
    }

    public static EDSV2NowPlayingDetailModel getModel(long titleId, String partnerMediaId) {
        return EDSV2MediaItemModel.getNowPlayingModel(titleId, partnerMediaId);
    }

    public synchronized void addObserver(XLEObserver<UpdateData> observer) {
        if (this.internalModel != null) {
            this.internalModel.addObserver(observer);
        } else {
            super.addObserver(observer);
        }
    }

    public synchronized void removeObserver(XLEObserver<UpdateData> observer) {
        if (this.internalModel != null) {
            this.internalModel.removeObserver(observer);
        } else {
            super.removeObserver(observer);
        }
    }

    public void load(boolean forceRefresh) {
        if (this.internalModel != null) {
            this.internalModel.load(forceRefresh);
        } else {
            super.load(forceRefresh);
        }
    }

    public void loadRelated(boolean forceRefresh) {
        XLEAssert.assertNotNull(this.internalModel);
        XLELog.Diagnostic("NowPlayingDetailModel", "load related");
        this.internalModel.loadRelated(forceRefresh);
    }

    protected void onGetMediaItemDetailCompleted(AsyncResult<EDSV2MediaItem> result) {
        result = updateDataForBrowser(result);
        super.updateWithNewData(result);
        if (result.getException() == null) {
            EDSV2MediaItem data = (EDSV2MediaItem) result.getResult();
            if (data != null) {
                String partnerMediaId = this.detailData.getPartnerMediaId();
                data.setPartnerMediaId(this.detailData.getPartnerMediaId());
                this.internalModel = (EDSV2MediaItemDetailModel) EDSV2MediaItemModel.createModel(data);
                ArrayList<XLEObserver<UpdateData>> currentObservers = getObservers();
                clearObserver();
                Iterator<XLEObserver<UpdateData>> i = currentObservers.iterator();
                while (i.hasNext()) {
                    this.internalModel.addObserver((XLEObserver) i.next());
                }
                this.internalModel.onGetMediaItemDetailCompleted(result);
                return;
            }
        }
        notifyObservers(new AsyncResult(new UpdateData(UpdateType.MediaItemDetail, true), this, result.getException()));
    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this || o == this.internalModel) {
            return true;
        }
        return false;
    }
}
