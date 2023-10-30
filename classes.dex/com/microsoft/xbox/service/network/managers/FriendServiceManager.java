package com.microsoft.xbox.service.network.managers;

import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.network.XboxLiveEnvironment;

public class FriendServiceManager implements IFriendServiceManager {
    private static final String ACCEPT_FRIEND_URI = (XboxLiveEnvironment.Instance().getUdsUrlBaseSecure() + XboxLiveEnvironment.FRIEND_ACCEPT_API_PATH + XboxLiveEnvironment.FRIEND_QUERY_PARAMS);
    private static final String ADD_FRIEND_URI = (XboxLiveEnvironment.Instance().getUdsUrlBaseSecure() + XboxLiveEnvironment.FRIEND_ADD_API_PATH + XboxLiveEnvironment.FRIEND_QUERY_PARAMS);
    private static final String DECLINE_FRIEND_URI = (XboxLiveEnvironment.Instance().getUdsUrlBaseSecure() + XboxLiveEnvironment.FRIEND_DECLINE_API_PATH + XboxLiveEnvironment.FRIEND_QUERY_PARAMS);
    private static final String REMOVE_FRIEND_URI = (XboxLiveEnvironment.Instance().getUdsUrlBaseSecure() + XboxLiveEnvironment.FRIEND_REMOVE_API_PATH + XboxLiveEnvironment.FRIEND_QUERY_PARAMS);

    public void addFriend(String gamertag) throws XLEException {
        String url = String.format(ADD_FRIEND_URI, new Object[]{gamertag});
        XLELog.Info("FriendServiceManager", "Adding friend for " + gamertag);
        ServiceCommon.postLivenStream(XboxLiveEnvironment.UDS_AUDIENCE_URI, url, "");
    }

    public void removeFriend(String gamertag) throws XLEException {
        String url = String.format(REMOVE_FRIEND_URI, new Object[]{gamertag});
        XLELog.Info("FriendServiceManager", "Removing friend for " + gamertag);
        ServiceCommon.postLivenStream(XboxLiveEnvironment.UDS_AUDIENCE_URI, url, "");
    }

    public void acceptFriendRequest(String gamertag) throws XLEException {
        String url = String.format(ACCEPT_FRIEND_URI, new Object[]{gamertag});
        XLELog.Info("FriendServiceManager", "Accepting friend request for " + gamertag);
        ServiceCommon.postLivenStream(XboxLiveEnvironment.UDS_AUDIENCE_URI, url, "");
    }

    public void declineFriendRequest(String gamertag) throws XLEException {
        String url = String.format(DECLINE_FRIEND_URI, new Object[]{gamertag});
        XLELog.Info("FriendServiceManager", "Declining friend request for " + gamertag);
        ServiceCommon.postLivenStream(XboxLiveEnvironment.UDS_AUDIENCE_URI, url, "");
    }
}
