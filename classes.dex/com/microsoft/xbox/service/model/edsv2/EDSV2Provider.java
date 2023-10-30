package com.microsoft.xbox.service.model.edsv2;

import java.net.URI;
import java.util.ArrayList;

public class EDSV2Provider {
    private String canonicalId;
    private URI imageUrl;
    private ArrayList<EDSV2Image> images;
    private boolean isXboxMusic = false;
    private boolean isXboxVideo = false;
    private ArrayList<EDSV2PartnerApplicationLaunchInfo> launchInfos;
    private String name;
    private String partnerMediaId;
    private long titleId;

    public long getTitleId() {
        return this.titleId;
    }

    public void setTitleId(long id) {
        this.titleId = id;
    }

    public String getCanonicalId() {
        return this.canonicalId;
    }

    public void setCanonicalId(String id) {
        this.canonicalId = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPartnerMediaId() {
        return this.partnerMediaId;
    }

    public void setPartnerMediaId(String id) {
        this.partnerMediaId = id;
    }

    public URI getImageUrl() {
        return this.imageUrl;
    }

    public void setImageUrl(URI url) {
        this.imageUrl = url;
    }

    public ArrayList<EDSV2PartnerApplicationLaunchInfo> getLaunchInfos() {
        return this.launchInfos;
    }

    public void setLaunchInfos(ArrayList<EDSV2PartnerApplicationLaunchInfo> launchInfos) {
        this.launchInfos = launchInfos;
    }

    public ArrayList<EDSV2Image> getImages() {
        return this.images;
    }

    public void setImages(ArrayList<EDSV2Image> images) {
        this.images = images;
    }

    public boolean getIsXboxMusic() {
        return this.isXboxMusic;
    }

    public void setIsXboxMusic(boolean value) {
        this.isXboxMusic = value;
    }

    public boolean getIsXboxVideo() {
        return this.isXboxVideo;
    }

    public void setIsXboxVideo(boolean value) {
        this.isXboxVideo = value;
    }
}
