package com.microsoft.xbox.xle.app.adapter;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.toolkit.ui.XLEButton;
import com.microsoft.xbox.xle.viewmodel.AdapterBaseWithFloatingAvatarView;
import com.microsoft.xbox.xle.viewmodel.AvatarEditorBodyActivityViewModel;

public class AvatarEditorBodyAdapter extends AdapterBaseWithFloatingAvatarView {
    private XLEButton editorFat;
    private XLEButton editorNormal;
    private XLEButton editorSmall;
    private XLEButton editorTall;
    private XLEButton editorThin;
    private TextView screenTitle;
    private AvatarEditorBodyActivityViewModel viewModel;

    public AvatarEditorBodyAdapter(AvatarEditorBodyActivityViewModel vm) {
        this.screenBody = findViewById(R.id.avatar_body_body);
        this.viewModel = vm;
        this.avatarView.setHitBox(R.id.avatar_editor_hitbox);
        this.screenTitle = (TextView) findViewById(R.id.avatar_body_title);
        this.editorTall = (XLEButton) findViewById(R.id.avatar_tall);
        this.editorSmall = (XLEButton) findViewById(R.id.avatar_small);
        this.editorThin = (XLEButton) findViewById(R.id.avatar_thin);
        this.editorFat = (XLEButton) findViewById(R.id.avatar_fat);
        this.editorNormal = (XLEButton) findViewById(R.id.avatar_normal);
        this.editorTall.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                AvatarEditorBodyAdapter.this.viewModel.editorTall();
            }
        });
        this.editorSmall.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                AvatarEditorBodyAdapter.this.viewModel.editorSmall();
            }
        });
        this.editorThin.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                AvatarEditorBodyAdapter.this.viewModel.editorThin();
            }
        });
        this.editorFat.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                AvatarEditorBodyAdapter.this.viewModel.editorFat();
            }
        });
        this.editorNormal.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                AvatarEditorBodyAdapter.this.viewModel.editorNormal();
            }
        });
    }

    public void updateViewOverride() {
        setBlocking(this.viewModel.isBlockingBusy(), this.viewModel.getBlockingStatusText());
        this.avatarView.setAvatarViewVM(this.viewModel.getAvatarViewVM());
        this.avatarActor.setActorVM(this.viewModel.getAvatarActorVM());
        this.screenTitle.setText(XboxApplication.Resources.getString(R.string.avatar_category_bodysize).toUpperCase());
        this.editorTall.setEnabled(this.viewModel.editorTallEnabled());
        this.editorSmall.setEnabled(this.viewModel.editorSmallEnabled());
        this.editorThin.setEnabled(this.viewModel.editorThinEnabled());
        this.editorFat.setEnabled(this.viewModel.editorFatEnabled());
        this.editorNormal.setEnabled(true);
    }
}
