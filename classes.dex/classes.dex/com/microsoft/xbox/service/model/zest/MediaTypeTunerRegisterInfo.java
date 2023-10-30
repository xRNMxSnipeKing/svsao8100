package com.microsoft.xbox.service.model.zest;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "MediaTypeTunerRegisterInfo")
public class MediaTypeTunerRegisterInfo {
    @Element
    public boolean Activable;
    @Element
    public boolean Activated;
    @Element
    public String RegisterType;
}
