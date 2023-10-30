package com.microsoft.xbox.smartglass.canvas;

import com.microsoft.xbox.avatar.model.AvatarEditorModel;

public enum CanvasComponents {
    Messaging(1),
    Accelerometer(2),
    Gyroscope(4),
    Input(8),
    Media(16),
    ServiceProxy(32),
    Information(64),
    Location(128),
    Camera(256),
    Haptic(AvatarEditorModel.AVATAREDIT_OPTION_GLASSES),
    Touch(1024),
    Logging(AvatarEditorModel.AVATAREDIT_OPTION_EARRINGS),
    Browser(AvatarEditorModel.AVATAREDIT_OPTION_RINGS),
    Developer(AvatarEditorModel.AVATAREDIT_OPTION_COLOR_FACIAL_HAIR);
    
    private int _value;

    private CanvasComponents(int value) {
        this._value = value;
    }

    public int getValue() {
        return this._value;
    }
}
