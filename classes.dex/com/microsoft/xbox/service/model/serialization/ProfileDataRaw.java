package com.microsoft.xbox.service.model.serialization;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "ProfileEx")
public class ProfileDataRaw {
    protected static final String MEMBERSHIP_LEVEL_FAMILY = "Family";
    protected static final String MEMBERSHIP_LEVEL_GOLD = "Gold";
    protected static final String MEMBERSHIP_LEVEL_SILVER = "Silver";
    @Element(required = false)
    public FriendList FriendList;
    @Element(required = false)
    public PresenceInfo PresenceInfo;
    @Element(required = false)
    public PrivacySettings PrivacySettings;
    @Element(required = false)
    public ProfileProperties ProfileProperties;
    @Element(required = false)
    public RecentGames RecentGames;
    @Element(name = "SectionFlags")
    public int SectionFlags;

    public String getGamertag() {
        if (this.ProfileProperties != null) {
            return this.ProfileProperties.Gamertag;
        }
        return null;
    }

    public int getGameScore() {
        if (this.ProfileProperties != null) {
            return Integer.parseInt(this.ProfileProperties.Gamerscore);
        }
        return 0;
    }

    public boolean isGold() {
        if (this.ProfileProperties == null) {
            return false;
        }
        String membershipLevel = this.ProfileProperties.MembershipLevel;
        if (membershipLevel == null || membershipLevel.length() <= 0) {
            return false;
        }
        if (membershipLevel.equalsIgnoreCase(MEMBERSHIP_LEVEL_GOLD) || membershipLevel.equalsIgnoreCase(MEMBERSHIP_LEVEL_FAMILY)) {
            return true;
        }
        return false;
    }
}
