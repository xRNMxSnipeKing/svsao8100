package com.microsoft.xbox.service.model;

import com.microsoft.xbox.service.model.edsv2.EDSV2ActivityItem;
import com.microsoft.xbox.service.model.edsv2.EDSV2AppDetailModel;
import com.microsoft.xbox.service.model.edsv2.EDSV2AppMediaItem;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaItem;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaItemDetailModel;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaItemModel;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaType;
import com.microsoft.xbox.service.model.edsv2.EDSV2Provider;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEConstants;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.XLEObservable;
import com.microsoft.xbox.toolkit.XLEObserver;
import com.microsoft.xbox.toolkit.network.ListState;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

public class MergedActivitySummaryModel extends XLEObservable<UpdateData> implements XLEObserver<UpdateData> {
    private static MergedActivitySummaryModel instance = new MergedActivitySummaryModel();
    private Hashtable<ActivitySummaryModel, ListState> activityModelLoadStateMap = new Hashtable();
    private EDSV2ActivityItem featuredActivity;
    private boolean forceRefresh = false;
    private ArrayList<EDSV2ActivityItem> mergedActivityList;
    private EDSV2AppDetailModel musicAppDetailModel;
    private ActivitySummaryModel parentActivityModel;
    private EDSV2MediaItemDetailModel parentDetailModel;
    private Hashtable<String, ActivitySummaryModel> providerActivityModelMap = new Hashtable();

    public void setParentItem(EDSV2MediaItem parentMediaItem) {
        XLEAssert.assertNotNull(parentMediaItem);
        if (getParentItem() == null || !getParentItem().equals(parentMediaItem)) {
            clearParentItem();
            this.parentDetailModel = (EDSV2MediaItemDetailModel) EDSV2MediaItemModel.getModel(parentMediaItem);
            this.parentDetailModel.addObserver(this);
            if (!JavaUtil.isNullOrEmpty(this.parentDetailModel.getCanonicalId()) && this.parentDetailModel.getShouldCheckActivity()) {
                this.parentActivityModel = ActivitySummaryModel.getModel(this.parentDetailModel.getMediaItemDetailData());
                this.parentActivityModel.addObserver(this);
            }
        }
    }

    public EDSV2MediaItem getParentItem() {
        if (this.parentDetailModel != null) {
            return this.parentDetailModel.getMediaItemDetailData();
        }
        return null;
    }

    public void clearParentItem() {
        this.mergedActivityList = null;
        this.featuredActivity = null;
        if (this.parentActivityModel != null) {
            this.parentActivityModel.removeObserver(this);
            this.parentActivityModel = null;
        }
        if (this.musicAppDetailModel != null) {
            this.musicAppDetailModel.removeObserver(this);
            this.musicAppDetailModel = null;
        }
        if (this.parentDetailModel != null) {
            this.parentDetailModel.removeObserver(this);
            this.parentDetailModel = null;
        }
        clearProviderActivityModelMap();
    }

    public static MergedActivitySummaryModel getInstance() {
        return instance;
    }

    public boolean getHasActivities() {
        return this.featuredActivity != null || (this.mergedActivityList != null && this.mergedActivityList.size() > 0);
    }

    public ArrayList<EDSV2ActivityItem> getActivitiesList() {
        return this.mergedActivityList;
    }

    public EDSV2ActivityItem getFeaturedActivity() {
        return this.featuredActivity;
    }

    public String getParentTitle() {
        if (this.parentDetailModel != null) {
            return this.parentDetailModel.getTitle();
        }
        return null;
    }

    public String getParentItemCanonicalId() {
        if (getParentItem() != null) {
            return getParentItem().getCanonicalId();
        }
        return null;
    }

    public boolean isBusy() {
        boolean stillLoading = false;
        for (ListState currentModelState : this.activityModelLoadStateMap.values()) {
            if (currentModelState == ListState.LoadingState) {
                stillLoading = true;
                break;
            }
        }
        return this.activityModelLoadStateMap.size() != 0 && stillLoading;
    }

    public void update(AsyncResult<UpdateData> asyncResult) {
        boolean parentMediaDetailFailed = false;
        boolean allLoaded = false;
        boolean errorExists = false;
        switch (((UpdateData) asyncResult.getResult()).getUpdateType()) {
            case MediaItemDetail:
                if (asyncResult.getException() == null) {
                    if (((UpdateData) asyncResult.getResult()).getIsFinal() && (asyncResult.getSender() instanceof EDSV2MediaItemDetailModel) && this.musicAppDetailModel != null && asyncResult.getSender() == this.musicAppDetailModel && !JavaUtil.isNullOrEmpty(this.musicAppDetailModel.getCanonicalId())) {
                        loadAllActivityModels();
                        break;
                    }
                }
                parentMediaDetailFailed = true;
                break;
            case ActivitiesSummary:
                ActivitySummaryModel activityModel = (ActivitySummaryModel) asyncResult.getSender();
                XLEAssert.assertNotNull(activityModel);
                ListState modelState = ListState.LoadingState;
                if (asyncResult.getException() != null) {
                    XLELog.Diagnostic("ActivitySummaryActivityViewModel", "Failed to get activities for: " + activityModel.getParentMediaItem().getTitle());
                    modelState = ListState.ErrorState;
                } else if (((UpdateData) asyncResult.getResult()).getIsFinal()) {
                    XLELog.Diagnostic("ActivitySummaryActivityViewModel", "Got activities for: " + activityModel.getParentMediaItem().getTitle());
                    modelState = ListState.ValidContentState;
                }
                this.activityModelLoadStateMap.put(activityModel, modelState);
                allLoaded = this.activityModelLoadStateMap.size() > 0;
                for (ListState currentModelState : this.activityModelLoadStateMap.values()) {
                    if (currentModelState == ListState.ErrorState) {
                        errorExists = true;
                    } else if (currentModelState == ListState.LoadingState) {
                        allLoaded = false;
                        break;
                    }
                }
                break;
            default:
                return;
        }
        if (parentMediaDetailFailed || allLoaded) {
            mergeActivityLists();
            XLEException exception = null;
            if (parentMediaDetailFailed) {
                exception = new XLEException(XLEErrorCode.FAILED_TO_GET_MEDIA_ITEM_DETAILS);
            } else if (errorExists) {
                exception = new XLEException(XLEErrorCode.FAILED_TO_GET_ACTIVITY_SUMMARY);
            }
            notifyObservers(new AsyncResult(new UpdateData(UpdateType.MergedActivitiesSummary, true), this, exception));
            return;
        }
        notifyObservers(new AsyncResult(new UpdateData(UpdateType.MergedActivitiesSummary, !isBusy()), this, null));
    }

    private void clearProviderActivityModelMap() {
        if (this.providerActivityModelMap.size() > 0) {
            for (ActivitySummaryModel model : this.providerActivityModelMap.values()) {
                model.removeObserver(this);
            }
            this.providerActivityModelMap.clear();
        }
    }

    private void invalidateProviderActivityModelData() {
        if (this.providerActivityModelMap.size() > 0) {
            for (ActivitySummaryModel model : this.providerActivityModelMap.values()) {
                model.invalidateData();
            }
        }
    }

    private void loadAllActivityModels() {
        ActivitySummaryModel model;
        final boolean forceRefresh = this.forceRefresh;
        this.forceRefresh = false;
        if (!(this.parentActivityModel == null || this.activityModelLoadStateMap.contains(this.parentActivityModel))) {
            this.activityModelLoadStateMap.put(this.parentActivityModel, ListState.LoadingState);
        }
        if (this.parentDetailModel != null && this.parentDetailModel.shouldGetProviderActivities() && this.parentDetailModel.getProviders() != null && this.parentDetailModel.getProviders().size() > 0) {
            Iterator<EDSV2Provider> i = this.parentDetailModel.getProviders().iterator();
            while (i.hasNext()) {
                EDSV2Provider provider = (EDSV2Provider) i.next();
                if (!JavaUtil.isNullOrEmpty(provider.getCanonicalId())) {
                    model = (ActivitySummaryModel) this.providerActivityModelMap.get(provider.getCanonicalId());
                    if (model == null) {
                        model = ActivitySummaryModel.getModel(new EDSV2AppMediaItem(provider));
                        model.addObserver(this);
                        XLELog.Diagnostic("ActivitySummaryVM", "Adding provider activity model: " + provider.getName());
                        this.providerActivityModelMap.put(provider.getCanonicalId(), model);
                    }
                    if (!this.activityModelLoadStateMap.contains(model)) {
                        this.activityModelLoadStateMap.put(model, ListState.LoadingState);
                    }
                }
            }
        }
        if (this.parentActivityModel != null) {
            ThreadManager.UIThreadPost(new Runnable() {
                public void run() {
                    if (MergedActivitySummaryModel.this.parentActivityModel != null) {
                        MergedActivitySummaryModel.this.parentActivityModel.load(forceRefresh);
                    }
                }
            });
        }
        for (final ActivitySummaryModel model2 : this.providerActivityModelMap.values()) {
            ThreadManager.UIThreadPost(new Runnable() {
                public void run() {
                    if (model2 != null) {
                        model2.load(forceRefresh);
                    }
                }
            });
        }
    }

    private void mergeActivityLists() {
        if (this.parentDetailModel == null) {
            this.featuredActivity = null;
            this.mergedActivityList = null;
            return;
        }
        if (this.parentActivityModel != null) {
            this.featuredActivity = this.parentActivityModel.getFirstHeroActivity();
        } else {
            this.featuredActivity = null;
        }
        ArrayList<ActivitySummaryModel> providerModels = new ArrayList();
        if (this.parentDetailModel.getProviders() != null && this.parentDetailModel.getProviders().size() > 0) {
            Iterator i$ = this.parentDetailModel.getProviders().iterator();
            while (i$.hasNext()) {
                EDSV2Provider provider = (EDSV2Provider) i$.next();
                if (!JavaUtil.isNullOrEmpty(provider.getCanonicalId())) {
                    ActivitySummaryModel model = (ActivitySummaryModel) this.providerActivityModelMap.get(provider.getCanonicalId());
                    if (model != null) {
                        providerModels.add(model);
                    }
                }
            }
        }
        this.mergedActivityList = mergeActivityListsExcludeFeaturedActivity(this.parentActivityModel, providerModels, this.parentDetailModel.getMediaType());
    }

    public static ArrayList<EDSV2ActivityItem> mergeActivityListsExcludeFeaturedActivity(ActivitySummaryModel parentActivityModel, ArrayList<ActivitySummaryModel> providerActivityModelList, int parentMediaType) {
        ArrayList<EDSV2ActivityItem> newMergedList = new ArrayList();
        if (!(parentActivityModel == null || parentActivityModel.getActivitiesListExcludeFirstHero() == null)) {
            newMergedList.addAll(parentActivityModel.getActivitiesListExcludeFirstHero());
        }
        if (providerActivityModelList != null && providerActivityModelList.size() > 0) {
            Iterator i$ = providerActivityModelList.iterator();
            while (i$.hasNext()) {
                ActivitySummaryModel model = (ActivitySummaryModel) i$.next();
                if (model.getFullActivitiesList() != null) {
                    ArrayList<EDSV2ActivityItem> providerActivityList = new ArrayList();
                    providerActivityList.addAll(model.getFullActivitiesList());
                    if (model.getParentMediaItem().getTitleId() == XLEConstants.ZUNE_TITLE_ID) {
                        switch (parentMediaType) {
                            case EDSV2MediaType.MEDIATYPE_MOVIE /*1000*/:
                            case EDSV2MediaType.MEDIATYPE_TVEPISODE /*1003*/:
                                ActivitySummaryModel.removeMusicActivity(providerActivityList);
                                break;
                            case EDSV2MediaType.MEDIATYPE_ALBUM /*1006*/:
                                ActivitySummaryModel.removeVideoActivity(providerActivityList);
                                break;
                            case EDSV2MediaType.MEDIATYPE_MUSICVIDEO /*1008*/:
                                ActivitySummaryModel.removeVideoActivity(providerActivityList);
                                ActivitySummaryModel.removeMusicActivity(providerActivityList);
                                break;
                        }
                    }
                    newMergedList.addAll(providerActivityList);
                }
            }
        }
        return newMergedList;
    }

    public void load(final boolean forceRefresh) {
        boolean z = true;
        if (getParentItem() == null) {
            notifyObservers(new AsyncResult(new UpdateData(UpdateType.MergedActivitiesSummary, true), this, null));
            return;
        }
        this.forceRefresh = forceRefresh;
        this.activityModelLoadStateMap.clear();
        if (forceRefresh) {
            invalidateProviderActivityModelData();
        }
        if (this.parentActivityModel == null && !JavaUtil.isNullOrEmpty(this.parentDetailModel.getCanonicalId()) && this.parentDetailModel.getShouldCheckActivity()) {
            this.parentActivityModel = ActivitySummaryModel.getModel(this.parentDetailModel.getMediaItemDetailData());
            this.parentActivityModel.addObserver(this);
        }
        if (!JavaUtil.isNullOrEmpty(EDSV2MediaItemModel.getZuneCanonicalId()) || getParentItem().getMediaType() != EDSV2MediaType.MEDIATYPE_ALBUM || this.parentDetailModel.getProviders() == null || this.parentDetailModel.getProviders().size() <= 0) {
            loadAllActivityModels();
            UpdateType updateType = UpdateType.MergedActivitiesSummary;
            if (isBusy()) {
                z = false;
            }
            notifyObservers(new AsyncResult(new UpdateData(updateType, z), this, null));
            return;
        }
        this.musicAppDetailModel = (EDSV2AppDetailModel) EDSV2MediaItemModel.getZuneModel();
        this.musicAppDetailModel.addObserver(this);
        ThreadManager.UIThreadPost(new Runnable() {
            public void run() {
                if (MergedActivitySummaryModel.this.musicAppDetailModel != null) {
                    MergedActivitySummaryModel.this.musicAppDetailModel.load(forceRefresh);
                }
            }
        });
    }
}
