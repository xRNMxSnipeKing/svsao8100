package com.microsoft.xbox.service.model.edsv2;

import com.microsoft.xbox.service.model.JTitleType;
import com.microsoft.xbox.service.model.LaunchType;
import java.util.ArrayList;

public class EDSV2GameContentMediaItem extends EDSV2MediaItem {
    private static final String PROVIDERFORMAT = "ContentMediaId=%s;MediaId=%s;ContentMediaTypeId=%d";
    private float averageUserRating;
    private String developer;
    private String publisher;
    private ArrayList<EDSV2RatingDescriptor> ratingDescriptors;
    private String ratingId;
    private int userRatingCount;

    public EDSV2GameContentMediaItem(EDSV2MediaItem source) {
        super(source);
        setMediaType(18);
    }

    public String getDeveloper() {
        return this.developer;
    }

    public void setDeveloper(String developer) {
        this.developer = developer;
    }

    public String getPublisher() {
        return this.publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public float getAverageUserRating() {
        return this.averageUserRating;
    }

    public void setAverageUserRating(float averageUserRating) {
        this.averageUserRating = averageUserRating;
    }

    public int getUserRatingCount() {
        return this.userRatingCount;
    }

    public void setUserRatingCount(int userRatingCount) {
        this.userRatingCount = userRatingCount;
    }

    public String getRatingId() {
        return this.ratingId;
    }

    public void setRatingId(String ratingId) {
        this.ratingId = ratingId;
    }

    public ArrayList<EDSV2RatingDescriptor> getRatingDescriptors() {
        return this.ratingDescriptors;
    }

    public void setRatingDescriptors(ArrayList<EDSV2RatingDescriptor> ratingDescriptors) {
        this.ratingDescriptors = ratingDescriptors;
    }

    public void setProviders(ArrayList<EDSV2Provider> arrayList) {
        arrayList = new ArrayList();
        EDSV2Provider gameContentProvider = new EDSV2Provider();
        EDSV2PartnerApplicationLaunchInfo gameContentProviderLaunchInfo = new EDSV2PartnerApplicationLaunchInfo();
        gameContentProviderLaunchInfo.setDeepLinkInfo(String.format(PROVIDERFORMAT, new Object[]{getCanonicalId(), getParentCanonicalId(), Integer.valueOf(getMediaType())}));
        gameContentProviderLaunchInfo.setLaunchType(LaunchType.GameContentLaunchType);
        gameContentProviderLaunchInfo.setTitleType(JTitleType.Standard);
        ArrayList<EDSV2PartnerApplicationLaunchInfo> launchInfoList = new ArrayList();
        launchInfoList.add(gameContentProviderLaunchInfo);
        gameContentProvider.setLaunchInfos(launchInfoList);
        arrayList.add(gameContentProvider);
        super.setProviders(arrayList);
    }
}
