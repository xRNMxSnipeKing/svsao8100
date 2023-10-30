package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.xbox.avatar.model.AvatarEditorModel;
import com.microsoft.xbox.avatar.view.AvatarEditorOption;
import com.microsoft.xbox.avatar.view.AvatarEditorOptionAsset;
import com.microsoft.xbox.avatar.view.AvatarEditorSelectTitleCatalog;
import com.microsoft.xbox.toolkit.XboxApplication;

public class AvatarEditorSelectTypeAssetColors extends AvatarEditorSelectType {
    private final int categoryMask;
    private final AvatarEditorOptionAsset colorableAsset;

    public AvatarEditorSelectTypeAssetColors(int categoryMask, AvatarEditorOptionAsset option) {
        this.colorableAsset = option;
        this.categoryMask = categoryMask;
    }

    public AvatarEditorOptionAsset getColorableAsset() {
        return this.colorableAsset;
    }

    public AvatarEditorOption[] getSelectButtons() {
        return AvatarEditorModel.getInstance().getColorOptions(this.colorableAsset);
    }

    public String getTag() {
        return "AVATAREDIT_COLORABLE_ASSET";
    }

    public String getTitle() {
        return XboxApplication.Resources.getString(AvatarEditorSelectTitleCatalog.getTitle(this.categoryMask));
    }

    public String getDescription() {
        return null;
    }

    public int getCameraCategoryType() {
        return this.categoryMask;
    }
}
