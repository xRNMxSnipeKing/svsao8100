package com.microsoft.xbox.xle.app.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.BackgroundThreadWaitor;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.XboxAppMeasurement;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.toolkit.anim.XLEAnimationPackage;
import com.microsoft.xbox.toolkit.ui.ScreenLayout;
import com.microsoft.xbox.toolkit.ui.XLEButton;
import com.microsoft.xbox.toolkit.ui.appbar.AppBarMenuButton;
import com.microsoft.xbox.toolkit.ui.appbar.ApplicationBarView;
import com.microsoft.xbox.xle.app.ApplicationBarManager;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.test.automator.Automator;
import com.microsoft.xbox.xle.test.automator.IAutomator.ActivityStateChange;
import com.microsoft.xbox.xle.ui.XLERootView;
import com.microsoft.xbox.xle.viewmodel.CompareGamesActivityPivotViewModel;
import com.microsoft.xbox.xle.viewmodel.EDSV2MediaItemViewModel;
import com.microsoft.xbox.xle.viewmodel.PivotViewModelBase;
import com.microsoft.xbox.xle.viewmodel.ViewModelBase;
import com.microsoft.xbox.xle.viewmodel.XLEGlobalData;
import java.lang.ref.WeakReference;

public abstract class ActivityBase extends ScreenLayout {
    protected static final int NO_CONTEXTUAL_APPBAR_ID = -1;
    public static final String aboutChannel = "About";
    public static final String avatarChannel = "Avatar";
    public static final String detailsChannel = "Details";
    public static final String discoverChannel = "Discover";
    public static final String gamesChannel = "Games";
    public static final String homeChannel = "Home";
    public static final String launchChannel = "Launch";
    public static final String messageChannel = "Message";
    public static final String nowPlayingChannel = "NowPlaying";
    public static final String profileChannel = "Profile";
    public static final String searchChannel = "Search";
    public static final String searchFilterChannel = "SearchFilter";
    public static final String searchResultsChannel = "SearchResults";
    public static final String settingChannel = "Settings";
    public static final String socialChannel = "Social";
    public static final String whatsnewChannel = "What's new";
    private XLEButton[] collapsedAppBarButtons;
    private boolean enableMediaTransportControls;
    private XLEButton[] expandedAppBarButtons;
    private boolean isPivotPane;
    protected ViewModelBase viewModel;

    protected abstract String getActivityName();

    protected abstract String getChannelName();

    public abstract void onCreateContentView();

    public ActivityBase() {
        this(7);
    }

    public ActivityBase(int orientation) {
        this(orientation, false);
    }

    public ActivityBase(int orientation, boolean usesAccelerometer) {
        super(XboxApplication.Instance.getApplicationContext(), orientation, usesAccelerometer);
        this.isPivotPane = false;
    }

    protected boolean shouldTrackPageVisit() {
        return true;
    }

    public boolean getShowNoNetworkPopup() {
        if (this.viewModel == null) {
            return true;
        }
        return this.viewModel.getShowNoNetworkPopup();
    }

    public void onCreate() {
        super.onCreate();
        Automator.getInstance().OnActivityStateChange(this, ActivityStateChange.Created);
        trackVisit();
    }

    private PivotActivity findPivotParent(ViewParent parent) {
        if (parent != null && (parent instanceof PivotActivity)) {
            return (PivotActivity) parent;
        }
        if (parent != null) {
            return findPivotParent(parent.getParent());
        }
        return null;
    }

    public void onStop() {
        if (getIsStarted()) {
            super.onStop();
            if (this.viewModel != null) {
                this.viewModel.onSetInactive();
                this.viewModel.onStop();
            }
        }
        if (this instanceof PivotActivity) {
            XLEApplication.getMainActivity().setPivotTitle(null);
            XLEGlobalData.getInstance().setPivotTitle(null);
        }
        Automator.getInstance().OnActivityStateChange(this, ActivityStateChange.Stopped);
    }

    public void onStart() {
        boolean z = true;
        if (!getIsStarted()) {
            super.onStart();
            if (this.viewModel != null) {
                boolean z2 = this.isPivotPane;
                boolean z3 = (this.viewModel instanceof PivotViewModelBase) || (this.viewModel instanceof EDSV2MediaItemViewModel) || (this.viewModel instanceof CompareGamesActivityPivotViewModel);
                if (z2 != z3) {
                    z = false;
                }
                XLEAssert.assertTrue(z);
                this.viewModel.onStart();
                this.viewModel.load();
            }
        }
        if (!delayAppbarAnimation()) {
            adjustBottomMargin(computeBottomMargin());
        }
        if (this instanceof PivotActivity) {
            XLEApplication.getMainActivity().clearPivotHeaders();
            XLEApplication.getMainActivity().setPivotTitle(XLEGlobalData.getInstance().getPivotTitle());
        } else if (getXLERootView() != null) {
            OnClickListener listener = null;
            int pivotHeaderIndex = 0;
            if (this.isPivotPane) {
                final PivotActivity parent = findPivotParent(getParent());
                if (parent != null) {
                    pivotHeaderIndex = parent.getIndexOfScreen(getClass());
                    listener = new OnClickListener() {
                        public void onClick(View v) {
                            parent.setActivePivotPane(ActivityBase.this.getClass());
                        }
                    };
                }
            } else {
                XLEApplication.getMainActivity().clearPivotHeaders();
            }
            XLEApplication.getMainActivity().addPivotHeader(getXLERootView().getHeaderName(), pivotHeaderIndex, listener);
        }
        Automator.getInstance().OnActivityStateChange(this, ActivityStateChange.Started);
        Automator.getInstance().setViewModelForActivity(this, this.viewModel);
    }

    public void onAnimateInStarted() {
        if (this.viewModel != null) {
            this.viewModel.forceUpdateViewImmediately();
        }
    }

    public void onAnimateInCompleted() {
        if (this.viewModel != null) {
            final WeakReference<ViewModelBase> viewModelWeakPtr = new WeakReference(this.viewModel);
            BackgroundThreadWaitor.getInstance().postRunnableAfterReady(new Runnable() {
                public void run() {
                    ViewModelBase viewModelPtr = (ViewModelBase) viewModelWeakPtr.get();
                    if (viewModelPtr != null) {
                        viewModelPtr.forceUpdateViewImmediately();
                    }
                }
            });
        }
    }

    public void forceUpdateViewImmediately() {
        if (this.viewModel != null) {
            this.viewModel.forceUpdateViewImmediately();
        }
    }

    protected boolean isManagingOwnAppBar() {
        return false;
    }

    protected int computeBottomMargin() {
        int marginBottom = 0;
        if (getXLERootView() != null) {
            if (getShouldShowAppbar()) {
                marginBottom = XLEApplication.Resources.getDimensionPixelSize(R.dimen.applicationBarHeight);
            } else if (this.collapsedAppBarButtons != null && this.collapsedAppBarButtons.length > 0) {
                boolean hasIconButtons = false;
                for (XLEButton button : this.collapsedAppBarButtons) {
                    if (!(button instanceof AppBarMenuButton)) {
                        hasIconButtons = true;
                    }
                }
                marginBottom = hasIconButtons ? XLEApplication.Resources.getDimensionPixelSize(R.dimen.applicationBarHeight) : 0;
            }
            if (isManagingOwnAppBar()) {
                marginBottom = XLEApplication.Resources.getDimensionPixelSize(R.dimen.applicationBarHeight);
            }
            if (this.isPivotPane) {
                marginBottom += XLEApplication.Resources.getDimensionPixelSize(R.dimen.applicationBarPageIndicatorMarginBottom);
            }
            XLELog.Diagnostic(JavaUtil.getShortClassName(getClass()), "Bottom margin: " + marginBottom);
        }
        return marginBottom;
    }

    public XLEAnimationPackage getAnimateOut(boolean goingBack) {
        if (this.viewModel != null) {
            return this.viewModel.getAnimateOut(goingBack);
        }
        return null;
    }

    public XLEAnimationPackage getAnimateIn(boolean goingBack) {
        if (this.viewModel != null) {
            return this.viewModel.getAnimateIn(goingBack);
        }
        return null;
    }

    public void onSaveInstanceState(Bundle outState) {
        if (this.viewModel != null) {
            this.viewModel.onSaveInstanceState(outState);
        }
        super.onSaveInstanceState(outState);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (this.viewModel != null) {
            this.viewModel.onRestoreInstanceState(savedInstanceState);
        }
    }

    public void onBackButtonPressed() {
        if (this.viewModel != null) {
            this.viewModel.onBackButtonPressed();
        } else {
            XLEApplication.MainActivity.goBack();
        }
    }

    public void onSetActive() {
        super.onSetActive();
        if (this.viewModel != null) {
            this.viewModel.onSetActive();
        }
        if (this instanceof PivotActivity) {
            XLELog.Diagnostic("ActivityBase", "This is a pivot activity. Skipping screen specific operations.");
            return;
        }
        if (!isManagingOwnAppBar()) {
            ApplicationBarManager.getInstance().setShouldShowNowPlaying(getShouldShowAppbar());
            ApplicationBarManager.getInstance().setEnableMediaTransportControls(this.enableMediaTransportControls);
            ApplicationBarManager.getInstance().addNewCollapsedButtons(this.collapsedAppBarButtons);
            ApplicationBarManager.getInstance().addNewExpandedButtons(this.expandedAppBarButtons);
        }
        if (getXLERootView() != null) {
            XLEApplication.getMainActivity().setIsTopLevel(getXLERootView().getIsTopLevel());
            XLEApplication.getMainActivity().setShowTitleBar(getXLERootView().getShowTitleBar());
            if (!(isManagingOwnAppBar() || delayAppbarAnimation())) {
                ApplicationBarManager.getInstance().beginAnimation();
            }
            sendAccessibilityEvent(8);
            XLEApplication.getMainActivity().setActivePivotHeader(getXLERootView().getHeaderName());
        }
    }

    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() != 8 || getXLERootView() == null || getXLERootView().getContentDescription() == null) {
            return super.dispatchPopulateAccessibilityEvent(event);
        }
        event.getText().clear();
        event.getText().add(getXLERootView().getContentDescription());
        return true;
    }

    public void onSetInactive() {
        super.onSetInactive();
        if (this.viewModel != null) {
            this.viewModel.onSetInactive();
        }
        if (!(this instanceof PivotActivity) && getXLERootView() != null) {
            XLEApplication.getMainActivity().setInactivePivotHeader(getXLERootView().getHeaderName());
        }
    }

    protected void trackVisit() {
        if (shouldTrackPageVisit()) {
            XboxAppMeasurement.getInstance().trackVisit(getChannelName(), getActivityName());
        }
    }

    protected void setAppBarLayout(int appBarLayoutId, boolean isEditable, boolean enableMediaTransportControls) {
        if (appBarLayoutId > 0) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService("layout_inflater");
            ApplicationBarView expandedAppBar = (ApplicationBarView) vi.inflate(appBarLayoutId, null);
            this.collapsedAppBarButtons = extractAppBarButtons((ApplicationBarView) vi.inflate(appBarLayoutId, null));
            this.expandedAppBarButtons = extractAppBarButtons(expandedAppBar);
        } else {
            this.collapsedAppBarButtons = new XLEButton[0];
            this.expandedAppBarButtons = new XLEButton[0];
        }
        setIsEditable(isEditable);
        if (isEditable) {
            enableMediaTransportControls = false;
        } else if (XLEApplication.Instance.getIsTablet()) {
            enableMediaTransportControls = true;
        }
        this.enableMediaTransportControls = enableMediaTransportControls;
    }

    private XLEButton[] extractAppBarButtons(ApplicationBarView appBarView) {
        if (appBarView != null) {
            return appBarView.getAppBarButtons();
        }
        return null;
    }

    public void onPause() {
        super.onPause();
        if (this.viewModel != null) {
            this.viewModel.onPause();
        }
    }

    public void onApplicationPause() {
        super.onApplicationPause();
        if (this.viewModel != null) {
            this.viewModel.onApplicationPause();
        }
    }

    public void onApplicationResume() {
        super.onApplicationResume();
        if (this.viewModel != null) {
            this.viewModel.onApplicationResume();
        }
    }

    public void onResume() {
        super.onResume();
        if (this.viewModel != null) {
            this.viewModel.onResume();
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (this.viewModel != null) {
            this.viewModel.onDestroy();
        }
        XLELog.Diagnostic("ActivityBase", "onDestroy called");
        this.viewModel = null;
    }

    public void onTombstone() {
        if (this.viewModel != null) {
            this.viewModel.onTombstone();
        }
        super.onTombstone();
    }

    public void onRehydrate() {
        super.onRehydrate();
        if (this.viewModel != null) {
            XLELog.Diagnostic("ActivityBase", "onRehydrate");
            this.viewModel.onRehydrate();
        }
    }

    public void onRehydrateOverride() {
        onCreateContentView();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        XLELog.Diagnostic("ActivityBase", "onConfigurationChanged");
        if (this.viewModel != null) {
            this.viewModel.onConfigurationChanged(newConfig);
        }
    }

    public void setIsPivotPane(boolean isPivotPane) {
        this.isPivotPane = isPivotPane;
    }

    private XLERootView getXLERootView() {
        if (getChildAt(0) instanceof XLERootView) {
            return (XLERootView) getChildAt(0);
        }
        return null;
    }

    public void adjustBottomMargin(int bottomMargin) {
        if (getXLERootView() != null) {
            getXLERootView().setBottomMargin(bottomMargin);
        }
    }

    public void removeBottomMargin() {
        if (getXLERootView() != null) {
            getXLERootView().setBottomMargin(0);
        }
    }

    public void resetBottomMargin() {
        if (getXLERootView() != null) {
            adjustBottomMargin(computeBottomMargin());
        }
    }

    protected boolean delayAppbarAnimation() {
        return false;
    }
}
