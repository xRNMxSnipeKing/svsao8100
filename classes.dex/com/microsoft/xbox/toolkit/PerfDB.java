package com.microsoft.xbox.toolkit;

import java.security.InvalidParameterException;
import java.util.Hashtable;

public class PerfDB {
    private static PerfDB inst = new PerfDB();
    private Hashtable<String, Long> data = new Hashtable();
    private boolean enabled = false;

    public static PerfDB instance() {
        return inst;
    }

    public void setEnabled(boolean e) {
        this.enabled = e;
    }

    public void saveData(String id, long val) {
        if (this.enabled) {
            this.data.put(id, Long.valueOf(val));
        }
    }

    public void saveData(String id, String tag, long val) {
        saveData(buildId(tag, id), val);
    }

    public String buildId(String tag, String id) {
        return tag + "_" + id;
    }

    public long getData(String id) {
        if (this.data.containsKey(id)) {
            return ((Long) this.data.get(id)).longValue();
        }
        throw new InvalidParameterException("Can't find " + id);
    }

    public long getData(String id, String tag) {
        return getData(buildId(tag, id));
    }
}
