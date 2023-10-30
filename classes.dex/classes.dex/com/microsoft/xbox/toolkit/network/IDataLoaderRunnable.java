package com.microsoft.xbox.toolkit.network;

import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.XLEException;

public abstract class IDataLoaderRunnable<T> {
    public abstract T buildData() throws XLEException;

    public abstract long getDefaultErrorCode();

    public abstract void onPostExcute(AsyncResult<T> asyncResult);

    public abstract void onPreExecute();

    public Object getUserObject() {
        return null;
    }
}
