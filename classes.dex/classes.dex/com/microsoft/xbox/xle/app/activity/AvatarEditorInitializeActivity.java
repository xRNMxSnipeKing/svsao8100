package com.microsoft.xbox.xle.app.activity;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.avatar.model.AvatarRendererModel;
import com.microsoft.xbox.toolkit.SoundManager;
import com.microsoft.xbox.xle.app.XboxMobileOmnitureTracking;
import com.microsoft.xbox.xle.viewmodel.AvatarEditorInitializeActivityViewModel;

public class AvatarEditorInitializeActivity extends ActivityBase {
    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        this.viewModel = new AvatarEditorInitializeActivityViewModel();
        SoundManager.getInstance().loadSound(R.raw.avatartransition);
        XboxMobileOmnitureTracking.TrackAvatarEditStart(Integer.toString(AvatarRendererModel.getInstance().getFPS()));
    }

    public void onCreateContentView() {
        setContentView(R.layout.avatar_editor_initialize_activity);
        setAppBarLayout(-1, true, false);
    }

    protected String getActivityName() {
        return "AvatarEditorInitialize";
    }

    protected String getChannelName() {
        return ActivityBase.avatarChannel;
    }
}
