package com.microsoft.xbox.xle.app.adapter;

import android.view.View;
import android.view.View.OnClickListener;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.xle.ui.XLEAvatarEditorButton;
import com.microsoft.xbox.xle.viewmodel.AdapterBaseWithFloatingAvatarView;
import com.microsoft.xbox.xle.viewmodel.AvatarEditorMainActivityViewModel;

public class AvatarEditorMainAdapter extends AdapterBaseWithFloatingAvatarView {
    private XLEAvatarEditorButton features;
    private XLEAvatarEditorButton style;
    private AvatarEditorMainActivityViewModel viewModel;

    public AvatarEditorMainAdapter(AvatarEditorMainActivityViewModel vm) {
        this.screenBody = findViewById(R.id.avatar_main_body);
        this.content = findViewById(R.id.avatar_editor_main_content);
        this.viewModel = vm;
        this.style = (XLEAvatarEditorButton) findViewById(R.id.avatar_editor_style);
        this.features = (XLEAvatarEditorButton) findViewById(R.id.avatar_editor_features);
        this.avatarView.setHitBox(R.id.avatar_editor_hitbox);
        this.style.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                AvatarEditorMainAdapter.this.viewModel.navigateToStyle();
            }
        });
        this.features.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                AvatarEditorMainAdapter.this.viewModel.navigateToFeatures();
            }
        });
    }

    protected void onAppBarUpdated() {
        setAppBarButtonClickListener(R.id.appbar_save, new OnClickListener() {
            public void onClick(View v) {
                AvatarEditorMainAdapter.this.viewModel.saveAvatar();
            }
        });
        setAppBarButtonClickListener(R.id.appbar_cancel, new OnClickListener() {
            public void onClick(View v) {
                AvatarEditorMainAdapter.this.viewModel.cancelAvatar();
            }
        });
        setAppBarButtonClickListener(R.id.editor_new_avatar, new OnClickListener() {
            public void onClick(View v) {
                AvatarEditorMainAdapter.this.viewModel.promptForNewAvatar();
            }
        });
    }

    public void updateViewOverride() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        this.avatarView.setAvatarViewVM(this.viewModel.getAvatarViewVM());
        this.avatarActor.setActorVM(this.viewModel.getAvatarActorVM());
        this.style.setGender(this.viewModel.isMale());
        this.features.setGender(this.viewModel.isMale());
        setAppBarButtonEnabled(R.id.appbar_save, this.viewModel.isSaveButtonEnabled());
        setBlocking(this.viewModel.isBlockingBusy(), this.viewModel.getBlockingStatusText());
    }
}
