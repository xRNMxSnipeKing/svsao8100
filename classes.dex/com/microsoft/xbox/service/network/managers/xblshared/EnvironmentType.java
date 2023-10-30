package com.microsoft.xbox.service.network.managers.xblshared;

public enum EnvironmentType {
    Vint(0),
    PartnerNet(1),
    Production(2),
    STUB(3);
    
    private int value;

    private EnvironmentType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public static EnvironmentType fromInt(int value) {
        switch (value) {
            case 0:
                return Vint;
            case 1:
                return PartnerNet;
            case 2:
                return Production;
            case 3:
                return STUB;
            default:
                return Production;
        }
    }
}
