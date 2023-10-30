package com.microsoft.xbox.service.network.managers.xblshared;

import com.microsoft.xbox.service.model.sls.ConsolePresenceInfo;
import com.microsoft.xbox.service.model.sls.GamerContext;
import com.microsoft.xbox.service.model.sls.UserTitleHistory;
import com.microsoft.xbox.toolkit.XLEException;

public interface ISLSServiceManager {
    String getAndCacheUserXuid() throws XLEException;

    ConsolePresenceInfo getConsolePresence() throws XLEException;

    GamerContext getGamerContext() throws XLEException;

    UserTitleHistory getUserTitleHistory(String str, int i, String str2, String str3) throws XLEException;
}
