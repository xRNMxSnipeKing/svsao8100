package com.microsoft.xbox.service.model.serialization;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "item")
public class SerializationItem {
    @Element
    public String ProfileProperty;
    @Element
    public String anyType;
}
