package com.microsoft.xbox.service.network.managers;

import com.microsoft.xbox.service.model.serialization.GameDataRaw;
import com.microsoft.xbox.toolkit.XLEException;

public interface IGameServiceManager {
    GameDataRaw getData(String str, String str2, int i, int i2) throws XLEException;
}
