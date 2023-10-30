package com.microsoft.xbox.service.model.serialization;

import java.util.Date;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.convert.Convert;

@Root(name = "RefreshToken")
public class RefreshTokenRaw {
    @Element(required = true)
    @Convert(UTCDateConverter.class)
    public Date Expires;
    @Element(required = true)
    public String Scope;
    @Element(required = true)
    public String Token;
}
