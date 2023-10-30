package com.microsoft.xbox.toolkit;

import java.io.File;
import java.util.HashMap;

public class XLEFileCacheManager {
    public static XLEFileCache emptyFileCache = new XLEFileCache();
    private static HashMap<String, XLEFileCache> sAllCaches = new HashMap();
    private static HashMap<XLEFileCache, File> sCacheRootDirMap = new HashMap();

    public static synchronized XLEFileCache createCache(String subDirectory, int maxFileNumber) {
        XLEFileCache createCache;
        synchronized (XLEFileCacheManager.class) {
            createCache = createCache(subDirectory, maxFileNumber, true);
        }
        return createCache;
    }

    public static synchronized XLEFileCache createCache(String subDirectory, int maxFileNumber, boolean enabled) {
        XLEFileCache xLEFileCache;
        synchronized (XLEFileCacheManager.class) {
            if (maxFileNumber <= 0) {
                throw new IllegalArgumentException("maxFileNumber must be > 0");
            }
            if (subDirectory != null) {
                if (subDirectory.length() > 0) {
                    XLEFileCache fileCache = (XLEFileCache) sAllCaches.get(subDirectory);
                    if (fileCache != null) {
                        if (fileCache.maxFileNumber != maxFileNumber) {
                            throw new IllegalArgumentException("The same subDirectory with different maxFileNumber already exist.");
                        }
                        xLEFileCache = fileCache;
                    } else if (enabled) {
                        fileCache = new XLEFileCache(subDirectory, maxFileNumber);
                        File rootDir = new File(XboxApplication.Instance.getCacheDir(), subDirectory);
                        if (!rootDir.exists()) {
                            rootDir.mkdirs();
                        }
                        fileCache.size = rootDir.list().length;
                        sAllCaches.put(subDirectory, fileCache);
                        sCacheRootDirMap.put(fileCache, rootDir);
                        xLEFileCache = fileCache;
                    } else {
                        xLEFileCache = emptyFileCache;
                    }
                }
            }
            throw new IllegalArgumentException("subDirectory must be not null and at least one character length");
        }
        return xLEFileCache;
    }

    static File getCacheRootDir(XLEFileCache cache) {
        return (File) sCacheRootDirMap.get(cache);
    }

    public static String getCacheStatus() {
        return sAllCaches.values().toString();
    }
}
