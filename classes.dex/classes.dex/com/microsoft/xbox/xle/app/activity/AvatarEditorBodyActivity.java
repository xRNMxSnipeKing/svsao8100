package com.microsoft.xbox.xle.app.activity;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.xle.viewmodel.AvatarEditorBodyActivityViewModel;

public class AvatarEditorBodyActivity extends ActivityBase {
    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        this.viewModel = new AvatarEditorBodyActivityViewModel();
    }

    public void onCreateContentView() {
        setContentView(R.layout.avatar_editor_body);
        setAppBarLayout(-1, true, false);
    }

    protected String getActivityName() {
        return "AvatarEditorBody";
    }

    protected String getChannelName() {
        return ActivityBase.avatarChannel;
    }
}
