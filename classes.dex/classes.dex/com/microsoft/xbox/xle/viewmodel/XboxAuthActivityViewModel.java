package com.microsoft.xbox.xle.viewmodel;

import android.content.Intent;
import android.net.Uri;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.authenticate.LoginModel;
import com.microsoft.xbox.avatar.model.AvatarEditorModel;
import com.microsoft.xbox.avatar.model.AvatarRendererModel;
import com.microsoft.xbox.service.model.MeProfileModel;
import com.microsoft.xbox.service.model.MessageModel;
import com.microsoft.xbox.service.model.NowPlayingGlobalModel;
import com.microsoft.xbox.service.model.UpdateData;
import com.microsoft.xbox.service.model.UpdateType;
import com.microsoft.xbox.service.model.VersionModel;
import com.microsoft.xbox.service.network.managers.ServiceCommon;
import com.microsoft.xbox.toolkit.ApplicationSettingManager;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.toolkit.network.XboxLiveEnvironment;
import com.microsoft.xbox.xle.app.ApplicationBarManager;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.app.XboxMobileOmnitureTracking;
import com.microsoft.xbox.xle.app.activity.MainPivotActivity;
import com.microsoft.xbox.xle.app.activity.NowPlayingActivity;
import com.microsoft.xbox.xle.app.activity.WhatsNewActivity;
import com.microsoft.xbox.xle.app.activity.XboxAuthActivity;
import com.microsoft.xbox.xle.app.adapter.XboxAuthActivityAdapter;
import java.util.EnumSet;

public class XboxAuthActivityViewModel extends ViewModelBase {
    private String loadingText;
    private LoginAnimationState loginAnimationState;
    private Runnable onSignedInAnimationCompleted;
    private boolean showLoginError;

    public enum LoginAnimationState {
        NotStarted,
        SigningIn,
        SignedIn
    }

    public String getLoadingText() {
        return this.loadingText;
    }

    public boolean getShowLoginError() {
        return this.showLoginError;
    }

    public boolean getWebviewVisible() {
        return LoginModel.getInstance().getIsWebViewVisible();
    }

    public LoginAnimationState getLoginAnimationState() {
        return this.loginAnimationState;
    }

    public boolean isBusy() {
        return LoginModel.getInstance().getIsBusy() || MeProfileModel.getModel().getIsLoading();
    }

    public XboxAuthActivityViewModel() {
        this.showLoginError = false;
        this.loginAnimationState = LoginAnimationState.NotStarted;
        this.adapter = new XboxAuthActivityAdapter(this);
        this.showLoginError = XLEGlobalData.getInstance().getShowLoginError();
    }

    public void onRehydrate() {
        XLEAssert.assertTrue(false);
    }

    public void load(boolean forceRefresh) {
    }

    public void beginLogin() {
        this.loadingText = null;
        this.loginAnimationState = LoginAnimationState.NotStarted;
        this.onSignedInAnimationCompleted = null;
        setShowLoginError(false);
        try {
            ServiceCommon.checkConnectivity();
        } catch (XLEException ex) {
            if (ex.getErrorCode() == 1) {
                showMustActDialog(XLEApplication.Resources.getString(R.string.dialog_attention_title), XLEApplication.Resources.getString(R.string.dialog_connection_required_description), XLEApplication.Resources.getString(R.string.OK), new Runnable() {
                    public void run() {
                        XboxAuthActivityViewModel.this.onLoginError("no network access during login");
                    }
                }, false);
                return;
            }
        }
        if (!AvatarRendererModel.getInstance().didNativeLibraryLoad()) {
            showMustActDialog(XboxApplication.Resources.getString(R.string.device_specs_fail_title), XboxApplication.Resources.getString(R.string.device_specs_cant_deflate_native_so), XboxApplication.Resources.getString(R.string.OK), new Runnable() {
                public void run() {
                    XboxApplication.Instance.killApp(true);
                }
            }, true);
        }
        setUpdateTypesToCheck(EnumSet.of(UpdateType.MeProfileData, UpdateType.GamerContext, UpdateType.CombinedContentRating));
        if (XboxLiveEnvironment.Instance().isUsingStub()) {
            this.loginAnimationState = LoginAnimationState.SigningIn;
            this.loadingText = XLEApplication.Resources.getString(R.string.login_status_retrieving_xbox_live_data);
            MeProfileModel.getModel().addObserver(this);
            MeProfileModel.getModel().load(true);
        } else {
            LoginModel.getInstance().setScope(XboxLiveEnvironment.Instance().getXboxComScope());
            LoginModel.getInstance().beginLogin();
        }
        if (this.adapter != null) {
            this.adapter.updateView();
        }
    }

    public void cancelLogin(boolean clearState) {
        if (clearState) {
            XLELog.Diagnostic("XboxAuthActivityViewModel", "cancel, reset model");
            LoginModel.getInstance().logOut(true);
        }
        LoginModel.reset();
        NavigateTo(XboxAuthActivity.class, false);
    }

    protected void onStartOverride() {
        LoginModel.getInstance().addObserver(this);
        LoginModel.getInstance().initialize(((XboxAuthActivityAdapter) this.adapter).getWebView());
        XLEGlobalData.getInstance().setLoggedIn(false);
        XLEGlobalData.getInstance().resetGlobalParameters();
    }

    protected void onStopOverride() {
        LoginModel.getInstance().removeObserver(this);
        LoginModel.getInstance().onStop();
        XLELog.Diagnostic("XboxAuthActivityViewModel", "removed me profile model");
        MeProfileModel.getModel().removeObserver(this);
    }

    public void updateOverride(AsyncResult<UpdateData> asyncResult) {
        boolean onLoginErrorAtEnd = false;
        String onLoginErrorAtEndString = null;
        UpdateType type = ((UpdateData) asyncResult.getResult()).getUpdateType();
        if (asyncResult.getException() != null) {
            XboxMobileOmnitureTracking.TrackError(Long.toString(asyncResult.getException().getErrorCode()));
        }
        switch (type) {
            case LoadingLogin:
                this.loadingText = XLEApplication.Resources.getString(R.string.loading);
                this.loginAnimationState = LoginAnimationState.SigningIn;
                break;
            case LoggingIntoWL:
                this.loadingText = XLEApplication.Resources.getString(R.string.login_status_signing_in);
                this.loginAnimationState = LoginAnimationState.SigningIn;
                break;
            case LoggingIntoXbox:
                this.loadingText = XLEApplication.Resources.getString(R.string.login_status_connecting_to_xbox_live);
                this.loginAnimationState = LoginAnimationState.SigningIn;
                break;
            case LoggedIntoXbox:
                XLELog.Warning("XboxAuthActivityViewModel", "logged into xbox, register model ");
                LoginModel.getInstance().removeObserver(this);
                MeProfileModel.getModel().addObserver(this);
                MeProfileModel.getModel().load(true);
                this.loadingText = XLEApplication.Resources.getString(R.string.login_status_retrieving_xbox_live_data);
                this.loginAnimationState = LoginAnimationState.SigningIn;
                break;
            case LoginError:
                onLoginErrorAtEnd = true;
                onLoginErrorAtEndString = String.format("login_failure %s", new Object[]{LoginModel.getInstance().getLoginState()});
                this.loginAnimationState = LoginAnimationState.NotStarted;
                break;
            case ExternalAccountTroubleshootRequired:
                onTroubleshootRequired();
                this.loginAnimationState = LoginAnimationState.NotStarted;
                break;
            case ExternalAccountCreation:
                launchBrowserToAccountCreation();
                this.loginAnimationState = LoginAnimationState.NotStarted;
                break;
            case MeProfileData:
            case GamerContext:
            case CombinedContentRating:
                if (asyncResult.getException() != null) {
                    onLoginErrorAtEnd = true;
                    onLoginErrorAtEndString = "failed to get profile";
                    this.loginAnimationState = LoginAnimationState.NotStarted;
                    break;
                }
                break;
        }
        this.adapter.updateView();
        if (onLoginErrorAtEnd) {
            onLoginError(onLoginErrorAtEndString);
        }
    }

    protected void onUpdateFinished() {
        if (checkErrorCode(UpdateType.MeProfileData, XLEErrorCode.FAILED_TO_GET_ME_PROFILE) || checkErrorCode(UpdateType.GamerContext, XLEErrorCode.FAILED_TO_GET_GAMER_CONTEXT) || checkErrorCode(UpdateType.CombinedContentRating, XLEErrorCode.FAILED_TO_GET_COMBINED_CONTENT_RATING) || JavaUtil.isNullOrEmpty(MeProfileModel.getModel().getGamertag()) || JavaUtil.isNullOrEmpty(MeProfileModel.getModel().getLegalLocale()) || !MeProfileModel.getModel().getInitializeComplete()) {
            onLoginError("Failed to get MeProfile");
            return;
        }
        MessageModel.getInstance().loadMessageList(false);
        NowPlayingGlobalModel.getInstance().onResume();
        ApplicationBarManager.getInstance().onResume();
        NowPlayingGlobalModel.getInstance().load(false);
        XboxMobileOmnitureTracking.TrackSignIn(MeProfileModel.getModel().getMembershipLevel(), XboxLiveEnvironment.Instance().getDeviceModelName(), XboxLiveEnvironment.Instance().getOsVersion(), Integer.toString(XboxApplication.getVersionCode()));
        this.onSignedInAnimationCompleted = new Runnable() {
            public void run() {
                if (XboxAuthActivityViewModel.this.checkVersion()) {
                    XboxAuthActivityViewModel.this.gotoNextPage();
                }
            }
        };
        this.loadingText = XLEApplication.Resources.getString(R.string.login_status_signed_in);
        this.loginAnimationState = LoginAnimationState.SignedIn;
        this.adapter.updateView();
    }

    public void onSignedInAnimationComplete() {
        if (this.onSignedInAnimationCompleted != null) {
            this.onSignedInAnimationCompleted.run();
        }
    }

    private void onLoginError(String errorText) {
        if (errorText != null && errorText.length() > 0) {
            XboxMobileOmnitureTracking.TrackError(errorText);
            XLELog.Error("XboxAuthActivityViewModel", errorText);
        }
        setShowLoginError(true);
        cancelLogin(false);
    }

    private void onTroubleshootRequired() {
        Runnable okHandler = new Runnable() {
            public void run() {
                XboxAuthActivityViewModel.this.onLoginError("Account troubleshoot required.");
            }
        };
        showMustActDialog(XLEApplication.Resources.getString(R.string.dialog_attention_title), XLEApplication.Resources.getString(R.string.login_troubleshoot_required), XLEApplication.Resources.getString(R.string.OK), okHandler, false);
    }

    private void launchBrowserToAccountCreation() {
        XLELog.Diagnostic("XboxAuthActivityViewModel", "Launching browser for account creation");
        Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(XboxLiveEnvironment.Instance().getAccountCreationUrl()));
        browserIntent.addFlags(AvatarEditorModel.AVATAREDIT_OPTION_COLOR_FACIAL_FEATURE);
        XboxApplication.Instance.startActivity(browserIntent);
    }

    public void onBackButtonPressed() {
        if (LoginModel.getInstance().getIsWebViewVisible()) {
            cancelLogin(true);
        } else {
            super.onBackButtonPressed();
        }
    }

    private void forceUpdate() {
        showOkCancelDialog(XLEApplication.Resources.getString(R.string.update_required), XLEApplication.Resources.getString(R.string.update_available_getitnow), new Runnable() {
            public void run() {
                XboxAuthActivityViewModel.this.launchMarketplace();
            }
        }, XLEApplication.Resources.getString(R.string.Exit), new Runnable() {
            public void run() {
                XboxAuthActivityViewModel.this.goBack();
            }
        });
    }

    private boolean checkVersion() {
        if (XLEGlobalData.getInstance().getIsVersionChecked()) {
            XLELog.Info("VersionCheck", "already checked, let it pass");
            return true;
        } else if (VersionModel.getInstance().getMustUpdate(XboxApplication.getVersionCode())) {
            XLELog.Diagnostic("VersionCheck", "must update");
            forceUpdate();
            return false;
        } else if (VersionModel.getInstance().getHasUpdate(XboxApplication.getVersionCode())) {
            XLELog.Diagnostic("VersionCheck", "available optional update");
            showOkCancelDialog(XLEApplication.Resources.getString(R.string.update_available), XLEApplication.Resources.getString(R.string.update_available_getitnow), new Runnable() {
                public void run() {
                    XboxAuthActivityViewModel.this.launchMarketplace();
                }
            }, XLEApplication.Resources.getString(R.string.update_available_remindme), new Runnable() {
                public void run() {
                    XLEGlobalData.getInstance().setVersionChecked(true);
                    XboxAuthActivityViewModel.this.gotoNextPage();
                }
            });
            return false;
        } else {
            XLELog.Diagnostic("VersionCheck", "up to date");
            XLEGlobalData.getInstance().setVersionChecked(true);
            return true;
        }
    }

    private void gotoNextPage() {
        XLEGlobalData.getInstance().setLoggedIn(true);
        if (XboxApplication.getVersionCode() != ApplicationSettingManager.getInstance().getShowWhatsNewLastVersionCode()) {
            NavigateTo(WhatsNewActivity.class, false);
            return;
        }
        XLEGlobalData.getInstance().setActivePivotPane(MainPivotActivity.class, NowPlayingActivity.class);
        NavigateTo(MainPivotActivity.class, false);
        AutoConnectAndLaunchViewModel.getInstance().autoConnectAndLaunch();
    }

    private void launchMarketplace() {
        XLELog.Diagnostic("LoginActivity", "go to marketplace for update " + VersionModel.getInstance().getMarketUrl());
        Intent gotoMarket = new Intent("android.intent.action.VIEW", Uri.parse(VersionModel.getInstance().getMarketUrl()));
        gotoMarket.addFlags(AvatarEditorModel.AVATAREDIT_OPTION_COLOR_FACIAL_FEATURE);
        XLEApplication.Instance.startActivity(gotoMarket);
    }

    private void setShowLoginError(boolean newValue) {
        XLEGlobalData.getInstance().setShowLoginError(newValue);
        this.showLoginError = newValue;
    }
}
