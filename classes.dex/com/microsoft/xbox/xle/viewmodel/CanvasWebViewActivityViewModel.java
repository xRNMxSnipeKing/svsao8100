package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.authenticate.XstsToken;
import com.microsoft.xbox.service.model.NowPlayingGlobalModel;
import com.microsoft.xbox.service.model.SessionModel;
import com.microsoft.xbox.service.model.UpdateData;
import com.microsoft.xbox.service.model.edsv2.EDSV2ActivityItem;
import com.microsoft.xbox.service.model.edsv2.EDSV2ActivityProviderPolicy;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaGroup;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaItem;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaType;
import com.microsoft.xbox.service.network.managers.XstsTokenManager;
import com.microsoft.xbox.smartglass.canvas.CanvasComponents;
import com.microsoft.xbox.smartglass.canvas.CanvasTokenManager;
import com.microsoft.xbox.smartglass.canvas.CanvasViewClient;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.ui.NavigationManager;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.app.XLEUtil;
import com.microsoft.xbox.xle.app.XboxMobileOmnitureTracking;
import com.microsoft.xbox.xle.app.activity.CanvasWebViewActivity;
import com.microsoft.xbox.xle.app.activity.SmartGlassActivity;
import com.microsoft.xbox.xle.app.adapter.CanvasWebViewActivityAdapter;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;

public class CanvasWebViewActivityViewModel extends ViewModelBase {
    private static final String CANVAS_STATE_AUTOLAUNCH = XLEApplication.Resources.getString(R.string.canvas_loading_autolaunch);
    private static final String CANVAS_STATE_ERROR = XLEApplication.Resources.getString(R.string.canvas_error);
    private static final int GRACEPERIOD = 3000;
    private static final String MEDIA_QUERY_PARAM_FORMAT = "canonicalId=%s&mediaGroup=%s";
    private EDSV2ActivityItem activityData;
    private EDSV2MediaItem activityParentData;
    private CanvasViewClient canvasClient;
    private CanvasInternalState canvasRealState;
    private boolean isAutoLaunch;
    private boolean isInitialLoad;
    private boolean isLoading;
    private String launchUrl;
    private boolean needToLoadActivity;
    private boolean timedup;
    private XLECanvasTokenManager tokenManager;

    public enum CanvasInternalState {
        Loading,
        Loaded,
        Error
    }

    public enum CanvasViewState {
        Splash,
        Webview
    }

    private class XLECanvasClient extends CanvasViewClient {
        private XLECanvasClient() {
        }

        public void onLoadCompleted(String url) {
            CanvasWebViewActivityViewModel.this.isLoading = false;
            CanvasWebViewActivityViewModel.this.isInitialLoad = false;
            CanvasWebViewActivityViewModel.this.updateCanvasState(CanvasInternalState.Loaded);
        }

        public void onNavigating(String url) {
            CanvasWebViewActivityViewModel.this.isLoading = true;
            if (CanvasWebViewActivityViewModel.this.isInitialLoad) {
                CanvasWebViewActivityViewModel.this.updateCanvasState(CanvasInternalState.Loading);
            }
        }

        public void onNavigationFailed(String url, int errorCode, String description) {
            CanvasWebViewActivityViewModel.this.isLoading = false;
            CanvasWebViewActivityViewModel.this.updateCanvasState(CanvasInternalState.Error);
        }
    }

    private class XLECanvasTokenManager implements CanvasTokenManager {
        private XLECanvasTokenManager() {
        }

        public String getXstsToken(String audienceUri, boolean forceRefresh) throws IOException {
            try {
                XstsToken token = XstsTokenManager.getInstance().getXstsToken(audienceUri, forceRefresh);
                if (token != null) {
                    return token.getToken();
                }
                return null;
            } catch (XLEException e) {
                throw new IOException(e);
            }
        }
    }

    public CanvasWebViewActivityViewModel() {
        this.canvasRealState = CanvasInternalState.Loading;
        this.isAutoLaunch = false;
        this.canvasClient = new XLECanvasClient();
        this.tokenManager = new XLECanvasTokenManager();
        this.isLoading = false;
        this.isInitialLoad = true;
        this.timedup = true;
        this.launchUrl = null;
        this.needToLoadActivity = true;
        this.activityData = XLEGlobalData.getInstance().getSelectedActivityData();
        this.activityParentData = XLEGlobalData.getInstance().getActivityParentMediaItemData();
        this.isAutoLaunch = XLEGlobalData.getInstance().getIsAutoLaunch();
        if (this.isAutoLaunch) {
            XLELog.Diagnostic("CanvasWebViewActivityViewModel", "new instance of canvas for auto launched case");
            this.timedup = false;
        } else {
            XLELog.Diagnostic("CanvasWebViewActivityViewModel", "new instance of canvas for not auto launched case");
            this.timedup = true;
        }
        XLEAssert.assertNotNull(this.activityData);
        XLEAssert.assertNotNull(this.activityData.getActivityLaunchInfo());
        this.adapter = new CanvasWebViewActivityAdapter(this);
    }

    public String getLaunchUrl() {
        return this.launchUrl;
    }

    public boolean drainNeedToLoadActivity() {
        boolean value = this.needToLoadActivity;
        this.needToLoadActivity = false;
        return value;
    }

    private static String getXboxMusicOrVideoLaunchUrl(String launchUrl, String mediaCanonicalId, String mediaGroup) {
        if (JavaUtil.isNullOrEmpty(mediaCanonicalId) || JavaUtil.isNullOrEmpty(mediaGroup)) {
            return launchUrl;
        }
        String newUrl = new String(launchUrl);
        if (launchUrl.contains("?")) {
            newUrl = newUrl + "&";
        } else {
            newUrl = newUrl + "?";
        }
        newUrl = newUrl + String.format(MEDIA_QUERY_PARAM_FORMAT, new Object[]{mediaCanonicalId, mediaGroup});
        XLELog.Diagnostic("Canvas", "Returning modified launch url: " + newUrl);
        return newUrl;
    }

    public CanvasViewState getCanvasState() {
        if (!this.timedup) {
            return CanvasViewState.Splash;
        }
        switch (this.canvasRealState) {
            case Loading:
            case Error:
                return CanvasViewState.Splash;
            default:
                return CanvasViewState.Webview;
        }
    }

    public EnumSet<CanvasComponents> getUsesCapabilities() {
        EnumSet<CanvasComponents> capabilities = EnumSet.noneOf(CanvasComponents.class);
        for (CanvasComponents component : CanvasComponents.values()) {
            if (JavaUtil.containsFlag(this.activityData.getActivityLaunchInfo().getUsesCapabilities(), component.getValue())) {
                capabilities.add(component);
            }
        }
        return capabilities;
    }

    public URI getSplashImageUri() {
        return this.activityData.getSplashImageUrl();
    }

    public String getParentTitle() {
        return this.activityParentData != null ? this.activityParentData.getTitle() : null;
    }

    public String getActivityTitle() {
        return this.activityData.getTitle();
    }

    public CanvasViewClient getCanvasClient() {
        return this.canvasClient;
    }

    public CanvasTokenManager getTokenManager() {
        return this.tokenManager;
    }

    public String getSplashString() {
        if (!this.timedup) {
            return CANVAS_STATE_AUTOLAUNCH;
        }
        if (this.canvasRealState == CanvasInternalState.Loading || this.canvasRealState != CanvasInternalState.Error) {
            return null;
        }
        return CANVAS_STATE_ERROR;
    }

    public boolean getIsRemoteButtonVisibleInAppBar() {
        return this.timedup && this.canvasRealState == CanvasInternalState.Loaded;
    }

    public boolean getShowError() {
        return this.timedup && this.canvasRealState == CanvasInternalState.Error;
    }

    public boolean getCanvasInternalStateIsLoading() {
        return this.timedup && this.canvasRealState == CanvasInternalState.Loading;
    }

    public boolean getIsNowPlayingTileVisibleInAppBar() {
        return getCanvasState() == CanvasViewState.Webview;
    }

    public boolean shouldShowMediaTransport() {
        return NowPlayingGlobalModel.getInstance().isMediaInProgress() && getCanvasState() == CanvasViewState.Webview;
    }

    public boolean isRemoteButtonEnabled() {
        return SessionModel.getInstance().getDisplayedSessionState() == 2;
    }

    public void setInitialLoad() {
        this.isInitialLoad = true;
    }

    public ArrayList<String> getWhitelistUrls() {
        return this.activityData.getActivityLaunchInfo().getWhitelistUrls();
    }

    public ArrayList<Integer> getWhitelistTitleIds() {
        ArrayList<Integer> titleIds = new ArrayList();
        if (this.activityData.getProviderPolicies() != null) {
            Iterator i$ = this.activityData.getProviderPolicies().iterator();
            while (i$.hasNext()) {
                titleIds.add(Integer.valueOf((int) ((EDSV2ActivityProviderPolicy) i$.next()).getTitleId()));
            }
        }
        return titleIds;
    }

    public void updateLaunchUrl() {
        String newUrl = this.activityData.getActivityLaunchInfo().getActivityUrlString();
        if (this.activityParentData != null) {
            if (this.activityData.isXboxMusicActivity()) {
                switch (this.activityParentData.getMediaType()) {
                    case EDSV2MediaType.MEDIATYPE_ALBUM /*1006*/:
                    case EDSV2MediaType.MEDIATYPE_MUSICARTIST /*1009*/:
                        newUrl = getXboxMusicOrVideoLaunchUrl(newUrl, this.activityParentData.getCanonicalId(), EDSV2MediaGroup.MEDIAGROUP_MUSIC_STRING);
                        break;
                }
            }
            if (this.activityData.isXboxVideoActivity()) {
                switch (this.activityParentData.getMediaType()) {
                    case EDSV2MediaType.MEDIATYPE_MOVIE /*1000*/:
                        break;
                    case EDSV2MediaType.MEDIATYPE_TVSHOW /*1002*/:
                    case EDSV2MediaType.MEDIATYPE_TVEPISODE /*1003*/:
                    case EDSV2MediaType.MEDIATYPE_TVSERIES /*1004*/:
                    case EDSV2MediaType.MEDIATYPE_TVSEASON /*1005*/:
                        newUrl = getXboxMusicOrVideoLaunchUrl(newUrl, this.activityParentData.getCanonicalId(), EDSV2MediaGroup.MEDIAGROUP_TV_STRING);
                        break;
                }
                newUrl = getXboxMusicOrVideoLaunchUrl(newUrl, this.activityParentData.getCanonicalId(), EDSV2MediaGroup.MEDIAGROUP_MOVIE_STRING);
            }
        }
        if (this.launchUrl == null || !JavaUtil.stringsEqualCaseInsensitive(this.launchUrl, newUrl)) {
            XLELog.Diagnostic("Canvas", String.format("Need to update launch url old:%s, new %s", new Object[]{this.launchUrl, newUrl}));
            this.launchUrl = newUrl;
            this.needToLoadActivity = true;
        }
    }

    public void onApplicationPause() {
        super.onApplicationPause();
        this.launchUrl = null;
    }

    public void onPause() {
        this.timedup = true;
        super.onPause();
    }

    public void onRehydrate() {
        if (this.adapter == null) {
            this.adapter = new CanvasWebViewActivityAdapter(this);
        }
    }

    public void load(boolean forceRefresh) {
        if (forceRefresh) {
            this.launchUrl = null;
        }
        updateLaunchUrl();
        this.adapter.updateView();
    }

    protected void onStartOverride() {
        XLEGlobalData.getInstance().setSelectedActivityData(this.activityData);
        XLEGlobalData.getInstance().setActivityParentMediaItemData(this.activityParentData);
        XLEGlobalData.getInstance().setIsAutoLaunch(this.isAutoLaunch);
        SessionModel.getInstance().addObserver(this);
        NowPlayingGlobalModel.getInstance().addObserver(this);
        XLEUtil.setKeepScreenOn(isActivityOnlineAndLoaded());
        if (this.isAutoLaunch) {
            XLELog.Diagnostic("CanvasWebViewActivityViewModel", "auto launch is true, post a delay timer");
            ThreadManager.UIThreadPostDelayed(new Runnable() {
                public void run() {
                    XLELog.Diagnostic("CanvasWebViewActivityViewModel", "grace period is up");
                    if (CanvasWebViewActivityViewModel.this.getIsActive()) {
                        NavigationManager.getInstance().RemoveScreensFromBackstack(CanvasWebViewActivity.class);
                        NavigationManager.getInstance().RemoveScreensFromBackstack(SmartGlassActivity.class);
                        CanvasWebViewActivityViewModel.this.timedup = true;
                        CanvasWebViewActivityViewModel.this.adapter.updateView();
                    }
                }
            }, 3000);
        }
    }

    protected void onStopOverride() {
        SessionModel.getInstance().removeObserver(this);
        NowPlayingGlobalModel.getInstance().removeObserver(this);
        XLEUtil.setKeepScreenOn(false);
    }

    public boolean isBusy() {
        return this.isLoading;
    }

    protected void updateOverride(AsyncResult<UpdateData> asyncResult) {
        switch (((UpdateData) asyncResult.getResult()).getUpdateType()) {
            case NowPlayingState:
                XLEUtil.setKeepScreenOn(isActivityOnlineAndLoaded());
                break;
        }
        this.adapter.updateView();
    }

    public void navigateToRemote() {
        navigateToRemote(true);
    }

    public void exit() {
        goBack();
    }

    protected void goBack() {
        if (!this.timedup) {
            XboxMobileOmnitureTracking.TrackCancelAutoPlay(Integer.toString(this.activityData.getMediaType()), this.activityData.getTitle(), this.activityData.getCanonicalId());
        }
        if (this.isAutoLaunch) {
            AutoConnectAndLaunchViewModel.getInstance().setCancelledActivityData(this.activityData);
        }
        super.goBack();
    }

    private void updateCanvasState(CanvasInternalState newState) {
        if (newState != this.canvasRealState) {
            XLELog.Diagnostic("CanvasWebViewActivityViewModel", "update canvas state to " + newState.toString());
            this.canvasRealState = newState;
            this.adapter.updateView();
            XLEUtil.setKeepScreenOn(isActivityOnlineAndLoaded());
            return;
        }
        XLELog.Diagnostic("CanvasWebViewActivityViewModel", "ignore update canvas state to " + newState.toString());
    }

    private boolean isActivityOnlineAndLoaded() {
        if (this.canvasRealState == CanvasInternalState.Error || this.canvasRealState == CanvasInternalState.Loading) {
            return false;
        }
        switch (NowPlayingGlobalModel.getInstance().getNowPlayingState()) {
            case Disconnected:
            case Connecting:
            case ConnectedPlayingDash:
            case ConnectedPlayingDashMedia:
                return false;
            case ConnectedPlayingVideo:
            case ConnectedPlayingMusic:
            case ConnectedPlayingApp:
            case ConnectedPlayingGame:
                if (this.activityData.getMediaType() != 66 && this.activityData.getMediaType() != 67) {
                    return this.activityParentData.equals(NowPlayingGlobalModel.getInstance().getNowPlayingMediaItem());
                }
                if (this.activityData.getAllowedTitleIds() == null) {
                    return false;
                }
                long currentTitleId = NowPlayingGlobalModel.getInstance().getCurrentTitleId();
                Iterator i$ = this.activityData.getAllowedTitleIds().iterator();
                while (i$.hasNext()) {
                    if (currentTitleId == ((long) ((Integer) i$.next()).intValue())) {
                        return true;
                    }
                }
                return false;
            default:
                return false;
        }
    }
}
