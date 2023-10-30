package com.microsoft.xbox.service.model.eds;

import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.JavaUtil.HexLongJSONDeserializer;
import com.microsoft.xbox.toolkit.UrlUtil;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

public class JNowPlayingItem extends EDSSearchResponseBase {
    private String detailMediaId;
    private JMediaType detailMediaType;
    private String mediaId;
    private URI mediaImageUrl;
    private String mediaName;
    private NowPlayingItemType nowPlayingItemType;
    private long titleId;
    private URI titleImageUrl;
    private String titleName;
    private URI titleTileImageUrl;

    public enum NowPlayingItemType {
        NowPlaying_Unknown(0),
        NowPlaying_App(1),
        NowPlaying_Game(2),
        NowPlaying_Video(3),
        NowPlaying_TvEpisode(4),
        NowPlaying_Music_Album(5),
        NowPlaying_Music_Track(6);
        
        private static HashMap<Integer, NowPlayingItemType> hash;
        private final int value;

        static {
            hash = new HashMap();
            for (NowPlayingItemType type : values()) {
                hash.put(Integer.valueOf(type.getValue()), type);
            }
        }

        private NowPlayingItemType(int v) {
            this.value = v;
        }

        @JsonValue
        public int getValue() {
            return this.value;
        }

        @JsonCreator
        public static NowPlayingItemType forValue(int value) {
            return (NowPlayingItemType) hash.get(Integer.valueOf(value));
        }
    }

    public long getTitleId() {
        return this.titleId;
    }

    @JsonDeserialize(using = HexLongJSONDeserializer.class)
    public void setTitleId(long value) {
        this.titleId = value;
    }

    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }

    public void setTitleImageUrl(URI titleImageUrl) {
        this.titleImageUrl = titleImageUrl;
    }

    public void setTitleTileImageUrl(URI titleTileImageUrl) {
        this.titleTileImageUrl = titleTileImageUrl;
    }

    public String getMediaId() {
        return JavaUtil.urlDecode(this.mediaId);
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public void setMediaName(String mediaName) {
        this.mediaName = mediaName;
    }

    public void setMediaImageUrl(String url) {
        this.mediaImageUrl = UrlUtil.getUri(url);
    }

    public void setDetailMediaId(String detailMediaId) {
        this.detailMediaId = detailMediaId;
    }

    public NowPlayingItemType getNowPlayingItemType() {
        return this.nowPlayingItemType;
    }

    public void setNowPlayingItemType(NowPlayingItemType type) {
        this.nowPlayingItemType = type;
    }

    public JMediaType getDetailMediaType() {
        return this.detailMediaType;
    }

    public void setDetailMediaType(JMediaType type) {
        this.detailMediaType = type;
    }

    public String getIdentifier() {
        return this.detailMediaId;
    }

    public int getUserRatingCount() {
        return 0;
    }

    public float getAverageUserRating() {
        return 0.0f;
    }

    public URI getImageUrl() {
        if (this.mediaImageUrl != null) {
            return this.mediaImageUrl;
        }
        return this.titleImageUrl;
    }

    public String getName() {
        if (JavaUtil.isNullOrEmpty(this.mediaName)) {
            return this.titleName;
        }
        return this.mediaName;
    }

    public String getProductionCompany() {
        return null;
    }

    public Date getReleaseDate() {
        return null;
    }

    public String getDuration() {
        return null;
    }

    public boolean isSmartGlassEnabled() {
        return true;
    }

    public JFilters getFilter() {
        return null;
    }
}
