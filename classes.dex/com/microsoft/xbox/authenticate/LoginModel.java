package com.microsoft.xbox.authenticate;

import android.webkit.WebView;
import com.microsoft.xbox.authenticate.XboxComAuthData.AccountTroubleshootType;
import com.microsoft.xbox.service.model.AchievementModel;
import com.microsoft.xbox.service.model.ActivityDetailModel;
import com.microsoft.xbox.service.model.ActivitySummaryModel;
import com.microsoft.xbox.service.model.AvatarClosetModel;
import com.microsoft.xbox.service.model.AvatarManifestModel;
import com.microsoft.xbox.service.model.DiscoverModel2;
import com.microsoft.xbox.service.model.FriendsModel;
import com.microsoft.xbox.service.model.GameModel;
import com.microsoft.xbox.service.model.MeProfileModel;
import com.microsoft.xbox.service.model.MessageModel;
import com.microsoft.xbox.service.model.QuickplayModel;
import com.microsoft.xbox.service.model.SessionModel;
import com.microsoft.xbox.service.model.UpdateData;
import com.microsoft.xbox.service.model.UpdateType;
import com.microsoft.xbox.service.model.YouProfileModel;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaItemModel;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaType;
import com.microsoft.xbox.service.model.serialization.RefreshTokenRaw;
import com.microsoft.xbox.service.network.managers.PartnerTokenManager;
import com.microsoft.xbox.service.network.managers.XstsTokenManager;
import com.microsoft.xbox.service.network.managers.xblshared.CompanionSession;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.DataLoaderTask;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.XLEObservable;
import com.microsoft.xbox.toolkit.network.IDataLoaderRunnable;
import com.microsoft.xbox.toolkit.network.XboxLiveEnvironment;
import com.microsoft.xle.test.interop.TestInterop;
import java.util.Date;

public class LoginModel extends XLEObservable<UpdateData> {
    private static final int ERROR_RETRY_MAX = 3;
    private static final long MS_SHOULD_BACKGROUND_REFRESH = 14400000;
    private static final String REFRESH_TOKEN_FILENAME = "refresh.xle.com.microsoft";
    private static int currentErrorCount;
    private static LoginModel instance;
    private String busyText;
    private XLEException currentException;
    private LoginState currentState;
    private boolean isBusy = false;
    private boolean isPartialRefresh = false;
    private boolean isStopped = false;
    private boolean isWebViewVisible = false;
    private Date lastRefreshTime = null;
    private AuthWebModel loginClient;
    private String scope;
    private AccountTroubleshootWebModel troubleshootClient;

    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$microsoft$xbox$authenticate$LoginModel$LoginState = new int[LoginState.values().length];

        static {
            $SwitchMap$com$microsoft$xbox$authenticate$XboxComAuthData$AccountTroubleshootType = new int[AccountTroubleshootType.values().length];
            try {
                $SwitchMap$com$microsoft$xbox$authenticate$XboxComAuthData$AccountTroubleshootType[AccountTroubleshootType.NONE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$authenticate$XboxComAuthData$AccountTroubleshootType[AccountTroubleshootType.AccountCreation.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$authenticate$XboxComAuthData$AccountTroubleshootType[AccountTroubleshootType.TOU.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$authenticate$XboxComAuthData$AccountTroubleshootType[AccountTroubleshootType.Other.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$authenticate$LoginModel$LoginState[LoginState.INITIALIZING.ordinal()] = 1;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$authenticate$LoginModel$LoginState[LoginState.INVALID_REFRESH_TOKEN.ordinal()] = 2;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$authenticate$LoginModel$LoginState[LoginState.LOADING_LOGIN.ordinal()] = 3;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$authenticate$LoginModel$LoginState[LoginState.ERROR_LOADING_LOGIN.ordinal()] = 4;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$authenticate$LoginModel$LoginState[LoginState.LOADED_LOGIN.ordinal()] = 5;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$authenticate$LoginModel$LoginState[LoginState.POSTING_CREDENTIALS.ordinal()] = 6;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$authenticate$LoginModel$LoginState[LoginState.ERROR_RETRIEVING_REFRESH_TOKEN.ordinal()] = 7;
            } catch (NoSuchFieldError e11) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$authenticate$LoginModel$LoginState[LoginState.REFRESHING_ACCESS_TOKEN.ordinal()] = 8;
            } catch (NoSuchFieldError e12) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$authenticate$LoginModel$LoginState[LoginState.ERROR_REFRESHING_ACCESS_TOKEN.ordinal()] = 9;
            } catch (NoSuchFieldError e13) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$authenticate$LoginModel$LoginState[LoginState.ACCESS_TOKEN_SUCCESS.ordinal()] = 10;
            } catch (NoSuchFieldError e14) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$authenticate$LoginModel$LoginState[LoginState.RETRIEVING_XBOXCOM_COOKIE.ordinal()] = 11;
            } catch (NoSuchFieldError e15) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$authenticate$LoginModel$LoginState[LoginState.XBOX_ACCOUNT_CREATION.ordinal()] = 12;
            } catch (NoSuchFieldError e16) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$authenticate$LoginModel$LoginState[LoginState.XBOX_TOU_REQUIRED.ordinal()] = 13;
            } catch (NoSuchFieldError e17) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$authenticate$LoginModel$LoginState[LoginState.EXTERNAL_TROUBLESHOOT_REQUIRED.ordinal()] = 14;
            } catch (NoSuchFieldError e18) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$authenticate$LoginModel$LoginState[LoginState.ERROR_RETRIEVING_XBOXCOM_COOKIE.ordinal()] = 15;
            } catch (NoSuchFieldError e19) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$authenticate$LoginModel$LoginState[LoginState.LOADING_XBOXCOM_TROUBLESHOOT.ordinal()] = 16;
            } catch (NoSuchFieldError e20) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$authenticate$LoginModel$LoginState[LoginState.LOADED_XBOXCOM_TROUBLESHOOT.ordinal()] = 17;
            } catch (NoSuchFieldError e21) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$authenticate$LoginModel$LoginState[LoginState.ERROR_LOADING_XBOXCOM_TROUBLESHOOT.ordinal()] = 18;
            } catch (NoSuchFieldError e22) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$authenticate$LoginModel$LoginState[LoginState.XBOXCOM_COOKIE_SUCCESS.ordinal()] = 19;
            } catch (NoSuchFieldError e23) {
            }
        }
    }

    public enum LoginState {
        NONE,
        INITIALIZING,
        INVALID_REFRESH_TOKEN,
        LOADING_LOGIN,
        ERROR_LOADING_LOGIN,
        LOADED_LOGIN,
        POSTING_CREDENTIALS,
        ERROR_RETRIEVING_REFRESH_TOKEN,
        REFRESHING_ACCESS_TOKEN,
        ERROR_REFRESHING_ACCESS_TOKEN,
        ACCESS_TOKEN_SUCCESS,
        RETRIEVING_XBOXCOM_COOKIE,
        ERROR_RETRIEVING_XBOXCOM_COOKIE,
        LOADING_XBOXCOM_TROUBLESHOOT,
        ERROR_LOADING_XBOXCOM_TROUBLESHOOT,
        LOADED_XBOXCOM_TROUBLESHOOT,
        XBOX_ACCOUNT_CREATION,
        XBOX_TOU_REQUIRED,
        EXTERNAL_TROUBLESHOOT_REQUIRED,
        XBOXCOM_COOKIE_SUCCESS
    }

    private class AccessTokenRunnable extends IDataLoaderRunnable<Boolean> {
        private final String scope;

        public AccessTokenRunnable(String scope) {
            this.scope = scope;
        }

        public void onPreExecute() {
        }

        public Boolean buildData() throws XLEException {
            if (XboxAuthDataManager.getInstance().retrieveNewAccessToken(this.scope)) {
                return Boolean.valueOf(true);
            }
            throw new XLEException(getDefaultErrorCode());
        }

        public void onPostExcute(AsyncResult<Boolean> result) {
            LoginModel.this.onGetAccessTokenComplete(result);
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_GET_ACCESS_TOKEN;
        }
    }

    private class RefreshTokenDeleteRunnable extends IDataLoaderRunnable<Boolean> {
        private final String tokenFileName;

        public RefreshTokenDeleteRunnable(String tokenFileName) {
            this.tokenFileName = tokenFileName;
        }

        public void onPreExecute() {
        }

        public Boolean buildData() throws XLEException {
            return Boolean.valueOf(LoginServiceManagerFactory.getInstance().getTokenStorageManager().deleteRefreshTokenFile(this.tokenFileName));
        }

        public void onPostExcute(AsyncResult<Boolean> result) {
            LoginModel.this.onDeleteRefreshTokenComplete(result);
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_DELETE_REFRESH_TOKEN;
        }
    }

    private class RefreshTokenReadRunnable extends IDataLoaderRunnable<RefreshTokenRaw> {
        private final String tokenFileName;

        public RefreshTokenReadRunnable(String tokenFileName) {
            this.tokenFileName = tokenFileName;
        }

        public void onPreExecute() {
        }

        public RefreshTokenRaw buildData() throws XLEException {
            RefreshTokenRaw token = LoginServiceManagerFactory.getInstance().getTokenStorageManager().readRefreshTokenFile(this.tokenFileName);
            if (token != null) {
                return token;
            }
            throw new XLEException(getDefaultErrorCode());
        }

        public void onPostExcute(AsyncResult<RefreshTokenRaw> result) {
            LoginModel.this.onReadRefreshTokenComplete(result);
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_READ_REFRESH_TOKEN;
        }
    }

    private class RefreshTokenSaveRunnable extends IDataLoaderRunnable<Boolean> {
        private final String tokenFileName;
        private final RefreshTokenRaw tokenRaw;

        public RefreshTokenSaveRunnable(String tokenFileName, RefreshTokenRaw tokenRaw) {
            this.tokenFileName = tokenFileName;
            this.tokenRaw = tokenRaw;
        }

        public void onPreExecute() {
        }

        public Boolean buildData() throws XLEException {
            return Boolean.valueOf(LoginServiceManagerFactory.getInstance().getTokenStorageManager().saveRefreshTokenFile(this.tokenFileName, this.tokenRaw));
        }

        public void onPostExcute(AsyncResult<Boolean> result) {
            LoginModel.this.onSaveRefreshTokenComplete(result);
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_SAVE_REFRESH_TOKEN;
        }
    }

    private class XboxComCookieRunnable extends IDataLoaderRunnable<Boolean> {
        private XboxComCookieRunnable() {
        }

        public void onPreExecute() {
            LoginModel.this.onStateChanged(LoginState.RETRIEVING_XBOXCOM_COOKIE, null);
        }

        public Boolean buildData() throws XLEException {
            if (XboxAuthDataManager.getInstance().getXboxComCookie()) {
                return Boolean.valueOf(true);
            }
            throw new XLEException(getDefaultErrorCode());
        }

        public void onPostExcute(AsyncResult<Boolean> result) {
            LoginModel.this.onGetXboxComCookieComplete(result);
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_GET_XBOXCOM_COOKIE;
        }
    }

    private class XstsTokenRunnable extends IDataLoaderRunnable<Boolean> {
        private final String audienceUri;

        private XstsTokenRunnable(String uri) {
            this.audienceUri = uri;
        }

        public void onPreExecute() {
        }

        public Boolean buildData() throws XLEException {
            XstsTokenManager.getInstance().getXstsToken(this.audienceUri);
            return Boolean.valueOf(true);
        }

        public void onPostExcute(AsyncResult<Boolean> asyncResult) {
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.INVALID_TOKEN;
        }
    }

    private LoginModel() {
    }

    public static LoginModel getInstance() {
        if (instance == null) {
            instance = new LoginModel();
        }
        return instance;
    }

    public static void reset() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        instance.onStop();
        instance.clearObserver();
        instance = new LoginModel();
    }

    public boolean getIsWebViewVisible() {
        return !this.isPartialRefresh && this.isWebViewVisible;
    }

    public boolean getIsBusy() {
        return this.isBusy;
    }

    public String getBusyText() {
        return this.busyText;
    }

    public LoginState getLoginState() {
        return this.currentState;
    }

    public String getAccessToken() throws XLEException {
        String accessToken = XboxAuthDataManager.getInstance().getCurrentAccessToken();
        if (accessToken != null) {
            return accessToken;
        }
        throw new XLEException(XLEErrorCode.INVALID_ACCESS_TOKEN);
    }

    public void initialize(WebView webView) {
        this.isStopped = false;
        this.loginClient = new AuthWebModel(webView);
        this.troubleshootClient = new AccountTroubleshootWebModel(webView);
        resetModel();
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public void resetModel() {
        SessionModel.reset(true);
        MeProfileModel.reset();
        YouProfileModel.reset();
        FriendsModel.reset();
        GameModel.reset();
        MessageModel.reset();
        AchievementModel.reset();
        DiscoverModel2.reset();
        AvatarManifestModel.reset();
        AvatarClosetModel.reset();
        QuickplayModel.reset();
        ActivitySummaryModel.reset();
        ActivityDetailModel.reset();
        EDSV2MediaItemModel.reset();
    }

    public void onStop() {
        this.currentState = LoginState.NONE;
        this.currentException = null;
        if (this.loginClient != null) {
            this.loginClient.stopLogin();
        }
        if (this.troubleshootClient != null) {
            this.troubleshootClient.stop();
        }
        this.isWebViewVisible = false;
        this.isBusy = false;
        this.busyText = null;
        this.scope = null;
        this.loginClient = null;
        this.troubleshootClient = null;
        this.isStopped = true;
        if (this.isPartialRefresh) {
            this.isPartialRefresh = false;
            notifyObservers(new AsyncResult(new UpdateData(UpdateType.AccessTokenRefreshComplete, true), this, null));
        }
        XLELog.Diagnostic("LoginModel", "stopped");
    }

    public void beginLogin() {
        this.isPartialRefresh = false;
        onStateChanged(LoginState.INITIALIZING, null);
    }

    public void refreshAccessTokenIfNecessary() {
        if (!this.isStopped || this.lastRefreshTime == null) {
            XLELog.Warning("LoginModel", "login in progress or never successful, ignore this background refresh request");
            notifyObservers(new AsyncResult(new UpdateData(UpdateType.AccessTokenRefreshComplete, true), this, null));
        } else if (new Date().getTime() - this.lastRefreshTime.getTime() > MS_SHOULD_BACKGROUND_REFRESH) {
            XLELog.Warning("LoginModel", "Access token is old, refresh one before continue");
            XboxAuthDataManager.getInstance().TEST_RESET_ACCESSTOKEN();
            this.isPartialRefresh = true;
            this.isStopped = false;
            this.loginClient = null;
            this.troubleshootClient = null;
            onStateChanged(LoginState.INITIALIZING, null);
        } else {
            XLELog.Diagnostic("LoginModel", "Token still fresh, ignore this refresh request");
            notifyObservers(new AsyncResult(new UpdateData(UpdateType.AccessTokenRefreshComplete, true), this, null));
        }
    }

    public void logOut(boolean forceDeleteRefreshToken) {
        resetModel();
        XboxAuthDataManager.getInstance().resetCookieStore();
        XboxAuthDataManager.getInstance().resetAllTokens();
        PartnerTokenManager.getInstance().expireAllPartnerTokens();
        XstsTokenManager.expireAllXstsTokens();
        if (forceDeleteRefreshToken) {
            deleteRefreshToken();
        }
        onStop();
    }

    void onStateChanged(LoginState loginState, XLEException exception) {
        if (this.isStopped) {
            XLELog.Diagnostic("LoginModel", "Model stopped. Ignoring all state changes.");
        } else if (loginState != this.currentState) {
            XLELog.Diagnostic("LoginModel", "State changed to: " + loginState.toString());
            TestInterop.onLoginStateChanged(loginState.toString(), exception);
            this.currentState = loginState;
            this.currentException = exception;
            UpdateType updateType = UpdateType.LoadingLogin;
            switch (AnonymousClass1.$SwitchMap$com$microsoft$xbox$authenticate$LoginModel$LoginState[this.currentState.ordinal()]) {
                case 1:
                    this.isBusy = true;
                    this.isWebViewVisible = false;
                    updateType = UpdateType.LoadingLogin;
                    getRefreshTokenFromStorage();
                    break;
                case 2:
                    this.isBusy = true;
                    this.isWebViewVisible = false;
                    deleteRefreshToken();
                    beginLoginFlow();
                    break;
                case 3:
                    break;
                case 4:
                    this.isBusy = false;
                    this.isWebViewVisible = false;
                    updateType = UpdateType.LoginError;
                    break;
                case 5:
                    this.isBusy = true;
                    this.isWebViewVisible = true;
                    updateType = UpdateType.LoadingLogin;
                    break;
                case 6:
                    this.isBusy = true;
                    this.isWebViewVisible = false;
                    updateType = UpdateType.LoggingIntoWL;
                    break;
                case 7:
                    this.isBusy = false;
                    this.isWebViewVisible = false;
                    updateType = UpdateType.LoginError;
                    break;
                case 8:
                    this.isBusy = true;
                    this.isWebViewVisible = false;
                    updateType = UpdateType.LoggingIntoWL;
                    getAccessTokenFromRefreshToken();
                    break;
                case 9:
                    this.isBusy = false;
                    this.isWebViewVisible = false;
                    updateType = UpdateType.LoadingLogin;
                    deleteRefreshToken();
                    XboxAuthDataManager.getInstance().resetAllTokens();
                    beginLoginFlow();
                    break;
                case 10:
                    this.isBusy = true;
                    this.isWebViewVisible = false;
                    updateType = UpdateType.LoggingIntoXbox;
                    saveRefreshToken();
                    this.lastRefreshTime = new Date();
                    if (!this.isPartialRefresh) {
                        preloadXstsTokens();
                        getXboxComCookie();
                        break;
                    }
                    XLELog.Diagnostic("LoginModel", "background mode, access token retrieved, stop. ");
                    onStop();
                    return;
                case 11:
                    this.isBusy = true;
                    this.isWebViewVisible = false;
                    updateType = UpdateType.LoggingIntoXbox;
                    break;
                case CompanionSession.LRCERROR_TOO_MANY_CLIENTS /*12*/:
                    this.isBusy = false;
                    this.isWebViewVisible = false;
                    updateType = UpdateType.ExternalAccountCreation;
                    break;
                case CompanionSession.LRCERROR_EXPIRED_COMMAND /*13*/:
                    this.isBusy = true;
                    this.isWebViewVisible = false;
                    updateType = UpdateType.LoggingIntoXbox;
                    beginTOU();
                    break;
                case CompanionSession.LRCERROR_NO_EXCLUSIVE /*14*/:
                    this.isBusy = false;
                    this.isWebViewVisible = false;
                    updateType = UpdateType.ExternalAccountTroubleshootRequired;
                    break;
                case 15:
                    this.isBusy = false;
                    this.isWebViewVisible = false;
                    updateType = UpdateType.LoginError;
                    break;
                case 16:
                    this.isBusy = true;
                    this.isWebViewVisible = false;
                    updateType = UpdateType.LoadingLogin;
                    break;
                case CompanionSession.LRCERROR_TITLECHANNEL_EXISTS /*17*/:
                    this.isBusy = true;
                    this.isWebViewVisible = true;
                    updateType = UpdateType.LoadingLogin;
                    break;
                case EDSV2MediaType.MEDIATYPE_XBOX360GAMECONTENT /*18*/:
                    this.isBusy = false;
                    this.isWebViewVisible = false;
                    updateType = UpdateType.LoginError;
                    break;
                case EDSV2MediaType.MEDIATYPE_XBOX360GAMEDEMO /*19*/:
                    this.isBusy = false;
                    this.isWebViewVisible = false;
                    updateType = UpdateType.LoggedIntoXbox;
                    break;
            }
            this.isBusy = true;
            this.isWebViewVisible = false;
            updateType = UpdateType.LoadingLogin;
            if (updateType == UpdateType.LoggedIntoXbox) {
                currentErrorCount = 0;
            } else if (updateType == UpdateType.LoginError) {
                currentErrorCount++;
                if (currentErrorCount >= 3) {
                    currentErrorCount = 0;
                    deleteRefreshToken();
                }
            }
            if (!this.isPartialRefresh) {
                notifyObservers(new AsyncResult(new UpdateData(updateType, true), this, this.currentException));
            }
        }
    }

    private void beginLoginFlow() {
        if (this.loginClient != null) {
            this.loginClient.beginLogin(this.scope);
        }
    }

    private void getRefreshTokenFromStorage() {
        XLELog.Diagnostic("LoginModel", "Reading refresh token from storage.");
        XboxAuthDataManager.getInstance().resetCookieStore();
        new DataLoaderTask(0, new RefreshTokenReadRunnable(REFRESH_TOKEN_FILENAME)).execute();
    }

    private void deleteRefreshToken() {
        XLELog.Diagnostic("LoginModel", "Deleting refresh token from storage.");
        new DataLoaderTask(0, new RefreshTokenDeleteRunnable(REFRESH_TOKEN_FILENAME)).execute();
    }

    private void saveRefreshToken() {
        XLELog.Diagnostic("LoginModel", "Saving refresh token to storage.");
        RefreshTokenRaw tokenRaw = XboxAuthDataManager.getInstance().getRefreshTokenRaw();
        if (tokenRaw == null) {
            XLELog.Error("LoginModel", "Failed to save refresh token because the raw token is null");
        } else {
            new DataLoaderTask(0, new RefreshTokenSaveRunnable(REFRESH_TOKEN_FILENAME, tokenRaw)).execute();
        }
    }

    private void getAccessTokenFromRefreshToken() {
        XLELog.Diagnostic("LoginModel", "Getting access token with existing refresh token.");
        new DataLoaderTask(0, new AccessTokenRunnable(XboxLiveEnvironment.Instance().getXboxComScope())).execute();
    }

    private void preloadXstsTokens() {
        new DataLoaderTask(new XstsTokenRunnable(XboxLiveEnvironment.SLS_AUDIENCE_URI)).execute();
        new DataLoaderTask(new XstsTokenRunnable(XboxLiveEnvironment.XLINK_AUDIENCE_URI)).execute();
    }

    private void getXboxComCookie() {
        new DataLoaderTask(0, new XboxComCookieRunnable()).execute();
    }

    private void beginTOU() {
        if (!this.isStopped) {
            if (this.troubleshootClient != null) {
                this.troubleshootClient.beginLoadTOU(XboxAuthDataManager.getInstance().getXboxComTroubleshootUrl());
            } else {
                XLELog.Error("LoginModel", "Troubleshoot client should not be null when the model isn't stopped.");
            }
        }
    }

    private void onReadRefreshTokenComplete(AsyncResult<RefreshTokenRaw> result) {
        if (result.getException() == null) {
            RefreshTokenRaw rawToken = (RefreshTokenRaw) result.getResult();
            if (rawToken != null) {
                OAuthToken refreshToken = OAuthToken.parseRefreshTokenFromRaw(rawToken);
                if (refreshToken.isValid()) {
                    XboxAuthDataManager.getInstance().addRefreshToken(refreshToken);
                    onStateChanged(LoginState.REFRESHING_ACCESS_TOKEN, null);
                    return;
                }
            }
        }
        if (this.isPartialRefresh) {
            XLELog.Diagnostic("LoginModel", "background mode, refresh token failed, stop. ");
            onStop();
            return;
        }
        onStateChanged(LoginState.INVALID_REFRESH_TOKEN, null);
    }

    private void onDeleteRefreshTokenComplete(AsyncResult<Boolean> asyncResult) {
    }

    private void onSaveRefreshTokenComplete(AsyncResult<Boolean> asyncResult) {
    }

    private void onGetAccessTokenComplete(AsyncResult<Boolean> result) {
        if (result.getException() == null) {
            onStateChanged(LoginState.ACCESS_TOKEN_SUCCESS, null);
        } else if (this.isPartialRefresh) {
            XLELog.Diagnostic("LoginModel", "background mode, access token failed, stop");
            onStop();
        } else {
            onStateChanged(LoginState.ERROR_REFRESHING_ACCESS_TOKEN, result.getException());
        }
    }

    private void onGetXboxComCookieComplete(AsyncResult<Boolean> result) {
        if (result.getException() == null) {
            switch (XboxAuthDataManager.getInstance().getAccountTroubleshootType()) {
                case NONE:
                    onStateChanged(LoginState.XBOXCOM_COOKIE_SUCCESS, null);
                    return;
                case AccountCreation:
                    onStateChanged(LoginState.XBOX_ACCOUNT_CREATION, null);
                    return;
                case TOU:
                    onStateChanged(LoginState.XBOX_TOU_REQUIRED, null);
                    return;
                case Other:
                    onStateChanged(LoginState.EXTERNAL_TROUBLESHOOT_REQUIRED, null);
                    return;
                default:
                    return;
            }
        }
        onStateChanged(LoginState.ERROR_RETRIEVING_XBOXCOM_COOKIE, result.getException());
    }
}
