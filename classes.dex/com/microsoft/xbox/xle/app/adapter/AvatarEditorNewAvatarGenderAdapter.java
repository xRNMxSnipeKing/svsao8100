package com.microsoft.xbox.xle.app.adapter;

import android.view.View;
import android.view.View.OnClickListener;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.ui.XLEImageView;
import com.microsoft.xbox.xle.viewmodel.AdapterBaseWithFloatingAvatarView;
import com.microsoft.xbox.xle.viewmodel.AvatarEditorNewAvatarGenderActivityViewModel;

public class AvatarEditorNewAvatarGenderAdapter extends AdapterBaseWithFloatingAvatarView {
    private XLEImageView genderFemale;
    private XLEImageView genderMale;
    private AvatarEditorNewAvatarGenderActivityViewModel viewModel;

    public AvatarEditorNewAvatarGenderAdapter(AvatarEditorNewAvatarGenderActivityViewModel vm) {
        this.screenBody = findViewById(R.id.avatar_editor_new_avatar_gender_body);
        this.viewModel = vm;
        this.genderMale = (XLEImageView) findViewById(R.id.avatar_editor_new_avatar_male);
        this.genderFemale = (XLEImageView) findViewById(R.id.avatar_editor_new_avatar_female);
        this.genderMale.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                AvatarEditorNewAvatarGenderAdapter.this.viewModel.navigateToNewAvatarMale();
            }
        });
        this.genderFemale.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                AvatarEditorNewAvatarGenderAdapter.this.viewModel.navigateToNewAvatarFemale();
            }
        });
    }

    public void updateViewOverride() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
    }
}
