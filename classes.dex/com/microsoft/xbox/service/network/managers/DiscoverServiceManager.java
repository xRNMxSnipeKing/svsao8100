package com.microsoft.xbox.service.network.managers;

import com.microsoft.xbox.service.model.discover.DiscoverAllMusic;
import com.microsoft.xbox.service.model.zest.CommerceUserInfoModel;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.XMLHelper;
import com.microsoft.xbox.toolkit.network.XLEHttpStatusAndStream;
import com.microsoft.xbox.toolkit.network.XboxLiveEnvironment;
import com.microsoft.xle.test.interop.TestInterop;
import com.microsoft.xle.test.interop.TestInterop.ServiceManagerActivityStateChange;
import java.util.ArrayList;

public class DiscoverServiceManager implements IDiscoverServiceManager {
    public DiscoverAllMusic getAllMusicData() throws XLEException {
        XLELog.Diagnostic("DiscoverServiceManager", "get all music data...");
        String url = String.format(XboxLiveEnvironment.Instance().getDiscoverAllMusicUrlFormat(), new Object[]{CommerceUserInfoModel.getInstance().getLegalLocale()});
        try {
            XLEHttpStatusAndStream statusAndStream = ServiceCommon.getStreamAndStatus(url, new ArrayList());
            if (200 == statusAndStream.statusCode) {
                XLELog.Diagnostic("DiscoverServiceManager", "Successfully retrieved music info.");
                TestInterop.onServiceManagerActivity(url, ServiceManagerActivityStateChange.Completed);
                return (DiscoverAllMusic) XMLHelper.instance().load(statusAndStream.stream, DiscoverAllMusic.class);
            }
            XLELog.Error("DiscoverServiceManager", "music endpoint returned error " + Integer.toString(statusAndStream.statusCode));
            TestInterop.onServiceManagerActivity(url, ServiceManagerActivityStateChange.Error);
            throw new XLEException(XLEErrorCode.FAILED_TO_GET_ALLMUSIC);
        } catch (Throwable e) {
            throw new XLEException((long) XLEErrorCode.FAILED_TO_PARSE_ALLMUSIC_RESPONSE, e);
        } catch (Exception e2) {
            if (e2 instanceof XLEException) {
                throw ((XLEException) e2);
            }
            XLELog.Error("DiscoverServiceManager", "Failed to get music " + e2.toString());
            throw new XLEException(XLEErrorCode.FAILED_TO_GET_ALLMUSIC);
        }
    }
}
