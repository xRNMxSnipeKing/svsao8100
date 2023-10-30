package com.microsoft.xbox.service.model;

import com.microsoft.xbox.service.model.serialization.Achievement;
import com.microsoft.xbox.service.model.serialization.AchievementDataRaw;
import com.microsoft.xbox.service.model.serialization.UserAchievements;
import com.microsoft.xbox.toolkit.XLEAssert;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public final class AchievementData {
    private final List<UserAchievements> userAchievementList;

    public AchievementData() {
        this.userAchievementList = null;
    }

    public AchievementData(AchievementDataRaw data) {
        XLEAssert.assertNotNull("We should never try to create the data object from null raw data.", data);
        if (data.UserAchievementsCollection.size() > 0) {
            this.userAchievementList = data.UserAchievementsCollection;
        } else {
            this.userAchievementList = null;
        }
    }

    public ArrayList<Achievement> getAchievements(String gamertag) {
        UserAchievements userAchievements = getUserAchievements(gamertag);
        if (userAchievements != null) {
            return userAchievements.AchievementList;
        }
        return null;
    }

    public LinkedHashMap<String, Achievement> getAchievementsHashMap(String gamertag) {
        UserAchievements userAchievements = getUserAchievements(gamertag);
        if (userAchievements != null) {
            return userAchievements.AchievementHashMap;
        }
        return null;
    }

    public int getTotalAchievementsEarned(String gamertag) {
        UserAchievements userAchievements = getUserAchievements(gamertag);
        if (userAchievements != null) {
            return userAchievements.TotalAchievementsEarned;
        }
        return 0;
    }

    public int getTotalPossibleAchievements(String gamertag) {
        UserAchievements userAchievements = getUserAchievements(gamertag);
        if (userAchievements != null) {
            return userAchievements.TotalPossibleAchievements;
        }
        return 0;
    }

    public int getGamerscore(String gamertag) {
        UserAchievements userAchievements = getUserAchievements(gamertag);
        if (userAchievements != null) {
            return userAchievements.Gamerscore;
        }
        return 0;
    }

    public int getTotalPossibleGamerscore(String gamertag) {
        UserAchievements userAchievements = getUserAchievements(gamertag);
        if (userAchievements != null) {
            return userAchievements.TotalPossibleGamerscore;
        }
        return 0;
    }

    private UserAchievements getUserAchievements(String gamertag) {
        if (this.userAchievementList != null && this.userAchievementList.size() > 0) {
            for (UserAchievements userAchievements : this.userAchievementList) {
                if (gamertag.equalsIgnoreCase(userAchievements.GamerTag)) {
                    return userAchievements;
                }
            }
        }
        return null;
    }
}
