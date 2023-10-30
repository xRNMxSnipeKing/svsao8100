package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.avatar.view.AvatarEditorOption;
import com.microsoft.xbox.avatar.view.AvatarEditorOptionNewAvatar;
import com.microsoft.xbox.toolkit.XboxApplication;

public class AvatarEditorSelectTypeNewAvatar extends AvatarEditorSelectType {
    private static final AvatarEditorOption[] FEMALE_BUTTONS = new AvatarEditorOption[]{new AvatarEditorOptionNewAvatar(1), new AvatarEditorOptionNewAvatar(2), new AvatarEditorOptionNewAvatar(3), new AvatarEditorOptionNewAvatar(4), new AvatarEditorOptionNewAvatar(5), new AvatarEditorOptionNewAvatar(6), new AvatarEditorOptionNewAvatar(7), new AvatarEditorOptionNewAvatar(8), new AvatarEditorOptionNewAvatar(9), new AvatarEditorOptionNewAvatar(10), new AvatarEditorOptionNewAvatar(11), new AvatarEditorOptionNewAvatar(12)};
    private static final AvatarEditorOption[] MALE_BUTTONS = new AvatarEditorOption[]{new AvatarEditorOptionNewAvatar(13), new AvatarEditorOptionNewAvatar(14), new AvatarEditorOptionNewAvatar(15), new AvatarEditorOptionNewAvatar(16), new AvatarEditorOptionNewAvatar(17), new AvatarEditorOptionNewAvatar(18), new AvatarEditorOptionNewAvatar(19), new AvatarEditorOptionNewAvatar(20), new AvatarEditorOptionNewAvatar(21), new AvatarEditorOptionNewAvatar(22), new AvatarEditorOptionNewAvatar(23), new AvatarEditorOptionNewAvatar(24)};
    private AvatarEditorOption[] buttons = null;

    public AvatarEditorSelectTypeNewAvatar(boolean male) {
        this.buttons = male ? MALE_BUTTONS : FEMALE_BUTTONS;
    }

    public AvatarEditorOption[] getSelectButtons() {
        return this.buttons;
    }

    public String getTag() {
        return "AVATAREDIT_NEW_AVATAR";
    }

    public String getTitle() {
        return XboxApplication.Resources.getString(R.string.avatar).toUpperCase();
    }

    public String getDescription() {
        return XboxApplication.Resources.getString(R.string.avatar_editor_choose_new_avatar);
    }

    public int getCameraCategoryType() {
        return 1;
    }
}
