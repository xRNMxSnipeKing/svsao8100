package com.microsoft.xbox.avatar.view;

import com.xbox.avatarrenderer.AvatarEditor.AvatarEditOption;

public class AvatarEditorOptionAssetStock extends AvatarEditorOptionAsset {
    public AvatarEditorOptionAssetStock(AvatarEditOption option, int closetCategory) {
        super(option, "avatar/thumbnail/" + option.getAssetGuid() + ".png", closetCategory);
    }
}
