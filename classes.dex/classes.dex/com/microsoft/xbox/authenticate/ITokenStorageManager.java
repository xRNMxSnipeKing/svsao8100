package com.microsoft.xbox.authenticate;

import com.microsoft.xbox.service.model.serialization.RefreshTokenRaw;
import com.microsoft.xbox.toolkit.XLEException;

public interface ITokenStorageManager {
    boolean deleteRefreshTokenFile(String str) throws XLEException;

    RefreshTokenRaw readRefreshTokenFile(String str) throws XLEException;

    boolean saveRefreshTokenFile(String str, RefreshTokenRaw refreshTokenRaw) throws XLEException;
}
