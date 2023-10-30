package com.microsoft.xbox.service.model;

import com.microsoft.xbox.service.model.serialization.GameInfo;
import com.microsoft.xbox.service.model.serialization.ProfileDataRaw;
import com.microsoft.xbox.service.network.managers.IProfileServiceManager;
import com.microsoft.xbox.service.network.managers.ServiceManagerFactory;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.FixedSizeHashtable;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import java.net.URI;
import java.util.ArrayList;
import java.util.Enumeration;

public class YouProfileModel extends ModelBase<ProfileDataRaw> {
    private static final int MAX_YOU_PROFILE_MODELS = 10;
    public static final int YOU_PROFILE_SECTIONS = 41;
    private static FixedSizeHashtable<String, YouProfileModel> profileModelCache = new FixedSizeHashtable(10);
    private YouProfileData profileData = new YouProfileData();
    private IProfileServiceManager serviceManager = ServiceManagerFactory.getInstance().getProfileServiceManager();

    private YouProfileModel(String gamerTag) {
        this.loaderRunnable = new ProfileLoaderRunnable(this.serviceManager, this, gamerTag, 41);
    }

    public String getGamertag() {
        return this.profileData.getGamertag();
    }

    public String getGamerscore() {
        return this.profileData.getGamerscore();
    }

    public String getMotto() {
        return this.profileData.getMotto();
    }

    public URI getAvatarImageUri() {
        return this.profileData.getAvatarImageUri();
    }

    public String getBio() {
        return this.profileData.getBio();
    }

    public URI getGamerPicUri() {
        return this.profileData.getGamerPicUri();
    }

    public URI getSmallGamerPicUri() {
        return this.profileData.getSmallGamerPicUri();
    }

    public String getLocation() {
        return this.profileData.getLocation();
    }

    public String getName() {
        return this.profileData.getName();
    }

    public String getMembershipLevel() {
        return this.profileData.getMembershipLevel();
    }

    public boolean getIsGold() {
        return this.profileData.getIsGold();
    }

    public ArrayList<GameInfo> getRecentGames() {
        return this.profileData.getRecentGames();
    }

    public void load(boolean forceRefresh) {
        loadInternal(forceRefresh, UpdateType.YouProfileData, this.loaderRunnable);
    }

    public void updateWithNewData(AsyncResult<ProfileDataRaw> asyncResult) {
        super.updateWithNewData(asyncResult);
        if (asyncResult.getException() == null) {
            this.profileData = new YouProfileData((ProfileDataRaw) asyncResult.getResult());
        }
        notifyObservers(new AsyncResult(new UpdateData(UpdateType.YouProfileData, true), this, asyncResult.getException()));
    }

    public static YouProfileModel getModel(String gamerTag) {
        boolean z;
        boolean z2 = true;
        if (Thread.currentThread() == ThreadManager.UIThread) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        String str = "Gamertag must not be empty";
        if (gamerTag == null || gamerTag.length() <= 0) {
            z2 = false;
        }
        XLEAssert.assertTrue(str, z2);
        YouProfileModel model = (YouProfileModel) profileModelCache.get(gamerTag);
        if (model != null) {
            return model;
        }
        model = new YouProfileModel(gamerTag);
        profileModelCache.put(gamerTag, model);
        return model;
    }

    public static void reset() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        Enumeration<YouProfileModel> e = profileModelCache.elements();
        while (e.hasMoreElements()) {
            ((YouProfileModel) e.nextElement()).clearObserver();
        }
        profileModelCache = new FixedSizeHashtable(10);
    }
}
