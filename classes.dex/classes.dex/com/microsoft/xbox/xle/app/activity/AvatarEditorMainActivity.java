package com.microsoft.xbox.xle.app.activity;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.xle.viewmodel.AvatarEditorMainActivityViewModel;

public class AvatarEditorMainActivity extends ActivityBase {
    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        this.viewModel = new AvatarEditorMainActivityViewModel();
    }

    public void onCreateContentView() {
        setContentView(R.layout.avatar_editor_main);
        setAppBarLayout(R.layout.avatar_editor_main_appbar, true, false);
    }

    protected String getActivityName() {
        return "AvatarEditorMain";
    }

    protected String getChannelName() {
        return ActivityBase.avatarChannel;
    }
}
