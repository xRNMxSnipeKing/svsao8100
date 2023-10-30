package com.microsoft.xbox.service.model;

import com.microsoft.xbox.service.model.serialization.GameDataRaw;
import com.microsoft.xbox.service.model.serialization.GameInfo;
import com.microsoft.xbox.service.model.serialization.UserGames;
import com.microsoft.xbox.toolkit.XLEAssert;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

public final class GameData {
    private LinkedHashMap<Long, GameInfo> gameHashTable;
    private Object listLock;
    private int totalUniqueGames;
    private HashMap<String, UserGamesInfo> userGamesMap;

    private static class UserGamesInfo {
        private ArrayList<GameInfo> userGamesList;
        private int userTotalPlayedGames;

        private UserGamesInfo() {
            this.userGamesList = new ArrayList();
            this.userTotalPlayedGames = 0;
        }
    }

    public GameData() {
        this.listLock = new Object();
        this.gameHashTable = null;
        this.userGamesMap = null;
    }

    public GameData(GameDataRaw data) {
        this.listLock = new Object();
        update(data);
    }

    public ArrayList<GameInfo> getGames(String gamertag) {
        if (this.userGamesMap == null || !this.userGamesMap.containsKey(gamertag)) {
            return null;
        }
        return ((UserGamesInfo) this.userGamesMap.get(gamertag)).userGamesList;
    }

    public GameInfo getGameInfo(long titleId) {
        if (this.gameHashTable == null || !this.gameHashTable.containsKey(Long.valueOf(titleId))) {
            return null;
        }
        return (GameInfo) this.gameHashTable.get(Long.valueOf(titleId));
    }

    public int getTotalUniqueGames() {
        return this.totalUniqueGames;
    }

    public int getTotalGamesPlayed(String gamertag) {
        if (this.userGamesMap == null || !this.userGamesMap.containsKey(gamertag)) {
            return 0;
        }
        return ((UserGamesInfo) this.userGamesMap.get(gamertag)).userTotalPlayedGames;
    }

    public void update(GameDataRaw data) {
        XLEAssert.assertNotNull("We should never try to create the data object from null raw data.", data);
        if (data.UserGamesCollection.size() > 0) {
            if (this.userGamesMap == null) {
                this.userGamesMap = new HashMap();
            }
            if (this.gameHashTable == null) {
                this.gameHashTable = new LinkedHashMap();
            }
            this.totalUniqueGames = data.TotalUniqueGames;
            Iterator it = data.UserGamesCollection.iterator();
            while (it.hasNext()) {
                UserGames userGames = (UserGames) it.next();
                synchronized (this.listLock) {
                    UserGamesInfo userGamesInfo;
                    if (this.userGamesMap.containsKey(userGames.GamerTag)) {
                        userGamesInfo = (UserGamesInfo) this.userGamesMap.get(userGames.GamerTag);
                        userGamesInfo.userGamesList.clear();
                    } else {
                        userGamesInfo = new UserGamesInfo();
                    }
                    userGamesInfo.userGamesList.addAll(userGames.GameList);
                    userGamesInfo.userTotalPlayedGames = userGames.TotalGamesPlayed;
                    this.userGamesMap.put(userGames.GamerTag, userGamesInfo);
                }
                Iterator i$ = userGames.GameList.iterator();
                while (i$.hasNext()) {
                    GameInfo game = (GameInfo) i$.next();
                    if (!this.gameHashTable.containsKey(Long.valueOf(game.Id))) {
                        this.gameHashTable.put(Long.valueOf(game.Id), game);
                    }
                }
            }
            return;
        }
        this.gameHashTable = null;
        this.userGamesMap = null;
    }
}
