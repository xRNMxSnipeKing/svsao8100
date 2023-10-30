package com.microsoft.xbox.service.network.managers;

import com.microsoft.xbox.service.model.serialization.AchievementDataRaw;
import com.microsoft.xbox.toolkit.XLEException;

public interface IAchievementServiceManager {
    AchievementDataRaw getData(String str, String str2, long j) throws XLEException;
}
