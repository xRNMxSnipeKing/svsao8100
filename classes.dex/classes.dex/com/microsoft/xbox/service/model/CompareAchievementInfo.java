package com.microsoft.xbox.service.model;

import com.microsoft.xbox.service.model.serialization.Achievement;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XboxApplication;
import java.net.URI;

public class CompareAchievementInfo {
    private static final String ACHIEVEMENTS_ACQUIRED = XboxApplication.Resources.getString(XboxApplication.Instance.getStringRValue("achievements_acquired"));
    private final String achievementName;
    private final URI achievementTileUri;
    private final boolean isSecret;
    private final String key;
    private final String meGamerDescription;
    private final String meGamerEarnedDateTime;
    private final String meGamerscore;
    private final String youGamerEarnedDateTime;
    private final String youGamerscore;

    public CompareAchievementInfo(Achievement meAchievement, Achievement youAchievement) {
        XLEAssert.assertNotNull("Achievement should never be null. The service should always return all achievements.", meAchievement);
        this.achievementTileUri = meAchievement.PictureUri;
        this.key = meAchievement.Key;
        if (meAchievement.IsEarned) {
            this.meGamerscore = Integer.toString(meAchievement.Gamerscore);
            this.achievementName = meAchievement.Name;
            if (meAchievement.EarnedDateTime != null) {
                this.meGamerEarnedDateTime = JavaUtil.getLocalizedDateStringValidated(meAchievement.EarnedDateTime);
            } else {
                this.meGamerEarnedDateTime = ACHIEVEMENTS_ACQUIRED;
            }
        } else {
            this.meGamerscore = XboxApplication.Resources.getString(XboxApplication.Instance.getStringRValue("gamerscore_empty"));
            this.achievementName = meAchievement.Name;
            this.meGamerEarnedDateTime = null;
        }
        if (youAchievement == null || !youAchievement.IsEarned) {
            this.youGamerscore = XboxApplication.Resources.getString(XboxApplication.Instance.getStringRValue("gamerscore_empty"));
            this.youGamerEarnedDateTime = null;
        } else {
            this.youGamerscore = Integer.toString(youAchievement.Gamerscore);
            if (youAchievement.EarnedDateTime != null) {
                this.youGamerEarnedDateTime = JavaUtil.getLocalizedDateStringValidated(youAchievement.EarnedDateTime);
            } else {
                this.youGamerEarnedDateTime = ACHIEVEMENTS_ACQUIRED;
            }
        }
        this.meGamerDescription = meAchievement.Description;
        boolean z = (meAchievement.DisplayBeforeEarned || meAchievement.IsEarned || youAchievement.DisplayBeforeEarned) ? false : true;
        this.isSecret = z;
    }

    public URI getAchievementTileUri() {
        return this.achievementTileUri;
    }

    public String getAchievementName() {
        if (getIsSecret()) {
            return XboxApplication.Resources.getString(XboxApplication.Instance.getStringRValue("achievements_secret"));
        }
        return this.achievementName;
    }

    public String getMeGamerscore() {
        return this.meGamerscore;
    }

    public String getYouGamerscore() {
        return this.youGamerscore;
    }

    public String getKey() {
        return this.key;
    }

    public String getMeGamerEarnedDateTime() {
        return this.meGamerEarnedDateTime;
    }

    public String getYouGamerEarnedDateTime() {
        return this.youGamerEarnedDateTime;
    }

    public String getMeGamerDescription() {
        if (getIsSecret()) {
            return null;
        }
        return this.meGamerDescription;
    }

    private boolean getIsSecret() {
        return this.isSecret;
    }
}
