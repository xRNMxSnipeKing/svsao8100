package com.microsoft.xbox.service.network.managers;

import com.microsoft.xbox.toolkit.XLEException;

public interface IFriendServiceManager {
    void acceptFriendRequest(String str) throws XLEException;

    void addFriend(String str) throws XLEException;

    void declineFriendRequest(String str) throws XLEException;

    void removeFriend(String str) throws XLEException;
}
