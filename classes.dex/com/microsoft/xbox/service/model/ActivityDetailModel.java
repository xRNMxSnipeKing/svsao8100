package com.microsoft.xbox.service.model;

import com.microsoft.xbox.service.model.edsv2.EDSV2ActivityItem;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaItem;
import com.microsoft.xbox.service.network.managers.ServiceManagerFactory;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.FixedSizeHashtable;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.network.IDataLoaderRunnable;
import java.util.Date;
import java.util.Enumeration;

public class ActivityDetailModel extends ModelBase<EDSV2ActivityItem> {
    private static final int MAX_ACTIVITY_DETAIL_MODELS = 10;
    private static FixedSizeHashtable<String, ActivityDetailModel> identifierToModelCache = new FixedSizeHashtable(10);
    private EDSV2ActivityItem activityData;
    private EDSV2MediaItem parentMediaItem;

    private class ActivityDetailRunnable extends IDataLoaderRunnable<EDSV2ActivityItem> {
        private ActivityDetailRunnable() {
        }

        public void onPreExecute() {
        }

        public EDSV2ActivityItem buildData() throws XLEException {
            return ServiceManagerFactory.getInstance().getActivitiesServiceManager().getActivityDetail(ActivityDetailModel.this.activityData, ActivityDetailModel.this.parentMediaItem);
        }

        public void onPostExcute(AsyncResult<EDSV2ActivityItem> result) {
            ActivityDetailModel.this.updateWithNewData(result);
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_GET_ACTIVITY_DETAIL;
        }
    }

    private ActivityDetailModel(EDSV2ActivityItem preloadData, EDSV2MediaItem parentMediaItem) {
        XLEAssert.assertNotNull(preloadData);
        XLEAssert.assertNotNull(parentMediaItem);
        this.loaderRunnable = new ActivityDetailRunnable();
        this.lifetime = 1800000;
        this.activityData = preloadData;
        this.parentMediaItem = parentMediaItem;
        this.lastRefreshTime = new Date();
    }

    public EDSV2ActivityItem getActivityData() {
        return this.activityData;
    }

    public void load(boolean forceRefresh) {
        loadInternal(forceRefresh, UpdateType.ActivityDetail, this.loaderRunnable);
    }

    public void updateWithNewData(AsyncResult<EDSV2ActivityItem> result) {
        super.updateWithNewData(result);
        if (result.getException() == null) {
            this.activityData = (EDSV2ActivityItem) result.getResult();
        }
        notifyObservers(new AsyncResult(new UpdateData(UpdateType.ActivityDetail, true), this, result.getException()));
    }

    public static ActivityDetailModel getModel(EDSV2ActivityItem preloadData, EDSV2MediaItem parentMediaItem) {
        boolean z;
        boolean z2 = true;
        if (Thread.currentThread() == ThreadManager.UIThread) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        if (JavaUtil.isNullOrEmpty(preloadData.getCanonicalId())) {
            z2 = false;
        }
        XLEAssert.assertTrue(z2);
        ActivityDetailModel model = (ActivityDetailModel) identifierToModelCache.get(preloadData.getCanonicalId());
        if (model != null) {
            return model;
        }
        model = new ActivityDetailModel(preloadData, parentMediaItem);
        identifierToModelCache.put(preloadData.getCanonicalId(), model);
        return model;
    }

    public static final void reset() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        Enumeration<ActivityDetailModel> e = identifierToModelCache.elements();
        while (e.hasMoreElements()) {
            ((ActivityDetailModel) e.nextElement()).clearObserver();
        }
        identifierToModelCache = new FixedSizeHashtable(10);
    }
}
