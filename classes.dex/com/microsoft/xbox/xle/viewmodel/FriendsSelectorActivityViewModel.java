package com.microsoft.xbox.xle.viewmodel;

import android.os.Bundle;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.FriendsModel;
import com.microsoft.xbox.service.model.MeProfileModel;
import com.microsoft.xbox.service.model.UpdateData;
import com.microsoft.xbox.service.model.UpdateType;
import com.microsoft.xbox.service.model.serialization.Friend;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.MultiSelection;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.network.ListState;
import com.microsoft.xbox.xle.app.adapter.FriendsSelectorActivityAdapter;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;

public class FriendsSelectorActivityViewModel extends ViewModelBase {
    private final String bundleKey;
    private ArrayList<FriendSelectorItem> friendList;
    private FriendsModel friendsModel;
    private MultiSelection<String> snapShotSelection;
    private ListState viewModelState;

    public FriendsSelectorActivityViewModel() {
        this.bundleKey = "FriendSelector";
        this.viewModelState = ListState.LoadingState;
        this.adapter = new FriendsSelectorActivityAdapter(this);
    }

    public void onRehydrate() {
        this.adapter = new FriendsSelectorActivityAdapter(this);
    }

    public ArrayList<FriendSelectorItem> getFriends() {
        return this.friendList;
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

    public void toggleSelection(FriendSelectorItem friend) {
        friend.toggleSelection();
        if (friend.getIsSelected()) {
            XLEGlobalData.getInstance().getSelectedRecipients().add(friend.getGamertag());
        } else {
            XLEGlobalData.getInstance().getSelectedRecipients().remove(friend.getGamertag());
        }
        this.adapter.updateView();
    }

    private void updateFriendsList() {
        if (this.friendsModel.getFriendsList() != null) {
            ArrayList<FriendSelectorItem> list = new ArrayList(this.friendsModel.getFriendsList().size());
            Iterator i$ = this.friendsModel.getFriendsList().iterator();
            while (i$.hasNext()) {
                FriendSelectorItem selectorItem = new FriendSelectorItem((Friend) i$.next());
                if (XLEGlobalData.getInstance().getSelectedRecipients().contains(selectorItem.getGamertag())) {
                    selectorItem.setSelected(true);
                }
                list.add(selectorItem);
            }
            this.friendList = list;
        }
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
        this.snapShotSelection = new MultiSelection();
        XLELog.Diagnostic("FriendSelector", "onstart called. make copy from global data");
        Iterator i$ = XLEGlobalData.getInstance().getSelectedRecipients().toArrayList().iterator();
        while (i$.hasNext()) {
            this.snapShotSelection.add((String) i$.next());
        }
    }

    protected void onStopOverride() {
        this.friendsModel.removeObserver(this);
        this.friendsModel = null;
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putStringArrayList("FriendSelector", this.snapShotSelection.toArrayList());
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        ArrayList<String> savedBundle = savedInstanceState.getStringArrayList("FriendSelector");
        if (savedBundle != null) {
            XLELog.Diagnostic("FriendSelector", "onrestoreinstancestate called. use saved instance state");
            this.snapShotSelection.reset();
            Iterator i$ = savedBundle.iterator();
            while (i$.hasNext()) {
                this.snapShotSelection.add((String) i$.next());
            }
            return;
        }
        XLELog.Error("FriendSelector", "can't find the stored list");
    }

    public void confirm() {
        goBack();
    }

    public void cancel() {
        XLEGlobalData.getInstance().getSelectedRecipients().reset();
        Iterator i$ = this.snapShotSelection.toArrayList().iterator();
        while (i$.hasNext()) {
            XLEGlobalData.getInstance().getSelectedRecipients().add((String) i$.next());
        }
        goBack();
    }

    public void onBackButtonPressed() {
        cancel();
    }
}
