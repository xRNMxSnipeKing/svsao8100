package com.microsoft.xbox.toolkit;

import java.util.HashMap;

public class InvertibleHashMap<K, V> {
    private HashMap<K, V> forward = new HashMap();
    private HashMap<V, K> reverse = new HashMap();

    public void put(K key, V value) {
        this.forward.put(key, value);
        this.reverse.put(value, key);
    }

    public void remove(K key) {
        V value = this.forward.get(key);
        this.forward.remove(key);
        this.reverse.remove(value);
    }

    public V getUsingKey(K key) {
        return this.forward.get(key);
    }

    public K getUsingValue(V value) {
        return this.reverse.get(value);
    }

    public boolean containsKey(K key) {
        return this.forward.containsKey(key);
    }

    public int getSize() {
        return this.forward.size();
    }
}
