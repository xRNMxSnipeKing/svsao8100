package com.microsoft.xbox.service.model.serialization;

import java.util.List;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name = "SendMessageRequest")
public class SendMessageRequest {
    @Element
    public String MessageText;
    @ElementList
    public List<String> Recipients;
}
