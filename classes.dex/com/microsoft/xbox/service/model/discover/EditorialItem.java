package com.microsoft.xbox.service.model.discover;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "editorialItem")
public class EditorialItem {
    @Element(name = "image")
    public ImageUUID ImageUUID;
    @Element(name = "title")
    public String Title;
}
