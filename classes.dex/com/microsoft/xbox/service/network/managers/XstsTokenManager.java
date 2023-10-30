package com.microsoft.xbox.service.network.managers;

import com.microsoft.xbox.authenticate.LoginModel;
import com.microsoft.xbox.authenticate.XstsToken;
import com.microsoft.xbox.toolkit.StreamUtil;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.toolkit.network.XLEHttpStatusAndStream;
import com.microsoft.xbox.toolkit.network.XboxLiveEnvironment;
import com.microsoft.xle.test.interop.TestInterop;
import com.microsoft.xle.test.interop.TestInterop.ServiceManagerActivityStateChange;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.UUID;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

public class XstsTokenManager {
    private static final String TOKEN_END_TAG = "</EncryptedAssertion>";
    private static final String TOKEN_START_TAG = "<EncryptedAssertion";
    private static XstsTokenManager instance;
    private Hashtable<String, XstsToken> tokens = new Hashtable();

    public static XstsTokenManager getInstance() {
        if (instance == null) {
            instance = new XstsTokenManager();
        }
        return instance;
    }

    public synchronized XstsToken getXstsToken(String audience) throws XLEException {
        return getXstsToken(audience, false);
    }

    public synchronized XstsToken getXstsToken(String audience, boolean forceRefresh) throws XLEException {
        XstsToken rv;
        XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
        if (!(forceRefresh || this.tokens == null || !this.tokens.containsKey(audience))) {
            rv = (XstsToken) this.tokens.get(audience);
            if (rv.isExpired()) {
                XLELog.Info("XstsTokenManager", "Token expired for " + audience);
            }
        }
        XstsToken newToken = getTokenFromService(audience);
        if (newToken != null) {
            this.tokens.put(audience, newToken);
        } else {
            XLELog.Diagnostic("XstsTokenManager", "getXstsTokenFromService returned null");
        }
        rv = newToken;
        return rv;
    }

    public static void expireAllXstsTokens() {
        instance = new XstsTokenManager();
    }

    public void refreshTokenIfNecessary() {
    }

    public XstsToken getHardcodedToken(String audienceUrl) {
        try {
            String body = StreamUtil.ReadAsString(XboxApplication.AssetManager.open("stubdata/CurrentTMFXstsToken.xml"));
            XstsToken newToken = new XstsToken(audienceUrl);
            newToken.setToken(body);
            return newToken;
        } catch (Exception ex) {
            XLELog.Diagnostic("XstsTokenManager", "failed to get token with exception " + ex.toString());
            return null;
        }
    }

    private XstsToken getTokenFromService(String audienceUrl) throws XLEException {
        Throwable e;
        XLELog.Diagnostic("XstsTokenManager", "getting token for " + audienceUrl);
        String url = XboxLiveEnvironment.Instance().getXstsTokenUrl();
        ArrayList<Header> headers = new ArrayList();
        headers.add(new BasicHeader("Authorization", "WLID1.0 t=" + LoginModel.getInstance().getAccessToken()));
        headers.add(new BasicHeader("Content-type", "application/soap+xml; charset=utf-8"));
        try {
            XLEHttpStatusAndStream statusAndStream = ServiceCommon.postStringWithStatus(url, headers, String.format(StreamUtil.ReadAsString(XboxApplication.AssetManager.open("requestTemplate/XstsBody.xml")), new Object[]{UUID.randomUUID().toString(), audienceUrl}));
            if (200 == statusAndStream.statusCode) {
                try {
                    XLELog.Diagnostic("XstsToken", "Successfully retrieved token for " + audienceUrl);
                    TestInterop.onServiceManagerActivity(url, ServiceManagerActivityStateChange.Completed);
                    String response = StreamUtil.ReadAsString(statusAndStream.stream);
                    String token = response.substring(response.indexOf(TOKEN_START_TAG), TOKEN_END_TAG.length() + response.indexOf(TOKEN_END_TAG));
                    XLELog.Diagnostic("XstsToken", "Token string: " + token);
                    XstsToken newToken = new XstsToken(audienceUrl);
                    try {
                        newToken.setToken(token);
                        return newToken;
                    } catch (Exception e2) {
                        e = e2;
                        XstsToken xstsToken = newToken;
                        throw new XLEException((long) XLEErrorCode.INVALID_ACCESS_TOKEN, e);
                    }
                } catch (Exception e3) {
                    e = e3;
                    throw new XLEException((long) XLEErrorCode.INVALID_ACCESS_TOKEN, e);
                }
            }
            XLELog.Error("XstsTokenManager", "service return error " + Integer.toString(statusAndStream.statusCode));
            TestInterop.onServiceManagerActivity(url, ServiceManagerActivityStateChange.Error);
            throw new XLEException(XLEErrorCode.INVALID_ACCESS_TOKEN);
        } catch (Exception e4) {
            XLELog.Error("XstsTokenManager", "Failed to get xsts token " + e4.toString());
            throw new XLEException(XLEErrorCode.INVALID_ACCESS_TOKEN);
        }
    }
}
