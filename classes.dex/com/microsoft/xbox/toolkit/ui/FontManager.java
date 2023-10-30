package com.microsoft.xbox.toolkit.ui;

import android.content.Context;
import android.graphics.Typeface;
import java.util.HashMap;

public class FontManager {
    private static FontManager instance = new FontManager();
    private HashMap<String, Typeface> fonts;

    public static FontManager Instance() {
        return instance;
    }

    public Typeface getTypeface(Context context, String typeface) {
        if (this.fonts == null) {
            this.fonts = new HashMap();
        }
        if (!this.fonts.containsKey(typeface)) {
            this.fonts.put(typeface, Typeface.createFromAsset(context.getAssets(), typeface));
        }
        return (Typeface) this.fonts.get(typeface);
    }
}
