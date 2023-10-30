package com.microsoft.xbox.avatar.view;

import com.microsoft.xbox.avatar.view.AvatarAnimationCatalog.AvatarClosetSpinAnimationType;
import com.microsoft.xbox.avatar.view.AvatarEditorOption.AvatarEditorOptionDisplayType;
import com.xbox.avatarrenderer.AvatarEditor.AvatarEditOption;

public class AvatarEditorOptionColor extends AvatarEditorOption {
    private static final int ARGBLEN = 8;
    private static final int HEXBASE = 16;
    private AvatarEditOption option = null;

    public AvatarEditorOptionColor(AvatarEditOption option) {
        super(extractColor(option), AvatarEditorOptionDisplayType.COLOR);
        this.option = option;
    }

    public static int extractColor(AvatarEditOption option) {
        int[] colors = new int[3];
        option.getColor(colors);
        return colors[0];
    }

    public AvatarEditOption getOption() {
        return this.option;
    }

    public boolean isSelected() {
        return this.option.getIsSelected().booleanValue();
    }

    public AvatarClosetSpinAnimationType getClosetSpinAnimationType() {
        return AvatarClosetSpinAnimationType.Snap;
    }
}
