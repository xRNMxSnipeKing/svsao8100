package com.microsoft.xbox.service.model.zest;

import com.microsoft.xbox.service.model.serialization.UTCDateConverter;
import java.util.Date;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.convert.Convert;

@Root(name = "SubscriptionInfo")
public class SubscriptionInfo {
    @Element(required = false)
    public String LastLabelTakedownDate;
    @Element
    public Boolean SubscriptionBillingViolation;
    @Element
    public Boolean SubscriptionEnabled;
    @Element
    @Convert(UTCDateConverter.class)
    public Date SubscriptionEndDate;
    @Element
    public String SubscriptionMeteringCertificate;
    @Element
    public String SubscriptionOfferID;
    @Element
    public boolean SubscriptionPendingCancel;
    @Element
    public String SubscriptionRenewalOfferID;
    @Element
    @Convert(UTCDateConverter.class)
    public Date SubscriptionStartDate;
}
