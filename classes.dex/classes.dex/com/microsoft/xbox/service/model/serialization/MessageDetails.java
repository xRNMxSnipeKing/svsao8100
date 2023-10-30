package com.microsoft.xbox.service.model.serialization;

import java.util.Date;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.convert.Convert;

@Root
public class MessageDetails {
    @Element(required = false)
    public String MessageBody;
    @Element
    public long MessageId;
    @Element
    public String SenderGamerTag;
    @Element
    @Convert(UTCDateConverter.class)
    public Date SentTime;
}
