package com.microsoft.xbox.service.network.managers;

import com.microsoft.xbox.service.model.MeProfileModel;
import com.microsoft.xbox.service.model.serialization.ProgrammingContentManifest;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.XMLHelper;
import com.microsoft.xbox.toolkit.network.XboxLiveEnvironment;
import java.io.InputStream;
import java.util.ArrayList;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

public class SGOverrideServiceManager implements ISGOverrideServiceManager {
    public ProgrammingContentManifest getProgrammingContentManifest() throws XLEException {
        String url = XboxLiveEnvironment.Instance().getSmartGlassOverrideUrl(MeProfileModel.getModel().getLegalLocale());
        XLELog.Info("ProgrammingServiceManager", "Getting SmartGlass override: " + url);
        ArrayList<Header> headers = new ArrayList();
        headers.add(new BasicHeader("User-Agent", XboxLiveEnvironment.Instance().getSmartGlassOverrideUserAgentString()));
        headers.add(new BasicHeader("X-DeviceType", XboxLiveEnvironment.Instance().getSmartGlassOverrideXDeviceType()));
        InputStream stream = ServiceCommon.getStreamAndStatus(url, headers).stream;
        if (stream != null) {
            return (ProgrammingContentManifest) XMLHelper.instance().load(stream, ProgrammingContentManifest.class);
        }
        throw new XLEException(XLEErrorCode.FAILED_TO_GET_PROGRAMMINGCONTENT);
    }
}
