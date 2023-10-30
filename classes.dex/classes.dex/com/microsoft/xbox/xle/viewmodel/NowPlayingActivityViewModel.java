package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.JTitleType;
import com.microsoft.xbox.service.model.LaunchType;
import com.microsoft.xbox.service.model.NowPlayingGlobalModel;
import com.microsoft.xbox.service.model.NowPlayingGlobalModel.NowPlayingState;
import com.microsoft.xbox.service.model.QuickplayModel;
import com.microsoft.xbox.service.model.SessionModel;
import com.microsoft.xbox.service.model.UpdateData;
import com.microsoft.xbox.service.model.UpdateType;
import com.microsoft.xbox.service.model.edsv2.EDSV2ActivityItem;
import com.microsoft.xbox.service.model.edsv2.EDSV2AppMediaItem;
import com.microsoft.xbox.service.model.edsv2.EDSV2GameMediaItem;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaItem;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaItemDetailModel;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaItemModel;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaType;
import com.microsoft.xbox.service.model.edsv2.EDSV2MusicAlbumMediaItem;
import com.microsoft.xbox.service.model.edsv2.EDSV2MusicArtistBrowseAlbumModel;
import com.microsoft.xbox.service.model.edsv2.EDSV2MusicArtistMediaItem;
import com.microsoft.xbox.service.model.edsv2.EDSV2MusicTrackMediaItemWithAlbum;
import com.microsoft.xbox.service.model.edsv2.EDSV2PartnerApplicationLaunchInfo;
import com.microsoft.xbox.service.model.edsv2.EDSV2Provider;
import com.microsoft.xbox.service.model.sls.Title;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.MediaProgressTimer.OnMediaProgressUpdatedListener;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEConstants;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.toolkit.network.ListState;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.app.XboxMobileOmnitureTracking;
import com.microsoft.xbox.xle.app.activity.ActivityBase;
import com.microsoft.xbox.xle.app.activity.DashDetailsActivity;
import com.microsoft.xbox.xle.app.activity.XboxConsoleHelpActivity;
import com.microsoft.xbox.xle.app.adapter.AdapterFactory;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class NowPlayingActivityViewModel extends PivotViewModelBase {
    private static final String DISCOGRAPHY = XboxApplication.Resources.getString(R.string.artist_album_sub_header);
    private static final String LAST_PLAYED = XboxApplication.Resources.getString(R.string.last_played_text);
    private static final String NOW_PLAYING = XboxApplication.Resources.getString(R.string.now_playing_text);
    private static final String NO_CONTENT = XboxApplication.Resources.getString(R.string.list_empty);
    private static final String NO_LAST_PLAYED = XboxApplication.Resources.getString(R.string.no_last_played_text);
    private static final String NO_RECENT_GAME_OR_APP = XboxApplication.Resources.getString(R.string.recent_games_empty);
    private static final String QUICKPLAY = XboxApplication.Resources.getString(R.string.recents_title_sub_header);
    private static final String RELATED = XboxApplication.Resources.getString(R.string.related_title_sub_header);
    private ConnectionState connectionState;
    private ContentType contentType;
    private EDSV2MusicArtistBrowseAlbumModel currentArtistDetailModel;
    private String description;
    private ListState listState;
    private String nowPlayingHeader;
    private String nowPlayingSubTitle;
    private String nowPlayingTitle;
    private URI nowplayingUri;
    private String providerName;
    private ListState quickplayListState;
    private RelatedContentType relatedContentType;

    public enum ActivityType {
        Hero,
        Controller,
        None
    }

    public enum ConnectionState {
        NotConnected,
        Connecting,
        Connected
    }

    public enum ContentType {
        Media,
        Game,
        Music,
        App,
        Dash,
        None
    }

    public enum RelatedContentType {
        QuickPlay,
        Related,
        Album,
        None
    }

    public NowPlayingActivityViewModel() {
        this.connectionState = ConnectionState.Connecting;
        this.relatedContentType = RelatedContentType.None;
        this.contentType = ContentType.None;
        this.listState = ListState.LoadingState;
        this.quickplayListState = ListState.LoadingState;
        this.adapter = AdapterFactory.getInstance().getNowPlayingActivityAdapter(this);
        this.nowPlayingHeader = NOW_PLAYING;
        this.description = null;
        this.currentArtistDetailModel = null;
        updateConnectionState();
    }

    public void onRehydrate() {
        this.adapter = AdapterFactory.getInstance().getNowPlayingActivityAdapter(this);
    }

    public ListState getListState() {
        switch (this.relatedContentType) {
            case QuickPlay:
                return this.quickplayListState;
            case Related:
            case Album:
                return this.listState;
            default:
                return ListState.LoadingState;
        }
    }

    public List<Title> getQuickplayList() {
        XLEAssert.assertTrue(this.relatedContentType == RelatedContentType.QuickPlay);
        return QuickplayModel.getInstance().getRecentQuickplayList();
    }

    public boolean isZuneInQuickplayListFirstItem() {
        List<Title> res = QuickplayModel.getInstance().getRecentQuickplayList();
        if (res == null || res.size() <= 0 || XLEConstants.ZUNE_TITLE_ID != ((Title) res.get(0)).titleId) {
            return false;
        }
        return true;
    }

    public ConnectionState getConnectionState() {
        return this.connectionState;
    }

    public URI getNowPlayingTileUrl() {
        return this.nowplayingUri;
    }

    public String getRelatedHeader() {
        switch (this.contentType) {
            case Media:
            case Game:
                return RELATED;
            case Music:
                return DISCOGRAPHY;
            case Dash:
                return QUICKPLAY;
            default:
                return null;
        }
    }

    public String getNowPlayingHeader() {
        return this.nowPlayingHeader;
    }

    public boolean getShouldShowNowPlaying() {
        return this.contentType != ContentType.None;
    }

    public void TEST_setNotShouldShowNowPlaying() {
        this.contentType = ContentType.None;
    }

    public boolean getShouldShowDescription() {
        if (this.contentType == ContentType.Music || this.contentType == ContentType.Dash || this.contentType == ContentType.None) {
            return false;
        }
        return true;
    }

    public boolean getShouldShowRelated() {
        if (this.contentType == ContentType.App || this.contentType == ContentType.None) {
            return false;
        }
        return true;
    }

    public int getNowPlayingDefaultImageRid() {
        return NowPlayingGlobalModel.getInstance().getDefaultResourceId();
    }

    public String getDescription() {
        return this.description;
    }

    public String getNowPlayingTitle() {
        return this.nowPlayingTitle;
    }

    public String getProviderName() {
        return this.providerName;
    }

    public String getNowPlayingSubTitle() {
        return this.nowPlayingSubTitle;
    }

    public boolean shouldShowMediaProgress() {
        return NowPlayingGlobalModel.getInstance().isMediaInProgress() && NowPlayingGlobalModel.getInstance().getMediaDurationInSeconds() > 0;
    }

    public EDSV2ActivityItem getHeroActivity() {
        return NowPlayingGlobalModel.getInstance().getHeroActivity();
    }

    public RelatedContentType getRelatedContentType() {
        return this.relatedContentType;
    }

    public String getRelatedNoContentText() {
        if (this.relatedContentType == RelatedContentType.QuickPlay) {
            return NO_RECENT_GAME_OR_APP;
        }
        return NO_CONTENT;
    }

    public ArrayList<EDSV2MediaItem> getRelated() {
        XLEAssert.assertTrue(this.relatedContentType == RelatedContentType.Related);
        XLEAssert.assertNotNull(NowPlayingGlobalModel.getInstance().getCurrentDetailModel());
        return NowPlayingGlobalModel.getInstance().getCurrentDetailModel().getRelated();
    }

    public ArrayList<EDSV2MusicAlbumMediaItem> getRelatedAlbums() {
        XLEAssert.assertTrue(this.relatedContentType == RelatedContentType.Album);
        XLEAssert.assertNotNull(this.currentArtistDetailModel);
        return this.currentArtistDetailModel.getAlbums();
    }

    public ActivityType getActivityType() {
        if (NowPlayingGlobalModel.getInstance().shouldShowController()) {
            return ActivityType.Controller;
        }
        if (getHeroActivity() != null) {
            return ActivityType.Hero;
        }
        return ActivityType.None;
    }

    protected void onStartOverride() {
        NowPlayingGlobalModel.getInstance().addObserver(this);
    }

    protected void onStopOverride() {
        NowPlayingGlobalModel.getInstance().removeObserver(this);
        SessionModel.getInstance().removeObserver(this);
        if (this.currentArtistDetailModel != null) {
            this.currentArtistDetailModel.removeObserver(this);
            this.currentArtistDetailModel = null;
        }
    }

    public void onPause() {
        XLELog.Diagnostic("NowPlayingVM", "onPause is called");
        this.relatedContentType = RelatedContentType.None;
        this.contentType = ContentType.None;
        super.onPause();
    }

    public boolean isBusy() {
        return QuickplayModel.getInstance().getIsLoading() || NowPlayingGlobalModel.getInstance().getIsLoading();
    }

    public boolean isBlockingBusy() {
        return this.isLaunching;
    }

    public void load(boolean forceRefresh) {
        NowPlayingGlobalModel.getInstance().load(true);
        NowPlayingGlobalModel.getInstance().refreshDetailModels(forceRefresh);
    }

    protected void onUpdateFinished() {
        super.onUpdateFinished();
    }

    public void setOnMediaProgressUpdatedListener(OnMediaProgressUpdatedListener listener) {
        NowPlayingGlobalModel.getInstance().setOnMediaProgressUpdatedRunnable(listener);
    }

    public void connectToConsole() {
        XboxMobileOmnitureTracking.TrackConsoleConnectAttempt("Manual", "Home Connect");
        AutoConnectAndLaunchViewModel.getInstance().manualConnectAndLaunch();
    }

    public void launchOnConsole(Title item) {
        EDSV2MediaItem titleItem;
        if (item.IsApplication()) {
            titleItem = new EDSV2AppMediaItem(item);
        } else {
            titleItem = new EDSV2GameMediaItem(item);
        }
        if (titleItem.getProviders() == null || titleItem.getProviders().size() == 0) {
            XLELog.Warning("NowPlayingActivityViewModel", "No provider for the app. Won't launch.");
        } else if (item.getIsXboxMusic()) {
            EDSV2Provider musicProvider = new EDSV2Provider();
            musicProvider.setTitleId(item.titleId);
            musicProvider.setName(EDSV2MediaItemDetailModel.XBOX_MUSIC_TITLE_STRING);
            musicProvider.setIsXboxMusic(true);
            EDSV2PartnerApplicationLaunchInfo musicProviderLaunchInfo = new EDSV2PartnerApplicationLaunchInfo();
            musicProviderLaunchInfo.setTitleId(item.titleId);
            musicProviderLaunchInfo.setDeepLinkInfo(EDSV2MediaItemDetailModel.XBOX_MUSIC_LAUNCH_PARAM);
            musicProviderLaunchInfo.setLaunchType(LaunchType.UnknownLaunchType);
            musicProviderLaunchInfo.setTitleType(JTitleType.Application);
            ArrayList<EDSV2PartnerApplicationLaunchInfo> musiclaunchInfoList = new ArrayList();
            musiclaunchInfoList.add(musicProviderLaunchInfo);
            musicProvider.setLaunchInfos(musiclaunchInfoList);
            launchTitleOnConsoleWithConfirmation(musicProvider);
        } else if (item.getIsXboxVideo()) {
            EDSV2Provider videoProvider = new EDSV2Provider();
            videoProvider.setTitleId(item.titleId);
            videoProvider.setName(EDSV2MediaItemDetailModel.XBOX_VIDEO_TITLE_STRING);
            videoProvider.setIsXboxVideo(true);
            EDSV2PartnerApplicationLaunchInfo videoProviderLaunchInfo = new EDSV2PartnerApplicationLaunchInfo();
            videoProviderLaunchInfo.setTitleId(item.titleId);
            videoProviderLaunchInfo.setDeepLinkInfo(EDSV2MediaItemDetailModel.XBOX_VIDEO_LAUNCH_PARAM);
            videoProviderLaunchInfo.setLaunchType(LaunchType.UnknownLaunchType);
            videoProviderLaunchInfo.setTitleType(JTitleType.Application);
            ArrayList<EDSV2PartnerApplicationLaunchInfo> videolaunchInfoList = new ArrayList();
            videolaunchInfoList.add(videoProviderLaunchInfo);
            videoProvider.setLaunchInfos(videolaunchInfoList);
            launchTitleOnConsoleWithConfirmation(videoProvider);
        } else {
            launchTitleOnConsoleWithConfirmation((EDSV2Provider) titleItem.getProviders().get(0));
        }
    }

    public void navigateToNowPlayingDetails() {
        switch (NowPlayingGlobalModel.getInstance().getNowPlayingState()) {
            case Disconnected:
            case Connecting:
                EDSV2MediaItemDetailModel lastPlayedTitleModel = NowPlayingGlobalModel.getInstance().getCurrentDetailModel();
                if (lastPlayedTitleModel != null) {
                    navigateToAppOrMediaDetails(lastPlayedTitleModel.getMediaItemDetailData());
                    return;
                }
                return;
            case ConnectedPlayingApp:
            case ConnectedPlayingGame:
            case ConnectedPlayingVideo:
            case ConnectedPlayingMusic:
                EDSV2MediaItem mediaItem = NowPlayingGlobalModel.getInstance().getNowPlayingMediaItem();
                if (mediaItem instanceof EDSV2MusicTrackMediaItemWithAlbum) {
                    EDSV2MediaItem albumItem = ((EDSV2MusicTrackMediaItemWithAlbum) mediaItem).getAlbum();
                    if (albumItem != null) {
                        mediaItem = albumItem;
                    } else {
                        return;
                    }
                }
                if (mediaItem != null) {
                    navigateToAppOrMediaDetails(mediaItem);
                    return;
                }
                return;
            case ConnectedPlayingDash:
            case ConnectedPlayingDashMedia:
                NavigateTo(DashDetailsActivity.class);
                return;
            default:
                return;
        }
    }

    public void navigateToRelated(EDSV2MediaItem mediaItem) {
        if (mediaItem != null) {
            navigateToAppOrMediaDetails(mediaItem);
        }
    }

    public void navigateToTitleDetail(Title title) {
        XLELog.Diagnostic("NowPlayingActivityVM", "Navigating to title detail.");
        if (title.IsGame()) {
            navigateToAppOrMediaDetails(new EDSV2GameMediaItem(title));
        } else {
            navigateToAppOrMediaDetails(new EDSV2AppMediaItem(title));
        }
    }

    public void navigateToActivityDetails(EDSV2ActivityItem activityData) {
        navigateToActivityDetails(NowPlayingGlobalModel.getInstance().getNowPlayingMediaItem(), activityData);
    }

    public void launchActivity(EDSV2ActivityItem activityData) {
        XboxMobileOmnitureTracking.TrackLaunchActivity("Manual", ActivityBase.nowPlayingChannel, Integer.toString(activityData.getMediaType()), activityData.getTitle(), activityData.getCanonicalId(), "true");
        checkDeviceRequirementAndLaunchActivity(NowPlayingGlobalModel.getInstance().getNowPlayingMediaItem(), activityData);
    }

    public void updateOverride(AsyncResult<UpdateData> asyncResult) {
        UpdateType type = ((UpdateData) asyncResult.getResult()).getUpdateType();
        XLELog.Diagnostic("NowPlayingVM", "Received update: " + type.toString());
        switch (type) {
            case NowPlayingRelated:
            case MediaListBrowse:
            case MediaItemDetail:
                updateRelatedListState(asyncResult);
                break;
            case NowPlayingState:
            case NowPlayingDetail:
            case NowPlayingQuickplay:
                updateQuickplayListState(asyncResult);
                updateConnectionState();
                updateContentType();
                updateRelatedContentType();
                updateRelatedListState(asyncResult);
                break;
            case SessionLaunchRequestComplete:
                if (((UpdateData) asyncResult.getResult()).getIsFinal()) {
                    cancelLaunchTimeout();
                    SessionModel.getInstance().removeObserver(this);
                    if (asyncResult.getException() == null) {
                        XLELog.Diagnostic("NowPlayingActivityVM", "launch title successful, navigate to dpad");
                        navigateToRemote(false);
                        return;
                    }
                }
                break;
            case SessionRequestFailure:
                if (this.isLaunching) {
                    XLELog.Diagnostic("NowPlayingActivityViewModel", "request failed because can't join session");
                    cancelLaunchTimeout();
                    if (SessionModel.getInstance().getLastErrorCode() == 12) {
                        showMustActDialog(XLEApplication.Resources.getString(R.string.failed_to_connect_to_xbox), XLEApplication.Resources.getString(R.string.failed_to_connect_max_users), XLEApplication.Resources.getString(R.string.Dismiss), new Runnable() {
                            public void run() {
                            }
                        }, true);
                        return;
                    } else {
                        NavigateTo(XboxConsoleHelpActivity.class);
                        return;
                    }
                }
                return;
            case SessionState:
                return;
        }
        this.adapter.updateView();
    }

    private void updateConnectionState() {
        NowPlayingState state = NowPlayingGlobalModel.getInstance().getNowPlayingState();
        XLELog.Diagnostic("NowPlayingVM", "NowplayingState changed to  " + state);
        switch (state) {
            case Disconnected:
                this.connectionState = ConnectionState.NotConnected;
                return;
            case Connecting:
                this.connectionState = ConnectionState.Connecting;
                return;
            case ConnectedPlayingApp:
            case ConnectedPlayingGame:
            case ConnectedPlayingVideo:
            case ConnectedPlayingMusic:
            case ConnectedPlayingDash:
            case ConnectedPlayingDashMedia:
                this.connectionState = ConnectionState.Connected;
                return;
            default:
                XLEAssert.assertTrue(false);
                this.connectionState = ConnectionState.NotConnected;
                return;
        }
    }

    private void updateContentType() {
        NowPlayingState state = NowPlayingGlobalModel.getInstance().getNowPlayingState();
        XLELog.Diagnostic("NowPlayingVM", "NowplayingState changed to  " + state);
        ContentType newContentType = ContentType.None;
        switch (state) {
            case Disconnected:
            case Connecting:
                EDSV2MediaItemDetailModel detailModel = NowPlayingGlobalModel.getInstance().getCurrentDetailModel();
                if (detailModel == null || (state != NowPlayingState.Disconnected && (state != NowPlayingState.Connecting || this.contentType != ContentType.None))) {
                    if (detailModel != null && state == NowPlayingState.Connecting) {
                        newContentType = this.contentType;
                        break;
                    }
                    this.nowPlayingHeader = NO_LAST_PLAYED;
                    newContentType = ContentType.None;
                    break;
                }
                this.nowPlayingHeader = LAST_PLAYED;
                if (!detailModel.isGameType()) {
                    newContentType = ContentType.App;
                    break;
                } else {
                    newContentType = ContentType.Game;
                    break;
                }
            case ConnectedPlayingApp:
                newContentType = ContentType.App;
                break;
            case ConnectedPlayingGame:
                newContentType = ContentType.Game;
                break;
            case ConnectedPlayingVideo:
                newContentType = ContentType.Media;
                break;
            case ConnectedPlayingMusic:
                newContentType = ContentType.Music;
                break;
            case ConnectedPlayingDash:
            case ConnectedPlayingDashMedia:
                newContentType = ContentType.Dash;
                break;
            default:
                XLEAssert.assertTrue("We should've captured all states.", false);
                break;
        }
        if (!(state == NowPlayingState.Disconnected || state == NowPlayingState.Connecting)) {
            this.nowPlayingHeader = NOW_PLAYING;
        }
        this.description = NowPlayingGlobalModel.getInstance().getDescription();
        this.nowPlayingTitle = NowPlayingGlobalModel.getInstance().getHeader();
        this.nowPlayingSubTitle = NowPlayingGlobalModel.getInstance().getSubHeader();
        this.providerName = NowPlayingGlobalModel.getInstance().getProviderName();
        this.nowplayingUri = NowPlayingGlobalModel.getInstance().getImageUri();
        XLELog.Diagnostic("NowPlayingVM", "set content type to " + newContentType.toString());
        this.contentType = newContentType;
    }

    private void updateRelatedListState(AsyncResult<UpdateData> asyncResult) {
        boolean hasError;
        UpdateData updateData = (UpdateData) asyncResult.getResult();
        if (asyncResult.getException() != null) {
            hasError = true;
        } else {
            hasError = false;
        }
        boolean hasData = false;
        boolean isLoading = false;
        switch (this.relatedContentType) {
            case Related:
                if (NowPlayingGlobalModel.getInstance().getCurrentDetailModel() != null) {
                    EDSV2MediaItemDetailModel detailModel = NowPlayingGlobalModel.getInstance().getCurrentDetailModel();
                    if (detailModel.getRelated() == null || detailModel.getRelated().size() <= 0) {
                        hasData = false;
                    } else {
                        hasData = true;
                    }
                    isLoading = detailModel.getIsLoadingRelated();
                    break;
                }
                break;
            case Album:
                if (this.currentArtistDetailModel != null) {
                    hasData = this.currentArtistDetailModel.getAlbums() != null && this.currentArtistDetailModel.getAlbums().size() > 0;
                    isLoading = this.currentArtistDetailModel.getIsLoadingChild();
                    break;
                }
                break;
            default:
                XLELog.Diagnostic("NowPlayingVM", "unsupported state " + this.relatedContentType);
                return;
        }
        if (hasError && !hasData) {
            this.listState = ListState.ErrorState;
        } else if (isLoading && !hasData) {
            this.listState = ListState.LoadingState;
        } else if (isLoading || hasData) {
            this.listState = ListState.ValidContentState;
        } else {
            this.listState = ListState.NoContentState;
        }
        XLELog.Diagnostic("NowPlayingVM", "related list state changed to " + this.listState);
    }

    private void updateQuickplayListState(AsyncResult<UpdateData> asyncResult) {
        boolean hasError;
        if (asyncResult.getException() != null) {
            hasError = true;
        } else {
            hasError = false;
        }
        boolean hasData;
        if (QuickplayModel.getInstance().getRecentQuickplayList() == null || QuickplayModel.getInstance().getRecentQuickplayList().size() <= 0) {
            hasData = false;
        } else {
            hasData = true;
        }
        boolean isLoading = QuickplayModel.getInstance().getIsLoading();
        if (hasError && !hasData) {
            this.quickplayListState = ListState.ErrorState;
        } else if (isLoading && !hasData) {
            this.quickplayListState = ListState.LoadingState;
        } else if (isLoading || hasData) {
            this.quickplayListState = ListState.ValidContentState;
        } else {
            this.quickplayListState = ListState.NoContentState;
        }
    }

    private void updateRelatedContentType() {
        EDSV2MediaItemDetailModel newDetailModel;
        EDSV2MusicTrackMediaItemWithAlbum trackitem;
        boolean z = true;
        XLELog.Diagnostic("NowPlayingVM", "Update related content type is called");
        RelatedContentType newRelatedContentType = RelatedContentType.None;
        switch (this.contentType) {
            case Media:
            case Game:
                newRelatedContentType = RelatedContentType.Related;
                break;
            case Music:
                newRelatedContentType = RelatedContentType.Album;
                break;
            case Dash:
                newRelatedContentType = RelatedContentType.QuickPlay;
                break;
            case App:
                newRelatedContentType = RelatedContentType.QuickPlay;
                break;
            case None:
                newRelatedContentType = RelatedContentType.None;
                break;
            default:
                XLEAssert.assertTrue(false);
                break;
        }
        boolean needToUpdateAndLoadArtistModel = false;
        XLELog.Diagnostic("NowPlayingVM", "new related content tye is " + newRelatedContentType);
        if (newRelatedContentType != this.relatedContentType) {
            if (newRelatedContentType == RelatedContentType.Album) {
                needToUpdateAndLoadArtistModel = true;
            } else if (this.relatedContentType == RelatedContentType.Album) {
                XLELog.Diagnostic("NowPlayingVM", "clean up current artist detail model");
                if (this.currentArtistDetailModel != null) {
                    this.currentArtistDetailModel.removeObserver(this);
                    this.currentArtistDetailModel = null;
                }
            }
        } else if (this.relatedContentType == RelatedContentType.Album) {
            boolean z2;
            if (this.currentArtistDetailModel != null) {
                z2 = true;
            } else {
                z2 = false;
            }
            XLEAssert.assertTrue(z2);
            newDetailModel = NowPlayingGlobalModel.getInstance().getCurrentDetailModel();
            if (newDetailModel != null) {
                z2 = true;
            } else {
                z2 = false;
            }
            XLEAssert.assertTrue(z2);
            trackitem = (EDSV2MusicTrackMediaItemWithAlbum) newDetailModel.getMediaItemDetailData();
            XLEAssert.assertNotNull(trackitem);
            if (!JavaUtil.stringsEqualCaseInsensitive(this.currentArtistDetailModel.getCanonicalId(), trackitem.getArtistCanonicalId())) {
                this.currentArtistDetailModel.removeObserver(this);
                needToUpdateAndLoadArtistModel = true;
            }
        }
        if (needToUpdateAndLoadArtistModel) {
            newDetailModel = NowPlayingGlobalModel.getInstance().getCurrentDetailModel();
            if (newDetailModel == null) {
                z = false;
            }
            XLEAssert.assertTrue(z);
            trackitem = (EDSV2MusicTrackMediaItemWithAlbum) newDetailModel.getMediaItemDetailData();
            XLEAssert.assertNotNull(trackitem);
            EDSV2MusicArtistMediaItem artistitem = new EDSV2MusicArtistMediaItem();
            artistitem.setCanonicalId(trackitem.getArtistCanonicalId());
            artistitem.setMediaType(EDSV2MediaType.MEDIATYPE_MUSICARTIST);
            this.currentArtistDetailModel = (EDSV2MusicArtistBrowseAlbumModel) EDSV2MediaItemModel.getModel(artistitem);
            this.currentArtistDetailModel.addObserver(this);
            ThreadManager.UIThreadPost(new Runnable() {
                public void run() {
                    if (NowPlayingActivityViewModel.this.currentArtistDetailModel != null) {
                        NowPlayingActivityViewModel.this.currentArtistDetailModel.load(false);
                    }
                }
            });
        }
        this.relatedContentType = newRelatedContentType;
        XLELog.Diagnostic("NowPlayingActivityVM", "set the content type to " + this.relatedContentType);
    }
}
