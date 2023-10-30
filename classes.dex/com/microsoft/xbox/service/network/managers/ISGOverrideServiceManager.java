package com.microsoft.xbox.service.network.managers;

import com.microsoft.xbox.service.model.serialization.ProgrammingContentManifest;
import com.microsoft.xbox.toolkit.XLEException;

public interface ISGOverrideServiceManager {
    ProgrammingContentManifest getProgrammingContentManifest() throws XLEException;
}
