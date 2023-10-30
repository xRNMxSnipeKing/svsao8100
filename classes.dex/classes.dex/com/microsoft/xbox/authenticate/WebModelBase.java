package com.microsoft.xbox.authenticate;

import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.microsoft.xbox.authenticate.LoginModel.LoginState;
import com.microsoft.xbox.avatar.model.AvatarEditorModel;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xle.test.interop.TestInterop;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Timer;
import java.util.TimerTask;

public abstract class WebModelBase extends WebViewClient {
    private static final int WEB_PAGE_LOAD_TIME = 30000;
    protected HeartBeatUrl currentHeartBeatUrl;
    protected Timer heartBeatTimer;
    protected long startLoadingTime = 0;
    protected WebView webView;

    private class HeartBeatTask extends TimerTask {
        private HeartBeatUrl lastCheckedHeartBeatUrl;

        public HeartBeatTask(HeartBeatUrl startVal) {
            this.lastCheckedHeartBeatUrl = startVal;
        }

        public void run() {
            HeartBeatUrl currenHeartBeatUrl = WebModelBase.this.getHeartBeatUrl();
            if (this.lastCheckedHeartBeatUrl == null || !this.lastCheckedHeartBeatUrl.equals(currenHeartBeatUrl) || LoginModel.getInstance().getIsWebViewVisible()) {
                this.lastCheckedHeartBeatUrl = currenHeartBeatUrl;
                if (this.lastCheckedHeartBeatUrl != null) {
                    XLELog.Diagnostic("WebModelHeartBeat", "update loading status, loading " + this.lastCheckedHeartBeatUrl.url);
                    return;
                }
                return;
            }
            XLELog.Error("WebModelHeartBeat", "Stuck on URL " + this.lastCheckedHeartBeatUrl.url);
            ThreadManager.UIThreadPost(new Runnable() {
                public void run() {
                    XboxApplication.Instance.trackError(HeartBeatTask.this.lastCheckedHeartBeatUrl.url);
                    WebModelBase.this.onStateChanged(WebModelBase.this.getFailureLoginState(), new XLEException(2));
                }
            });
        }
    }

    protected abstract LoginState getFailureLoginState();

    protected abstract HeartBeatUrl getHeartBeatUrl();

    public WebModelBase(WebView webView) {
        this.webView = webView;
        this.webView.getSettings().setJavaScriptEnabled(true);
        this.webView.getSettings().setSupportZoom(false);
        this.webView.getSettings().setUseWideViewPort(true);
        this.webView.getSettings().getUserAgentString();
    }

    protected void onStateChanged(LoginState loginState, XLEException exception) {
        LoginModel.getInstance().onStateChanged(loginState, exception);
    }

    protected final LoginState getLoginStateOnPageStart(String url, LoginState loginState) {
        return LoginState.valueOf(TestInterop.getLoginErrorOnPageStart(url, loginState.toString()));
    }

    protected final LoginState getLoginStateOnPageFinish(String url, LoginState loginState) {
        return LoginState.valueOf(TestInterop.getLoginErrorOnPageFinish(url, loginState.toString()));
    }

    protected void start() {
        if (this.heartBeatTimer == null) {
            XLELog.Diagnostic("WebModelHeartBeat", "heart beat created");
            this.heartBeatTimer = new Timer();
        } else {
            XLELog.Diagnostic("WebModelHeartBeat", "heart beat exist, cancell it");
            this.heartBeatTimer.cancel();
            this.heartBeatTimer = new Timer();
        }
        this.heartBeatTimer.schedule(new HeartBeatTask(getHeartBeatUrl()), 30000, 30000);
    }

    protected void stop() {
        if (this.heartBeatTimer != null) {
            XLELog.Diagnostic("WebModelHeartBeat", "heart beat cancelled");
            this.heartBeatTimer.cancel();
            this.heartBeatTimer = null;
        }
        this.webView = null;
    }

    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        String urlPath = getPath(url);
        if (!urlPath.contains("terms.aspx") && !url.contains("privacy.microsoft.com") && !urlPath.contains("legal/LiveTOU")) {
            return super.shouldOverrideUrlLoading(view, urlPath);
        }
        XLELog.Diagnostic("WebModelBase", "launch browser");
        Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(url));
        browserIntent.addFlags(AvatarEditorModel.AVATAREDIT_OPTION_COLOR_FACIAL_FEATURE);
        XboxApplication.Instance.startActivity(browserIntent);
        return true;
    }

    protected String getPath(String url) {
        try {
            return new URI(url).getPath();
        } catch (URISyntaxException e) {
            return null;
        }
    }
}
