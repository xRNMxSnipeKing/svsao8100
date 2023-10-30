package com.microsoft.xbox.authenticate;

import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import com.microsoft.xbox.authenticate.XboxComAuthData.AccountTroubleshootType;
import com.microsoft.xbox.service.model.serialization.RefreshTokenRaw;
import com.microsoft.xbox.service.network.managers.PartnerTokenManager;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.network.XboxLiveEnvironment;
import org.apache.http.cookie.Cookie;

public class XboxAuthDataManager {
    private static XboxAuthDataManager instance;
    private OAuthToken accessToken;
    private AccountTroubleshootType accountTroubleshootType;
    private OAuthToken refreshToken;
    private String xboxComTroubleshootUrl;

    private XboxAuthDataManager() {
    }

    public static XboxAuthDataManager getInstance() {
        if (instance == null) {
            instance = new XboxAuthDataManager();
        }
        return instance;
    }

    public AccountTroubleshootType getAccountTroubleshootType() {
        return this.accountTroubleshootType;
    }

    public String getXboxComTroubleshootUrl() {
        return this.xboxComTroubleshootUrl;
    }

    public String getCurrentAccessToken() {
        if (this.accessToken != null) {
            return this.accessToken.getToken();
        }
        XLELog.Error("XboxAuthDataManager", "The access token is null");
        return null;
    }

    public void TEST_RESET_ACCESSTOKEN() {
    }

    public boolean retrieveNewAccessToken(String scope) throws XLEException {
        XLEAssert.assertTrue(ThreadManager.UIThread != Thread.currentThread());
        resetAccessToken();
        XLEAssert.assertNotNull(this.refreshToken);
        addAccessToken(LoginServiceManagerFactory.getInstance().getAuthServiceManager().getAccessToken(this.refreshToken.getToken(), scope));
        return true;
    }

    public boolean getXboxComCookie() throws XLEException {
        boolean z;
        if (ThreadManager.UIThread != Thread.currentThread()) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        this.accountTroubleshootType = AccountTroubleshootType.NONE;
        this.xboxComTroubleshootUrl = null;
        XLEAssert.assertNotNull(this.accessToken);
        XboxComAuthData authData = LoginServiceManagerFactory.getInstance().getAuthServiceManager().getXboxComCookie(this.accessToken.getToken());
        XLELog.Diagnostic("XboxAuthDataManager", "Cookie for " + XboxLiveEnvironment.Instance().getLoginUrlBaseSecure());
        for (Cookie cookie : authData.getCookieStore().getCookies()) {
            String cookieValue = String.format("%s=%s;domain=%s", new Object[]{cookie.getName(), cookie.getValue(), cookie.getDomain()});
            XLELog.Diagnostic("XboxAuthDataManager", cookieValue);
            CookieManager.getInstance().setCookie(cookie.getDomain(), cookieValue);
        }
        CookieSyncManager.getInstance().sync();
        PartnerTokenManager.getInstance().setAccessCookieStore(authData.getCookieStore());
        this.accountTroubleshootType = authData.getAccountTroubleshootType();
        this.xboxComTroubleshootUrl = authData.getRedirectUrl();
        return true;
    }

    public RefreshTokenRaw getRefreshTokenRaw() {
        if (this.refreshToken == null) {
            return null;
        }
        XLEAssert.assertTrue(this.refreshToken.getType() == TokenType.Refresh);
        return this.refreshToken.getRefreshTokenRaw();
    }

    public void addRefreshToken(OAuthToken newRefreshToken) {
        XLEAssert.assertTrue(newRefreshToken.getType() == TokenType.Refresh);
        XLEAssert.assertTrue(newRefreshToken.isValid());
        this.refreshToken = newRefreshToken;
    }

    public void addAccessToken(OAuthToken newAccessToken) {
        XLEAssert.assertTrue(newAccessToken.getType() == TokenType.Access);
        XLEAssert.assertTrue(newAccessToken.isValid());
        this.accessToken = newAccessToken;
    }

    public void resetAllTokens() {
        XLEAssert.assertTrue(ThreadManager.UIThread == Thread.currentThread());
        this.refreshToken = null;
        this.accessToken = null;
    }

    private void resetAccessToken() {
        this.accessToken = null;
    }

    public void resetCookieStore() {
        PartnerTokenManager.getInstance().setAccessCookie(null);
        PartnerTokenManager.getInstance().setAccessCookieStore(null);
        CookieManager.getInstance().removeAllCookie();
        CookieSyncManager.getInstance().sync();
    }
}
