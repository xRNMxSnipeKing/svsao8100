package com.microsoft.xbox.service.model.serialization;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "Tokens")
public class TokensRaw {
    @Element
    public String Partner;
    @Element(required = false)
    public String User;
}
