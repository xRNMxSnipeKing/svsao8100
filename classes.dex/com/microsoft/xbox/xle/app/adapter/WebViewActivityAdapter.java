package com.microsoft.xbox.xle.app.adapter;

import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewDatabase;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.toolkit.anim.MAAS.MAASAnimationType;
import com.microsoft.xbox.toolkit.anim.XLEAnimation;
import com.microsoft.xbox.xle.ui.TitleBarView;
import com.microsoft.xbox.xle.viewmodel.AdapterBaseNormal;
import com.microsoft.xbox.xle.viewmodel.WebViewActivityViewModel;
import java.util.ArrayList;

public class WebViewActivityAdapter extends AdapterBaseNormal {
    private TitleBarView titleBar;
    private WebViewActivityViewModel viewModel;
    private WebView webView = ((WebView) findViewById(R.id.webview_webview));

    public WebViewActivityAdapter(WebViewActivityViewModel vm) {
        this.viewModel = vm;
        this.webView.getSettings().setSaveFormData(false);
        this.webView.getSettings().setSavePassword(false);
        this.titleBar = (TitleBarView) findViewById(R.id.title_bar);
        this.webView.requestFocus();
        this.screenBody = findViewById(R.id.webview_activity_body);
    }

    protected void onAppBarUpdated() {
        setAppBarButtonClickListener(R.id.appbar_refresh, new OnClickListener() {
            public void onClick(View arg0) {
                WebViewActivityAdapter.this.viewModel.load(true);
            }
        });
        setAppBarButtonClickListener(R.id.webview_exit, new OnClickListener() {
            public void onClick(View arg0) {
                WebViewActivityAdapter.this.viewModel.exit();
            }
        });
    }

    public void updateViewOverride() {
        this.titleBar.updateIsLoading(this.viewModel.isBusy());
    }

    public WebView getWebView() {
        return this.webView;
    }

    public void onStop() {
    }

    public void onDestroy() {
        super.onDestroy();
        if (this.webView != null) {
            this.webView.destroy();
            this.webView.removeAllViews();
            this.webView = null;
        }
        WebViewDatabase webviewDatabase = WebViewDatabase.getInstance(XboxApplication.MainActivity);
        if (webviewDatabase != null) {
            try {
                webviewDatabase.clearUsernamePassword();
            } catch (Exception ex) {
                XLELog.Diagnostic("XboxAuthActivityAdapter", "can't clear the webivew database " + ex.toString());
            }
        }
    }

    public ArrayList<XLEAnimation> getAnimateIn(boolean goingBack) {
        ArrayList<XLEAnimation> animations = new ArrayList();
        XLEAnimation titleBarAnimation = getTitleBarAnimation(MAASAnimationType.ANIMATE_IN, goingBack);
        if (titleBarAnimation != null) {
            animations.add(titleBarAnimation);
        }
        return animations;
    }

    public ArrayList<XLEAnimation> getAnimateOut(boolean goingBack) {
        ArrayList<XLEAnimation> animations = new ArrayList();
        XLEAnimation titleBarAnimation = getTitleBarAnimation(MAASAnimationType.ANIMATE_OUT, goingBack);
        if (titleBarAnimation != null) {
            animations.add(titleBarAnimation);
        }
        return animations;
    }
}
