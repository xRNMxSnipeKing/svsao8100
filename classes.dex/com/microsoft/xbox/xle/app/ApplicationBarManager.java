package com.microsoft.xbox.xle.app;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.LRCControlKey;
import com.microsoft.xbox.service.model.MediaTitleState;
import com.microsoft.xbox.service.model.NowPlayingGlobalModel;
import com.microsoft.xbox.service.model.NowPlayingGlobalModel.NowPlayingState;
import com.microsoft.xbox.service.model.SessionModel;
import com.microsoft.xbox.service.model.UpdateData;
import com.microsoft.xbox.service.model.UpdateType;
import com.microsoft.xbox.service.model.eds.DetailDisplayScreenType;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaItem;
import com.microsoft.xbox.service.model.edsv2.EDSV2MusicTrackMediaItemWithAlbum;
import com.microsoft.xbox.toolkit.ApplicationSettingManager;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.BackgroundThreadWaitor;
import com.microsoft.xbox.toolkit.BackgroundThreadWaitor.WaitType;
import com.microsoft.xbox.toolkit.DialogManager;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.XLEObserver;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.toolkit.anim.MAAS;
import com.microsoft.xbox.toolkit.anim.XLEAnimation;
import com.microsoft.xbox.toolkit.ui.NavigationManager;
import com.microsoft.xbox.toolkit.ui.PageIndicator;
import com.microsoft.xbox.toolkit.ui.XLEButton;
import com.microsoft.xbox.toolkit.ui.XLEUniformImageView;
import com.microsoft.xbox.toolkit.ui.appbar.ApplicationBarView;
import com.microsoft.xbox.toolkit.ui.appbar.ExpandedAppBar;
import com.microsoft.xbox.xle.anim.XLEMAASAnimation;
import com.microsoft.xbox.xle.app.activity.CanvasWebViewActivity;
import com.microsoft.xbox.xle.app.activity.DetailsPivotActivity;
import com.microsoft.xbox.xle.app.activity.MainPivotActivity;
import com.microsoft.xbox.xle.app.activity.NowPlayingActivity;
import com.microsoft.xbox.xle.app.activity.SettingsActivity;
import com.microsoft.xbox.xle.app.activity.SmartGlassActivity;
import com.microsoft.xbox.xle.test.automator.Automator;
import com.microsoft.xbox.xle.viewmodel.AutoConnectAndLaunchViewModel;
import com.microsoft.xbox.xle.viewmodel.DetailPageHelper;
import com.microsoft.xbox.xle.viewmodel.DetailPivotPaneData;
import com.microsoft.xbox.xle.viewmodel.XLEGlobalData;
import java.net.URI;

public abstract class ApplicationBarManager implements XLEObserver<UpdateData> {
    protected static final int APP_BAR_BLOCK_TIMEOUT_MS = 5000;
    protected static final String APP_BAR_HIDE_APPBAR_ANIMATION_NAME = "AppBarHide";
    protected static final String APP_BAR_HIDE_APPBAR_BUTTON_ANIMATION_NAME = "AppBarHideButton";
    protected static final String APP_BAR_SHOW_APPBAR_BUTTON_ANIMATION_NAME = "AppBarShowButton";
    protected static final String APP_BAR_SHOW_APPBAR_FULL_ANIMATION_NAME = "AppBarShowFull";
    protected static final String TEXT_CONNECT_TO_XBOX = XLEApplication.Resources.getString(R.string.appbar_connect_to_xbox);
    protected static final String TEXT_REMOTE = XLEApplication.Resources.getString(R.string.remote);
    private AppBarAnimationState animationState;
    private String cachedMediaAssetId;
    private int cachedSessionState;
    private long cachedTitleId;
    protected ApplicationBarView collapsedAppBarView;
    protected AppBarState currentAppBarState;
    private String currentDetailIdentifier;
    private int currentNowPlayingImageDefaultRid = -1;
    private URI currentNowPlayingImageUri;
    protected boolean enableMediaTransportControls;
    private boolean enableNextPrevTransportControls;
    protected ExpandedAppBar expandedAppBar;
    protected ApplicationBarView expandedAppBarView;
    protected XLEButton[] globalIconButtons;
    protected XLEButton[] globalMenuButtons;
    private boolean isBlocking = false;
    protected boolean isExpandable;
    private boolean isPaused = true;
    protected AppBarState lastAppBarState;
    protected XLEButton[] newButtons;
    private Runnable onAnimationEndRunnable;
    private Runnable onNewButtonsAddedRunnable;
    protected XLEButton[] previousButtons;
    protected boolean shouldShowNowPlaying;
    protected boolean showMediaButton = false;

    public enum AppBarAnimationState {
        READY,
        SHOW_APPBAR_FULL,
        HIDE_APPBAR,
        SHOW_APPBAR_BUTTONS,
        SWAP_APPBAR_BUTTONS,
        HIDE_APPBAR_BUTTONS,
        DONE
    }

    public enum AppBarState {
        FULL,
        HALF,
        HIDE
    }

    public abstract void addNewCollapsedButtons(XLEButton[] xLEButtonArr);

    public abstract void addNewExpandedButtons(XLEButton[] xLEButtonArr);

    public abstract PageIndicator getPageIndicator();

    public abstract void setCurrentPage(int i);

    public abstract void setTotalPageCount(int i);

    protected abstract boolean shouldShowSwapButtonAnimation();

    protected abstract void updateRemoteButton();

    protected ApplicationBarManager() {
    }

    public static ApplicationBarManager getInstance() {
        if (XLEApplication.Instance.getIsTablet()) {
            return ApplicationBarManagerTablet.getInstance();
        }
        return ApplicationBarManagerPhone.getInstance();
    }

    public AppBarState getAppBarState() {
        return this.currentAppBarState;
    }

    public boolean getIsBlocking() {
        return this.isBlocking;
    }

    public ApplicationBarView getCollapsedAppBarView() {
        return this.collapsedAppBarView;
    }

    public ApplicationBarView getExpandedAppBarView() {
        return this.expandedAppBarView;
    }

    public void setShouldShowNowPlaying(boolean shouldShowNowPlaying) {
        int i = 0;
        if (this.shouldShowNowPlaying != shouldShowNowPlaying) {
            this.shouldShowNowPlaying = shouldShowNowPlaying;
            this.collapsedAppBarView.getNowPlayingTile().setEnabled(this.shouldShowNowPlaying);
            this.expandedAppBarView.getNowPlayingTile().setEnabled(this.shouldShowNowPlaying);
            this.collapsedAppBarView.getNowPlayingTile().setVisibility(this.shouldShowNowPlaying ? 0 : 4);
            XLEUniformImageView nowPlayingTile = this.expandedAppBarView.getNowPlayingTile();
            if (!this.shouldShowNowPlaying) {
                i = 4;
            }
            nowPlayingTile.setVisibility(i);
            this.collapsedAppBarView.getProgressBar().setEnabled(this.shouldShowNowPlaying);
            this.expandedAppBarView.getProgressBar().setEnabled(this.shouldShowNowPlaying);
            updateIsExpandable(this.shouldShowNowPlaying);
        }
    }

    public boolean getShouldShowNowPlaying() {
        return this.shouldShowNowPlaying;
    }

    public void setEnableMediaTransportControls(boolean enabled) {
        setEnableMediaTransportControls(enabled, true);
    }

    public void setEnableMediaTransportControls(boolean enabled, boolean enableNextPrev) {
        if (this.enableMediaTransportControls != enabled || this.enableNextPrevTransportControls != enableNextPrev) {
            this.enableMediaTransportControls = enabled;
            this.enableNextPrevTransportControls = enableNextPrev;
            updateMediaButtonVisibility();
        }
    }

    public void setCurrentDetailIdentifier(String id) {
        this.currentDetailIdentifier = id;
    }

    public void onCreate() {
        this.expandedAppBarView = new ApplicationBarView(XboxApplication.MainActivity);
        this.collapsedAppBarView = new ApplicationBarView(XboxApplication.MainActivity);
        this.collapsedAppBarView.setId(R.id.root_app_bar);
        this.collapsedAppBarView.setVisibility(8);
        this.expandedAppBar = new ExpandedAppBar(this.expandedAppBarView, XboxApplication.MainActivity);
        this.expandedAppBar.setOnDismissListener(new OnDismissListener() {
            public void onDismiss(DialogInterface dialog) {
                DialogManager.getInstance().onAppBarDismissed();
            }
        });
        ApplicationBarView globalAppBar = (ApplicationBarView) ((LayoutInflater) XLEApplication.MainActivity.getSystemService("layout_inflater")).inflate(R.layout.appbar_global, null);
        XLEAssert.assertNotNull(globalAppBar);
        this.globalMenuButtons = globalAppBar.getAppBarButtons();
        globalAppBar.removeAllViews();
    }

    public void onResume() {
        if (this.isPaused) {
            XLELog.Diagnostic("ApplicationBarManager", "onResume called ");
            this.collapsedAppBarView.getNowPlayingTile().setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    ApplicationBarManager.this.navigateToNowPlayingDetails();
                }
            });
            this.cachedSessionState = 0;
            this.cachedMediaAssetId = null;
            this.cachedTitleId = 0;
            NowPlayingGlobalModel.getInstance().addObserver(this);
            this.isPaused = false;
            return;
        }
        XLELog.Diagnostic("ApplicationBarManager", "the applicationbar manager is not paused, ignore onResume request");
    }

    public void onPause() {
        if (this.isPaused) {
            XLELog.Warning("ApplicationBarManager", "the applicationbar manager is paused, ignore onPause request");
            return;
        }
        if (this.previousButtons != null) {
            for (XLEButton onClickListener : this.previousButtons) {
                onClickListener.setOnClickListener(null);
            }
        }
        XLELog.Diagnostic("ApplicationBarManager", "onPause called ");
        this.collapsedAppBarView.cleanup();
        this.expandedAppBarView.cleanup();
        Automator.getInstance().cleanupListenerHooks();
        this.isExpandable = false;
        this.currentNowPlayingImageDefaultRid = -1;
        this.currentNowPlayingImageUri = null;
        NowPlayingGlobalModel.getInstance().removeObserver(this);
        this.showMediaButton = false;
        this.isPaused = true;
    }

    public void setButtonClickListener(int resId, OnClickListener listener) {
        setButtonClickListener(resId, listener, null);
    }

    public void setButtonClickListener(int resId, final OnClickListener listener, OnLongClickListener longClickListener) {
        XLEButton collapsedButton = (XLEButton) this.collapsedAppBarView.findViewById(resId);
        if (collapsedButton != null) {
            collapsedButton.setOnClickListener(listener);
            collapsedButton.setOnLongClickListener(longClickListener);
        }
        XLEButton expandedButton = (XLEButton) this.expandedAppBarView.findViewById(resId);
        if (expandedButton != null) {
            OnClickListener newListener = new OnClickListener() {
                public void onClick(View v) {
                    DialogManager.getInstance().dismissAppBar();
                    listener.onClick(v);
                }
            };
            expandedButton.setOnClickListener(newListener);
            expandedButton.setOnLongClickListener(longClickListener);
            Automator.getInstance().setListenerHook(resId, newListener);
        }
    }

    public void setButtonEnabled(int resId, boolean isEnabled) {
        View collapsedButton = this.collapsedAppBarView.findViewById(resId);
        if (collapsedButton != null) {
            collapsedButton.setEnabled(isEnabled);
        }
        View expandedButton = this.expandedAppBarView.findViewById(resId);
        if (expandedButton != null) {
            expandedButton.setEnabled(isEnabled);
        }
    }

    public void setButtonVisibility(int resId, int visibility) {
        View collapsedButton = this.collapsedAppBarView.findViewById(resId);
        if (collapsedButton != null) {
            collapsedButton.setVisibility(visibility);
        }
        View expandedButton = this.expandedAppBarView.findViewById(resId);
        if (expandedButton != null) {
            expandedButton.setVisibility(visibility);
        }
    }

    public void setButtonText(int resId, String text) {
        TextView collapsedButton = (TextView) this.collapsedAppBarView.findViewById(resId);
        if (collapsedButton != null) {
            collapsedButton.setText(text);
        }
        TextView expandedButton = (TextView) this.expandedAppBarView.findViewById(resId);
        if (expandedButton != null) {
            expandedButton.setText(text);
        }
    }

    public void hide() {
        this.collapsedAppBarView.setVisibility(8);
        NavigationManager.getInstance().getCurrentActivity().removeBottomMargin();
    }

    public void show() {
        this.collapsedAppBarView.setVisibility(0);
        NavigationManager.getInstance().getCurrentActivity().resetBottomMargin();
    }

    protected void addNewCollapsedButtonsToView() {
        this.collapsedAppBarView.getIconButtonContainer().removeAllViews();
        if (this.newButtons != null && this.newButtons.length > 0) {
            for (XLEButton button : this.newButtons) {
                this.collapsedAppBarView.addIconButton(button);
            }
        }
        updateMediaButtonVisibility();
        if (this.onNewButtonsAddedRunnable != null) {
            this.onNewButtonsAddedRunnable.run();
        }
    }

    protected void disableButtons() {
        BackgroundThreadWaitor.getInstance().setBlocking(WaitType.ApplicationBar, 5000);
        this.isBlocking = true;
        this.collapsedAppBarView.setEnabled(false);
    }

    protected void enableButtons() {
        BackgroundThreadWaitor.getInstance().clearBlocking(WaitType.ApplicationBar);
        this.isBlocking = false;
        this.collapsedAppBarView.setEnabled(true);
    }

    protected void updateIsExpandable(boolean isExpandable) {
        int i = 0;
        if (this.isExpandable != isExpandable) {
            this.isExpandable = isExpandable;
            this.collapsedAppBarView.getMenuOptionButton().setVisibility(this.isExpandable ? 0 : 8);
            ImageView menuOptionButton = this.expandedAppBarView.getMenuOptionButton();
            if (!this.isExpandable) {
                i = 8;
            }
            menuOptionButton.setVisibility(i);
            if (this.isExpandable) {
                this.collapsedAppBarView.getMenuOptionButton().setOnClickListener(new OnClickListener() {
                    public void onClick(View arg0) {
                        ApplicationBarManager.this.expandAppBar();
                    }
                });
                return;
            }
            this.collapsedAppBarView.getMenuOptionButton().setOnClickListener(null);
            this.expandedAppBarView.getMenuOptionButton().setOnClickListener(null);
        }
    }

    public void expandAppBar() {
        updateRemoteButton();
        LayoutParams params;
        if (this.shouldShowNowPlaying) {
            params = (LayoutParams) this.expandedAppBarView.getMenuButtonContainer().getLayoutParams();
            params.addRule(3, this.expandedAppBarView.getNowPlayingTile().getId());
            this.expandedAppBarView.getMenuButtonContainer().setLayoutParams(params);
        } else {
            params = (LayoutParams) this.expandedAppBarView.getMenuButtonContainer().getLayoutParams();
            params.addRule(3, this.expandedAppBarView.getIconButtonContainer().getId());
            this.expandedAppBarView.getMenuButtonContainer().setLayoutParams(params);
        }
        if (!this.isBlocking && this.isExpandable) {
            DialogManager.getInstance().showAppBarMenu(this.expandedAppBar);
        }
    }

    public void beginAnimation() {
        cleanUpAnimations();
        initializeAnimation();
    }

    private void initializeAnimation() {
        onStateChanged(AppBarAnimationState.READY);
        switch (this.currentAppBarState) {
            case FULL:
                if (this.lastAppBarState == AppBarState.FULL) {
                    onStateChanged(AppBarAnimationState.SWAP_APPBAR_BUTTONS);
                    return;
                } else {
                    onStateChanged(AppBarAnimationState.SHOW_APPBAR_FULL);
                    return;
                }
            case HIDE:
                onStateChanged(AppBarAnimationState.HIDE_APPBAR);
                return;
            default:
                return;
        }
    }

    private void onStateChanged(AppBarAnimationState state) {
        if (this.animationState != state) {
            this.animationState = state;
            XLELog.Diagnostic("ApplicationBar", "Animation state updated: " + state.toString());
            XLEAnimation animation = null;
            switch (this.animationState) {
                case SHOW_APPBAR_FULL:
                    XLELog.Diagnostic("ApplicationBar", "Playing show full app bar animation");
                    this.collapsedAppBarView.setVisibility(0);
                    this.collapsedAppBarView.getIconButtonContainer().setVisibility(4);
                    if (this.lastAppBarState == AppBarState.HIDE) {
                        this.collapsedAppBarView.getMenuOptionButton().setVisibility(4);
                    }
                    this.currentAppBarState = AppBarState.HIDE;
                    animation = ((XLEMAASAnimation) MAAS.getInstance().getAnimation(APP_BAR_SHOW_APPBAR_FULL_ANIMATION_NAME)).compile(this.collapsedAppBarView);
                    animation.setOnAnimationEnd(new Runnable() {
                        public void run() {
                            ApplicationBarManager.this.currentAppBarState = AppBarState.FULL;
                            if (ApplicationBarManager.this.isExpandable) {
                                ApplicationBarManager.this.collapsedAppBarView.getMenuOptionButton().setVisibility(0);
                            }
                            ApplicationBarManager.this.onStateChanged(AppBarAnimationState.SHOW_APPBAR_BUTTONS);
                        }
                    });
                    break;
                case HIDE_APPBAR:
                    XLELog.Diagnostic("ApplicationBar", "Playing hide app bar animation");
                    this.collapsedAppBarView.getIconButtonContainer().setVisibility(4);
                    animation = ((XLEMAASAnimation) MAAS.getInstance().getAnimation(APP_BAR_HIDE_APPBAR_ANIMATION_NAME)).compile(this.collapsedAppBarView);
                    animation.setOnAnimationEnd(new Runnable() {
                        public void run() {
                            ApplicationBarManager.this.collapsedAppBarView.getIconButtonContainer().removeAllViews();
                            ApplicationBarManager.this.collapsedAppBarView.setVisibility(8);
                            ApplicationBarManager.this.onStateChanged(AppBarAnimationState.DONE);
                        }
                    });
                    break;
                case SHOW_APPBAR_BUTTONS:
                    XLELog.Diagnostic("ApplicationBar", "Playing show app bar button animation");
                    this.collapsedAppBarView.setVisibility(0);
                    this.collapsedAppBarView.getIconButtonContainer().setVisibility(0);
                    addNewCollapsedButtonsToView();
                    Runnable afterShowAppbarButtons = new Runnable() {
                        public void run() {
                            ApplicationBarManager.this.onStateChanged(AppBarAnimationState.DONE);
                        }
                    };
                    if (countTotalVisibleIcons() != 0) {
                        animation = ((XLEMAASAnimation) MAAS.getInstance().getAnimation(APP_BAR_SHOW_APPBAR_BUTTON_ANIMATION_NAME)).compile(this.collapsedAppBarView.getIconButtonContainer());
                        animation.setOnAnimationEnd(afterShowAppbarButtons);
                        break;
                    }
                    afterShowAppbarButtons.run();
                    return;
                case SWAP_APPBAR_BUTTONS:
                    XLELog.Diagnostic("ApplicationBar", "Playing hide app bar button animation for swapping them out");
                    this.collapsedAppBarView.setVisibility(0);
                    this.collapsedAppBarView.getIconButtonContainer().setVisibility(0);
                    if (!shouldShowSwapButtonAnimation()) {
                        this.collapsedAppBarView.getIconButtonContainer().removeAllViews();
                        addNewCollapsedButtonsToView();
                        onStateChanged(AppBarAnimationState.DONE);
                        break;
                    }
                    Runnable afterSwapAppbarButtons = new Runnable() {
                        public void run() {
                            ApplicationBarManager.this.collapsedAppBarView.getIconButtonContainer().removeAllViews();
                            ApplicationBarManager.this.collapsedAppBarView.getIconButtonContainer().setVisibility(4);
                            ApplicationBarManager.this.onStateChanged(AppBarAnimationState.SHOW_APPBAR_BUTTONS);
                        }
                    };
                    if (countTotalVisibleIcons() != 0) {
                        animation = ((XLEMAASAnimation) MAAS.getInstance().getAnimation(APP_BAR_HIDE_APPBAR_BUTTON_ANIMATION_NAME)).compile(this.collapsedAppBarView.getIconButtonContainer());
                        animation.setOnAnimationEnd(afterSwapAppbarButtons);
                        break;
                    }
                    afterSwapAppbarButtons.run();
                    return;
                case HIDE_APPBAR_BUTTONS:
                    XLELog.Diagnostic("ApplicationBar", "Playing hide app bar button animation");
                    this.collapsedAppBarView.setVisibility(0);
                    animation = ((XLEMAASAnimation) MAAS.getInstance().getAnimation(APP_BAR_HIDE_APPBAR_BUTTON_ANIMATION_NAME)).compile(this.collapsedAppBarView.getIconButtonContainer());
                    animation.setOnAnimationEnd(new Runnable() {
                        public void run() {
                            ApplicationBarManager.this.collapsedAppBarView.getIconButtonContainer().removeAllViews();
                            ApplicationBarManager.this.collapsedAppBarView.getIconButtonContainer().setVisibility(4);
                            ApplicationBarManager.this.onStateChanged(AppBarAnimationState.DONE);
                        }
                    });
                    break;
                case DONE:
                    cleanUpAnimations();
                    enableButtons();
                    if (this.onAnimationEndRunnable != null) {
                        this.onAnimationEndRunnable.run();
                        break;
                    }
                    break;
            }
            Automator.getInstance().setAppbarAnimationState(this.animationState, AppBarAnimationState.DONE);
            if (animation != null) {
                disableButtons();
                animation.start();
            }
        }
    }

    private int countTotalVisibleIcons() {
        int totalVisibleIcons = 0;
        for (int i = 0; i < this.collapsedAppBarView.getIconButtonContainer().getChildCount(); i++) {
            if (this.collapsedAppBarView.getIconButtonContainer().getChildAt(i).getVisibility() == 0) {
                totalVisibleIcons++;
            }
        }
        return totalVisibleIcons;
    }

    private void cleanUpAnimations() {
        this.collapsedAppBarView.setAnimation(null);
        this.collapsedAppBarView.getIconButtonContainer().setAnimation(null);
    }

    public void setOnAnimationEndRunnable(Runnable onAnimationEnd) {
        this.onAnimationEndRunnable = onAnimationEnd;
    }

    public void setOnNewButtonsAddedRunnable(Runnable onButtonsAdded) {
        this.onNewButtonsAddedRunnable = onButtonsAdded;
    }

    public void loadAnimations() {
        MAAS.getInstance().getAnimation(APP_BAR_SHOW_APPBAR_FULL_ANIMATION_NAME);
        MAAS.getInstance().getAnimation(APP_BAR_HIDE_APPBAR_ANIMATION_NAME);
        MAAS.getInstance().getAnimation(APP_BAR_SHOW_APPBAR_BUTTON_ANIMATION_NAME);
        MAAS.getInstance().getAnimation(APP_BAR_HIDE_APPBAR_BUTTON_ANIMATION_NAME);
    }

    public void update(AsyncResult<UpdateData> asyncResult) {
        UpdateType type = ((UpdateData) asyncResult.getResult()).getUpdateType();
        boolean isFinal = ((UpdateData) asyncResult.getResult()).getIsFinal();
        XLELog.Diagnostic("ApplicationBar", "Received update: " + type.toString());
        if (isFinal) {
            switch (type) {
                case NowPlayingState:
                case NowPlayingDetail:
                    updateRemoteButton();
                    updateMediaPlayPauseVisibility();
                    updateNowPlayingTile();
                    checkForAutoLaunchAndRetry();
                    return;
                default:
                    return;
            }
        } else if (type == UpdateType.NowPlayingState) {
            updateNowPlayingTile();
        }
    }

    public void updateNowPlayingTile() {
        boolean z;
        boolean z2 = true;
        if (!(this.currentNowPlayingImageUri == NowPlayingGlobalModel.getInstance().getAppBarNowPlayingImageUri() && this.currentNowPlayingImageDefaultRid == NowPlayingGlobalModel.getInstance().getAppBarNowPlayingDefaultRid())) {
            this.currentNowPlayingImageDefaultRid = NowPlayingGlobalModel.getInstance().getAppBarNowPlayingDefaultRid();
            this.currentNowPlayingImageUri = NowPlayingGlobalModel.getInstance().getAppBarNowPlayingImageUri();
            this.collapsedAppBarView.getNowPlayingTile().setImageURI2(this.currentNowPlayingImageUri, -1, this.currentNowPlayingImageDefaultRid);
            this.expandedAppBarView.getNowPlayingTile().setImageURI2(this.currentNowPlayingImageUri, -1, this.currentNowPlayingImageDefaultRid);
        }
        ApplicationBarView applicationBarView = this.collapsedAppBarView;
        if (AutoConnectAndLaunchViewModel.getInstance().getIsBlocking() || NowPlayingGlobalModel.getInstance().getNowPlayingState() != NowPlayingState.Connecting) {
            z = false;
        } else {
            z = true;
        }
        applicationBarView.setIsLoading(z);
        ApplicationBarView applicationBarView2 = this.expandedAppBarView;
        if (AutoConnectAndLaunchViewModel.getInstance().getIsBlocking() || NowPlayingGlobalModel.getInstance().getNowPlayingState() != NowPlayingState.Connecting) {
            z2 = false;
        }
        applicationBarView2.setIsLoading(z2);
    }

    private void checkForAutoLaunchAndRetry() {
        int sessionState = SessionModel.getInstance().getDisplayedSessionState();
        if (sessionState == 0 || sessionState == 3) {
            if (this.cachedSessionState == 2) {
                XLELog.Diagnostic("AutoConnectRetry", "start auto retry because state is disconnected from connected");
                ThreadManager.UIThreadPost(new Runnable() {
                    public void run() {
                        AutoConnectAndLaunchViewModel.getInstance().autoRetryConnect();
                    }
                });
            } else if (this.cachedSessionState == 1 && !SessionModel.getInstance().getIsRetryConnecting()) {
                XLELog.Diagnostic("AutoConnectRetry", "set auto retry failed because we will not retry in this case " + this.cachedSessionState);
                ThreadManager.UIThreadPost(new Runnable() {
                    public void run() {
                        SessionModel.getInstance().setRetryFailed();
                    }
                });
            }
        } else if (sessionState == 2 && this.cachedSessionState == 1) {
            XboxMobileOmnitureTracking.TrackConsoleConnectionType(Integer.toString(SessionModel.getInstance().getCurrentCapability()), Boolean.toString(ApplicationSettingManager.getInstance().getAutoLaunchSmartGlassStatus()));
        }
        this.cachedSessionState = sessionState;
        XLELog.Diagnostic("ApplicationBarManager", "cached session state changed to " + this.cachedSessionState);
        final long newTitleId = NowPlayingGlobalModel.getInstance().getCurrentTitleId();
        MediaTitleState mediaState = NowPlayingGlobalModel.getInstance().getCurrentMediaState();
        final String newMediaAssetId = mediaState == null ? null : mediaState.getMediaAssetId();
        if (sessionState == 2 && newTitleId != 0) {
            if (!((this.cachedTitleId == 0 || this.cachedTitleId == newTitleId) && JavaUtil.stringsEqualCaseInsensitive(this.cachedMediaAssetId, newMediaAssetId))) {
                if (!(this.cachedTitleId == 0 || this.cachedTitleId == newTitleId)) {
                    AutoConnectAndLaunchViewModel.getInstance().setDoNoRelaunchXboxMusicActivity(false);
                }
                ThreadManager.UIThreadPost(new Runnable() {
                    public void run() {
                        AutoConnectAndLaunchViewModel.getInstance().autoLaunch(newTitleId, newMediaAssetId);
                    }
                });
            }
            this.cachedTitleId = newTitleId;
            this.cachedMediaAssetId = newMediaAssetId;
        }
    }

    public void setMediaButtonVisibility(boolean visible) {
        if (this.showMediaButton != visible) {
            this.showMediaButton = visible;
            updateMediaButtonVisibility();
        }
    }

    protected void updateMediaButtonVisibility() {
        boolean showButton;
        int i;
        int i2 = 0;
        if (this.enableMediaTransportControls && this.showMediaButton) {
            showButton = true;
        } else {
            showButton = false;
        }
        String str = "ApplicationBar";
        String str2 = "Updating media button visibility: %s";
        Object[] objArr = new Object[1];
        objArr[0] = showButton ? "show" : "hide";
        XLELog.Diagnostic(str, String.format(str2, objArr));
        if (showButton && this.enableNextPrevTransportControls) {
            i = 0;
        } else {
            i = 8;
        }
        setButtonVisibility(R.id.appbar_media_previous, i);
        if (showButton && NowPlayingGlobalModel.getInstance().isMediaPaused()) {
            i = 0;
        } else {
            i = 8;
        }
        setButtonVisibility(R.id.appbar_media_play, i);
        if (!showButton || NowPlayingGlobalModel.getInstance().isMediaPaused()) {
            i = 8;
        } else {
            i = 0;
        }
        setButtonVisibility(R.id.appbar_media_pause, i);
        if (!(showButton && this.enableNextPrevTransportControls)) {
            i2 = 8;
        }
        setButtonVisibility(R.id.appbar_media_next, i2);
        if (showButton) {
            setButtonClickListener(R.id.appbar_media_next, new OnClickListener() {
                public void onClick(View v) {
                    ApplicationBarManager.this.sendNextCommand();
                }
            }, new OnLongClickListener() {
                public boolean onLongClick(View v) {
                    ApplicationBarManager.this.sendFastForwardCommand();
                    return true;
                }
            });
            setButtonClickListener(R.id.appbar_media_pause, new OnClickListener() {
                public void onClick(View v) {
                    ApplicationBarManager.this.sendPauseCommand();
                }
            });
            setButtonClickListener(R.id.appbar_media_play, new OnClickListener() {
                public void onClick(View v) {
                    ApplicationBarManager.this.sendPlayCommand();
                }
            });
            setButtonClickListener(R.id.appbar_media_previous, new OnClickListener() {
                public void onClick(View v) {
                    ApplicationBarManager.this.sendPreviousCommand();
                }
            }, new OnLongClickListener() {
                public boolean onLongClick(View v) {
                    ApplicationBarManager.this.sendRewindCommand();
                    return true;
                }
            });
            return;
        }
        setButtonClickListener(R.id.appbar_media_previous, null);
        setButtonClickListener(R.id.appbar_media_pause, null);
        setButtonClickListener(R.id.appbar_media_play, null);
        setButtonClickListener(R.id.appbar_media_next, null);
    }

    private void updateMediaPlayPauseVisibility() {
        int i = 0;
        if (this.enableMediaTransportControls && this.showMediaButton) {
            int i2;
            XLELog.Diagnostic("ApplicationBar", String.format("isMediaPaused %b", new Object[]{Boolean.valueOf(NowPlayingGlobalModel.getInstance().isMediaPaused())}));
            if (NowPlayingGlobalModel.getInstance().isMediaPaused()) {
                i2 = 0;
            } else {
                i2 = 8;
            }
            setButtonVisibility(R.id.appbar_media_play, i2);
            if (NowPlayingGlobalModel.getInstance().isMediaPaused()) {
                i = 8;
            }
            setButtonVisibility(R.id.appbar_media_pause, i);
        }
    }

    public void sendPreviousCommand() {
        SessionModel.getInstance().sendControlCommand(LRCControlKey.VK_REPLAY);
    }

    public void sendNextCommand() {
        SessionModel.getInstance().sendControlCommand(LRCControlKey.VK_SKIP);
    }

    public void sendPlayCommand() {
        SessionModel.getInstance().sendControlCommand(LRCControlKey.VK_PLAY);
    }

    public void sendPauseCommand() {
        SessionModel.getInstance().sendControlCommand(LRCControlKey.VK_PAUSE);
    }

    public void sendFastForwardCommand() {
        SessionModel.getInstance().sendControlCommand(LRCControlKey.VK_FASTFWD);
    }

    public void sendRewindCommand() {
        SessionModel.getInstance().sendControlCommand(LRCControlKey.VK_REWIND);
    }

    public void navigateToNowPlayingDetails() {
        if (this.isBlocking || !this.shouldShowNowPlaying) {
            XLELog.Diagnostic("ApplicationBar", "App bar is animating or it's not showing now playing. Ignore request to navigate to details.");
            return;
        }
        NowPlayingState currentState = NowPlayingGlobalModel.getInstance().getNowPlayingState();
        if (currentState == NowPlayingState.Connecting) {
            XLELog.Diagnostic("ApplicationBar", "Session is currently connecting, navigate to home");
            XboxMobileOmnitureTracking.TrackPlayControllerClicked("Connecting");
            navigateToHome();
        } else if (currentState == NowPlayingState.Disconnected) {
            XLELog.Diagnostic("ApplicationBar", "Session is disconnected, navigate to home");
            XboxMobileOmnitureTracking.TrackPlayControllerClicked("Disconnected");
            navigateToHome();
        } else if (currentState == NowPlayingState.ConnectedPlayingDash || currentState == NowPlayingState.ConnectedPlayingDashMedia) {
            XboxMobileOmnitureTracking.TrackPlayControllerClicked("PlayingDash");
            navigateToHome();
        } else if (shouldNavigateToNowPlayingDetail()) {
            EDSV2MediaItem mediaItem = NowPlayingGlobalModel.getInstance().getNowPlayingMediaItem();
            if (mediaItem instanceof EDSV2MusicTrackMediaItemWithAlbum) {
                EDSV2MediaItem albumItem = ((EDSV2MusicTrackMediaItemWithAlbum) mediaItem).getAlbum();
                if (albumItem != null) {
                    mediaItem = albumItem;
                } else {
                    return;
                }
            }
            if ((NavigationManager.getInstance().getPreviousActivity() instanceof DetailsPivotActivity) && mediaItem.equals(XLEGlobalData.getInstance().getSelectedMediaItemData())) {
                XLELog.Diagnostic("ApplicationBar", "Previous detail screen is the now playing detail. Navigating back.");
                try {
                    NavigationManager.getInstance().GoBack();
                    return;
                } catch (XLEException e) {
                    XLELog.Error("ApplicationBar", "Failed to navigate back");
                    return;
                }
            }
            XLEGlobalData.getInstance().setDefaultScreenClass(null);
            XLEGlobalData.getInstance().setSelectedMediaItemData(mediaItem);
            XboxMobileOmnitureTracking.SetDetails(Integer.toString(mediaItem.getMediaType()), mediaItem.getTitle(), mediaItem.getCanonicalId());
            DetailDisplayScreenType screenType = DetailPageHelper.getDetailScreenTypeFromMediaType(mediaItem.getMediaType());
            DetailPivotPaneData[] data = DetailPageHelper.getDetailPivotPaneData(screenType);
            XLEGlobalData.getInstance().setDetailPivotData(data);
            if (data != null) {
                XboxMobileOmnitureTracking.TrackPlayControllerClicked("MediaBoxArt");
                NavigationManager.getInstance().NavigateTo(DetailsPivotActivity.class, !(NavigationManager.getInstance().getCurrentActivity() instanceof CanvasWebViewActivity));
                return;
            }
            XLELog.Warning("ApplicationBar", new StringBuilder().append("the detail page for screen type '").append(screenType).toString() != null ? screenType.toString() : "NULL' does not exist");
        } else {
            XLELog.Diagnostic("ApplicationBar", "Current detailpage is in nowplaying status.");
        }
    }

    private boolean shouldNavigateToNowPlayingDetail() {
        return !NowPlayingGlobalModel.getInstance().isMediaItemNowPlaying(this.currentDetailIdentifier);
    }

    protected void navigateToHome() {
        if (NavigationManager.getInstance().getCurrentActivity() instanceof MainPivotActivity) {
            XLELog.Diagnostic("ApplicationBar", "Already in L1.");
            MainPivotActivity mainActivity = (MainPivotActivity) NavigationManager.getInstance().getCurrentActivity();
            if (mainActivity.getCurrentPivotPane() instanceof NowPlayingActivity) {
                XLELog.Diagnostic("ApplicationBar", "Already in home.");
                return;
            }
            XLELog.Diagnostic("ApplicationBar", "Setting now playing activity as active pivot pane.");
            mainActivity.setActivePivotPane(NowPlayingActivity.class);
            return;
        }
        XLELog.Diagnostic("ApplicationBar", "Navigating to home");
        XLEGlobalData.getInstance().setActivePivotPane(MainPivotActivity.class, NowPlayingActivity.class);
        try {
            NavigationManager.getInstance().GotoScreenWithPop(MainPivotActivity.class);
        } catch (XLEException e) {
            XLELog.Error("ApplicationBar", "Failed to navigate all the way back to main pivot");
        }
    }

    protected void navigateToRemote() {
        if (!NowPlayingGlobalModel.getInstance().isConnectedToConsole() || (NavigationManager.getInstance().getCurrentActivity() instanceof SmartGlassActivity)) {
            XLELog.Diagnostic("ApplicationBar", "Connecting to xbox");
            XboxMobileOmnitureTracking.TrackConsoleConnectAttempt("Manual", "Appbar Button");
            AutoConnectAndLaunchViewModel.getInstance().manualConnectAndLaunch();
            return;
        }
        XLELog.Diagnostic("ApplicationBar", "Navigating to remote");
        NavigationManager.getInstance().NavigateTo(SmartGlassActivity.class, true);
    }

    protected void navigateToSettings() {
        XLELog.Diagnostic("ApplicationBar", "Navigating to settings");
        NavigationManager.getInstance().NavigateTo(SettingsActivity.class, true);
    }
}
