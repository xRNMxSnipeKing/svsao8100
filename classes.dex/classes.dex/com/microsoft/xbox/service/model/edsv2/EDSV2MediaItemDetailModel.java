package com.microsoft.xbox.service.model.edsv2;

import com.microsoft.xbox.service.model.JTitleType;
import com.microsoft.xbox.service.model.LaunchType;
import com.microsoft.xbox.service.model.MeProfileModel;
import com.microsoft.xbox.service.model.UpdateData;
import com.microsoft.xbox.service.model.UpdateType;
import com.microsoft.xbox.service.model.sls.Title;
import com.microsoft.xbox.service.network.managers.ServiceManagerFactory;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.DataLoaderTask;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.XBLSharedUtil;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEConstants;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.toolkit.network.IDataLoaderRunnable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.UUID;

public abstract class EDSV2MediaItemDetailModel<T extends EDSV2MediaItem, U extends EDSV2MediaItem> extends EDSV2MediaItemModel<T> {
    public static final String XBOX_MUSIC_LAUNCH_PARAM = "app:5848085B:MusicHomePage";
    public static final String XBOX_MUSIC_TITLE_STRING = XboxApplication.Resources.getString(XboxApplication.Instance.getStringRValue("xbox_music_title"));
    public static final String XBOX_VIDEO_LAUNCH_PARAM = "app:5848085b:VideoHomePage";
    public static final String XBOX_VIDEO_TITLE_STRING = XboxApplication.Resources.getString(XboxApplication.Instance.getStringRValue("xbox_video_title"));
    protected T detailData;
    private GetEDSV2MediaItemDetailRunner detailRunner = new GetEDSV2MediaItemDetailRunner();
    private GetRelatedEDSV2MediaItemListRunner getRelatedRunner = new GetRelatedEDSV2MediaItemListRunner();
    private boolean isLoadingRelated = false;
    private Date lastRefreshRelatedTime;
    private ArrayList<U> related;

    private class GetEDSV2MediaItemDetailRunner extends IDataLoaderRunnable<T> {
        private GetEDSV2MediaItemDetailRunner() {
        }

        public void onPreExecute() {
        }

        public T buildData() throws XLEException {
            return ServiceManagerFactory.getInstance().getEDSServiceManager().getMediaItemDetail(EDSV2MediaItemDetailModel.this.getCanonicalId(), EDSV2MediaItemDetailModel.this.getPartnerMediaId(), EDSV2MediaItemDetailModel.this.getTitleId(), EDSV2MediaItemDetailModel.this.getMediaGroup(), EDSV2MediaItemDetailModel.this.getImpressionGuid());
        }

        public void onPostExcute(AsyncResult<T> result) {
            EDSV2MediaItemDetailModel.this.onGetMediaItemDetailCompleted(result);
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_GET_MEDIA_ITEM_DETAILS;
        }
    }

    private class GetRelatedEDSV2MediaItemListRunner extends IDataLoaderRunnable<ArrayList<U>> {
        private GetRelatedEDSV2MediaItemListRunner() {
        }

        public void onPreExecute() {
        }

        public ArrayList<U> buildData() throws XLEException {
            XLEAssert.assertTrue(!JavaUtil.isNullOrEmpty(EDSV2MediaItemDetailModel.this.getRelatedCanonicalId()));
            return ServiceManagerFactory.getInstance().getEDSServiceManager().getRelated(EDSV2MediaItemDetailModel.this.getRelatedCanonicalId(), EDSV2MediaItemDetailModel.this.getRelatedMediaType(), EDSV2MediaItemDetailModel.this.getRelatedMediaType());
        }

        public void onPostExcute(AsyncResult<ArrayList<U>> result) {
            EDSV2MediaItemDetailModel.this.onGetRelatedCompleted(result);
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_GET_RELATED;
        }
    }

    protected abstract T createMediaItem(EDSV2MediaItem eDSV2MediaItem);

    public abstract LaunchType getLaunchType();

    public abstract int getMediaGroup();

    public abstract JTitleType getTitleType();

    protected EDSV2MediaItemDetailModel(EDSV2MediaItem mediaItem) {
        this.detailData = createMediaItem(mediaItem);
    }

    public String getCanonicalId() {
        return this.detailData.getCanonicalId();
    }

    public T getMediaItemDetailData() {
        return this.detailData;
    }

    public String getPartnerMediaId() {
        return this.detailData.getPartnerMediaId();
    }

    public long getTitleId() {
        return this.detailData.getTitleId();
    }

    public int getMediaType() {
        return this.detailData.getMediaType();
    }

    public String getTitle() {
        return this.detailData.getTitle();
    }

    public String getDescription() {
        return this.detailData.getDescription();
    }

    public Date getReleaseDate() {
        return this.detailData.getReleaseDate();
    }

    public URI getImageUrl() {
        return this.detailData.getImageUrl();
    }

    public URI getBackgroundImageUrl() {
        return this.detailData.getBackgroundImageUrl();
    }

    public int getDurationInMinutes() {
        if (JavaUtil.isNullOrEmpty(this.detailData.getDuration())) {
            return 0;
        }
        return XBLSharedUtil.durationStringToMinutes(this.detailData.getDuration());
    }

    public String getParentalRating() {
        return this.detailData.getParentalRating();
    }

    public ArrayList<EDSV2Provider> getProviders() {
        return this.detailData.getProviders();
    }

    public String getParentCanonicalId() {
        return this.detailData.getParentCanonicalId();
    }

    public String getParentName() {
        return this.detailData.getParentName();
    }

    public int getParentMediaType() {
        return this.detailData.getParentMediaType();
    }

    public String getImpressionGuid() {
        return this.detailData.getImpressionGuid();
    }

    public boolean getShouldCheckActivity() {
        switch (getMediaGroup()) {
            case 1:
            case 2:
            case 3:
            case 4:
                return true;
            default:
                return false;
        }
    }

    protected String getRelatedCanonicalId() {
        return getCanonicalId();
    }

    protected int getRelatedMediaType() {
        return getMediaType();
    }

    public ArrayList<U> getRelated() {
        return this.related;
    }

    public boolean shouldGetProviderActivities() {
        return false;
    }

    public boolean isGameType() {
        switch (getMediaType()) {
            case 1:
            case 5:
            case EDSV2MediaType.MEDIATYPE_XBOX360GAMEDEMO /*19*/:
            case EDSV2MediaType.MEDIATYPE_XBOXORIGINALGAME /*21*/:
            case EDSV2MediaType.MEDIATYPE_XBOXARCADEGAME /*23*/:
            case EDSV2MediaType.MEDIATYPE_XBOXXNACOMMUNITYGAME /*37*/:
                return true;
            default:
                return false;
        }
    }

    public void load(boolean forceRefresh) {
        loadInternal(forceRefresh, UpdateType.MediaItemDetail, this.detailRunner);
    }

    public void loadRelated(boolean forceRefresh) {
        boolean z = true;
        if (JavaUtil.isNullOrEmpty(getCanonicalId())) {
            notifyObservers(new AsyncResult(new UpdateData(UpdateType.MediaItemDetailRelated, false), this, null));
        } else if (!supportsRelated()) {
            XLELog.Diagnostic("RelatedModel", "this content type does not support related");
            notifyObservers(new AsyncResult(new UpdateData(UpdateType.MediaItemDetailRelated, true), this, null));
        } else if (this.isLoadingRelated || !(forceRefresh || shouldRefreshRelated())) {
            UpdateType updateType = UpdateType.MediaItemDetailRelated;
            if (this.isLoadingRelated) {
                z = false;
            }
            notifyObservers(new AsyncResult(new UpdateData(updateType, z), this, null));
        } else {
            this.isLoadingRelated = true;
            new DataLoaderTask(0, this.getRelatedRunner).execute();
            notifyObservers(new AsyncResult(new UpdateData(UpdateType.MediaItemDetailRelated, false), this, null));
        }
    }

    public boolean supportsRelated() {
        return getMediaType() == EDSV2MediaType.MEDIATYPE_MOVIE || getMediaType() == EDSV2MediaType.MEDIATYPE_TVSERIES || getMediaType() == EDSV2MediaType.MEDIATYPE_TVSEASON || getMediaType() == EDSV2MediaType.MEDIATYPE_TVEPISODE || isGameType();
    }

    public boolean getIsLoadingRelated() {
        return getIsLoading() || this.isLoadingRelated;
    }

    protected void updateWithNewData(T data) {
        this.lastRefreshTime = new Date();
        this.detailData = data;
    }

    protected AsyncResult<T> updateDataForBrowser(AsyncResult<T> result) {
        if (getTitleId() != XLEConstants.BROWSER_TITLE_ID || result.getException() == null) {
            return result;
        }
        EDSV2AppMediaItem browserItem;
        XLELog.Diagnostic("EDSV2MediaItem", "Failed to download browser detail. Replacing the result with hard coded data");
        if (getMediaItemDetailData() instanceof EDSV2AppMediaItem) {
            browserItem = (EDSV2AppMediaItem) getMediaItemDetailData();
        } else {
            browserItem = new EDSV2AppMediaItem(getMediaItemDetailData());
        }
        browserItem.setMediaType(61);
        browserItem.setTitleId(XLEConstants.BROWSER_TITLE_ID);
        browserItem.setTitle(XboxApplication.Resources.getString(XboxApplication.Instance.getStringRValue("now_playing_home_browser")));
        browserItem.setImageUrl(Title.getImageUrl(MeProfileModel.getModel().getLegalLocale(), XLEConstants.BROWSER_TITLE_ID));
        if (JavaUtil.isNullOrEmpty(browserItem.getCanonicalId())) {
            browserItem.setCanonicalId(UUID.randomUUID().toString());
        }
        return new AsyncResult(browserItem, result.getSender(), null);
    }

    protected void onGetMediaItemDetailCompleted(AsyncResult<T> result) {
        result = updateDataForBrowser(result);
        this.isLoading = false;
        if (result.getException() == null) {
            this.lastRefreshTime = new Date();
            EDSV2MediaItem data = (EDSV2MediaItem) result.getResult();
            if (data != null) {
                String partnerMediaId = this.detailData.getPartnerMediaId();
                updateWithNewData(data);
                EDSV2MediaItemModel.updateModelInCache(this, partnerMediaId);
                if (!JavaUtil.isNullOrEmpty(getRelatedCanonicalId())) {
                    loadRelated(false);
                }
                updateXboxMusicAndVideoProviderName();
            }
        }
        notifyObservers(new AsyncResult(new UpdateData(UpdateType.MediaItemDetail, true), this, result.getException()));
    }

    private void updateXboxMusicAndVideoProviderName() {
        if (getProviders() != null) {
            Iterator i$ = getProviders().iterator();
            while (i$.hasNext()) {
                EDSV2Provider provider = (EDSV2Provider) i$.next();
                if (provider.getTitleId() == XLEConstants.ZUNE_TITLE_ID) {
                    switch (getMediaType()) {
                        case EDSV2MediaType.MEDIATYPE_MOVIE /*1000*/:
                        case EDSV2MediaType.MEDIATYPE_TVEPISODE /*1003*/:
                            provider.setName(XBOX_VIDEO_TITLE_STRING);
                            break;
                        case EDSV2MediaType.MEDIATYPE_ALBUM /*1006*/:
                        case EDSV2MediaType.MEDIATYPE_MUSICVIDEO /*1008*/:
                            provider.setName(XBOX_MUSIC_TITLE_STRING);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }

    private void onGetRelatedCompleted(AsyncResult<ArrayList<U>> result) {
        this.isLoadingRelated = false;
        if (result.getException() == null) {
            ArrayList<U> data = (ArrayList) result.getResult();
            if (data != null) {
                this.lastRefreshRelatedTime = new Date();
                this.related = data;
            }
        }
        notifyObservers(new AsyncResult(new UpdateData(UpdateType.MediaItemDetailRelated, true), this, result.getException()));
    }

    private boolean shouldRefreshRelated() {
        if (this.lastRefreshRelatedTime == null || new Date().getTime() - this.lastRefreshRelatedTime.getTime() > this.lifetime) {
            return true;
        }
        XLELog.Info("EDSV2MediaItemDetailsWithRelatedModel", "less than lifetime, should not refresh");
        return false;
    }

    protected EDSV2Provider getXboxMusicProvider() {
        EDSV2Provider musicProvider = new EDSV2Provider();
        musicProvider.setTitleId(getTitleId());
        musicProvider.setName(XBOX_MUSIC_TITLE_STRING);
        musicProvider.setCanonicalId(getCanonicalId());
        musicProvider.setIsXboxMusic(true);
        EDSV2PartnerApplicationLaunchInfo musicProviderLaunchInfo = new EDSV2PartnerApplicationLaunchInfo();
        musicProviderLaunchInfo.setTitleId(getTitleId());
        musicProviderLaunchInfo.setDeepLinkInfo(XBOX_MUSIC_LAUNCH_PARAM);
        musicProviderLaunchInfo.setLaunchType(LaunchType.UnknownLaunchType);
        musicProviderLaunchInfo.setTitleType(JTitleType.Application);
        ArrayList<EDSV2PartnerApplicationLaunchInfo> musiclaunchInfoList = new ArrayList();
        musiclaunchInfoList.add(musicProviderLaunchInfo);
        musicProvider.setLaunchInfos(musiclaunchInfoList);
        return musicProvider;
    }

    protected EDSV2Provider getXboxVideoProvider() {
        EDSV2Provider videoProvider = new EDSV2Provider();
        videoProvider.setTitleId(getTitleId());
        videoProvider.setName(XBOX_VIDEO_TITLE_STRING);
        videoProvider.setCanonicalId(getCanonicalId());
        EDSV2PartnerApplicationLaunchInfo videoProviderLaunchInfo = new EDSV2PartnerApplicationLaunchInfo();
        videoProviderLaunchInfo.setTitleId(getTitleId());
        videoProviderLaunchInfo.setLaunchType(LaunchType.AppLaunchType);
        videoProviderLaunchInfo.setTitleType(JTitleType.Application);
        ArrayList<EDSV2PartnerApplicationLaunchInfo> videolaunchInfoList = new ArrayList();
        videolaunchInfoList.add(videoProviderLaunchInfo);
        videoProvider.setLaunchInfos(videolaunchInfoList);
        return videoProvider;
    }
}
