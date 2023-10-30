package com.microsoft.xbox.xle.viewmodel;

import android.os.Bundle;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.MeProfileModel;
import com.microsoft.xbox.service.model.UpdateData;
import com.microsoft.xbox.service.model.UpdateType;
import com.microsoft.xbox.service.model.serialization.PrivacySettings;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.app.adapter.PrivacyActivityAdapter;
import java.util.EnumSet;

public class PrivacyActivityViewModel extends ViewModelBase {
    private final String DIRTY_STATE;
    private final String FRIENDS_LIST;
    private final String GAME_HISTORY;
    private final String MEMBER_CONTENT;
    private final String ONLINE_STATUS;
    private final String PROFILE_SHARING;
    private final String VOICE_AND_TEXT;
    private int friendsList;
    private int gameHistory;
    private boolean isDirty;
    private int memberContent;
    private int onlineStatus;
    private MeProfileModel profileModel;
    private int profileSharing;
    private int voiceAndText;

    public PrivacyActivityViewModel() {
        this.VOICE_AND_TEXT = "VoiceAndText";
        this.PROFILE_SHARING = "ProfileSharing";
        this.ONLINE_STATUS = "OnlineStatus";
        this.MEMBER_CONTENT = "MemberContent";
        this.GAME_HISTORY = "GameHistory";
        this.FRIENDS_LIST = "FriendsList";
        this.DIRTY_STATE = "DirtyState";
        this.adapter = new PrivacyActivityAdapter(this);
    }

    public void onRehydrate() {
        this.adapter = new PrivacyActivityAdapter(this);
    }

    public int getVoiceAndText() {
        return this.voiceAndText;
    }

    public void setVoiceAndText(int value) {
        this.voiceAndText = value;
    }

    public int getProfileSharing() {
        return this.profileSharing;
    }

    public void setProfileSharing(int value) {
        this.profileSharing = value;
    }

    public int getOnlineStatus() {
        return this.onlineStatus;
    }

    public void setOnlineStatus(int value) {
        this.onlineStatus = value;
    }

    public int getMemberContent() {
        return this.memberContent;
    }

    public void setMemberContent(int value) {
        this.memberContent = value;
    }

    public int getGameHistory() {
        return this.gameHistory;
    }

    public void setGameHistory(int value) {
        this.gameHistory = value;
    }

    public int getFriendsList() {
        return this.friendsList;
    }

    public void setFriendsList(int value) {
        this.friendsList = value;
    }

    public void setIsDirty(boolean isDirty) {
        this.isDirty = isDirty;
    }

    public boolean getIsDirty() {
        return this.isDirty;
    }

    public boolean isBusy() {
        return this.profileModel.getIsLoading();
    }

    public boolean isBlockingBusy() {
        return this.profileModel.getIsSaving();
    }

    public String getBlockingStatusText() {
        return XLEApplication.Resources.getString(R.string.blocking_status_updating);
    }

    public void updateOverride(AsyncResult<UpdateData> asyncResult) {
        switch (((UpdateData) asyncResult.getResult()).getUpdateType()) {
            case MePrivacySave:
                if (asyncResult.getException() == null && ((UpdateData) asyncResult.getResult()).getIsFinal()) {
                    goBack();
                    return;
                }
        }
        this.adapter.updateView();
    }

    protected void onUpdateFinished() {
        boolean goBack = false;
        if (checkErrorCode(UpdateType.MePrivacySave, 10)) {
            XLELog.Warning("PrivacyActivityViewModel", "We tried to save privacy when there was no change.");
            goBack = true;
        } else if (checkErrorCode(UpdateType.MePrivacySave, XLEErrorCode.FAILED_TO_SAVE_PRIVACY)) {
            showError(R.string.toast_privacy_save_error);
        }
        super.onUpdateFinished();
        if (goBack) {
            goBack();
        }
    }

    public void save() {
        XLEAssert.assertTrue("Save button should've been disabled.", this.isDirty);
        setUpdateTypesToCheck(EnumSet.of(UpdateType.MePrivacySave));
        PrivacySettings newData = new PrivacySettings();
        newData.VoiceAndText = this.voiceAndText;
        newData.GamerProfile = this.profileSharing;
        newData.OnlineStatus = this.onlineStatus;
        newData.MemberContent = this.memberContent;
        newData.PlayedGames = this.gameHistory;
        newData.FriendsList = this.friendsList;
        if (this.profileModel.uploadNewPrivacyData(newData)) {
            this.adapter.updateView();
        }
    }

    public void onBackButtonPressed() {
        cancel();
    }

    public void cancel() {
        if (this.isDirty) {
            showDiscardChangesGoBack();
        } else {
            goBack();
        }
    }

    public void load(boolean forceRefresh) {
    }

    protected void onStartOverride() {
        String str = "MeProfileModel should have been loaded.";
        boolean z = MeProfileModel.getModel().getGamertag() != null && MeProfileModel.getModel().getGamertag().length() > 0;
        XLEAssert.assertTrue(str, z);
        this.profileModel = MeProfileModel.getModel();
        this.profileModel.addObserver(this);
        setVoiceAndText(this.profileModel.getVoiceAndTextSetting());
        setProfileSharing(this.profileModel.getShareProfileSetting());
        setOnlineStatus(this.profileModel.getShowOnlineStatusSetting());
        setMemberContent(this.profileModel.getUseMemberContentSetting());
        setGameHistory(this.profileModel.getShareGameHistorySetting());
        setFriendsList(this.profileModel.getShowFriendsListSetting());
        this.adapter.loadInitialDataFromVM();
    }

    protected void onStopOverride() {
        this.profileModel.removeObserver(this);
        this.profileModel = null;
    }

    public void onSaveInstanceState(Bundle outState) {
        if (outState != null) {
            outState.putInt("VoiceAndText", getVoiceAndText());
            outState.putInt("ProfileSharing", getProfileSharing());
            outState.putInt("OnlineStatus", getOnlineStatus());
            outState.putInt("MemberContent", getMemberContent());
            outState.putInt("GameHistory", getGameHistory());
            outState.putInt("FriendsList", getFriendsList());
            outState.putBoolean("DirtyState", this.isDirty);
        }
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            setVoiceAndText(savedInstanceState.getInt("VoiceAndText"));
            setProfileSharing(savedInstanceState.getInt("ProfileSharing"));
            setOnlineStatus(savedInstanceState.getInt("OnlineStatus"));
            setMemberContent(savedInstanceState.getInt("MemberContent"));
            setGameHistory(savedInstanceState.getInt("GameHistory"));
            setFriendsList(savedInstanceState.getInt("FriendsList"));
            setIsDirty(savedInstanceState.getBoolean("DirtyState"));
        }
        this.adapter.loadInitialDataFromVM();
    }
}
