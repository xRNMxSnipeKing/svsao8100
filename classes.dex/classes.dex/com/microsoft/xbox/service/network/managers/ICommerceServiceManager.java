package com.microsoft.xbox.service.network.managers;

import com.microsoft.xbox.service.model.zest.SignInResponse;
import com.microsoft.xbox.toolkit.XLEException;

public interface ICommerceServiceManager {
    SignInResponse signIn() throws XLEException;
}
