package com.microsoft.xbox.xle.app.adapter;

import android.widget.LinearLayout;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.xle.viewmodel.AdapterBaseWithFloatingAvatarView;
import com.microsoft.xbox.xle.viewmodel.AvatarEditorInitializeActivityViewModel;

public class AvatarEditorInitializeAdapter extends AdapterBaseWithFloatingAvatarView {
    private AvatarEditorInitializeActivityViewModel viewModel;

    public AvatarEditorInitializeAdapter(AvatarEditorInitializeActivityViewModel vm) {
        this.viewModel = vm;
        this.screenBody = (LinearLayout) findViewById(R.id.avatar_initialize_body);
    }

    public void updateViewOverride() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        setBlocking(this.viewModel.isBlockingBusy(), this.viewModel.getBlockingStatusText());
        XLELog.Diagnostic("AvatarEditorInitializeActivity", "Initialized AvatarView with Model");
        this.avatarView.setAvatarViewVM(this.viewModel.getAvatarViewVM());
        this.avatarActor.setActorVM(this.viewModel.getAvatarActorVM());
    }
}
