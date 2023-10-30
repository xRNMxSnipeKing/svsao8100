package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.serialization.Friend;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.app.XLEUtil;
import java.net.URI;

public class FriendItem {
    private final int gameScore;
    private final URI gamerpicUri;
    private final String gamertag;
    private final boolean isEmptyListText;
    private final boolean isHeader;
    private final boolean isOnline;
    private final String lastOnlineText;
    private final String name;
    private final String statusText;

    public FriendItem(Friend friend) {
        this.isHeader = false;
        this.isEmptyListText = false;
        this.gameScore = friend.getGameScore();
        this.isOnline = false;
        this.gamertag = friend.getGamertag();
        this.gamerpicUri = friend.ProfileEx.ProfileProperties.GamerPicUri;
        this.name = friend.ProfileEx.ProfileProperties.Name;
        switch (friend.FriendState) {
            case 0:
                if (friend.ProfileEx.PresenceInfo.OnlineState != 1) {
                    this.statusText = String.format("%s %s", new Object[]{XboxApplication.Resources.getString(R.string.friend_status_playing), friend.ProfileEx.PresenceInfo.LastSeenTitleName});
                    this.lastOnlineText = "";
                    return;
                } else if (friend.ProfileEx.PresenceInfo.LastSeenTitleId > 0) {
                    this.statusText = friend.ProfileEx.PresenceInfo.LastSeenTitleName;
                    this.lastOnlineText = XLEUtil.dateToDurationSinceNowValidate(friend.ProfileEx.PresenceInfo.LastSeenDateTime);
                    return;
                } else {
                    this.statusText = "";
                    this.lastOnlineText = "";
                    return;
                }
            case 1:
                this.statusText = XboxApplication.Resources.getString(R.string.friend_status_pending);
                this.lastOnlineText = "";
                return;
            case 2:
                this.statusText = XboxApplication.Resources.getString(R.string.friend_status_request);
                this.lastOnlineText = "";
                return;
            default:
                XLELog.Error("FriendsListActivityViewModel", "Unhandled friend state: " + Integer.toString(friend.FriendState));
                this.statusText = "";
                this.lastOnlineText = "";
                return;
        }
    }

    public FriendItem(Friend friend, boolean isOnline) {
        this.isHeader = false;
        this.isEmptyListText = false;
        this.gameScore = friend.getGameScore();
        this.isOnline = isOnline;
        this.gamertag = friend.getGamertag();
        this.gamerpicUri = friend.ProfileEx.ProfileProperties.GamerPicUri;
        this.name = friend.ProfileEx.ProfileProperties.Name;
        switch (friend.FriendState) {
            case 0:
                if (friend.ProfileEx.PresenceInfo.OnlineState != 1) {
                    this.statusText = String.format("%s %s", new Object[]{XboxApplication.Resources.getString(R.string.friend_status_playing), friend.ProfileEx.PresenceInfo.LastSeenTitleName});
                    this.lastOnlineText = "";
                    return;
                } else if (friend.ProfileEx.PresenceInfo.LastSeenTitleId > 0) {
                    this.statusText = friend.ProfileEx.PresenceInfo.LastSeenTitleName;
                    this.lastOnlineText = XLEUtil.dateToDurationSinceNowValidate(friend.ProfileEx.PresenceInfo.LastSeenDateTime);
                    return;
                } else {
                    this.statusText = "";
                    this.lastOnlineText = "";
                    return;
                }
            case 1:
                this.statusText = XboxApplication.Resources.getString(R.string.friend_status_pending);
                this.lastOnlineText = "";
                return;
            case 2:
                this.statusText = XboxApplication.Resources.getString(R.string.friend_status_request);
                this.lastOnlineText = "";
                return;
            default:
                XLELog.Error("FriendsListActivityViewModel", "Unhandled friend state: " + Integer.toString(friend.FriendState));
                this.statusText = "";
                this.lastOnlineText = "";
                return;
        }
    }

    public FriendItem(int headerResourceId, int listCount) {
        this.isHeader = true;
        this.isEmptyListText = false;
        this.gamertag = null;
        this.gameScore = 0;
        this.isOnline = false;
        this.gamerpicUri = null;
        this.lastOnlineText = null;
        this.name = null;
        if (listCount <= 0) {
            this.statusText = XLEApplication.Resources.getString(headerResourceId);
        } else if (XLEGlobalData.getInstance().getIsTablet()) {
            this.statusText = String.format("%d %s", new Object[]{Integer.valueOf(listCount), XLEApplication.Resources.getString(headerResourceId)});
        } else {
            this.statusText = String.format("%s (%d)", new Object[]{XLEApplication.Resources.getString(headerResourceId), Integer.valueOf(listCount)});
        }
    }

    public FriendItem(boolean isOnline) {
        this.isHeader = false;
        this.isEmptyListText = true;
        this.gamertag = null;
        this.gameScore = 0;
        this.isOnline = isOnline;
        this.gamerpicUri = null;
        this.lastOnlineText = null;
        this.name = null;
        if (isOnline) {
            this.statusText = XLEApplication.Resources.getString(R.string.friend_status_no_friends_online);
        } else {
            this.statusText = XLEApplication.Resources.getString(R.string.friend_status_no_friends_offline);
        }
    }

    public String getGamertag() {
        return this.gamertag;
    }

    public String getStatusText() {
        return this.statusText;
    }

    public int getGameScore() {
        return this.gameScore;
    }

    public boolean getIsOnline() {
        return this.isOnline;
    }

    public String getLastOnlineText() {
        return this.lastOnlineText;
    }

    public URI getGamerpicUri() {
        return this.gamerpicUri;
    }

    public String getName() {
        return this.name;
    }

    public boolean getIsHeader() {
        return this.isHeader;
    }

    public boolean getIsEmptyListText() {
        return this.isEmptyListText;
    }
}
