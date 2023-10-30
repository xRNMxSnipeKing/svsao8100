package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.MergedActivitySummaryModel;
import com.microsoft.xbox.service.model.NowPlayingGlobalModel;
import com.microsoft.xbox.service.model.SessionModel;
import com.microsoft.xbox.service.model.UpdateData;
import com.microsoft.xbox.service.model.UpdateType;
import com.microsoft.xbox.service.model.edsv2.EDSV2ActivityItem;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaItem;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaItemModel;
import com.microsoft.xbox.service.model.edsv2.EDSV2Provider;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.toolkit.network.ListState;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.app.XboxMobileOmnitureTracking;
import com.microsoft.xbox.xle.app.adapter.AdapterFactory;
import java.util.ArrayList;
import java.util.Iterator;

public class ActivitySummaryActivityViewModel extends PivotViewModelBase {
    private static final String PIPE_DELIMITER = XboxApplication.Resources.getString(R.string.pipe_delimiter);
    private static final String PROVIDER_SPECIFIC = XboxApplication.Resources.getString(R.string.activity_provider_specific);
    private EDSV2MediaItemModel parentDetailModel;
    private ListState viewModelState;

    public ActivitySummaryActivityViewModel() {
        this.viewModelState = ListState.LoadingState;
        this.adapter = AdapterFactory.getInstance().getActivitySummaryAdapter(this);
        EDSV2MediaItem selectedMediaItem = XLEGlobalData.getInstance().getSelectedMediaItemData();
        XLEAssert.assertNotNull(selectedMediaItem);
        this.parentDetailModel = EDSV2MediaItemModel.getModel(selectedMediaItem);
    }

    private EDSV2MediaItem getScreenData() {
        XLEAssert.assertNotNull(MergedActivitySummaryModel.getInstance().getParentItem());
        return MergedActivitySummaryModel.getInstance().getParentItem();
    }

    protected void onStartOverride() {
        MergedActivitySummaryModel.getInstance().addObserver(this);
        NowPlayingGlobalModel.getInstance().addObserver(this);
        this.parentDetailModel.addObserver(this);
    }

    protected void onStopOverride() {
        MergedActivitySummaryModel.getInstance().removeObserver(this);
        NowPlayingGlobalModel.getInstance().removeObserver(this);
        this.parentDetailModel.removeObserver(this);
    }

    public void onRehydrate() {
        this.adapter = AdapterFactory.getInstance().getActivitySummaryAdapter(this);
    }

    public ArrayList<EDSV2ActivityItem> getActivitiesList() {
        return MergedActivitySummaryModel.getInstance().getActivitiesList();
    }

    public ListState getViewModelState() {
        return this.viewModelState;
    }

    public String getTitle() {
        return MergedActivitySummaryModel.getInstance().getParentTitle();
    }

    public EDSV2ActivityItem getFeaturedActivity() {
        return MergedActivitySummaryModel.getInstance().getFeaturedActivity();
    }

    public String getProviderPriceStringForActivity(EDSV2ActivityItem activity) {
        String providerSpecific;
        boolean isProviderSpecific = false;
        if (getScreenData() != null && getScreenData().getProviders() != null && getScreenData().getProviders().size() > 0) {
            Iterator i$ = getScreenData().getProviders().iterator();
            while (i$.hasNext()) {
                if (!activity.supportsProvider(((EDSV2Provider) i$.next()).getTitleId())) {
                    isProviderSpecific = true;
                    break;
                }
            }
        }
        if (isProviderSpecific) {
            providerSpecific = PROVIDER_SPECIFIC;
        } else {
            providerSpecific = null;
        }
        return JavaUtil.concatenateStringsWithDelimiter(activity.getPriceString(), providerSpecific, null, PIPE_DELIMITER, true);
    }

    public boolean isBusy() {
        return MergedActivitySummaryModel.getInstance().isBusy();
    }

    public void updateOverride(AsyncResult<UpdateData> asyncResult) {
        UpdateType type = ((UpdateData) asyncResult.getResult()).getUpdateType();
        switch (type) {
            case MediaItemDetail:
                if (!(asyncResult.getException() == null || MergedActivitySummaryModel.getInstance().getHasActivities())) {
                    this.viewModelState = ListState.ErrorState;
                    break;
                }
            case MergedActivitiesSummary:
                if (asyncResult.getException() == null || MergedActivitySummaryModel.getInstance().getHasActivities()) {
                    if (!((UpdateData) asyncResult.getResult()).getIsFinal()) {
                        if (!MergedActivitySummaryModel.getInstance().getHasActivities()) {
                            this.viewModelState = ListState.LoadingState;
                            break;
                        }
                    } else if (!MergedActivitySummaryModel.getInstance().getHasActivities()) {
                        this.viewModelState = ListState.NoContentState;
                        break;
                    } else {
                        this.viewModelState = ListState.ValidContentState;
                        break;
                    }
                }
                this.viewModelState = ListState.ErrorState;
                break;
                break;
            default:
                XLELog.Diagnostic("ActivitySummaryVM", "Unexpected update type: " + type.toString());
                break;
        }
        this.adapter.updateView();
    }

    public void load(boolean forceRefresh) {
        MergedActivitySummaryModel.getInstance().load(forceRefresh);
    }

    private boolean isNowPlaying() {
        return JavaUtil.stringsEqualCaseInsensitive(NowPlayingGlobalModel.getInstance().getCurrentNowPlayingIdentifier(), MergedActivitySummaryModel.getInstance().getParentItemCanonicalId());
    }

    public boolean shouldGreyOutActivity(EDSV2ActivityItem activity) {
        if (!isNowPlaying() || activity.supportsProvider(SessionModel.getInstance().getCurrentTitleId())) {
            return false;
        }
        return true;
    }

    public void navigateToDetails(EDSV2ActivityItem activity) {
        navigateToActivityDetails(getScreenData(), activity);
    }

    public void checkRelevantAndLaunchActivity(final EDSV2ActivityItem activityData) {
        XLEAssert.assertTrue("Launch button should have been hidden", activityData.isPurchased());
        if (shouldGreyOutActivity(activityData)) {
            Runnable okHandler = new Runnable() {
                public void run() {
                    XboxMobileOmnitureTracking.TrackLaunchActivity("Manual", "ActivitySummary", Integer.toString(activityData.getMediaType()), activityData.getTitle(), activityData.getCanonicalId(), "false");
                    ActivitySummaryActivityViewModel.this.checkDeviceRequirementAndLaunchActivity(ActivitySummaryActivityViewModel.this.getScreenData(), activityData);
                }
            };
            showOkCancelDialog(XLEApplication.Resources.getString(R.string.activity_play_provider_mismatch), XLEApplication.Resources.getString(R.string.Yes), okHandler, XLEApplication.Resources.getString(R.string.No), null);
            return;
        }
        XboxMobileOmnitureTracking.TrackLaunchActivity("Manual", "ActivitySummary", Integer.toString(activityData.getMediaType()), activityData.getTitle(), activityData.getCanonicalId(), "true");
        checkDeviceRequirementAndLaunchActivity(getScreenData(), activityData);
    }
}
