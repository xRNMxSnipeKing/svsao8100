package com.microsoft.xbox.service.model.serialization;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "item")
public class ProfileProperty {
    @Element
    public String ProfileProperty;
    @Element(required = false)
    public AnyType anyType;

    public ProfileProperty(String key, String value, String type) {
        this.ProfileProperty = key;
        this.anyType = new AnyType(value, type);
    }
}
