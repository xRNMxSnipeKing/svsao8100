package com.microsoft.xbox.service.network.managers;

import com.microsoft.xbox.service.model.serialization.AvatarManifestDataRaw;
import com.microsoft.xbox.toolkit.XLEException;

public interface IAvatarManifestServiceManager {
    AvatarManifestDataRaw getGamerData(String str) throws XLEException;

    AvatarManifestDataRaw getPlayerData() throws XLEException;

    boolean savePlayerData(String str) throws XLEException;
}
