package com.microsoft.xbox.xle.viewmodel;

import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.microsoft.xbox.service.model.UpdateData;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.xle.app.adapter.WebViewActivityAdapter;
import com.microsoft.xle.test.interop.TestInterop;
import com.microsoft.xle.test.interop.TestInterop.ServiceManagerActivityStateChange;

public class WebViewActivityViewModel extends ViewModelBase {
    private final String JavaScriptForReload = "javascript:window.location.reload( true )";
    private String currentPageUrl;
    private String dataSource = XLEGlobalData.getInstance().getSelectedDataSource();
    private boolean pageLoaded;
    private WebViewActivityClient webViewClient;

    private class WebViewActivityClient extends WebViewClient {
        private long startLoadingTime;
        private WebView webView;

        public WebViewActivityClient(WebView view) {
            this.webView = view;
        }

        public void NavigateBack() {
            if (this.webView.canGoBack()) {
                this.webView.goBack();
            } else {
                WebViewActivityViewModel.this.goBack();
            }
        }

        public void Reload() {
            if (!WebViewActivityViewModel.this.pageLoaded) {
                XLELog.Error("WebViewClient", "Calling refresh before the page was loaded");
            }
            this.webView.loadUrl("javascript:window.location.reload( true )");
        }

        public void Start() {
            this.startLoadingTime = System.currentTimeMillis();
            this.webView.getSettings().setJavaScriptEnabled(true);
            this.webView.getSettings().setSupportZoom(false);
            this.webView.setWebViewClient(this);
            this.webView.getSettings().setUseWideViewPort(true);
            this.webView.getSettings().getUserAgentString();
            this.webView.loadUrl(WebViewActivityViewModel.this.getDataSource());
        }

        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            XLELog.Diagnostic("WebViewClient", "Starting to load page: " + url);
            XLELog.Diagnostic("WebViewClient", "At Time: " + (System.currentTimeMillis() - this.startLoadingTime));
            WebViewActivityViewModel.this.setCurrentPageUrl(url);
            WebViewActivityViewModel.this.setPageLoaded(false);
            TestInterop.onServiceManagerActivity(url, ServiceManagerActivityStateChange.Started);
        }

        public void onPageFinished(WebView view, String url) {
            XLELog.Diagnostic("WebViewClient", "Loaded page: " + url);
            XLELog.Diagnostic("WebViewClient", "At Time: " + (System.currentTimeMillis() - this.startLoadingTime));
            WebViewActivityViewModel.this.setPageLoaded(true);
            TestInterop.onServiceManagerActivity(url, ServiceManagerActivityStateChange.Completed);
        }

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }
    }

    public WebViewActivityViewModel() {
        XLEAssert.assertTrue(this.dataSource != null);
        WebViewActivityAdapter webviewAdapter = new WebViewActivityAdapter(this);
        this.webViewClient = new WebViewActivityClient(webviewAdapter.getWebView());
        this.adapter = webviewAdapter;
    }

    public void onRehydrate() {
        XLEAssert.assertTrue(false);
    }

    public String getCurrentPageUrl() {
        return this.currentPageUrl;
    }

    public void setCurrentPageUrl(String url) {
        this.currentPageUrl = url;
    }

    public String getDataSource() {
        return this.dataSource;
    }

    public void setPageLoaded(boolean value) {
        this.pageLoaded = value;
        if (this.adapter != null) {
            this.adapter.updateView();
        }
    }

    public void updateOverride(AsyncResult<UpdateData> asyncResult) {
    }

    public void load(boolean forceRefresh) {
        if (this.pageLoaded) {
            this.webViewClient.Reload();
        } else {
            this.webViewClient.Start();
        }
    }

    public void onBackButtonPressed() {
        if (this.webViewClient != null) {
            this.webViewClient.NavigateBack();
        } else {
            goBack();
        }
    }

    protected void onStartOverride() {
    }

    protected void onStopOverride() {
    }

    public boolean isBusy() {
        return !this.pageLoaded;
    }

    public void exit() {
        goBack();
    }
}
