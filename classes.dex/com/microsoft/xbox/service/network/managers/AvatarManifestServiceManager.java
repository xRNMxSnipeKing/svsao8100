package com.microsoft.xbox.service.network.managers;

import com.microsoft.xbox.service.model.AvatarManifestUpload;
import com.microsoft.xbox.service.model.serialization.AvatarManifestDataRaw;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.XMLHelper;
import com.microsoft.xbox.toolkit.network.XboxLiveEnvironment;
import java.io.InputStream;

public class AvatarManifestServiceManager implements IAvatarManifestServiceManager {
    public static final int AVATAR_MANIFEST_LENGTH = 2000;
    private static final String GetGamerManifestUri = (XboxLiveEnvironment.Instance().getAvatarReadBaseInsecure() + XboxLiveEnvironment.AVATAR_MANIFEST_PUBLIC_API_PATH + XboxLiveEnvironment.AVATAR_MANIFEST_PUBLIC_QUERY_PARAMS);
    private static final String GetPlayerManifestUri = (XboxLiveEnvironment.Instance().getAvatarReadBaseSecure() + XboxLiveEnvironment.AVATAR_MANIFEST_PRIVATE_API_PATH);
    private static final String UpdatePlayerManifestUri = (XboxLiveEnvironment.Instance().getAvatarWriteBaseSecure() + XboxLiveEnvironment.AVATAR_MANIFEST_UPDATE_API_PATH);

    public AvatarManifestDataRaw getPlayerData() throws XLEException {
        InputStream stream = ServiceCommon.getLivenStream(XboxLiveEnvironment.AVATAR_AUDIENCE_URI, GetPlayerManifestUri);
        if (stream != null) {
            return (AvatarManifestDataRaw) XMLHelper.instance().load(stream, AvatarManifestDataRaw.class);
        }
        XLELog.Error("AvatarManifestServiceManager", "Failed to get avatar manifest!");
        throw new XLEException(XLEErrorCode.FAILED_TO_GET_AVATAR_MANIFEST);
    }

    public AvatarManifestDataRaw getGamerData(String gamertag) throws XLEException {
        if (gamertag == null || gamertag.length() == 0) {
            throw new IllegalArgumentException();
        }
        return (AvatarManifestDataRaw) XMLHelper.instance().load(ServiceCommon.getLivenStream(null, GetGamerManifestUri + gamertag), AvatarManifestDataRaw.class);
    }

    public boolean savePlayerData(String newManifest) throws XLEException {
        AvatarManifestUpload uploadData = new AvatarManifestUpload();
        uploadData.Manifest = newManifest;
        XLEAssert.assertTrue(2000 == uploadData.Manifest.length());
        String outputXml = XMLHelper.instance().save(uploadData);
        if (outputXml != null) {
            return ServiceCommon.postLivenStream(XboxLiveEnvironment.AVATAR_AUDIENCE_URI, UpdatePlayerManifestUri, outputXml);
        }
        XLELog.Error("AvatarManifestServiceManager", "Failed to serialize avatar xml");
        throw new XLEException(8);
    }
}
