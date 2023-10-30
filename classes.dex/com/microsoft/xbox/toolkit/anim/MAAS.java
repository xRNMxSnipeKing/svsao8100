package com.microsoft.xbox.toolkit.anim;

import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.XMLHelper;
import com.microsoft.xbox.toolkit.XboxApplication;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Hashtable;

public class MAAS {
    private static MAAS instance = new MAAS();
    private final String ASSET_FILENAME = "animation/%sAnimation.xml";
    private final String SDCARD_FILENAME = "/sdcard/bishop/maas/%sAnimation.xml";
    private Hashtable<String, MAASAnimation> maasFileCache = new Hashtable();
    private boolean usingSdcard = false;

    public enum MAASAnimationType {
        ANIMATE_IN,
        ANIMATE_OUT
    }

    public static MAAS getInstance() {
        return instance;
    }

    public MAASAnimation getAnimation(String name) {
        if (name != null) {
            return getMAASFile(name);
        }
        throw new IllegalArgumentException();
    }

    private MAASAnimation getMAASFile(String name) {
        if (!this.maasFileCache.containsKey(name)) {
            MAASAnimation file = loadMAASFile(name);
            if (file != null) {
                this.maasFileCache.put(name, file);
            }
        }
        return (MAASAnimation) this.maasFileCache.get(name);
    }

    private MAASAnimation loadMAASFile(String name) {
        try {
            InputStream s;
            if (this.usingSdcard) {
                s = new FileInputStream(new File(String.format("/sdcard/bishop/maas/%sAnimation.xml", new Object[]{name})));
            } else {
                s = XboxApplication.AssetManager.open(String.format("animation/%sAnimation.xml", new Object[]{name}));
            }
            return (MAASAnimation) XMLHelper.instance().load(s, MAASAnimation.class);
        } catch (Exception e) {
            XLELog.Error("MAAS", "Failed to load file: " + e.toString());
            return null;
        }
    }
}
