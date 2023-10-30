package com.microsoft.xbox.toolkit.network;

import com.microsoft.xbox.toolkit.XLEThread;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class XLEThreadPool {
    public static XLEThreadPool nativeOperationsThreadPool = new XLEThreadPool(1, 4, "XLENativeOperationsPool");
    public static XLEThreadPool networkOperationsThreadPool = new XLEThreadPool(8, 3, "XLENetworkOperationsPool");
    public static XLEThreadPool textureThreadPool = new XLEThreadPool(8, 1, "XLETexturePool");
    private ExecutorService executor;
    private String name;

    public XLEThreadPool(int nthreads, final int priority, String newname) {
        this.name = newname;
        this.executor = Executors.newFixedThreadPool(nthreads, new ThreadFactory() {
            public Thread newThread(Runnable arg0) {
                Thread t = new XLEThread(arg0, XLEThreadPool.this.name);
                t.setDaemon(true);
                t.setPriority(priority);
                return t;
            }
        });
    }

    public void run(Runnable runnable) {
        this.executor.execute(runnable);
    }
}
