package org.simpleframework.xml.util;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

public class WeakCache<K, V> implements Cache<K, V> {
    private SegmentList list;

    private class Segment extends WeakHashMap<K, V> {
        private Segment() {
        }

        public synchronized void cache(K key, V value) {
            put(key, value);
        }

        public synchronized V fetch(K key) {
            return get(key);
        }

        public synchronized V take(K key) {
            return remove(key);
        }

        public synchronized boolean contains(K key) {
            return containsKey(key);
        }
    }

    private class SegmentList {
        private List<Segment> list = new ArrayList();
        private int size;

        public SegmentList(int size) {
            this.size = size;
            create(size);
        }

        public Segment get(K key) {
            int segment = segment(key);
            if (segment < this.size) {
                return (Segment) this.list.get(segment);
            }
            return null;
        }

        private void create(int size) {
            int count = size;
            while (true) {
                int count2 = count - 1;
                if (count > 0) {
                    this.list.add(new Segment());
                    count = count2;
                } else {
                    return;
                }
            }
        }

        private int segment(K key) {
            return Math.abs(key.hashCode() % this.size);
        }
    }

    public WeakCache() {
        this(10);
    }

    public WeakCache(int size) {
        this.list = new SegmentList(size);
    }

    public void cache(K key, V value) {
        map(key).cache(key, value);
    }

    public V take(K key) {
        return map(key).take(key);
    }

    public V fetch(K key) {
        return map(key).fetch(key);
    }

    public boolean contains(K key) {
        return map(key).contains(key);
    }

    private Segment map(K key) {
        return this.list.get(key);
    }
}
