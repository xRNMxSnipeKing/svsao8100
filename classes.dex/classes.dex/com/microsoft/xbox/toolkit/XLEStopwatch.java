package com.microsoft.xbox.toolkit;

public class XLEStopwatch {
    private long startMs;

    public XLEStopwatch() {
        this.startMs = 0;
        this.startMs = System.currentTimeMillis();
    }

    public void start() {
        this.startMs = System.currentTimeMillis();
    }

    public long elapsed() {
        return System.currentTimeMillis() - this.startMs;
    }
}
