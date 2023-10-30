package com.microsoft.xbox.service.model.discover;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.convert.Convert;

@Root(name = "image")
public class ImageUUID {
    @Element(name = "id")
    @Convert(ImageUuidConverter.class)
    public String ID;
}
