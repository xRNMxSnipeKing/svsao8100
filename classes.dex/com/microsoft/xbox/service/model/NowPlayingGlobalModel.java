package com.microsoft.xbox.service.model;

import com.microsoft.xbox.service.model.edsv2.EDSV2ActivityItem;
import com.microsoft.xbox.service.model.edsv2.EDSV2AppMediaItem;
import com.microsoft.xbox.service.model.edsv2.EDSV2GameMediaItem;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaItem;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaItemDetailModel;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaItemModel;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaType;
import com.microsoft.xbox.service.model.edsv2.EDSV2MusicTrackMediaItemWithAlbum;
import com.microsoft.xbox.service.model.edsv2.EDSV2NowPlayingDetailModel;
import com.microsoft.xbox.service.model.edsv2.EDSV2Provider;
import com.microsoft.xbox.service.model.edsv2.EDSV2TVEpisodeMediaItem;
import com.microsoft.xbox.service.model.sls.Title;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.MediaProgressTimer;
import com.microsoft.xbox.toolkit.MediaProgressTimer.OnMediaProgressUpdatedListener;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEConstants;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.XLEObservable;
import com.microsoft.xbox.toolkit.XLEObserver;
import com.microsoft.xbox.toolkit.XboxApplication;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import org.codehaus.jackson.util.MinimalPrettyPrinter;

public class NowPlayingGlobalModel extends XLEObservable<UpdateData> implements XLEObserver<UpdateData> {
    private static final String COMMA_DELIMITER = XboxApplication.Resources.getString(XboxApplication.Instance.getStringRValue("comma_delimiter"));
    private static final int QUICKPLAY_REFRESH_DELAY_MS = 30000;
    private static final int UPDATE_DELAY_BETWEEN_TRACKS = 3000;
    private static NowPlayingGlobalModel instance = new NowPlayingGlobalModel();
    private MediaTitleState currentMediaState;
    private String currentNowPlayingIdentifier;
    private QuickplayModel currentQuickplayModel;
    private long currentTitleId;
    private ActivitySummaryModel lastPlayedTitleActivityModel;
    private EDSV2MediaItemDetailModel lastPlayedTitleModel;
    private ActivitySummaryModel nowPlayingAppActivityModel;
    private EDSV2NowPlayingDetailModel nowPlayingAppModel;
    private ActivitySummaryModel nowPlayingMediaActivityModel;
    private EDSV2NowPlayingDetailModel nowPlayingMediaModel;
    private NowPlayingState nowPlayingState = NowPlayingState.Disconnected;
    private boolean pendingLoadingNowPlayingModel;
    private EDSV2NowPlayingDetailModel pendingMusicDetailModel;
    private MediaProgressTimer timer = new MediaProgressTimer();

    public enum NowPlayingState {
        Disconnected,
        Connecting,
        ConnectedPlayingDash,
        ConnectedPlayingDashMedia,
        ConnectedPlayingVideo,
        ConnectedPlayingMusic,
        ConnectedPlayingApp,
        ConnectedPlayingGame
    }

    private NowPlayingGlobalModel() {
    }

    public static NowPlayingGlobalModel getInstance() {
        return instance;
    }

    public NowPlayingState getNowPlayingState() {
        return this.nowPlayingState;
    }

    public URI getImageUri() {
        switch (this.nowPlayingState) {
            case ConnectedPlayingApp:
            case ConnectedPlayingGame:
                if (this.nowPlayingAppModel != null) {
                    return this.nowPlayingAppModel.getImageUrl();
                }
                return null;
            case ConnectedPlayingVideo:
            case ConnectedPlayingMusic:
                if (this.nowPlayingMediaModel != null) {
                    return this.nowPlayingMediaModel.getImageUrl();
                }
                return null;
            case Connecting:
            case Disconnected:
                return this.lastPlayedTitleModel != null ? this.lastPlayedTitleModel.getImageUrl() : null;
            default:
                return null;
        }
    }

    public URI getAppBarNowPlayingImageUri() {
        switch (this.nowPlayingState) {
            case Connecting:
            case Disconnected:
                return null;
            default:
                return getImageUri();
        }
    }

    public int getDefaultResourceId() {
        switch (this.nowPlayingState) {
            case ConnectedPlayingApp:
                return XboxApplication.Instance.getDrawableRValue("app_missing");
            case ConnectedPlayingGame:
                return XboxApplication.Instance.getDrawableRValue("game_missing");
            case ConnectedPlayingVideo:
                return XboxApplication.Instance.getDrawableRValue("movie_missing");
            case ConnectedPlayingMusic:
                return XboxApplication.Instance.getDrawableRValue("music_missing");
            case ConnectedPlayingDash:
            case ConnectedPlayingDashMedia:
                return XboxApplication.Instance.getDrawableRValue("dash_now_playing_tile");
            default:
                return XboxApplication.Instance.getDrawableRValue("unknown_missing");
        }
    }

    public int getAppBarNowPlayingDefaultRid() {
        switch (this.nowPlayingState) {
            case Connecting:
                return XboxApplication.Instance.getDrawableRValue("connected_appbar_icon");
            case Disconnected:
                return XboxApplication.Instance.getDrawableRValue("disconnected_appbar_icon");
            default:
                return getDefaultResourceId();
        }
    }

    public EDSV2MediaItemDetailModel getCurrentDetailModel() {
        switch (this.nowPlayingState) {
            case ConnectedPlayingVideo:
            case ConnectedPlayingMusic:
                XLELog.Diagnostic("NowPlayingGlobalModel", "returning MediaModel");
                return this.nowPlayingMediaModel;
            case Connecting:
            case Disconnected:
                XLELog.Diagnostic("NowPlayingGlobalModel", "returning last played title model");
                return this.lastPlayedTitleModel;
            default:
                XLELog.Diagnostic("NowPlayingGlobalModel", "returning AppModel");
                return this.nowPlayingAppModel;
        }
    }

    public String getHeader() {
        switch (this.nowPlayingState) {
            case ConnectedPlayingApp:
            case ConnectedPlayingGame:
                if (this.nowPlayingAppModel != null) {
                    return this.nowPlayingAppModel.getTitle();
                }
                return null;
            case ConnectedPlayingVideo:
            case ConnectedPlayingMusic:
                if (this.nowPlayingMediaModel != null) {
                    return this.nowPlayingMediaModel.getTitle();
                }
                return null;
            case Connecting:
            case Disconnected:
                return this.lastPlayedTitleModel != null ? this.lastPlayedTitleModel.getTitle() : null;
            case ConnectedPlayingDash:
                if (this.currentTitleId == XLEConstants.DASH_TITLE_ID) {
                    return XboxApplication.Resources.getString(XboxApplication.Instance.getStringRValue("now_playing_home_dash"));
                }
                if (this.pendingLoadingNowPlayingModel || (this.nowPlayingAppModel != null && this.nowPlayingAppModel.getIsLoading())) {
                    return XboxApplication.Resources.getString(XboxApplication.Instance.getStringRValue("loading"));
                }
                return XboxApplication.Resources.getString(XboxApplication.Instance.getStringRValue("list_empty"));
            case ConnectedPlayingDashMedia:
                return XboxApplication.Resources.getString(XboxApplication.Instance.getStringRValue("now_playing_home_dash_media"));
            default:
                return null;
        }
    }

    public String getSubHeader() {
        switch (this.nowPlayingState) {
            case ConnectedPlayingGame:
                if (this.nowPlayingAppModel == null || !(getNowPlayingMediaItem() instanceof EDSV2GameMediaItem)) {
                    return null;
                }
                EDSV2GameMediaItem item = (EDSV2GameMediaItem) getNowPlayingMediaItem();
                return JavaUtil.concatenateStringsWithDelimiter(item.getDeveloper(), item.getReleaseDate() != null ? new SimpleDateFormat("yyyy").format(item.getReleaseDate()) : "", null, COMMA_DELIMITER, false);
            case ConnectedPlayingVideo:
                if (this.nowPlayingMediaModel == null || !(getNowPlayingMediaItem() instanceof EDSV2TVEpisodeMediaItem)) {
                    return null;
                }
                EDSV2TVEpisodeMediaItem item2 = (EDSV2TVEpisodeMediaItem) getNowPlayingMediaItem();
                if (item2.getMediaType() != EDSV2MediaType.MEDIATYPE_TVSHOW) {
                    return JavaUtil.concatenateStringsWithDelimiter(XboxApplication.Resources.getString(XboxApplication.Instance.getStringRValue("tv_series_details_season")) + MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR + item2.getSeasonNumber(), XboxApplication.Resources.getString(XboxApplication.Instance.getStringRValue("tv_season_details_episode")) + MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR + item2.getEpisodeNumber(), null, COMMA_DELIMITER, false);
                }
                return null;
            case ConnectedPlayingMusic:
                if (this.nowPlayingMediaModel == null || !(getNowPlayingMediaItem() instanceof EDSV2MusicTrackMediaItemWithAlbum)) {
                    return null;
                }
                return ((EDSV2MusicTrackMediaItemWithAlbum) getNowPlayingMediaItem()).getArtistName();
            default:
                return null;
        }
    }

    public String getDescription() {
        switch (this.nowPlayingState) {
            case ConnectedPlayingApp:
            case ConnectedPlayingGame:
                if (this.nowPlayingAppModel != null) {
                    return this.nowPlayingAppModel.getDescription();
                }
                return null;
            case ConnectedPlayingVideo:
            case ConnectedPlayingMusic:
                if (this.nowPlayingMediaModel != null) {
                    return this.nowPlayingMediaModel.getDescription();
                }
                return null;
            case Connecting:
            case Disconnected:
                return this.lastPlayedTitleModel != null ? this.lastPlayedTitleModel.getDescription() : null;
            default:
                return null;
        }
    }

    public String getCanonicalId() {
        switch (this.nowPlayingState) {
            case ConnectedPlayingApp:
            case ConnectedPlayingGame:
                if (this.nowPlayingAppModel != null) {
                    return this.nowPlayingAppModel.getCanonicalId();
                }
                return null;
            case ConnectedPlayingVideo:
            case ConnectedPlayingMusic:
                if (this.nowPlayingMediaModel != null) {
                    return this.nowPlayingMediaModel.getCanonicalId();
                }
                return null;
            case Connecting:
            case Disconnected:
                return this.lastPlayedTitleModel != null ? this.lastPlayedTitleModel.getCanonicalId() : null;
            default:
                return null;
        }
    }

    public String getProviderName() {
        return this.nowPlayingAppModel != null ? this.nowPlayingAppModel.getTitle() : null;
    }

    public EDSV2ActivityItem getHeroActivity() {
        switch (this.nowPlayingState) {
            case ConnectedPlayingApp:
            case ConnectedPlayingGame:
                return getNowPlayingHeroAppActivity();
            case ConnectedPlayingVideo:
                return getHeroVideoActivity();
            case ConnectedPlayingMusic:
                return getHeroMusicActivity();
            case Connecting:
            case Disconnected:
                return getLastPlayedHeroAppActivity();
            default:
                XLELog.Diagnostic("NowPlayingGlobalModel", "updateHeroActivity: Current now playing state doesn't support activities: " + this.nowPlayingState.toString());
                return null;
        }
    }

    public EDSV2MediaItem getNowPlayingMediaItem() {
        switch (this.nowPlayingState) {
            case ConnectedPlayingApp:
            case ConnectedPlayingGame:
                if (this.nowPlayingAppModel != null) {
                    return this.nowPlayingAppModel.getMediaItemDetailData();
                }
                return null;
            case ConnectedPlayingVideo:
            case ConnectedPlayingMusic:
                if (this.nowPlayingMediaModel != null) {
                    return this.nowPlayingMediaModel.getMediaItemDetailData();
                }
                return null;
            case Disconnected:
                return this.lastPlayedTitleModel != null ? this.lastPlayedTitleModel.getMediaItemDetailData() : null;
            default:
                return null;
        }
    }

    public boolean getIsLoading() {
        boolean loadingAppModel;
        if (this.nowPlayingAppModel == null || !this.nowPlayingAppModel.getIsLoading()) {
            loadingAppModel = false;
        } else {
            loadingAppModel = true;
        }
        boolean loadingMediaModel;
        if (this.nowPlayingMediaModel == null || !this.nowPlayingMediaModel.getIsLoading()) {
            loadingMediaModel = false;
        } else {
            loadingMediaModel = true;
        }
        boolean loadingAppActivityModel;
        if (this.nowPlayingAppActivityModel == null || !this.nowPlayingAppActivityModel.getIsLoading()) {
            loadingAppActivityModel = false;
        } else {
            loadingAppActivityModel = true;
        }
        boolean loadingMediaActivityModel;
        if (this.nowPlayingMediaActivityModel == null || !this.nowPlayingMediaActivityModel.getIsLoading()) {
            loadingMediaActivityModel = false;
        } else {
            loadingMediaActivityModel = true;
        }
        if (loadingAppModel || loadingMediaModel || loadingAppActivityModel || loadingMediaActivityModel) {
            return true;
        }
        return false;
    }

    public String getCurrentNowPlayingIdentifier() {
        return this.currentNowPlayingIdentifier;
    }

    public long getCurrentTitleId() {
        return this.currentTitleId;
    }

    public MediaTitleState getCurrentMediaState() {
        return this.currentMediaState;
    }

    public boolean isConnectedToConsole() {
        return SessionModel.getInstance().getDisplayedSessionState() == 2;
    }

    public boolean isMediaInProgress() {
        switch (this.nowPlayingState) {
            case ConnectedPlayingApp:
            case ConnectedPlayingVideo:
            case ConnectedPlayingMusic:
            case ConnectedPlayingDashMedia:
                return SessionModel.getInstance().getCurrentMediaState() != null && SessionModel.getInstance().getCurrentMediaState().isMediaInProgress();
            default:
                return false;
        }
    }

    public boolean isMediaPaused() {
        MediaTitleState mediaState = SessionModel.getInstance().getCurrentMediaState();
        return mediaState != null && mediaState.getTransportState() == 4;
    }

    public boolean isMediaItemNowPlaying(String canonicalId) {
        return JavaUtil.stringsEqualNonNullCaseInsensitive(canonicalId, this.currentNowPlayingIdentifier);
    }

    public boolean isAppNowPlaying(long titleid) {
        return SessionModel.getInstance().getCurrentTitleId() == titleid;
    }

    public boolean isAppPlayingMedia(long titleid) {
        boolean z;
        String str = "Use isDashPlayingMedia for dash";
        if (titleid != XLEConstants.DASH_TITLE_ID) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(str, z);
        return isAppNowPlaying(titleid) && SessionModel.getInstance().getCurrentMediaState() != null && SessionModel.getInstance().getCurrentMediaState().isMediaInProgress();
    }

    public boolean isDashPlayingMedia() {
        return SessionModel.getInstance().getCurrentTitleId() == XLEConstants.DASH_TITLE_ID && SessionModel.getInstance().getCurrentMediaState() != null && SessionModel.getInstance().getCurrentMediaState().isMediaInProgress();
    }

    public void setOnMediaProgressUpdatedRunnable(OnMediaProgressUpdatedListener listener) {
        this.timer.setOnPositionUpdatedRunnable(listener);
        if (listener == null) {
            this.timer.stop();
        } else {
            this.timer.start();
        }
    }

    public long getMediaDurationInSeconds() {
        return this.timer.getDurationInSeconds();
    }

    public boolean shouldShowController() {
        switch (this.nowPlayingState) {
            case ConnectedPlayingApp:
                if (this.nowPlayingAppModel == null || this.nowPlayingAppModel.isGameType() || getNowPlayingHeroAppActivity() != null) {
                    return false;
                }
                return true;
            case ConnectedPlayingVideo:
                if (this.nowPlayingMediaModel == null || getHeroVideoActivity() != null) {
                    return false;
                }
                return true;
            case ConnectedPlayingMusic:
                return true;
            case ConnectedPlayingDash:
            case ConnectedPlayingDashMedia:
                return true;
            default:
                return false;
        }
    }

    public void load(boolean refreshSessionData) {
        if (refreshSessionData) {
            notifyObservers(new AsyncResult(new UpdateData(UpdateType.NowPlayingState, true), this, null));
        }
    }

    public void refreshDetailModels(boolean forceRefresh) {
        if (this.nowPlayingAppActivityModel != null) {
            this.nowPlayingAppActivityModel.load(forceRefresh);
        }
        if (this.nowPlayingAppModel != null) {
            this.nowPlayingAppModel.load(forceRefresh);
        }
        if (this.nowPlayingMediaActivityModel != null) {
            this.nowPlayingMediaActivityModel.load(forceRefresh);
        }
        if (this.nowPlayingMediaModel != null) {
            this.nowPlayingMediaModel.load(forceRefresh);
        }
        if (this.currentQuickplayModel != QuickplayModel.getInstance()) {
            this.currentQuickplayModel = QuickplayModel.getInstance();
            QuickplayModel.getInstance().addObserver(this);
        }
        QuickplayModel.getInstance().load(forceRefresh);
    }

    public void onResume() {
        SessionModel.getInstance().addObserver(this);
        QuickplayModel.getInstance().addObserver(this);
        QuickplayModel.getInstance().load(false);
        this.nowPlayingState = NowPlayingState.Connecting;
    }

    public void onPause() {
        SessionModel.getInstance().removeObserver(this);
        QuickplayModel.getInstance().removeObserver(this);
        resetAllNowPlayingData();
        if (this.lastPlayedTitleModel != null) {
            this.lastPlayedTitleModel.removeObserver(this);
            this.lastPlayedTitleModel = null;
        }
        this.nowPlayingState = NowPlayingState.Disconnected;
    }

    public void update(AsyncResult<UpdateData> asyncResult) {
        boolean z = false;
        UpdateType type = ((UpdateData) asyncResult.getResult()).getUpdateType();
        boolean isFinal = ((UpdateData) asyncResult.getResult()).getIsFinal();
        XLEException exception = asyncResult.getException();
        if (isFinal) {
            XLELog.Diagnostic("NowPlayingGlobalModel", "Received update: " + type.toString());
            switch (type) {
                case SessionState:
                    updateNowPlayingModels();
                    updateNowPlayingState();
                    notifyObservers(new AsyncResult(new UpdateData(UpdateType.NowPlayingState, isFinal), this, exception));
                    return;
                case MediaItemDetail:
                    if (this.pendingMusicDetailModel != null && isFinal && this.pendingMusicDetailModel.equals(asyncResult.getSender())) {
                        XLELog.Diagnostic("NowPlayingGlobalModel", "Pending music model update is received.");
                        this.pendingMusicDetailModel.removeObserver(this);
                        this.pendingMusicDetailModel = null;
                        SessionModel.getInstance().getMediaTitleState();
                        this.currentMediaState = SessionModel.getInstance().getCurrentMediaState();
                        return;
                    }
                    updateActivityModels(asyncResult.getSender());
                    updateNowPlayingState();
                    boolean appModelLoading = this.nowPlayingAppModel == null ? false : this.nowPlayingAppModel.getIsLoading();
                    boolean mediaModelLoading = this.nowPlayingMediaModel == null ? false : this.nowPlayingMediaModel.getIsLoading();
                    UpdateType updateType = UpdateType.NowPlayingDetail;
                    if (!(appModelLoading || mediaModelLoading)) {
                        z = true;
                    }
                    notifyObservers(new AsyncResult(new UpdateData(updateType, z), this, exception));
                    return;
                case ActivitiesSummary:
                    notifyObservers(new AsyncResult(new UpdateData(UpdateType.NowPlayingHeroActivity, isFinal), this, exception));
                    return;
                case MediaItemDetailRelated:
                    notifyObservers(new AsyncResult(new UpdateData(UpdateType.NowPlayingRelated, isFinal), this, exception));
                    return;
                case RecentsData:
                    updateLastPlayedTitleModel();
                    notifyObservers(new AsyncResult(new UpdateData(UpdateType.NowPlayingQuickplay, isFinal), this, exception));
                    return;
                default:
                    XLELog.Warning("NowPlayingGlobalModel", "ignores this update type");
                    return;
            }
        }
        switch (type) {
            case SessionState:
                updateNowPlayingState();
                notifyObservers(new AsyncResult(new UpdateData(UpdateType.NowPlayingState, isFinal), this, exception));
                return;
            case MediaItemDetailRelated:
                notifyObservers(new AsyncResult(new UpdateData(UpdateType.NowPlayingRelated, isFinal), this, exception));
                return;
            case RecentsData:
                notifyObservers(new AsyncResult(new UpdateData(UpdateType.NowPlayingQuickplay, isFinal), this, exception));
                return;
            default:
                return;
        }
    }

    private void updateNowPlayingState() {
        int sessionState = SessionModel.getInstance().getDisplayedSessionState();
        String newNowPlayingIdentifier = null;
        boolean shouldRefreshQuickplay = false;
        NowPlayingState newState = NowPlayingState.Disconnected;
        switch (sessionState) {
            case 0:
            case 3:
                newState = NowPlayingState.Disconnected;
                if (this.nowPlayingState != newState) {
                    shouldRefreshQuickplay = true;
                    break;
                }
                break;
            case 1:
                newState = NowPlayingState.Connecting;
                break;
            case 2:
                if (this.currentTitleId != XLEConstants.DASH_TITLE_ID) {
                    if (this.currentTitleId != XLEConstants.AVATAR_EDITOR_TITLE_ID) {
                        if (this.currentMediaState == null || !this.currentMediaState.isMediaInProgress() || this.nowPlayingMediaModel == null || JavaUtil.isNullOrEmpty(this.nowPlayingMediaModel.getTitle())) {
                            if (this.nowPlayingAppModel != null && !JavaUtil.isNullOrEmpty(this.nowPlayingAppModel.getTitle())) {
                                if (this.nowPlayingAppModel.getMediaType() == 61) {
                                    newState = NowPlayingState.ConnectedPlayingApp;
                                } else {
                                    newState = NowPlayingState.ConnectedPlayingGame;
                                }
                                newNowPlayingIdentifier = this.nowPlayingAppModel.getCanonicalId();
                                break;
                            }
                            XLELog.Diagnostic("NowPlayingGlobalModel", "titleId failed to load, treated as dash " + this.currentTitleId);
                            newState = NowPlayingState.ConnectedPlayingDash;
                            break;
                        }
                        newNowPlayingIdentifier = this.nowPlayingMediaModel.getCanonicalId();
                        if (!ActivityUtil.isValidMediaTypeForActivity(this.nowPlayingMediaModel.getMediaType())) {
                            newState = NowPlayingState.ConnectedPlayingMusic;
                            if (this.nowPlayingMediaModel.getMediaItemDetailData() instanceof EDSV2MusicTrackMediaItemWithAlbum) {
                                newNowPlayingIdentifier = ((EDSV2MusicTrackMediaItemWithAlbum) this.nowPlayingMediaModel.getMediaItemDetailData()).getAlbumCanonicalId();
                                break;
                            }
                        }
                        newState = NowPlayingState.ConnectedPlayingVideo;
                        break;
                    }
                    newState = NowPlayingState.ConnectedPlayingDash;
                    break;
                } else if (!isDashPlayingMedia()) {
                    newState = NowPlayingState.ConnectedPlayingDash;
                    break;
                } else {
                    newState = NowPlayingState.ConnectedPlayingDashMedia;
                    break;
                }
                break;
            default:
                if (SessionModel.getInstance().getIsConnecting()) {
                    newState = NowPlayingState.Connecting;
                    break;
                }
                break;
        }
        this.currentNowPlayingIdentifier = newNowPlayingIdentifier;
        if (newState == NowPlayingState.Disconnected) {
            resetAllNowPlayingData();
        }
        if (this.nowPlayingState != newState) {
            XLELog.Diagnostic("NowPlayingGlobalModel", "Old now playing state: " + this.nowPlayingState.toString());
            this.nowPlayingState = newState;
            XLELog.Diagnostic("NowPlayingGlobalModel", "New now playing state: " + this.nowPlayingState.toString());
            if (shouldRefreshQuickplay) {
                ThreadManager.UIThreadPost(new Runnable() {
                    public void run() {
                        QuickplayModel.getInstance().load(false);
                    }
                });
            }
        }
    }

    private void updateLastPlayedTitleModel() {
        long lastPlayedTitleId = QuickplayModel.getInstance().getLastPlayedTitle() != null ? QuickplayModel.getInstance().getLastPlayedTitle().getTitleId() : 0;
        if (!(this.lastPlayedTitleModel == null || this.lastPlayedTitleModel.getTitleId() == lastPlayedTitleId)) {
            XLELog.Diagnostic("NowPlayingGlobalModel", "last played app detail model is reset");
            this.lastPlayedTitleModel.removeObserver(this);
            this.lastPlayedTitleModel = null;
        }
        if (this.lastPlayedTitleModel == null && isValidTitleIdForDetail(lastPlayedTitleId)) {
            XLELog.Diagnostic("NowPlayingGlobalModel", "new last played title model");
            Title lastPlayedTitle = QuickplayModel.getInstance().getLastPlayedTitle();
            if (lastPlayedTitle.IsGame()) {
                this.lastPlayedTitleModel = (EDSV2MediaItemDetailModel) EDSV2MediaItemModel.getModel(new EDSV2GameMediaItem(lastPlayedTitle));
            } else {
                this.lastPlayedTitleModel = (EDSV2MediaItemDetailModel) EDSV2MediaItemModel.getModel(new EDSV2AppMediaItem(lastPlayedTitle));
            }
            if (this.nowPlayingAppModel == null || !(this.nowPlayingAppModel == null || this.nowPlayingAppModel.equals(this.lastPlayedTitleModel))) {
                this.lastPlayedTitleModel.addObserver(this);
                ThreadManager.UIThreadPost(new Runnable() {
                    public void run() {
                        if (NowPlayingGlobalModel.this.lastPlayedTitleModel != null) {
                            NowPlayingGlobalModel.this.lastPlayedTitleModel.load(false);
                        }
                    }
                });
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateNowPlayingModels() {
        /*
        r17 = this;
        r1 = com.microsoft.xbox.service.model.SessionModel.getInstance();
        r1 = r1.getDisplayedSessionState();
        r2 = 2;
        if (r1 == r2) goto L_0x0013;
    L_0x000b:
        r0 = r17;
        r1 = r0.timer;
        r1.stop();
    L_0x0012:
        return;
    L_0x0013:
        r0 = r17;
        r14 = r0.currentTitleId;
        r0 = r17;
        r13 = r0.currentMediaState;
        r10 = 0;
        r11 = 0;
        r12 = 0;
        r1 = com.microsoft.xbox.service.model.SessionModel.getInstance();
        r1 = r1.getCurrentTitleId();
        r0 = r17;
        r3 = r0.currentTitleId;
        r1 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1));
        if (r1 == 0) goto L_0x00e6;
    L_0x002e:
        r1 = "NowPlayingGlobalModel";
        r2 = "TitleId is updated. old=%d, new=%d";
        r3 = 2;
        r3 = new java.lang.Object[r3];
        r4 = 0;
        r0 = r17;
        r5 = r0.currentTitleId;
        r5 = java.lang.Long.valueOf(r5);
        r3[r4] = r5;
        r4 = 1;
        r5 = com.microsoft.xbox.service.model.SessionModel.getInstance();
        r5 = r5.getCurrentTitleId();
        r5 = java.lang.Long.valueOf(r5);
        r3[r4] = r5;
        r2 = java.lang.String.format(r2, r3);
        com.microsoft.xbox.toolkit.XLELog.Diagnostic(r1, r2);
        r1 = com.microsoft.xbox.service.model.SessionModel.getInstance();
        r14 = r1.getCurrentTitleId();
        r1 = new com.microsoft.xbox.service.model.NowPlayingGlobalModel$3;
        r0 = r17;
        r1.<init>();
        r2 = 30000; // 0x7530 float:4.2039E-41 double:1.4822E-319;
        com.microsoft.xbox.toolkit.ThreadManager.UIThreadPostDelayed(r1, r2);
        r0 = r17;
        r1 = r0.nowPlayingAppModel;
        if (r1 == 0) goto L_0x00b1;
    L_0x0070:
        r0 = r17;
        r1 = r0.nowPlayingAppModel;
        r1 = r1.getTitleId();
        r1 = (r1 > r14 ? 1 : (r1 == r14 ? 0 : -1));
        if (r1 == 0) goto L_0x00b1;
    L_0x007c:
        r1 = "NowPlayingGlobalModel";
        r2 = "Now playing app detail model is reset.";
        com.microsoft.xbox.toolkit.XLELog.Diagnostic(r1, r2);
        r0 = r17;
        r1 = r0.nowPlayingAppModel;
        r0 = r17;
        r1.removeObserver(r0);
        r1 = 0;
        r0 = r17;
        r0.nowPlayingAppModel = r1;
        r1 = 0;
        r0 = r17;
        r0.pendingLoadingNowPlayingModel = r1;
        r0 = r17;
        r1 = r0.nowPlayingAppActivityModel;
        if (r1 == 0) goto L_0x00b1;
    L_0x009c:
        r1 = "NowPlayingGlobalModel";
        r2 = "Now playing app activity model and hero is reset.";
        com.microsoft.xbox.toolkit.XLELog.Diagnostic(r1, r2);
        r0 = r17;
        r1 = r0.nowPlayingAppActivityModel;
        r0 = r17;
        r1.removeObserver(r0);
        r1 = 0;
        r0 = r17;
        r0.nowPlayingAppActivityModel = r1;
    L_0x00b1:
        r0 = r17;
        r1 = r0.nowPlayingAppModel;
        if (r1 != 0) goto L_0x00e6;
    L_0x00b7:
        r1 = isValidTitleIdForDetail(r14);
        if (r1 == 0) goto L_0x00e6;
    L_0x00bd:
        r1 = "NowPlayingGlobalModel";
        r2 = "Running valid title. New now playing app detail loaded.";
        com.microsoft.xbox.toolkit.XLELog.Diagnostic(r1, r2);
        r1 = 0;
        r1 = com.microsoft.xbox.service.model.edsv2.EDSV2NowPlayingDetailModel.getModel(r14, r1);
        r0 = r17;
        r0.nowPlayingAppModel = r1;
        r0 = r17;
        r1 = r0.nowPlayingAppModel;
        r0 = r17;
        r2 = r0.lastPlayedTitleModel;
        if (r1 == r2) goto L_0x00e0;
    L_0x00d7:
        r0 = r17;
        r1 = r0.nowPlayingAppModel;
        r0 = r17;
        r1.addObserver(r0);
    L_0x00e0:
        r10 = 1;
        r1 = 1;
        r0 = r17;
        r0.pendingLoadingNowPlayingModel = r1;
    L_0x00e6:
        r0 = r17;
        r1 = r0.currentMediaState;
        r2 = com.microsoft.xbox.service.model.SessionModel.getInstance();
        r2 = r2.getCurrentMediaState();
        if (r1 != r2) goto L_0x0106;
    L_0x00f4:
        r0 = r17;
        r1 = r0.currentMediaState;
        r2 = com.microsoft.xbox.service.model.SessionModel.getInstance();
        r2 = r2.getCurrentMediaState();
        r1 = mediaHasChanged(r1, r2);
        if (r1 == 0) goto L_0x0200;
    L_0x0106:
        r2 = "NowPlayingGlobalModel";
        r3 = "Media state is updated. old=%s, new=%s";
        r1 = 2;
        r4 = new java.lang.Object[r1];
        r5 = 0;
        r0 = r17;
        r1 = r0.currentMediaState;
        if (r1 != 0) goto L_0x023c;
    L_0x0114:
        r1 = "null";
    L_0x0116:
        r4[r5] = r1;
        r5 = 1;
        r1 = com.microsoft.xbox.service.model.SessionModel.getInstance();
        r1 = r1.getCurrentMediaState();
        if (r1 != 0) goto L_0x0246;
    L_0x0123:
        r1 = "null";
    L_0x0125:
        r4[r5] = r1;
        r1 = java.lang.String.format(r3, r4);
        com.microsoft.xbox.toolkit.XLELog.Diagnostic(r2, r1);
        r1 = com.microsoft.xbox.service.model.SessionModel.getInstance();
        r13 = r1.getCurrentMediaState();
        r0 = r17;
        r1 = r0.currentTitleId;
        r1 = (r14 > r1 ? 1 : (r14 == r1 ? 0 : -1));
        if (r1 != 0) goto L_0x0181;
    L_0x013e:
        r0 = r17;
        r1 = r0.nowPlayingState;
        r2 = com.microsoft.xbox.service.model.NowPlayingGlobalModel.NowPlayingState.ConnectedPlayingMusic;
        if (r1 != r2) goto L_0x0181;
    L_0x0146:
        if (r13 == 0) goto L_0x0181;
    L_0x0148:
        r1 = r13.getMediaAssetId();
        r1 = com.microsoft.xbox.toolkit.JavaUtil.isNullOrEmpty(r1);
        if (r1 == 0) goto L_0x0181;
    L_0x0152:
        r1 = "NowPlayingGlobalModel";
        r2 = "Null asset id detected while playing music. Delay posting new media state";
        com.microsoft.xbox.toolkit.XLELog.Diagnostic(r1, r2);
        r7 = r13;
        r8 = r14;
        r1 = new com.microsoft.xbox.service.model.NowPlayingGlobalModel$4;
        r0 = r17;
        r1.<init>(r8, r7);
        r2 = 3000; // 0xbb8 float:4.204E-42 double:1.482E-320;
        com.microsoft.xbox.toolkit.ThreadManager.UIThreadPostDelayed(r1, r2);
        r0 = r17;
        r13 = r0.currentMediaState;
        r1 = 5;
        r13.setTransportState(r1);
        r0 = r17;
        r1 = r0.currentMediaState;
        r1 = r1.getDuration();
        r3 = 1000000; // 0xf4240 float:1.401298E-39 double:4.940656E-318;
        r1 = r1 - r3;
        r3 = 1;
        r1 = r1 - r3;
        r13.setPosition(r1);
    L_0x0181:
        r0 = r17;
        r1 = r0.currentTitleId;
        r1 = (r14 > r1 ? 1 : (r14 == r1 ? 0 : -1));
        if (r1 != 0) goto L_0x0254;
    L_0x0189:
        r0 = r17;
        r1 = r0.nowPlayingState;
        r2 = com.microsoft.xbox.service.model.NowPlayingGlobalModel.NowPlayingState.ConnectedPlayingMusic;
        if (r1 != r2) goto L_0x0254;
    L_0x0191:
        r1 = mediaIsValid(r13);
        if (r1 == 0) goto L_0x0254;
    L_0x0197:
        r0 = r17;
        r1 = r0.currentMediaState;
        r1 = mediaHasChanged(r1, r13);
        if (r1 == 0) goto L_0x0254;
    L_0x01a1:
        r1 = "NowPlayingGlobalModel";
        r2 = "New asset id received for music. Delay updating media model";
        com.microsoft.xbox.toolkit.XLELog.Diagnostic(r1, r2);
        r0 = r17;
        r1 = r0.pendingMusicDetailModel;
        if (r1 == 0) goto L_0x01c3;
    L_0x01ae:
        r1 = "NowPlayingGlobalModel";
        r2 = "Resetting old pending music model";
        com.microsoft.xbox.toolkit.XLELog.Diagnostic(r1, r2);
        r0 = r17;
        r1 = r0.pendingMusicDetailModel;
        r0 = r17;
        r1.removeObserver(r0);
        r1 = 0;
        r0 = r17;
        r0.pendingMusicDetailModel = r1;
    L_0x01c3:
        r0 = r17;
        r1 = r0.pendingMusicDetailModel;
        if (r1 != 0) goto L_0x01e6;
    L_0x01c9:
        r1 = "NowPlayingGlobalModel";
        r2 = "Updating pending media model";
        com.microsoft.xbox.toolkit.XLELog.Diagnostic(r1, r2);
        r1 = r13.getMediaAssetId();
        r1 = com.microsoft.xbox.service.model.edsv2.EDSV2NowPlayingDetailModel.getModel(r14, r1);
        r0 = r17;
        r0.pendingMusicDetailModel = r1;
        r0 = r17;
        r1 = r0.pendingMusicDetailModel;
        r0 = r17;
        r1.addObserver(r0);
        r12 = 1;
    L_0x01e6:
        r0 = r17;
        r13 = r0.currentMediaState;
        r1 = 5;
        r13.setTransportState(r1);
        r0 = r17;
        r1 = r0.currentMediaState;
        r1 = r1.getDuration();
        r3 = 1000000; // 0xf4240 float:1.401298E-39 double:4.940656E-318;
        r1 = r1 - r3;
        r3 = 1;
        r1 = r1 - r3;
        r13.setPosition(r1);
    L_0x0200:
        r0 = r17;
        r1 = r0.currentTitleId;
        r1 = (r14 > r1 ? 1 : (r14 == r1 ? 0 : -1));
        if (r1 != 0) goto L_0x020e;
    L_0x0208:
        r0 = r17;
        r1 = r0.currentMediaState;
        if (r13 == r1) goto L_0x0216;
    L_0x020e:
        r0 = r17;
        r0.currentTitleId = r14;
        r0 = r17;
        r0.currentMediaState = r13;
    L_0x0216:
        if (r10 == 0) goto L_0x0222;
    L_0x0218:
        r1 = new com.microsoft.xbox.service.model.NowPlayingGlobalModel$5;
        r0 = r17;
        r1.<init>();
        com.microsoft.xbox.toolkit.ThreadManager.UIThreadPost(r1);
    L_0x0222:
        if (r11 == 0) goto L_0x022e;
    L_0x0224:
        r1 = new com.microsoft.xbox.service.model.NowPlayingGlobalModel$6;
        r0 = r17;
        r1.<init>();
        com.microsoft.xbox.toolkit.ThreadManager.UIThreadPost(r1);
    L_0x022e:
        if (r12 == 0) goto L_0x0012;
    L_0x0230:
        r1 = new com.microsoft.xbox.service.model.NowPlayingGlobalModel$7;
        r0 = r17;
        r1.<init>();
        com.microsoft.xbox.toolkit.ThreadManager.UIThreadPost(r1);
        goto L_0x0012;
    L_0x023c:
        r0 = r17;
        r1 = r0.currentMediaState;
        r1 = r1.getMediaAssetId();
        goto L_0x0116;
    L_0x0246:
        r1 = com.microsoft.xbox.service.model.SessionModel.getInstance();
        r1 = r1.getCurrentMediaState();
        r1 = r1.getMediaAssetId();
        goto L_0x0125;
    L_0x0254:
        r0 = r17;
        r1 = r0.nowPlayingMediaModel;
        if (r1 == 0) goto L_0x02a7;
    L_0x025a:
        r0 = r17;
        r1 = r0.nowPlayingMediaModel;
        r1 = r1.getTitleId();
        r1 = (r1 > r14 ? 1 : (r1 == r14 ? 0 : -1));
        if (r1 != 0) goto L_0x0277;
    L_0x0266:
        r0 = r17;
        r1 = r0.nowPlayingMediaModel;
        r2 = r1.getPartnerMediaId();
        if (r13 != 0) goto L_0x030f;
    L_0x0270:
        r1 = 0;
    L_0x0271:
        r1 = com.microsoft.xbox.toolkit.JavaUtil.stringsEqualCaseInsensitive(r2, r1);
        if (r1 != 0) goto L_0x02a7;
    L_0x0277:
        r1 = "NowPlayingGlobalModel";
        r2 = "Now playing media detail model is reset.";
        com.microsoft.xbox.toolkit.XLELog.Diagnostic(r1, r2);
        r0 = r17;
        r1 = r0.nowPlayingMediaModel;
        r0 = r17;
        r1.removeObserver(r0);
        r1 = 0;
        r0 = r17;
        r0.nowPlayingMediaModel = r1;
        r0 = r17;
        r1 = r0.nowPlayingMediaActivityModel;
        if (r1 == 0) goto L_0x02a7;
    L_0x0292:
        r1 = "NowPlayingGlobalModel";
        r2 = "Now playing media activity model and hero is reset.";
        com.microsoft.xbox.toolkit.XLELog.Diagnostic(r1, r2);
        r0 = r17;
        r1 = r0.nowPlayingMediaActivityModel;
        r0 = r17;
        r1.removeObserver(r0);
        r1 = 0;
        r0 = r17;
        r0.nowPlayingMediaActivityModel = r1;
    L_0x02a7:
        r0 = r17;
        r1 = r0.timer;
        r1.stop();
        if (r13 == 0) goto L_0x0200;
    L_0x02b0:
        r0 = r17;
        r1 = r0.nowPlayingMediaModel;
        if (r1 != 0) goto L_0x02e3;
    L_0x02b6:
        r1 = isValidTitleIdForDetail(r14);
        if (r1 == 0) goto L_0x02e3;
    L_0x02bc:
        r1 = r13.getMediaAssetId();
        r1 = isValidId(r1);
        if (r1 == 0) goto L_0x02e3;
    L_0x02c6:
        r1 = "NowPlayingGlobalModel";
        r2 = "Running valid title and media. New now playing media detail loaded.";
        com.microsoft.xbox.toolkit.XLELog.Diagnostic(r1, r2);
        r1 = r13.getMediaAssetId();
        r1 = com.microsoft.xbox.service.model.edsv2.EDSV2NowPlayingDetailModel.getModel(r14, r1);
        r0 = r17;
        r0.nowPlayingMediaModel = r1;
        r0 = r17;
        r1 = r0.nowPlayingMediaModel;
        r0 = r17;
        r1.addObserver(r0);
        r11 = 1;
    L_0x02e3:
        r1 = r13.isMediaInProgress();
        if (r1 == 0) goto L_0x0200;
    L_0x02e9:
        r0 = r17;
        r1 = r0.timer;
        r2 = r13.getPosition();
        r4 = r13.getDuration();
        r6 = r13.getTransportState();
        r16 = 3;
        r0 = r16;
        if (r6 != r0) goto L_0x0315;
    L_0x02ff:
        r6 = r13.getRate();
    L_0x0303:
        r1.update(r2, r4, r6);
        r0 = r17;
        r1 = r0.timer;
        r1.start();
        goto L_0x0200;
    L_0x030f:
        r1 = r13.getMediaAssetId();
        goto L_0x0271;
    L_0x0315:
        r6 = 0;
        goto L_0x0303;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.microsoft.xbox.service.model.NowPlayingGlobalModel.updateNowPlayingModels():void");
    }

    private static boolean mediaHasChanged(MediaTitleState oldMediaState, MediaTitleState newMediaState) {
        String newMediaAssetId = null;
        if (oldMediaState == newMediaState) {
            return false;
        }
        String oldMediaAssetId = oldMediaState == null ? null : oldMediaState.getMediaAssetId();
        if (newMediaState != null) {
            newMediaAssetId = newMediaState.getMediaAssetId();
        }
        if (JavaUtil.stringsEqualCaseInsensitive(oldMediaAssetId, newMediaAssetId)) {
            return false;
        }
        return true;
    }

    private static boolean mediaIsValid(MediaTitleState mediaState) {
        if (mediaState == null || JavaUtil.isNullOrEmpty(mediaState.getMediaAssetId())) {
            return false;
        }
        return true;
    }

    private void updateActivityModels(Object sender) {
        if (sender instanceof EDSV2MediaItemModel) {
            EDSV2MediaItemDetailModel detailModel = (EDSV2MediaItemDetailModel) sender;
            if (detailModel.getMediaType() == EDSV2MediaType.MEDIATYPE_TRACK) {
                if (this.nowPlayingMediaActivityModel != null) {
                    XLELog.Diagnostic("NowPlayingGlobalModel", "Now playing media activity model is reset.");
                    this.nowPlayingMediaActivityModel.removeObserver(this);
                    this.nowPlayingMediaActivityModel = null;
                    return;
                }
                return;
            } else if (this.nowPlayingAppModel != null && JavaUtil.stringsEqualNonNullCaseInsensitive(detailModel.getCanonicalId(), this.nowPlayingAppModel.getCanonicalId())) {
                this.nowPlayingAppActivityModel = updateAppActivityModel(this.nowPlayingAppActivityModel, this.nowPlayingAppModel, this.currentTitleId);
                return;
            } else if (this.lastPlayedTitleModel != null && JavaUtil.stringsEqualNonNullCaseInsensitive(detailModel.getCanonicalId(), this.lastPlayedTitleModel.getCanonicalId())) {
                this.lastPlayedTitleActivityModel = updateAppActivityModel(this.lastPlayedTitleActivityModel, this.lastPlayedTitleModel, this.lastPlayedTitleModel.getTitleId());
                return;
            } else if (this.nowPlayingMediaModel != null && JavaUtil.stringsEqualNonNullCaseInsensitive(detailModel.getCanonicalId(), this.nowPlayingMediaModel.getCanonicalId())) {
                if (!JavaUtil.stringsEqualCaseInsensitive(this.nowPlayingMediaActivityModel == null ? null : this.nowPlayingMediaActivityModel.getParentCanonicalId(), this.nowPlayingMediaModel == null ? null : this.nowPlayingMediaModel.getCanonicalId())) {
                    XLELog.Diagnostic("NowPlayingGlobalModel", String.format("Media canonical id is updated. old=%s, new=%s", new Object[]{this.nowPlayingMediaActivityModel == null ? null : this.nowPlayingMediaActivityModel.getParentCanonicalId(), this.nowPlayingMediaModel == null ? null : this.nowPlayingMediaModel.getCanonicalId()}));
                    if (this.nowPlayingMediaActivityModel != null) {
                        XLELog.Diagnostic("NowPlayingGlobalModel", "Now playing media activity model is reset.");
                        this.nowPlayingMediaActivityModel.removeObserver(this);
                        this.nowPlayingMediaActivityModel = null;
                    }
                    if (this.currentMediaState != null && this.nowPlayingMediaActivityModel == null && isValidTitleIdForActivity(this.currentTitleId) && isValidId(this.currentMediaState.getMediaAssetId()) && this.nowPlayingMediaModel != null) {
                        XLELog.Diagnostic("NowPlayingGlobalModel", "Running valid app and media canonical id is valid. New now playing media activity model loaded.");
                        EDSV2MediaItem itemDetailData = this.nowPlayingMediaModel.getMediaItemDetailData();
                        addNowPlayingTitleAsProviderIfNecessary(this.currentTitleId, itemDetailData);
                        this.nowPlayingMediaActivityModel = ActivitySummaryModel.getModel(itemDetailData);
                        this.nowPlayingMediaActivityModel.addObserver(this);
                        ThreadManager.UIThreadPost(new Runnable() {
                            public void run() {
                                if (NowPlayingGlobalModel.this.nowPlayingMediaActivityModel != null) {
                                    NowPlayingGlobalModel.this.nowPlayingMediaActivityModel.load(false);
                                }
                            }
                        });
                        return;
                    }
                    return;
                }
                return;
            } else {
                return;
            }
        }
        XLEAssert.assertTrue("Someone other than EDSV2MediaItemModel is sending update type of MediaItemDetail: " + sender.getClass().getSimpleName(), false);
    }

    public void addNowPlayingTitleAsProviderIfNecessary(long titleId, EDSV2MediaItem item) {
        if (item.getProviders() != null && item.getProviders().size() > 0) {
            Iterator i$ = item.getProviders().iterator();
            while (i$.hasNext()) {
                if (((EDSV2Provider) i$.next()).getTitleId() == titleId) {
                    return;
                }
            }
        }
        XLELog.Diagnostic("NowPlayingGlobalModel", "add now playing title as provider. " + titleId);
        EDSV2Provider provider = new EDSV2Provider();
        provider.setTitleId(titleId);
        if (item.getProviders() == null) {
            ArrayList<EDSV2Provider> providers = new ArrayList();
            providers.add(provider);
            item.setProviders(providers);
            return;
        }
        item.getProviders().add(provider);
    }

    private static ActivitySummaryModel updateAppActivityModel(ActivitySummaryModel currentActivityModel, EDSV2MediaItemDetailModel parentAppDetailModel, long titleId) {
        String currentAppCanonicalId = null;
        String cachedAppCanonicalId = currentActivityModel == null ? null : currentActivityModel.getParentCanonicalId();
        if (parentAppDetailModel != null) {
            currentAppCanonicalId = parentAppDetailModel.getCanonicalId();
        }
        if (JavaUtil.stringsEqualCaseInsensitive(cachedAppCanonicalId, currentAppCanonicalId)) {
            return currentActivityModel;
        }
        XLELog.Diagnostic("NowPlayingGlobalModel", String.format("App canonical id is updated. old=%s, new=%s", new Object[]{cachedAppCanonicalId, currentAppCanonicalId}));
        if (currentActivityModel != null) {
            XLELog.Diagnostic("NowPlayingGlobalModel", "Now playing app activity model is reset.");
            currentActivityModel.removeObserver(getInstance());
            currentActivityModel = null;
        }
        if (currentActivityModel != null || !isValidTitleIdForActivity(titleId) || parentAppDetailModel == null) {
            return currentActivityModel;
        }
        XLELog.Diagnostic("NowPlayingGlobalModel", "Running valid title and app canonical id is valid. New now playing app activity model loaded.");
        currentActivityModel = ActivitySummaryModel.getModel(parentAppDetailModel.getMediaItemDetailData());
        currentActivityModel.addObserver(getInstance());
        final ActivitySummaryModel modelToLoad = currentActivityModel;
        ThreadManager.UIThreadPost(new Runnable() {
            public void run() {
                modelToLoad.load(false);
            }
        });
        return currentActivityModel;
    }

    private EDSV2ActivityItem getNowPlayingHeroAppActivity() {
        return getHeroAppActivity(this.nowPlayingAppActivityModel, this.currentTitleId);
    }

    private EDSV2ActivityItem getLastPlayedHeroAppActivity() {
        if (this.lastPlayedTitleModel != null) {
            return getHeroAppActivity(this.lastPlayedTitleActivityModel, this.lastPlayedTitleModel.getTitleId());
        }
        return null;
    }

    private EDSV2ActivityItem getHeroAppActivity(ActivitySummaryModel activityModel, long titleId) {
        boolean appActivityModelReady;
        if (activityModel == null || !activityModel.isLoaded()) {
            appActivityModelReady = false;
        } else {
            appActivityModelReady = true;
        }
        if (appActivityModelReady) {
            return ActivityUtil.getDefaultActivity(this.nowPlayingAppActivityModel, null, 0, titleId);
        }
        XLELog.Diagnostic("NowPlayingGlobalModel", "getHeroAppActivity: App activity model is not ready.");
        return null;
    }

    private EDSV2ActivityItem getHeroMusicActivity() {
        boolean appActivityModelReady = this.nowPlayingAppActivityModel != null && this.nowPlayingAppActivityModel.isLoaded();
        if (appActivityModelReady) {
            return ActivityUtil.getDefaultActivity(this.nowPlayingAppActivityModel, null, EDSV2MediaType.MEDIATYPE_TRACK, this.currentTitleId);
        }
        XLELog.Diagnostic("NowPlayingGlobalModel", "getHeroMusicActivity: App activity model is not ready.");
        return null;
    }

    private EDSV2ActivityItem getHeroVideoActivity() {
        boolean mediaActivityModelReady;
        boolean appActivityModelReady = true;
        if (this.nowPlayingMediaActivityModel == null || !this.nowPlayingMediaActivityModel.isLoaded()) {
            mediaActivityModelReady = false;
        } else {
            mediaActivityModelReady = true;
        }
        if (mediaActivityModelReady) {
            if (this.nowPlayingAppActivityModel == null || !this.nowPlayingAppActivityModel.isLoaded()) {
                appActivityModelReady = false;
            }
            if (appActivityModelReady) {
                return ActivityUtil.getDefaultActivity(this.nowPlayingAppActivityModel, this.nowPlayingMediaActivityModel, this.nowPlayingMediaModel.getMediaType(), this.currentTitleId);
            }
            XLELog.Diagnostic("NowPlayingGlobalModel", "getHeroAppActivity: App activity model is not ready.");
            return null;
        }
        XLELog.Diagnostic("NowPlayingGlobalModel", "getHeroMediaActivity: Media activity model is not ready.");
        return null;
    }

    private static boolean isValidTitleIdForDetail(long titleId) {
        return titleId > 0 && titleId != XLEConstants.DASH_TITLE_ID;
    }

    private static boolean isValidTitleIdForActivity(long titleId) {
        return (titleId <= 0 || titleId == XLEConstants.DASH_TITLE_ID || titleId == XLEConstants.BROWSER_TITLE_ID) ? false : true;
    }

    private static boolean isValidId(String id) {
        return !JavaUtil.isNullOrEmpty(id);
    }

    private void resetAllNowPlayingData() {
        if (this.nowPlayingMediaModel != null) {
            this.nowPlayingMediaModel.removeObserver(this);
            this.nowPlayingMediaModel = null;
        }
        if (this.nowPlayingMediaActivityModel != null) {
            this.nowPlayingMediaActivityModel.removeObserver(this);
            this.nowPlayingMediaActivityModel = null;
        }
        if (this.nowPlayingAppModel != null) {
            this.nowPlayingAppModel.removeObserver(this);
            this.nowPlayingAppModel = null;
            this.pendingLoadingNowPlayingModel = false;
        }
        if (this.nowPlayingAppActivityModel != null) {
            this.nowPlayingAppActivityModel.removeObserver(this);
            this.nowPlayingAppActivityModel = null;
        }
        this.currentMediaState = null;
        this.currentTitleId = 0;
        this.currentNowPlayingIdentifier = null;
        this.timer.stop();
        this.timer.setOnPositionUpdatedRunnable(null);
    }
}
