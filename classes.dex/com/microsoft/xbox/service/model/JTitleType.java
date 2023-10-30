package com.microsoft.xbox.service.model;

public enum JTitleType {
    System(0),
    Standard(1),
    Demo(2),
    Arcade(3),
    Application(5),
    Unknown(6);
    
    private final int value;

    private JTitleType(int v) {
        this.value = v;
    }

    public int getValue() {
        return this.value;
    }
}
