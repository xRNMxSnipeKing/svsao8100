package com.microsoft.xbox.service.model.serialization;

import java.util.ArrayList;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.core.Commit;

@Root(name = "Achievements")
public class AchievementDataRaw {
    @ElementList
    public ArrayList<UserAchievements> UserAchievementsCollection;

    @Commit
    public void postprocess() {
    }
}
