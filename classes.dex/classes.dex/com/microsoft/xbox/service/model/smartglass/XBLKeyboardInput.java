package com.microsoft.xbox.service.model.smartglass;

import java.util.HashMap;

public enum XBLKeyboardInput {
    KeyboardInputTypeDefault(1),
    KeyboardInputTypeText(2),
    KeyboardInputTypePassword(3),
    KeyboardInputTypeEmail(4),
    KeyboardInputTypePhone(5),
    KeyboardInputTypeNumber(6);
    
    private static HashMap<Integer, XBLKeyboardInput> hash;
    private final int value;

    static {
        hash = new HashMap();
        for (XBLKeyboardInput type : values()) {
            hash.put(Integer.valueOf(type.getValue()), type);
        }
    }

    private XBLKeyboardInput(int v) {
        this.value = v;
    }

    public int getValue() {
        return this.value;
    }

    public static XBLKeyboardInput forValue(int value) {
        if (hash.containsKey(Integer.valueOf(value))) {
            return (XBLKeyboardInput) hash.get(Integer.valueOf(value));
        }
        return KeyboardInputTypeDefault;
    }
}
