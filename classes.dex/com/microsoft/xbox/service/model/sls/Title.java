package com.microsoft.xbox.service.model.sls;

import com.microsoft.xbox.service.model.LaunchType;
import com.microsoft.xbox.service.model.serialization.UTCDateConverter;
import com.microsoft.xbox.toolkit.XLELog;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Title {
    private static final int PLATFORM_XBOX = 0;
    private static final int PLATFORM_XBOX360 = 1;
    private static final String XBOX_MUSIC_LAUNCH_PARAM = "app:5848085B:MusicHomePage";
    private static final String imageUrlFormat = "http://tiles.xbox.com/consoleAssets/%s/%s/largeboxart.jpg";
    public int currentAchievements;
    public int currentGamerScore;
    public boolean isApp;
    public boolean isGame;
    public boolean isTitleAvailableOnConsole;
    private boolean isXboxMusic;
    private boolean isXboxVideo;
    public String lastPlayed;
    public String name;
    public List<Integer> platforms;
    public int sequence;
    public long titleId;
    public int titleType;
    public int totalAchievemenets;
    public int totalGamerScore;

    public Title(Title source) {
        String str = null;
        this.currentAchievements = source.currentAchievements;
        this.currentGamerScore = source.currentGamerScore;
        this.lastPlayed = source.lastPlayed == null ? null : new String(source.lastPlayed);
        if (source.name != null) {
            str = new String(source.name);
        }
        this.name = str;
        if (source.platforms != null) {
            this.platforms = new ArrayList(source.platforms);
        }
        this.sequence = source.sequence;
        this.titleId = source.titleId;
        this.titleType = source.titleType;
        this.totalAchievemenets = source.totalAchievemenets;
        this.totalGamerScore = source.totalGamerScore;
        this.isApp = source.isApp;
        this.isGame = source.isGame;
        this.isTitleAvailableOnConsole = source.isTitleAvailableOnConsole;
    }

    public String getName() {
        return this.name;
    }

    public int getTitleType() {
        return this.titleType;
    }

    public long getTitleId() {
        return this.titleId;
    }

    public URI getImageUrl(String locale) {
        return getImageUrl(locale, this.titleId);
    }

    public static URI getImageUrl(String locale, long titleId) {
        try {
            return new URI(String.format(imageUrlFormat, new Object[]{Long.toString(titleId, 16).toLowerCase(), locale.toLowerCase()}));
        } catch (URISyntaxException e) {
            XLELog.Error("Title", "failed to get uri");
            return null;
        }
    }

    public boolean IsGame() {
        return this.isGame;
    }

    public boolean IsApplication() {
        return this.isApp;
    }

    public boolean IsLaunchableOnConsole() {
        return this.isTitleAvailableOnConsole;
    }

    public Date getLastPlayed() {
        return UTCDateConverter.convert(this.lastPlayed);
    }

    public int getTotalAchievements() {
        return this.totalAchievemenets;
    }

    public int getCurrentAchievements() {
        return this.currentAchievements;
    }

    public int getTotalGamerScore() {
        return this.totalGamerScore;
    }

    public int getCurrentGamerScore() {
        return this.currentGamerScore;
    }

    public boolean getIsXboxMusic() {
        return this.isXboxMusic;
    }

    public void setIsXboxMusic(boolean value) {
        this.isXboxMusic = value;
    }

    public boolean getIsXboxVideo() {
        return this.isXboxVideo;
    }

    public void setIsXboxVideo(boolean value) {
        this.isXboxVideo = value;
    }

    public String getLaunchParameter() {
        if (getIsXboxMusic()) {
            return "app:5848085B:MusicHomePage";
        }
        return null;
    }

    public int getLaunchType() {
        if (getIsXboxMusic()) {
            return LaunchType.UnknownLaunchType.getValue();
        }
        if (IsGame()) {
            return LaunchType.GameLaunchType.getValue();
        }
        return LaunchType.AppLaunchType.getValue();
    }
}
