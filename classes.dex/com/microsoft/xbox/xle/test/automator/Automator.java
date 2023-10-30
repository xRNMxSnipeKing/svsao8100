package com.microsoft.xbox.xle.test.automator;

import android.app.AlertDialog;
import android.graphics.Rect;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import com.microsoft.xbox.authenticate.LoginModel.LoginState;
import com.microsoft.xbox.avatar.model.AvatarEditorModel;
import com.microsoft.xbox.avatar.model.AvatarRendererModel;
import com.microsoft.xbox.avatar.view.AvatarViewActor;
import com.microsoft.xbox.service.model.LRCControlKey;
import com.microsoft.xbox.service.network.managers.IAchievementServiceManager;
import com.microsoft.xbox.service.network.managers.IAvatarClosetServiceManager;
import com.microsoft.xbox.service.network.managers.IAvatarManifestServiceManager;
import com.microsoft.xbox.service.network.managers.IFriendServiceManager;
import com.microsoft.xbox.service.network.managers.IGameServiceManager;
import com.microsoft.xbox.service.network.managers.IMessageServiceManager;
import com.microsoft.xbox.service.network.managers.IProfileServiceManager;
import com.microsoft.xbox.service.network.managers.xblshared.ICompanionSession;
import com.microsoft.xbox.service.network.managers.xblshared.IEDSServiceManager;
import com.microsoft.xbox.service.network.managers.xblshared.ISLSServiceManager;
import com.microsoft.xbox.service.network.managers.xblshared.XBLSharedServiceManager;
import com.microsoft.xbox.toolkit.FPSTool;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.TimeTool;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.network.AbstractXLEHttpClient;
import com.microsoft.xbox.toolkit.network.XboxLiveEnvironment;
import com.microsoft.xbox.toolkit.network.XboxLiveEnvironment.Environment;
import com.microsoft.xbox.toolkit.ui.ScreenLayout;
import com.microsoft.xbox.xle.app.ApplicationBarManager.AppBarAnimationState;
import com.microsoft.xbox.xle.app.ApplicationBarManager.AppBarState;
import com.microsoft.xbox.xle.app.activity.ActivityBase;
import com.microsoft.xbox.xle.app.activity.AvatarEditorSelectActivity;
import com.microsoft.xbox.xle.test.automator.IAutomator.ActivityStateChange;
import com.microsoft.xbox.xle.viewmodel.AdapterBase;
import com.microsoft.xbox.xle.viewmodel.ViewModelBase;
import com.microsoft.xle.test.interop.CrashReporter;
import com.microsoft.xle.test.interop.TestInterop;
import com.microsoft.xle.test.interop.TestInterop.ServiceManagerActivityStateChange;
import com.microsoft.xle.test.interop.delegates.Action;
import com.microsoft.xle.test.interop.delegates.Action1;
import com.microsoft.xle.test.interop.delegates.Action2;
import com.xbox.avatarrenderer.ASSET_COLOR_TABLE;
import com.xbox.avatarrenderer.Core2Renderer.AVATAR_DYNAMIC_COLOR_TYPE;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

public class Automator implements IAutomator {
    private static Action2<AppBarAnimationState, AppBarAnimationState> appBarAnimationStateChanged = null;
    private static IAutomator instance = null;
    private boolean allowAutoConnect = true;
    private Action2<Integer, OnClickListener> appBarCallback = null;
    private Action cleanupCallback = null;
    private AlertDialog currentDialog = null;
    private Action1<Throwable> handleThrowableMethod = null;
    private boolean logoutDesired = false;
    private Action2<ScreenLayout, ActivityStateChange> notifyActivityStateChangeMethod = null;
    private Action2<String, String> onShowDialogMethod = null;
    private Action1<Integer> onShowErrorMethod = null;
    private Action1<Integer> onShowToastMethod = null;
    private Action takeSnapshotMethod = null;
    private HashMap<Class<? extends ViewModelBase>, WeakReference<AdapterBase>> viewModelAdapterMap = new HashMap();
    private HashMap<String, WeakReference<ViewModelBase>> viewModels = new HashMap();

    private Automator() {
    }

    public static IAutomator getInstance() {
        if (instance == null) {
            instance = new Automator();
        }
        return instance;
    }

    public void setCurrentDialog(String prompt, AlertDialog dialog) {
    }

    public AlertDialog getCurrentDialog() {
        return null;
    }

    public void setThrowableHandler(Action1<Throwable> action) {
        CrashReporter.setThrowableHandler(action);
        this.handleThrowableMethod = action;
    }

    public void handleThrowable(Throwable ex) {
    }

    public void setTakeScreenshot(Action action) {
        this.takeSnapshotMethod = action;
    }

    public void takeScreenshot() {
    }

    public Action getDismissKeyboardMethod() {
        return null;
    }

    public void setAllowDismissKeyboardHandler(boolean allow) {
    }

    public void setLogOutOnLaunch(boolean logoutOnLaunch) {
        this.logoutDesired = logoutOnLaunch;
    }

    public void logOut() {
    }

    public void setUseStub(boolean useStubData) {
        XboxLiveEnvironment.Instance().setStub(useStubData);
    }

    public void setEnvironment(Environment env) {
        XboxLiveEnvironment.Instance().setEnvironment(env);
    }

    public void setIsUsingToken(boolean value) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        XboxLiveEnvironment.Instance().setIsUsingToken(value);
    }

    public void setTestSettings(boolean testEDSStub) {
        XBLSharedServiceManager.setTestSettings(testEDSStub);
    }

    public void setNotifyOnActivityStateChange(Action2<ScreenLayout, ActivityStateChange> activityStateChangeAction) {
        this.notifyActivityStateChangeMethod = activityStateChangeAction;
    }

    public void OnActivityStateChange(ScreenLayout activity, ActivityStateChange change) {
    }

    public <T extends ViewGroup> void setViewModelForActivity(T t, ViewModelBase viewModel) {
    }

    public <T> ViewModelBase getCurrentViewModel(Class<T> cls) {
        return null;
    }

    public void setGameServiceManager(IGameServiceManager serviceManager) {
    }

    public void setMessageServiceManager(IMessageServiceManager serviceManager) {
    }

    public void setAchievementServiceManager(IAchievementServiceManager serviceManager) {
    }

    public void setProfileServiceManager(IProfileServiceManager serviceManager) {
    }

    public void setFriendServiceManager(IFriendServiceManager serviceManager) {
    }

    public void setAvatarManifestServiceManager(IAvatarManifestServiceManager serviceManager) {
    }

    public void setAvatarClosetServiceManager(IAvatarClosetServiceManager serviceManager) {
    }

    public void setEDSServiceManager(IEDSServiceManager serviceManager) {
    }

    public void setCompanionSession(ICompanionSession companionSession) {
    }

    public void setSLSServiceManager(ISLSServiceManager serviceManager) {
    }

    public void setAdapter(Class<? extends ViewModelBase> cls, AdapterBase adapter) {
    }

    public AdapterBase getAdapter(Class<? extends ViewModelBase> cls) {
        return null;
    }

    public void setOnShowError(Action1<Integer> action1) {
    }

    public boolean onShowError(int resourceId) {
        return false;
    }

    public void setOnShowDialog(Action2<String, String> action2) {
    }

    public boolean onShowDialog(String title, String message) {
        return false;
    }

    public ScreenLayout getCurrentActivity() {
        return null;
    }

    public String getResourceText(int resourceId) {
        return null;
    }

    public boolean getApplicationBlockingTouch() {
        return false;
    }

    public String getAvatarEditorSelectActivityTag(ActivityBase a) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        if (a instanceof AvatarEditorSelectActivity) {
            return ((AvatarEditorSelectActivity) a).getViewModel().getTag();
        }
        return null;
    }

    public String getAvatarEditorManifest() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        return AvatarEditorModel.getInstance().getManifestString();
    }

    public int getAvatarEditorColor(AVATAR_DYNAMIC_COLOR_TYPE type) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        return AvatarEditorModel.getInstance().getManifestEditor().getAvatarColor(type);
    }

    public boolean getAvatarEditorEyeshadowEnabled() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        return AvatarEditorModel.getInstance().getManifestEditor().getEyeShadowsEnabled().booleanValue();
    }

    public float getAvatarEditorHeightFactor() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        return AvatarEditorModel.getInstance().getManifestEditor().getHeightFactor();
    }

    public float getAvatarEditorWeightFactor() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        return AvatarEditorModel.getInstance().getManifestEditor().getWeightFactor();
    }

    public boolean getAvatarEditorIsAssetApplied(String assetGuid) {
        return false;
    }

    public boolean getAvatarActorIsAssetApplied(View actorView, String assetGuid) {
        return false;
    }

    public boolean getAvatarEditorIsAssetColorableApplied(String assetGuid, int color1, int color2, int color3) {
        ASSET_COLOR_TABLE colorTable = new ASSET_COLOR_TABLE(color1, color2, color3);
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        return AvatarEditorModel.getInstance().getManifestEditor().isAssetPresent(assetGuid, colorTable).booleanValue();
    }

    public void setCore2WebAccess(boolean enabled) {
    }

    public void clearLastPlayedAnimations(View view) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        ((AvatarViewActor) view).clearLastPlayedAnimations();
    }

    public String[] getLastPlayedAnimations(View view) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        return ((AvatarViewActor) view).getLastPlayedAnimations();
    }

    public boolean getAvatarEditorIsMale() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        return AvatarEditorModel.getInstance().isMale();
    }

    public boolean getAvatarActorIsMale(View view) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        return ((AvatarViewActor) view).getIsMale();
    }

    public boolean getAvatarActorInitialized(View view) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        return ((AvatarViewActor) view).getIsLoaded();
    }

    public boolean getAvatarActorIsShadowtar(View view) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        return ((AvatarViewActor) view).getIsShadowtarVisible();
    }

    public float getAvatarEditorCurrentRotateAngle() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        return AvatarEditorModel.getInstance().getAvatarActorVM().getCurrentRotation();
    }

    public boolean getGLThreadRunning() {
        return AvatarRendererModel.getInstance().getGLThreadRunning();
    }

    private Boolean AutoLoginManualOverride() {
        return Boolean.valueOf(false);
    }

    public void setMembershipLevel(boolean overrideMembershipLevel, boolean isGold) {
    }

    public void setNotifyOnServiceManagerActivityStateChange(Action2<String, ServiceManagerActivityStateChange> action2) {
    }

    public void setOnLoginStateChangedCallback(Action2<String, XLEException> action2) {
    }

    public void setLoginError(boolean overrideState, boolean errorOnPageStart, LoginState errorState, String page) {
        TestInterop.setLoginError(overrideState, errorOnPageStart, errorState.toString(), page);
    }

    public void setUserIsChild(boolean enableOverride, boolean isChildAccount) {
    }

    public void testTearDown() {
        ThreadManager.UIThreadSend(new Runnable() {
            public void run() {
                AvatarRendererModel.getInstance().testTearDown();
            }
        });
    }

    public void setAppBarAnimationCallback(Action2<AppBarAnimationState, AppBarAnimationState> action2) {
    }

    public void setAppbarAnimationState(AppBarAnimationState currentState, AppBarAnimationState desiredState) {
    }

    public Rect getViewScreenRect(View view) {
        boolean z;
        if (Thread.currentThread() == ThreadManager.UIThread) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        int[] locationOnScreen = new int[2];
        view.getLocationOnScreen(locationOnScreen);
        int x = locationOnScreen[0];
        int y = locationOnScreen[1];
        return new Rect(x, y, view.getWidth() + x, view.getHeight() + y);
    }

    public int getTotalPages() {
        return 0;
    }

    public ArrayList<Integer> getPageStates() {
        return null;
    }

    public void clearFPSTracker() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        FPSTool.getInstance().clearFPS();
    }

    public int getAverageFPS() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        return FPSTool.getInstance().getAverageFPS();
    }

    public int getMinFPS() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        return FPSTool.getInstance().getMinFPS();
    }

    public void setMonitorLPS(boolean monitor) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        TestInterop.setMonitorLPS(monitor);
    }

    public void clearLPSTracker() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        TimeTool.getInstance().clear();
    }

    public void setVersion(int minVersionRequired, int latestVersionAvailable, int currentVersion) {
    }

    public void setVersionToApplicationDefaults() {
    }

    public void setTestNetworkHttpClient(AbstractXLEHttpClient httpClient) {
    }

    public void setNoRedirectTestNetworkHttpClient(AbstractXLEHttpClient httpClient) {
    }

    public void setTestTextureHttpClient(AbstractXLEHttpClient httpClient) {
    }

    public boolean expireToken(String audienceUri) {
        return false;
    }

    public Integer[] getMostRecentlyPlayedSounds() {
        return null;
    }

    public void clearMostRecentlyPlayedSounds() {
    }

    public int getTotalLeakedAdapterCount() {
        return 0;
    }

    public int getTotalBitmapsCount() {
        return 0;
    }

    public void setCachingEnabled(boolean enabled) {
    }

    public int getUsedKb() {
        return 0;
    }

    public int getMaxMemoryLimitKb() {
        return 0;
    }

    public void setAutomaticSignin(boolean automaticSignin, int userIndex, Action2<WebView, Integer> signInDelegate) {
        TestInterop.setAutomaticSignin(automaticSignin, userIndex, signInDelegate);
    }

    public AppBarState getAppBarState() {
        return null;
    }

    public boolean getAppBarNowPlaying() {
        return false;
    }

    public LRCControlKey[] getLastSentKeys() {
        return null;
    }

    public void clearLastSentKeys() {
    }

    public void leaveSession() {
    }

    public void setAllowAutoConnect(boolean allow) {
    }

    public boolean getTestAllowsAutoConnect(boolean original) {
        return original;
    }

    public void setAppBarItemHook(Action2<Integer, OnClickListener> action2, Action cleanup) {
    }

    public void setListenerHook(int id, OnClickListener listener) {
    }

    public void cleanupListenerHooks() {
    }

    public boolean getIsTablet() {
        return false;
    }
}
