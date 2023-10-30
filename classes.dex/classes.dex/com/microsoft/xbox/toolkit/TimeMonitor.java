package com.microsoft.xbox.toolkit;

public class TimeMonitor {
    private long startTicks = 0;

    public void start() {
        this.startTicks = System.currentTimeMillis();
    }

    public long currentTime() {
        return System.currentTimeMillis() - this.startTicks;
    }
}
