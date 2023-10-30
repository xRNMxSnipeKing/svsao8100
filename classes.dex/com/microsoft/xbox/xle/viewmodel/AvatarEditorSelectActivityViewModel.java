package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.avatar.model.AvatarEditorModel;
import com.microsoft.xbox.avatar.model.AvatarViewActorVM;
import com.microsoft.xbox.avatar.model.AvatarViewVM;
import com.microsoft.xbox.avatar.view.AvatarEditorOption;
import com.microsoft.xbox.avatar.view.AvatarEditorOptionCategory;
import com.microsoft.xbox.service.model.UpdateData;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.xle.app.activity.AvatarEditorBodyActivity;
import com.microsoft.xbox.xle.app.activity.AvatarEditorPreviewActivity;
import com.microsoft.xbox.xle.app.activity.AvatarEditorSelectActivity;
import com.microsoft.xbox.xle.app.adapter.AvatarEditorSelectAdapter;

public class AvatarEditorSelectActivityViewModel extends AvatarEditorViewModelBase {
    private AvatarEditorOption[] buttons;
    private String description;
    private String title;
    private AvatarEditorSelectType type;

    public AvatarEditorSelectActivityViewModel() {
        this.buttons = null;
        this.title = null;
        this.description = null;
        this.type = XLEGlobalData.getInstance().getSelectedMenu();
        this.adapter = new AvatarEditorSelectAdapter(this);
    }

    public void onRehydrate() {
        this.adapter = new AvatarEditorSelectAdapter(this);
    }

    public void load(boolean forceRefresh) {
    }

    public boolean isBusy() {
        return false;
    }

    public String getTag() {
        XLEAssert.assertTrue(this.type != null);
        return this.type.getTag();
    }

    protected void onStartOverride() {
        AvatarEditorModel.getInstance().addObserver(this);
        AvatarEditorModel.getInstance().warpToOffscreen();
        setCurrentSelectType();
        XLEAssert.assertTrue(this.type != null);
        this.buttons = this.type.getSelectButtons();
        this.title = this.type.getTitle();
        this.description = this.type.getDescription();
        if (XLEGlobalData.getInstance().getAvatarEditorAssetApplyError()) {
            showError(R.string.toast_avatar_editor_apply_error);
            XLEGlobalData.getInstance().setAvatarEditorAssetApplyError(false);
        }
    }

    protected void onStopOverride() {
        AvatarEditorModel.getInstance().removeObserver(this);
    }

    public void updateOverride(AsyncResult<UpdateData> asyncResult) {
        this.adapter.updateView();
    }

    public AvatarEditorOption[] getOptions() {
        return this.buttons;
    }

    public int getFirstSelectedOptionIndex() {
        if (this.buttons != null) {
            for (int i = 0; i < this.buttons.length; i++) {
                if (this.buttons[i].isSelected()) {
                    return i;
                }
            }
        }
        return 0;
    }

    public String getScreenTitle() {
        return this.title;
    }

    public String getScreenDescription() {
        return this.description;
    }

    public void navigateToOption(AvatarEditorOption option) {
        XLEGlobalData.getInstance().setSelectedAsset(option);
        XLEGlobalData.getInstance().setAvatarEditorPreviewNeedsApply(true);
        NavigateTo(AvatarEditorPreviewActivity.class);
    }

    public void navigateToOptionCategory(AvatarEditorOptionCategory option) {
        XLEGlobalData.getInstance().setSelectedMenu(new AvatarEditorSelectTypeCategory(option.getCategoryMask()));
        if (option.getCategoryMask() == 1) {
            NavigateTo(AvatarEditorBodyActivity.class);
        } else {
            NavigateTo(AvatarEditorSelectActivity.class);
        }
    }

    public AvatarViewVM getAvatarViewVM() {
        return AvatarEditorModel.getInstance().getAvatarViewVM();
    }

    public AvatarViewActorVM getAvatarActorVM() {
        return AvatarEditorModel.getInstance().getAvatarActorVM();
    }

    private void setCurrentSelectType() {
        XLEAssert.assertTrue(this.type != null);
        XLEGlobalData.getInstance().setSelectedMenu(this.type);
    }
}
