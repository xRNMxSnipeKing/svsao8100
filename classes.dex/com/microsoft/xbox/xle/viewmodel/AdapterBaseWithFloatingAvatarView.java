package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.xbox.avatar.view.AvatarViewActor;
import com.microsoft.xbox.xle.app.XLEApplication;

public abstract class AdapterBaseWithFloatingAvatarView extends AdapterBaseWithAvatar {
    protected AvatarViewActor avatarActor;

    public AdapterBaseWithFloatingAvatarView() {
        super(false);
        this.avatarView = XLEApplication.getMainActivity().getAvatarViewFloat();
        this.avatarActor = XLEApplication.getMainActivity().getAvatarActorFloat();
    }

    public void onStart() {
        if (this.avatarView != null) {
            this.avatarView.forceRenderFrame();
        }
        super.onStart();
    }
}
