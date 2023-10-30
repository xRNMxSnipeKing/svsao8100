package com.microsoft.xbox.service.model;

import com.microsoft.xbox.service.model.serialization.GameDataRaw;
import com.microsoft.xbox.service.model.serialization.GameInfo;
import com.microsoft.xbox.service.network.managers.IGameServiceManager;
import com.microsoft.xbox.service.network.managers.ServiceManagerFactory;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.DataLoaderTask;
import com.microsoft.xbox.toolkit.FixedSizeHashtable;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.network.IDataLoaderRunnable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;

public class GameModel extends ModelBase<GameDataRaw> {
    private static final int FIRST_PAGE_SIZE = 50;
    private static final int FULL_PAGE_SIZE = 1000;
    private static final int MAX_COMPARE_GAME_MODELS = 5;
    private static final int PAGE_NUMBER = 1;
    private static FixedSizeHashtable<String, GameModel> compareGameModelCache = new FixedSizeHashtable(5);
    private String compareGamertag;
    private GameDataLoaderRunnable2 fullPageRunnable;
    private GameData gameData = new GameData();
    private IGameServiceManager gameServiceManager;
    private String gamertag;
    private boolean hasMoreData = false;

    private class GameDataLoaderRunnable1 extends IDataLoaderRunnable<GameDataRaw> {
        private GameDataLoaderRunnable1() {
        }

        public void onPreExecute() {
        }

        public GameDataRaw buildData() throws XLEException {
            return GameModel.this.gameServiceManager.getData(GameModel.this.gamertag, GameModel.this.compareGamertag, GameModel.FIRST_PAGE_SIZE, 1);
        }

        public void onPostExcute(AsyncResult<GameDataRaw> result) {
            GameModel.this.onFirstPageComplete(result);
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_GET_GAMES;
        }
    }

    private class GameDataLoaderRunnable2 extends IDataLoaderRunnable<GameDataRaw> {
        private GameDataLoaderRunnable2() {
        }

        public void onPreExecute() {
        }

        public GameDataRaw buildData() throws XLEException {
            return GameModel.this.gameServiceManager.getData(GameModel.this.gamertag, GameModel.this.compareGamertag, 1000, 1);
        }

        public void onPostExcute(AsyncResult<GameDataRaw> result) {
            GameModel.this.onFullPageComplete(result);
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_GET_GAMES;
        }
    }

    private GameModel(String gamertag, String compareGamertag) {
        this.gamertag = gamertag;
        this.compareGamertag = compareGamertag;
        this.gameServiceManager = ServiceManagerFactory.getInstance().getGameServiceManager();
        this.loaderRunnable = new GameDataLoaderRunnable1();
        this.fullPageRunnable = new GameDataLoaderRunnable2();
    }

    public ArrayList<GameInfo> getMeGames() {
        return this.gameData.getGames(this.gamertag);
    }

    public ArrayList<GameInfo> getYouGames() {
        String str = "compareGamertag must not be empty to call this method";
        boolean z = this.compareGamertag != null && this.compareGamertag.length() > 0;
        XLEAssert.assertTrue(str, z);
        return this.gameData.getGames(this.compareGamertag);
    }

    public int getMeTotalGamesPlayed() {
        return this.gameData.getTotalGamesPlayed(this.gamertag);
    }

    public int getYouTotalGamesPlayed() {
        String str = "compareGamertag must not be empty to call this method";
        boolean z = this.compareGamertag != null && this.compareGamertag.length() > 0;
        XLEAssert.assertTrue(str, z);
        return this.gameData.getTotalGamesPlayed(this.compareGamertag);
    }

    public boolean getHasMoreData() {
        return this.hasMoreData;
    }

    public void load(boolean forceRefresh) {
        loadInternal(forceRefresh, UpdateType.GameData, this.loaderRunnable);
    }

    public void updateWithNewData(AsyncResult<GameDataRaw> result) {
        if (result.getException() == null) {
            this.lastRefreshTime = new Date();
        }
    }

    public void onFirstPageComplete(AsyncResult<GameDataRaw> asyncResult) {
        updateWithNewData(asyncResult);
        if (asyncResult.getException() == null) {
            this.gameData = new GameData((GameDataRaw) asyncResult.getResult());
            if (this.gameData.getTotalUniqueGames() > FIRST_PAGE_SIZE) {
                XLELog.Diagnostic("GameModel", "Loading the rest of the games");
                this.hasMoreData = true;
                new DataLoaderTask(this.lastInvalidatedTick, this.fullPageRunnable).execute();
            } else {
                this.isLoading = false;
            }
        } else {
            this.isLoading = false;
        }
        notifyObservers(new AsyncResult(new UpdateData(UpdateType.GameData, true), this, asyncResult.getException()));
    }

    public void onFullPageComplete(AsyncResult<GameDataRaw> asyncResult) {
        updateWithNewData(asyncResult);
        if (asyncResult.getException() == null) {
            this.gameData.update((GameDataRaw) asyncResult.getResult());
        }
        this.isLoading = false;
        this.hasMoreData = false;
        notifyObservers(new AsyncResult(new UpdateData(UpdateType.GameData, true), this, asyncResult.getException()));
    }

    public GameInfo getGameInfo(long titleId) {
        return this.gameData.getGameInfo(titleId);
    }

    public static void reset() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        Enumeration<GameModel> e = compareGameModelCache.elements();
        while (e.hasMoreElements()) {
            ((GameModel) e.nextElement()).clearObserver();
        }
        compareGameModelCache = new FixedSizeHashtable(5);
    }

    public static GameModel getCompareModel(String gamertag, String compareGamertag) {
        boolean z;
        if (Thread.currentThread() == ThreadManager.UIThread) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        String str = "Gamertag must not be empty.";
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
        String key = String.format("%s_%s", new Object[]{gamertag, compareGamertag});
        GameModel model = (GameModel) compareGameModelCache.get(key);
        if (model != null) {
            return model;
        }
        model = new GameModel(gamertag, compareGamertag);
        compareGameModelCache.put(key, model);
        return model;
    }
}
