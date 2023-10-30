package com.microsoft.xbox.authenticate;

import com.microsoft.xbox.toolkit.XLEException;

public interface IAuthServiceManager {
    OAuthToken getAccessToken(String str, String str2) throws XLEException;

    XboxComAuthData getXboxComCookie(String str) throws XLEException;
}
