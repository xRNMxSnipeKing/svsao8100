package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.xbox.avatar.model.AvatarEditorModel;
import com.microsoft.xbox.service.model.UpdateData;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.xle.app.activity.AvatarEditorSelectActivity;
import com.microsoft.xbox.xle.app.adapter.AvatarEditorNewAvatarGenderAdapter;

public class AvatarEditorNewAvatarGenderActivityViewModel extends ViewModelBase {
    public AvatarEditorNewAvatarGenderActivityViewModel() {
        this.adapter = new AvatarEditorNewAvatarGenderAdapter(this);
    }

    public void onRehydrate() {
        this.adapter = new AvatarEditorNewAvatarGenderAdapter(this);
    }

    public void load(boolean forceRefresh) {
    }

    public boolean isBusy() {
        return false;
    }

    protected void onStartOverride() {
        AvatarEditorModel.getInstance().addObserver(this);
        AvatarEditorModel.getInstance().warpToOffscreen();
    }

    protected void onStopOverride() {
        AvatarEditorModel.getInstance().removeObserver(this);
    }

    public void updateOverride(AsyncResult<UpdateData> asyncResult) {
        this.adapter.updateView();
    }

    public void navigateToNewAvatarMale() {
        XLEGlobalData.getInstance().setSelectedMenu(new AvatarEditorSelectTypeNewAvatar(true));
        NavigateTo(AvatarEditorSelectActivity.class);
    }

    public void navigateToNewAvatarFemale() {
        XLEGlobalData.getInstance().setSelectedMenu(new AvatarEditorSelectTypeNewAvatar(false));
        NavigateTo(AvatarEditorSelectActivity.class);
    }
}
