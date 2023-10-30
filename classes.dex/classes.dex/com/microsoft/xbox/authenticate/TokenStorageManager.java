package com.microsoft.xbox.authenticate;

import com.microsoft.xbox.service.model.serialization.RefreshTokenRaw;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.XMLHelper;
import com.microsoft.xbox.toolkit.XboxApplication;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class TokenStorageManager implements ITokenStorageManager {
    public RefreshTokenRaw readRefreshTokenFile(String tokenFileName) throws XLEException {
        XLEAssert.assertTrue(ThreadManager.UIThread != Thread.currentThread());
        try {
            FileInputStream fileStream = XboxApplication.Instance.openFileInput(tokenFileName);
            RefreshTokenRaw token = null;
            if (fileStream != null) {
                token = (RefreshTokenRaw) XMLHelper.instance().load(fileStream, RefreshTokenRaw.class);
            }
            if (token != null) {
                return token;
            }
            XboxApplication.Instance.deleteFile(tokenFileName);
            return null;
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    public boolean deleteRefreshTokenFile(String tokenFileName) throws XLEException {
        XLEAssert.assertTrue(ThreadManager.UIThread != Thread.currentThread());
        return XboxApplication.Instance.deleteFile(tokenFileName);
    }

    public boolean saveRefreshTokenFile(String tokenFileName, RefreshTokenRaw tokenRaw) throws XLEException {
        XLEAssert.assertTrue(ThreadManager.UIThread != Thread.currentThread());
        if (tokenRaw == null) {
            return false;
        }
        try {
            XMLHelper.instance().save(tokenRaw, XboxApplication.Instance.openFileOutput(tokenFileName, 0));
            return true;
        } catch (Throwable e) {
            throw new XLEException((long) XLEErrorCode.FAILED_TO_SAVE_REFRESH_TOKEN, e);
        }
    }
}
