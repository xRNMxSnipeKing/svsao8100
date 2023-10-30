package com.microsoft.xbox.service.model;

import com.microsoft.xbox.service.model.serialization.GameInfo;
import com.microsoft.xbox.service.model.serialization.ProfileDataRaw;
import com.microsoft.xbox.toolkit.XLEAssert;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class YouProfileData {
    private final URI avatarImageUri;
    private final String bio;
    private final URI gamerPicUri;
    private final String gamerscore;
    private final String gamertag;
    private final boolean isGold;
    private final String location;
    private final String membershipLevel;
    private final String motto;
    private final String name;
    private final ArrayList<GameInfo> recentGames;
    private final boolean showGamerProfile;
    private final URI smallGamerPicUri;

    public YouProfileData() {
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
        this.recentGames = null;
        this.showGamerProfile = false;
        this.isGold = false;
    }

    public YouProfileData(ProfileDataRaw data) {
        XLEAssert.assertNotNull("We should never try to create the data object from null raw data.", data);
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
        if (data.RecentGames == null || data.RecentGames.items == null) {
            this.recentGames = new ArrayList();
        } else {
            this.recentGames = data.RecentGames.items;
            Collections.sort(this.recentGames, new Comparator<GameInfo>() {
                public int compare(GameInfo game1, GameInfo game2) {
                    return game2.LastPlayed.compareTo(game1.LastPlayed);
                }
            });
        }
        this.showGamerProfile = data.ProfileProperties.ShowGamerProfile;
        this.isGold = data.isGold();
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

    public ArrayList<GameInfo> getRecentGames() {
        return this.recentGames;
    }

    public boolean getShowGamerProfile() {
        return this.showGamerProfile;
    }

    public boolean getIsGold() {
        return this.isGold;
    }
}
