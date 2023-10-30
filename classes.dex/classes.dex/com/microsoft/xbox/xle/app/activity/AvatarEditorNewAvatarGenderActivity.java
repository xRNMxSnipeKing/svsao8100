package com.microsoft.xbox.xle.app.activity;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.xle.viewmodel.AvatarEditorNewAvatarGenderActivityViewModel;

public class AvatarEditorNewAvatarGenderActivity extends ActivityBase {
    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        this.viewModel = new AvatarEditorNewAvatarGenderActivityViewModel();
    }

    public void onCreateContentView() {
        setContentView(R.layout.avatar_editor_new_avatar_gender);
        setAppBarLayout(-1, true, false);
    }

    protected String getActivityName() {
        return "AvatarEditorNewAvatarGender";
    }

    protected String getChannelName() {
        return ActivityBase.avatarChannel;
    }
}
