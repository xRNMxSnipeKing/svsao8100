package com.microsoft.xbox.service.network.managers;

import com.microsoft.xbox.authenticate.PartnerToken;
import com.microsoft.xbox.service.model.serialization.TokensRaw;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.XMLHelper;
import com.microsoft.xbox.toolkit.network.AbstractXLEHttpClient;
import com.microsoft.xbox.toolkit.network.HttpClientFactory;
import com.microsoft.xbox.toolkit.network.XLEHttpStatusAndStream;
import com.microsoft.xbox.toolkit.network.XboxLiveEnvironment;
import com.microsoft.xle.test.interop.TestInterop;
import com.microsoft.xle.test.interop.TestInterop.ServiceManagerActivityStateChange;
import java.util.Hashtable;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;

public class PartnerTokenManager {
    private static PartnerTokenManager instance;
    private CookieStore cookieJar;
    private Hashtable<String, PartnerToken> tokens = new Hashtable();

    public static synchronized PartnerTokenManager getInstance() {
        PartnerTokenManager partnerTokenManager;
        synchronized (PartnerTokenManager.class) {
            if (instance == null) {
                instance = new PartnerTokenManager();
            }
            partnerTokenManager = instance;
        }
        return partnerTokenManager;
    }

    public void setAccessCookie(String cookieValue) {
        if (cookieValue == null || cookieValue.length() <= 0) {
            this.cookieJar = null;
            return;
        }
        this.cookieJar = new BasicCookieStore();
        String[] cookies = cookieValue.split(";");
        for (int i = 0; i < cookies.length; i++) {
            int index = cookies[i].indexOf("=");
            BasicClientCookie c = new BasicClientCookie(cookies[i].substring(0, index), cookies[i].substring(index + 1));
            c.setDomain(XboxLiveEnvironment.Instance().getXboxDomain());
            this.cookieJar.addCookie(c);
        }
    }

    public void setAccessCookieStore(CookieStore cookieStore) {
        this.cookieJar = cookieStore;
    }

    public synchronized PartnerToken getPartnerToken(String audience) throws XLEException {
        PartnerToken rv;
        XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
        PartnerToken newToken = null;
        if (this.tokens.containsKey(audience)) {
            rv = (PartnerToken) this.tokens.get(audience);
            if (rv.isExpired()) {
                XLELog.Info("PartnerTokenManager", "Token expired for " + audience);
            }
        }
        if (this.cookieJar != null) {
            newToken = getTokenFromService(audience);
            if (newToken != null) {
                this.tokens.put(audience, newToken);
            } else {
                XLELog.Diagnostic("PartnerTokenManager", "getTokenFromService returned null");
            }
        } else {
            XLELog.Diagnostic("PartnerTokenManager", "getToken is called before cookie is retrieved!");
        }
        rv = newToken;
        return rv;
    }

    public void expireAllPartnerTokens() {
        this.tokens = new Hashtable();
    }

    public void refreshTokenIfNecessary() {
    }

    private PartnerToken getTokenFromService(String audienceUrl) throws XLEException {
        XLELog.Diagnostic("PartnerTokenManager", "getting token for " + audienceUrl);
        AbstractXLEHttpClient httpclient = HttpClientFactory.networkOperationsFactory.getHttpClient(0);
        String url = XboxLiveEnvironment.Instance().getPartnerTokenPrefixUrl() + audienceUrl;
        HttpGet httpget = new HttpGet(url);
        TestInterop.onServiceManagerActivity(url, ServiceManagerActivityStateChange.Started);
        httpclient.setCookieStore(this.cookieJar);
        httpget.setHeader("Accept", "text/html, application/xhtml+xml, */*");
        httpget.setHeader("Accept-Encoding", "gzip, deflate");
        XLEHttpStatusAndStream statusAndStream = httpclient.getHttpStatusAndStreamInternal(httpget, false);
        if (200 == statusAndStream.statusCode) {
            try {
                PartnerToken token = PartnerToken.parseTokenFromRaw(audienceUrl, (TokensRaw) XMLHelper.instance().load(statusAndStream.stream, TokensRaw.class));
                XLELog.Diagnostic("PartnerTokenManager", "Successfully retrieved token for " + audienceUrl);
                TestInterop.onServiceManagerActivity(url, ServiceManagerActivityStateChange.Completed);
                return token;
            } catch (Throwable e) {
                throw new XLEException((long) XLEErrorCode.INVALID_TOKEN, e);
            }
        }
        XLELog.Error("PartnerTokenManager", "service return error " + Integer.toString(statusAndStream.statusCode));
        TestInterop.onServiceManagerActivity(url, ServiceManagerActivityStateChange.Error);
        throw new XLEException(XLEErrorCode.INVALID_COOKIE);
    }
}
