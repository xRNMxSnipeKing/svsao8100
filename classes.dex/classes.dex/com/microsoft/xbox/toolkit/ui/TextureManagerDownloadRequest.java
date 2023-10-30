package com.microsoft.xbox.toolkit.ui;

import java.io.InputStream;
import java.util.concurrent.atomic.AtomicInteger;

public class TextureManagerDownloadRequest implements Comparable<TextureManagerDownloadRequest> {
    private static AtomicInteger nextIndex = new AtomicInteger(0);
    public int index = nextIndex.incrementAndGet();
    public TextureManagerScaledNetworkBitmapRequest key;
    public InputStream stream;

    public TextureManagerDownloadRequest(TextureManagerScaledNetworkBitmapRequest key) {
        this.key = key;
    }

    public int compareTo(TextureManagerDownloadRequest rhs) {
        return this.index - rhs.index;
    }
}
