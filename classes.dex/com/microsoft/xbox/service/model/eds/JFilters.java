package com.microsoft.xbox.service.model.eds;

import java.util.HashMap;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;

public enum JFilters {
    All(0),
    App(1),
    Game(2),
    Music(3),
    Tv(4),
    Movie(5);
    
    private static HashMap<Integer, JFilters> hash;
    private final int value;

    static {
        hash = new HashMap();
        for (JFilters type : values()) {
            hash.put(Integer.valueOf(type.getValue()), type);
        }
    }

    private JFilters(int v) {
        this.value = v;
    }

    @JsonValue
    public int getValue() {
        return this.value;
    }

    @JsonCreator
    public static JFilters forValue(int value) {
        return (JFilters) hash.get(Integer.valueOf(value));
    }
}
