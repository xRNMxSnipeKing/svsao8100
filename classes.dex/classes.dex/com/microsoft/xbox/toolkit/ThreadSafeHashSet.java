package com.microsoft.xbox.toolkit;

import java.util.HashSet;

public class ThreadSafeHashSet<T> {
    private HashSet<T> data = new HashSet();
    private Object syncObject = new Object();

    public void remove(T v) {
        synchronized (this.syncObject) {
            this.data.remove(v);
        }
    }

    public boolean contains(T v) {
        boolean contains;
        synchronized (this.syncObject) {
            contains = this.data.contains(v);
        }
        return contains;
    }

    public boolean ifNotContainsAdd(T v) {
        boolean rv;
        synchronized (this.syncObject) {
            rv = this.data.contains(v);
            if (!rv) {
                this.data.add(v);
            }
        }
        return rv;
    }
}
