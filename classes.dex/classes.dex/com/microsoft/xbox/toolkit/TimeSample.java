package com.microsoft.xbox.toolkit;

public class TimeSample {
    public long finish = -1;
    public long start = System.nanoTime();

    public long getElapsed() {
        if (this.finish == -1) {
            this.finish = System.nanoTime();
        }
        return this.finish - this.start;
    }

    public void setFinished() {
        this.finish = System.nanoTime();
        XLELog.Diagnostic("XLETEST", String.format("Elapsed:%d", new Object[]{Long.valueOf(this.finish - this.start)}));
    }
}
