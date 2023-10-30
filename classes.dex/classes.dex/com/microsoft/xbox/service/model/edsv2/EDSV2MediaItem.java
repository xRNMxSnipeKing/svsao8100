package com.microsoft.xbox.service.model.edsv2;

import com.microsoft.xbox.service.model.eds.JMediaType;
import com.microsoft.xbox.service.model.eds.JNowPlayingItem;
import com.microsoft.xbox.service.model.eds.JNowPlayingItem.NowPlayingItemType;
import com.microsoft.xbox.service.model.serialization.UTCDateConverter.UTCDateConverterJSONDeserializer;
import com.microsoft.xbox.toolkit.JavaUtil;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonSubTypes.Type;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeInfo.As;
import org.codehaus.jackson.annotate.JsonTypeInfo.Id;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

@JsonSubTypes({@Type(name = "EDSV2MovieMediaItem", value = EDSV2MovieMediaItem.class), @Type(name = "EDSV2GameMediaItem", value = EDSV2GameMediaItem.class), @Type(name = "EDSV2GameContentMediaItem", value = EDSV2GameContentMediaItem.class), @Type(name = "EDSV2AppMediaItem", value = EDSV2AppMediaItem.class), @Type(name = "EDSV2TVEpisodeMediaItem", value = EDSV2TVEpisodeMediaItem.class), @Type(name = "EDSV2TVSeasonMediaItem", value = EDSV2TVSeasonMediaItem.class), @Type(name = "EDSV2TVSeriesMediaItem", value = EDSV2TVSeriesMediaItem.class), @Type(name = "EDSV2MusicAlbumMediaItem", value = EDSV2MusicAlbumMediaItem.class), @Type(name = "EDSV2MusicArtistMediaItem", value = EDSV2MusicArtistMediaItem.class), @Type(name = "EDSV2MusicTrackMediaItem", value = EDSV2MusicTrackMediaItem.class), @Type(name = "EDSV2SearchResultItem", value = EDSV2SearchResultItem.class), @Type(name = "EDSV2MusicTrackMediaItemWithAlbum", value = EDSV2MusicTrackMediaItemWithAlbum.class), @Type(name = "EDSV2MusicVideoMediaItem", value = EDSV2MusicVideoMediaItem.class), @Type(name = "EDSV2TVShowMediaItem", value = EDSV2TVShowMediaItem.class)})
@JsonTypeInfo(include = As.PROPERTY, property = "EDSV2DataType", use = Id.NAME)
public class EDSV2MediaItem {
    private URI backgroundImageUrl;
    private String canonicalId;
    private String description;
    private String duration;
    private boolean hasSmartGlassActivity;
    private URI imageUrl;
    private ArrayList<EDSV2Image> images;
    private String impressionGuid;
    private boolean isProgrammingOverride = false;
    private int mediaType;
    private String parentCanonicalId;
    private int parentMediaType;
    private String parentName;
    private String parentalRating;
    private String partnerMediaId;
    private ArrayList<EDSV2Provider> providers;
    private Date releaseDate;
    private String title;
    private long titleId;

    public EDSV2MediaItem(EDSV2MediaItem source) {
        setCanonicalId(source.getCanonicalId());
        setPartnerMediaId(source.getPartnerMediaId());
        setTitleId(source.getTitleId());
        setMediaType(source.getMediaType());
        setParentCanonicalId(source.getParentCanonicalId());
        setParentName(source.getParentName());
        setParentMediaType(source.getParentMediaType());
        setTitle(source.getTitle());
        setDescription(source.getDescription());
        setReleaseDate(source.getReleaseDate());
        setImageUrl(source.getImageUrl());
        setDuration(source.getDuration());
        setParentalRating(source.getParentalRating());
        setBackgroundImageUrl(source.getBackgroundImageUrl());
        setImages(source.getImages());
        setProviders(source.getProviders());
        setImpressionGuid(source.getImpressionGuid());
        setHasSmartGlassActivity(source.getHasSmartGlassActivity());
    }

    public static EDSV2MediaItem getEDSV2MediaItemFromJNowPlayingItem(JNowPlayingItem nowPlayingItem) {
        EDSV2MediaItem mediaItem = new EDSV2MediaItem();
        mediaItem.setCanonicalId(nowPlayingItem.getIdentifier());
        mediaItem.setTitleId(nowPlayingItem.getTitleId());
        mediaItem.setPartnerMediaId(nowPlayingItem.getMediaId());
        mediaItem.setImageUrl(nowPlayingItem.getImageUrl());
        if (nowPlayingItem.getDetailMediaType() != JMediaType.Undefined) {
            mediaItem.setMediaType(getEDSV2MediaType(nowPlayingItem.getDetailMediaType()));
        } else if (nowPlayingItem.getNowPlayingItemType() == NowPlayingItemType.NowPlaying_Game) {
            mediaItem.setMediaType(1);
        } else {
            mediaItem.setMediaType(61);
        }
        return mediaItem;
    }

    private static int getEDSV2MediaType(JMediaType type) {
        switch (type) {
            case Movie:
                return EDSV2MediaType.MEDIATYPE_MOVIE;
            case Music_Album:
                return EDSV2MediaType.MEDIATYPE_ALBUM;
            case Music_Track:
                return EDSV2MediaType.MEDIATYPE_TRACK;
            case Tv_Episode:
                return EDSV2MediaType.MEDIATYPE_TVEPISODE;
            case Music_Music_Video:
                return EDSV2MediaType.MEDIATYPE_MUSICVIDEO;
            case Music_Artist:
                return EDSV2MediaType.MEDIATYPE_MUSICARTIST;
            case Tv_Series:
                return EDSV2MediaType.MEDIATYPE_TVSERIES;
            case Tv_Season:
                return EDSV2MediaType.MEDIATYPE_TVSEASON;
            case Game:
                return 1;
            case GameContent:
                return 18;
            case App:
                return 61;
            default:
                return 0;
        }
    }

    public String getCanonicalId() {
        return this.canonicalId;
    }

    public void setCanonicalId(String id) {
        this.canonicalId = id;
    }

    public String getPartnerMediaId() {
        return this.partnerMediaId;
    }

    public void setPartnerMediaId(String id) {
        this.partnerMediaId = id;
    }

    public long getTitleId() {
        return this.titleId;
    }

    public void setTitleId(long id) {
        this.titleId = id;
    }

    public int getMediaType() {
        return this.mediaType;
    }

    public void setMediaType(int type) {
        this.mediaType = type;
    }

    public String getParentCanonicalId() {
        return this.parentCanonicalId;
    }

    public void setParentCanonicalId(String id) {
        this.parentCanonicalId = id;
    }

    public String getParentName() {
        return this.parentName;
    }

    public void setParentName(String name) {
        this.parentName = name;
    }

    public int getParentMediaType() {
        return this.parentMediaType;
    }

    public void setParentMediaType(int type) {
        this.parentMediaType = type;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDisplayTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String desc) {
        this.description = desc;
    }

    public Date getReleaseDate() {
        return this.releaseDate;
    }

    @JsonDeserialize(using = UTCDateConverterJSONDeserializer.class)
    @JsonProperty("releaseDate")
    public void setReleaseDate(Date date) {
        this.releaseDate = date;
    }

    public URI getImageUrl() {
        return this.imageUrl;
    }

    public void setImageUrl(URI url) {
        this.imageUrl = url;
    }

    public String getDuration() {
        return this.duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getParentalRating() {
        return this.parentalRating;
    }

    public void setParentalRating(String rating) {
        this.parentalRating = rating;
    }

    public URI getBackgroundImageUrl() {
        return this.backgroundImageUrl;
    }

    public void setBackgroundImageUrl(URI url) {
        this.backgroundImageUrl = url;
    }

    public ArrayList<EDSV2Image> getImages() {
        return this.images;
    }

    public void setImages(ArrayList<EDSV2Image> images) {
        this.images = images;
    }

    public ArrayList<EDSV2Provider> getProviders() {
        return this.providers;
    }

    public void setProviders(ArrayList<EDSV2Provider> providers) {
        this.providers = providers;
    }

    public String getImpressionGuid() {
        return this.impressionGuid;
    }

    public void setImpressionGuid(String guid) {
        this.impressionGuid = guid;
    }

    public boolean getHasSmartGlassActivity() {
        return this.hasSmartGlassActivity;
    }

    public void setHasSmartGlassActivity(boolean hasActivities) {
        if (this.mediaType != EDSV2MediaType.MEDIATYPE_ALBUM && this.mediaType != EDSV2MediaType.MEDIATYPE_TRACK && this.mediaType != EDSV2MediaType.MEDIATYPE_MUSICVIDEO) {
            this.hasSmartGlassActivity = hasActivities;
        }
    }

    public boolean getIsProgrammingOverride() {
        return this.isProgrammingOverride;
    }

    public void setIsProgrammingOverride(boolean isProgrammingOverride) {
        this.isProgrammingOverride = isProgrammingOverride;
    }

    public boolean equals(Object o) {
        if (o instanceof EDSV2MediaItem) {
            return JavaUtil.stringsEqualNonNullCaseInsensitive(getCanonicalId(), ((EDSV2MediaItem) o).getCanonicalId());
        }
        return false;
    }
}
