package com.microsoft.xbox.toolkit.ui;

public class TextureManagerScaledResourceBitmapRequest {
    public final TextureBindingOption bindingOption;
    public final int resourceId;

    public TextureManagerScaledResourceBitmapRequest(int resourceId) {
        this(resourceId, new TextureBindingOption());
    }

    public TextureManagerScaledResourceBitmapRequest(int resourceId, TextureBindingOption option) {
        this.resourceId = resourceId;
        this.bindingOption = option;
    }

    public boolean equals(Object rhsuntyped) {
        if (this == rhsuntyped) {
            return true;
        }
        if (!(rhsuntyped instanceof TextureManagerScaledResourceBitmapRequest)) {
            return false;
        }
        TextureManagerScaledResourceBitmapRequest rhs = (TextureManagerScaledResourceBitmapRequest) rhsuntyped;
        if (this.resourceId == rhs.resourceId && this.bindingOption.equals(rhs.bindingOption)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return this.resourceId;
    }
}
