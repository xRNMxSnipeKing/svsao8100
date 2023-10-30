package com.microsoft.xbox.toolkit;

import java.util.ArrayList;
import java.util.Iterator;

public class TimeTool {
    private static final double NSTOSEC = 1.0E-9d;
    private static TimeTool instance = new TimeTool();
    private ArrayList<TimeSample> allSamples = new ArrayList();

    public static TimeTool getInstance() {
        return instance;
    }

    public void clear() {
        this.allSamples.clear();
    }

    public TimeSample start() {
        this.allSamples.add(new TimeSample());
        return (TimeSample) this.allSamples.get(this.allSamples.size() - 1);
    }

    public long getAverageTime() {
        long sum = 0;
        long count = (long) this.allSamples.size();
        Iterator i$ = this.allSamples.iterator();
        while (i$.hasNext()) {
            TimeSample sample = (TimeSample) i$.next();
            long elapsed = sample.getElapsed();
            sum += sample.getElapsed();
        }
        return sum / count;
    }

    public long getAverageLPS() {
        return (long) (1.0d / (((double) getAverageTime()) * NSTOSEC));
    }

    public long getMinimumLPS() {
        return (long) (1.0d / (((double) getMaximumTime()) * NSTOSEC));
    }

    public long getMaximumTime() {
        long current = 0;
        Iterator i$ = this.allSamples.iterator();
        while (i$.hasNext()) {
            long elapsed = ((TimeSample) i$.next()).getElapsed();
            if (elapsed > current) {
                current = elapsed;
            }
        }
        return current;
    }

    public long getSampleCount() {
        return (long) this.allSamples.size();
    }
}
