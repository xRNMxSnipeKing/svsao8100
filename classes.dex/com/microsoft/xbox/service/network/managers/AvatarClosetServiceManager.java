package com.microsoft.xbox.service.network.managers;

import com.microsoft.xbox.toolkit.StreamUtil;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.network.XboxLiveEnvironment;
import java.io.InputStream;

public class AvatarClosetServiceManager implements IAvatarClosetServiceManager {
    private static final String GetPlayerClosetUri = (XboxLiveEnvironment.Instance().getClosetReadBaseSecure() + XboxLiveEnvironment.AVATAR_CLOSET_API_PATH);

    public byte[] getData() throws XLEException {
        InputStream stream = ServiceCommon.getLivenStream(XboxLiveEnvironment.AVATAR_AUDIENCE_URI, GetPlayerClosetUri);
        if (stream != null) {
            return StreamUtil.CreateByteArray(stream);
        }
        XLELog.Error("AvatarClosetServiceManager", "Failed to get avatar closet!");
        throw new XLEException(XLEErrorCode.FAILED_TO_GET_AVATAR_CLOSET);
    }
}
