package com.microsoft.xbox.toolkit.locale;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "LocaleConfigItem")
public class LocaleConfigItem {
    @Element
    public String CountryRegion;
    @Element(required = false)
    public String Language;
    @Element
    public String Locale;
}
