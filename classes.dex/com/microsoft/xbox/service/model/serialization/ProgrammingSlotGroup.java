package com.microsoft.xbox.service.model.serialization;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "SlotGroup")
public class ProgrammingSlotGroup {
    @Attribute(name = "SequenceId")
    public String SequenceId;
    @Element(required = false)
    public ProgrammingSlot Slot;
}
