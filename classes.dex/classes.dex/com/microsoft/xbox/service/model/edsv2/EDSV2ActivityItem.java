package com.microsoft.xbox.service.model.edsv2;

import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.XLEConstants;
import com.microsoft.xbox.toolkit.XboxApplication;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import org.codehaus.jackson.util.MinimalPrettyPrinter;

public class EDSV2ActivityItem extends EDSV2MediaItem {
    private static final String XBOX_MUSIC_ACTIVITY_TOKEN = "music";
    private static final String XBOX_VIDEO_ACTIVITY_TOKEN = "video";
    private EDSV2ActivityLaunchInfo activityLaunchInfo;
    private int activityType;
    private ArrayList<Integer> allowedTitleIds;
    private String deepLink;
    private String displayPurchasePrice;
    private URI icon2x1Url;
    private URI iconUrl;
    private boolean isProviderSpecific;
    private boolean isPurchaseStatusVerified;
    private boolean isXboxMusicOrVideoActivity;
    private String priceString;
    private ArrayList<EDSV2ActivityProviderPolicy> providerPolicies;
    private String providerString;
    private String purchaseStateString;
    private int purchaseStatus;
    private ArrayList<URI> screenshots;
    private URI splashImageUrl;

    public void setActivityType(int type) {
        this.activityType = type;
    }

    public void setScreenshots(ArrayList<EDSV2Image> images) {
        if (images != null && images.size() > 0) {
            this.screenshots = new ArrayList();
            Iterator i$ = images.iterator();
            while (i$.hasNext()) {
                this.screenshots.add(((EDSV2Image) i$.next()).getUrl());
            }
        }
    }

    public ArrayList<URI> getScreenshots() {
        return this.screenshots;
    }

    public void setActivityLaunchInfo(EDSV2ActivityLaunchInfo launchInfo) {
        this.activityLaunchInfo = launchInfo;
    }

    public EDSV2ActivityLaunchInfo getActivityLaunchInfo() {
        return this.activityLaunchInfo;
    }

    public void setDeepLink(String link) {
        this.deepLink = link;
    }

    public void setDisplayPurchasePrice(String price) {
        this.displayPurchasePrice = price;
    }

    public String getDisplayPurchasePrice() {
        return this.displayPurchasePrice;
    }

    public void setPurchaseStatus(int status) {
        this.purchaseStatus = status;
    }

    public int getPurchaseStatus() {
        return this.purchaseStatus;
    }

    public void setIsPurchaseStatusVerified(boolean verified) {
        this.isPurchaseStatusVerified = verified;
    }

    public boolean getIsPurchaseStatusVerified() {
        return this.isPurchaseStatusVerified;
    }

    public void setIsProviderSpecific(boolean isProviderSpecific) {
        this.isProviderSpecific = isProviderSpecific;
    }

    public boolean getIsProviderSpecific() {
        return this.isProviderSpecific;
    }

    public void setIconUrl(URI uri) {
        this.iconUrl = uri;
    }

    public URI getIconUrl() {
        return this.iconUrl;
    }

    public void setIcon2x1Url(URI uri) {
        this.icon2x1Url = uri;
    }

    public URI getIcon2x1Url() {
        return this.icon2x1Url;
    }

    public void setSplashImageUrl(URI uri) {
        this.splashImageUrl = uri;
    }

    public URI getSplashImageUrl() {
        return this.splashImageUrl;
    }

    public void setProviderPolicies(ArrayList<EDSV2ActivityProviderPolicy> policies) {
        this.providerPolicies = policies;
        this.allowedTitleIds = new ArrayList();
        if (this.providerPolicies != null) {
            Iterator i$ = this.providerPolicies.iterator();
            while (i$.hasNext()) {
                this.allowedTitleIds.add(Integer.valueOf((int) ((EDSV2ActivityProviderPolicy) i$.next()).getTitleId()));
            }
        }
    }

    public ArrayList<EDSV2ActivityProviderPolicy> getProviderPolicies() {
        return this.providerPolicies;
    }

    public void setIsXboxMusicOrVideoActivity(boolean isXboxMusicOrVideoActivity) {
        this.isXboxMusicOrVideoActivity = isXboxMusicOrVideoActivity;
    }

    public boolean isValidActivity() {
        boolean urlResolved;
        if (getActivityLaunchInfo() == null || getActivityLaunchInfo().getActivityUrl() == null) {
            urlResolved = false;
        } else {
            urlResolved = true;
        }
        boolean validPurchaseState;
        if (getPurchaseStatus() != 0) {
            validPurchaseState = true;
        } else {
            validPurchaseState = false;
        }
        if (urlResolved && validPurchaseState) {
            return true;
        }
        return false;
    }

    public boolean isXboxMusicActivity() {
        return isValidActivity() && this.isXboxMusicOrVideoActivity && getActivityLaunchInfo().getActivityUrlString().toLowerCase().contains(XBOX_MUSIC_ACTIVITY_TOKEN.toLowerCase());
    }

    public boolean isXboxVideoActivity() {
        return isValidActivity() && this.isXboxMusicOrVideoActivity && getActivityLaunchInfo().getActivityUrlString().toLowerCase().contains(XBOX_VIDEO_ACTIVITY_TOKEN.toLowerCase());
    }

    public boolean isDefaultForAtLeastOneProvider() {
        if (this.providerPolicies != null && this.providerPolicies.size() > 0) {
            Iterator i$ = this.providerPolicies.iterator();
            while (i$.hasNext()) {
                if (((EDSV2ActivityProviderPolicy) i$.next()).getIsDefault()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isDefaultForProvider(long providerTitleId) {
        if (this.providerPolicies != null && this.providerPolicies.size() > 0) {
            Iterator i$ = this.providerPolicies.iterator();
            while (i$.hasNext()) {
                EDSV2ActivityProviderPolicy policy = (EDSV2ActivityProviderPolicy) i$.next();
                if (policy.getTitleId() == providerTitleId && policy.getIsDefault()) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getPriceString() {
        if (JavaUtil.isNullOrEmpty(this.priceString)) {
            switch (getPurchaseStatus()) {
                case 1:
                case 2:
                    this.priceString = XboxApplication.Resources.getString(XboxApplication.Instance.getStringRValue("activity_price_free"));
                    break;
                case 3:
                case 4:
                    this.priceString = XboxApplication.Resources.getString(XboxApplication.Instance.getStringRValue("activity_price_purchased"));
                    break;
                case 5:
                case 6:
                    this.priceString = this.displayPurchasePrice;
                    break;
            }
        }
        return this.priceString;
    }

    public String getPurchaseStateString() {
        if (JavaUtil.isNullOrEmpty(this.purchaseStateString)) {
            switch (getPurchaseStatus()) {
                case 1:
                    if (!isParentPurchaseRequired()) {
                        this.purchaseStateString = XboxApplication.Resources.getString(XboxApplication.Instance.getStringRValue("activity_price_free"));
                        break;
                    }
                    this.purchaseStateString = XboxApplication.Resources.getString(XboxApplication.Instance.getStringRValue("activity_price_free_with_video"));
                    break;
                case 2:
                    this.purchaseStateString = XboxApplication.Resources.getString(XboxApplication.Instance.getStringRValue("activity_price_free_upgrade_gold"));
                    break;
                case 3:
                case 4:
                    this.purchaseStateString = XboxApplication.Resources.getString(XboxApplication.Instance.getStringRValue("activity_price_purchased"));
                    break;
                case 5:
                case 6:
                    this.purchaseStateString = XboxApplication.Resources.getString(XboxApplication.Instance.getStringRValue("activity_price_buy"));
                    break;
            }
        }
        return this.purchaseStateString;
    }

    public boolean isPurchased() {
        return getPurchaseStatus() == 1 || getPurchaseStatus() == 3;
    }

    public boolean canAutoLaunch() {
        return isPurchased() && !isParentPurchaseRequired();
    }

    public boolean isGoldRequired() {
        return getPurchaseStatus() == 2 || getPurchaseStatus() == 6 || getPurchaseStatus() == 4;
    }

    public boolean isPurchaseRequired() {
        return getPurchaseStatus() == 6 || getPurchaseStatus() == 5;
    }

    public String getProviderString() {
        if (JavaUtil.isNullOrEmpty(this.providerString)) {
            if (isXboxMusicActivity()) {
                this.providerString = XboxApplication.Resources.getString(XboxApplication.Instance.getStringRValue("xbox_music_title"));
            } else if (isXboxVideoActivity()) {
                this.providerString = XboxApplication.Resources.getString(XboxApplication.Instance.getStringRValue("xbox_video_title"));
            } else if (this.isProviderSpecific && this.providerPolicies != null) {
                StringBuilder sb = new StringBuilder();
                Iterator i$ = this.providerPolicies.iterator();
                while (i$.hasNext()) {
                    EDSV2ActivityProviderPolicy policy = (EDSV2ActivityProviderPolicy) i$.next();
                    if (sb.length() > 0 && !JavaUtil.isNullOrEmpty(policy.getTitle())) {
                        sb.append(XboxApplication.Resources.getString(XboxApplication.Instance.getStringRValue("comma_delimiter")) + MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR);
                    }
                    sb.append(policy.getTitle());
                }
                this.providerString = sb.toString();
            }
        }
        return this.providerString;
    }

    public boolean supportsProvider(long providerTitleId) {
        if (this.providerPolicies == null || !this.allowedTitleIds.contains(Integer.valueOf((int) providerTitleId))) {
            return false;
        }
        return true;
    }

    public boolean isParentPurchaseRequired() {
        if (this.providerPolicies != null) {
            Iterator i$ = this.providerPolicies.iterator();
            while (i$.hasNext()) {
                EDSV2ActivityProviderPolicy policy = (EDSV2ActivityProviderPolicy) i$.next();
                if (policy.getTitleId() == XLEConstants.ZUNE_TITLE_ID) {
                    return policy.getRequiresParentPurchase();
                }
            }
        }
        return false;
    }

    public ArrayList<Integer> getAllowedTitleIds() {
        return this.allowedTitleIds;
    }
}
