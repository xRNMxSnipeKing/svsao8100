package com.microsoft.xbox.authenticate;

import android.graphics.Bitmap;
import android.webkit.WebStorage;
import android.webkit.WebView;
import com.microsoft.xbox.authenticate.LoginModel.LoginState;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.network.XboxLiveEnvironment;
import com.microsoft.xle.test.interop.TestInterop;
import java.net.URI;
import java.net.URISyntaxException;

public class AuthWebModel extends WebModelBase {
    private static final String ACCOUNT_CREATION_PARTIAL_URL = "AccountCreation";
    private static final String AUTHORIZATION_PARTIAL_URL = "/oauth20_authorize.srf";
    private static final String ERROR_PARTIAL_URL = "/err.srf";
    private static final String LIVE_ACCOUNT_SIGNUP_PARTIAL_URL = "signup.aspx";
    private static final String LOGIN_SUCCESS_PARTIAL_URL = "/signin/authenticate";
    private static final String PC_EXP_PARTIAL_URL = "&pcexp=false";
    private static final String POST_CREDENTIAL_PARTIAL_URL = "/post.srf";
    private static final String WINDOWS_LIVE_LOGIN_PARTIAL_URL = "/login.srf";
    private static final String WINDOWS_LIVE_LOGOUT_PARTIAL_URL = "/logout.srf";
    private static final String WL_STOP_PARTIAL_URL = "/oauth20_desktop.srf";
    private static final String XBOXCOM_HOME_PARTIAL_URL = "/Home";
    private boolean existingWLID = false;
    private String scope;

    public AuthWebModel(WebView webView) {
        super(webView);
        WebStorage.getInstance().deleteAllData();
        this.webView.clearCache(true);
        this.webView.clearSslPreferences();
    }

    protected HeartBeatUrl getHeartBeatUrl() {
        return this.currentHeartBeatUrl;
    }

    protected LoginState getFailureLoginState() {
        return LoginState.ERROR_LOADING_LOGIN;
    }

    public void beginLogin(String scope) {
        super.start();
        this.scope = scope;
        this.webView.setWebViewClient(this);
        this.startLoadingTime = System.currentTimeMillis();
        String url = XboxLiveEnvironment.Instance().getLoginAuthorizeUrlWithScope(this.scope);
        XLELog.Diagnostic("AuthWebModel", "Begin loading " + url);
        this.webView.loadUrl(url);
    }

    public void stopLogin() {
        if (this.webView != null) {
            this.startLoadingTime = 0;
            super.stop();
        }
    }

    protected void onStateChanged(LoginState loginState, XLEException exception) {
        switch (loginState) {
            case ERROR_LOADING_LOGIN:
            case ERROR_RETRIEVING_REFRESH_TOKEN:
            case ACCESS_TOKEN_SUCCESS:
            case XBOXCOM_COOKIE_SUCCESS:
            case XBOX_ACCOUNT_CREATION:
                stopLogin();
                break;
        }
        super.onStateChanged(loginState, exception);
    }

    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        XLELog.Warning("AuthWebModel", "Failed to load " + failingUrl);
        XLELog.Warning("AuthWebModel", "failed to load because " + description);
        XLELog.Warning("AuthWebModel", "failed to load with error code " + errorCode);
        onStateChanged(getLoginStateOnPageFinish(failingUrl, LoginState.ERROR_LOADING_LOGIN), null);
    }

    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        XLELog.Diagnostic("AuthWebModel", "Starting to load page: " + url);
        XLELog.Diagnostic("AuthWebModel", "At Time: " + (System.currentTimeMillis() - this.startLoadingTime));
        this.currentHeartBeatUrl = new HeartBeatUrl(url);
        String urlPath = getPath(url);
        if (urlPath != null) {
            if (urlPath.contains(POST_CREDENTIAL_PARTIAL_URL)) {
                XLELog.Diagnostic("AuthWebModel", "Posting credentials.");
                onStateChanged(getLoginStateOnPageStart(url, LoginState.POSTING_CREDENTIALS), null);
            } else if (urlPath.contains(AUTHORIZATION_PARTIAL_URL)) {
                XLELog.Diagnostic("AuthWebModel", "Loading login.");
                onStateChanged(getLoginStateOnPageStart(url, LoginState.LOADING_LOGIN), null);
            } else if (urlPath.contains(ACCOUNT_CREATION_PARTIAL_URL)) {
                XLELog.Diagnostic("AuthWebModel", "Loading account creation.");
                onStateChanged(getLoginStateOnPageStart(url, LoginState.XBOX_ACCOUNT_CREATION), null);
            } else {
                super.onPageStarted(view, url, favicon);
            }
        }
    }

    public void onPageFinished(WebView view, String url) {
        XLELog.Diagnostic("AuthWebModel", "Loaded page: " + url);
        XLELog.Diagnostic("AuthWebModel", "At Time: " + (System.currentTimeMillis() - this.startLoadingTime));
        String urlPath = getPath(url);
        if (urlPath != null) {
            if (urlPath.contains(POST_CREDENTIAL_PARTIAL_URL)) {
                XLELog.Diagnostic("AuthWebModel", "Still need to post credentials.");
                onStateChanged(getLoginStateOnPageFinish(url, LoginState.LOADED_LOGIN), null);
            } else if (urlPath.contains(AUTHORIZATION_PARTIAL_URL)) {
                XLELog.Diagnostic("AuthWebModel", "Finished loading the sign in page.");
                TestInterop.DoAutomaticSignin(view);
                onStateChanged(getLoginStateOnPageFinish(url, LoginState.LOADED_LOGIN), null);
            } else {
                if (url.contains(ACCOUNT_CREATION_PARTIAL_URL)) {
                    XLELog.Diagnostic("AuthWebModel", "Finished loading account creation page.");
                    onStateChanged(getLoginStateOnPageFinish(url, LoginState.XBOX_ACCOUNT_CREATION), null);
                } else if (urlPath.contains(LOGIN_SUCCESS_PARTIAL_URL) || urlPath.contains(XBOXCOM_HOME_PARTIAL_URL)) {
                    onAccountCreationComplete(url);
                } else if (urlPath.contains(WL_STOP_PARTIAL_URL)) {
                    XLELog.Diagnostic("AuthWebModel", "Got redirected: " + url);
                    try {
                        String fragment = new URI(url).getFragment();
                        if (fragment == null) {
                            onStateChanged(getLoginStateOnPageStart(url, LoginState.ERROR_RETRIEVING_REFRESH_TOKEN), null);
                        } else if (fragment.contains("access_token")) {
                            XLELog.Diagnostic("AuthWebModel", "Fragment: " + fragment);
                            OAuthToken[] tokens = OAuthToken.parseTokensFromOAuthFragment(fragment);
                            if (tokens == null || tokens.length != 2) {
                                XLELog.Error("AuthWebModel", "Expected 2 tokens from fragment");
                                onStateChanged(getLoginStateOnPageStart(url, LoginState.ERROR_RETRIEVING_REFRESH_TOKEN), null);
                            }
                            for (OAuthToken token : tokens) {
                                switch (token.getType()) {
                                    case Access:
                                        XboxAuthDataManager.getInstance().addAccessToken(token);
                                        break;
                                    case Refresh:
                                        XboxAuthDataManager.getInstance().addRefreshToken(token);
                                        break;
                                    default:
                                        break;
                                }
                            }
                            onStateChanged(getLoginStateOnPageFinish(XboxLiveEnvironment.Instance().getLoginAuthorizeUrlBase(), LoginState.ACCESS_TOKEN_SUCCESS), null);
                        } else {
                            XLELog.Error("AuthWebModel", "Invalid fragment: " + fragment);
                            onStateChanged(getLoginStateOnPageStart(url, LoginState.ERROR_RETRIEVING_REFRESH_TOKEN), null);
                        }
                    } catch (URISyntaxException e) {
                        onStateChanged(getLoginStateOnPageStart(url, LoginState.ERROR_RETRIEVING_REFRESH_TOKEN), null);
                    }
                }
            }
        }
    }

    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        XLELog.Diagnostic("AuthWebModel", "shouldOverrideUrlLoading: " + url);
        String urlPath = getPath(url);
        if (urlPath == null) {
            onStateChanged(getLoginStateOnPageStart(url, LoginState.ERROR_LOADING_LOGIN), null);
            return true;
        } else if (urlPath.contains(ERROR_PARTIAL_URL)) {
            XLELog.Diagnostic("AuthWebModel", "Failed to load authorization page.");
            onStateChanged(getLoginStateOnPageStart(url, LoginState.ERROR_LOADING_LOGIN), null);
            return true;
        } else if (urlPath.contains(WINDOWS_LIVE_LOGIN_PARTIAL_URL)) {
            if (url.contains(ACCOUNT_CREATION_PARTIAL_URL)) {
                XLELog.Diagnostic("AuthWebModel", "Parent over-the-shoulder sign in: " + url);
                return false;
            }
            XLELog.Diagnostic("AuthWebModel", "Restarting login flow: " + url);
            beginLogin(this.scope);
            return true;
        } else if (urlPath.contains(WINDOWS_LIVE_LOGOUT_PARTIAL_URL)) {
            XLELog.Diagnostic("AuthWebModel", "Logged out of windows live.");
            onStateChanged(getLoginStateOnPageStart(url, LoginState.ERROR_LOADING_LOGIN), null);
            return true;
        } else {
            if (urlPath.contains(AUTHORIZATION_PARTIAL_URL)) {
                XLELog.Diagnostic("AuthWebModel", "Authorization url.");
                if (!url.contains(PC_EXP_PARTIAL_URL)) {
                    url = url + PC_EXP_PARTIAL_URL;
                    XLELog.Diagnostic("AuthWebModel", "Override url to : " + url);
                    view.loadUrl(url);
                    return true;
                }
            }
            if (urlPath.contains(LIVE_ACCOUNT_SIGNUP_PARTIAL_URL)) {
                onStateChanged(getLoginStateOnPageStart(url, LoginState.XBOX_ACCOUNT_CREATION), null);
                return true;
            } else if (!url.contains(ACCOUNT_CREATION_PARTIAL_URL)) {
                return super.shouldOverrideUrlLoading(view, url);
            } else {
                XLELog.Diagnostic("AuthWebModel", "Loading account creation page. Don't override");
                onStateChanged(getLoginStateOnPageFinish(url, LoginState.LOADING_LOGIN), null);
                return false;
            }
        }
    }

    private void onAccountCreationComplete(String url) {
        if (this.existingWLID) {
            onStateChanged(getLoginStateOnPageFinish(url, LoginState.XBOXCOM_COOKIE_SUCCESS), null);
        } else {
            beginLogin(this.scope);
        }
    }
}
