package com.microsoft.xbox.service.model;

import com.microsoft.xbox.service.model.serialization.PrivacySettings;
import com.microsoft.xbox.service.model.serialization.PrivacySettingsUploadRaw;
import com.microsoft.xbox.service.model.serialization.ProfileDataRaw;
import com.microsoft.xbox.service.model.serialization.ProfileDataUploadRaw;
import com.microsoft.xbox.service.model.serialization.ProfileProperties;
import com.microsoft.xbox.service.model.sls.GamerContext;
import com.microsoft.xbox.service.network.managers.IProfileServiceManager;
import com.microsoft.xbox.service.network.managers.ServiceManagerFactory;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.DataLoaderTask;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.toolkit.network.IDataLoaderRunnable;
import java.net.URI;
import java.util.Date;

public class MeProfileModel extends ModelBase<ProfileDataRaw> {
    public static final int ME_PROFILE_SECTIONS = 97;
    public static final int PRIVACY_BLOCKED = 2;
    public static final int PRIVACY_EVERYONE = 0;
    public static final int PRIVACY_FRIENDS = 1;
    private String combinedContentRating;
    private GamerContext gamerContext;
    private boolean initializeComplete;
    private boolean isLoadingBasicProfile;
    private boolean isLoadingContentRating;
    private boolean isLoadingGamerContext;
    private boolean isSaving;
    private PrivacySettings pendingPrivacyChanges;
    private ProfileProperties pendingProfileChanges;
    private MeProfileData profileData;
    private IProfileServiceManager serviceManager;
    private String xuid;

    private static class MeProfileModelHolder {
        public static MeProfileModel instance = new MeProfileModel();

        private MeProfileModelHolder() {
        }

        public static void reset() {
            instance = new MeProfileModel();
        }
    }

    private class GetCombinedContentRatingRunner extends IDataLoaderRunnable<String> {
        private GetCombinedContentRatingRunner() {
        }

        public void onPreExecute() {
        }

        public String buildData() throws XLEException {
            String rv = ServiceManagerFactory.getInstance().getEDSServiceManager().getCombinedContentRating();
            if (rv != null) {
                return rv;
            }
            throw new XLEException(XLEErrorCode.FAILED_TO_GET_COMBINED_CONTENT_RATING);
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_GET_COMBINED_CONTENT_RATING;
        }

        public void onPostExcute(AsyncResult<String> result) {
            MeProfileModel.this.onCombinedContentRatingCompleted(result);
        }
    }

    private class GetGamerContextRunner extends IDataLoaderRunnable<GamerContext> {
        private GetGamerContextRunner() {
        }

        public void onPreExecute() {
        }

        public GamerContext buildData() throws XLEException {
            return ServiceManagerFactory.getInstance().getSLSServiceManager().getGamerContext();
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_GET_GAMER_CONTEXT;
        }

        public void onPostExcute(AsyncResult<GamerContext> result) {
            MeProfileModel.this.onGetGamerContextCompleted(result);
        }
    }

    private class PrivacySaveRunner extends IDataLoaderRunnable<PrivacySettings> {
        PrivacySaveRunner() {
        }

        public void onPreExecute() {
        }

        public PrivacySettings buildData() throws XLEException {
            if (MeProfileModel.this.pendingPrivacyChanges == null) {
                throw new XLEException(10);
            }
            MeProfileModel.this.serviceManager.savePrivacy(new PrivacySettingsUploadRaw(MeProfileModel.this.pendingPrivacyChanges));
            return null;
        }

        public void onPostExcute(AsyncResult<PrivacySettings> result) {
            MeProfileModel.this.onSavePrivacyComplete(result);
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_SAVE_PRIVACY;
        }
    }

    private class ProfileSaveRunner extends IDataLoaderRunnable<ProfileProperties> {
        boolean isBadRequest = false;

        ProfileSaveRunner() {
        }

        public void onPreExecute() {
        }

        public ProfileProperties buildData() throws XLEException {
            if (MeProfileModel.this.pendingProfileChanges == null) {
                throw new XLEException(10);
            }
            try {
                MeProfileModel.this.serviceManager.saveProfile(new ProfileDataUploadRaw(MeProfileModel.this.pendingProfileChanges));
                return null;
            } catch (XLEException e) {
                if (e.getErrorCode() == 15) {
                    this.isBadRequest = true;
                }
                throw e;
            }
        }

        public void onPostExcute(AsyncResult<ProfileProperties> result) {
            MeProfileModel.this.onSaveProfileComplete(result);
        }

        public long getDefaultErrorCode() {
            if (this.isBadRequest) {
                return XLEErrorCode.FAILED_TO_SAVE_PROFILE_OFFENSIVE;
            }
            return XLEErrorCode.FAILED_TO_SAVE_PROFILE;
        }
    }

    private MeProfileModel() {
        this.profileData = new MeProfileData();
        this.initializeComplete = false;
        this.serviceManager = ServiceManagerFactory.getInstance().getProfileServiceManager();
    }

    public synchronized String getXuid() {
        return this.xuid;
    }

    public synchronized void setXuid(String xuid) {
        this.xuid = xuid;
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

    public URI getAvatarImageUrl() {
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

    public boolean getIsGold() {
        return this.profileData.getIsGold();
    }

    public String getMembershipLevel() {
        return this.profileData.getMembershipLevel();
    }

    public boolean getIsParentallyControlled() {
        return this.profileData.getIsParentallyControlled();
    }

    public int getVoiceAndTextSetting() {
        return this.profileData.getVoiceAndTextSetting();
    }

    public int getShareProfileSetting() {
        return this.profileData.getShareProfileSetting();
    }

    public int getShowOnlineStatusSetting() {
        return this.profileData.getShowOnlineStatusSetting();
    }

    public int getUseMemberContentSetting() {
        return this.profileData.getUseMemberContentSetting();
    }

    public int getShareGameHistorySetting() {
        return this.profileData.getShareGameHistorySetting();
    }

    public int getShowFriendsListSetting() {
        return this.profileData.getShowFriendsListSetting();
    }

    public void TESTsetProfile(String locale, String combinedContentRating) {
    }

    public String getLegalLocale() {
        if (this.gamerContext != null) {
            return this.gamerContext.legalLocale;
        }
        return null;
    }

    public String getCombinedContentRating() {
        return this.combinedContentRating;
    }

    public MessagingCapabilityStatus getCanComposeMessage() {
        if (getVoiceAndTextSetting() == 2) {
            return new MessagingCapabilityStatus(false, XboxApplication.Instance.getStringRValue("toast_message_setting_blocked"));
        }
        return new MessagingCapabilityStatus(true, -1);
    }

    public boolean getIsSaving() {
        return this.isSaving;
    }

    public boolean getIsLoading() {
        return this.isLoadingGamerContext || this.isLoadingBasicProfile || this.isLoadingContentRating;
    }

    public boolean getIsXboxMusicSupported() {
        if (getLegalLocale() == null || isLegalLocaleJapanese()) {
            return false;
        }
        return true;
    }

    public boolean isLegalLocaleJapanese() {
        if (getLegalLocale() == null) {
            return false;
        }
        return getLegalLocale().toLowerCase().endsWith("jp");
    }

    public boolean getInitializeComplete() {
        return this.initializeComplete;
    }

    public void load(boolean forceRefresh) {
        boolean z = true;
        if (getIsLoading() || !(forceRefresh || shouldRefresh())) {
            UpdateType updateType = UpdateType.MeProfileData;
            if (getIsLoading()) {
                z = false;
            }
            notifyObservers(new AsyncResult(new UpdateData(updateType, z), this, null));
            return;
        }
        this.isLoadingBasicProfile = true;
        this.isLoadingGamerContext = true;
        this.isLoadingContentRating = true;
        new DataLoaderTask(this.lastInvalidatedTick, new ProfileLoaderRunnable(this.serviceManager, this, null, 97)).execute();
        new DataLoaderTask(new GetGamerContextRunner()).execute();
        notifyObservers(new AsyncResult(new UpdateData(UpdateType.MeProfileData, false), this, null));
    }

    public void updateWithNewData(AsyncResult<ProfileDataRaw> asyncResult) {
        XLELog.Diagnostic("MeProfileModel", "basic profile data is loaded");
        if (asyncResult.getException() == null) {
            this.profileData = new MeProfileData((ProfileDataRaw) asyncResult.getResult());
            this.lastRefreshTime = new Date();
        }
        this.isLoadingBasicProfile = false;
        loadCombinedContentRatingIfNecessary();
        notifyObservers(new AsyncResult(new UpdateData(UpdateType.MeProfileData, true), this, asyncResult.getException()));
    }

    public boolean uploadNewProfileData(ProfileProperties newData) {
        if (newData == null) {
            return false;
        }
        this.pendingProfileChanges = newData;
        XLEAssert.assertFalse("We shouldn't be saving profile if we're already saving.", this.isSaving);
        this.isSaving = true;
        new DataLoaderTask(new ProfileSaveRunner()).execute();
        return true;
    }

    public boolean uploadNewPrivacyData(PrivacySettings newData) {
        if (newData == null) {
            return false;
        }
        this.pendingPrivacyChanges = newData;
        XLEAssert.assertFalse("We shouldn't be saving privacy settings if we're already saving.", this.isSaving);
        this.isSaving = true;
        new DataLoaderTask(new PrivacySaveRunner()).execute();
        return true;
    }

    public void onSaveProfileComplete(AsyncResult<ProfileProperties> saveResult) {
        if (saveResult.getException() == null) {
            this.profileData.updateWithNewData(this.pendingProfileChanges);
        }
        this.pendingProfileChanges = null;
        this.isSaving = false;
        notifyObservers(new AsyncResult(new UpdateData(UpdateType.MeProfileDataSave, true), this, saveResult.getException()));
    }

    public void onSavePrivacyComplete(AsyncResult<PrivacySettings> saveResult) {
        if (saveResult.getException() == null) {
            this.profileData.updateWithNewData(this.pendingPrivacyChanges);
        }
        this.pendingPrivacyChanges = null;
        this.isSaving = false;
        notifyObservers(new AsyncResult(new UpdateData(UpdateType.MePrivacySave, true), this, saveResult.getException()));
    }

    public static MeProfileModel getModel() {
        return MeProfileModelHolder.instance;
    }

    public static void reset() {
        getModel().clearObserver();
        MeProfileModelHolder.reset();
    }

    private void onGetGamerContextCompleted(AsyncResult<GamerContext> result) {
        XLELog.Diagnostic("MeProfileModel", "GetGamerContext Completed");
        if (result.getException() == null) {
            this.gamerContext = (GamerContext) result.getResult();
            XLELog.Diagnostic("MeProfileModel", "GetGamerContext loaded " + this.gamerContext.getLegalLocale());
        } else {
            XLELog.Diagnostic("MeProfileModel", "GetGamerContext failed with " + result.getException().toString());
        }
        this.isLoadingGamerContext = false;
        loadCombinedContentRatingIfNecessary();
        notifyObservers(new AsyncResult(new UpdateData(UpdateType.GamerContext, true), this, result.getException()));
    }

    private void loadCombinedContentRatingIfNecessary() {
        if (!this.isLoadingBasicProfile && !this.isLoadingGamerContext && !JavaUtil.isNullOrEmpty(getLegalLocale()) && !JavaUtil.isNullOrEmpty(getMembershipLevel())) {
            onCombinedContentRatingCompleted(new AsyncResult("", this, null));
        }
    }

    private void onCombinedContentRatingCompleted(AsyncResult<String> result) {
        XLELog.Diagnostic("MeProfileModel", "GetCombinedContentRating completed ");
        if (result.getException() == null) {
            this.combinedContentRating = (String) result.getResult();
            if (this.combinedContentRating != null) {
                XLELog.Diagnostic("MeProfileModel", "CombinedContentRating is " + this.combinedContentRating);
            }
            ServiceManagerFactory.getInstance().getEDSServiceManager().initializeServiceManager();
            this.initializeComplete = true;
        } else {
            XLELog.Diagnostic("MeProfileModel", "CombinedContentRating failed with " + result.getException().toString());
        }
        this.isLoadingContentRating = false;
        notifyObservers(new AsyncResult(new UpdateData(UpdateType.CombinedContentRating, true), this, result.getException()));
    }
}
