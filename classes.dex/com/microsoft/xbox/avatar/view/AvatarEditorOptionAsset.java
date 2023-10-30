package com.microsoft.xbox.avatar.view;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.avatar.model.AvatarEditorModel;
import com.microsoft.xbox.avatar.model.XLEManifestUtils;
import com.microsoft.xbox.avatar.view.AvatarAnimationCatalog.AvatarClosetSpinAnimationType;
import com.microsoft.xbox.avatar.view.AvatarEditorOption.AvatarEditorOptionDisplayType;
import com.xbox.avatarrenderer.AvatarEditor.AvatarEditOption;
import com.xbox.avatarrenderer.AvatarEditor.AvatarEditOptions;

public abstract class AvatarEditorOptionAsset extends AvatarEditorOption {
    private int closetCategory;
    private int[] colorsOut3 = new int[3];
    protected AvatarEditOption data = null;

    public AvatarEditorOptionAsset(AvatarEditOption option, String url, int closetCategory) {
        this.data = option;
        this.data.getColor(this.colorsOut3);
        this.closetCategory = closetCategory;
        if (isRemove()) {
            setDisplayTypeResource(R.drawable.editor_remove, AvatarEditorOptionDisplayType.RESOURCE);
        } else {
            setDisplayTypeURL(url);
        }
    }

    public String getAssetGuid() {
        return this.data.getAssetGuid();
    }

    public AvatarEditOptions getColorOptions() {
        return this.data.getColorOptions();
    }

    public AvatarEditOption getOption() {
        return this.data;
    }

    public int getOwnershipResourceId() {
        if (this.data.getIsAward().booleanValue()) {
            return R.drawable.editor_icon_awarded;
        }
        if (this.data.getIsMarketPlaceAsset().booleanValue()) {
            return R.drawable.editor_icon_purchased;
        }
        return R.drawable.empty;
    }

    public boolean isSelected() {
        return this.data.getIsSelected().booleanValue() && !isRemove();
    }

    public int getColorableStyleAssetResourceId() {
        if (isColorableStyleAsset()) {
            return R.drawable.editor_colors;
        }
        return R.drawable.empty;
    }

    public boolean isColorable() {
        return this.data.getIsColorableAsset().booleanValue() && !this.data.getIsColor().booleanValue();
    }

    public String getAssetTitle() {
        if (isRemove()) {
            return AvatarEditorSelectTitleCatalog.getRemoveTitle(this.closetCategory);
        }
        return this.data.getTitle();
    }

    public AvatarClosetSpinAnimationType getClosetSpinAnimationType() {
        int animationCategory = this.closetCategory;
        if (animationCategory == AvatarEditorModel.AVATAREDIT_OPTION_AWARDS) {
            animationCategory = XLEManifestUtils.assetGuidToAvatarEditorModelCategory(getAssetGuid());
        }
        if (this.data.getIsColor().booleanValue()) {
            return AvatarClosetSpinAnimationType.Snap;
        }
        return AvatarAnimationCatalog.getAvatarClosetSpinAnimationTypeForCategory(animationCategory);
    }

    private boolean isColorableStyleAsset() {
        boolean isStyleAsset;
        if ((this.closetCategory & AvatarEditorModel.AVATAREDIT_OPTION_MASK_STYLE) != 0) {
            isStyleAsset = true;
        } else {
            isStyleAsset = false;
        }
        return this.data.getIsColorableAsset().booleanValue() && isStyleAsset;
    }

    private boolean isRemove() {
        return this.data.getAssetGuid().equals(AvatarEditorModel.REMOVE_GUID);
    }
}
