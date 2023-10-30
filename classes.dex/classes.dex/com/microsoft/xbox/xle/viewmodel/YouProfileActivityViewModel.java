package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.avatar.model.AvatarViewActorVM;
import com.microsoft.xbox.avatar.model.AvatarViewActorVMDefault;
import com.microsoft.xbox.avatar.model.AvatarViewVM;
import com.microsoft.xbox.avatar.model.AvatarViewVMDefault;
import com.microsoft.xbox.service.model.AvatarManifestModel;
import com.microsoft.xbox.service.model.FriendsModel;
import com.microsoft.xbox.service.model.MeProfileModel;
import com.microsoft.xbox.service.model.MessagingCapabilityStatus;
import com.microsoft.xbox.service.model.UpdateData;
import com.microsoft.xbox.service.model.UpdateType;
import com.microsoft.xbox.service.model.YouProfileModel;
import com.microsoft.xbox.service.model.serialization.Friend;
import com.microsoft.xbox.service.model.serialization.XLEAvatarManifest;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.app.XboxMobileOmnitureTracking;
import com.microsoft.xbox.xle.app.activity.ComposeMessageActivity;
import com.microsoft.xbox.xle.app.adapter.AdapterFactory;
import java.net.URI;
import java.util.EnumSet;

public class YouProfileActivityViewModel extends PivotViewModelBase {
    private AvatarManifestModel avatarModel;
    private AvatarViewVM avatarViewVM;
    private int friendState;
    private FriendsModel friendsModel;
    private String gamerName;
    private String gamerScore;
    private URI gamerpicUri;
    private boolean isBlockingBusy;
    private AvatarViewActorVMDefault meActorVM;
    private String membershipLevel;
    private String motto;
    private MottoBubbleTask mottoTask;
    private YouProfileModel profileModel;
    private boolean showPropFirst;
    private String youGamertag;

    public YouProfileActivityViewModel(String youGamertag) {
        boolean z = true;
        this.meActorVM = null;
        this.avatarViewVM = null;
        this.showPropFirst = true;
        this.adapter = AdapterFactory.getInstance().getYouProfileAdapter(this);
        String str = "You gamertag must not be empty.";
        if (youGamertag == null || youGamertag.length() <= 0) {
            z = false;
        }
        XLEAssert.assertTrue(str, z);
        this.youGamertag = youGamertag;
        this.mottoTask = new MottoBubbleTask(new Runnable() {
            public void run() {
                if (YouProfileActivityViewModel.this.isForeground) {
                    YouProfileActivityViewModel.this.adapter.updateView();
                }
            }
        });
    }

    public void onRehydrate() {
        this.adapter = AdapterFactory.getInstance().getYouProfileAdapter(this);
    }

    public String getGamertag() {
        return this.youGamertag;
    }

    public URI getGamerpicUri() {
        return this.gamerpicUri;
    }

    public String getGamerName() {
        return this.gamerName;
    }

    public boolean isGold() {
        return this.profileModel.getIsGold();
    }

    public boolean isAllowedToRespondToFriendRequests() {
        return !MeProfileModel.getModel().getIsParentallyControlled();
    }

    public String getGamerScore() {
        return this.gamerScore;
    }

    public int getAddFriendButtonVisibility() {
        return this.friendState == 3 ? 0 : 8;
    }

    public int getRemoveFriendButtonVisibility() {
        return (this.friendState == 0 || this.friendState == 1) ? 0 : 8;
    }

    public int getFriendRequestViewVisibility() {
        return this.friendState == 2 ? 0 : 8;
    }

    public int getFriendState() {
        return this.friendState;
    }

    public String getMotto() {
        return this.motto;
    }

    public boolean getShowMotto() {
        return this.mottoTask.getShowMotto();
    }

    public boolean isBusy() {
        return this.profileModel.getIsLoading() || this.friendsModel.getIsLoading() || this.avatarModel.getIsLoading() || isBlockingBusy();
    }

    public boolean isBlockingBusy() {
        return this.isBlockingBusy || this.friendsModel.getIsAcceptingFriendRequest() || this.friendsModel.getIsDecliningFriendRequest() || this.friendsModel.getIsAddingFriend() || this.friendsModel.getIsRemovingFriend();
    }

    public String getBlockingStatusText() {
        return XLEApplication.Resources.getString(R.string.blocking_status_updating);
    }

    public void updateOverride(AsyncResult<UpdateData> asyncResult) {
        Friend friend;
        switch (((UpdateData) asyncResult.getResult()).getUpdateType()) {
            case FriendsData:
                if (asyncResult.getException() == null && ((UpdateData) asyncResult.getResult()).getIsFinal()) {
                    friend = this.friendsModel.getFriend(this.youGamertag);
                    if (friend != null) {
                        this.gamerpicUri = friend.ProfileEx.ProfileProperties.GamerPicUri;
                        this.gamerName = friend.ProfileEx.ProfileProperties.Name;
                        this.membershipLevel = friend.ProfileEx.ProfileProperties.MembershipLevel;
                        this.gamerScore = friend.ProfileEx.ProfileProperties.Gamerscore;
                        this.friendState = friend.FriendState;
                        break;
                    }
                }
                break;
            case UpdateFriend:
                if (asyncResult.getException() == null && ((UpdateData) asyncResult.getResult()).getIsFinal()) {
                    XLEGlobalData.getInstance().setFriendListUpdated(true);
                    friend = this.friendsModel.getFriend(this.youGamertag);
                    if (friend != null) {
                        this.friendState = friend.FriendState;
                        if (this.friendState == 1) {
                            friend.ProfileEx.ProfileProperties.Gamertag = this.youGamertag;
                            friend.ProfileEx.ProfileProperties.GamerPicUri = this.gamerpicUri;
                            friend.ProfileEx.ProfileProperties.Gamerscore = this.gamerScore;
                            friend.ProfileEx.ProfileProperties.Name = this.gamerName;
                            friend.ProfileEx.ProfileProperties.MembershipLevel = this.membershipLevel;
                        }
                    } else {
                        this.friendState = 3;
                    }
                }
                this.isBlockingBusy = false;
                break;
            case YouProfileData:
                if (asyncResult.getException() == null && ((UpdateData) asyncResult.getResult()).getIsFinal() && this.profileModel.getGamertag() != null && this.profileModel.getGamertag().length() > 0) {
                    this.youGamertag = this.profileModel.getGamertag();
                    this.gamerName = this.profileModel.getName();
                    this.gamerpicUri = this.profileModel.getGamerPicUri();
                    this.gamerScore = this.profileModel.getGamerscore();
                    this.membershipLevel = this.profileModel.getMembershipLevel();
                    this.motto = this.profileModel.getMotto();
                    if (!(this.motto == null || this.motto.length() <= 0 || this.profileModel.getIsLoading())) {
                        this.mottoTask.setDataReady();
                        break;
                    }
                }
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
        if (checkErrorCode(UpdateType.YouProfileData, XLEErrorCode.FAILED_TO_GET_YOU_PROFILE) || checkErrorCode(UpdateType.FriendsData, XLEErrorCode.FAILED_TO_GET_FRIENDS)) {
            showError(R.string.toast_profile_error);
        } else if (checkErrorCode(UpdateType.UpdateFriend, XLEErrorCode.FAILED_TO_ACCEPT_FRIEND)) {
            showError(R.string.toast_friend_accept_error);
        } else if (checkErrorCode(UpdateType.UpdateFriend, XLEErrorCode.FAILED_TO_ADD_FRIEND)) {
            showError(R.string.toast_friend_send_request_error);
        } else if (checkErrorCode(UpdateType.UpdateFriend, XLEErrorCode.FAILED_TO_DECLINE_FRIEND)) {
            showError(R.string.toast_friend_decline_error);
        } else if (checkErrorCode(UpdateType.UpdateFriend, XLEErrorCode.FAILED_TO_REMOVE_FRIEND)) {
            showError(R.string.toast_friend_remove_error);
        }
        super.onUpdateFinished();
    }

    public void sendMessage() {
        MessagingCapabilityStatus status = MeProfileModel.getModel().getCanComposeMessage();
        if (status.getCanSendMessage()) {
            XLEGlobalData.getInstance().getSelectedRecipients().reset();
            XLEGlobalData.getInstance().getSelectedRecipients().add(this.youGamertag);
            NavigateTo(ComposeMessageActivity.class);
            return;
        }
        showError(status.getErrorResourceId());
    }

    public void showRemoveFriendDialog() {
        if (checkAllowedToModifyFriendState()) {
            String dialogMessage;
            if (this.friendState == 0) {
                dialogMessage = XLEApplication.Resources.getString(R.string.friend_request_dialog_remove);
            } else if (this.friendState == 1) {
                dialogMessage = XLEApplication.Resources.getString(R.string.friend_request_dialog_cancel);
            } else {
                dialogMessage = XLEApplication.Resources.getString(R.string.friend_request_dialog_remove);
                XLELog.Warning("YouProfileActivityViewModel", "We should never try to remove a friend when the friend state is neither pending or friend.");
            }
            showOkCancelDialog(XLEApplication.Resources.getString(R.string.dialog_areyousure_title), dialogMessage, XLEApplication.Resources.getString(R.string.Yes), new Runnable() {
                public void run() {
                    YouProfileActivityViewModel.this.removeFriend();
                }
            }, XLEApplication.Resources.getString(R.string.No), null);
        }
    }

    private void removeFriend() {
        setUpdateTypesToCheck(EnumSet.of(UpdateType.UpdateFriend));
        this.isBlockingBusy = true;
        this.friendsModel.removeFriend(this.youGamertag);
        this.adapter.updateView();
    }

    public void sendFriendRequest() {
        if (checkAllowedToModifyFriendState()) {
            setUpdateTypesToCheck(EnumSet.of(UpdateType.UpdateFriend));
            this.isBlockingBusy = true;
            this.friendsModel.sendFriendRequest(this.youGamertag);
            this.adapter.updateView();
            XboxMobileOmnitureTracking.TrackFriendRequest();
        }
    }

    public void acceptFriendRequest() {
        if (checkAllowedToModifyFriendState()) {
            setUpdateTypesToCheck(EnumSet.of(UpdateType.UpdateFriend));
            this.isBlockingBusy = true;
            this.friendsModel.acceptFriendRequest(this.youGamertag);
            this.adapter.updateView();
            XboxMobileOmnitureTracking.TrackFriendAccept();
        }
    }

    public void declineFriendRequest() {
        if (checkAllowedToModifyFriendState()) {
            setUpdateTypesToCheck(EnumSet.of(UpdateType.UpdateFriend));
            this.isBlockingBusy = true;
            this.friendsModel.declineFriendRequest(this.youGamertag);
            this.adapter.updateView();
            XboxMobileOmnitureTracking.TrackFriendDeny();
        }
    }

    public XLEAvatarManifest getManifest() {
        return this.avatarModel.getManifest();
    }

    public boolean getIsShadowtarVisible() {
        return this.meActorVM.getIsShadowtarVisible();
    }

    private boolean checkAllowedToModifyFriendState() {
        if (isAllowedToRespondToFriendRequests()) {
            return true;
        }
        showError(R.string.friend_request_child_blocked);
        return false;
    }

    public void load(boolean forceRefresh) {
        setUpdateTypesToCheck(EnumSet.of(UpdateType.YouProfileData, UpdateType.FriendsData));
        this.profileModel.load(forceRefresh);
        this.friendsModel.load(forceRefresh);
        this.avatarModel.load(forceRefresh);
    }

    protected void onStartOverride() {
        boolean z = true;
        String str = "MeProfileModel should have been loaded.";
        boolean z2 = MeProfileModel.getModel().getGamertag() != null && MeProfileModel.getModel().getGamertag().length() > 0;
        XLEAssert.assertTrue(str, z2);
        String str2 = "FriendsModel should have been loaded.";
        if (FriendsModel.getModel().getFriendsList() == null) {
            z = false;
        }
        XLEAssert.assertTrue(str2, z);
        this.mottoTask.resetReadyFlags();
        this.profileModel = YouProfileModel.getModel(this.youGamertag);
        this.friendsModel = FriendsModel.getModel();
        this.avatarModel = AvatarManifestModel.getGamerModel(this.youGamertag);
        this.profileModel.addObserver(this);
        this.friendsModel.addObserver(this);
        this.avatarModel.addObserver(this);
        if (this.friendsModel.getFriend(this.youGamertag) != null) {
            this.friendState = this.friendsModel.getFriend(this.youGamertag).FriendState;
        } else {
            this.friendState = 3;
        }
        this.avatarViewVM = new AvatarViewVMDefault();
        this.meActorVM = new AvatarViewActorVMDefault();
        this.meActorVM.setShowPropFirst(this.showPropFirst);
        this.avatarViewVM.registerActor(this.meActorVM);
        this.meActorVM.setShadowtarVisibilityChangedCallback(new Runnable() {
            public void run() {
                if (YouProfileActivityViewModel.this.isForeground) {
                    YouProfileActivityViewModel.this.adapter.updateView();
                }
            }
        });
        this.meActorVM.setMottoShowCallback(new Runnable() {
            public void run() {
                YouProfileActivityViewModel.this.mottoTask.setAnimationReady();
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
        this.avatarViewVM.onDestroy();
        this.avatarViewVM = null;
        this.meActorVM = null;
        this.showPropFirst = false;
    }

    public AvatarViewActorVM getActorVM() {
        return this.meActorVM;
    }

    public AvatarViewVM getAvatarViewVM() {
        return this.avatarViewVM;
    }
}
