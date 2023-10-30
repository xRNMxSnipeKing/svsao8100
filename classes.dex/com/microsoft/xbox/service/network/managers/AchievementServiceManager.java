package com.microsoft.xbox.service.network.managers;

import com.microsoft.xbox.service.model.serialization.AchievementDataRaw;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.XMLHelper;
import com.microsoft.xbox.toolkit.network.XboxLiveEnvironment;
import java.io.InputStream;

public class AchievementServiceManager implements IAchievementServiceManager {
    public String GetAchievementsUriBase() {
        return XboxLiveEnvironment.Instance().getUdsUrlBaseSecure() + XboxLiveEnvironment.ACHIEVEMENT_API_PATH + XboxLiveEnvironment.ACHIEVEMENT_QUERY_PARAMS;
    }

    public AchievementDataRaw getData(String gamertag, String compareGamertag, long titleId) throws XLEException {
        String url;
        if (compareGamertag == null || compareGamertag.length() <= 0) {
            url = String.format(GetAchievementsUriBase(), new Object[]{gamertag, Long.valueOf(titleId)});
        } else {
            String GetAchievementsUriBase = GetAchievementsUriBase();
            r4 = new Object[2];
            r4[0] = String.format("%s,%s", new Object[]{gamertag, compareGamertag});
            r4[1] = Long.valueOf(titleId);
            url = String.format(GetAchievementsUriBase, r4);
        }
        InputStream stream = ServiceCommon.getLivenStream(XboxLiveEnvironment.UDS_AUDIENCE_URI, url);
        if (stream != null) {
            return (AchievementDataRaw) XMLHelper.instance().load(stream, AchievementDataRaw.class);
        }
        if (compareGamertag == null || compareGamertag.length() == 0) {
            XLELog.Error("AchievementServiceManager", "Failed to get achievements data for " + gamertag);
        } else {
            XLELog.Error("AchievementServiceManager", String.format("Failed to get compare achievements data for %1 and %2", new Object[]{gamertag, compareGamertag}));
        }
        throw new XLEException(XLEErrorCode.FAILED_TO_GET_ACHIEVEMENTS);
    }
}
