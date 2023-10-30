package com.microsoft.xbox.toolkit;

import java.util.ArrayList;
import java.util.HashSet;

public abstract class XLEObservable<T> {
    private HashSet<XLEObserver<T>> data = new HashSet();

    public synchronized void addObserver(XLEObserver<T> observer) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        this.data.add(observer);
        XLELog.Diagnostic("XLEObservable", "There are " + this.data.size() + " observers.");
    }

    public synchronized void removeObserver(XLEObserver<T> observer) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        XLELog.Diagnostic("XLEObservable", "There are " + this.data.size() + " observers.");
        this.data.remove(observer);
    }

    public synchronized void notifyObservers(AsyncResult<T> asyncResult) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        for (XLEObserver<T> observer : new ArrayList(this.data)) {
            observer.update(asyncResult);
        }
    }

    protected synchronized void clearObserver() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        XLELog.Diagnostic("XLEObservable", "clear There are " + this.data.size() + " observers.");
        this.data.clear();
    }

    protected synchronized ArrayList<XLEObserver<T>> getObservers() {
        return new ArrayList(this.data);
    }
}
