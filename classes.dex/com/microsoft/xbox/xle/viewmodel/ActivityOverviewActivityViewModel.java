package com.microsoft.xbox.xle.viewmodel;

import android.content.Intent;
import android.net.Uri;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.avatar.model.AvatarEditorModel;
import com.microsoft.xbox.service.model.ActivityDetailModel;
import com.microsoft.xbox.service.model.MeProfileModel;
import com.microsoft.xbox.service.model.UpdateData;
import com.microsoft.xbox.service.model.UpdateType;
import com.microsoft.xbox.service.model.edsv2.EDSV2ActivityItem;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaItem;
import com.microsoft.xbox.smartglass.canvas.CanvasComponents;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.toolkit.network.ListState;
import com.microsoft.xbox.toolkit.network.XboxLiveEnvironment;
import com.microsoft.xbox.xle.app.DeviceCapabilities;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.app.XboxMobileOmnitureTracking;
import com.microsoft.xbox.xle.app.adapter.AdapterFactory;
import java.net.URI;
import java.util.EnumSet;

public class ActivityOverviewActivityViewModel extends PivotViewModelBase {
    private EDSV2ActivityItem activityData = XLEGlobalData.getInstance().getSelectedActivityData();
    private int currentCapabilities = 0;
    private ActivityDetailModel detailModel;
    private String deviceRequirementString;
    private boolean forceRefreshUponReturn = XLEGlobalData.getInstance().getForceRefreshProfile();
    private EDSV2MediaItem parentMediaItem = XLEGlobalData.getInstance().getActivityParentMediaItemData();
    private ListState viewModelState = ListState.LoadingState;

    public ActivityOverviewActivityViewModel() {
        XLEAssert.assertNotNull(this.activityData);
        XLEAssert.assertNotNull(this.parentMediaItem);
        this.adapter = AdapterFactory.getInstance().getActivityOverviewAdapter(this);
    }

    public ListState getViewModelState() {
        return this.viewModelState;
    }

    public String getTitle() {
        return this.activityData.getTitle();
    }

    public String getParentTitle() {
        if (JavaUtil.isNullOrEmpty(this.activityData.getParentName())) {
            return null;
        }
        return String.format("%s %s", new Object[]{XLEApplication.Resources.getString(R.string.activity_parent_title_for), this.activityData.getParentName()});
    }

    public URI getTileImageUrl() {
        return this.activityData.getIconUrl();
    }

    public String getDescription() {
        return this.activityData.getDescription();
    }

    public String getProviderText() {
        if (JavaUtil.isNullOrEmpty(this.activityData.getProviderString())) {
            return this.activityData.getProviderString();
        }
        return String.format("%s %s", new Object[]{XLEApplication.Resources.getString(R.string.activity_providers), this.activityData.getProviderString()});
    }

    public String getDeviceRequirementString() {
        return this.deviceRequirementString;
    }

    public boolean shouldShowLaunchButton() {
        return this.activityData.isPurchased();
    }

    public String getPurchaseStateText() {
        return this.activityData.getPurchaseStateString();
    }

    public String getDisplayPurchasePrice() {
        return this.activityData.getDisplayPurchasePrice();
    }

    public boolean shouldShowUpgradeGoldButton() {
        return this.activityData.isGoldRequired();
    }

    public boolean shouldShowBuyButton() {
        return this.activityData.isPurchaseRequired();
    }

    public boolean isPurchaseVerificationFailed() {
        return !this.activityData.getIsPurchaseStatusVerified();
    }

    public boolean isBusy() {
        return this.detailModel.getIsLoading() || MeProfileModel.getModel().getIsLoading();
    }

    public void load(boolean forceRefresh) {
        forceRefresh = forceRefresh || this.forceRefreshUponReturn;
        this.forceRefreshUponReturn = false;
        if (forceRefresh) {
            setUpdateTypesToCheck(EnumSet.of(UpdateType.MeProfileData));
            MeProfileModel.getModel().load(forceRefresh);
            return;
        }
        setUpdateTypesToCheck(EnumSet.of(UpdateType.ActivityDetail));
        this.detailModel.load(forceRefresh);
    }

    protected void onStartOverride() {
        this.detailModel = ActivityDetailModel.getModel(this.activityData, this.parentMediaItem);
        this.detailModel.addObserver(this);
        MeProfileModel.getModel().addObserver(this);
        XLEGlobalData.getInstance().setSelectedActivityData(this.activityData);
        XLEGlobalData.getInstance().setActivityParentMediaItemData(this.parentMediaItem);
        XLEGlobalData.getInstance().setForceRefreshProfile(this.forceRefreshUponReturn);
    }

    protected void onStopOverride() {
        this.detailModel.removeObserver(this);
        this.detailModel = null;
        MeProfileModel.getModel().removeObserver(this);
        XLEGlobalData.getInstance().setForceRefreshProfile(this.forceRefreshUponReturn);
    }

    public void onRehydrate() {
        this.adapter = AdapterFactory.getInstance().getActivityOverviewAdapter(this);
    }

    public void updateOverride(AsyncResult<UpdateData> asyncResult) {
        switch (((UpdateData) asyncResult.getResult()).getUpdateType()) {
            case MeProfileData:
                if (((UpdateData) asyncResult.getResult()).getIsFinal() && asyncResult.getException() == null) {
                    setUpdateTypesToCheck(EnumSet.of(UpdateType.ActivityDetail));
                    this.detailModel.load(true);
                    break;
                }
            case ActivityDetail:
                if (((UpdateData) asyncResult.getResult()).getIsFinal()) {
                    if (asyncResult.getException() != null && this.detailModel.getActivityData() == null) {
                        this.viewModelState = ListState.ErrorState;
                        break;
                    }
                    this.viewModelState = ListState.ValidContentState;
                    this.activityData = this.detailModel.getActivityData();
                    if (this.currentCapabilities != this.activityData.getActivityLaunchInfo().getRequiresCapabilities()) {
                        this.currentCapabilities = this.activityData.getActivityLaunchInfo().getRequiresCapabilities();
                        updateCapabilitiesStrings();
                    }
                    if (this.activityData.getScreenshots() != null && this.activityData.getScreenshots().size() > 0) {
                        addActivityGalleryScreenToDetailsPivot(this.activityData);
                        break;
                    }
                }
                break;
        }
        this.adapter.updateView();
    }

    protected void onUpdateFinished() {
        if (checkErrorCode(UpdateType.MeProfileData, XLEErrorCode.FAILED_TO_GET_ME_PROFILE) || checkErrorCode(UpdateType.ActivityDetail, XLEErrorCode.FAILED_TO_GET_ACTIVITY_DETAIL)) {
            showError(R.string.toast_activity_overview_error);
        }
        super.onUpdateFinished();
    }

    private void updateCapabilitiesStrings() {
        this.deviceRequirementString = null;
        if (JavaUtil.containsFlag(this.currentCapabilities, CanvasComponents.Accelerometer.getValue())) {
            this.deviceRequirementString = JavaUtil.concatenateStringsWithDelimiter(this.deviceRequirementString, XLEApplication.Resources.getString(R.string.activity_capability_accelerometer), null, XLEApplication.Resources.getString(R.string.comma_delimiter), false);
        }
        if (JavaUtil.containsFlag(this.currentCapabilities, CanvasComponents.Gyroscope.getValue())) {
            this.deviceRequirementString = JavaUtil.concatenateStringsWithDelimiter(this.deviceRequirementString, XLEApplication.Resources.getString(R.string.activity_capability_gyroscope), null, XLEApplication.Resources.getString(R.string.comma_delimiter), false);
        }
        if (JavaUtil.containsFlag(this.currentCapabilities, CanvasComponents.Location.getValue())) {
            this.deviceRequirementString = JavaUtil.concatenateStringsWithDelimiter(this.deviceRequirementString, XLEApplication.Resources.getString(R.string.activity_capability_location), null, XLEApplication.Resources.getString(R.string.comma_delimiter), false);
        }
        if (JavaUtil.containsFlag(this.currentCapabilities, CanvasComponents.Haptic.getValue())) {
            this.deviceRequirementString = JavaUtil.concatenateStringsWithDelimiter(this.deviceRequirementString, XLEApplication.Resources.getString(R.string.activity_capability_haptic), null, XLEApplication.Resources.getString(R.string.comma_delimiter), false);
        }
        if (!JavaUtil.isNullOrEmpty(this.deviceRequirementString)) {
            this.deviceRequirementString = JavaUtil.concatenateStringsWithDelimiter(XLEApplication.Resources.getString(R.string.activity_devide_requirements), this.deviceRequirementString, null, XLEApplication.Resources.getString(R.string.colon_delimiter), false);
        }
    }

    public void upgradeToGold() {
        final String url = XboxLiveEnvironment.Instance().getSubscriptionPurchaseUrl();
        final String type = Integer.toString(this.activityData.getMediaType());
        final String title = this.activityData.getTitle();
        final String id = this.activityData.getCanonicalId();
        XboxMobileOmnitureTracking.TrackIntentToPurchaseSubscription("BuyGold", type, title, id);
        showOkCancelDialog(XLEApplication.Resources.getString(meetsDeviceCapabilities() ? R.string.activity_overview_upgrade_gold_dialog : R.string.activity_overview_buy_device_capabilities_failed_dialog), XLEApplication.Resources.getString(R.string.OK), new Runnable() {
            public void run() {
                ActivityOverviewActivityViewModel.this.forceRefreshUponReturn = true;
                XLELog.Diagnostic("ActivityOverviewVM", "Launching browser for purchasing subscription: " + url);
                XboxMobileOmnitureTracking.TrackIntentToPurchaseSubscription("StartBuyGoldXboxDotCom", type, title, id);
                Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(url));
                browserIntent.addFlags(AvatarEditorModel.AVATAREDIT_OPTION_COLOR_FACIAL_FEATURE);
                XboxApplication.Instance.startActivity(browserIntent);
            }
        }, XLEApplication.Resources.getString(R.string.Cancel), null);
    }

    public void purchaseActivity() {
        final String url = XboxLiveEnvironment.Instance().getActivityPurchaseUrl(MeProfileModel.getModel().getLegalLocale(), this.activityData.getCanonicalId());
        final String type = Integer.toString(this.activityData.getMediaType());
        final String title = this.activityData.getTitle();
        final String id = this.activityData.getCanonicalId();
        XboxMobileOmnitureTracking.TrackIntentToPurchase("BuyButton", type, title, id);
        showOkCancelDialog(XLEApplication.Resources.getString(meetsDeviceCapabilities() ? R.string.activity_overview_buy_dialog : R.string.activity_overview_buy_device_capabilities_failed_dialog), XLEApplication.Resources.getString(R.string.OK), new Runnable() {
            public void run() {
                ActivityOverviewActivityViewModel.this.forceRefreshUponReturn = true;
                XLELog.Diagnostic("ActivityOverviewVM", "Launching browser for purchasing activity: " + url);
                XboxMobileOmnitureTracking.TrackIntentToPurchase("CheckoutXboxDotCom", type, title, id);
                Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(url));
                browserIntent.addFlags(AvatarEditorModel.AVATAREDIT_OPTION_COLOR_FACIAL_FEATURE);
                XboxApplication.Instance.startActivity(browserIntent);
            }
        }, XLEApplication.Resources.getString(R.string.Cancel), null);
    }

    private boolean meetsDeviceCapabilities() {
        return DeviceCapabilities.getInstance().checkDeviceRequirements(this.activityData.getActivityLaunchInfo().getRequiresCapabilities());
    }

    public void launchActivity() {
        XboxMobileOmnitureTracking.TrackLaunchActivity("Manual", "ActivityOverview", Integer.toString(this.activityData.getMediaType()), this.activityData.getTitle(), this.activityData.getCanonicalId(), "false");
        checkDeviceRequirementAndLaunchActivity(this.parentMediaItem, this.activityData);
    }

    public void showGoldRequiredError() {
        showError(R.string.toast_message_send_freemember);
    }
}
