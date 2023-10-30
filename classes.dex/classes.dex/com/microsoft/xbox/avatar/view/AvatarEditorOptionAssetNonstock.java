package com.microsoft.xbox.avatar.view;

import com.xbox.avatarrenderer.AvatarEditor.AvatarEditOption;

public class AvatarEditorOptionAssetNonstock extends AvatarEditorOptionAsset {
    public AvatarEditorOptionAssetNonstock(AvatarEditOption option, int closetCategory) {
        super(option, option.getImageUrl(128), closetCategory);
    }
}
