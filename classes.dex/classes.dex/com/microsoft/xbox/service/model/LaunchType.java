package com.microsoft.xbox.service.model;

public enum LaunchType {
    GameLaunchType(0),
    GameContentLaunchType(1),
    AppLaunchType(2),
    UnknownLaunchType(3);
    
    private int value;

    private LaunchType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
