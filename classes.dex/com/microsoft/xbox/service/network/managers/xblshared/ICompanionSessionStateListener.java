package com.microsoft.xbox.service.network.managers.xblshared;

import com.microsoft.xbox.toolkit.XLEException;

public interface ICompanionSessionStateListener {
    void onSessionStateChanged(int i, XLEException xLEException);
}
