package com.microsoft.xbox.service.model;

import com.microsoft.xbox.service.model.edsv2.EDSV2ActivityItem;
import com.microsoft.xbox.service.model.edsv2.EDSV2ActivityProviderPolicy;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaItem;
import com.microsoft.xbox.service.network.managers.ServiceManagerFactory;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.FixedSizeHashtable;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEConstants;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.network.IDataLoaderRunnable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

public class ActivitySummaryModel extends ModelBase<ArrayList<EDSV2ActivityItem>> {
    private static final int MAX_ACTIVITY_LIST_MODELS = 10;
    private static FixedSizeHashtable<String, ActivitySummaryModel> identifierToModelCache = new FixedSizeHashtable(10);
    private ArrayList<EDSV2ActivityItem> activityList;
    private EDSV2MediaItem parentMediaItem;
    private Hashtable<Long, EDSV2ActivityItem> providerDefaultMap = new Hashtable();

    private class ActivitySummaryRunnable extends IDataLoaderRunnable<ArrayList<EDSV2ActivityItem>> {
        private ActivitySummaryRunnable() {
        }

        public void onPreExecute() {
        }

        public ArrayList<EDSV2ActivityItem> buildData() throws XLEException {
            return ServiceManagerFactory.getInstance().getActivitiesServiceManager().getActivities(ActivitySummaryModel.this.parentMediaItem);
        }

        public void onPostExcute(AsyncResult<ArrayList<EDSV2ActivityItem>> result) {
            ActivitySummaryModel.this.updateWithNewData(result);
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_GET_ACTIVITY_SUMMARY;
        }
    }

    private ActivitySummaryModel(EDSV2MediaItem parentMediaItem) {
        this.parentMediaItem = parentMediaItem;
        this.loaderRunnable = new ActivitySummaryRunnable();
        this.lifetime = 1800000;
    }

    public ArrayList<EDSV2ActivityItem> getFullActivitiesList() {
        return this.activityList;
    }

    public EDSV2MediaItem getParentMediaItem() {
        return this.parentMediaItem;
    }

    public String getParentCanonicalId() {
        return this.parentMediaItem.getCanonicalId();
    }

    public EDSV2ActivityItem getFirstHeroActivity() {
        if (this.providerDefaultMap.size() > 0) {
            return (EDSV2ActivityItem) this.providerDefaultMap.values().iterator().next();
        }
        return null;
    }

    public EDSV2ActivityItem getFirstHeroActivityForProvider(long providerTitleId) {
        return (EDSV2ActivityItem) this.providerDefaultMap.get(Long.valueOf(providerTitleId));
    }

    public ArrayList<EDSV2ActivityItem> getActivitiesListExcludeFirstHero() {
        EDSV2ActivityItem hero = getFirstHeroActivity();
        if (hero == null || !getHasActivities()) {
            return this.activityList;
        }
        ArrayList<EDSV2ActivityItem> activityListExcludingHero = new ArrayList();
        activityListExcludingHero.addAll(this.activityList);
        activityListExcludingHero.remove(hero);
        return activityListExcludingHero;
    }

    public boolean getHasActivities() {
        return this.activityList != null && this.activityList.size() > 0;
    }

    public void load(boolean forceRefresh) {
        loadInternal(forceRefresh, UpdateType.ActivitiesSummary, this.loaderRunnable);
    }

    public void updateWithNewData(AsyncResult<ArrayList<EDSV2ActivityItem>> result) {
        super.updateWithNewData(result);
        if (result.getException() == null && result.getResult() != null) {
            EDSV2ActivityItem activity;
            ArrayList<EDSV2ActivityItem> activitiesToRemove = new ArrayList();
            Iterator i$ = ((ArrayList) result.getResult()).iterator();
            while (i$.hasNext()) {
                activity = (EDSV2ActivityItem) i$.next();
                if (!activity.isValidActivity()) {
                    activitiesToRemove.add(activity);
                }
            }
            ((ArrayList) result.getResult()).removeAll(activitiesToRemove);
            this.activityList = (ArrayList) result.getResult();
            this.providerDefaultMap.clear();
            i$ = this.activityList.iterator();
            while (i$.hasNext()) {
                activity = (EDSV2ActivityItem) i$.next();
                if (this.parentMediaItem.getTitleId() == XLEConstants.ZUNE_TITLE_ID) {
                    activity.setIsXboxMusicOrVideoActivity(true);
                }
                if (activity.getProviderPolicies() != null) {
                    Iterator i$2 = activity.getProviderPolicies().iterator();
                    while (i$2.hasNext()) {
                        EDSV2ActivityProviderPolicy policy = (EDSV2ActivityProviderPolicy) i$2.next();
                        if (!this.providerDefaultMap.contains(Long.valueOf(policy.getTitleId())) && policy.getIsDefault()) {
                            this.providerDefaultMap.put(Long.valueOf(policy.getTitleId()), activity);
                        }
                    }
                }
            }
        }
        removeMusicActivity(this.activityList);
        removeVideoActivity(this.activityList);
        notifyObservers(new AsyncResult(new UpdateData(UpdateType.ActivitiesSummary, true), this, result.getException()));
    }

    public static void removeMusicActivity(ArrayList<EDSV2ActivityItem> source) {
        if (source != null && source.size() != 0) {
            source.removeAll(findXboxMusicActivity(source));
        }
    }

    public static void removeVideoActivity(ArrayList<EDSV2ActivityItem> source) {
        if (source != null && source.size() != 0) {
            source.removeAll(findXboxVideoActivity(source));
        }
    }

    public ArrayList<EDSV2ActivityItem> findXboxMusicActivity() {
        return findXboxMusicActivity(this.activityList);
    }

    private static ArrayList<EDSV2ActivityItem> findXboxMusicActivity(ArrayList<EDSV2ActivityItem> activityList) {
        ArrayList<EDSV2ActivityItem> musicActivities = new ArrayList();
        if (activityList != null && activityList.size() > 0) {
            Iterator i$ = activityList.iterator();
            while (i$.hasNext()) {
                EDSV2ActivityItem activity = (EDSV2ActivityItem) i$.next();
                if (activity.isXboxMusicActivity()) {
                    musicActivities.add(activity);
                }
            }
        }
        return musicActivities;
    }

    public ArrayList<EDSV2ActivityItem> findXboxVideoActivity() {
        return findXboxVideoActivity(this.activityList);
    }

    private static ArrayList<EDSV2ActivityItem> findXboxVideoActivity(ArrayList<EDSV2ActivityItem> activityList) {
        ArrayList<EDSV2ActivityItem> videoActivities = new ArrayList();
        if (activityList != null && activityList.size() > 0) {
            Iterator i$ = activityList.iterator();
            while (i$.hasNext()) {
                EDSV2ActivityItem activity = (EDSV2ActivityItem) i$.next();
                if (activity.isXboxVideoActivity()) {
                    videoActivities.add(activity);
                }
            }
        }
        return videoActivities;
    }

    public static ActivitySummaryModel getModel(EDSV2MediaItem parentMediaItem) {
        boolean z;
        boolean z2 = true;
        if (Thread.currentThread() == ThreadManager.UIThread) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        if (JavaUtil.isNullOrEmpty(parentMediaItem.getCanonicalId())) {
            z2 = false;
        }
        XLEAssert.assertTrue(z2);
        XLEAssert.assertTrue(ActivityUtil.isValidMediaTypeForActivity(parentMediaItem.getMediaType()));
        ActivitySummaryModel model = (ActivitySummaryModel) identifierToModelCache.get(parentMediaItem.getCanonicalId());
        if (model != null) {
            return model;
        }
        model = new ActivitySummaryModel(parentMediaItem);
        identifierToModelCache.put(parentMediaItem.getCanonicalId(), model);
        return model;
    }

    public static final void reset() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        Enumeration<ActivitySummaryModel> e = identifierToModelCache.elements();
        while (e.hasMoreElements()) {
            ((ActivitySummaryModel) e.nextElement()).clearObserver();
        }
        identifierToModelCache = new FixedSizeHashtable(10);
    }
}
