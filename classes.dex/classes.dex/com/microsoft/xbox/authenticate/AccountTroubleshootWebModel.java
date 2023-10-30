package com.microsoft.xbox.authenticate;

import android.graphics.Bitmap;
import android.webkit.WebView;
import com.microsoft.xbox.authenticate.LoginModel.LoginState;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.XLELog;

public class AccountTroubleshootWebModel extends WebModelBase {
    private static final String LOGIN_FAIL_PARTIAL_URL = "/login.srf";
    private static final String LOGIN_SUCCESS_PARTIAL_URL = "/signin/authenticate";
    private static final String TOU_PARTIAL_URL = "/Account/NewTermsOfUse";

    public AccountTroubleshootWebModel(WebView webView) {
        super(webView);
    }

    public String getWebViewUrl() {
        return this.webView.getUrl();
    }

    public void beginLoadTOU(String url) {
        super.start();
        XLEAssert.assertTrue(url.contains(TOU_PARTIAL_URL));
        this.webView.setWebViewClient(this);
        onStateChanged(getLoginStateOnPageStart(url, LoginState.LOADING_XBOXCOM_TROUBLESHOOT), null);
        this.startLoadingTime = System.currentTimeMillis();
        XLELog.Diagnostic("AccountTroubleshootWebModel", "Begin loading " + url);
        this.webView.loadUrl(url);
    }

    protected HeartBeatUrl getHeartBeatUrl() {
        return this.currentHeartBeatUrl;
    }

    protected LoginState getFailureLoginState() {
        return LoginState.ERROR_LOADING_LOGIN;
    }

    public void stop() {
        if (this.webView != null) {
            this.startLoadingTime = 0;
            super.stop();
        }
    }

    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        XLELog.Warning("AccountTroubleshootWebModel", "Failed to load " + failingUrl);
        XLELog.Warning("AccountTroubleshootWebModel", "failed to load because " + description);
        XLELog.Warning("AccountTroubleshootWebModel", "failed to load with error code " + errorCode);
        onStateChanged(getLoginStateOnPageFinish(failingUrl, LoginState.ERROR_LOADING_XBOXCOM_TROUBLESHOOT), null);
    }

    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        XLELog.Diagnostic("AccountTroubleshootWebModel", "Starting to load page: " + url);
        XLELog.Diagnostic("AccountTroubleshootWebModel", "At Time: " + (System.currentTimeMillis() - this.startLoadingTime));
        this.currentHeartBeatUrl = new HeartBeatUrl(url);
        if (getPath(url) != null) {
            super.onPageStarted(view, url, favicon);
        }
    }

    protected void onStateChanged(LoginState loginState, XLEException exception) {
        boolean shouldStop = false;
        switch (loginState) {
            case ERROR_LOADING_XBOXCOM_TROUBLESHOOT:
            case EXTERNAL_TROUBLESHOOT_REQUIRED:
            case XBOXCOM_COOKIE_SUCCESS:
                shouldStop = true;
                break;
        }
        if (shouldStop) {
            stop();
        }
        super.onStateChanged(loginState, exception);
    }

    public void onPageFinished(WebView view, String url) {
        XLELog.Diagnostic("AccountTroubleshootWebModel", "Loaded page: " + url);
        XLELog.Diagnostic("AccountTroubleshootWebModel", "At Time: " + (System.currentTimeMillis() - this.startLoadingTime));
        String urlPath = getPath(url);
        if (urlPath != null) {
            if (urlPath.contains(TOU_PARTIAL_URL)) {
                onStateChanged(LoginState.LOADED_XBOXCOM_TROUBLESHOOT, null);
            } else if (urlPath.contains(LOGIN_SUCCESS_PARTIAL_URL)) {
                onStateChanged(getLoginStateOnPageFinish(url, LoginState.XBOXCOM_COOKIE_SUCCESS), null);
            } else {
                onStateChanged(getLoginStateOnPageFinish(url, LoginState.EXTERNAL_TROUBLESHOOT_REQUIRED), null);
            }
        }
    }

    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        XLELog.Diagnostic("AccountTroubleshootWebModel", "shouldOverrideUrlLoading: " + url);
        String urlPath = getPath(url);
        if (urlPath == null) {
            onStateChanged(getLoginStateOnPageStart(url, LoginState.ERROR_LOADING_XBOXCOM_TROUBLESHOOT), null);
            return true;
        } else if (!urlPath.contains(LOGIN_FAIL_PARTIAL_URL)) {
            return super.shouldOverrideUrlLoading(view, url);
        } else {
            onStateChanged(getLoginStateOnPageStart(url, LoginState.ERROR_LOADING_XBOXCOM_TROUBLESHOOT), null);
            return true;
        }
    }
}
