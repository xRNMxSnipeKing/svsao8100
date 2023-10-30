package com.microsoft.xbox.service.network.managers;

import com.microsoft.xbox.service.model.serialization.PrivacySettingsUploadRaw;
import com.microsoft.xbox.service.model.serialization.ProfileDataRaw;
import com.microsoft.xbox.service.model.serialization.ProfileDataUploadRaw;
import com.microsoft.xbox.toolkit.XLEException;

public interface IProfileServiceManager {

    public static class ProfileSections {
        public static final int ALL = 249;
        public static final int Friends = 128;
        public static final int PresenceInfo = 32;
        public static final int PrivacySettings = 64;
        public static final int RecentAchievements = 16;
        public static final int RecentGames = 8;
        public static final int XboxLiveProperties = 1;
    }

    ProfileDataRaw getData(String str, int i) throws XLEException;

    boolean savePrivacy(PrivacySettingsUploadRaw privacySettingsUploadRaw) throws XLEException;

    boolean saveProfile(ProfileDataUploadRaw profileDataUploadRaw) throws XLEException;
}
