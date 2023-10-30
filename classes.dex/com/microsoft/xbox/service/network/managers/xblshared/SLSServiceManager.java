package com.microsoft.xbox.service.network.managers.xblshared;

import com.microsoft.xbox.authenticate.PartnerToken;
import com.microsoft.xbox.authenticate.XstsToken;
import com.microsoft.xbox.service.model.MeProfileModel;
import com.microsoft.xbox.service.model.sls.ConsolePresenceInfo;
import com.microsoft.xbox.service.model.sls.GamerContext;
import com.microsoft.xbox.service.model.sls.UserInfo;
import com.microsoft.xbox.service.model.sls.UserTitleHistory;
import com.microsoft.xbox.service.network.managers.PartnerTokenManager;
import com.microsoft.xbox.service.network.managers.ServiceCommon;
import com.microsoft.xbox.service.network.managers.XstsTokenManager;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.Ready;
import com.microsoft.xbox.toolkit.StreamUtil;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.network.XLEHttpStatusAndStream;
import com.microsoft.xbox.toolkit.network.XLEThreadPool;
import com.microsoft.xbox.toolkit.network.XboxLiveEnvironment;
import com.microsoft.xle.test.interop.TestInterop;
import com.microsoft.xle.test.interop.TestInterop.ServiceManagerActivityStateChange;
import java.util.ArrayList;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.codehaus.jackson.map.ObjectMapper;

public class SLSServiceManager implements ISLSServiceManager {
    public static native void nativeGetConsolePresence(String str, String str2, EDSServiceManagerGetJSONWorker eDSServiceManagerGetJSONWorker);

    public static native void nativeGetGamerContext(String str, String str2, EDSServiceManagerGetJSONWorker eDSServiceManagerGetJSONWorker);

    public static native void nativeGetUserTitleHistory(String str, int i, String str2, String str3, String str4, EDSServiceManagerGetJSONWorker eDSServiceManagerGetJSONWorker);

    private String getXstsTokenString(String audience) throws XLEException {
        String tokenStr = "";
        if (XboxLiveEnvironment.Instance().isUsingStub() || !XboxLiveEnvironment.Instance().isUsingToken()) {
            return tokenStr;
        }
        XstsToken token = XstsTokenManager.getInstance().getXstsToken(audience);
        if (token != null) {
            return token.getToken();
        }
        throw new XLEException(XLEErrorCode.INVALID_TOKEN);
    }

    private String getPartnerTokenString(String audience) throws XLEException {
        String tokenStr = "";
        if (XboxLiveEnvironment.Instance().isUsingStub() || !XboxLiveEnvironment.Instance().isUsingToken()) {
            return tokenStr;
        }
        PartnerToken token = PartnerTokenManager.getInstance().getPartnerToken(audience);
        if (token != null) {
            return token.getToken();
        }
        throw new XLEException(XLEErrorCode.INVALID_TOKEN);
    }

    public UserTitleHistory getUserTitleHistory(String xuid, int maxItems, String continuationToken, String locale) throws XLEException {
        String contToken = null;
        if (xuid == null || locale == null) {
            return null;
        }
        final EDSServiceManagerGetJSONWorker worker = new EDSServiceManagerGetJSONWorker();
        final Ready ready = new Ready();
        final String token = getXstsTokenString(XboxLiveEnvironment.SLS_AUDIENCE_URI);
        if (continuationToken != null) {
            contToken = JavaUtil.urlEncode(continuationToken);
        }
        final String str = xuid;
        final int i = maxItems;
        final String str2 = locale;
        XLEThreadPool.nativeOperationsThreadPool.run(new Runnable() {
            public void run() {
                SLSServiceManager.nativeGetUserTitleHistory(str, i, token, contToken, str2, worker);
                ready.setReady();
            }
        });
        ready.waitForReady();
        return (UserTitleHistory) worker.deserialize(UserTitleHistory.class, XLEErrorCode.FAILED_TO_GET_USER_TITLE_HISTORY);
    }

    public GamerContext getGamerContext() throws XLEException {
        XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
        final EDSServiceManagerGetJSONWorker worker = new EDSServiceManagerGetJSONWorker();
        final Ready ready = new Ready();
        final String token = getXstsTokenString(XboxLiveEnvironment.SLS_AUDIENCE_URI);
        final String xuid = getAndCacheUserXuid();
        XLEThreadPool.nativeOperationsThreadPool.run(new Runnable() {
            public void run() {
                SLSServiceManager.nativeGetGamerContext(xuid, token, worker);
                ready.setReady();
            }
        });
        ready.waitForReady();
        return (GamerContext) worker.deserialize(GamerContext.class, XLEErrorCode.FAILED_TO_GET_GAMER_CONTEXT);
    }

    public ConsolePresenceInfo getConsolePresence() throws XLEException {
        final EDSServiceManagerGetJSONWorker worker = new EDSServiceManagerGetJSONWorker();
        final Ready ready = new Ready();
        final String token = getXstsTokenString(XboxLiveEnvironment.SLS_AUDIENCE_URI);
        final String userXuid = getAndCacheUserXuid();
        if (token == null || userXuid == null) {
            return null;
        }
        XLEThreadPool.nativeOperationsThreadPool.run(new Runnable() {
            public void run() {
                SLSServiceManager.nativeGetConsolePresence(userXuid, token, worker);
                ready.setReady();
            }
        });
        ready.waitForReady();
        return (ConsolePresenceInfo) worker.deserialize(ConsolePresenceInfo.class, XLEErrorCode.FAILED_TO_GET_CONSOLE_PRESENCE);
    }

    public String getAndCacheUserXuid() throws XLEException {
        MeProfileModel profileModel = MeProfileModel.getModel();
        String xuid = profileModel.getXuid();
        if (!JavaUtil.isNullOrEmpty(xuid)) {
            return xuid;
        }
        xuid = getXuidFromService();
        profileModel.setXuid(xuid);
        if (!JavaUtil.isNullOrEmpty(xuid)) {
            return xuid;
        }
        throw new XLEException(XLEErrorCode.FAILED_TO_GET_USER_INFO);
    }

    private String getXuidFromService() throws XLEException {
        XLELog.Diagnostic("SLSServiceManager", "getting current gamer's Xuid token for ");
        XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
        UserInfo result = null;
        String url = XboxLiveEnvironment.Instance().getUserInfoUrl();
        ArrayList<Header> headers = new ArrayList();
        headers.add(new BasicHeader("Authorization", "XBL2.0 x=" + getXstsTokenString(XboxLiveEnvironment.SLS_AUDIENCE_URI)));
        headers.add(new BasicHeader("Content-type", "application/soap+xml; charset=utf-8"));
        headers.add(new BasicHeader("x-xbl-contract-version", "1"));
        String response = null;
        try {
            XLEHttpStatusAndStream statusAndStream = ServiceCommon.getStreamAndStatus(url, headers);
            if (200 == statusAndStream.statusCode) {
                response = StreamUtil.ReadAsString(statusAndStream.stream);
            }
            if (response != null) {
                result = (UserInfo) new ObjectMapper().readValue(response, UserInfo.class);
            }
            TestInterop.onServiceManagerActivity(url, result == null ? ServiceManagerActivityStateChange.Error : ServiceManagerActivityStateChange.Completed);
            if (result != null) {
                return result.xuid;
            }
            return null;
        } catch (Throwable ex) {
            XLELog.Error("SLSServiceManager", "failed to get user info with exception " + ex.toString());
            if (ex instanceof XLEException) {
                throw ((XLEException) ex);
            }
            throw new XLEException((long) XLEErrorCode.FAILED_TO_GET_USER_INFO, ex);
        }
    }
}
