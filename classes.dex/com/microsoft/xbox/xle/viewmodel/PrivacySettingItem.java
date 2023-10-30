package com.microsoft.xbox.xle.viewmodel;

public final class PrivacySettingItem {
    private final String description;
    private final String title;
    private final int value;

    public PrivacySettingItem(String title, String description, int value) {
        this.title = title;
        this.description = description;
        this.value = value;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public int getValue() {
        return this.value;
    }
}
