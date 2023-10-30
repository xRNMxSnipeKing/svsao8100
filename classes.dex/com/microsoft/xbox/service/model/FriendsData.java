package com.microsoft.xbox.service.model;

import com.microsoft.xbox.service.model.serialization.Friend;
import com.microsoft.xbox.service.model.serialization.ProfileDataRaw;
import com.microsoft.xbox.service.model.serialization.ProfileProperties;
import com.microsoft.xbox.toolkit.XLEAssert;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

public final class FriendsData {
    private int friendRequestCount;
    private final TreeSet<Friend> friendsList;

    public FriendsData() {
        this.friendsList = null;
        this.friendRequestCount = 0;
    }

    public FriendsData(ProfileDataRaw data) {
        XLEAssert.assertNotNull("We should never try to create the data object from null raw data.", data);
        XLEAssert.assertNotNull("Friend list should not be null", data.FriendList);
        this.friendsList = new TreeSet(new Comparator<Friend>() {
            public int compare(Friend friend1, Friend friend2) {
                return friend1.getGamertag().toLowerCase().compareTo(friend2.getGamertag().toLowerCase());
            }
        });
        int requestCount = 0;
        if (data.FriendList.items != null) {
            Iterator i$ = data.FriendList.items.iterator();
            while (i$.hasNext()) {
                Friend friend = (Friend) i$.next();
                XLEAssert.assertNotNull("Friend's profile cannot be null", friend.ProfileEx);
                XLEAssert.assertNotNull("Friend's profile properties cannot be null", friend.ProfileEx.ProfileProperties);
                this.friendsList.add(friend);
                if (friend.FriendState == 2) {
                    requestCount++;
                }
            }
        }
        this.friendRequestCount = requestCount;
    }

    public TreeSet<Friend> getFriendsList() {
        return this.friendsList;
    }

    public int getFriendRequestCount() {
        return this.friendRequestCount;
    }

    public Friend getFriend(String gamertag) {
        Iterator i$ = this.friendsList.iterator();
        while (i$.hasNext()) {
            Friend friend = (Friend) i$.next();
            if (friend.getGamertag().equalsIgnoreCase(gamertag)) {
                return friend;
            }
        }
        return null;
    }

    public void addPendingFriend(String gamertag) {
        Friend friend = new Friend();
        friend.ProfileEx = new ProfileDataRaw();
        friend.ProfileEx.ProfileProperties = new ProfileProperties();
        friend.ProfileEx.ProfileProperties.Gamertag = gamertag;
        friend.FriendState = 1;
        this.friendsList.add(friend);
    }

    public void addConfirmedFriend(String gamertag) {
        Friend friend = getFriend(gamertag);
        if (friend != null) {
            this.friendRequestCount = Math.max(0, this.friendRequestCount - 1);
            friend.FriendState = 0;
        }
    }

    public void removeFriend(String gamertag) {
        Friend friend = getFriend(gamertag);
        if (friend != null) {
            if (friend.FriendState == 2) {
                this.friendRequestCount = Math.max(0, this.friendRequestCount - 1);
            }
            friend.FriendState = 3;
            this.friendsList.remove(friend);
        }
    }
}
