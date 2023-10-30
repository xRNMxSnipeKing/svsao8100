package com.microsoft.xbox.service.model;

import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.DataLoaderTask;
import com.microsoft.xbox.toolkit.ModelData;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.XLEObservable;
import com.microsoft.xbox.toolkit.network.IDataLoaderRunnable;
import java.util.Date;

public abstract class ModelBase<T> extends XLEObservable<UpdateData> implements ModelData<T> {
    protected static final long MilliSecondsInADay = 86400000;
    protected static final long MilliSecondsInAnHour = 3600000;
    protected static final long MilliSecondsInHalfHour = 1800000;
    protected boolean isLoading = false;
    protected long lastInvalidatedTick = 0;
    protected Date lastRefreshTime;
    protected long lifetime = MilliSecondsInADay;
    protected IDataLoaderRunnable<T> loaderRunnable;

    protected boolean shouldRefresh() {
        return shouldRefresh(this.lastRefreshTime);
    }

    protected boolean shouldRefresh(Date lastRefreshTime) {
        if (lastRefreshTime == null || new Date().getTime() - lastRefreshTime.getTime() > this.lifetime) {
            return true;
        }
        XLELog.Info("ModelBase", "less than lifetime, should not refresh");
        return false;
    }

    protected boolean isLoaded() {
        return this.lastRefreshTime != null;
    }

    public void updateWithNewData(AsyncResult<T> result) {
        this.isLoading = false;
        if (result.getException() == null) {
            this.lastRefreshTime = new Date();
        }
    }

    public boolean getIsLoading() {
        return this.isLoading;
    }

    protected void loadInternal(boolean forceRefresh, UpdateType updateType, IDataLoaderRunnable<T> runnable) {
        loadInternal(forceRefresh, updateType, runnable, this.lastRefreshTime);
    }

    protected void loadInternal(boolean forceRefresh, UpdateType updateType, IDataLoaderRunnable<T> runnable, Date lastRefreshTime) {
        boolean z = true;
        if (this.isLoading || !(forceRefresh || shouldRefresh(lastRefreshTime))) {
            if (getIsLoading()) {
                z = false;
            }
            notifyObservers(new AsyncResult(new UpdateData(updateType, z), this, null));
            return;
        }
        this.isLoading = true;
        new DataLoaderTask(0, runnable).execute();
        notifyObservers(new AsyncResult(new UpdateData(updateType, false), this, null));
    }

    public void invalidateData() {
        this.lastRefreshTime = null;
    }
}
