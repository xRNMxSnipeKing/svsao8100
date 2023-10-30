package com.microsoft.xbox.service.model;

import com.microsoft.xbox.service.model.serialization.PrivacySettings;
import com.microsoft.xbox.service.model.serialization.ProfileDataRaw;
import com.microsoft.xbox.service.model.serialization.ProfileProperties;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xle.test.interop.TestInterop;
import java.net.URI;

public final class MeProfileData {
    private final URI avatarImageUri;
    private String bio;
    private final URI gamerPicUri;
    private final String gamerscore;
    private final String gamertag;
    private final boolean isGold;
    private final boolean isParentallyControlled;
    private String location;
    private final String membershipLevel;
    private String motto;
    private String name;
    private int shareGameHistorySetting;
    private int shareProfileSetting;
    private int showFriendsListSetting;
    private int showOnlineStatusSetting;
    private final URI smallGamerPicUri;
    private int useMemberContentSetting;
    private int voiceAndTextSetting;

    public MeProfileData() {
        this.gamertag = "";
        this.gamerscore = "";
        this.motto = "";
        this.avatarImageUri = null;
        this.bio = "";
        this.gamerPicUri = null;
        this.smallGamerPicUri = null;
        this.location = "";
        this.name = "";
        this.membershipLevel = "";
        this.isGold = false;
        this.isParentallyControlled = true;
        this.voiceAndTextSetting = -1;
        this.shareProfileSetting = -1;
        this.showOnlineStatusSetting = -1;
        this.useMemberContentSetting = -1;
        this.shareGameHistorySetting = -1;
        this.showFriendsListSetting = -1;
    }

    public MeProfileData(ProfileDataRaw data) {
        XLEAssert.assertNotNull("We should never try to create the data object from null raw data.", data);
        XLEAssert.assertNotNull("We should always have profile properties in me profile.", data.ProfileProperties);
        XLEAssert.assertNotNull("We should always have privacy settings in me profile.", data.PrivacySettings);
        this.gamertag = data.getGamertag();
        this.gamerscore = data.ProfileProperties.Gamerscore;
        this.motto = data.ProfileProperties.Motto;
        this.avatarImageUri = data.ProfileProperties.AvatarImageUri;
        this.bio = data.ProfileProperties.Bio;
        this.gamerPicUri = data.ProfileProperties.GamerPicUri;
        this.smallGamerPicUri = data.ProfileProperties.SmallGamerPicUri;
        this.location = data.ProfileProperties.Location;
        this.name = data.ProfileProperties.Name;
        this.membershipLevel = data.ProfileProperties.MembershipLevel;
        this.isParentallyControlled = data.ProfileProperties.IsParentallyControlled;
        this.isGold = data.isGold();
        this.voiceAndTextSetting = data.PrivacySettings.VoiceAndText;
        this.shareProfileSetting = data.PrivacySettings.GamerProfile;
        this.showOnlineStatusSetting = data.PrivacySettings.OnlineStatus;
        this.useMemberContentSetting = data.PrivacySettings.MemberContent;
        this.shareGameHistorySetting = data.PrivacySettings.PlayedGames;
        this.showFriendsListSetting = data.PrivacySettings.FriendsList;
    }

    public String getGamertag() {
        return this.gamertag;
    }

    public String getGamerscore() {
        return this.gamerscore;
    }

    public String getMotto() {
        return this.motto;
    }

    public URI getAvatarImageUri() {
        return this.avatarImageUri;
    }

    public String getBio() {
        return this.bio;
    }

    public URI getGamerPicUri() {
        return this.gamerPicUri;
    }

    public URI getSmallGamerPicUri() {
        return this.smallGamerPicUri;
    }

    public String getLocation() {
        return this.location;
    }

    public String getName() {
        return this.name;
    }

    public String getMembershipLevel() {
        return this.membershipLevel;
    }

    public boolean getIsParentallyControlled() {
        return TestInterop.getUserChildSetting(this.isParentallyControlled);
    }

    public boolean getIsGold() {
        return TestInterop.getMembershipLevel(this.isGold);
    }

    public int getVoiceAndTextSetting() {
        return this.voiceAndTextSetting;
    }

    public int getShareProfileSetting() {
        return this.shareProfileSetting;
    }

    public int getShowOnlineStatusSetting() {
        return this.showOnlineStatusSetting;
    }

    public int getUseMemberContentSetting() {
        return this.useMemberContentSetting;
    }

    public int getShareGameHistorySetting() {
        return this.shareGameHistorySetting;
    }

    public int getShowFriendsListSetting() {
        return this.showFriendsListSetting;
    }

    public void updateWithNewData(ProfileProperties data) {
        if (data != null) {
            this.motto = data.Motto;
            this.name = data.Name;
            this.location = data.Location;
            this.bio = data.Bio;
        }
    }

    public void updateWithNewData(PrivacySettings data) {
        if (data != null) {
            this.voiceAndTextSetting = data.VoiceAndText;
            this.shareProfileSetting = data.GamerProfile;
            this.showOnlineStatusSetting = data.OnlineStatus;
            this.useMemberContentSetting = data.MemberContent;
            this.shareGameHistorySetting = data.PlayedGames;
            this.showFriendsListSetting = data.FriendsList;
        }
    }
}
