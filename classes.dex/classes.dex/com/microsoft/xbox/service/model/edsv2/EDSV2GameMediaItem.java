package com.microsoft.xbox.service.model.edsv2;

import com.microsoft.xbox.service.model.JTitleType;
import com.microsoft.xbox.service.model.LaunchType;
import com.microsoft.xbox.service.model.MeProfileModel;
import com.microsoft.xbox.service.model.serialization.GameInfo;
import com.microsoft.xbox.service.model.sls.Title;
import com.microsoft.xbox.toolkit.XLEAssert;
import java.util.ArrayList;
import java.util.Date;

public class EDSV2GameMediaItem extends EDSV2MediaItem {
    private float averageUserRating;
    private String developer;
    private Date lastPlayedDate;
    private String parentalRating;
    private String publisher;
    private ArrayList<EDSV2RatingDescriptor> ratingDescriptors;
    private String ratingId;
    private ArrayList<String> slideshowImages;
    private int userRatingCount;

    public EDSV2GameMediaItem(EDSV2MediaItem source) {
        super(source);
        setMediaType(1);
    }

    public EDSV2GameMediaItem(GameInfo gameInfo) {
        setTitleId(gameInfo.Id);
        setTitle(gameInfo.Name);
        setImageUrl(gameInfo.ImageUri);
        setLastPlayedDate(gameInfo.LastPlayed);
        switch (gameInfo.Type) {
            case 1:
            case 2:
            case 8:
                setMediaType(58);
                return;
            default:
                setMediaType(1);
                return;
        }
    }

    public EDSV2GameMediaItem(Title title) {
        XLEAssert.assertNotNull(title);
        XLEAssert.assertTrue(title.IsGame());
        setTitleId(title.getTitleId());
        setTitle(title.getName());
        setImageUrl(title.getImageUrl(MeProfileModel.getModel().getLegalLocale()));
        setMediaType(1);
        setLastPlayedDate(title.getLastPlayed());
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

    public String getParentalRating() {
        return this.parentalRating;
    }

    public void setParentalRating(String parentalRating) {
        this.parentalRating = parentalRating;
    }

    public ArrayList<String> getSlideshowImages() {
        return this.slideshowImages;
    }

    public void setSlideshowImages(ArrayList<String> slideshowImages) {
        this.slideshowImages = slideshowImages;
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

    public void setLastPlayedDate(Date lastPlayedDate) {
        this.lastPlayedDate = lastPlayedDate;
    }

    public Date getLastPlayedDate() {
        return this.lastPlayedDate;
    }

    public ArrayList<EDSV2Provider> getProviders() {
        ArrayList<EDSV2Provider> providers = new ArrayList();
        EDSV2Provider gameProvider = new EDSV2Provider();
        gameProvider.setTitleId(getTitleId());
        gameProvider.setName(getTitle());
        gameProvider.setCanonicalId(getCanonicalId());
        EDSV2PartnerApplicationLaunchInfo gameProviderLaunchInfo = new EDSV2PartnerApplicationLaunchInfo();
        gameProviderLaunchInfo.setTitleId(getTitleId());
        gameProviderLaunchInfo.setLaunchType(LaunchType.GameLaunchType);
        gameProviderLaunchInfo.setTitleType(JTitleType.Standard);
        ArrayList<EDSV2PartnerApplicationLaunchInfo> launchInfoList = new ArrayList();
        launchInfoList.add(gameProviderLaunchInfo);
        gameProvider.setLaunchInfos(launchInfoList);
        providers.add(gameProvider);
        return providers;
    }
}
