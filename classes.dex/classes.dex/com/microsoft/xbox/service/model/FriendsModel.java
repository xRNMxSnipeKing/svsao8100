package com.microsoft.xbox.service.model;

import com.microsoft.xbox.service.model.serialization.Friend;
import com.microsoft.xbox.service.model.serialization.ProfileDataRaw;
import com.microsoft.xbox.service.network.managers.IFriendServiceManager;
import com.microsoft.xbox.service.network.managers.IProfileServiceManager;
import com.microsoft.xbox.service.network.managers.ServiceManagerFactory;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.DataLoaderTask;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.network.IDataLoaderRunnable;
import java.util.TreeSet;

public class FriendsModel extends ModelBase<ProfileDataRaw> {
    public static final int FRIENDS_SECTIONS = 128;
    public static final int FRIEND_STATE_FRIEND = 0;
    public static final int FRIEND_STATE_NOTAFRIEND = 3;
    public static final int FRIEND_STATE_PENDING = 1;
    public static final int FRIEND_STATE_REQUESTING = 2;
    public static final int ONLINE_STATE_OFFLINE = 1;
    public static final int ONLINE_STATE_ONLINE = 0;
    private IFriendServiceManager friendServiceManager;
    private FriendsData friendsData;
    private boolean isAcceptingFriendRequest;
    private boolean isAddingFriend;
    private boolean isDecliningFriendRequest;
    private boolean isRemovingFriend;
    private IProfileServiceManager profileServiceManager;

    private static class FriendsModelHolder {
        public static FriendsModel instance = new FriendsModel();

        private FriendsModelHolder() {
        }

        public static void reset() {
            instance = new FriendsModel();
        }
    }

    private class AcceptFriendRequestRunnable extends IDataLoaderRunnable<String> {
        private String gamertag;

        public AcceptFriendRequestRunnable(String gamertag) {
            this.gamertag = gamertag;
        }

        public void onPreExecute() {
        }

        public String buildData() throws XLEException {
            FriendsModel.this.friendServiceManager.acceptFriendRequest(this.gamertag);
            return this.gamertag;
        }

        public void onPostExcute(AsyncResult<String> result) {
            FriendsModel.this.onAcceptFriendRequestCompleted(result);
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_ACCEPT_FRIEND;
        }
    }

    private class AddFriendRunnable extends IDataLoaderRunnable<String> {
        private String gamertag;

        public AddFriendRunnable(String gamertag) {
            this.gamertag = gamertag;
        }

        public void onPreExecute() {
        }

        public String buildData() throws XLEException {
            FriendsModel.this.friendServiceManager.addFriend(this.gamertag);
            return this.gamertag;
        }

        public void onPostExcute(AsyncResult<String> result) {
            FriendsModel.this.onAddFriendCompleted(result);
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_ADD_FRIEND;
        }
    }

    private class DeclineFriendRequestRunnable extends IDataLoaderRunnable<String> {
        private String gamertag;

        public DeclineFriendRequestRunnable(String gamertag) {
            this.gamertag = gamertag;
        }

        public void onPreExecute() {
        }

        public String buildData() throws XLEException {
            FriendsModel.this.friendServiceManager.declineFriendRequest(this.gamertag);
            return this.gamertag;
        }

        public void onPostExcute(AsyncResult<String> result) {
            FriendsModel.this.onDeclineFriendRequestCompleted(result);
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_DECLINE_FRIEND;
        }
    }

    private class RemoveFriendRunnable extends IDataLoaderRunnable<String> {
        private String gamertag;

        public RemoveFriendRunnable(String gamertag) {
            this.gamertag = gamertag;
        }

        public void onPreExecute() {
        }

        public String buildData() throws XLEException {
            FriendsModel.this.friendServiceManager.removeFriend(this.gamertag);
            return this.gamertag;
        }

        public void onPostExcute(AsyncResult<String> result) {
            FriendsModel.this.onRemoveFriendCompleted(result);
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_REMOVE_FRIEND;
        }
    }

    private FriendsModel() {
        this.friendsData = new FriendsData();
        this.profileServiceManager = ServiceManagerFactory.getInstance().getProfileServiceManager();
        this.friendServiceManager = ServiceManagerFactory.getInstance().getFriendServiceManager();
        this.loaderRunnable = new ProfileLoaderRunnable(this.profileServiceManager, this, null, 128);
    }

    public TreeSet<Friend> getFriendsList() {
        return this.friendsData.getFriendsList();
    }

    public Friend getFriend(String friendGamertag) {
        return this.friendsData.getFriend(friendGamertag);
    }

    public int getFriendsRequestCount() {
        return this.friendsData.getFriendRequestCount();
    }

    public boolean getIsAddingFriend() {
        return this.isAddingFriend;
    }

    public boolean getIsRemovingFriend() {
        return this.isRemovingFriend;
    }

    public boolean getIsAcceptingFriendRequest() {
        return this.isAcceptingFriendRequest;
    }

    public boolean getIsDecliningFriendRequest() {
        return this.isDecliningFriendRequest;
    }

    public void load(boolean forceRefresh) {
        loadInternal(forceRefresh, UpdateType.FriendsData, this.loaderRunnable);
    }

    public void updateWithNewData(AsyncResult<ProfileDataRaw> asyncResult) {
        super.updateWithNewData(asyncResult);
        if (asyncResult.getException() == null) {
            this.friendsData = new FriendsData((ProfileDataRaw) asyncResult.getResult());
        }
        notifyObservers(new AsyncResult(new UpdateData(UpdateType.FriendsData, true), this, asyncResult.getException()));
    }

    public void sendFriendRequest(String gamertag) {
        boolean z;
        boolean z2 = false;
        if (Thread.currentThread() == ThreadManager.UIThread) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        if (getFriendsList() != null) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        if (!this.isAddingFriend) {
            z2 = true;
        }
        XLEAssert.assertTrue(z2);
        this.isAddingFriend = true;
        new DataLoaderTask(new AddFriendRunnable(gamertag)).execute();
    }

    public void removeFriend(String gamertag) {
        boolean z;
        boolean z2 = false;
        if (Thread.currentThread() == ThreadManager.UIThread) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        if (getFriendsList() != null) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        if (!this.isRemovingFriend) {
            z2 = true;
        }
        XLEAssert.assertTrue(z2);
        this.isRemovingFriend = true;
        new DataLoaderTask(new RemoveFriendRunnable(gamertag)).execute();
    }

    public void acceptFriendRequest(String gamertag) {
        boolean z;
        boolean z2 = false;
        if (Thread.currentThread() == ThreadManager.UIThread) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        if (getFriendsList() != null) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        if (!this.isAcceptingFriendRequest) {
            z2 = true;
        }
        XLEAssert.assertTrue(z2);
        this.isAcceptingFriendRequest = true;
        new DataLoaderTask(new AcceptFriendRequestRunnable(gamertag)).execute();
    }

    public void declineFriendRequest(String gamertag) {
        boolean z;
        boolean z2 = false;
        if (Thread.currentThread() == ThreadManager.UIThread) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        if (getFriendsList() != null) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        if (!this.isDecliningFriendRequest) {
            z2 = true;
        }
        XLEAssert.assertTrue(z2);
        this.isDecliningFriendRequest = true;
        new DataLoaderTask(new DeclineFriendRequestRunnable(gamertag)).execute();
    }

    public void onAddFriendCompleted(AsyncResult<String> result) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        if (result.getException() == null) {
            this.friendsData.addPendingFriend((String) result.getResult());
        }
        this.isAddingFriend = false;
        notifyObservers(new AsyncResult(new UpdateData(UpdateType.UpdateFriend, true), this, result.getException()));
    }

    public void onRemoveFriendCompleted(AsyncResult<String> result) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        if (result.getException() == null) {
            this.friendsData.removeFriend((String) result.getResult());
        }
        this.isRemovingFriend = false;
        notifyObservers(new AsyncResult(new UpdateData(UpdateType.UpdateFriend, true), this, result.getException()));
    }

    public void onAcceptFriendRequestCompleted(AsyncResult<String> result) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        if (result.getException() == null) {
            this.friendsData.addConfirmedFriend((String) result.getResult());
        }
        this.isAcceptingFriendRequest = false;
        notifyObservers(new AsyncResult(new UpdateData(UpdateType.UpdateFriend, true), this, result.getException()));
    }

    public void onDeclineFriendRequestCompleted(AsyncResult<String> result) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        if (result.getException() == null) {
            this.friendsData.removeFriend((String) result.getResult());
        }
        this.isDecliningFriendRequest = false;
        notifyObservers(new AsyncResult(new UpdateData(UpdateType.UpdateFriend, true), this, result.getException()));
    }

    public static FriendsModel getModel() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        return FriendsModelHolder.instance;
    }

    public static void reset() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        getModel().clearObserver();
        FriendsModelHolder.reset();
    }
}
