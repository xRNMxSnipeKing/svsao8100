package com.microsoft.xbox.service.model.serialization;

import java.util.ArrayList;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name = "ContentManifest")
public class ProgrammingContentManifest {
    @ElementList(name = "Content", required = false)
    public ArrayList<ProgrammingSlotGroup> Content;
    @Element(name = "Culture", required = false)
    public String Culture;
    @Element(name = "LastUpdated", required = false)
    public String LastUpdated;
    @Element(name = "Name", required = false)
    public String Name;
}
