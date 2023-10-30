package com.microsoft.xbox.service.model.activities;

import java.util.ArrayList;

public class ActivityLaunchInfo {
    public static final int SGActivityInteractionMode_OfflineInteractive = 2;
    public static final int SGActivityInteractionMode_OnlineInteractive = 0;
    public static final int SGActivityInteractionMode_OnlineOnly = 1;
    private String activityUrl;
    private int interactionMode;
    private int requiredCapabilities;
    private int usesCapabilities;
    private ArrayList<String> whitelistUrls;

    public String getActivityUrl() {
        return this.activityUrl;
    }

    public void setActivityUrl(String activityUrl) {
        this.activityUrl = activityUrl;
    }

    public ArrayList<String> getWhitelistUrls() {
        return this.whitelistUrls;
    }

    public void setWhitelistUrls(ArrayList<String> whitelistUrls) {
        this.whitelistUrls = whitelistUrls;
    }

    public int getRequiredCapabilities() {
        return this.requiredCapabilities;
    }

    public void setRequiredCapabilities(int requiredCapabilities) {
        this.requiredCapabilities = requiredCapabilities;
    }

    public int getUsesCapabilities() {
        return this.usesCapabilities;
    }

    public void setUsesCapabilities(int usesCapabilities) {
        this.usesCapabilities = usesCapabilities;
    }

    public int getInteractionMode() {
        return this.interactionMode;
    }

    public void setInteractionMode(int interactionMode) {
        this.interactionMode = interactionMode;
    }
}
