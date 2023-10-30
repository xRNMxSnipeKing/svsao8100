package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.xbox.toolkit.network.ListState;
import java.util.ArrayList;

public interface IFriendsListViewModel {
    ArrayList<FriendItem> getFriends();

    ListState getViewModelState();

    void navigateToYouProfile(FriendItem friendItem);
}
