package com.microsoft.xbox.avatar.view;

import com.microsoft.xbox.avatar.model.AvatarRendererModel;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.xbox.avatarrenderer.Core2View;

public class AvatarRendererView {
    private static AvatarRendererView instance = new AvatarRendererView();
    private Core2View sharedView = null;

    public static AvatarRendererView getInstance() {
        return instance;
    }

    public Core2View getSharedView() {
        if (this.sharedView == null) {
            this.sharedView = new Core2View(XboxApplication.Instance);
            this.sharedView.initialize(XboxApplication.Instance.getApplicationContext(), AvatarRendererModel.getInstance().getCore2Model(), Boolean.valueOf(true), Boolean.valueOf(AvatarRendererModel.getInstance().getAntiAlias()));
        }
        return this.sharedView;
    }

    public void onPause() {
        if (this.sharedView != null) {
            this.sharedView.onPause();
        }
    }

    public void onResume() {
        if (this.sharedView != null) {
            this.sharedView.onResume();
        }
    }
}
