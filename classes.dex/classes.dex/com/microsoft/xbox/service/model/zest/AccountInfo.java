package com.microsoft.xbox.service.model.zest;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "AccountInfo")
public class AccountInfo {
    @Element
    public boolean ExplicitPrivilege;
    @Element
    public boolean Lightweight;
    @Element
    public String Locale;
    @Element
    public boolean ParentallyControlled;
    @Element
    public boolean UsageCollectionAllowed;
    @Element
    public String UserReadID;
    @Element
    public String UserWriteID;
    @Element
    public long Xuid;
    @Element
    public String ZuneTag;
}
