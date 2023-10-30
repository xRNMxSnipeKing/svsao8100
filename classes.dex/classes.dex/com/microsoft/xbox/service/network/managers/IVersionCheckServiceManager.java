package com.microsoft.xbox.service.network.managers;

import com.microsoft.xbox.service.model.serialization.Version;
import com.microsoft.xbox.toolkit.XLEException;

public interface IVersionCheckServiceManager {
    Version getLatestVersion() throws XLEException;
}
