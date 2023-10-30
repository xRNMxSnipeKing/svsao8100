package com.microsoft.xbox.service.model.serialization;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "item")
public class PrivacySetting {
    @Element
    public String PrivacySetting;
    @Element
    public int unsignedInt;

    public PrivacySetting(String setting, int value) {
        this.PrivacySetting = setting;
        this.unsignedInt = value;
    }
}
