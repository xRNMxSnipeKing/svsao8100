package com.microsoft.xbox.service.model.discover;

import java.util.ArrayList;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name = "editorialItems")
public class EditorialItems {
    @ElementList(inline = true, name = "editorialItem", required = false)
    public ArrayList<EditorialItem> EditorialItem;
}
