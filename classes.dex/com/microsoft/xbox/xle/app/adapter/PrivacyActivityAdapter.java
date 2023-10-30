package com.microsoft.xbox.xle.app.adapter;

import android.view.View;
import android.view.View.OnClickListener;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.ui.XLEButton;
import com.microsoft.xbox.xle.ui.PrivacySettingView;
import com.microsoft.xbox.xle.ui.PrivacySettingView.OnSelectionChanged;
import com.microsoft.xbox.xle.viewmodel.AdapterBaseNormal;
import com.microsoft.xbox.xle.viewmodel.PrivacyActivityViewModel;

public class PrivacyActivityAdapter extends AdapterBaseNormal {
    private XLEButton cancelButton;
    private PrivacySettingView friendsListView;
    private PrivacySettingView gameHistoryView;
    private PrivacySettingView memberContentView;
    private PrivacySettingView onlineStatusView;
    private PrivacyActivityViewModel privacyViewModel;
    private PrivacySettingView profileSharingView;
    private PrivacySettingView voiceAndTextView;

    public PrivacyActivityAdapter(PrivacyActivityViewModel viewModel) {
        this.screenBody = findViewById(R.id.privacy_activity_body);
        this.privacyViewModel = viewModel;
        this.voiceAndTextView = (PrivacySettingView) findViewById(R.id.privacy_voice_and_text);
        this.profileSharingView = (PrivacySettingView) findViewById(R.id.privacy_profile_sharing);
        this.onlineStatusView = (PrivacySettingView) findViewById(R.id.privacy_online_status);
        this.memberContentView = (PrivacySettingView) findViewById(R.id.privacy_member_content);
        this.gameHistoryView = (PrivacySettingView) findViewById(R.id.privacy_game_history);
        this.friendsListView = (PrivacySettingView) findViewById(R.id.privacy_friends_list);
    }

    protected void onAppBarUpdated() {
        setAppBarButtonClickListener(R.id.appbar_save, new OnClickListener() {
            public void onClick(View v) {
                PrivacyActivityAdapter.this.privacyViewModel.save();
            }
        });
        setAppBarButtonClickListener(R.id.appbar_cancel, new OnClickListener() {
            public void onClick(View v) {
                PrivacyActivityAdapter.this.privacyViewModel.cancel();
            }
        });
    }

    public void updateViewOverride() {
        updateLoadingIndicator(this.privacyViewModel.isBusy());
        setAppBarButtonEnabled(R.id.appbar_save, this.privacyViewModel.getIsDirty());
        setBlocking(this.privacyViewModel.isBlockingBusy(), this.privacyViewModel.getBlockingStatusText());
    }

    public void loadInitialDataFromVM() {
        this.voiceAndTextView.setSelectedOption(this.privacyViewModel.getVoiceAndText());
        this.profileSharingView.setSelectedOption(this.privacyViewModel.getProfileSharing());
        this.onlineStatusView.setSelectedOption(this.privacyViewModel.getOnlineStatus());
        this.memberContentView.setSelectedOption(this.privacyViewModel.getMemberContent());
        this.gameHistoryView.setSelectedOption(this.privacyViewModel.getGameHistory());
        this.friendsListView.setSelectedOption(this.privacyViewModel.getFriendsList());
        this.voiceAndTextView.setOnSelectionChanged(new OnSelectionChanged() {
            public void run(int newSelection) {
                PrivacyActivityAdapter.this.privacyViewModel.setVoiceAndText(newSelection);
                PrivacyActivityAdapter.this.markAsDirty();
            }
        });
        this.profileSharingView.setOnSelectionChanged(new OnSelectionChanged() {
            public void run(int newSelection) {
                PrivacyActivityAdapter.this.privacyViewModel.setProfileSharing(newSelection);
                PrivacyActivityAdapter.this.markAsDirty();
            }
        });
        this.onlineStatusView.setOnSelectionChanged(new OnSelectionChanged() {
            public void run(int newSelection) {
                PrivacyActivityAdapter.this.privacyViewModel.setOnlineStatus(newSelection);
                PrivacyActivityAdapter.this.markAsDirty();
            }
        });
        this.memberContentView.setOnSelectionChanged(new OnSelectionChanged() {
            public void run(int newSelection) {
                PrivacyActivityAdapter.this.privacyViewModel.setMemberContent(newSelection);
                PrivacyActivityAdapter.this.markAsDirty();
            }
        });
        this.gameHistoryView.setOnSelectionChanged(new OnSelectionChanged() {
            public void run(int newSelection) {
                PrivacyActivityAdapter.this.privacyViewModel.setGameHistory(newSelection);
                PrivacyActivityAdapter.this.markAsDirty();
            }
        });
        this.friendsListView.setOnSelectionChanged(new OnSelectionChanged() {
            public void run(int newSelection) {
                PrivacyActivityAdapter.this.privacyViewModel.setFriendsList(newSelection);
                PrivacyActivityAdapter.this.markAsDirty();
            }
        });
    }

    private void markAsDirty() {
        setAppBarButtonEnabled(R.id.appbar_save, true);
        this.privacyViewModel.setIsDirty(true);
    }
}
