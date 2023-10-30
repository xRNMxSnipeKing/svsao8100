package com.microsoft.xbox.service.model.edsv2;

import java.util.HashMap;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;

public enum EDSV2SearchFilterType {
    SEARCHFILTERTYPE_ALL(0),
    SEARCHFILTERTYPE_APP(1),
    SEARCHFILTERTYPE_XBOXGAME(2),
    SEARCHFILTERTYPE_MUSIC(3),
    SEARCHFILTERTYPE_TV(4),
    SEARCHFILTERTYPE_MOVIE(5),
    SEARCHFILTERTYPE_MUSICARTIST(6),
    SEARCHFILTERTYPE_WEBVIDEO(7);
    
    private static HashMap<Integer, EDSV2SearchFilterType> hash;
    private final int value;

    static {
        hash = new HashMap();
        for (EDSV2SearchFilterType type : values()) {
            hash.put(Integer.valueOf(type.getValue()), type);
        }
    }

    private EDSV2SearchFilterType(int v) {
        this.value = v;
    }

    @JsonValue
    public int getValue() {
        return this.value;
    }

    @JsonCreator
    public static EDSV2SearchFilterType forValue(int value) {
        return (EDSV2SearchFilterType) hash.get(Integer.valueOf(value));
    }
}
