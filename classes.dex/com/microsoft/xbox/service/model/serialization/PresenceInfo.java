package com.microsoft.xbox.service.model.serialization;

import java.util.Date;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.convert.Convert;

public class PresenceInfo {
    @Element(required = false)
    public String DetailedPresence;
    @Element(required = false)
    @Convert(UTCDateConverter.class)
    public Date LastSeenDateTime;
    @Element(required = false)
    public long LastSeenTitleId;
    @Element(required = false)
    public String LastSeenTitleName;
    @Element
    public int OnlineState;
}
