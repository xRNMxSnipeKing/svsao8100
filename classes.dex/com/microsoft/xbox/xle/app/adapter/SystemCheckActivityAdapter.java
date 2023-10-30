package com.microsoft.xbox.xle.app.adapter;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.avatar.model.AvatarRendererModel;
import com.microsoft.xbox.xle.viewmodel.AdapterBase;
import com.microsoft.xbox.xle.viewmodel.SystemCheckActivityViewModel;
import com.xbox.avatarrenderer.Core2View;

public class SystemCheckActivityAdapter extends AdapterBase {
    private Core2View avatarView;
    private boolean avatarViewInitialized = false;
    private SystemCheckActivityViewModel viewModel;

    public SystemCheckActivityAdapter(SystemCheckActivityViewModel vm) {
        this.viewModel = vm;
        this.avatarView = (Core2View) findViewById(R.id.syscheck_core2View);
    }

    public void updateViewOverride() {
        if (!this.avatarViewInitialized) {
            this.avatarViewInitialized = true;
            this.avatarView.initialize(this.avatarView.getContext(), this.viewModel.getCore2Renderer(), Boolean.valueOf(true), Boolean.valueOf(AvatarRendererModel.getInstance().getAntiAlias()));
        }
    }

    public void onPause() {
        super.onPause();
        if (this.avatarView != null && this.avatarViewInitialized) {
            this.avatarView.onPause();
        }
    }

    public void onResume() {
        super.onResume();
        if (this.avatarView != null && this.avatarViewInitialized) {
            this.avatarView.onResume();
        }
    }
}
