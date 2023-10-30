package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.FriendsModel;
import com.microsoft.xbox.service.model.MeProfileModel;
import com.microsoft.xbox.service.model.UpdateData;
import com.microsoft.xbox.service.model.UpdateType;
import com.microsoft.xbox.service.model.serialization.Friend;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.network.ListState;
import com.microsoft.xbox.xle.app.XboxMobileOmnitureTracking;
import com.microsoft.xbox.xle.app.activity.SearchGamerActivity;
import com.microsoft.xbox.xle.app.activity.YouPivotActivity;
import com.microsoft.xbox.xle.app.adapter.AdapterFactory;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.TreeSet;

public class FriendsListActivityViewModel extends ViewModelBase implements IFriendsListViewModel {
    private FriendsModel friendsModel;
    private ArrayList<FriendItem> mergedList;
    private TreeSet<Friend> modelFriendList;
    private ListState viewModelState;

    public FriendsListActivityViewModel() {
        this.mergedList = new ArrayList();
        this.viewModelState = ListState.LoadingState;
        this.adapter = AdapterFactory.getInstance().getFriendsAdapter(this);
    }

    public void onRehydrate() {
        this.adapter = AdapterFactory.getInstance().getFriendsAdapter(this);
    }

    public ArrayList<FriendItem> getFriends() {
        return this.mergedList;
    }

    public ListState getViewModelState() {
        return this.viewModelState;
    }

    public boolean isBusy() {
        return this.friendsModel.getIsLoading();
    }

    private void updateViewModelState() {
        ListState newState = ListState.LoadingState;
        if (this.friendsModel.getFriendsList() == null) {
            newState = ListState.LoadingState;
        } else if (this.friendsModel.getFriendsList().size() == 0) {
            newState = ListState.NoContentState;
        } else {
            newState = ListState.ValidContentState;
        }
        if (this.viewModelState != newState) {
            this.viewModelState = newState;
        }
    }

    public void updateOverride(AsyncResult<UpdateData> asyncResult) {
        switch (((UpdateData) asyncResult.getResult()).getUpdateType()) {
            case FriendsData:
                if (asyncResult.getException() == null) {
                    if (((UpdateData) asyncResult.getResult()).getIsFinal()) {
                        updateFriendsList();
                        updateViewModelState();
                        break;
                    }
                } else if (this.friendsModel.getFriendsList() == null) {
                    this.viewModelState = ListState.ErrorState;
                    break;
                }
                break;
        }
        this.adapter.updateView();
    }

    protected void onUpdateFinished() {
        if (checkErrorCode(UpdateType.FriendsData, XLEErrorCode.FAILED_TO_GET_FRIENDS)) {
            if (getViewModelState() == ListState.ValidContentState) {
                showError(R.string.toast_friend_list_error);
            } else {
                this.viewModelState = ListState.ErrorState;
                this.adapter.updateView();
            }
        }
        super.onUpdateFinished();
    }

    private void updateFriendsList() {
        if (this.friendsModel.getFriendsList() != null && this.modelFriendList != this.friendsModel.getFriendsList()) {
            this.modelFriendList = this.friendsModel.getFriendsList();
            ArrayList<FriendItem> newMergedList = new ArrayList(this.modelFriendList.size());
            ArrayList<FriendItem> requestList = new ArrayList();
            ArrayList<FriendItem> onlineList = new ArrayList();
            ArrayList<FriendItem> offlineList = new ArrayList();
            ArrayList<FriendItem> pendingList = new ArrayList();
            Iterator i$ = this.modelFriendList.iterator();
            while (i$.hasNext()) {
                Friend friend = (Friend) i$.next();
                switch (friend.FriendState) {
                    case 0:
                        if (friend.ProfileEx.PresenceInfo.OnlineState == 1) {
                            offlineList.add(new FriendItem(friend, false));
                            break;
                        } else {
                            onlineList.add(new FriendItem(friend, true));
                            break;
                        }
                    case 1:
                        pendingList.add(new FriendItem(friend));
                        break;
                    case 2:
                        requestList.add(new FriendItem(friend));
                        break;
                    default:
                        XLELog.Error("FriendsListActivityViewModel", "Unhandled friend state: " + Integer.toString(friend.FriendState));
                        break;
                }
            }
            if (this.modelFriendList.size() == 0) {
                this.mergedList = new ArrayList();
                return;
            }
            if (requestList.size() > 0) {
                newMergedList.add(new FriendItem((int) R.string.friends_list_requests, requestList.size()));
                newMergedList.addAll(requestList);
            }
            if (onlineList.size() > 0) {
                newMergedList.add(new FriendItem((int) R.string.friends_list_online, onlineList.size()));
                newMergedList.addAll(onlineList);
            } else {
                newMergedList.add(new FriendItem((int) R.string.friends_list_online, 0));
                newMergedList.add(new FriendItem(true));
            }
            if (offlineList.size() > 0) {
                newMergedList.add(new FriendItem((int) R.string.friends_list_offline, offlineList.size()));
                newMergedList.addAll(offlineList);
            } else {
                newMergedList.add(new FriendItem((int) R.string.friends_list_offline, 0));
                newMergedList.add(new FriendItem(false));
            }
            if (pendingList.size() > 0) {
                newMergedList.add(new FriendItem((int) R.string.friends_list_pending, pendingList.size()));
                newMergedList.addAll(pendingList);
            }
            this.mergedList = new ArrayList();
            this.mergedList.addAll(newMergedList);
        }
    }

    public void navigateToYouProfile(FriendItem friend) {
        String str = "You gamertag must not be empty.";
        boolean z = friend.getGamertag() != null && friend.getGamertag().length() > 0;
        XLEAssert.assertTrue(str, z);
        XLEGlobalData.getInstance().setSelectedGamertag(friend.getGamertag());
        XLELog.Info("FriendsListActivityViewModel", String.format("Navigating to you profile pivot for gamertag=%s", new Object[]{friend.getGamertag()}));
        NavigateTo(YouPivotActivity.class);
    }

    public void navigateToSearchGamer() {
        XLELog.Info("FriendsListActivityViewModel", "Navigating to gamer search");
        XboxMobileOmnitureTracking.TrackFriendSearch();
        NavigateTo(SearchGamerActivity.class);
    }

    public void load(boolean forceRefresh) {
        setUpdateTypesToCheck(EnumSet.of(UpdateType.FriendsData));
        this.friendsModel.load(forceRefresh);
    }

    protected void onStartOverride() {
        String str = "MeProfileModel should have been loaded.";
        boolean z = MeProfileModel.getModel().getGamertag() != null && MeProfileModel.getModel().getGamertag().length() > 0;
        XLEAssert.assertTrue(str, z);
        this.friendsModel = FriendsModel.getModel();
        this.friendsModel.addObserver(this);
        if (XLEGlobalData.getInstance().getFriendListUpdated()) {
            this.modelFriendList = null;
        }
        XLEGlobalData.getInstance().setFriendListUpdated(false);
    }

    protected void onStopOverride() {
        this.friendsModel.removeObserver(this);
        this.friendsModel = null;
    }
}
