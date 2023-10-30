package com.microsoft.xbox.service.model.zest;

import com.microsoft.xbox.service.model.serialization.UTCDateConverter;
import java.util.ArrayList;
import java.util.Date;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.convert.Convert;

@Root(name = "SignInResponse")
public class SignInResponse {
    @Element
    public AccountInfo AccountInfo;
    @Element
    public AccountState AccountState;
    @Element
    public Balances Balances;
    @Element
    public SubscriptionInfo SubscriptionInfo;
    @ElementList
    public ArrayList<MediaTypeTunerRegisterInfo> TunerRegisterInfo;
    @Element(required = false)
    @Convert(UTCDateConverter.class)
    public Date retrievedTime;
}
