package com.microsoft.xbox.service.model.eds;

import java.util.HashMap;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;

public enum JMediaType {
    Undefined(0),
    Movie(1),
    Music_Album(2),
    Music_Track(3),
    Tv_Episode(4),
    Music_Music_Video(5),
    Music_Artist(6),
    Music_Playlist(7),
    Tv_Series(8),
    Hub(9),
    MovieTrailer(10),
    Tv_Season(11),
    Game(12),
    App(13),
    GameContent(14);
    
    private static HashMap<Integer, JMediaType> hash;
    private final int value;

    static {
        hash = new HashMap();
        for (JMediaType type : values()) {
            hash.put(Integer.valueOf(type.getValue()), type);
        }
    }

    private JMediaType(int v) {
        this.value = v;
    }

    @JsonValue
    public int getValue() {
        return this.value;
    }

    @JsonCreator
    public static JMediaType forValue(int value) {
        return (JMediaType) hash.get(Integer.valueOf(value));
    }
}
