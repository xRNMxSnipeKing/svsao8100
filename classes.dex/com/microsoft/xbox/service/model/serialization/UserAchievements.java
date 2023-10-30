package com.microsoft.xbox.service.model.serialization;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.core.Commit;

@Root
public class UserAchievements {
    public LinkedHashMap<String, Achievement> AchievementHashMap;
    @ElementList
    public ArrayList<Achievement> AchievementList;
    @Element
    public String GamerTag;
    @Element
    public int Gamerscore;
    @Element
    public int TotalAchievementsEarned;
    @Element
    public int TotalPossibleAchievements;
    @Element
    public int TotalPossibleGamerscore;

    @Commit
    public void postprocess() {
        this.AchievementHashMap = new LinkedHashMap();
        Iterator i$ = this.AchievementList.iterator();
        while (i$.hasNext()) {
            Achievement achievement = (Achievement) i$.next();
            this.AchievementHashMap.put(achievement.Key, achievement);
        }
    }
}
