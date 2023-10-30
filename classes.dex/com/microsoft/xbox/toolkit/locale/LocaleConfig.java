package com.microsoft.xbox.toolkit.locale;

import java.util.ArrayList;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name = "LocaleConfig")
public class LocaleConfig {
    @ElementList(inline = true)
    public ArrayList<LocaleConfigItem> items;
}
