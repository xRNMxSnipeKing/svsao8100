package com.microsoft.xbox.xle.app.adapter;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.MeProfileModel;
import com.microsoft.xbox.smartglass.canvas.CanvasView;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.ui.ScreenLayout;
import com.microsoft.xbox.toolkit.ui.SwitchPanel;
import com.microsoft.xbox.toolkit.ui.SwitchPanelItem;
import com.microsoft.xbox.xle.app.ApplicationBarManager;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.ui.TitleBarView;
import com.microsoft.xbox.xle.ui.XLEHandleImeRootView;
import com.microsoft.xbox.xle.ui.XLEHandleImeRootView.HandleImeInterface;
import com.microsoft.xbox.xle.viewmodel.AdapterBase;
import com.microsoft.xbox.xle.viewmodel.CanvasWebViewActivityViewModel;
import com.microsoft.xbox.xle.viewmodel.CanvasWebViewActivityViewModel.CanvasViewState;

public class CanvasWebViewActivityAdapter extends AdapterBase {
    private TextView activityTitleView;
    private CanvasView canvasView;
    private SwitchPanelItem canvasViewParent = ((SwitchPanelItem) findViewById(R.id.canvas_loaded));
    private CanvasViewState currentState;
    private ImageView errorImageView;
    private XLEHandleImeRootView handleImeRootView;
    private TextView loadingTextView;
    private ProgressBar mProgressBar;
    private SwitchPanel switchPanel = ((SwitchPanel) findViewById(R.id.canvas_switch_panel));
    private TitleBarView titleBar;
    private CanvasWebViewActivityViewModel viewModel;

    public CanvasWebViewActivityAdapter(CanvasWebViewActivityViewModel vm) {
        this.viewModel = vm;
        addWebView();
        this.mProgressBar = (ProgressBar) findViewById(R.id.canvas_activity_progressbar);
        this.activityTitleView = (TextView) findViewById(R.id.canvas_activity_title);
        this.loadingTextView = (TextView) findViewById(R.id.canvas_loading_text);
        this.errorImageView = (ImageView) findViewById(R.id.canvas_loading_error);
        this.titleBar = (TitleBarView) findViewById(R.id.title_bar);
        this.handleImeRootView = (XLEHandleImeRootView) findViewById(R.id.canvas_webview_layout);
        this.handleImeRootView.setHandleImeInterface(new HandleImeInterface() {
            public void onDismissKeyboard() {
                ApplicationBarManager.getInstance().show();
            }

            public void onShowKeyboard() {
                ApplicationBarManager.getInstance().hide();
            }
        });
    }

    protected void onAppBarUpdated() {
        setAppBarButtonClickListener(R.id.canvas_exit, new OnClickListener() {
            public void onClick(View v) {
                CanvasWebViewActivityAdapter.this.viewModel.exit();
            }
        });
        setAppBarButtonClickListener(R.id.canvas_refresh, new OnClickListener() {
            public void onClick(View arg0) {
                CanvasWebViewActivityAdapter.this.loadLandingPage();
            }
        });
        setAppBarButtonClickListener(R.id.canvas_refresh_icon, new OnClickListener() {
            public void onClick(View arg0) {
                CanvasWebViewActivityAdapter.this.loadLandingPage();
            }
        });
        setAppBarButtonClickListener(R.id.canvas_remote, new OnClickListener() {
            public void onClick(View arg0) {
                CanvasWebViewActivityAdapter.this.viewModel.navigateToRemote();
            }
        });
        updateAppBarButtons();
    }

    private void updateAppBarButtons() {
        int i = 0;
        setAppBarButtonVisibility(R.id.canvas_refresh_icon, this.viewModel.getShowError() ? 0 : 8);
        ApplicationBarManager.getInstance().setShouldShowNowPlaying(this.viewModel.getIsNowPlayingTileVisibleInAppBar());
        if (XLEApplication.Instance.getIsTablet()) {
            int i2;
            setAppBarButtonEnabled(R.id.appbar_remote, this.viewModel.isRemoteButtonEnabled());
            if (this.viewModel.getIsRemoteButtonVisibleInAppBar()) {
                i2 = 0;
            } else {
                i2 = 8;
            }
            setAppBarButtonVisibility(R.id.appbar_remote, i2);
            if (!this.viewModel.getIsRemoteButtonVisibleInAppBar()) {
                i = 8;
            }
            setAppBarButtonVisibility(R.id.appbar_home, i);
            setAppBarMediaButtonVisibility(this.viewModel.shouldShowMediaTransport());
            return;
        }
        setAppBarButtonEnabled(R.id.canvas_remote, this.viewModel.isRemoteButtonEnabled());
        if (!this.viewModel.getIsRemoteButtonVisibleInAppBar()) {
            i = 8;
        }
        setAppBarButtonVisibility(R.id.canvas_remote, i);
    }

    public void updateViewOverride() {
        int i = 0;
        this.titleBar.updateIsLoading(this.viewModel.isBusy());
        this.switchPanel.setState(this.viewModel.getCanvasState().ordinal());
        ProgressBar progressBar = this.mProgressBar;
        int i2 = (this.viewModel.getCanvasState() == CanvasViewState.Splash && this.viewModel.getCanvasInternalStateIsLoading()) ? 0 : 8;
        progressBar.setVisibility(i2);
        this.activityTitleView.setText(this.viewModel.getActivityTitle());
        this.loadingTextView.setText(this.viewModel.getSplashString());
        ImageView imageView = this.errorImageView;
        if (!this.viewModel.getShowError()) {
            i = 8;
        }
        imageView.setVisibility(i);
        updateAppBarButtons();
        if (this.currentState != this.viewModel.getCanvasState()) {
            this.currentState = this.viewModel.getCanvasState();
            updateAppBarButtons();
        }
        if (this.viewModel.drainNeedToLoadActivity()) {
            loadLandingPage();
        }
    }

    private void loadLandingPage() {
        this.viewModel.setInitialLoad();
        this.canvasView.setComponents(this.viewModel.getUsesCapabilities());
        this.canvasView.setClient(this.viewModel.getCanvasClient());
        this.canvasView.setTokenManager(this.viewModel.getTokenManager());
        this.canvasView.setLegalLocale(MeProfileModel.getModel().getLegalLocale());
        this.canvasView.setAllowedUrlPrefixes(this.viewModel.getWhitelistUrls());
        this.canvasView.setAllowedTitleIds(this.viewModel.getWhitelistTitleIds());
        this.canvasView.loadUrl(this.viewModel.getLaunchUrl());
    }

    private void addWebView() {
        XLEAssert.assertTrue(this.canvasView == null);
        this.canvasView = new CanvasView(XLEApplication.getMainActivity());
        this.canvasViewParent.addView(this.canvasView, -1, -1);
    }

    public void onApplicationPause() {
        super.onApplicationPause();
        this.canvasView.stopAllComponents();
        this.canvasView.stopLoading();
        this.canvasView.loadUrl("about:blank");
        XLELog.Diagnostic("CanvasWebViewActivityAdapter", "onApplicationPause stopping all components");
        this.canvasViewParent.removeView(this.canvasView);
        clearCanvasView();
    }

    public void onApplicationResume() {
        super.onApplicationResume();
        if (this.canvasView == null) {
            addWebView();
        }
        this.canvasView.setComponents(this.viewModel.getUsesCapabilities());
        XLELog.Diagnostic("CanvasWebViewActivityAdapter", "onApplicationResume setting all components");
    }

    public void onStop() {
        super.onStop();
        this.handleImeRootView.setHandleImeInterface(null);
    }

    public void onDestroy() {
        super.onDestroy();
        if (this.handleImeRootView != null) {
            this.handleImeRootView.setHandleImeInterface(null);
            this.handleImeRootView = null;
        }
        clearCanvasView();
        XLELog.Diagnostic("CanvasWebViewActivityAdapter", "onDestroy stopping all components");
    }

    private void clearCanvasView() {
        if (this.canvasView != null) {
            XLELog.Diagnostic("CanvasWebViewActiivty", "destroying canvasView ");
            ScreenLayout.removeViewAndWorkaroundAndroidLeaks(this.canvasView);
            this.canvasView.stopAllComponents();
            this.canvasView.stopLoading();
            this.canvasView.removeAllViews();
            this.canvasView.setClient(null);
            this.canvasView.setWebChromeClient(null);
            this.canvasView.setTokenManager(null);
            this.canvasView.setLegalLocale(null);
            this.canvasView.setAllowedUrlPrefixes(null);
            this.canvasView.setAllowedTitleIds(null);
            this.canvasView.destroy();
            this.canvasView = null;
        }
    }
}
