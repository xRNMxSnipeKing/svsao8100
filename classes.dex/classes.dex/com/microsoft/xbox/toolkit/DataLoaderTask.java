package com.microsoft.xbox.toolkit;

import com.microsoft.xbox.toolkit.network.IDataLoaderRunnable;
import com.microsoft.xbox.toolkit.network.XLEThreadPool;

public class DataLoaderTask<T> extends XLEAsyncTask<AsyncResult<T>> {
    private static final int DATA_WAIT_TIMEOUT_MS = 1000;
    private IDataLoaderRunnable<T> dataLoadRunnable;
    private XLEException exception;
    private long lastInvalidatedTick;
    private long requestStartTime;
    private boolean shouldCheckTimeStamp;
    private TimeMonitor stopwatch;

    public DataLoaderTask(long lastInvalidatedTick, IDataLoaderRunnable<T> runnable) {
        super(XLEThreadPool.networkOperationsThreadPool);
        this.stopwatch = new TimeMonitor();
        this.lastInvalidatedTick = lastInvalidatedTick;
        this.dataLoadRunnable = runnable;
        this.requestStartTime = System.currentTimeMillis();
        if (lastInvalidatedTick > 0) {
            this.shouldCheckTimeStamp = true;
        } else {
            this.shouldCheckTimeStamp = false;
        }
    }

    public DataLoaderTask(IDataLoaderRunnable<T> runnable) {
        this(0, runnable);
    }

    public AsyncResult<T> doInBackground() {
        Object data;
        try {
            data = this.dataLoadRunnable.buildData();
        } catch (Exception e) {
            data = null;
            this.exception = new XLEException(this.dataLoadRunnable.getDefaultErrorCode(), null, e, this.dataLoadRunnable.getUserObject());
            if (e instanceof XLEException) {
                long errorCode = ((XLEException) e).getErrorCode();
                if (errorCode == XLEErrorCode.INVALID_COOKIE || errorCode == XLEErrorCode.INVALID_ACCESS_TOKEN) {
                    this.exception = (XLEException) e;
                } else if (errorCode == XLEErrorCode.INVALID_TOKEN || errorCode == 1) {
                    XLELog.Diagnostic("DataLoaderTask", "INVALID_TOKEN error code received. Returning generic error code for the model.");
                }
            }
            XLELog.Diagnostic("DataLoaderTask", "Caught an exception during background operation: " + e.toString());
        }
        BackgroundThreadWaitor.getInstance().waitForReady(1000);
        return new AsyncResult(data, this, this.exception);
    }

    public void onPreExecute() {
        this.exception = null;
        this.dataLoadRunnable.onPreExecute();
    }

    public void onPostExecute(AsyncResult<T> result) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        if (this.shouldCheckTimeStamp && this.requestStartTime <= this.lastInvalidatedTick) {
            result = new AsyncResult(null, this, new XLEException(9));
            XLELog.Warning("DataLoaderTask", "Invalidated an incoming data packet because it was deemed out of date");
        }
        this.dataLoadRunnable.onPostExcute(result);
    }
}
