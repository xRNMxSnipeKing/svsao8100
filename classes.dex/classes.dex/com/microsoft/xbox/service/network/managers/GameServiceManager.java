package com.microsoft.xbox.service.network.managers;

import com.microsoft.xbox.service.model.serialization.GameDataRaw;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.XMLHelper;
import com.microsoft.xbox.toolkit.network.XboxLiveEnvironment;
import java.io.InputStream;

public class GameServiceManager implements IGameServiceManager {
    public String GetGamesUriBase() {
        return XboxLiveEnvironment.Instance().getUdsUrlBaseSecure() + XboxLiveEnvironment.GAME_API_PATH + XboxLiveEnvironment.GAME_QUERY_PARAMS;
    }

    public GameDataRaw getData(String gamertag, String compareGamertag, int pageCount, int pageNumber) throws XLEException {
        String url;
        if (compareGamertag == null || compareGamertag.length() <= 0) {
            url = String.format(GetGamesUriBase(), new Object[]{gamertag, Integer.valueOf(pageCount), Integer.valueOf(pageNumber)});
        } else {
            String GetGamesUriBase = GetGamesUriBase();
            r4 = new Object[3];
            r4[0] = String.format("%s,%s", new Object[]{gamertag, compareGamertag});
            r4[1] = Integer.valueOf(pageCount);
            r4[2] = Integer.valueOf(pageNumber);
            url = String.format(GetGamesUriBase, r4);
        }
        XLELog.Info("GameServiceManager", "Getting game data for " + url);
        InputStream stream = ServiceCommon.getLivenStream(XboxLiveEnvironment.UDS_AUDIENCE_URI, url);
        if (stream != null) {
            return (GameDataRaw) XMLHelper.instance().load(stream, GameDataRaw.class);
        }
        if (compareGamertag == null || compareGamertag.length() == 0) {
            XLELog.Error("GameServiceManager", "Failed to get games data for " + gamertag);
        } else {
            XLELog.Error("GameServiceManager", String.format("Failed to get compare games data for %1 and %2", new Object[]{gamertag, compareGamertag}));
        }
        throw new XLEException(XLEErrorCode.FAILED_TO_GET_GAMES);
    }
}
