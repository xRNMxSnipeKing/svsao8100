package com.microsoft.xbox.toolkit;

import android.os.SystemClock;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

public class BackgroundThreadWaitor {
    private static BackgroundThreadWaitor instance = new BackgroundThreadWaitor();
    private BackgroundThreadWaitorChangedCallback blockingChangedCallback = null;
    private Hashtable<WaitType, WaitObject> blockingTable = new Hashtable();
    private Ready waitReady = new Ready();
    private ArrayList<Runnable> waitingRunnables = new ArrayList();

    public interface BackgroundThreadWaitorChangedCallback {
        void run(EnumSet<WaitType> enumSet, boolean z);
    }

    private class WaitObject {
        private long expires;
        private WaitType type;

        public WaitObject(WaitType type, long expireMs) {
            this.type = type;
            this.expires = SystemClock.uptimeMillis() + expireMs;
        }

        public boolean isExpired() {
            return this.expires < SystemClock.uptimeMillis();
        }
    }

    public enum WaitType {
        Navigation,
        ApplicationBar,
        ListScroll,
        ListLayout,
        PivotScroll
    }

    public static BackgroundThreadWaitor getInstance() {
        if (instance == null) {
            instance = new BackgroundThreadWaitor();
        }
        return instance;
    }

    public void waitForReady(int timeoutMs) {
        XLEAssert.assertTrue(ThreadManager.UIThread != Thread.currentThread());
        ThreadManager.UIThreadPost(new Runnable() {
            public void run() {
                BackgroundThreadWaitor.this.updateWaitReady();
            }
        });
        this.waitReady.waitForReady(timeoutMs);
    }

    public void setBlocking(WaitType type, int expireMs) {
        XLEAssert.assertTrue(ThreadManager.UIThread == Thread.currentThread());
        XLELog.Diagnostic("MVHFPS", "set blocking for " + type.toString());
        this.blockingTable.put(type, new WaitObject(type, (long) expireMs));
        updateWaitReady();
    }

    public void clearBlocking(WaitType type) {
        XLEAssert.assertTrue(ThreadManager.UIThread == Thread.currentThread());
        XLELog.Diagnostic("MVHFPS", "clear blocking for " + type.toString());
        this.blockingTable.remove(type);
        updateWaitReady();
    }

    public void setChangedCallback(BackgroundThreadWaitorChangedCallback callback) {
        this.blockingChangedCallback = callback;
    }

    public boolean isBlocking() {
        return !this.waitReady.getIsReady();
    }

    private void updateWaitReady() {
        boolean blocking;
        XLEAssert.assertTrue(ThreadManager.UIThread == Thread.currentThread());
        HashSet<WaitType> waitTypesToRemove = new HashSet();
        EnumSet<WaitType> blockingTypes = EnumSet.noneOf(WaitType.class);
        Enumeration<WaitObject> e = this.blockingTable.elements();
        while (e.hasMoreElements()) {
            WaitObject waitObject = (WaitObject) e.nextElement();
            if (waitObject.isExpired()) {
                XLELog.Error("MVHFPS", "Somewhere we forgot to clear the wait object for " + waitObject.type.toString());
                waitTypesToRemove.add(waitObject.type);
            } else {
                blockingTypes.add(waitObject.type);
            }
        }
        Iterator i$ = waitTypesToRemove.iterator();
        while (i$.hasNext()) {
            this.blockingTable.remove((WaitType) i$.next());
        }
        if (this.blockingTable.size() == 0) {
            XLELog.Diagnostic("MVHFPS", "blocking table empty");
            this.waitReady.setReady();
            drainWaitingRunnables();
            blocking = false;
        } else {
            XLELog.Diagnostic("MVHFPS", "blocking table not empty");
            this.waitReady.reset();
            blocking = true;
        }
        if (this.blockingChangedCallback != null) {
            this.blockingChangedCallback.run(blockingTypes, blocking);
        }
    }

    public void postRunnableAfterReady(Runnable r) {
        XLEAssert.assertTrue(ThreadManager.UIThread == Thread.currentThread());
        if (r != null) {
            if (isBlocking()) {
                this.waitingRunnables.add(r);
            } else {
                r.run();
            }
        }
    }

    private void drainWaitingRunnables() {
        XLEAssert.assertTrue(ThreadManager.UIThread == Thread.currentThread());
        Iterator i$ = this.waitingRunnables.iterator();
        while (i$.hasNext()) {
            ((Runnable) i$.next()).run();
        }
        this.waitingRunnables.clear();
    }
}
