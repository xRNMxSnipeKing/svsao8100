package com.microsoft.xbox.service.model;

public enum JMediaState {
    MediaState_NoMedia(-1),
    MediaState_Invalid(0),
    MediaState_Stopped(1),
    MediaState_Starting(2),
    MediaState_Playing(3),
    MediaState_Paused(4),
    MediaState_Buffering(5);
    
    private final int value;

    private JMediaState(int v) {
        this.value = v;
    }

    public static JMediaState fromOrdinal(int v) {
        for (JMediaState state : values()) {
            if (state.value == v) {
                return state;
            }
        }
        return MediaState_Invalid;
    }

    public int getValue() {
        return this.value;
    }
}
