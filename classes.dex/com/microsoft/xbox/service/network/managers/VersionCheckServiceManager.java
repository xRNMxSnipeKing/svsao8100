package com.microsoft.xbox.service.network.managers;

import com.microsoft.xbox.service.model.serialization.Version;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.XMLHelper;
import com.microsoft.xbox.toolkit.network.XboxLiveEnvironment;
import java.io.InputStream;

public class VersionCheckServiceManager implements IVersionCheckServiceManager {
    public Version getLatestVersion() throws XLEException {
        String url = XboxLiveEnvironment.Instance().getVersionCheckUrl();
        XLELog.Info("VersionCheckServiceManager", "getting version " + url);
        InputStream stream = ServiceCommon.getStream(url);
        if (stream != null) {
            return (Version) XMLHelper.instance().load(stream, Version.class);
        }
        throw new XLEException(XLEErrorCode.FAILED_TO_CHECK_UPDATE);
    }
}
