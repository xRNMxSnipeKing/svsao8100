package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.xbox.avatar.view.AvatarEditorOption;

public abstract class AvatarEditorSelectType {
    public abstract int getCameraCategoryType();

    public abstract String getDescription();

    public abstract AvatarEditorOption[] getSelectButtons();

    public abstract String getTag();

    public abstract String getTitle();
}
