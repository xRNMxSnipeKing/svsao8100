package com.microsoft.xbox.xle.app.activity;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.xle.viewmodel.AvatarEditorPreviewActivityViewModel;

public class AvatarEditorPreviewActivity extends ActivityBase {
    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        this.viewModel = new AvatarEditorPreviewActivityViewModel();
    }

    public void onCreateContentView() {
        setContentView(R.layout.avatar_editor_preview);
        setAppBarLayout(R.layout.avatar_editor_preview_appbar, true, false);
    }

    protected String getActivityName() {
        return "AvatarEditorPreview";
    }

    protected String getChannelName() {
        return ActivityBase.avatarChannel;
    }
}
