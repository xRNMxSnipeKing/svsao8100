package com.microsoft.xbox.toolkit;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.PriorityQueue;

public class FixedSizeHashtable<K, V> {
    private int count = 0;
    private Hashtable<K, V> hashtable = new Hashtable();
    private PriorityQueue<KeyTuple> lru = new PriorityQueue();
    private final int maxSize;

    private class KeyTuple implements Comparable<KeyTuple> {
        private int index = 0;
        private K key;

        public KeyTuple(K key, int index) {
            this.key = key;
            this.index = index;
        }

        public int compareTo(KeyTuple rhs) {
            return this.index - rhs.index;
        }

        public K getKey() {
            return this.key;
        }
    }

    public FixedSizeHashtable(int maxSize) {
        this.maxSize = maxSize;
        if (maxSize <= 0) {
            throw new IllegalArgumentException();
        }
    }

    public void put(K key, V value) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        if (value != null) {
            if (this.hashtable.get(key) != null) {
                remove(key);
            }
            this.count++;
            this.lru.add(new KeyTuple(key, this.count));
            this.hashtable.put(key, value);
            cleanupIfNecessary();
        }
    }

    public V get(K key) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        return this.hashtable.get(key);
    }

    public void remove(K key) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        this.hashtable.remove(key);
        KeyTuple keyTuple = null;
        Iterator<KeyTuple> queueIterator = this.lru.iterator();
        while (queueIterator.hasNext()) {
            keyTuple = (KeyTuple) queueIterator.next();
            if (keyTuple.key == key) {
                break;
            }
        }
        XLEAssert.assertNotNull("Queue should contain an item with the given key.", keyTuple);
        this.lru.remove(keyTuple);
    }

    public Enumeration<V> elements() {
        return this.hashtable.elements();
    }

    public Enumeration<K> keys() {
        return this.hashtable.keys();
    }

    private void cleanupIfNecessary() {
        boolean z;
        if (this.hashtable.size() == this.lru.size()) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        while (this.hashtable.size() > this.maxSize) {
            this.hashtable.remove(((KeyTuple) this.lru.remove()).getKey());
            if (this.hashtable.size() == this.lru.size()) {
                z = true;
            } else {
                z = false;
            }
            XLEAssert.assertTrue(z);
        }
    }
}
