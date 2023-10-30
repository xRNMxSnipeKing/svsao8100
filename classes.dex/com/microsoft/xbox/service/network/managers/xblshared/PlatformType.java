package com.microsoft.xbox.service.network.managers.xblshared;

public enum PlatformType {
    PlatformType_AndroidPhone(2),
    PlatformType_AndroidSlate(3);
    
    private int value;

    private PlatformType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static PlatformType fromInt(int value) {
        switch (value) {
            case 2:
                return PlatformType_AndroidPhone;
            case 3:
                return PlatformType_AndroidSlate;
            default:
                return PlatformType_AndroidPhone;
        }
    }
}
