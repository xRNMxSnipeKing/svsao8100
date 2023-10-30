package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.JTitleType;
import com.microsoft.xbox.service.model.LaunchType;
import com.microsoft.xbox.service.model.MergedActivitySummaryModel;
import com.microsoft.xbox.service.model.NowPlayingGlobalModel;
import com.microsoft.xbox.service.model.NowPlayingGlobalModel.NowPlayingState;
import com.microsoft.xbox.service.model.SessionModel;
import com.microsoft.xbox.service.model.UpdateData;
import com.microsoft.xbox.service.model.UpdateType;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaItem;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaItemDetailModel;
import com.microsoft.xbox.service.model.edsv2.EDSV2PartnerApplicationLaunchInfo;
import com.microsoft.xbox.service.model.edsv2.EDSV2Provider;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.MediaProgressTimer.OnMediaProgressUpdatedListener;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.network.ListState;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.app.XLEUtil;
import com.microsoft.xbox.xle.app.XboxMobileOmnitureTracking;
import com.microsoft.xbox.xle.app.activity.XboxConsoleHelpActivity;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public abstract class EDSV2MediaItemDetailViewModel<T extends EDSV2MediaItemDetailModel> extends EDSV2MediaItemViewModel<T> {
    private boolean cachedNowPlaying = false;
    private boolean forceRefresh = false;
    protected boolean needToTryAddActivityPane = true;
    private boolean wasNowPlaying = false;

    public long getTitleId() {
        return ((EDSV2MediaItemDetailModel) this.mediaModel).getTitleId();
    }

    public String getTitle() {
        return ((EDSV2MediaItemDetailModel) this.mediaModel).getTitle();
    }

    public String getDescription() {
        return ((EDSV2MediaItemDetailModel) this.mediaModel).getDescription();
    }

    public URI getImageUrl() {
        return ((EDSV2MediaItemDetailModel) this.mediaModel).getImageUrl();
    }

    public String getReleaseYear() {
        Date date = ((EDSV2MediaItemDetailModel) this.mediaModel).getReleaseDate();
        if (date != null) {
            return new SimpleDateFormat("yyyy").format(date);
        }
        return null;
    }

    public String getReleaseDate() {
        return JavaUtil.getDateStringAsMonthDateYear(((EDSV2MediaItemDetailModel) this.mediaModel).getReleaseDate());
    }

    public String getParentalRating() {
        return ((EDSV2MediaItemDetailModel) this.mediaModel).getParentalRating();
    }

    public int getDurationInMinutes() {
        return ((EDSV2MediaItemDetailModel) this.mediaModel).getDurationInMinutes();
    }

    public int getMediaGroup() {
        return ((EDSV2MediaItemDetailModel) this.mediaModel).getMediaGroup();
    }

    public int getMediaType() {
        return ((EDSV2MediaItemDetailModel) this.mediaModel).getMediaType();
    }

    public boolean getHasActivities() {
        return MergedActivitySummaryModel.getInstance().getHasActivities();
    }

    public boolean isBlockingBusy() {
        return this.isLaunching;
    }

    public boolean isBusy() {
        return super.isBusy() || MergedActivitySummaryModel.getInstance().isBusy();
    }

    public boolean isMediaPaused() {
        return NowPlayingGlobalModel.getInstance().isMediaPaused();
    }

    public boolean isMediaInProgress() {
        return NowPlayingGlobalModel.getInstance().isMediaInProgress();
    }

    public boolean isNowPlaying() {
        return this.cachedNowPlaying;
    }

    public boolean shouldShowMediaProgressBar() {
        return isNowPlaying();
    }

    public boolean shouldShowMediaTransportControls() {
        if (XLEApplication.Instance.getIsTablet()) {
            return isMediaInProgress();
        }
        return shouldShowMediaProgressBar() || (this.wasNowPlaying && isMediaInProgress());
    }

    public boolean shouldShowProviderButtons() {
        return !isNowPlaying();
    }

    private LaunchType getLaunchType() {
        return ((EDSV2MediaItemDetailModel) this.mediaModel).getLaunchType();
    }

    private JTitleType getTitleType() {
        return ((EDSV2MediaItemDetailModel) this.mediaModel).getTitleType();
    }

    public void LaunchWithProviderInfo(EDSV2Provider provider) {
        ArrayList<EDSV2PartnerApplicationLaunchInfo> launchInfoList = provider.getLaunchInfos();
        if (launchInfoList == null || launchInfoList.size() <= 0) {
            XLELog.Error("SearchDetailPage", "There is no launch info for the time");
            return;
        }
        XboxMobileOmnitureTracking.TrackPlayOnXboxClick(Long.toString(provider.getTitleId()));
        final EDSV2PartnerApplicationLaunchInfo info = (EDSV2PartnerApplicationLaunchInfo) launchInfoList.get(0);
        runLaunchTaskWithConfirmation(info.getTitleId(), new Runnable() {
            public void run() {
                EDSV2MediaItemDetailViewModel.this.clearAutoLaunchFlagAndLaunchProvider(info.getTitleId(), EDSV2MediaItemDetailViewModel.this.getLaunchType().getValue(), info.getDeepLinkInfo());
                EDSV2MediaItemDetailViewModel.this.isLaunching = true;
                EDSV2MediaItemDetailViewModel.this.startLaunchTimeOut();
                EDSV2MediaItemDetailViewModel.this.adapter.updateView();
            }
        });
    }

    public void LaunchAppWithProviderInfo(EDSV2Provider provider) {
        launchTitleOnConsoleWithConfirmation(provider);
    }

    public ArrayList<EDSV2Provider> getProviders() {
        return ((EDSV2MediaItemDetailModel) this.mediaModel).getProviders();
    }

    public int getDefaultImageRid() {
        return XLEUtil.getMediaItemDefaultRid(((EDSV2MediaItemDetailModel) this.mediaModel).getMediaType());
    }

    public void setOnMediaProgressUpdatedListener(OnMediaProgressUpdatedListener listener) {
        NowPlayingGlobalModel.getInstance().setOnMediaProgressUpdatedRunnable(listener);
    }

    protected EDSV2MediaItem getCurrentScreenData() {
        return ((EDSV2MediaItemDetailModel) this.mediaModel).getMediaItemDetailData();
    }

    public void load(boolean forceRefresh) {
        if (forceRefresh) {
            this.needToTryAddActivityPane = true;
        }
        this.forceRefresh = forceRefresh;
        NowPlayingGlobalModel.getInstance().load(true);
        super.load(forceRefresh);
    }

    protected void onStartOverride() {
        super.onStartOverride();
        NowPlayingGlobalModel.getInstance().addObserver(this);
        SessionModel.getInstance().addObserver(this);
        MergedActivitySummaryModel.getInstance().addObserver(this);
    }

    protected void onStopOverride() {
        super.onStopOverride();
        NowPlayingGlobalModel.getInstance().removeObserver(this);
        SessionModel.getInstance().removeObserver(this);
        MergedActivitySummaryModel.getInstance().clearParentItem();
        MergedActivitySummaryModel.getInstance().removeObserver(this);
        this.wasNowPlaying = false;
    }

    public void updateOverride(AsyncResult<UpdateData> asyncResult) {
        super.updateOverride(asyncResult);
        UpdateType type = ((UpdateData) asyncResult.getResult()).getUpdateType();
        if (asyncResult.getException() != null) {
            cancelLaunchTimeout();
        }
        switch (type) {
            case NowPlayingState:
                if (((UpdateData) asyncResult.getResult()).getIsFinal() && NowPlayingGlobalModel.getInstance().getNowPlayingState() == NowPlayingState.Disconnected) {
                    cancelLaunchTimeout();
                    break;
                }
            case SessionLaunchRequestComplete:
                cancelLaunchTimeout();
                if (asyncResult.getException() != null) {
                    showMustActDialog(XLEApplication.Resources.getString(R.string.error), XLEApplication.Resources.getString(R.string.xbox_connect_error_launch), XLEApplication.Resources.getString(R.string.Dismiss), new Runnable() {
                        public void run() {
                        }
                    }, true);
                    break;
                } else if (asyncResult.getException() == null && ((UpdateData) asyncResult.getResult()).getIsFinal()) {
                    navigateToRemote(false);
                    return;
                }
            case SessionRequestFailure:
                if (this.isLaunching) {
                    XLELog.Diagnostic("SearchDetailsViewModel", "request failed because can't join session");
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
            case MergedActivitiesSummary:
                if (asyncResult.getException() == null && ((UpdateData) asyncResult.getResult()).getIsFinal()) {
                    addActivitiesPane();
                    break;
                }
            default:
                if (type == getDefaultUpdateType()) {
                    if (((UpdateData) asyncResult.getResult()).getIsFinal() && this.viewModelState == ListState.ValidContentState) {
                        loadActivities(this.forceRefresh);
                        break;
                    }
                }
                XLELog.Diagnostic("EDSV2MediaItemDetailViewModel", "Unexpceted update type " + type.toString());
                return;
        }
        calculateIsNowPlaying();
        this.adapter.updateView();
    }

    private void calculateIsNowPlaying() {
        this.cachedNowPlaying = NowPlayingGlobalModel.getInstance().isMediaItemNowPlaying(((EDSV2MediaItemDetailModel) this.mediaModel).getCanonicalId());
        if (this.cachedNowPlaying) {
            this.wasNowPlaying = true;
        }
    }

    protected boolean shouldAddActivitiesPane() {
        return false;
    }

    protected boolean shouldLoadActivities() {
        return ((EDSV2MediaItemDetailModel) this.mediaModel).getShouldCheckActivity();
    }

    protected void loadActivities(final boolean forceRefresh) {
        if (!shouldLoadActivities() || JavaUtil.isNullOrEmpty(((EDSV2MediaItemDetailModel) this.mediaModel).getCanonicalId())) {
            XLELog.Diagnostic("EDSV2MediaItemDetailViewModel", "No need to load activities");
            return;
        }
        XLELog.Diagnostic("EDSV2MediaItemDetailViewModel", "Loading activities");
        MergedActivitySummaryModel.getInstance().setParentItem(getCurrentScreenData());
        ThreadManager.UIThreadPost(new Runnable() {
            public void run() {
                MergedActivitySummaryModel.getInstance().load(forceRefresh);
            }
        });
    }

    protected void addActivitiesPane() {
        if (shouldAddActivitiesPane() && this.needToTryAddActivityPane && getHasActivities()) {
            XLELog.Diagnostic("EDSV2MediaItemDetailViewModel", "Adding activities pane");
            addActivitySummaryScreenToDetailsPivot();
            this.needToTryAddActivityPane = false;
            return;
        }
        XLELog.Diagnostic("EDSV2MediaItemDetailViewModel", "No need to add activities pane");
    }
}
