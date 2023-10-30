package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.avatar.model.AvatarEditorModel;
import com.microsoft.xbox.avatar.model.AvatarViewActorVM;
import com.microsoft.xbox.avatar.model.AvatarViewActorVMDefault;
import com.microsoft.xbox.avatar.model.AvatarViewVM;
import com.microsoft.xbox.avatar.model.AvatarViewVMDefault;
import com.microsoft.xbox.service.model.AvatarManifestModel;
import com.microsoft.xbox.service.model.FriendsModel;
import com.microsoft.xbox.service.model.MeProfileModel;
import com.microsoft.xbox.service.model.MessageModel;
import com.microsoft.xbox.service.model.UpdateData;
import com.microsoft.xbox.service.model.UpdateType;
import com.microsoft.xbox.service.model.serialization.Friend;
import com.microsoft.xbox.service.model.serialization.XLEAvatarManifest;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.toolkit.network.ListState;
import com.microsoft.xbox.toolkit.network.XboxLiveEnvironment;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.app.XboxMobileOmnitureTracking;
import com.microsoft.xbox.xle.app.activity.AvatarEditorInitializeActivity;
import com.microsoft.xbox.xle.app.activity.EditProfileActivity;
import com.microsoft.xbox.xle.app.activity.FriendsListActivity;
import com.microsoft.xbox.xle.app.activity.FullProfileActivity;
import com.microsoft.xbox.xle.app.activity.MessagesActivity;
import com.microsoft.xbox.xle.app.activity.SearchGamerActivity;
import com.microsoft.xbox.xle.app.activity.TabletProfileActivity;
import com.microsoft.xbox.xle.app.activity.WebViewActivity;
import com.microsoft.xbox.xle.app.activity.YouPivotActivity;
import com.microsoft.xbox.xle.app.adapter.SocialActivityAdapter;
import java.net.URI;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Random;
import java.util.TreeSet;

public class SocialActivityViewModel extends PivotViewModelBase implements IFriendsListViewModel {
    private static final int AVATAR_SHAKE_COUNT = 4;
    private static final int FRIEND_AVATAR_URL_MAX = 5;
    private static final Random rand = new Random();
    private AvatarManifestModel avatarModel;
    private AvatarViewVM avatarViewVM;
    private ArrayList<URI> friendAvatarUriList;
    private FriendsModel friendsModel;
    private AvatarViewActorVMDefault meActorVM;
    private ArrayList<FriendItem> mergedList;
    private TreeSet<Friend> modelFriendList;
    private MottoBubbleTask mottoTask;
    private MeProfileModel profileModel;
    private ListState viewModelState;

    public SocialActivityViewModel() {
        this.friendAvatarUriList = null;
        this.avatarViewVM = null;
        this.meActorVM = null;
        this.mergedList = new ArrayList();
        this.viewModelState = ListState.LoadingState;
        this.adapter = new SocialActivityAdapter(this);
        this.mottoTask = new MottoBubbleTask(new Runnable() {
            public void run() {
                if (SocialActivityViewModel.this.isForeground) {
                    SocialActivityViewModel.this.adapter.updateView();
                }
            }
        });
    }

    public void onRehydrate() {
        XLELog.Diagnostic("SocialActivityViewModel", "onRehydrate");
        this.adapter = new SocialActivityAdapter(this);
    }

    public String getGamertag() {
        return this.profileModel.getGamertag();
    }

    public String getGamerscore() {
        return this.profileModel.getGamerscore();
    }

    public String getMotto() {
        return this.profileModel.getMotto();
    }

    public ArrayList<URI> getFriendAvatarUrlList() {
        return this.friendAvatarUriList;
    }

    public int getFriendsRequestCount() {
        return this.friendsModel.getFriendsRequestCount();
    }

    public String getName() {
        return this.profileModel.getName();
    }

    public boolean isGold() {
        return this.profileModel.getIsGold();
    }

    public boolean shouldShowMotto() {
        return this.mottoTask.getShowMotto();
    }

    public boolean getIsManifestFiltered() {
        return AvatarManifestModel.getIsPlayerManifestFiltered();
    }

    public boolean getIsShadowtarVisible() {
        return this.meActorVM.getIsShadowtarVisible();
    }

    public boolean isBusy() {
        return this.profileModel.getIsLoading() || this.friendsModel.getIsLoading();
    }

    public void updateOverride(AsyncResult<UpdateData> asyncResult) {
        if (XLEGlobalData.getInstance().getAvatarEditorCrashed()) {
            showError(R.string.toast_avatar_editor_apply_error);
            XLEGlobalData.getInstance().setAvatarEditorCrashed(false);
        }
        switch (((UpdateData) asyncResult.getResult()).getUpdateType()) {
            case MeProfileData:
                if (asyncResult.getException() == null && ((UpdateData) asyncResult.getResult()).getIsFinal() && this.profileModel.getGamertag() != null && this.profileModel.getGamertag().length() > 0 && this.profileModel.getMotto() != null && this.profileModel.getMotto().length() > 0 && !this.profileModel.getIsLoading()) {
                    this.mottoTask.setDataReady();
                    break;
                }
            case FriendsData:
                if (asyncResult.getException() == null) {
                    if (((UpdateData) asyncResult.getResult()).getIsFinal()) {
                        updateFriendAvatarUrlList();
                        updateFriendsList();
                        updateViewModelState();
                        break;
                    }
                } else if (this.friendsModel.getFriendsList() == null) {
                    this.viewModelState = ListState.ErrorState;
                    break;
                }
                break;
            case AvatarManifestLoad:
                if (asyncResult.getException() != null || !((UpdateData) asyncResult.getResult()).getIsFinal()) {
                    if (asyncResult.getException() != null) {
                        asyncResult.getException().setIsHandled(true);
                        this.meActorVM.setManifest(XLEAvatarManifest.SHADOWTAR);
                        break;
                    }
                }
                this.meActorVM.setManifest(this.avatarModel.getManifest());
                break;
                break;
        }
        this.adapter.updateView();
    }

    protected void onUpdateFinished() {
        if (checkErrorCode(UpdateType.MeProfileData, XLEErrorCode.FAILED_TO_GET_ME_PROFILE)) {
            showError(R.string.toast_profile_error);
        } else if (checkErrorCode(UpdateType.FriendsData, XLEErrorCode.FAILED_TO_GET_FRIENDS)) {
            showError(R.string.toast_friend_list_error);
        }
        super.onUpdateFinished();
    }

    private void updateFriendAvatarUrlList() {
        if (this.friendsModel.getFriendsList() != null && this.friendsModel.getFriendsList().size() > 0 && this.friendAvatarUriList == null) {
            int i;
            ArrayList<URI> avatarUriList = new ArrayList();
            Iterator i$ = this.friendsModel.getFriendsList().iterator();
            while (i$.hasNext()) {
                Friend friend = (Friend) i$.next();
                if (friend.ProfileEx.ProfileProperties.HasAvatar && friend.ProfileEx.ProfileProperties.AvatarImageUri != null) {
                    avatarUriList.add(friend.ProfileEx.ProfileProperties.AvatarImageUri);
                }
            }
            int listSize = avatarUriList.size();
            int pickSize = Math.min(5, listSize);
            URI[] avatarUris = (URI[]) avatarUriList.toArray(new URI[0]);
            for (i = 0; i < listSize; i++) {
                int j = rand.nextInt(listSize - i) + i;
                URI temp = avatarUris[i];
                avatarUris[i] = avatarUris[j];
                avatarUris[j] = temp;
            }
            avatarUriList.clear();
            for (i = 0; i < pickSize; i++) {
                XLEAssert.assertNotNull(avatarUris[i]);
                XLEAssert.assertTrue(avatarUris[i] instanceof URI);
                avatarUriList.add(avatarUris[i]);
                XLELog.Diagnostic("Friends", avatarUris[i].toASCIIString());
            }
            this.friendAvatarUriList = avatarUriList;
        }
    }

    public void load(boolean forceRefresh) {
        setUpdateTypesToCheck(EnumSet.of(UpdateType.MeProfileData, UpdateType.FriendsData));
        this.profileModel.load(forceRefresh);
        this.friendsModel.load(forceRefresh);
        this.avatarModel.load(forceRefresh);
        MessageModel.getInstance().loadMessageList(forceRefresh);
    }

    protected void onStartOverride() {
        XLEApplication.getMainActivity().clearAvatarViewFloat();
        this.mottoTask.resetReadyFlags();
        this.profileModel = MeProfileModel.getModel();
        this.friendsModel = FriendsModel.getModel();
        this.avatarModel = AvatarManifestModel.getPlayerModel();
        this.profileModel.addObserver(this);
        this.friendsModel.addObserver(this);
        this.avatarModel.addObserver(this);
        if (XLEGlobalData.getInstance().getFriendListUpdated()) {
            this.modelFriendList = null;
        }
        XLEGlobalData.getInstance().setFriendListUpdated(false);
        this.avatarViewVM = new AvatarViewVMDefault();
        this.meActorVM = new AvatarViewActorVMDefault();
        this.avatarViewVM.registerActor(this.meActorVM);
        this.meActorVM.setShadowtarVisibilityChangedCallback(new Runnable() {
            public void run() {
                if (SocialActivityViewModel.this.isForeground) {
                    SocialActivityViewModel.this.adapter.updateView();
                }
            }
        });
        this.meActorVM.setMottoShowCallback(new Runnable() {
            public void run() {
                SocialActivityViewModel.this.mottoTask.setAnimationReady();
            }
        });
        AvatarEditorModel.getInstance().shutdownIfNecessary();
        XboxApplication.Accelerometer.setShakeUpdatedRunnable(new Runnable() {
            public void run() {
                SocialActivityViewModel.this.shakeUpdated();
            }
        });
    }

    protected void onStopOverride() {
        this.mottoTask.cancelMotto();
        this.profileModel.removeObserver(this);
        this.friendsModel.removeObserver(this);
        this.avatarModel.removeObserver(this);
        this.profileModel = null;
        this.friendsModel = null;
        this.avatarModel = null;
        this.friendAvatarUriList = null;
        this.avatarViewVM.onDestroy();
        this.avatarViewVM = null;
        this.meActorVM = null;
        XboxApplication.Accelerometer.setShakeUpdatedRunnable(null);
    }

    public void navigateToAvatarEditor() {
        NavigateTo(AvatarEditorInitializeActivity.class);
    }

    public void navigateToAvatarEditorFiltered() {
        showMustActDialog(XLEApplication.Resources.getString(R.string.dialog_attention_title), XLEApplication.Resources.getString(R.string.toast_avatar_manifest_filtered), XLEApplication.Resources.getString(R.string.OK), new Runnable() {
            public void run() {
                SocialActivityViewModel.this.navigateToAvatarEditor();
            }
        }, false);
        AvatarManifestModel.setIsUserAwareFiltered(true);
    }

    public void navigateToEditProfile() {
        NavigateTo(EditProfileActivity.class);
    }

    public void navigateToFriendsList() {
        XLELog.Info("ProfileActivityViewModel", "Navigating to friends list");
        NavigateTo(FriendsListActivity.class);
    }

    public void navigateToMessagesList() {
        XLELog.Info("ProfileActivityViewModel", "Navigating to messages list");
        NavigateTo(MessagesActivity.class);
    }

    public void navigateToBeacons() {
        XLELog.Info("ProfileActivityViewModel", "Navigating to beacons");
        XLEGlobalData.getInstance().setSelectedDataSource(XboxLiveEnvironment.Instance().getBeaconsUrl());
        NavigateTo(WebViewActivity.class);
    }

    public AvatarViewVM getAvatarViewVM() {
        return this.avatarViewVM;
    }

    public AvatarViewActorVM getActorVM() {
        return this.meActorVM;
    }

    private void shakeUpdated() {
        if (XboxApplication.Accelerometer.getShakeCount() > 4) {
            XboxApplication.Accelerometer.clearShakes();
            if (this.meActorVM != null) {
                this.meActorVM.shakeFall();
            }
        }
    }

    public void navigateToYouProfile(FriendItem friend) {
        String str = "You gamertag must not be empty.";
        boolean z = friend.getGamertag() != null && friend.getGamertag().length() > 0;
        XLEAssert.assertTrue(str, z);
        XLEGlobalData.getInstance().setSelectedGamertag(friend.getGamertag());
        XLELog.Info("SocialActivityViewModel", String.format("Navigating to you profile for gamertag=%s", new Object[]{friend.getGamertag()}));
        if (XLEApplication.Instance.getIsTablet()) {
            NavigateTo(TabletProfileActivity.class);
        } else {
            NavigateTo(YouPivotActivity.class);
        }
    }

    public void navigateToMeProfile() {
        XLEGlobalData.getInstance().setSelectedGamertag(MeProfileModel.getModel().getGamertag());
        XLELog.Info("SocialActivityViewModel", "Navigating to me profile");
        if (XLEApplication.Instance.getIsTablet()) {
            NavigateTo(TabletProfileActivity.class);
        } else {
            NavigateTo(FullProfileActivity.class);
        }
    }

    public ArrayList<FriendItem> getFriends() {
        return this.mergedList;
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
                        XLELog.Error("SocialActivityViewModel", "Unhandled friend state: " + Integer.toString(friend.FriendState));
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

    public void navigateToSearchGamer() {
        XLELog.Info("SocialActivityViewModel", "Navigating to gamer search");
        XboxMobileOmnitureTracking.TrackFriendSearch();
        NavigateTo(SearchGamerActivity.class);
    }

    public ListState getViewModelState() {
        return this.viewModelState;
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
}
