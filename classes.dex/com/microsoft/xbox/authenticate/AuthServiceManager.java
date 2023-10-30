package com.microsoft.xbox.authenticate;

import com.microsoft.xbox.authenticate.XboxComAuthData.AccountTroubleshootType;
import com.microsoft.xbox.toolkit.StreamUtil;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.network.AbstractXLEHttpClient;
import com.microsoft.xbox.toolkit.network.HttpClientFactory;
import com.microsoft.xbox.toolkit.network.XLEHttpStatusAndStream;
import com.microsoft.xbox.toolkit.network.XboxLiveEnvironment;
import com.microsoft.xle.test.interop.TestInterop;
import com.microsoft.xle.test.interop.TestInterop.ServiceManagerActivityStateChange;
import java.io.UnsupportedEncodingException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;

public class AuthServiceManager implements IAuthServiceManager {
    private static final String TOKEN_REFRESH_BODY_TEMPLATE = "grant_type=refresh_token&client_id=%1$s&scope=%2$s&refresh_token=%3$s";
    private static final String XBOXCOM_COOKIE_NAME = "RPSSecAuth";

    public OAuthToken getAccessToken(String refreshToken, String scope) throws XLEException {
        XLEAssert.assertTrue(ThreadManager.UIThread != Thread.currentThread());
        XLEAssert.assertNotNull(refreshToken);
        OAuthToken accessToken = null;
        XLELog.Diagnostic("AuthServiceManager", "Getting access token for scope: " + scope);
        String url = XboxLiveEnvironment.Instance().getLoginRefreshUrlBase();
        AbstractXLEHttpClient httpclient = HttpClientFactory.networkOperationsFactory.getHttpClient(0);
        String postContent = String.format(TOKEN_REFRESH_BODY_TEMPLATE, new Object[]{XboxLiveEnvironment.Instance().getClientId(), scope, refreshToken});
        TestInterop.onServiceManagerActivity(url, ServiceManagerActivityStateChange.Started);
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
        try {
            httpPost.setEntity(new StringEntity(postContent, "UTF-8"));
            XLEHttpStatusAndStream statusAndStream = httpclient.getHttpStatusAndStreamInternal(httpPost, false);
            if (statusAndStream.statusCode == 200) {
                if (statusAndStream.stream == null) {
                    XLELog.Error("AuthServiceManager", "No response stream for " + url);
                    TestInterop.onServiceManagerActivity(url, ServiceManagerActivityStateChange.Error);
                    throw new XLEException(6);
                }
                String responseText = StreamUtil.ReadAsString(statusAndStream.stream);
                if (responseText != null && responseText.contains("access_token")) {
                    XLELog.Diagnostic("AuthServiceManager", "getAccessToken response: " + responseText);
                    OAuthToken[] tokens = OAuthToken.parseTokensFromOAuthResponseBodyJSON(responseText);
                    if (tokens == null || tokens.length != 2) {
                        XLELog.Error("AuthServiceManager", "Expected 2 tokens from fragment");
                        throw new XLEException(XLEErrorCode.FAILED_TO_GET_ACCESS_TOKEN);
                    }
                    for (OAuthToken token : tokens) {
                        switch (token.getType()) {
                            case Access:
                                XboxAuthDataManager.getInstance().addAccessToken(token);
                                accessToken = token;
                                break;
                            case Refresh:
                                XboxAuthDataManager.getInstance().addRefreshToken(token);
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
            if (accessToken != null) {
                return accessToken;
            }
            XLELog.Warning("AuthServiceManager", "Access token is null");
            throw new XLEException(XLEErrorCode.FAILED_TO_GET_ACCESS_TOKEN);
        } catch (UnsupportedEncodingException e) {
            XLELog.Error("AuthServiceManager", e.toString());
            TestInterop.onServiceManagerActivity(url, ServiceManagerActivityStateChange.Error);
            throw new XLEException(4, "getAccessToken", e);
        }
    }

    public XboxComAuthData getXboxComCookie(String accessToken) throws XLEException {
        XLEAssert.assertTrue(ThreadManager.UIThread != Thread.currentThread());
        XLEAssert.assertNotNull(accessToken);
        String url = XboxLiveEnvironment.Instance().getXboxComSetCookieUrl() + "&t=" + accessToken;
        AbstractXLEHttpClient httpclient = HttpClientFactory.noRedirectNetworkOperationsFactory.getHttpClient(0);
        XLELog.Diagnostic("AuthServiceManager", "getXboxComCookie: " + url);
        TestInterop.onServiceManagerActivity(url, ServiceManagerActivityStateChange.Started);
        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader("Content-Type", "application/soap+xml");
        httpclient.setCookieStore(null);
        XLEHttpStatusAndStream statusAndStream = httpclient.getHttpStatusAndStreamInternal(httpGet, true);
        int statusCode = statusAndStream.statusCode;
        XLELog.Diagnostic("AuthServiceManager", "getXboxComCookie status code: " + Integer.toString(statusCode));
        if (statusCode == 200 || statusCode == 302) {
            CookieStore returnCookieStore = null;
            AccountTroubleshootType type = AccountTroubleshootType.NONE;
            if (httpclient.getCookieStore() != null) {
                CookieStore cookieStore = httpclient.getCookieStore();
                if (cookieStore.getCookies() != null) {
                    for (Cookie cookie : cookieStore.getCookies()) {
                        if (cookie.getName().equalsIgnoreCase(XBOXCOM_COOKIE_NAME)) {
                            XLELog.Diagnostic("AuthServiceManager", "getXboxComCookie: got RPSSecAuth cookie");
                            returnCookieStore = cookieStore;
                            break;
                        }
                    }
                }
            }
            if (statusCode == 302) {
                type = AccountTroubleshootType.Other;
                if (statusAndStream.redirectUrl != null) {
                    if (statusAndStream.redirectUrl.contains("/signin/authenticate")) {
                        type = AccountTroubleshootType.NONE;
                    } else if (statusAndStream.redirectUrl.contains("AccountCreation")) {
                        type = AccountTroubleshootType.AccountCreation;
                    } else if (statusAndStream.redirectUrl.contains("/Account/NewTermsOfUse")) {
                        type = AccountTroubleshootType.TOU;
                    } else if (statusAndStream.redirectUrl.contains("SignInUnavailable")) {
                        type = AccountTroubleshootType.NONE;
                        XLELog.Warning("AuthServiceManager", "Failed to get xboxcom cookie because sign in is unavailable right now.");
                        throw new XLEException(XLEErrorCode.FAILED_TO_GET_XBOXCOM_COOKIE);
                    }
                }
            }
            if (returnCookieStore != null) {
                return new XboxComAuthData(returnCookieStore, type, statusAndStream.redirectUrl);
            }
        }
        throw new XLEException(XLEErrorCode.FAILED_TO_GET_XBOXCOM_COOKIE);
    }
}
