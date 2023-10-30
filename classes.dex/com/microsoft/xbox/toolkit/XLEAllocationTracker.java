package com.microsoft.xbox.toolkit;

import java.util.HashMap;

public class XLEAllocationTracker {
    private static XLEAllocationTracker instance = new XLEAllocationTracker();
    private HashMap<String, HashMap<String, Integer>> adapterCounter = new HashMap();

    public static XLEAllocationTracker getInstance() {
        return instance;
    }

    private HashMap<String, Integer> getTagHash(String tag) {
        if (!this.adapterCounter.containsKey(tag)) {
            this.adapterCounter.put(tag, new HashMap());
        }
        return (HashMap) this.adapterCounter.get(tag);
    }

    public void debugIncrement(String tag, String key) {
    }

    public void debugDecrement(String tag, String key) {
    }

    public int debugGetOverallocatedCount(String tag) {
        return 0;
    }

    public int debugGetTotalCount(String tag) {
        return 0;
    }

    public void debugPrintOverallocated(String tag) {
    }
}
