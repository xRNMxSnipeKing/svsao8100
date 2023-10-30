package com.microsoft.xbox.service.model;

import com.microsoft.xbox.service.model.serialization.Achievement;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XboxApplication;
import java.net.URI;
import org.codehaus.jackson.util.MinimalPrettyPrinter;

public final class AchievementItem {
    private final String acquired;
    private final String description;
    private final String earnedDateTime;
    private final String gamerscore;
    private final boolean isEarned;
    private final boolean isSecret;
    private final String key;
    private final String name;
    private final URI tileUri;

    public enum AchievementAnimState {
        NONE,
        SECRET,
        UNEARNED,
        EARNED,
        COUNT
    }

    public AchievementItem(Achievement achievement) {
        this.tileUri = achievement.PictureUri;
        boolean z = (achievement.DisplayBeforeEarned || achievement.IsEarned) ? false : true;
        this.isSecret = z;
        if (this.isSecret) {
            this.name = XboxApplication.Resources.getString(XboxApplication.Instance.getStringRValue("achievements_secret"));
        } else {
            this.name = achievement.Name;
        }
        if (this.isSecret) {
            this.gamerscore = XboxApplication.Resources.getString(XboxApplication.Instance.getStringRValue("gamerscore_empty"));
        } else {
            this.gamerscore = Integer.toString(achievement.Gamerscore);
        }
        String text = "";
        if (achievement.IsEarned) {
            text = XboxApplication.Resources.getString(XboxApplication.Instance.getStringRValue("achievements_acquired"));
            if (achievement.EarnedOnline) {
                text = (text + MinimalPrettyPrinter.DEFAULT_ROOT_VALUE_SEPARATOR) + JavaUtil.getLocalizedDateString(achievement.EarnedDateTime);
            }
        }
        this.acquired = text;
        if (achievement.IsEarned) {
            this.description = achievement.Description;
        } else if (achievement.DisplayBeforeEarned) {
            this.description = achievement.HowToEarn;
        } else {
            XLEAssert.assertTrue(this.isSecret);
            this.description = XboxApplication.Resources.getString(XboxApplication.Instance.getStringRValue("achievementdetails_sercret_description"));
        }
        this.isEarned = achievement.IsEarned;
        this.key = achievement.Key;
        if (achievement.IsEarned && achievement.EarnedOnline) {
            this.earnedDateTime = JavaUtil.getLocalizedDateString(achievement.EarnedDateTime);
        } else {
            this.earnedDateTime = null;
        }
    }

    public URI getTileUri() {
        return this.tileUri;
    }

    public String getName() {
        return this.name;
    }

    public String getGamerscore() {
        return this.gamerscore;
    }

    public String getAcquired() {
        return this.acquired;
    }

    public String getDescription() {
        return this.description;
    }

    public boolean getIsEarned() {
        return this.isEarned;
    }

    public boolean getIsSecret() {
        return this.isSecret;
    }

    public String getKey() {
        return this.key;
    }

    public String getEarnedDateTime() {
        return this.earnedDateTime;
    }

    public AchievementAnimState getAchievementAnimState() {
        if (getIsEarned()) {
            return AchievementAnimState.EARNED;
        }
        if (getIsSecret()) {
            return AchievementAnimState.SECRET;
        }
        return AchievementAnimState.UNEARNED;
    }
}
