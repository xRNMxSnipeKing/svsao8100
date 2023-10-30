package com.microsoft.xbox.service.network.managers;

import com.microsoft.xbox.service.model.serialization.PrivacySettingsUploadRaw;
import com.microsoft.xbox.service.model.serialization.ProfileDataRaw;
import com.microsoft.xbox.service.model.serialization.ProfileDataUploadRaw;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.XMLHelper;
import com.microsoft.xbox.toolkit.network.XboxLiveEnvironment;
import java.io.InputStream;

public class ProfileServiceManager implements IProfileServiceManager {
    public String GetProfileUriBase() {
        return XboxLiveEnvironment.Instance().getUdsUrlBaseSecure() + XboxLiveEnvironment.PROFILE_API_PATH + XboxLiveEnvironment.PROFILE_QUERY_PARAMS;
    }

    public ProfileDataRaw getData(String gamertag, int sectionFlags) throws XLEException {
        boolean z;
        String url;
        if ((sectionFlags & 128) == 0 || gamertag == null) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        if (gamertag == null) {
            url = String.format(GetProfileUriBase(), new Object[]{Integer.valueOf(sectionFlags)});
        } else {
            url = String.format(GetProfileUriBase() + XboxLiveEnvironment.PROFILE_QUERY_PARAMS_EXTENSION, new Object[]{Integer.valueOf(sectionFlags), gamertag});
        }
        InputStream stream = ServiceCommon.getLivenStream(XboxLiveEnvironment.UDS_AUDIENCE_URI, url);
        if (stream != null) {
            ProfileDataRaw data = (ProfileDataRaw) XMLHelper.instance().load(stream, ProfileDataRaw.class);
            if (data != null) {
                return data;
            }
            throwXLEException(gamertag);
            return null;
        }
        throwXLEException(gamertag);
        return null;
    }

    private void throwXLEException(String gamertag) throws XLEException {
        if (gamertag == null) {
            XLELog.Error("ProfileServiceManager", "Failed to get me profile");
            throw new XLEException(XLEErrorCode.FAILED_TO_GET_ME_PROFILE);
        } else {
            XLELog.Error("ProfileServiceManager", "Failed to get profile for " + gamertag);
            throw new XLEException(XLEErrorCode.FAILED_TO_GET_YOU_PROFILE);
        }
    }

    public boolean saveProfile(ProfileDataUploadRaw newData) throws XLEException {
        String outputXml = XMLHelper.instance().save(newData);
        if (outputXml == null) {
            XLELog.Error("ProfileServiceManager", "Failed to serialize profile xml");
            throw new XLEException(8);
        }
        return ServiceCommon.postLivenStream(XboxLiveEnvironment.UDS_AUDIENCE_URI, XboxLiveEnvironment.Instance().getUdsUrlBaseSecure() + "/Profile.svc/", outputXml);
    }

    public boolean savePrivacy(PrivacySettingsUploadRaw newData) throws XLEException {
        String outputXml = XMLHelper.instance().save(newData);
        if (outputXml == null) {
            XLELog.Error("ProfileServiceManager", "Failed to serialize privacy xml");
            throw new XLEException(8);
        }
        return ServiceCommon.postLivenStream(XboxLiveEnvironment.UDS_AUDIENCE_URI, XboxLiveEnvironment.Instance().getUdsUrlBaseSecure() + "/Profile.svc/", outputXml);
    }
}
