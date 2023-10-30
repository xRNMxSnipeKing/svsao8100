package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.xbox.service.model.serialization.Friend;

public final class FriendSelectorItem extends FriendItem {
    private boolean selected = false;

    public FriendSelectorItem(Friend friend) {
        super(friend);
    }

    public void toggleSelection() {
        this.selected = !this.selected;
    }

    public boolean getIsSelected() {
        return this.selected;
    }

    public void setSelected(boolean value) {
        this.selected = value;
    }
}
