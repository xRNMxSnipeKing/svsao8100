package com.microsoft.xbox.service.model.serialization;

import java.util.List;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name = "MessageSummariesResponse")
public class MessageSummariesResponse {
    @Element(required = false)
    public String HashCode;
    @ElementList
    public List<MessageSummary> Summaries;
}
