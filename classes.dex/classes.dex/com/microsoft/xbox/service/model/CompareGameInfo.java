package com.microsoft.xbox.service.model;

import com.microsoft.xbox.service.model.serialization.GameInfo;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XboxApplication;
import java.net.URI;

public final class CompareGameInfo {
    private static final String GAMER_SCORE_EMPTY = XboxApplication.Resources.getString(XboxApplication.Instance.getStringRValue("gamerscore_empty"));
    private static final String GAMER_SCORE_PERCENT = XboxApplication.Resources.getString(XboxApplication.Instance.getStringRValue("achievements_earned_percentage"));
    private static final String GAMER_SCORE_SLASH = "/";
    private static final int ONE_HUNDRED = 100;
    private final GameInfo gameInfo;
    private final String gameName;
    private final URI gameTileUri;
    private final String meAchievementsEarnedPercent;
    private final int meAchievementsEarnedPercentValue;
    private final String meAchievementsEarnedWithTotal;
    private final String meGamerscore;
    private final String meGamerscoreWithTotal;
    private final long titleId;
    private final String youAchievementsEarnedPercent;
    private final int youAchievementsEarnedPercentValue;
    private final String youAchievementsEarnedWithTotal;
    private final String youGamerscore;
    private final String youGamerscoreWithTotal;

    public CompareGameInfo() {
        this.gameName = null;
        this.gameTileUri = null;
        this.meGamerscore = null;
        this.youGamerscore = null;
        this.titleId = 0;
        this.gameInfo = null;
        this.meAchievementsEarnedWithTotal = null;
        this.youAchievementsEarnedWithTotal = null;
        this.meGamerscoreWithTotal = null;
        this.youGamerscoreWithTotal = null;
        this.meAchievementsEarnedPercent = null;
        this.youAchievementsEarnedPercent = null;
        this.meAchievementsEarnedPercentValue = 0;
        this.youAchievementsEarnedPercentValue = 0;
    }

    public CompareGameInfo(GameInfo meGameInfo, GameInfo youGameInfo) {
        int i = 0;
        String str = "At least one game info needs to be not null";
        boolean z = meGameInfo == null && youGameInfo == null;
        XLEAssert.assertFalse(str, z);
        if (meGameInfo != null) {
            this.meGamerscore = Integer.toString(meGameInfo.Gamerscore);
            this.gameTileUri = meGameInfo.ImageUri;
            this.gameName = meGameInfo.Name;
            this.titleId = meGameInfo.Id;
            this.gameInfo = meGameInfo;
            this.meAchievementsEarnedWithTotal = meGameInfo.AchievementsEarned + GAMER_SCORE_SLASH + meGameInfo.TotalAchievements;
            this.meGamerscoreWithTotal = meGameInfo.Gamerscore + GAMER_SCORE_SLASH + meGameInfo.TotalPossibleGamerscore;
            this.meAchievementsEarnedPercentValue = meGameInfo.TotalAchievements <= 0 ? 0 : (meGameInfo.AchievementsEarned * ONE_HUNDRED) / meGameInfo.TotalAchievements;
            this.meAchievementsEarnedPercent = this.meAchievementsEarnedPercentValue + GAMER_SCORE_PERCENT;
        } else {
            this.meGamerscore = GAMER_SCORE_EMPTY;
            this.gameTileUri = youGameInfo.ImageUri;
            this.gameName = youGameInfo.Name;
            this.titleId = youGameInfo.Id;
            this.gameInfo = youGameInfo;
            this.meAchievementsEarnedWithTotal = GAMER_SCORE_EMPTY;
            this.meGamerscoreWithTotal = GAMER_SCORE_EMPTY;
            this.meAchievementsEarnedPercentValue = 0;
            this.meAchievementsEarnedPercent = 0 + GAMER_SCORE_PERCENT;
        }
        if (youGameInfo != null) {
            this.youGamerscore = Integer.toString(youGameInfo.Gamerscore);
            this.youAchievementsEarnedWithTotal = youGameInfo.AchievementsEarned + GAMER_SCORE_SLASH + youGameInfo.TotalAchievements;
            this.youGamerscoreWithTotal = youGameInfo.Gamerscore + GAMER_SCORE_SLASH + youGameInfo.TotalPossibleGamerscore;
            if (youGameInfo.TotalAchievements > 0) {
                i = (youGameInfo.AchievementsEarned * ONE_HUNDRED) / youGameInfo.TotalAchievements;
            }
            this.youAchievementsEarnedPercentValue = i;
            this.youAchievementsEarnedPercent = this.youAchievementsEarnedPercentValue + GAMER_SCORE_PERCENT;
            return;
        }
        this.youGamerscore = GAMER_SCORE_EMPTY;
        this.youAchievementsEarnedWithTotal = GAMER_SCORE_EMPTY;
        this.youGamerscoreWithTotal = GAMER_SCORE_EMPTY;
        this.youAchievementsEarnedPercentValue = 0;
        this.youAchievementsEarnedPercent = 0 + GAMER_SCORE_PERCENT;
    }

    public URI getGameTileUri() {
        return this.gameTileUri;
    }

    public String getGameName() {
        return this.gameName;
    }

    public String getMeGamerscore() {
        return this.meGamerscore;
    }

    public String getYouGamerscore() {
        return this.youGamerscore;
    }

    public long getTitleId() {
        return this.titleId;
    }

    public GameInfo getGameInfo() {
        return this.gameInfo;
    }

    public String getMeAchievementsEarnedWithTotal() {
        return this.meAchievementsEarnedWithTotal;
    }

    public String getYouAchievementsEarnedWithTotal() {
        return this.youAchievementsEarnedWithTotal;
    }

    public String getMeGamerscoreWithTotal() {
        return this.meGamerscoreWithTotal;
    }

    public String getYouGamerscoreWithTotal() {
        return this.youGamerscoreWithTotal;
    }

    public String getMeAchievementsEarnedPercent() {
        return this.meAchievementsEarnedPercent;
    }

    public String getYouAchievementsEarnedPercent() {
        return this.youAchievementsEarnedPercent;
    }

    public int getMeAchievementsEarnedPercentValue() {
        return this.meAchievementsEarnedPercentValue;
    }

    public int getYouAchievementsEarnedPercentValue() {
        return this.youAchievementsEarnedPercentValue;
    }
}
