package com.microsoft.xbox.service.model.serialization;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "Slot")
public class ProgrammingSlot {
    @Element(required = false)
    public ProgrammingAction Action;
    @Element(name = "Description", required = false)
    public String Description;
    @Element(name = "ImageUrl", required = false)
    public String ImageUrl;
    @Element(name = "Title", required = false)
    public String Title;
    @Attribute(name = "type", required = false)
    public String type;
}
