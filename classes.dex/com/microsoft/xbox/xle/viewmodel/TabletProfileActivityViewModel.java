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
import com.microsoft.xbox.service.model.MessagingCapabilityStatus;
import com.microsoft.xbox.service.model.QuickplayModel;
import com.microsoft.xbox.service.model.UpdateData;
import com.microsoft.xbox.service.model.UpdateType;
import com.microsoft.xbox.service.model.YouProfileModel;
import com.microsoft.xbox.service.model.edsv2.EDSV2GameMediaItem;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaItem;
import com.microsoft.xbox.service.model.serialization.Friend;
import com.microsoft.xbox.service.model.serialization.GameInfo;
import com.microsoft.xbox.service.model.serialization.XLEAvatarManifest;
import com.microsoft.xbox.service.model.sls.Title;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.network.ListState;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.app.XboxMobileOmnitureTracking;
import com.microsoft.xbox.xle.app.activity.AchievementsActivity;
import com.microsoft.xbox.xle.app.activity.AvatarEditorInitializeActivity;
import com.microsoft.xbox.xle.app.activity.CollecitonGalleryActivity;
import com.microsoft.xbox.xle.app.activity.CompareAchievementsActivity;
import com.microsoft.xbox.xle.app.activity.CompareGamesActivity;
import com.microsoft.xbox.xle.app.activity.ComposeMessageActivity;
import com.microsoft.xbox.xle.app.activity.EditProfileActivity;
import com.microsoft.xbox.xle.app.adapter.AdapterFactory;
import java.net.URI;
import java.util.ArrayList;
import java.util.EnumSet;

public class TabletProfileActivityViewModel extends ViewModelBase {
    private AvatarManifestModel avatarModel;
    private AvatarViewVM avatarViewVM;
    private int friendState;
    private FriendsModel friendsModel;
    private String gamerBio;
    private String gamerName;
    private String gamerScore;
    private URI gamerpicUri;
    private String gamertag;
    private boolean isBlockingBusy;
    private boolean isGold;
    private String location;
    private AvatarViewActorVMDefault meActorVM;
    private MeProfileModel meProfileModel;
    private QuickplayModel meTitleModel;
    private String membershipLevel;
    private String motto;
    private MottoBubbleTask mottoTask;
    private ListState recentGamesViewModelState;
    private boolean showPropFirst;
    private YouProfileModel youProfileModel;

    public TabletProfileActivityViewModel(String youGamertag) {
        boolean z = true;
        this.recentGamesViewModelState = ListState.LoadingState;
        this.meActorVM = null;
        this.avatarViewVM = null;
        this.showPropFirst = true;
        this.adapter = AdapterFactory.getInstance().getTabletProfileAdapter(this);
        String str = "You gamertag must not be empty.";
        if (youGamertag == null || youGamertag.length() <= 0) {
            z = false;
        }
        XLEAssert.assertTrue(str, z);
        this.gamertag = youGamertag;
        this.mottoTask = new MottoBubbleTask(new Runnable() {
            public void run() {
                if (TabletProfileActivityViewModel.this.isForeground) {
                    TabletProfileActivityViewModel.this.adapter.updateView();
                }
            }
        });
    }

    public void onRehydrate() {
        this.adapter = AdapterFactory.getInstance().getTabletProfileAdapter(this);
    }

    public String getGamertag() {
        return this.gamertag;
    }

    public URI getGamerpicUri() {
        return this.gamerpicUri;
    }

    public String getGamerName() {
        return this.gamerName;
    }

    public String getGamerLocation() {
        return this.location;
    }

    public String getGamerBio() {
        return this.gamerBio;
    }

    public boolean isGold() {
        return this.isGold;
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
        return this.friendState == 0 ? 0 : 8;
    }

    public int getCancelRequestButtonVisibility() {
        return this.friendState == 1 ? 0 : 8;
    }

    public int getSendMessageButtonVisibility() {
        return this.friendState == 2 ? 8 : 0;
    }

    public int getFriendRequestViewVisibility() {
        return this.friendState == 2 ? 0 : 8;
    }

    public String getMotto() {
        return this.motto;
    }

    public boolean getShowMotto() {
        return this.mottoTask.getShowMotto();
    }

    public ArrayList getGames() {
        if (getIsMeProfile()) {
            return this.meTitleModel.getGamesQuickplayList();
        }
        return this.youProfileModel.getRecentGames();
    }

    public boolean getIsShadowtarVisible() {
        return this.meActorVM.getIsShadowtarVisible();
    }

    public ListState getViewModelState() {
        return this.recentGamesViewModelState;
    }

    public boolean getIsMeProfile() {
        return this.gamertag.equalsIgnoreCase(MeProfileModel.getModel().getGamertag());
    }

    public boolean isBusy() {
        if (getIsMeProfile()) {
            if (this.meProfileModel.getIsLoading() || this.meTitleModel.getIsLoading()) {
                return true;
            }
            return false;
        } else if (this.youProfileModel.getIsLoading() || this.friendsModel.getIsLoading() || this.avatarModel.getIsLoading() || isBlockingBusy()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isBlockingBusy() {
        if (getIsMeProfile()) {
            return this.isBlockingBusy;
        }
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
                    friend = this.friendsModel.getFriend(this.gamertag);
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
                    friend = this.friendsModel.getFriend(this.gamertag);
                    if (friend != null) {
                        this.friendState = friend.FriendState;
                        if (this.friendState == 1) {
                            friend.ProfileEx.ProfileProperties.Gamertag = this.gamertag;
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
                if (asyncResult.getException() == null && ((UpdateData) asyncResult.getResult()).getIsFinal() && this.youProfileModel.getGamertag() != null && this.youProfileModel.getGamertag().length() > 0) {
                    this.gamertag = this.youProfileModel.getGamertag();
                    this.gamerName = this.youProfileModel.getName();
                    this.gamerpicUri = this.youProfileModel.getGamerPicUri();
                    this.gamerScore = this.youProfileModel.getGamerscore();
                    this.membershipLevel = this.youProfileModel.getMembershipLevel();
                    this.motto = this.youProfileModel.getMotto();
                    this.location = this.youProfileModel.getLocation();
                    this.gamerBio = this.youProfileModel.getBio();
                    this.isGold = this.youProfileModel.getIsGold();
                    if (!(this.motto == null || this.motto.length() <= 0 || this.youProfileModel.getIsLoading())) {
                        this.mottoTask.setDataReady();
                    }
                }
                if (asyncResult.getException() == null) {
                    if (((UpdateData) asyncResult.getResult()).getIsFinal()) {
                        updateRecentGamesViewModelState();
                        break;
                    }
                } else if (getGames() == null) {
                    this.recentGamesViewModelState = ListState.ErrorState;
                    break;
                }
                break;
            case RecentsData:
                if (asyncResult.getException() == null) {
                    if (((UpdateData) asyncResult.getResult()).getIsFinal()) {
                        this.recentGamesViewModelState = CollectionActivityViewModel.getListState(this.meTitleModel.getGamesQuickplayList());
                        break;
                    }
                } else if (this.meTitleModel.getGamesQuickplayList() == null || this.meTitleModel.getGamesQuickplayList().size() == 0) {
                    this.recentGamesViewModelState = ListState.ErrorState;
                    break;
                }
                break;
            case MeProfileData:
                if (asyncResult.getException() == null && ((UpdateData) asyncResult.getResult()).getIsFinal()) {
                    updateDataWithMeProfileData();
                    if (!(this.motto == null || this.motto.length() <= 0 || this.meProfileModel.getIsLoading())) {
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
        if (checkErrorCode(UpdateType.YouProfileData, XLEErrorCode.FAILED_TO_GET_YOU_PROFILE) && getViewModelState() != ListState.ValidContentState) {
            this.recentGamesViewModelState = ListState.ErrorState;
            this.adapter.updateView();
        }
        super.onUpdateFinished();
    }

    public void sendMessage() {
        MessagingCapabilityStatus status = MeProfileModel.getModel().getCanComposeMessage();
        if (status.getCanSendMessage()) {
            XLEGlobalData.getInstance().getSelectedRecipients().reset();
            XLEGlobalData.getInstance().getSelectedRecipients().add(this.gamertag);
            NavigateTo(ComposeMessageActivity.class);
            return;
        }
        showError(status.getErrorResourceId());
    }

    public void navigeteToCompareGamesPage() {
        NavigateTo(CompareGamesActivity.class);
    }

    public void navigateToGameDetailsPage(Title title) {
        navigateToAppOrMediaDetails((EDSV2MediaItem) new EDSV2GameMediaItem(title), AchievementsActivity.class);
    }

    public void navigateToCompareAchievements(GameInfo game) {
        XLEAssert.assertNotNull("Game should not be null.", game);
        XLEGlobalData.getInstance().setSelectedGamertag(this.youProfileModel.getGamertag());
        XLEGlobalData.getInstance().setSelectedGame(game);
        XboxMobileOmnitureTracking.TrackCompareGame(game.Name);
        XLELog.Info("YouProfileActivityViewModel", String.format("Navigating to compare achievements for gamertag=%s, titleid=0x%x", new Object[]{this.youProfileModel.getGamertag(), Long.valueOf(game.Id)}));
        NavigateTo(CompareAchievementsActivity.class);
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
                    TabletProfileActivityViewModel.this.removeFriend();
                }
            }, XLEApplication.Resources.getString(R.string.No), null);
        }
    }

    public void sendFriendRequest() {
        if (checkAllowedToModifyFriendState()) {
            setUpdateTypesToCheck(EnumSet.of(UpdateType.UpdateFriend));
            this.isBlockingBusy = true;
            this.friendsModel.sendFriendRequest(this.gamertag);
            this.adapter.updateView();
            XboxMobileOmnitureTracking.TrackFriendRequest();
        }
    }

    public void acceptFriendRequest() {
        if (checkAllowedToModifyFriendState()) {
            setUpdateTypesToCheck(EnumSet.of(UpdateType.UpdateFriend));
            this.isBlockingBusy = true;
            this.friendsModel.acceptFriendRequest(this.gamertag);
            this.adapter.updateView();
            XboxMobileOmnitureTracking.TrackFriendAccept();
        }
    }

    public void declineFriendRequest() {
        if (checkAllowedToModifyFriendState()) {
            setUpdateTypesToCheck(EnumSet.of(UpdateType.UpdateFriend));
            this.isBlockingBusy = true;
            this.friendsModel.declineFriendRequest(this.gamertag);
            this.adapter.updateView();
            XboxMobileOmnitureTracking.TrackFriendDeny();
        }
    }

    public void navigateToViewAllGames() {
        XLEGlobalData.getInstance().setHideCollectionFilter(true);
        NavigateTo(CollecitonGalleryActivity.class);
    }

    public void navigateToEditAvatar() {
        NavigateTo(AvatarEditorInitializeActivity.class);
    }

    public void navigateToEditProfile() {
        NavigateTo(EditProfileActivity.class);
    }

    private void removeFriend() {
        setUpdateTypesToCheck(EnumSet.of(UpdateType.UpdateFriend));
        this.isBlockingBusy = true;
        this.friendsModel.removeFriend(this.gamertag);
        this.adapter.updateView();
    }

    private boolean checkAllowedToModifyFriendState() {
        if (isAllowedToRespondToFriendRequests()) {
            return true;
        }
        showError(R.string.friend_request_child_blocked);
        return false;
    }

    public AvatarViewActorVM getActorVM() {
        return this.meActorVM;
    }

    public AvatarViewVM getAvatarViewVM() {
        return this.avatarViewVM;
    }

    public void load(boolean forceRefresh) {
        if (getIsMeProfile()) {
            this.meProfileModel.load(forceRefresh);
            this.meTitleModel.load(forceRefresh);
        } else {
            setUpdateTypesToCheck(EnumSet.of(UpdateType.YouProfileData, UpdateType.FriendsData));
            this.youProfileModel.load(forceRefresh);
            this.friendsModel.load(forceRefresh);
        }
        this.avatarModel.load(forceRefresh);
    }

    protected void onStartOverride() {
        boolean z = true;
        XLEApplication.getMainActivity().clearAvatarViewFloat();
        AvatarEditorModel.getInstance().shutdownIfNecessary();
        this.mottoTask.resetReadyFlags();
        String str;
        if (getIsMeProfile()) {
            str = "MeProfileModel should have been loaded.";
            if (MeProfileModel.getModel().getGamertag() == null || MeProfileModel.getModel().getGamertag().length() <= 0) {
                z = false;
            }
            XLEAssert.assertTrue(str, z);
            this.avatarModel = AvatarManifestModel.getPlayerModel();
            this.meProfileModel = MeProfileModel.getModel();
            this.meTitleModel = QuickplayModel.getInstance();
            this.meProfileModel.addObserver(this);
            this.meTitleModel.addObserver(this);
        } else {
            str = "FriendsModel should have been loaded.";
            if (FriendsModel.getModel().getFriendsList() == null) {
                z = false;
            }
            XLEAssert.assertTrue(str, z);
            this.avatarModel = AvatarManifestModel.getGamerModel(this.gamertag);
            this.youProfileModel = YouProfileModel.getModel(this.gamertag);
            this.friendsModel = FriendsModel.getModel();
            this.youProfileModel.addObserver(this);
            this.friendsModel.addObserver(this);
            if (this.friendsModel.getFriend(this.gamertag) != null) {
                this.friendState = this.friendsModel.getFriend(this.gamertag).FriendState;
            } else {
                this.friendState = 3;
            }
        }
        this.avatarModel.addObserver(this);
        this.avatarViewVM = new AvatarViewVMDefault();
        this.meActorVM = new AvatarViewActorVMDefault();
        this.meActorVM.setShowPropFirst(this.showPropFirst);
        this.avatarViewVM.registerActor(this.meActorVM);
        this.meActorVM.setShadowtarVisibilityChangedCallback(new Runnable() {
            public void run() {
                if (TabletProfileActivityViewModel.this.isForeground) {
                    TabletProfileActivityViewModel.this.adapter.updateView();
                }
            }
        });
        this.meActorVM.setMottoShowCallback(new Runnable() {
            public void run() {
                TabletProfileActivityViewModel.this.mottoTask.setAnimationReady();
            }
        });
    }

    protected void onStopOverride() {
        this.mottoTask.cancelMotto();
        if (getIsMeProfile()) {
            this.meProfileModel.removeObserver(this);
            this.meTitleModel.removeObserver(this);
        } else {
            this.youProfileModel.removeObserver(this);
            this.friendsModel.removeObserver(this);
        }
        this.avatarModel.removeObserver(this);
        this.youProfileModel = null;
        this.friendsModel = null;
        this.avatarModel = null;
        this.meProfileModel = null;
        this.meTitleModel = null;
        this.avatarViewVM.onDestroy();
        this.avatarViewVM = null;
        this.meActorVM = null;
        this.showPropFirst = false;
    }

    private void updateRecentGamesViewModelState() {
        ListState newState = ListState.LoadingState;
        if (getGames() == null) {
            newState = ListState.LoadingState;
        } else if (getGames().size() == 0) {
            newState = ListState.NoContentState;
        } else {
            newState = ListState.ValidContentState;
        }
        if (this.recentGamesViewModelState != newState) {
            this.recentGamesViewModelState = newState;
        }
    }

    private void updateDataWithMeProfileData() {
        if (getIsMeProfile()) {
            this.gamertag = this.meProfileModel.getGamertag();
            this.gamerpicUri = this.meProfileModel.getGamerPicUri();
            this.gamerName = this.meProfileModel.getName();
            this.membershipLevel = this.meProfileModel.getMembershipLevel();
            this.gamerScore = this.meProfileModel.getGamerscore();
            this.location = this.meProfileModel.getLocation();
            this.gamerBio = this.meProfileModel.getBio();
            this.motto = this.meProfileModel.getMotto();
        }
    }
}
