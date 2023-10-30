package com.microsoft.xbox.authenticate;

import org.apache.http.client.CookieStore;

public class XboxComAuthData {
    private CookieStore cookieStore;
    private String redirectUrl;
    private AccountTroubleshootType type;

    public enum AccountTroubleshootType {
        NONE,
        AccountCreation,
        TOU,
        Other
    }

    public XboxComAuthData(CookieStore cookieStore, AccountTroubleshootType troubleshootType, String redirectUrl) {
        this.cookieStore = cookieStore;
        this.type = troubleshootType;
        this.redirectUrl = redirectUrl;
    }

    public CookieStore getCookieStore() {
        return this.cookieStore;
    }

    public AccountTroubleshootType getAccountTroubleshootType() {
        return this.type;
    }

    public String getRedirectUrl() {
        return this.redirectUrl;
    }
}
