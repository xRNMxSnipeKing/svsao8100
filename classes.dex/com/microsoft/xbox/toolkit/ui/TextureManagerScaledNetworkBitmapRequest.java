package com.microsoft.xbox.toolkit.ui;

import com.microsoft.xbox.toolkit.XLEFileCacheItemKey;

public class TextureManagerScaledNetworkBitmapRequest implements XLEFileCacheItemKey {
    public final TextureBindingOption bindingOption;
    public final String url;

    public TextureManagerScaledNetworkBitmapRequest(String url) {
        this(url, new TextureBindingOption());
    }

    public TextureManagerScaledNetworkBitmapRequest(String url, TextureBindingOption option) {
        this.url = url;
        this.bindingOption = option;
    }

    public boolean equals(Object rhsuntyped) {
        if (this == rhsuntyped) {
            return true;
        }
        if (!(rhsuntyped instanceof TextureManagerScaledNetworkBitmapRequest)) {
            return false;
        }
        TextureManagerScaledNetworkBitmapRequest rhs = (TextureManagerScaledNetworkBitmapRequest) rhsuntyped;
        if (this.url.equals(rhs.url) && this.bindingOption.equals(rhs.bindingOption)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return this.url == null ? 0 : this.url.hashCode();
    }

    public String getKeyString() {
        return this.url;
    }
}
