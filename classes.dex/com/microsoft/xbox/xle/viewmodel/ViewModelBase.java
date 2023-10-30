package com.microsoft.xbox.xle.viewmodel;

import android.content.res.Configuration;
import android.os.Bundle;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.authenticate.LoginModel;
import com.microsoft.xbox.service.model.NowPlayingGlobalModel;
import com.microsoft.xbox.service.model.NowPlayingGlobalModel.NowPlayingState;
import com.microsoft.xbox.service.model.SessionModel;
import com.microsoft.xbox.service.model.UpdateData;
import com.microsoft.xbox.service.model.UpdateType;
import com.microsoft.xbox.service.model.eds.DetailDisplayScreenType;
import com.microsoft.xbox.service.model.edsv2.EDSV2ActivityItem;
import com.microsoft.xbox.service.model.edsv2.EDSV2GameMediaItem;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaItem;
import com.microsoft.xbox.service.model.edsv2.EDSV2PartnerApplicationLaunchInfo;
import com.microsoft.xbox.service.model.edsv2.EDSV2Provider;
import com.microsoft.xbox.service.model.serialization.GameInfo;
import com.microsoft.xbox.service.network.managers.ServiceCommon;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.DialogManager;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEConstants;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.XLEObserver;
import com.microsoft.xbox.toolkit.anim.XLEAnimation;
import com.microsoft.xbox.toolkit.anim.XLEAnimationPackage;
import com.microsoft.xbox.toolkit.ui.NavigationManager;
import com.microsoft.xbox.toolkit.ui.ScreenLayout;
import com.microsoft.xbox.toolkit.ui.appbar.AppBarMenuButton;
import com.microsoft.xbox.xle.app.ApplicationBarManager;
import com.microsoft.xbox.xle.app.DeviceCapabilities;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.app.XboxMobileOmnitureTracking;
import com.microsoft.xbox.xle.app.activity.AchievementsActivity;
import com.microsoft.xbox.xle.app.activity.ActivityBase;
import com.microsoft.xbox.xle.app.activity.ActivityGalleryActivity;
import com.microsoft.xbox.xle.app.activity.ActivitySummaryActivity;
import com.microsoft.xbox.xle.app.activity.CanvasWebViewActivity;
import com.microsoft.xbox.xle.app.activity.DetailsPivotActivity;
import com.microsoft.xbox.xle.app.activity.SmartGlassActivity;
import com.microsoft.xbox.xle.app.activity.XboxAuthActivity;
import com.microsoft.xbox.xle.test.automator.Automator;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public abstract class ViewModelBase implements XLEObserver<UpdateData> {
    protected static int LAUNCH_TIME_OUT = ServiceCommon.TcpSocketTimeout;
    public static final String TAG_PAGE_LOADING_TIME = "performance_measure_page_loadingtime";
    protected int LifetimeInMinutes;
    protected AdapterBase adapter;
    private boolean isActive;
    protected boolean isForeground;
    protected boolean isLaunching;
    protected Runnable launchTimeoutHandler;
    protected int listIndex;
    private NavigationData nextScreenData;
    protected int offset;
    private boolean onlyProcessExceptionsAndShowToastsWhenActive;
    private boolean showNoNetworkPopup;
    private HashMap<UpdateType, XLEException> updateExceptions;
    private EnumSet<UpdateType> updateTypesToCheck;
    private boolean updating;

    private class NavigationData {
        private NavigationType navigationType;
        private Class<? extends ScreenLayout> screenClass;

        protected NavigationData(Class<? extends ScreenLayout> screen, NavigationType type) {
            this.screenClass = screen;
            this.navigationType = type;
        }

        protected Class<? extends ScreenLayout> getScreenClass() {
            return this.screenClass;
        }

        protected NavigationType getNavigationType() {
            return this.navigationType;
        }
    }

    private enum NavigationType {
        Push,
        PopReplace,
        PopAll
    }

    public abstract boolean isBusy();

    public abstract void load(boolean z);

    public abstract void onRehydrate();

    protected abstract void onStartOverride();

    protected abstract void onStopOverride();

    public ViewModelBase() {
        this(true, false);
    }

    public ViewModelBase(boolean showNoNetworkPopup, boolean onlyProcessExceptionsAndShowToastsWhenActive) {
        this.LifetimeInMinutes = 60;
        this.updateExceptions = new HashMap();
        this.showNoNetworkPopup = true;
        this.onlyProcessExceptionsAndShowToastsWhenActive = false;
        this.nextScreenData = null;
        this.updating = false;
        this.isLaunching = false;
        this.showNoNetworkPopup = showNoNetworkPopup;
        this.onlyProcessExceptionsAndShowToastsWhenActive = onlyProcessExceptionsAndShowToastsWhenActive;
    }

    public final void onStart() {
        this.isForeground = true;
        onStartOverride();
        this.adapter.onStart();
    }

    public void setListPosition(int index, int offset) {
        this.listIndex = index;
        this.offset = offset;
    }

    public int getAndResetListPosition() {
        int value = this.listIndex;
        this.listIndex = 0;
        return value;
    }

    public int getAndResetListOffset() {
        int offset = this.offset;
        this.offset = 0;
        return offset;
    }

    public final void onStop() {
        this.isForeground = false;
        this.adapter.onStop();
        DialogManager.getInstance().dismissBlocking();
        DialogManager.getInstance().dismissTopNonFatalAlert();
        DialogManager.getInstance().dismissToast();
        onStopOverride();
    }

    public void onPause() {
        cancelLaunchTimeout();
        if (this.adapter != null) {
            this.adapter.onPause();
        }
    }

    public void onApplicationPause() {
        if (this.adapter != null) {
            this.adapter.onApplicationPause();
        }
    }

    public void onApplicationResume() {
        if (this.adapter != null) {
            this.adapter.onApplicationResume();
        }
    }

    public void onResume() {
        if (this.adapter != null) {
            this.adapter.onResume();
        }
        this.adapter.updateView();
    }

    public void onDestroy() {
        XLELog.Diagnostic("ViewModelBase", "onDestroy");
        if (this.adapter != null) {
            this.adapter.onDestroy();
        }
        this.adapter = null;
    }

    public void onTombstone() {
        XLELog.Diagnostic("ViewModelBase", "onTombstone");
        if (this.adapter != null) {
            this.adapter.onDestroy();
        }
        this.adapter = null;
    }

    public void forceUpdateViewImmediately() {
        if (this.adapter != null) {
            this.adapter.forceUpdateViewImmediately();
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
    }

    public void onSaveInstanceState(Bundle outState) {
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
    }

    public void onBackButtonPressed() {
        goBack();
    }

    public boolean isBlockingBusy() {
        return false;
    }

    public String getBlockingStatusText() {
        return null;
    }

    public final void load() {
        load(false);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void update(com.microsoft.xbox.toolkit.AsyncResult<com.microsoft.xbox.service.model.UpdateData> r13) {
        /*
        r12 = this;
        r11 = 3;
        r10 = 2;
        r9 = 0;
        r4 = 1;
        r5 = 0;
        r12.updating = r4;
        r3 = r12.nextScreenData;
        if (r3 != 0) goto L_0x00b1;
    L_0x000b:
        r3 = r4;
    L_0x000c:
        com.microsoft.xbox.toolkit.XLEAssert.assertTrue(r3);
        r12.nextScreenData = r9;
        r3 = com.microsoft.xbox.toolkit.ui.NavigationManager.getInstance();
        r3 = r3.getCurrentActivity();
        r3 = r3 instanceof com.microsoft.xbox.xle.app.activity.XboxAuthActivity;
        if (r3 != 0) goto L_0x0059;
    L_0x001d:
        r3 = r13.getException();
        if (r3 == 0) goto L_0x0059;
    L_0x0023:
        r3 = r13.getException();
        r1 = r3.getErrorCode();
        r3 = java.lang.Long.toString(r1);
        com.microsoft.xbox.xle.app.XboxMobileOmnitureTracking.TrackError(r3);
        r3 = r13.getException();
        r3 = r3.getIsHandled();
        if (r3 != 0) goto L_0x0059;
    L_0x003c:
        r6 = 1002; // 0x3ea float:1.404E-42 double:4.95E-321;
        r3 = (r1 > r6 ? 1 : (r1 == r6 ? 0 : -1));
        if (r3 == 0) goto L_0x0048;
    L_0x0042:
        r6 = 1010; // 0x3f2 float:1.415E-42 double:4.99E-321;
        r3 = (r1 > r6 ? 1 : (r1 == r6 ? 0 : -1));
        if (r3 != 0) goto L_0x0059;
    L_0x0048:
        r3 = r13.getException();
        r3.setIsHandled(r4);
        r3 = "ViewModelBase";
        r6 = "cookie or access token expired, log out";
        com.microsoft.xbox.toolkit.XLELog.Warning(r3, r6);
        r12.logOut(r5);
    L_0x0059:
        r3 = r12.nextScreenData;
        if (r3 != 0) goto L_0x0064;
    L_0x005d:
        r3 = r12.adapter;
        if (r3 == 0) goto L_0x0064;
    L_0x0061:
        r12.updateOverride(r13);
    L_0x0064:
        r12.updating = r5;
        r3 = r12.nextScreenData;
        if (r3 == 0) goto L_0x010f;
    L_0x006a:
        r3 = com.microsoft.xbox.xle.viewmodel.ViewModelBase.AnonymousClass8.$SwitchMap$com$microsoft$xbox$xle$viewmodel$ViewModelBase$NavigationType;	 Catch:{ XLEException -> 0x00c3 }
        r6 = r12.nextScreenData;	 Catch:{ XLEException -> 0x00c3 }
        r6 = r6.getNavigationType();	 Catch:{ XLEException -> 0x00c3 }
        r6 = r6.ordinal();	 Catch:{ XLEException -> 0x00c3 }
        r3 = r3[r6];	 Catch:{ XLEException -> 0x00c3 }
        switch(r3) {
            case 1: goto L_0x00b4;
            case 2: goto L_0x00f0;
            case 3: goto L_0x0100;
            default: goto L_0x007b;
        };
    L_0x007b:
        r12.nextScreenData = r9;
        r6 = "ViewModelBase";
        r7 = "Update called. VM: %s, UpdateType: %s, isFinal: %b";
        r8 = new java.lang.Object[r11];
        r3 = r12.getClass();
        r3 = r3.getSimpleName();
        r8[r5] = r3;
        r3 = r13.getResult();
        r3 = (com.microsoft.xbox.service.model.UpdateData) r3;
        r3 = r3.getUpdateType();
        r8[r4] = r3;
        r3 = r13.getResult();
        r3 = (com.microsoft.xbox.service.model.UpdateData) r3;
        r3 = r3.getIsFinal();
        r3 = java.lang.Boolean.valueOf(r3);
        r8[r10] = r3;
        r3 = java.lang.String.format(r7, r8);
        com.microsoft.xbox.toolkit.XLELog.Diagnostic(r6, r3);
        return;
    L_0x00b1:
        r3 = r5;
        goto L_0x000c;
    L_0x00b4:
        r3 = com.microsoft.xbox.toolkit.ui.NavigationManager.getInstance();	 Catch:{ XLEException -> 0x00c3 }
        r6 = r12.nextScreenData;	 Catch:{ XLEException -> 0x00c3 }
        r6 = r6.getScreenClass();	 Catch:{ XLEException -> 0x00c3 }
        r7 = 1;
        r3.NavigateTo(r6, r7);	 Catch:{ XLEException -> 0x00c3 }
        goto L_0x007b;
    L_0x00c3:
        r0 = move-exception;
        r3 = "ViewModelBase";
        r6 = "Failed to navigate to %s with navigation type %s: ";
        r7 = new java.lang.Object[r11];
        r8 = r12.nextScreenData;
        r8 = r8.getScreenClass();
        r8 = r8.getSimpleName();
        r7[r5] = r8;
        r8 = r12.nextScreenData;
        r8 = r8.getNavigationType();
        r8 = r8.toString();
        r7[r4] = r8;
        r8 = r0.toString();
        r7[r10] = r8;
        r6 = java.lang.String.format(r6, r7);
        com.microsoft.xbox.toolkit.XLELog.Error(r3, r6);
        goto L_0x007b;
    L_0x00f0:
        r3 = com.microsoft.xbox.toolkit.ui.NavigationManager.getInstance();	 Catch:{ XLEException -> 0x00c3 }
        r6 = r12.nextScreenData;	 Catch:{ XLEException -> 0x00c3 }
        r6 = r6.getScreenClass();	 Catch:{ XLEException -> 0x00c3 }
        r7 = 0;
        r3.NavigateTo(r6, r7);	 Catch:{ XLEException -> 0x00c3 }
        goto L_0x007b;
    L_0x0100:
        r3 = com.microsoft.xbox.toolkit.ui.NavigationManager.getInstance();	 Catch:{ XLEException -> 0x00c3 }
        r6 = r12.nextScreenData;	 Catch:{ XLEException -> 0x00c3 }
        r6 = r6.getScreenClass();	 Catch:{ XLEException -> 0x00c3 }
        r3.GotoScreenWithPop(r6);	 Catch:{ XLEException -> 0x00c3 }
        goto L_0x007b;
    L_0x010f:
        r3 = r12.shouldProcessErrors();
        if (r3 == 0) goto L_0x007b;
    L_0x0115:
        r3 = r13.getException();
        if (r3 == 0) goto L_0x0196;
    L_0x011b:
        r3 = r13.getException();
        r3 = r3.getIsHandled();
        if (r3 != 0) goto L_0x0196;
    L_0x0125:
        r3 = "ViewModelBase";
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r7 = "Got XLEException: ";
        r6 = r6.append(r7);
        r7 = r13.getException();
        r7 = r7.getErrorCode();
        r7 = java.lang.Long.toString(r7);
        r6 = r6.append(r7);
        r6 = r6.toString();
        com.microsoft.xbox.toolkit.XLELog.Diagnostic(r3, r6);
        r3 = r12.updateTypesToCheck;
        if (r3 == 0) goto L_0x0196;
    L_0x014d:
        r6 = r12.updateTypesToCheck;
        r3 = r13.getResult();
        r3 = (com.microsoft.xbox.service.model.UpdateData) r3;
        r3 = r3.getUpdateType();
        r3 = r6.contains(r3);
        if (r3 == 0) goto L_0x0196;
    L_0x015f:
        r3 = "ViewModelBase";
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r7 = "Added to update exceptions XLEException: ";
        r6 = r6.append(r7);
        r7 = r13.getException();
        r7 = r7.getErrorCode();
        r7 = java.lang.Long.toString(r7);
        r6 = r6.append(r7);
        r6 = r6.toString();
        com.microsoft.xbox.toolkit.XLELog.Diagnostic(r3, r6);
        r6 = r12.updateExceptions;
        r3 = r13.getResult();
        r3 = (com.microsoft.xbox.service.model.UpdateData) r3;
        r3 = r3.getUpdateType();
        r7 = r13.getException();
        r6.put(r3, r7);
    L_0x0196:
        r3 = r13.getResult();
        r3 = (com.microsoft.xbox.service.model.UpdateData) r3;
        r3 = r3.getIsFinal();
        if (r3 == 0) goto L_0x007b;
    L_0x01a2:
        r3 = r12.updateTypesToCheck;
        if (r3 == 0) goto L_0x01b5;
    L_0x01a6:
        r6 = r12.updateTypesToCheck;
        r3 = r13.getResult();
        r3 = (com.microsoft.xbox.service.model.UpdateData) r3;
        r3 = r3.getUpdateType();
        r6.remove(r3);
    L_0x01b5:
        r3 = r12.updateTypesToCheck;
        if (r3 == 0) goto L_0x01c1;
    L_0x01b9:
        r3 = r12.updateTypesToCheck;
        r3 = r3.isEmpty();
        if (r3 == 0) goto L_0x007b;
    L_0x01c1:
        r12.onUpdateFinished();
        r12.updateTypesToCheck = r9;
        goto L_0x007b;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.microsoft.xbox.xle.viewmodel.ViewModelBase.update(com.microsoft.xbox.toolkit.AsyncResult):void");
    }

    protected void updateOverride(AsyncResult<UpdateData> asyncResult) {
    }

    protected void logOut(boolean clearEverything) {
        ApplicationBarManager.getInstance().onPause();
        LoginModel.getInstance().logOut(clearEverything);
        try {
            if (this.updating) {
                this.nextScreenData = new NavigationData(XboxAuthActivity.class, NavigationType.PopAll);
            } else {
                NavigationManager.getInstance().GotoScreenWithPop(XboxAuthActivity.class);
            }
        } catch (XLEException e) {
            XLELog.Error("Failed to navigate", e.toString());
        }
    }

    protected void setUpdateTypesToCheck(EnumSet<UpdateType> checkList) {
        this.updateTypesToCheck = checkList;
        this.updateExceptions.clear();
    }

    protected boolean checkErrorCode(UpdateType updateType, long errorCode) {
        if (!this.updateExceptions.containsKey(updateType) || ((XLEException) this.updateExceptions.get(updateType)).getErrorCode() != errorCode) {
            return false;
        }
        if (((XLEException) this.updateExceptions.get(updateType)).getIsHandled()) {
            return false;
        }
        XLELog.Diagnostic("ViewModelBase", String.format("checkErrorCode UpdateType: %s, ErrorCode: %d", new Object[]{updateType, Long.valueOf(errorCode)}));
        return true;
    }

    protected boolean updateTypesToCheckIsEmpty() {
        return this.updateTypesToCheck == null || this.updateTypesToCheck.isEmpty();
    }

    protected boolean updateTypesToCheckHadAnyErrors() {
        return !this.updateExceptions.isEmpty();
    }

    protected void onUpdateFinished() {
        this.updateTypesToCheck = null;
        this.updateExceptions.clear();
    }

    public XLEAnimationPackage getAnimateOut(boolean goingBack) {
        ArrayList<XLEAnimation> animations = this.adapter.getAnimateOut(goingBack);
        if (animations == null || animations.size() <= 0) {
            return null;
        }
        XLEAnimationPackage xLEAnimationPackage = new XLEAnimationPackage();
        Iterator i$ = animations.iterator();
        while (i$.hasNext()) {
            xLEAnimationPackage.add((XLEAnimation) i$.next());
        }
        return xLEAnimationPackage;
    }

    public XLEAnimationPackage getAnimateIn(boolean goingBack) {
        ArrayList<XLEAnimation> animations = this.adapter.getAnimateIn(goingBack);
        if (animations == null || animations.size() <= 0) {
            return null;
        }
        XLEAnimationPackage xLEAnimationPackage = new XLEAnimationPackage();
        Iterator i$ = animations.iterator();
        while (i$.hasNext()) {
            xLEAnimationPackage.add((XLEAnimation) i$.next());
        }
        return xLEAnimationPackage;
    }

    public void TEST_induceGoBack() {
    }

    protected void goBack() {
        DialogManager.getInstance().dismissBlocking();
        XLEApplication.MainActivity.goBack();
    }

    protected void NavigateTo(Class<? extends ScreenLayout> screenClass) {
        NavigateTo(screenClass, true);
    }

    protected void NavigateTo(Class<? extends ScreenLayout> screenClass, boolean addToStack) {
        cancelLaunchTimeout();
        XLEAssert.assertFalse("We shouldn't navigate to a new screen if the current screen is blocking", isBlockingBusy());
        if (this.updating) {
            this.nextScreenData = new NavigationData(screenClass, addToStack ? NavigationType.Push : NavigationType.PopReplace);
            return;
        }
        XLEAssert.assertFalse("We shouldn't navigate to a new screen if the current screen is blocking", isBlockingBusy());
        NavigationManager.getInstance().NavigateTo(screenClass, addToStack);
    }

    protected void showMustActDialog(String title, String promptText, String okText, Runnable okHandler, boolean isFatal) {
        if (!Automator.getInstance().onShowDialog(title, promptText)) {
            Runnable okRunnable = addTestHook(okHandler);
            if (isFatal) {
                DialogManager.getInstance().showFatalAlertDialog(title, promptText, okText, okRunnable);
            } else if (shouldProcessErrors()) {
                DialogManager.getInstance().showNonFatalAlertDialog(title, promptText, okText, okRunnable);
            }
            Automator.getInstance().setCurrentDialog(promptText, DialogManager.getInstance().getVisibleDialog());
        }
    }

    protected void showOkCancelDialog(String promptText, String okText, Runnable okHandler, String cancelText, Runnable cancelHandler) {
        showOkCancelDialog(null, promptText, okText, okHandler, cancelText, cancelHandler);
    }

    protected void showOkCancelDialog(String title, String promptText, String okText, Runnable okHandler, String cancelText, Runnable cancelHandler) {
        XLEAssert.assertNotNull("You must supply cancel text if this is not a must-act dialog.", cancelText);
        if (shouldProcessErrors() && !Automator.getInstance().onShowDialog(title, promptText)) {
            DialogManager.getInstance().showOkCancelDialog(title, promptText, okText, addTestHook(okHandler), cancelText, addTestHook(cancelHandler));
            Automator.getInstance().setCurrentDialog(promptText, DialogManager.getInstance().getVisibleDialog());
        }
    }

    private Runnable addTestHook(final Runnable originalRunnable) {
        return new Runnable() {
            public void run() {
                if (originalRunnable != null) {
                    originalRunnable.run();
                }
                Automator.getInstance().setCurrentDialog(null, null);
            }
        };
    }

    protected void showError(int contentResId) {
        if (!Automator.getInstance().onShowError(contentResId) && shouldProcessErrors()) {
            DialogManager.getInstance().showToast(contentResId);
        }
    }

    protected void showDiscardChangesGoBack() {
        showDiscardChangeWithRunnable(new Runnable() {
            public void run() {
                ViewModelBase.this.goBack();
            }
        });
    }

    protected void showDiscardChangeNavigate(final Class<? extends ScreenLayout> screenClass) {
        showDiscardChangeWithRunnable(new Runnable() {
            public void run() {
                ViewModelBase.this.NavigateTo(screenClass);
            }
        });
    }

    private void showDiscardChangeWithRunnable(Runnable okHandler) {
        showOkCancelDialog(XLEApplication.Resources.getString(R.string.dialog_discard_changes_title), XLEApplication.Resources.getString(R.string.dialog_discard_changes_description), XLEApplication.Resources.getString(R.string.OK), okHandler, XLEApplication.Resources.getString(R.string.Cancel), null);
    }

    public void onSetActive() {
        XLELog.Diagnostic("ViewModelBase", "onSetActive called on: " + getClass().getSimpleName());
        this.isActive = true;
        if (this.adapter != null) {
            this.adapter.onSetActive();
        }
    }

    public final void onSetInactive() {
        DialogManager.getInstance().dismissToast();
        XLELog.Diagnostic("ViewModelBase", "onSetInactive called on: " + getClass().getSimpleName());
        this.isActive = false;
        if (this.adapter != null) {
            this.adapter.onSetInactive();
        }
    }

    public boolean getIsActive() {
        return this.isActive;
    }

    public boolean getShowNoNetworkPopup() {
        return this.showNoNetworkPopup;
    }

    private boolean shouldProcessErrors() {
        if (this.onlyProcessExceptionsAndShowToastsWhenActive) {
            return this.isActive;
        }
        return true;
    }

    public List<AppBarMenuButton> getTestMenuButtons() {
        return null;
    }

    protected void navigateToAppOrMediaDetails(EDSV2MediaItem mediaItem) {
        navigateToAppOrMediaDetails(mediaItem, true, null);
    }

    protected void navigateToAppOrMediaDetails(EDSV2MediaItem mediaItem, boolean addToStack) {
        navigateToAppOrMediaDetails(mediaItem, addToStack, null);
    }

    protected void navigateToAppOrMediaDetails(EDSV2MediaItem mediaItem, Class<? extends ActivityBase> defaultScreen) {
        navigateToAppOrMediaDetails(mediaItem, true, defaultScreen);
    }

    protected void navigateToAppOrMediaDetails(EDSV2MediaItem mediaItem, boolean addToStack, Class<? extends ActivityBase> defaultScreen) {
        XLEGlobalData.getInstance().setDefaultScreenClass(defaultScreen);
        XLEGlobalData.getInstance().setSelectedMediaItemData(mediaItem);
        XboxMobileOmnitureTracking.SetDetails(Integer.toString(mediaItem.getMediaType()), mediaItem.getTitle(), mediaItem.getCanonicalId());
        DetailDisplayScreenType screenType = DetailPageHelper.getDetailScreenTypeFromMediaType(mediaItem.getMediaType());
        boolean z = defaultScreen != null && defaultScreen == ActivitySummaryActivity.class;
        DetailPivotPaneData[] data = DetailPageHelper.getDetailPivotPaneData(screenType, z);
        XLEGlobalData.getInstance().setDetailPivotData(data);
        if (data != null) {
            NavigateTo(DetailsPivotActivity.class, addToStack);
            XLEGlobalData.getInstance().setActivePivotPane(DetailsPivotActivity.class, defaultScreen);
            return;
        }
        XLELog.Warning("ViewModelBase", new StringBuilder().append("the detail page for type ").append(screenType).toString() != null ? screenType.toString() : "NULL does not exist");
        showError(R.string.details_page_nopage_for_itemtype);
    }

    public void navigateToActivityDetails(EDSV2MediaItem parentMediaItem, EDSV2ActivityItem activityData) {
        XLEGlobalData.getInstance().setSelectedMediaItemData(parentMediaItem);
        XLEGlobalData.getInstance().setActivityParentMediaItemData(parentMediaItem);
        XLEGlobalData.getInstance().setSelectedActivityData(activityData);
        DetailPivotPaneData[] data = DetailPageHelper.getDetailPivotPaneData(DetailDisplayScreenType.ActivityDetails);
        XLEGlobalData.getInstance().setDetailPivotData(data);
        XboxMobileOmnitureTracking.SetDetails(Integer.toString(activityData.getMediaType()), activityData.getTitle(), activityData.getCanonicalId());
        if (data != null) {
            NavigateTo(DetailsPivotActivity.class);
            return;
        }
        XLELog.Warning("ViewModelBase", "the activity detail page data does not exist");
        showError(R.string.details_page_nopage_for_itemtype);
    }

    protected void addScreenToDetailsPivot(final Class<? extends ActivityBase> screenClass) {
        ThreadManager.UIThreadPost(new Runnable() {
            public void run() {
                if (ViewModelBase.this.isForeground) {
                    XLEAssert.assertTrue(NavigationManager.getInstance().getCurrentActivity() instanceof DetailsPivotActivity);
                    ((DetailsPivotActivity) NavigationManager.getInstance().getCurrentActivity()).addPivotPane(screenClass);
                }
            }
        });
    }

    protected void addActivitySummaryScreenToDetailsPivot() {
        XLELog.Diagnostic("ViewModelBase", "Adding ActivitySummaryActivity to details pivot");
        addScreenToDetailsPivot(ActivitySummaryActivity.class);
    }

    protected void removeScreenFromDetailsPivot(final Class<? extends ActivityBase> screenClass) {
        ThreadManager.UIThreadPost(new Runnable() {
            public void run() {
                if (ViewModelBase.this.isForeground) {
                    XLEAssert.assertTrue(NavigationManager.getInstance().getCurrentActivity() instanceof DetailsPivotActivity);
                    ((DetailsPivotActivity) NavigationManager.getInstance().getCurrentActivity()).removePivotPane(screenClass);
                }
            }
        });
    }

    protected void removeActivitySummaryScreenFromDetailsPivot() {
        XLELog.Diagnostic("ViewModelBase", "Removing ActivitySummaryActivity from details pivot");
        removeScreenFromDetailsPivot(ActivitySummaryActivity.class);
    }

    protected void addActivityGalleryScreenToDetailsPivot(EDSV2ActivityItem activityData) {
        XLEGlobalData.getInstance().setSelectedActivityData(activityData);
        XLELog.Diagnostic("ViewModelBase", "Adding ActivityGalleryActivity to details pivot");
        addScreenToDetailsPivot(ActivityGalleryActivity.class);
    }

    public void launchActivityPurchasedAndMeetsDeviceRequirement(EDSV2MediaItem parentMediaItem, EDSV2ActivityItem activityData) {
        XLEAssert.assertTrue("Only launch activity that's purchased", activityData.isPurchased());
        XLEAssert.assertTrue("Only launch activity that's supported by the device", DeviceCapabilities.getInstance().checkDeviceRequirements(activityData.getActivityLaunchInfo().getRequiresCapabilities()));
        XLELog.Info("ViewModelBase", "Navigating to activity canvas");
        XLEGlobalData.getInstance().setSelectedActivityData(activityData);
        XLEGlobalData.getInstance().setActivityParentMediaItemData(parentMediaItem);
        XLEGlobalData.getInstance().setIsAutoLaunch(false);
        XboxMobileOmnitureTracking.SetDetails(Integer.toString(activityData.getMediaType()), activityData.getTitle(), activityData.getCanonicalId());
        NavigateTo(CanvasWebViewActivity.class);
    }

    protected void checkDeviceRequirementAndLaunchActivity(EDSV2MediaItem parentMediaItem, EDSV2ActivityItem activityData) {
        XLEAssert.assertTrue("Only check device requirements for purchased activities", activityData.isPurchased());
        if (DeviceCapabilities.getInstance().checkDeviceRequirements(activityData.getActivityLaunchInfo().getRequiresCapabilities())) {
            launchActivityPurchasedAndMeetsDeviceRequirement(parentMediaItem, activityData);
            return;
        }
        showMustActDialog(null, XLEApplication.Resources.getString(R.string.activity_play_device_capabilities_failed), XLEApplication.Resources.getString(R.string.OK), null, false);
    }

    public void navigateToAchievements(GameInfo game) {
        XLEAssert.assertNotNull("Game should not be null.", game);
        navigateToAchievements(new EDSV2GameMediaItem(game));
    }

    public void navigateToAchievements(EDSV2GameMediaItem game) {
        navigateToAchievements(game, true);
    }

    public void navigateToAchievements(EDSV2GameMediaItem game, boolean addToStack) {
        XLEAssert.assertNotNull("Game should not be null.", game);
        XboxMobileOmnitureTracking.TrackGameDetailView(game.getTitle());
        XLELog.Info("ViewModelBase", String.format("Navigating to achievements for titleid=0x%x", new Object[]{Long.valueOf(game.getTitleId())}));
        navigateToAppOrMediaDetails(game, addToStack, AchievementsActivity.class);
    }

    public void navigateToRemote(boolean launchedByUser) {
        XLELog.Diagnostic("ViewModelBase", "Navigating to remote");
        NavigateTo(SmartGlassActivity.class);
    }

    protected void cancelLaunchTimeout() {
        this.isLaunching = false;
        if (this.launchTimeoutHandler != null) {
            ThreadManager.Handler.removeCallbacks(this.launchTimeoutHandler);
        }
    }

    public void cancelLaunch() {
        this.isLaunching = false;
    }

    protected void startLaunchTimeOut() {
        if (this.launchTimeoutHandler == null) {
            this.launchTimeoutHandler = new Runnable() {
                public void run() {
                    XLELog.Diagnostic("SearchDetailsVM", "LaunchTimeout");
                    if (ViewModelBase.this.isLaunching) {
                        ViewModelBase.this.isLaunching = false;
                        ViewModelBase.this.adapter.updateView();
                    }
                }
            };
        } else {
            ThreadManager.Handler.removeCallbacks(this.launchTimeoutHandler);
        }
        ThreadManager.UIThreadPostDelayed(this.launchTimeoutHandler, (long) LAUNCH_TIME_OUT);
    }

    protected void runLaunchTaskWithConfirmation(long titleId, Runnable launchTask) {
        boolean shouldShowConfirmation;
        long currentTitleId = NowPlayingGlobalModel.getInstance().getCurrentTitleId();
        if (XLEConstants.DASH_TITLE_ID == currentTitleId || currentTitleId == 0 || NowPlayingGlobalModel.getInstance().getNowPlayingState() == NowPlayingState.Disconnected || NowPlayingGlobalModel.getInstance().getNowPlayingState() == NowPlayingState.Connecting) {
            shouldShowConfirmation = false;
        } else {
            shouldShowConfirmation = currentTitleId != titleId;
        }
        if (shouldShowConfirmation) {
            showOkCancelDialog(XLEApplication.Resources.getString(R.string.launch_different_title_warning_message), XLEApplication.Resources.getString(R.string.OK), launchTask, XLEApplication.Resources.getString(R.string.Cancel), null);
        } else {
            launchTask.run();
        }
    }

    protected void launchTitleOnConsoleWithConfirmation(EDSV2Provider provider) {
        boolean isXboxMusicOrVideo = false;
        if (provider == null || provider.getLaunchInfos() == null || provider.getLaunchInfos().size() == 0) {
            XLELog.Warning("ViewModelBase", "Launch request ignored because there is no launch info");
            return;
        }
        SessionModel.getInstance().addObserver(this);
        XboxMobileOmnitureTracking.TrackQuickPlayContentClicked(provider.getName());
        XboxMobileOmnitureTracking.TrackPlayOnXboxClick(Long.toString(provider.getTitleId()));
        final EDSV2PartnerApplicationLaunchInfo launchInfo = (EDSV2PartnerApplicationLaunchInfo) provider.getLaunchInfos().get(0);
        if (provider.getIsXboxMusic() || provider.getIsXboxVideo()) {
            isXboxMusicOrVideo = true;
        }
        runLaunchTaskWithConfirmation(provider.getTitleId(), new Runnable() {
            public void run() {
                ViewModelBase.this.isLaunching = true;
                if (isXboxMusicOrVideo) {
                    ViewModelBase.this.clearAutoLaunchFlagAndLaunchProvider(launchInfo.getTitleId(), launchInfo.getLaunchType().getValue(), launchInfo.getDeepLinkInfo());
                } else {
                    ViewModelBase.this.clearAutoLaunchFlagAndLaunchTitle(launchInfo.getTitleId(), launchInfo.getTitleType().getValue());
                }
                ViewModelBase.this.adapter.updateView();
            }
        });
    }

    protected void clearAutoLaunchFlagAndLaunchProvider(long titleId, int launchType, String deepLink) {
        XLELog.Diagnostic("ViewModelBase", "Launching xbox music app, reset auto launch music activity flag");
        AutoConnectAndLaunchViewModel.getInstance().setDoNoRelaunchXboxMusicActivity(false);
        SessionModel.getInstance().launchProvider(titleId, launchType, deepLink);
    }

    protected void clearAutoLaunchFlagAndLaunchTitle(long titleId, int titleType) {
        boolean z = false;
        XLELog.Diagnostic("ViewModelBase", "Launching xbox music app, reset auto launch music activity flag");
        AutoConnectAndLaunchViewModel.getInstance().setDoNoRelaunchXboxMusicActivity(false);
        XLEGlobalData instance = XLEGlobalData.getInstance();
        if (titleId == XLEConstants.BROWSER_TITLE_ID) {
            z = true;
        }
        instance.setLaunchTitleIsBrowser(z);
        SessionModel.getInstance().launchTitle(titleId, titleType);
    }
}
