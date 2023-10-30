package com.microsoft.xbox.service.model.serialization;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class XLEAvatarManifest {
    public static XLEAvatarManifest EMPTYTAR = new XLEAvatarManifest();
    public static XLEAvatarManifest SHADOWTAR = new XLEAvatarManifest();
    @Element
    public boolean Filtered;
    @Element
    public String Manifest;

    public XLEAvatarManifest() {
        this.Filtered = false;
        this.Manifest = null;
    }

    public XLEAvatarManifest(XLEAvatarManifest manifest) {
        if (manifest != null) {
            this.Filtered = manifest.Filtered;
            this.Manifest = manifest.Manifest == null ? null : new String(manifest.Manifest);
        }
    }

    static {
        SHADOWTAR.Filtered = false;
        SHADOWTAR.Manifest = null;
    }
}
