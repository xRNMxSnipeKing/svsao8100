package com.microsoft.xbox.xle.viewmodel;

import android.os.PowerManager;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.authenticate.LoginModel;
import com.microsoft.xbox.service.model.ActivitySummaryModel;
import com.microsoft.xbox.service.model.ActivityUtil;
import com.microsoft.xbox.service.model.ConsolePresenceModel;
import com.microsoft.xbox.service.model.MediaTitleState;
import com.microsoft.xbox.service.model.NowPlayingGlobalModel;
import com.microsoft.xbox.service.model.NowPlayingGlobalModel.NowPlayingState;
import com.microsoft.xbox.service.model.SessionModel;
import com.microsoft.xbox.service.model.UpdateData;
import com.microsoft.xbox.service.model.UpdateType;
import com.microsoft.xbox.service.model.edsv2.EDSV2ActivityItem;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaItem;
import com.microsoft.xbox.service.model.edsv2.EDSV2NowPlayingDetailModel;
import com.microsoft.xbox.toolkit.ApplicationSettingManager;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.DialogManager;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEConstants;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.XLEObserver;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.toolkit.ui.CancellableBlockingScreen;
import com.microsoft.xbox.toolkit.ui.NavigationManager;
import com.microsoft.xbox.xle.app.DeviceCapabilities;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.app.XboxMobileOmnitureTracking;
import com.microsoft.xbox.xle.app.activity.CanvasWebViewActivity;
import com.microsoft.xbox.xle.app.activity.SmartGlassActivity;
import com.microsoft.xbox.xle.app.activity.XboxConsoleHelpActivity;
import com.microsoft.xbox.xle.test.automator.Automator;

public class AutoConnectAndLaunchViewModel implements XLEObserver<UpdateData> {
    private static final int AUTO_RETRY_COUNT = 3;
    private static final String CONNECTED = XLEApplication.Resources.getString(R.string.connected_to_xbox);
    private static final String CONNECTING = XLEApplication.Resources.getString(R.string.connecting_to_xbox_blocking);
    private static final String FAILED = XLEApplication.Resources.getString(R.string.failed_to_connect_to_xbox);
    private static final long MS_CANCEL_EXPIRATION_PERIOD = 20000;
    private static final String WAIT = XLEApplication.Resources.getString(R.string.loading);
    private static AutoConnectAndLaunchViewModel instance = new AutoConnectAndLaunchViewModel();
    private AutoLaunchType activityState = AutoLaunchType.Unknown;
    private EDSV2ActivityItem activityToLaunch;
    private boolean appActivityFinal;
    private ActivitySummaryModel appActivityModel;
    private EDSV2NowPlayingDetailModel appModel;
    private long currentTitleId = 0;
    private CancellableBlockingScreen dialog;
    private boolean doNotRelaunchXboxMusicActivity = false;
    private boolean isConnected;
    private boolean isConnectedCase;
    private boolean isManaullyStarted;
    private boolean isPaused;
    private boolean isRetryMode;
    private boolean isStarted;
    private EDSV2ActivityItem lastCancelledActivityData;
    private long lastCancelledTimeMilli;
    private boolean mediaActivityFinal;
    private ActivitySummaryModel mediaActivityModel;
    private EDSV2NowPlayingDetailModel mediaModel;
    private int retryCount;
    private boolean shouldAutoLaunch;
    private String statusText;

    private enum AutoLaunchType {
        Unknown,
        None,
        Controller,
        Activity,
        ShowError,
        Retry
    }

    private AutoConnectAndLaunchViewModel() {
    }

    public static AutoConnectAndLaunchViewModel getInstance() {
        return instance;
    }

    public void setDoNoRelaunchXboxMusicActivity(boolean newValue) {
        XLELog.Diagnostic("AutoConnectAndLaunch", "do not relaunch music activity set to " + newValue);
        this.doNotRelaunchXboxMusicActivity = newValue;
    }

    public void setCancelledActivityData(EDSV2ActivityItem data) {
        XLELog.Diagnostic("AutoConnectAndLaunch", "set cancelled activity");
        this.lastCancelledActivityData = data;
        this.lastCancelledTimeMilli = SystemClock.uptimeMillis();
    }

    public boolean getIsBlocking() {
        return this.dialog != null && this.dialog.isShowing();
    }

    public void Init() {
        this.isPaused = false;
    }

    public void onPause() {
        XLELog.Diagnostic("AutoConnectAndLaunch", "onPause is called");
        XLEGlobalData.getInstance().setIsAutoLaunch(false);
        this.doNotRelaunchXboxMusicActivity = false;
        this.isPaused = true;
        dismiss();
    }

    public void onResume() {
        XLELog.Diagnostic("AutoConnectAndLaunch", "onResume is called");
        this.lastCancelledActivityData = null;
        this.lastCancelledTimeMilli = 0;
        if (((PowerManager) XboxApplication.MainActivity.getSystemService("power")).isScreenOn()) {
            XLELog.Diagnostic("AutoConnectAndLaunch", "screen is on, let's continue");
            LoginModel.getInstance().addObserver(this);
            LoginModel.getInstance().refreshAccessTokenIfNecessary();
            SessionModel.getInstance().falseStart();
            return;
        }
        XLELog.Warning("AutoConnectAndLaunch", "app started when phone is off, don't auto launch");
    }

    public void autoConnectAndLaunch() {
        XLELog.Diagnostic("AutoConnectAndLaunch", "start called ");
        if (this.isStarted) {
            XLELog.Error("AutoConnectAndLaunch", "Already start, should not happen");
            return;
        }
        this.isStarted = true;
        this.isConnectedCase = false;
        ConsolePresenceModel.getInstance().addObserver(this);
        ConsolePresenceModel.getInstance().loadConsolePresence();
        XboxMobileOmnitureTracking.TrackConsoleConnectAttempt("Auto", "Resume");
        start(false, false);
        if (!this.shouldAutoLaunch) {
            XLELog.Diagnostic("AutoConnectAndLaunch", "should not auto launch, do nothing");
            dismiss();
        }
    }

    public void autoLaunch(long titleId, String mediaAssetId) {
        XLELog.Diagnostic("AutoConnectAndLaunch", "start with known titleId and mediaAssetId");
        if (!this.isStarted || this.isConnected) {
            if (this.isStarted) {
                boolean z;
                XLELog.Error("AutoConnectAndLaunch", "Already start, stop first");
                if (this.isRetryMode) {
                    z = false;
                } else {
                    z = true;
                }
                XLEAssert.assertTrue(z);
                if (this.isManaullyStarted) {
                    z = false;
                } else {
                    z = true;
                }
                XLEAssert.assertTrue(z);
                if (this.isRetryMode || this.isManaullyStarted) {
                    XLELog.Diagnostic("AutoConnectAndLaunch", "retry mode or manually started, don't auto launch");
                    return;
                }
                dismiss();
            }
            this.isStarted = true;
            this.isConnected = true;
            this.isConnectedCase = true;
            start(false, false);
            if (this.shouldAutoLaunch) {
                loadModels(titleId, mediaAssetId);
                return;
            }
            XLELog.Diagnostic("AutoConnectAndLaunch", "should not auto launch, do nothing");
            dismiss();
            return;
        }
        XLELog.Warning("AutoConnectAndLaunch", "Not connected, should not call auto launch with titleid and mediaId");
        XLEAssert.assertTrue(this.isConnected);
    }

    public void autoRetryConnect() {
        XLELog.Diagnostic("AutoConnectAndLaunch", "autoRetryConnect called");
        if (this.isStarted) {
            XLELog.Error("AutoConnectAndLaunch", "Already start, auto retry should not happen");
            return;
        }
        this.isStarted = true;
        SessionModel.getInstance().setRetryConnectingStatus(true);
        XboxMobileOmnitureTracking.TrackConsoleConnectAttempt("Auto", "Retry");
        autoRetryInternal(3);
    }

    public void manualConnectAndLaunch() {
        XLELog.Diagnostic("AutoConnectAndLaunch", "manual start called");
        this.isStarted = true;
        this.isConnectedCase = false;
        showBlocking();
        start(true, false);
    }

    private void autoRetryInternal(int retryCount) {
        this.retryCount = retryCount;
        XLELog.Diagnostic("AutoConnectAndLaunch", "retry number " + this.retryCount);
        start(false, true);
    }

    private void start(boolean manualStarted, boolean retryMode) {
        if (this.isPaused) {
            XLELog.Diagnostic("AutoConnectAndLaunch", "tombstoned already, dismiss");
            dismiss();
            return;
        }
        XLELog.Diagnostic("AutoConnectAndLaunch", "started");
        this.isManaullyStarted = manualStarted;
        this.isRetryMode = retryMode;
        resetData();
        int sessionState = SessionModel.getInstance().getDisplayedSessionState();
        if (sessionState == 0 || sessionState == 3) {
            this.isConnected = false;
            this.statusText = CONNECTING;
        } else if (sessionState == 1) {
            this.statusText = CONNECTING;
            this.isConnected = false;
        } else {
            this.isConnected = true;
            this.statusText = WAIT;
            XLELog.Warning("AutoConnectAndLaunch", "Session already connected or connecting");
        }
        if (!canAutoLaunch()) {
            this.shouldAutoLaunch = false;
            this.appActivityFinal = true;
            this.mediaActivityFinal = true;
        } else if (this.isRetryMode) {
            this.shouldAutoLaunch = false;
            this.appActivityFinal = true;
            this.mediaActivityFinal = true;
        } else if (Automator.getInstance().getTestAllowsAutoConnect(true)) {
            this.shouldAutoLaunch = true;
        } else {
            this.shouldAutoLaunch = false;
            this.appActivityFinal = true;
            this.mediaActivityFinal = true;
        }
        NowPlayingGlobalModel.getInstance().addObserver(this);
        if (!this.isConnected) {
            SessionModel.getInstance().connectToConsole();
        }
    }

    public void dismiss() {
        if (this.dialog != null) {
            XLELog.Diagnostic("AutoConnectAndRetry", "dialog dismissed");
            this.dialog.dismiss();
            this.dialog = null;
        }
        this.isStarted = false;
        this.isManaullyStarted = false;
        if (this.isRetryMode) {
            SessionModel.getInstance().setRetryConnectingStatus(false);
        }
        this.isRetryMode = false;
        this.shouldAutoLaunch = false;
        this.isConnectedCase = false;
        resetData();
        ConsolePresenceModel.getInstance().removeObserver(this);
    }

    public void update(AsyncResult<UpdateData> asyncResult) {
        UpdateType type = ((UpdateData) asyncResult.getResult()).getUpdateType();
        XLELog.Diagnostic("AutoConnectAndLaunch", "Received update: " + type.toString());
        switch (type) {
            case NowPlayingState:
                updateSessionState();
                break;
            case NowPlayingDetail:
                if (this.shouldAutoLaunch && !this.isConnectedCase) {
                    loadModelsFromNowPlayingState();
                    break;
                }
            case MediaItemDetail:
                updateActivity(asyncResult);
                break;
            case ActivitiesSummary:
                updateHeroActivity(asyncResult);
                break;
            case ConsolePresence:
                updateConsolePresence(asyncResult);
                break;
            case AccessTokenRefreshComplete:
                LoginModel.getInstance().removeObserver(this);
                ThreadManager.UIThreadPost(new Runnable() {
                    public void run() {
                        if (!AutoConnectAndLaunchViewModel.this.isPaused) {
                            AutoConnectAndLaunchViewModel.getInstance().autoConnectAndLaunch();
                        }
                    }
                });
                return;
            case NowPlayingQuickplay:
            case NowPlayingRelated:
            case MediaItemDetailRelated:
                XLELog.Diagnostic("AutoConnectAndLaunch", "not expected update type, return");
                return;
        }
        XLELog.Diagnostic("AutoConnectAndLaunch", "old activity state: " + this.activityState);
        if (getIsActivityInfoReady() && this.activityState == AutoLaunchType.Unknown) {
            if (this.shouldAutoLaunch) {
                this.activityToLaunch = getActivityToLaunch();
                if (this.activityToLaunch != null) {
                    this.activityState = AutoLaunchType.Activity;
                } else if (this.appModel == null || !this.appModel.isGameType()) {
                    this.activityState = AutoLaunchType.Controller;
                    XLELog.Diagnostic("AutoConnectAndLaunch", "set to connect to controller");
                } else {
                    XLELog.Diagnostic("AutoConnectAndLaunch", "set to NONE because it is game and no hero");
                    this.activityState = AutoLaunchType.None;
                }
            } else if (!(this.isManaullyStarted || this.isRetryMode) || this.isConnected) {
                XLELog.Diagnostic("AutoConnectAndLaunch", "set to NONE because should not auto launch");
                this.activityState = AutoLaunchType.None;
            }
        }
        XLELog.Diagnostic("AutoConnectAndLaunch", "new activity state: " + this.activityState);
        if (this.isStarted) {
            switch (this.activityState) {
                case Unknown:
                    XLELog.Diagnostic("AutoConnectAndLaunch", "still not determined");
                    this.statusText = WAIT;
                    return;
                case None:
                    XLELog.Diagnostic("AutoConnectAndLaunch", "should not do anything");
                    dismiss();
                    return;
                case Controller:
                    XLELog.Diagnostic("AutoConnectAndLaunch", "should bring up controller");
                    launchController();
                    return;
                case Activity:
                    XLELog.Diagnostic("AutoConnectAndLaunch", "default activity exist, try to launch default activity");
                    launchActivity(this.activityToLaunch);
                    return;
                case ShowError:
                    showError();
                    return;
                case Retry:
                    this.retryCount--;
                    if (this.retryCount > 0) {
                        autoRetryInternal(this.retryCount);
                        return;
                    }
                    SessionModel.getInstance().setRetryFailed();
                    XLELog.Diagnostic("AutoConnectAndLaunch", "max retry, fail and don't do anything");
                    dismiss();
                    return;
                default:
                    return;
            }
        }
        XLELog.Diagnostic("AutoConnectAndLaunch", "dismissed already, ignore");
    }

    private EDSV2ActivityItem getActivityToLaunch() {
        long titleId;
        XLEAssert.assertTrue(getIsActivityInfoReady());
        int mediaType = this.mediaModel != null ? this.mediaModel.getMediaType() : -1;
        if (this.appModel == null) {
            titleId = 0;
        } else {
            titleId = this.appModel.getTitleId();
        }
        if (titleId > 0) {
            EDSV2ActivityItem activity = ActivityUtil.getDefaultActivity(this.appActivityModel, this.mediaActivityModel, mediaType, titleId);
            if (activity != null && DeviceCapabilities.getInstance().checkDeviceRequirements(activity.getActivityLaunchInfo().getRequiresCapabilities()) && activity.canAutoLaunch()) {
                return activity;
            }
        }
        return null;
    }

    private static boolean isValidTitleIdForActivity(long titleId) {
        return (titleId <= 0 || titleId == XLEConstants.DASH_TITLE_ID || titleId == XLEConstants.BROWSER_TITLE_ID || titleId == XLEConstants.AVATAR_EDITOR_TITLE_ID) ? false : true;
    }

    private static boolean isValidId(String id) {
        return !JavaUtil.isNullOrEmpty(id);
    }

    private void updateHeroActivity(AsyncResult<UpdateData> asyncResult) {
        ActivitySummaryModel caller = (ActivitySummaryModel) asyncResult.getSender();
        XLEAssert.assertNotNull(caller);
        if (!(this.appActivityModel != caller || this.appActivityModel == null || this.appActivityModel.getIsLoading())) {
            this.appActivityModel.removeObserver(this);
            this.appActivityFinal = true;
            XLELog.Diagnostic("AutoConnectAndLaunch", "app hero activity received");
        }
        if (this.mediaActivityModel == caller && this.mediaActivityModel != null && !this.mediaActivityModel.getIsLoading() && this.appActivityModel != null && !this.appActivityModel.getIsLoading()) {
            this.mediaActivityModel.removeObserver(this);
            this.mediaActivityFinal = true;
            XLELog.Diagnostic("AutoConnectAndLaunch", "media hero activity received");
        }
    }

    private boolean getIsActivityInfoReady() {
        return this.appActivityFinal && this.mediaActivityFinal;
    }

    private void updateConsolePresence(AsyncResult<UpdateData> asyncResult) {
        if (asyncResult.getException() != null || asyncResult.getResult() == null) {
            XLELog.Warning("AutoConnectAndLaunch", "console presence not valid, wait for now playing data");
        } else if (!((UpdateData) asyncResult.getResult()).getIsFinal()) {
            XLELog.Diagnostic("AutoConnectAndLaunch", "Load console presence not final, wait");
        } else if (ConsolePresenceModel.getInstance().getIsConsoleOnline()) {
            loadModels(ConsolePresenceModel.getInstance().getTitleId(), ConsolePresenceModel.getInstance().getMediaId());
        } else {
            XLELog.Diagnostic("AutoConnectAndLaunch", "console is offline, just stop");
            this.appActivityFinal = true;
            this.mediaActivityFinal = true;
            this.activityState = AutoLaunchType.None;
        }
    }

    private void updateActivity(AsyncResult<UpdateData> asyncResult) {
        Object caller = asyncResult.getSender();
        XLEAssert.assertNotNull(caller);
        if (this.appModel != null && this.appModel.equals(caller) && ((UpdateData) asyncResult.getResult()).getIsFinal()) {
            XLELog.Diagnostic("AutoConnectAndLaunch", "appModel loaded");
            this.appModel.removeObserver(this);
            if (this.appModel.getCanonicalId() == null || this.appActivityModel != null) {
                this.appActivityFinal = true;
                this.mediaActivityFinal = true;
                this.appActivityModel = null;
                this.activityState = AutoLaunchType.None;
                XLELog.Diagnostic("AutoConnectAndLaunch", "Service call error, set app activity final. ");
            } else {
                XLELog.Diagnostic("AutoConnectAndLaunch", "Running valid title and app canonical id is valid. load activity ");
                this.appActivityModel = ActivitySummaryModel.getModel(this.appModel.getMediaItemDetailData());
                ThreadManager.UIThreadPost(new Runnable() {
                    public void run() {
                        if (AutoConnectAndLaunchViewModel.this.appActivityModel != null) {
                            AutoConnectAndLaunchViewModel.this.appActivityModel.addObserver(AutoConnectAndLaunchViewModel.getInstance());
                            AutoConnectAndLaunchViewModel.this.appActivityModel.load(false);
                        }
                    }
                });
            }
        }
        if (this.mediaModel != null && this.mediaModel.equals(caller) && ((UpdateData) asyncResult.getResult()).getIsFinal()) {
            XLELog.Diagnostic("AutoConnectAndLaunch", "mediaModel loaded");
            this.mediaModel.removeObserver(this);
            if (this.mediaModel.getCanonicalId() != null && this.mediaActivityModel == null && ActivityUtil.isValidMediaTypeForActivity(this.mediaModel.getMediaType())) {
                EDSV2MediaItem item = this.mediaModel.getMediaItemDetailData();
                NowPlayingGlobalModel.getInstance().addNowPlayingTitleAsProviderIfNecessary(this.currentTitleId, item);
                this.mediaActivityModel = ActivitySummaryModel.getModel(item);
                ThreadManager.UIThreadPost(new Runnable() {
                    public void run() {
                        if (AutoConnectAndLaunchViewModel.this.mediaActivityModel != null) {
                            AutoConnectAndLaunchViewModel.this.mediaActivityModel.addObserver(AutoConnectAndLaunchViewModel.getInstance());
                            AutoConnectAndLaunchViewModel.this.mediaActivityModel.load(false);
                        }
                    }
                });
                return;
            }
            this.mediaActivityFinal = true;
            this.mediaActivityModel = null;
        }
    }

    private void loadModelsFromNowPlayingState() {
        XLELog.Diagnostic("AutoConnectAndLaunch", "update models from nowplaying state");
        NowPlayingState state = NowPlayingGlobalModel.getInstance().getNowPlayingState();
        if (state == NowPlayingState.Connecting || state == NowPlayingState.Disconnected) {
            XLELog.Diagnostic("AutoConnectAndLaunch", "not connected, ignore this loadmodels call");
            return;
        }
        long newTitleId = NowPlayingGlobalModel.getInstance().getCurrentTitleId();
        MediaTitleState mediaState = NowPlayingGlobalModel.getInstance().getCurrentMediaState();
        if (newTitleId == 0) {
            XLELog.Diagnostic("AutoConnectAndLaunch", "titleId not valid yet, just wait for next call");
        } else if (this.appModel == null && this.mediaModel == null) {
            XLELog.Diagnostic("AutoConnectAndLaunch", "need to load models");
            loadModels(newTitleId, mediaState == null ? null : mediaState.getMediaAssetId());
        } else {
            XLELog.Diagnostic("AutoConnectAndLaunch", "models already exist, ignore this call");
        }
    }

    private void loadModels(long titleId, String mediaAssetId) {
        boolean z = true;
        boolean shouldLoadAppModel = false;
        boolean shouldLoadMediaModel = false;
        XLELog.Diagnostic("AutoConnectAndLaunch", "load model for " + titleId);
        this.currentTitleId = titleId;
        XLELog.Diagnostic("AutoConnectAndLaunch", "load media asset for " + mediaAssetId);
        if (this.appModel == null && !this.appActivityFinal) {
            if (isValidTitleIdForActivity(titleId)) {
                this.appModel = EDSV2NowPlayingDetailModel.getModel(titleId, null);
                shouldLoadAppModel = true;
            } else {
                this.appActivityFinal = true;
                this.mediaActivityFinal = true;
                XLELog.Diagnostic("AutoConnectAndLaunch", "appModel not valid, set no hero activity for app and media");
            }
        }
        if (this.mediaModel == null && !this.mediaActivityFinal) {
            if (isValidTitleIdForActivity(titleId) && isValidId(mediaAssetId)) {
                this.mediaModel = EDSV2NowPlayingDetailModel.getModel(titleId, mediaAssetId);
                shouldLoadMediaModel = true;
            } else {
                XLELog.Diagnostic("AutoConnectAndLaunch", "mediaModel not valid, set no hero activity for media");
                this.mediaActivityFinal = true;
            }
        }
        if (shouldLoadMediaModel) {
            ThreadManager.UIThreadPost(new Runnable() {
                public void run() {
                    if (AutoConnectAndLaunchViewModel.this.mediaModel != null) {
                        AutoConnectAndLaunchViewModel.this.mediaModel.addObserver(AutoConnectAndLaunchViewModel.getInstance());
                        AutoConnectAndLaunchViewModel.this.mediaModel.load(false);
                    }
                }
            });
        }
        if (shouldLoadAppModel) {
            ThreadManager.UIThreadPost(new Runnable() {
                public void run() {
                    if (AutoConnectAndLaunchViewModel.this.appModel != null) {
                        AutoConnectAndLaunchViewModel.this.appModel.addObserver(AutoConnectAndLaunchViewModel.getInstance());
                        AutoConnectAndLaunchViewModel.this.appModel.load(false);
                    }
                }
            });
        }
        if (this.shouldAutoLaunch && this.appActivityFinal && this.mediaActivityFinal) {
            XLELog.Diagnostic("AutoConnectAndLaunch", "based on title and media state, use controller");
            XLEAssert.assertTrue(this.appActivityModel == null);
            if (this.mediaActivityModel != null) {
                z = false;
            }
            XLEAssert.assertTrue(z);
            this.activityState = AutoLaunchType.Controller;
            launchController();
        }
    }

    private void updateSessionState() {
        int newSessionState = SessionModel.getInstance().getDisplayedSessionState();
        XLELog.Diagnostic("AutoConnectAndLaunch", "SessionState update   " + newSessionState);
        switch (newSessionState) {
            case 0:
            case 3:
                this.isConnected = false;
                if (this.isManaullyStarted) {
                    this.activityState = AutoLaunchType.ShowError;
                    this.statusText = FAILED;
                    return;
                } else if (this.isRetryMode) {
                    this.activityState = AutoLaunchType.Retry;
                    this.statusText = WAIT;
                    return;
                } else {
                    XLELog.Diagnostic("AutoConnectAndLaunch", "set to NONE disconnected");
                    this.activityState = AutoLaunchType.None;
                    return;
                }
            case 1:
                this.isConnected = false;
                this.statusText = CONNECTING;
                return;
            default:
                this.isConnected = true;
                this.statusText = CONNECTED;
                if (this.isManaullyStarted || this.isRetryMode) {
                    XLELog.Diagnostic("AutoConnectAndLaunch", "set to NONE for retry and manually connected");
                    this.activityState = AutoLaunchType.None;
                    return;
                }
                return;
        }
    }

    private void launchActivity(final EDSV2ActivityItem activityToLaunch) {
        if (this.doNotRelaunchXboxMusicActivity && activityToLaunch.isXboxMusicActivity()) {
            XLELog.Diagnostic("AutoConnectAndLaunch", "User dismissed xbox music activity already. Don't relaunch it.");
            dismiss();
            return;
        }
        EDSV2ActivityItem lastCancelledActivity = getLastCancelledActivityData();
        if (lastCancelledActivity == null || !JavaUtil.stringsEqualCaseInsensitive(lastCancelledActivity.getCanonicalId(), activityToLaunch.getCanonicalId())) {
            if (activityToLaunch.isXboxMusicActivity()) {
                XLELog.Diagnostic("AutoConnectAndLaunch", "Auto launching music activity. We're not going to auto launch this again until title changes or user launching a content.");
                getInstance().setDoNoRelaunchXboxMusicActivity(true);
            }
            final EDSV2MediaItem parentMediaItem = this.mediaModel != null ? this.mediaModel.getMediaItemDetailData() : this.appModel.getMediaItemDetailData();
            XLELog.Diagnostic("AutoConnectAndLaunch", "launch activity after the wait. ");
            ThreadManager.UIThreadPost(new Runnable() {
                public void run() {
                    XLELog.Diagnostic("AutoConnectAndLaunch", "launch activity");
                    if (NavigationManager.getInstance().getCurrentActivity().getClass() == CanvasWebViewActivity.class && XLEGlobalData.getInstance().getSelectedActivityData() != null && XLEGlobalData.getInstance().getSelectedActivityData().equals(activityToLaunch)) {
                        if (activityToLaunch.isXboxMusicActivity()) {
                            XLELog.Diagnostic("AutoConnectAndLaunch", "Music activity already launched. Ignore.");
                            AutoConnectAndLaunchViewModel.this.dismiss();
                            return;
                        } else if (XLEGlobalData.getInstance().getActivityParentMediaItemData() != null && XLEGlobalData.getInstance().getActivityParentMediaItemData().equals(parentMediaItem)) {
                            XLELog.Diagnostic("AutoConnectAndLaunch", "Parent media is the same. Ignore.");
                            AutoConnectAndLaunchViewModel.this.dismiss();
                            return;
                        }
                    }
                    boolean addToStack = NavigationManager.getInstance().getCurrentActivity().getClass() != XboxConsoleHelpActivity.class;
                    XLEGlobalData.getInstance().setSelectedActivityData(activityToLaunch);
                    XboxMobileOmnitureTracking.TrackLaunchActivity("Auto", "Auto", Integer.toString(activityToLaunch.getMediaType()), activityToLaunch.getTitle(), activityToLaunch.getCanonicalId(), "true");
                    XboxMobileOmnitureTracking.SetDetails(Integer.toString(activityToLaunch.getMediaType()), activityToLaunch.getTitle(), activityToLaunch.getCanonicalId());
                    XLEGlobalData.getInstance().setActivityParentMediaItemData(parentMediaItem);
                    XLEGlobalData.getInstance().setIsAutoLaunch(true);
                    DialogManager.getInstance().dismissAppBar();
                    NavigationManager.getInstance().NavigateTo(CanvasWebViewActivity.class, addToStack);
                    AutoConnectAndLaunchViewModel.this.dismiss();
                }
            });
            return;
        }
        XLELog.Warning("AutoConnectAndLaunch", "the activity to launch was just cancelled seconds ago by the user, don't launch it again");
        dismiss();
    }

    private boolean allowAutoLaunchControllerForTitle(long titleId) {
        return titleId == XLEConstants.BROWSER_TITLE_ID;
    }

    private void launchController() {
        if (SessionModel.getInstance().getDisplayedSessionState() != 2) {
            XLELog.Diagnostic("AutoConnectAndLaunch", "not connected yet, ignore launch controller, wait for connection");
            dismiss();
        } else if (SessionModel.getInstance().getCurrentCapability() != 3) {
            XLELog.Diagnostic("AutoConnectAndLaunch", "not on local WIFI, ignore");
            dismiss();
        } else if (allowAutoLaunchControllerForTitle(NowPlayingGlobalModel.getInstance().getCurrentTitleId())) {
            XLELog.Diagnostic("AutoConnectAndLaunch", "launch controller after the wait");
            ThreadManager.UIThreadPost(new Runnable() {
                public void run() {
                    if (NavigationManager.getInstance().getCurrentActivity().getClass() == SmartGlassActivity.class) {
                        XLELog.Diagnostic("AutoConnectAndLaunch", "DPAD on top of stack, do nothing");
                        AutoConnectAndLaunchViewModel.this.dismiss();
                        return;
                    }
                    NavigationManager.getInstance().NavigateTo(SmartGlassActivity.class, NavigationManager.getInstance().getCurrentActivity().getClass() != XboxConsoleHelpActivity.class);
                    AutoConnectAndLaunchViewModel.this.dismiss();
                }
            });
        } else {
            XLELog.Diagnostic("AutoConnectAndLaunch", "title should not launch dpad, ignore");
            dismiss();
        }
    }

    private void showBlocking() {
        if (this.dialog == null || !this.dialog.isShowing()) {
            XLELog.Diagnostic("AutoConnectAndLaunch", "start blocking spinner");
            this.dialog = new CancellableBlockingScreen(XboxApplication.MainActivity);
            this.statusText = WAIT;
            this.dialog.setCancelButtonAction(new OnClickListener() {
                public void onClick(View v) {
                    AutoConnectAndLaunchViewModel.this.safeDismiss();
                }
            });
            this.dialog.show(XboxApplication.MainActivity, this.statusText);
        }
    }

    private void safeDismiss() {
        XLELog.Diagnostic("AutoConnectAndRety", "safe dismiss");
        dismissSmartGlassActivity();
        dismiss();
        SessionModel.getInstance().load(true);
    }

    private void showError() {
        ThreadManager.UIThreadPost(new Runnable() {
            public void run() {
                AutoConnectAndLaunchViewModel.this.dismissSmartGlassActivity();
                if (SessionModel.getInstance().getLastErrorCode() == 12) {
                    AutoConnectAndLaunchViewModel.this.dismiss();
                    String title = XLEApplication.Resources.getString(R.string.failed_to_connect_to_xbox);
                    String promptText = XLEApplication.Resources.getString(R.string.failed_to_connect_max_users);
                    String okText = XLEApplication.Resources.getString(R.string.Dismiss);
                    Runnable okHandler = new Runnable() {
                        public void run() {
                            Automator.getInstance().setCurrentDialog(null, null);
                        }
                    };
                    if (!Automator.getInstance().onShowDialog(title, promptText)) {
                        DialogManager.getInstance().showFatalAlertDialog(title, promptText, okText, okHandler);
                        Automator.getInstance().setCurrentDialog(promptText, DialogManager.getInstance().getVisibleDialog());
                        return;
                    }
                    return;
                }
                if (NavigationManager.getInstance().getCurrentActivity().getClass() != XboxConsoleHelpActivity.class) {
                    NavigationManager.getInstance().NavigateTo(XboxConsoleHelpActivity.class, true);
                }
                AutoConnectAndLaunchViewModel.this.dismiss();
            }
        });
    }

    private void dismissSmartGlassActivity() {
        if (NavigationManager.getInstance().getCurrentActivity().getClass() == SmartGlassActivity.class && SessionModel.getInstance().getDisplayedSessionState() != 2) {
            try {
                NavigationManager.getInstance().GoBack();
            } catch (XLEException e) {
                XLELog.Error("AutoConnectAndLaunch", "failed to pop the dpad during cancel");
            }
        }
    }

    private boolean canAutoLaunch() {
        if (!ApplicationSettingManager.getInstance().getAutoLaunchSmartGlassStatus()) {
            return false;
        }
        if (NavigationManager.getInstance().getCurrentActivity().getClass() != XboxConsoleHelpActivity.class && !NavigationManager.getInstance().getCurrentActivity().getCanAutoLaunch()) {
            XLELog.Diagnostic("AutoConnectAndLaunch", "Current screen can not auto launch");
            XLELog.Diagnostic("AutoConnectAndLaunch", "Current screen is " + NavigationManager.getInstance().getCurrentActivity().getClass().getSimpleName());
            return false;
        } else if (NavigationManager.getInstance().getCurrentActivity().getClass() != XboxApplication.MainActivity.getStartupScreenClass()) {
            return true;
        } else {
            return false;
        }
    }

    private EDSV2ActivityItem getLastCancelledActivityData() {
        if (SystemClock.uptimeMillis() - this.lastCancelledTimeMilli > MS_CANCEL_EXPIRATION_PERIOD) {
            XLELog.Diagnostic("AutoConnectAndLaunch", "last cancelled activity expired, reset");
            this.lastCancelledActivityData = null;
            this.lastCancelledTimeMilli = 0;
        }
        return this.lastCancelledActivityData;
    }

    private void resetData() {
        if (this.appModel != null) {
            this.appModel.removeObserver(this);
            this.appModel = null;
        }
        if (this.mediaModel != null) {
            this.mediaModel.removeObserver(this);
            this.mediaModel = null;
        }
        if (this.appActivityModel != null) {
            this.appActivityModel.removeObserver(this);
            this.appActivityModel = null;
        }
        if (this.mediaActivityModel != null) {
            this.mediaActivityModel.removeObserver(this);
            this.mediaActivityModel = null;
        }
        this.activityToLaunch = null;
        this.activityState = AutoLaunchType.Unknown;
        this.appActivityFinal = false;
        this.mediaActivityFinal = false;
        this.statusText = WAIT;
        this.currentTitleId = 0;
        NowPlayingGlobalModel.getInstance().removeObserver(this);
        LoginModel.getInstance().removeObserver(this);
    }
}
