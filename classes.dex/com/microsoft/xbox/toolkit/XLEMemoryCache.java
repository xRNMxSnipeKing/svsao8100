package com.microsoft.xbox.toolkit;

import java.util.LinkedList;

public class XLEMemoryCache<K, V> {
    private int bytesCurrent = 0;
    private final int bytesMax;
    private InvertibleHashMap<K, XLEMemoryCacheEntry<V>> cache = new InvertibleHashMap();
    private LinkedList<XLEMemoryCacheEntry<V>> cacheFIFO = new LinkedList();
    private int entrySerialNumber = 0;
    private final int maxFileSizeBytes;

    public XLEMemoryCache(int sizeInBytes, int maxFileSizeInBytes) {
        if (sizeInBytes < 0) {
            throw new IllegalArgumentException("sizeInBytes");
        } else if (maxFileSizeInBytes < 0) {
            throw new IllegalArgumentException("maxFileSizeInBytes");
        } else {
            this.bytesMax = sizeInBytes;
            this.maxFileSizeBytes = maxFileSizeInBytes;
        }
    }

    public int getBytesCurrent() {
        return this.bytesCurrent;
    }

    public int getItemsInCache() {
        int size = this.cache.getSize();
        XLEAssert.assertTrue(size == this.cacheFIFO.size());
        return size;
    }

    public void TESTlogDetails() {
    }

    public int getBytesFree() {
        return this.bytesMax - this.bytesCurrent;
    }

    public boolean add(K filename, V data, int fileByteCount) {
        if (contains(filename)) {
            return false;
        }
        if (fileByteCount > this.maxFileSizeBytes) {
            XLELog.Error("XLEMemoryCache", "Tried to add a " + fileByteCount + " file!  That is more than the max file size of: " + this.maxFileSizeBytes);
            return false;
        }
        freeAtLeastNBytes(fileByteCount);
        if (getBytesFree() < fileByteCount) {
            return false;
        }
        this.bytesCurrent += fileByteCount;
        this.entrySerialNumber++;
        XLEMemoryCacheEntry<V> entry = new XLEMemoryCacheEntry(data, this.entrySerialNumber, fileByteCount);
        this.cache.put(filename, entry);
        this.cacheFIFO.addFirst(entry);
        return true;
    }

    public void freeAtLeastNBytes(int fileByteCount) {
        evictToGivenLimit(Math.max(0, this.bytesMax - fileByteCount));
    }

    public boolean contains(K filename) {
        return this.cache.containsKey(filename);
    }

    public V get(K filename) {
        XLEMemoryCacheEntry<V> entry = (XLEMemoryCacheEntry) this.cache.getUsingKey(filename);
        if (entry == null) {
            return null;
        }
        return entry.getValue();
    }

    private void removeLast() {
        boolean z;
        boolean z2 = true;
        XLEMemoryCacheEntry<V> entry = (XLEMemoryCacheEntry) this.cacheFIFO.getLast();
        K filename = this.cache.getUsingValue(entry);
        int size = this.cache.getSize();
        this.bytesCurrent -= entry.getByteCount();
        this.cache.remove(filename);
        this.cacheFIFO.removeLast();
        if (size - 1 == this.cache.getSize()) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        if (size - 1 != this.cacheFIFO.size()) {
            z2 = false;
        }
        XLEAssert.assertTrue(z2);
    }

    private void evictToGivenLimit(int newBytesSize) {
        if (newBytesSize < 0) {
            throw new IllegalArgumentException("newBytesSize");
        }
        while (this.bytesCurrent > newBytesSize) {
            if (!evictOldest()) {
                throw new UnsupportedOperationException("MEMORY CACHE FAILED TO EVICT TO SIZE");
            }
        }
    }

    private boolean evictOldest() {
        XLEAssert.assertTrue(!this.cacheFIFO.isEmpty());
        if (this.cacheFIFO.isEmpty()) {
            return false;
        }
        removeLast();
        return true;
    }
}
