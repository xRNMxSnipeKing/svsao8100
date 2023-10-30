package com.microsoft.xbox.xle.app.adapter;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.ui.XLEButton;
import com.microsoft.xbox.xle.viewmodel.AdapterBaseWithFloatingAvatarView;
import com.microsoft.xbox.xle.viewmodel.AvatarEditorPreviewActivityViewModel;

public class AvatarEditorPreviewAdapter extends AdapterBaseWithFloatingAvatarView {
    private XLEButton editorColor;
    private TextView screenDescription;
    private TextView screenTitle;
    private AvatarEditorPreviewActivityViewModel viewModel;

    public AvatarEditorPreviewAdapter(AvatarEditorPreviewActivityViewModel vm) {
        this.screenBody = findViewById(R.id.avatar_preview_body);
        this.viewModel = vm;
        this.avatarView.setHitBox(R.id.avatar_editor_hitbox);
        this.screenTitle = (TextView) findViewById(R.id.avatar_preview_title);
        this.screenDescription = (TextView) findViewById(R.id.avatar_preview_description);
        this.editorColor = (XLEButton) findViewById(R.id.avatar_color);
        this.editorColor.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                AvatarEditorPreviewAdapter.this.viewModel.navigateToSelectColor();
            }
        });
    }

    protected void onAppBarUpdated() {
        setAppBarButtonClickListener(R.id.avatar_save, new OnClickListener() {
            public void onClick(View v) {
                AvatarEditorPreviewAdapter.this.viewModel.editorCommit();
            }
        });
        setAppBarButtonClickListener(R.id.avatar_revert, new OnClickListener() {
            public void onClick(View v) {
                AvatarEditorPreviewAdapter.this.viewModel.editorRevert();
            }
        });
    }

    public void updateViewOverride() {
        int i = 0;
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        this.avatarView.setAvatarViewVM(this.viewModel.getAvatarViewVM());
        this.avatarActor.setActorVM(this.viewModel.getAvatarActorVM());
        this.screenTitle.setText(this.viewModel.getScreenTitle());
        this.screenDescription.setText(this.viewModel.getScreenAssetTitle());
        setBlocking(this.viewModel.isBlockingBusy(), this.viewModel.getBlockingStatusText());
        XLEButton xLEButton = this.editorColor;
        if (!this.viewModel.getColorableAsset()) {
            i = 8;
        }
        xLEButton.setVisibility(i);
    }
}
