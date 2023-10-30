package com.microsoft.xbox.xle.test.automator;

import android.app.AlertDialog;
import android.graphics.Rect;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import com.microsoft.xbox.authenticate.LoginModel.LoginState;
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
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.network.AbstractXLEHttpClient;
import com.microsoft.xbox.toolkit.network.XboxLiveEnvironment.Environment;
import com.microsoft.xbox.toolkit.ui.ScreenLayout;
import com.microsoft.xbox.xle.app.ApplicationBarManager.AppBarAnimationState;
import com.microsoft.xbox.xle.app.ApplicationBarManager.AppBarState;
import com.microsoft.xbox.xle.app.activity.ActivityBase;
import com.microsoft.xbox.xle.viewmodel.AdapterBase;
import com.microsoft.xbox.xle.viewmodel.ViewModelBase;
import com.microsoft.xle.test.interop.TestInterop.ServiceManagerActivityStateChange;
import com.microsoft.xle.test.interop.delegates.Action;
import com.microsoft.xle.test.interop.delegates.Action1;
import com.microsoft.xle.test.interop.delegates.Action2;
import com.xbox.avatarrenderer.Core2Renderer.AVATAR_DYNAMIC_COLOR_TYPE;
import java.util.ArrayList;

public interface IAutomator {

    public enum ActivityStateChange {
        Created,
        Started,
        Stopped
    }

    void OnActivityStateChange(ScreenLayout screenLayout, ActivityStateChange activityStateChange);

    void cleanupListenerHooks();

    void clearFPSTracker();

    void clearLPSTracker();

    void clearLastPlayedAnimations(View view);

    void clearLastSentKeys();

    void clearMostRecentlyPlayedSounds();

    boolean expireToken(String str);

    AdapterBase getAdapter(Class<? extends ViewModelBase> cls);

    boolean getAppBarNowPlaying();

    AppBarState getAppBarState();

    boolean getApplicationBlockingTouch();

    boolean getAvatarActorInitialized(View view);

    boolean getAvatarActorIsAssetApplied(View view, String str);

    boolean getAvatarActorIsMale(View view);

    boolean getAvatarActorIsShadowtar(View view);

    int getAvatarEditorColor(AVATAR_DYNAMIC_COLOR_TYPE avatar_dynamic_color_type);

    float getAvatarEditorCurrentRotateAngle();

    boolean getAvatarEditorEyeshadowEnabled();

    float getAvatarEditorHeightFactor();

    boolean getAvatarEditorIsAssetApplied(String str);

    boolean getAvatarEditorIsAssetColorableApplied(String str, int i, int i2, int i3);

    boolean getAvatarEditorIsMale();

    String getAvatarEditorManifest();

    String getAvatarEditorSelectActivityTag(ActivityBase activityBase);

    float getAvatarEditorWeightFactor();

    int getAverageFPS();

    ScreenLayout getCurrentActivity();

    AlertDialog getCurrentDialog();

    <T> ViewModelBase getCurrentViewModel(Class<T> cls);

    Action getDismissKeyboardMethod();

    boolean getGLThreadRunning();

    boolean getIsTablet();

    String[] getLastPlayedAnimations(View view);

    LRCControlKey[] getLastSentKeys();

    int getMaxMemoryLimitKb();

    int getMinFPS();

    Integer[] getMostRecentlyPlayedSounds();

    ArrayList<Integer> getPageStates();

    String getResourceText(int i);

    boolean getTestAllowsAutoConnect(boolean z);

    int getTotalBitmapsCount();

    int getTotalLeakedAdapterCount();

    int getTotalPages();

    int getUsedKb();

    Rect getViewScreenRect(View view);

    void handleThrowable(Throwable th);

    void leaveSession();

    void logOut();

    boolean onShowDialog(String str, String str2);

    boolean onShowError(int i);

    void setAchievementServiceManager(IAchievementServiceManager iAchievementServiceManager);

    void setAdapter(Class<? extends ViewModelBase> cls, AdapterBase adapterBase);

    void setAllowAutoConnect(boolean z);

    void setAllowDismissKeyboardHandler(boolean z);

    void setAppBarAnimationCallback(Action2<AppBarAnimationState, AppBarAnimationState> action2);

    void setAppBarItemHook(Action2<Integer, OnClickListener> action2, Action action);

    void setAppbarAnimationState(AppBarAnimationState appBarAnimationState, AppBarAnimationState appBarAnimationState2);

    void setAutomaticSignin(boolean z, int i, Action2<WebView, Integer> action2);

    void setAvatarClosetServiceManager(IAvatarClosetServiceManager iAvatarClosetServiceManager);

    void setAvatarManifestServiceManager(IAvatarManifestServiceManager iAvatarManifestServiceManager);

    void setCachingEnabled(boolean z);

    void setCompanionSession(ICompanionSession iCompanionSession);

    void setCore2WebAccess(boolean z);

    void setCurrentDialog(String str, AlertDialog alertDialog);

    void setEDSServiceManager(IEDSServiceManager iEDSServiceManager);

    void setEnvironment(Environment environment);

    void setFriendServiceManager(IFriendServiceManager iFriendServiceManager);

    void setGameServiceManager(IGameServiceManager iGameServiceManager);

    void setIsUsingToken(boolean z);

    void setListenerHook(int i, OnClickListener onClickListener);

    void setLogOutOnLaunch(boolean z);

    void setLoginError(boolean z, boolean z2, LoginState loginState, String str);

    void setMembershipLevel(boolean z, boolean z2);

    void setMessageServiceManager(IMessageServiceManager iMessageServiceManager);

    void setMonitorLPS(boolean z);

    void setNoRedirectTestNetworkHttpClient(AbstractXLEHttpClient abstractXLEHttpClient);

    void setNotifyOnActivityStateChange(Action2<ScreenLayout, ActivityStateChange> action2);

    void setNotifyOnServiceManagerActivityStateChange(Action2<String, ServiceManagerActivityStateChange> action2);

    void setOnLoginStateChangedCallback(Action2<String, XLEException> action2);

    void setOnShowDialog(Action2<String, String> action2);

    void setOnShowError(Action1<Integer> action1);

    void setProfileServiceManager(IProfileServiceManager iProfileServiceManager);

    void setSLSServiceManager(ISLSServiceManager iSLSServiceManager);

    void setTakeScreenshot(Action action);

    void setTestNetworkHttpClient(AbstractXLEHttpClient abstractXLEHttpClient);

    void setTestSettings(boolean z);

    void setTestTextureHttpClient(AbstractXLEHttpClient abstractXLEHttpClient);

    void setThrowableHandler(Action1<Throwable> action1);

    void setUseStub(boolean z);

    void setUserIsChild(boolean z, boolean z2);

    void setVersion(int i, int i2, int i3);

    void setVersionToApplicationDefaults();

    <T extends ViewGroup> void setViewModelForActivity(T t, ViewModelBase viewModelBase);

    void takeScreenshot();

    void testTearDown();
}
