package com.microsoft.xbox.service.model.zest;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "AccountState")
public class AccountState {
    @Element
    public boolean AcceptedTermsOfService;
    @Element
    public boolean AccountSuspended;
    @Element
    public String AccountType;
    @Element
    public boolean BillingUnavailable;
    @Element
    public int SignInErrorCode;
    @Element
    public boolean SubscriptionLapsed;
    @Element
    public boolean TagChangeRequired;
}
