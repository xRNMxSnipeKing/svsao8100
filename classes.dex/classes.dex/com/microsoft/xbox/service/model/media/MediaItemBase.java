package com.microsoft.xbox.service.model.media;

import java.util.HashMap;

public class MediaItemBase {
    public String description;
    public String imageUrl;
    public String mediaId;
    public String title;

    public enum MediaType {
        UNDEFINED(0),
        MOVIE(1),
        MUSIC_ALBUM(2),
        MUSIC_TRACK(3),
        TV_EPISODE(4),
        MUSIC_MUSIC_VIDEO(5),
        MUSIC_ARTIST(6),
        MUSIC_PLAYLIST(7),
        TV_SERIRES(8),
        HUB(9),
        MOVIE_TRAILER(10);
        
        private static final HashMap<Integer, MediaType> intToTypeMap = null;
        private final int value;

        static {
            intToTypeMap = new HashMap();
            for (MediaType type : values()) {
                intToTypeMap.put(Integer.valueOf(type.value), type);
            }
        }

        private MediaType(int number) {
            this.value = number;
        }

        public int getValue() {
            return this.value;
        }

        public static MediaType getMediaTypeFromInt(int value) {
            MediaType mediaType = (MediaType) intToTypeMap.get(Integer.valueOf(value));
            if (mediaType == null) {
                return UNDEFINED;
            }
            return mediaType;
        }
    }
}
