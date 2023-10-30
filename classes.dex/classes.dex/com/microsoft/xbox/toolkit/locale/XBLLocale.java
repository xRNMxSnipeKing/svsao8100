package com.microsoft.xbox.toolkit.locale;

import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.XMLHelper;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Iterator;

public class XBLLocale {
    private static final String connector = "-";
    private static final String defaultLocale = "en-us";
    private Hashtable<String, String> localeMap;

    private static class container {
        private static XBLLocale instance = new XBLLocale();

        private container() {
        }
    }

    private XBLLocale() {
        this.localeMap = new Hashtable();
    }

    public static XBLLocale getInstance() {
        return container.instance;
    }

    public void Initialize(InputStream input) {
        XLEAssert.assertNotNull(input);
        if (input != null) {
            try {
                Initialize((LocaleConfig) XMLHelper.instance().load(input, LocaleConfig.class));
            } catch (XLEException e) {
                XLELog.Warning("XBLLocale", "Failed to deserialize the locale input");
            }
        }
    }

    public void Initialize(LocaleConfig config) {
        XLEAssert.assertNotNull(config);
        if (config != null) {
            Iterator i$ = config.items.iterator();
            while (i$.hasNext()) {
                LocaleConfigItem item = (LocaleConfigItem) i$.next();
                String key = getKey(item.CountryRegion, item.Language);
                if (!(key == null || item.Locale == null)) {
                    this.localeMap.put(key, item.Locale);
                }
            }
        }
    }

    public String getSupportedLocale(String countryRegion, String language) {
        String key = getKey(countryRegion, language);
        if (key != null && this.localeMap.containsKey(key)) {
            return (String) this.localeMap.get(key);
        }
        key = getKey(countryRegion, null);
        if (key != null && this.localeMap.containsKey(key)) {
            return (String) this.localeMap.get(key);
        }
        XLELog.Warning("XBLLocale", "Using default locale");
        return defaultLocale;
    }

    private String getKey(String countryRegion, String language) {
        String key;
        if (language != null) {
            key = language.toLowerCase() + connector + countryRegion.toLowerCase();
        } else {
            key = countryRegion.toLowerCase();
        }
        XLEAssert.assertNotNull(key);
        return key;
    }
}
