package com.microsoft.xbox.service.model.smartglass;

import com.microsoft.xbox.toolkit.XLELog;
import java.util.HashMap;

public enum XBLKeyboardType {
    KeyboardTypeSingleLine(1),
    KeyboardTypeMultiLine(2);
    
    private static HashMap<Integer, XBLKeyboardType> hash;
    private final int value;

    static {
        hash = new HashMap();
        for (XBLKeyboardType type : values()) {
            hash.put(Integer.valueOf(type.getValue()), type);
        }
    }

    private XBLKeyboardType(int v) {
        this.value = v;
    }

    public int getValue() {
        return this.value;
    }

    public static XBLKeyboardType forValue(int value) {
        XLELog.Error("XBLKEYBOARDTYPE", "XBLKEYBOARD TYPE FOR VALUE" + value);
        if (hash.containsKey(Integer.valueOf(value))) {
            return (XBLKeyboardType) hash.get(Integer.valueOf(value));
        }
        return KeyboardTypeSingleLine;
    }
}
