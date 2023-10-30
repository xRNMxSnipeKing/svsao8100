package com.microsoft.xbox.toolkit;

public class XLEMemoryCacheEntry<V> {
    private int byteCount;
    private V data;
    private int serialNumber;

    public XLEMemoryCacheEntry(V data, int serialNumber, int byteCount) {
        if (data == null) {
            throw new IllegalArgumentException("data");
        } else if (byteCount <= 0) {
            throw new IllegalArgumentException("byteCount");
        } else {
            this.data = data;
            this.serialNumber = serialNumber;
            this.byteCount = byteCount;
        }
    }

    public int getByteCount() {
        return this.byteCount;
    }

    public V getValue() {
        return this.data;
    }

    public boolean equals(Object rhsuntyped) {
        if (this == rhsuntyped) {
            return true;
        }
        if (!(rhsuntyped instanceof XLEMemoryCacheEntry)) {
            return false;
        }
        XLEMemoryCacheEntry<V> rhs = (XLEMemoryCacheEntry) rhsuntyped;
        if (this.data == rhs.data && this.serialNumber == rhs.serialNumber && this.byteCount == rhs.byteCount) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return this.data.hashCode();
    }
}
