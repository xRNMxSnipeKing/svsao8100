package com.microsoft.xbox.service.model.media;

public class PromoItem extends MediaItemBase {
    public static final int CHANNEL_APP = 4;
    public static final int CHANNEL_GAME = 2;
    public static final int CHANNEL_MUSIC = 3;
    public static final int CHANNEL_UNDEFINED = 0;
    public static final int CHANNEL_VIDEO = 1;
    public static final int PROMOTYPE_ALBUM = 3;
    public static final int PROMOTYPE_APP = 10;
    public static final int PROMOTYPE_ARTIST = 4;
    public static final int PROMOTYPE_EPISODE = 9;
    public static final int PROMOTYPE_GAME = 1;
    public static final int PROMOTYPE_GAMECONTENT = 2;
    public static final int PROMOTYPE_MOVIE = 5;
    public static final int PROMOTYPE_TVEPISODE = 7;
    public static final int PROMOTYPE_TVSEASON = 8;
    public static final int PROMOTYPE_TVSERIES = 6;
    public static final int PROMOTYPE_UNDEFINED = 0;
    public static final int PROMOTYPE_URI = 11;
    public int channelType;
    public String contentMediaId;
    public String contentMediaTypeId;
    public String deeplink;
    public int promoType;
    public String provider;
    public String tvSeasonNumber;
    public String tvSeriesId;
}
