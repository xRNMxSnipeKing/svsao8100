package com.microsoft.xbox.service.model.edsv2;

import java.net.URI;
import java.util.ArrayList;

public class EDSV2ActivityLaunchInfo {
    private URI activityUrl;
    private int requiresCapabilities;
    private int usesCapabilities;
    private ArrayList<String> whitelistUrls;

    public void setActivityUrl(URI url) {
        this.activityUrl = url;
    }

    public URI getActivityUrl() {
        return this.activityUrl;
    }

    public void setWhitelistUrls(ArrayList<String> urls) {
        this.whitelistUrls = urls;
    }

    public ArrayList<String> getWhitelistUrls() {
        return this.whitelistUrls;
    }

    public void setRequiresCapabilities(int capabilities) {
        this.requiresCapabilities = capabilities;
    }

    public int getRequiresCapabilities() {
        return this.requiresCapabilities;
    }

    public void setUsesCapabilities(int capabilities) {
        this.usesCapabilities = capabilities;
    }

    public int getUsesCapabilities() {
        return this.usesCapabilities;
    }

    public String getActivityUrlString() {
        URI activityUri = getActivityUrl();
        if (activityUri != null) {
            return activityUri.toString();
        }
        return null;
    }
}
