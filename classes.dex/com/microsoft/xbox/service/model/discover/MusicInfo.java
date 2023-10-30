package com.microsoft.xbox.service.model.discover;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "entry")
public class MusicInfo {
    @Element(name = "editorialItems", required = false)
    public EditorialItems EditorialItems;
}
