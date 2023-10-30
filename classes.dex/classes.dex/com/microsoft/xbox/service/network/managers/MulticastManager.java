package com.microsoft.xbox.service.network.managers;

import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.XLEThread;
import com.microsoft.xbox.toolkit.XboxApplication;

public class MulticastManager {
    private static final String MULTICASTLOCK_NAME = "com.microsoft.xle";
    private static MulticastManager instance = new MulticastManager();
    public UdpSocket socketInstance;
    private Thread updReceiverThread = null;
    private MulticastLock wifiMulticastLock;

    public static void RegisterMulticast(final String groupAddress, final int port, final int delegate) {
        ThreadManager.UIThreadPost(new Runnable() {
            public void run() {
                MulticastManager.getInstance().registerMulticast(groupAddress, port, delegate);
            }
        });
    }

    public static void UnregisterMulticast() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        getInstance().unregisterMulticast();
    }

    private static MulticastManager getInstance() {
        return instance;
    }

    private void registerMulticast(String groupAddress, int port, int delegate) {
        boolean z = false;
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        if (this.socketInstance == null || this.updReceiverThread == null) {
            XLELog.Diagnostic("MulticastManager", "Register multicast for " + groupAddress);
            this.wifiMulticastLock = ((WifiManager) XboxApplication.Instance.getSystemService("wifi")).createMulticastLock(MULTICASTLOCK_NAME);
            this.wifiMulticastLock.setReferenceCounted(false);
            this.wifiMulticastLock.acquire();
            XLEAssert.assertTrue(this.socketInstance == null);
            if (this.updReceiverThread == null) {
                z = true;
            }
            XLEAssert.assertTrue(z);
            this.socketInstance = new UdpSocket(groupAddress, port, delegate);
            this.updReceiverThread = new XLEThread(this.socketInstance, "UpdReceiverThread");
            this.updReceiverThread.setDaemon(true);
            this.updReceiverThread.setPriority(3);
            this.updReceiverThread.start();
            return;
        }
        XLELog.Warning("MulticastManager", "the upd receiver thread already exist, ignore this call");
    }

    private void unregisterMulticast() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        XLELog.Diagnostic("MulticastManager", "unregister multicast");
        if (this.socketInstance != null) {
            this.socketInstance.stop();
        }
        this.socketInstance = null;
        if (this.wifiMulticastLock != null && this.wifiMulticastLock.isHeld()) {
            this.wifiMulticastLock.release();
        }
        this.updReceiverThread = null;
        this.socketInstance = null;
        this.wifiMulticastLock = null;
    }
}
