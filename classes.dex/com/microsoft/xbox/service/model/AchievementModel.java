package com.microsoft.xbox.service.model;

import com.microsoft.xbox.service.model.serialization.Achievement;
import com.microsoft.xbox.service.model.serialization.AchievementDataRaw;
import com.microsoft.xbox.service.network.managers.IAchievementServiceManager;
import com.microsoft.xbox.service.network.managers.ServiceManagerFactory;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.FixedSizeHashtable;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.network.IDataLoaderRunnable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;

public class AchievementModel extends ModelBase<AchievementDataRaw> {
    private static final int MAX_COMPARE_ACHIEVEMENT_MODELS = 5;
    private static final int MAX_ME_ACHIEVEMENT_MODELS = 10;
    private static FixedSizeHashtable<String, AchievementModel> compareAchievementModelCache = new FixedSizeHashtable(5);
    private static FixedSizeHashtable<String, AchievementModel> meAchievementModelCache = new FixedSizeHashtable(10);
    private AchievementData achievementData = new AchievementData();
    private IAchievementServiceManager achievementServiceManager;
    private String compareGamertag;
    private String gamertag;
    private long titleId;

    private class AchievementDataLoaderRunnable extends IDataLoaderRunnable<AchievementDataRaw> {
        private AchievementDataLoaderRunnable() {
        }

        public void onPreExecute() {
        }

        public AchievementDataRaw buildData() throws XLEException {
            return AchievementModel.this.achievementServiceManager.getData(AchievementModel.this.gamertag, AchievementModel.this.compareGamertag, AchievementModel.this.titleId);
        }

        public void onPostExcute(AsyncResult<AchievementDataRaw> result) {
            AchievementModel.this.updateWithNewData(result);
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_GET_ACHIEVEMENTS;
        }
    }

    private AchievementModel(String gamertag, String compareGamertag, long titleId) {
        this.gamertag = gamertag;
        this.compareGamertag = compareGamertag;
        this.titleId = titleId;
        this.achievementServiceManager = ServiceManagerFactory.getInstance().getAchievementServiceManager();
        this.loaderRunnable = new AchievementDataLoaderRunnable();
    }

    public ArrayList<Achievement> getMeAchievements() {
        return this.achievementData.getAchievements(this.gamertag);
    }

    public ArrayList<Achievement> getYouAchievements() {
        String str = "compareGamertag must not be empty to call this method";
        boolean z = this.compareGamertag != null && this.compareGamertag.length() > 0;
        XLEAssert.assertTrue(str, z);
        return this.achievementData.getAchievements(this.compareGamertag);
    }

    public LinkedHashMap<String, Achievement> getMeAchievementsMap() {
        return this.achievementData.getAchievementsHashMap(this.gamertag);
    }

    public LinkedHashMap<String, Achievement> getYouAchievementsMap() {
        String str = "compareGamertag must not be empty to call this method";
        boolean z = this.compareGamertag != null && this.compareGamertag.length() > 0;
        XLEAssert.assertTrue(str, z);
        return this.achievementData.getAchievementsHashMap(this.compareGamertag);
    }

    public int getMeTotalAchievementsEarned() {
        return this.achievementData.getTotalAchievementsEarned(this.gamertag);
    }

    public int getYouTotalAchievementEarned() {
        String str = "compareGamertag must not be empty to call this method";
        boolean z = this.compareGamertag != null && this.compareGamertag.length() > 0;
        XLEAssert.assertTrue(str, z);
        return this.achievementData.getTotalAchievementsEarned(this.compareGamertag);
    }

    public int getTotalPossibleAchievements() {
        return this.achievementData.getTotalPossibleAchievements(this.gamertag);
    }

    public int getTotalPossibleGamerscore() {
        return this.achievementData.getTotalPossibleGamerscore(this.gamertag);
    }

    public int getMeGamerscore() {
        return this.achievementData.getGamerscore(this.gamertag);
    }

    public int getYouGamerscore() {
        String str = "compareGamertag must not be empty to call this method";
        boolean z = this.compareGamertag != null && this.compareGamertag.length() > 0;
        XLEAssert.assertTrue(str, z);
        return this.achievementData.getGamerscore(this.compareGamertag);
    }

    public void load(boolean forceRefresh) {
        loadInternal(forceRefresh, UpdateType.AchievementData, this.loaderRunnable);
    }

    public void updateWithNewData(AsyncResult<AchievementDataRaw> asyncResult) {
        super.updateWithNewData(asyncResult);
        if (asyncResult.getException() == null) {
            this.achievementData = new AchievementData((AchievementDataRaw) asyncResult.getResult());
        }
        notifyObservers(new AsyncResult(new UpdateData(UpdateType.AchievementData, true), this, asyncResult.getException()));
    }

    public static AchievementModel getMeModel(String gamertag, long titleId) {
        boolean z;
        if (Thread.currentThread() == ThreadManager.UIThread) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        String str = "Title id must be positive.";
        if (titleId > 0) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(str, z);
        str = "Gamertag must not be empty.";
        if (gamertag == null || gamertag.length() <= 0) {
            z = false;
        } else {
            z = true;
        }
        XLEAssert.assertTrue(str, z);
        String key = String.format("%s_%d", new Object[]{gamertag, Long.valueOf(titleId)});
        AchievementModel model = (AchievementModel) meAchievementModelCache.get(key);
        if (model != null) {
            return model;
        }
        model = new AchievementModel(gamertag, null, titleId);
        meAchievementModelCache.put(key, model);
        return model;
    }

    public static AchievementModel getCompareModel(String gamertag, String compareGamertag, long titleId) {
        boolean z;
        if (Thread.currentThread() == ThreadManager.UIThread) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        String str = "Title id must be positive.";
        if (titleId > 0) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(str, z);
        str = "Gamertag must not be empty.";
        if (gamertag == null || gamertag.length() <= 0) {
            z = false;
        } else {
            z = true;
        }
        XLEAssert.assertTrue(str, z);
        str = "Compare gamertag must not be empty.";
        if (compareGamertag == null || compareGamertag.length() <= 0) {
            z = false;
        } else {
            z = true;
        }
        XLEAssert.assertTrue(str, z);
        String key = String.format("%s_%s_%d", new Object[]{gamertag, compareGamertag, Long.valueOf(titleId)});
        AchievementModel model = (AchievementModel) compareAchievementModelCache.get(key);
        if (model != null) {
            return model;
        }
        model = new AchievementModel(gamertag, compareGamertag, titleId);
        compareAchievementModelCache.put(key, model);
        return model;
    }

    public static void reset() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        Enumeration<AchievementModel> e = meAchievementModelCache.elements();
        while (e.hasMoreElements()) {
            ((AchievementModel) e.nextElement()).clearObserver();
        }
        e = compareAchievementModelCache.elements();
        while (e.hasMoreElements()) {
            ((AchievementModel) e.nextElement()).clearObserver();
        }
        meAchievementModelCache = new FixedSizeHashtable(10);
        compareAchievementModelCache = new FixedSizeHashtable(5);
    }
}
