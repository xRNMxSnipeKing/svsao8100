package com.microsoft.xbox.toolkit;

import java.util.Hashtable;

public class ThreadSafeHashtable<K, V> {
    private Hashtable<K, V> data = new Hashtable();
    private Hashtable<V, K> dataInverted = new Hashtable();
    private Object syncObject = new Object();

    public Object getLock() {
        return this.syncObject;
    }

    public void put(K key, V value) {
        synchronized (this.syncObject) {
            this.data.put(key, value);
            this.dataInverted.put(value, key);
        }
    }

    public V get(K key) {
        V v;
        synchronized (this.syncObject) {
            v = this.data.get(key);
        }
        return v;
    }

    public boolean containsKey(K key) {
        boolean containsKey;
        synchronized (this.syncObject) {
            containsKey = this.data.containsKey(key);
        }
        return containsKey;
    }

    public boolean containsValue(V value) {
        boolean containsKey;
        synchronized (this.syncObject) {
            containsKey = this.dataInverted.containsKey(value);
        }
        return containsKey;
    }

    public void remove(K key) {
        synchronized (this.syncObject) {
            V value = this.data.get(key);
            this.data.remove(key);
            this.dataInverted.remove(value);
        }
    }

    public void removeValue(V value) {
        synchronized (this.syncObject) {
            this.data.remove(this.dataInverted.get(value));
            this.dataInverted.remove(value);
        }
    }
}
