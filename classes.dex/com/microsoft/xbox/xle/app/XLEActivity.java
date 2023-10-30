package com.microsoft.xbox.xle.app;

import android.content.res.Configuration;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import com.microsoft.smartglass.R;
import com.microsoft.smartglass.R.dimen;
import com.microsoft.smartglass.R.id;
import com.microsoft.xbox.avatar.model.AvatarRendererModel;
import com.microsoft.xbox.avatar.view.AvatarViewActor;
import com.microsoft.xbox.avatar.view.AvatarViewEditor;
import com.microsoft.xbox.service.model.MessageModel;
import com.microsoft.xbox.service.model.NowPlayingGlobalModel;
import com.microsoft.xbox.service.model.SessionModel;
import com.microsoft.xbox.service.model.VersionModel;
import com.microsoft.xbox.service.network.managers.ServiceCommon;
import com.microsoft.xbox.toolkit.BackgroundThreadWaitor;
import com.microsoft.xbox.toolkit.BackgroundThreadWaitor.BackgroundThreadWaitorChangedCallback;
import com.microsoft.xbox.toolkit.BackgroundThreadWaitor.WaitType;
import com.microsoft.xbox.toolkit.DialogManager;
import com.microsoft.xbox.toolkit.SoundManager;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.XboxAppMeasurement;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.toolkit.locale.XBLLocale;
import com.microsoft.xbox.toolkit.network.XboxLiveEnvironment;
import com.microsoft.xbox.toolkit.system.SystemUtil;
import com.microsoft.xbox.toolkit.ui.ApplicationActivity;
import com.microsoft.xbox.toolkit.ui.NavigationManager;
import com.microsoft.xbox.toolkit.ui.TextureManager;
import com.microsoft.xbox.toolkit.ui.XLEBitmap;
import com.microsoft.xbox.xle.app.activity.ActivityBase;
import com.microsoft.xbox.xle.app.activity.SystemCheckActivity;
import com.microsoft.xbox.xle.app.activity.XboxAuthActivity;
import com.microsoft.xbox.xle.ui.TitleBarView;
import com.microsoft.xbox.xle.viewmodel.AutoConnectAndLaunchViewModel;
import com.microsoft.xbox.xle.viewmodel.XLEGlobalData;
import java.lang.reflect.Field;
import java.util.EnumSet;

public class XLEActivity extends ApplicationActivity {
    protected static final int START_SCREEN_HEIGHT = SystemUtil.getScreenHeight();
    protected static final int START_SCREEN_WIDTH = SystemUtil.getScreenWidth();
    private AvatarViewActor avatarActorFloat;
    private AvatarViewEditor avatarViewFloat;
    private ImageView backgroundImageView;
    private View lastScreen;
    private TitleBarView titleBar;
    private RelativeLayout xleLayout;

    public XLEActivity() {
        this.startupScreenClass = SystemCheckActivity.class;
    }

    public void clearAvatarViewFloat() {
        for (int i = this.xleLayout.getChildCount() - 1; i >= 0; i--) {
            if (this.xleLayout.getChildAt(i) instanceof AvatarViewEditor) {
                this.xleLayout.removeViewAt(i);
            }
        }
    }

    public void resetAvatarViewFloat() {
        clearAvatarViewFloat();
        int appBarHeightPx = XboxApplication.Resources.getDimensionPixelSize(R.dimen.applicationBarHeight);
        LayoutParams avatarViewFloatParams = new LayoutParams(-1, -1);
        avatarViewFloatParams.setMargins(0, 0, 0, appBarHeightPx);
        this.xleLayout.addView(this.avatarViewFloat, avatarViewFloatParams);
    }

    protected void onCreate(Bundle savedInstanceState) {
        XLELog.Diagnostic("ApplicationActivity XLE", "onCreate called. ");
        super.onCreate(savedInstanceState);
        setVolumeControlStream(3);
        SoundManager.getInstance().loadSound(XboxApplication.Instance.getRawRValue("sndbuttonbackandroid"));
        SoundManager.getInstance().loadSound(XboxApplication.Instance.getRawRValue("sndbuttonselectandroid"));
        BackgroundThreadWaitor.getInstance().setChangedCallback(new BackgroundThreadWaitorChangedCallback() {
            public void run(EnumSet<WaitType> blockingTypes, boolean blocking) {
                if (blocking && (blockingTypes.contains(WaitType.Navigation) || blockingTypes.contains(WaitType.PivotScroll))) {
                    AvatarRendererModel.getInstance().setGLThreadRunningAnimation(false);
                } else {
                    AvatarRendererModel.getInstance().setGLThreadRunningAnimation(true);
                }
            }
        });
        AvatarRendererModel.getInstance().glThreadRunningReset();
        this.xleLayout = new RelativeLayout(this);
        super.setContentView(this.xleLayout);
        this.avatarViewFloat = new AvatarViewEditor(this);
        this.avatarViewFloat.setId(R.id.avatar_editor_view_floating);
        this.avatarActorFloat = new AvatarViewActor(this);
        this.avatarActorFloat.setId(R.id.avatar_editor_actor_floating);
        this.avatarViewFloat.addActor(this.avatarActorFloat);
        resetAvatarViewFloat();
        this.backgroundImageView = new ImageView(this);
        updateBGImage();
        this.xleLayout.addView(this.backgroundImageView, -1, -1);
        CookieSyncManager.createInstance(XboxApplication.MainActivity);
        CookieManager.getInstance().removeExpiredCookie();
        CookieManager.getInstance().setAcceptCookie(true);
        this.titleBar = new TitleBarView(this);
        LayoutParams titleBarParams = new LayoutParams(-1, (int) XLEApplication.Resources.getDimension(R.dimen.titleBarHeight));
        titleBarParams.addRule(10);
        this.xleLayout.addView(this.titleBar, titleBarParams);
        ApplicationBarManager.getInstance().onCreate();
        LayoutParams appBarParams = new LayoutParams(-1, -2);
        appBarParams.addRule(12);
        appBarParams.addRule(13);
        appBarParams.addRule(14, -1);
        this.xleLayout.addView(ApplicationBarManager.getInstance().getCollapsedAppBarView(), appBarParams);
        if (ApplicationBarManager.getInstance().getPageIndicator() != null) {
            LayoutParams pageParams = new LayoutParams(-1, -2);
            pageParams.addRule(2, R.id.root_app_bar);
            pageParams.alignWithParent = true;
            pageParams.addRule(14, -1);
            this.xleLayout.addView(ApplicationBarManager.getInstance().getPageIndicator(), pageParams);
        }
        if (!XboxAppMeasurement.getInstance().getIsInitialized()) {
            XboxAppMeasurement.getInstance().initialize(XboxApplication.Instance, XboxLiveEnvironment.Instance().getOmnitureAccount(), XboxLiveEnvironment.Instance().getOmnitureServer());
        }
    }

    protected void onPause() {
        XLELog.Diagnostic("ApplicationActivity XLE", "onPause called. ");
        super.onPause();
        if (this.titleBar != null) {
            this.titleBar.onPause();
        }
        NowPlayingGlobalModel.getInstance().onPause();
        ApplicationBarManager.getInstance().onPause();
        AutoConnectAndLaunchViewModel.getInstance().onPause();
        if (this.avatarViewFloat != null) {
            this.avatarViewFloat.onPause();
        }
        AvatarViewEditor.setNeedDummyViewGLThreadBoot();
        SessionModel.getInstance().leaveSession(false);
    }

    protected void onResume() {
        if (this.avatarViewFloat != null) {
            this.avatarViewFloat.onResume();
        }
        AutoConnectAndLaunchViewModel.getInstance().Init();
        if (XLEGlobalData.getInstance().getIsLoggedIn()) {
            NowPlayingGlobalModel.getInstance().onResume();
            ApplicationBarManager.getInstance().onResume();
            AutoConnectAndLaunchViewModel.getInstance().onResume();
        }
        DeviceCapabilities.getInstance().onResume();
        super.onResume();
    }

    private void updateBGImage() {
        this.backgroundImageView.setScaleType(ScaleType.MATRIX);
        XLEBitmap bitmap = TextureManager.Instance().loadResource(R.drawable.xbox_bg);
        int bitmapw = bitmap.getBitmap().getWidth();
        int bitmaph = bitmap.getBitmap().getHeight();
        Matrix m = new Matrix();
        m.postScale(((float) SystemUtil.getScreenWidth()) / ((float) bitmapw), ((float) SystemUtil.getScreenHeight()) / ((float) bitmaph));
        this.backgroundImageView.setImageMatrix(m);
        TextureManager.Instance().bindToView((int) R.drawable.xbox_bg, this.backgroundImageView, SystemUtil.getScreenWidth(), SystemUtil.getScreenHeight());
    }

    protected void onStop() {
        XLELog.Diagnostic("ApplicationActivity XLE", "onStop called. ");
        super.onStop();
    }

    protected void onBeforeApplicationExit() {
        super.onBeforeApplicationExit();
        NowPlayingGlobalModel.getInstance().onPause();
        AutoConnectAndLaunchViewModel.getInstance().onPause();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        XLELog.Diagnostic("ApplicationActivity XLE", "configuration changed");
        updateBGImage();
        this.xleLayout.requestLayout();
    }

    protected void onDestroy() {
        XLELog.Diagnostic("ApplicationActivity XLE", "onDestroy called. ");
        super.onDestroy();
    }

    public void setContentView(View screen) {
        if (this.lastScreen != screen) {
            if (this.lastScreen != null) {
                this.xleLayout.removeView(this.lastScreen);
            }
            LayoutParams activityParams = new LayoutParams(-1, -1);
            activityParams.addRule(10);
            activityParams.addRule(12);
            this.xleLayout.addView(screen, activityParams);
            this.titleBar.bringToFront();
            ApplicationBarManager.getInstance().getCollapsedAppBarView().bringToFront();
            if (ApplicationBarManager.getInstance().getPageIndicator() != null) {
                ApplicationBarManager.getInstance().getPageIndicator().bringToFront();
            }
            this.lastScreen = screen;
        }
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!(NavigationManager.getInstance().getCurrentActivity() == null || isBlocking())) {
            ApplicationBarManager.getInstance().expandAppBar();
        }
        return super.onPrepareOptionsMenu(menu);
    }

    public boolean onStartOverride(boolean isNewLaunch) {
        XLEGlobalData.getInstance().setAutoLoginStarted(false);
        if (isNewLaunch || !XLEGlobalData.getInstance().getIsLoggedIn()) {
            VersionModel.getInstance().load(false);
            try {
                XBLLocale.getInstance().Initialize(XboxApplication.AssetManager.open("locale/CurrentLocaleMap.xml"));
            } catch (Exception e) {
                XLELog.Diagnostic("failed to open CurrentLocaleMap.xml ", e.toString());
            }
            checkOOBE();
        } else {
            this.startupScreenClass = XboxAuthActivity.class;
            MessageModel.getInstance().loadMessageList(false);
            checkNetworkConnectivity();
        }
        return true;
    }

    public View findViewByString(String viewName) {
        Field field = null;
        try {
            field = id.class.getField(viewName);
        } catch (NoSuchFieldException e) {
        }
        int id = -1;
        if (field != null) {
            try {
                id = field.getInt(null);
            } catch (IllegalAccessException e2) {
            }
        }
        return findViewById(id);
    }

    public int findDimensionIdByName(String name) {
        Field field = null;
        try {
            field = dimen.class.getField(name);
        } catch (NoSuchFieldException e) {
        }
        int id = -1;
        if (field != null) {
            try {
                id = field.getInt(null);
            } catch (IllegalAccessException e2) {
            }
        }
        return id;
    }

    public void setIsTopLevel(boolean isTopLevel) {
        this.titleBar.updateIsTopLevel(isTopLevel);
    }

    public void setShowTitleBar(boolean showTitleBar) {
        this.titleBar.setVisibility(showTitleBar ? 0 : 8);
    }

    public void clearPivotHeaders() {
        this.titleBar.clearHeaders();
    }

    public void addPivotHeader(String headerName, int pivotHeaderIndex, OnClickListener listener) {
        this.titleBar.addHeader(headerName, pivotHeaderIndex, listener);
    }

    public void setActivePivotHeader(String headerName) {
        this.titleBar.setHeaderActive(headerName);
    }

    public void setInactivePivotHeader(String headerName) {
        this.titleBar.setHeaderInactive(headerName);
    }

    public void setPivotTitle(String title) {
        this.titleBar.setTitle(title);
    }

    public AvatarViewEditor getAvatarViewFloat() {
        return this.avatarViewFloat;
    }

    public AvatarViewActor getAvatarActorFloat() {
        return this.avatarActorFloat;
    }

    private void checkOOBE() {
        XLELog.Diagnostic("XLEActivity", "Check OOBE");
        this.startupScreenClass = XboxAuthActivity.class;
    }

    private void checkNetworkConnectivity() {
        try {
            ServiceCommon.checkConnectivity();
        } catch (XLEException ex) {
            if (ex.getErrorCode() == 1) {
                boolean showNoNetworkPopup = true;
                if (NavigationManager.getInstance().getCurrentActivity() instanceof ActivityBase) {
                    showNoNetworkPopup = ((ActivityBase) NavigationManager.getInstance().getCurrentActivity()).getShowNoNetworkPopup();
                }
                if (showNoNetworkPopup) {
                    DialogManager.getInstance().showFatalAlertDialog(XLEApplication.Resources.getString(R.string.dialog_attention_title), XLEApplication.Resources.getString(R.string.dialog_connection_required_description), XLEApplication.Resources.getString(R.string.OK), null);
                }
            }
        }
    }

    public void setRequestedOrientation(int requestedOrientation) {
        if (XLEGlobalData.getInstance().getIsTablet()) {
            super.setRequestedOrientation(6);
        } else {
            super.setRequestedOrientation(requestedOrientation);
        }
    }

    public int getScreenWidth() {
        if (XLEGlobalData.getInstance().getIsTablet()) {
            return START_SCREEN_WIDTH > START_SCREEN_HEIGHT ? START_SCREEN_WIDTH : START_SCREEN_HEIGHT;
        } else {
            return SCREEN_WIDTH;
        }
    }

    public int getScreenHeight() {
        if (XLEGlobalData.getInstance().getIsTablet()) {
            return START_SCREEN_WIDTH > START_SCREEN_HEIGHT ? START_SCREEN_HEIGHT : START_SCREEN_WIDTH;
        } else {
            return SCREEN_HEIGHT;
        }
    }
}
