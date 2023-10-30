package com.microsoft.xbox.service.network.managers.xblshared;

import com.microsoft.xbox.toolkit.XLEException;

public interface ICompanionSessionTitleChannelStateListener {
    void onTitleChannelStateChanged(int i, XLEException xLEException);
}
