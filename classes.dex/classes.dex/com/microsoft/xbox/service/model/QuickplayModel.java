package com.microsoft.xbox.service.model;

import com.microsoft.xbox.service.model.sls.Title;
import com.microsoft.xbox.service.model.sls.UserTitleHistory;
import com.microsoft.xbox.service.network.managers.ServiceManagerFactory;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEConstants;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.toolkit.network.IDataLoaderRunnable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class QuickplayModel extends ModelBase<UserTitleHistory> {
    private static final int MAX_ITEM_QUERY_COUNT = 1000;
    private static final int RECENT_ITEM_COUNT = 9;
    private static final String XBOX_MUSIC_TITLE_STRING = XboxApplication.Resources.getString(XboxApplication.Instance.getStringRValue("xbox_music_title"));
    private static final String XBOX_VIDEO_TITLE_STRING = XboxApplication.Resources.getString(XboxApplication.Instance.getStringRValue("xbox_video_title"));
    private ArrayList<Title> allList;
    private ArrayList<Title> appList;
    private ArrayList<Title> gamesList;
    private ArrayList<Title> quickplayList;

    private static class QuickplayModelHolder {
        private static QuickplayModel instance = new QuickplayModel();

        private QuickplayModelHolder() {
        }

        private static void reset() {
            XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
            instance = new QuickplayModel();
        }
    }

    private class LoadAllRunner extends IDataLoaderRunnable<UserTitleHistory> {
        private QuickplayModel caller;

        public LoadAllRunner(QuickplayModel caller) {
            this.caller = caller;
        }

        public void onPreExecute() {
        }

        public UserTitleHistory buildData() throws XLEException {
            return ServiceManagerFactory.getInstance().getSLSServiceManager().getUserTitleHistory(ServiceManagerFactory.getInstance().getSLSServiceManager().getAndCacheUserXuid(), 1000, null, MeProfileModel.getModel().getLegalLocale());
        }

        public void onPostExcute(AsyncResult<UserTitleHistory> result) {
            this.caller.onGetAllTitleListCompleted(result);
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_GET_QUICKPLAY_SUMMARY;
        }
    }

    public QuickplayModel() {
        this.isLoading = false;
        this.lifetime = 3600000;
    }

    public ArrayList<Title> getRecentQuickplayList() {
        return this.quickplayList;
    }

    public ArrayList<Title> getAllQuickplayList() {
        return this.allList;
    }

    public ArrayList<Title> getGamesQuickplayList() {
        return this.gamesList;
    }

    public ArrayList<Title> getAppsQuickplayList() {
        return this.appList;
    }

    public Title getLastPlayedTitle() {
        if (this.allList != null) {
            Iterator i$ = this.allList.iterator();
            while (i$.hasNext()) {
                Title title = (Title) i$.next();
                if (title.isTitleAvailableOnConsole) {
                    return title;
                }
            }
        }
        return null;
    }

    public static QuickplayModel getInstance() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        return QuickplayModelHolder.instance;
    }

    public static void reset() {
        getInstance().clearObserver();
        QuickplayModelHolder.reset();
    }

    public boolean getIsLoading() {
        return this.isLoading;
    }

    public void load(boolean forceRefresh) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        loadInternal(forceRefresh, UpdateType.RecentsData, new LoadAllRunner(this));
    }

    private void onGetAllTitleListCompleted(AsyncResult<UserTitleHistory> asyncResult) {
        boolean z;
        if (Thread.currentThread() == ThreadManager.UIThread) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        if (asyncResult.getException() == null && asyncResult.getResult() != null) {
            UserTitleHistory allTitleHistory = (UserTitleHistory) asyncResult.getResult();
            if (allTitleHistory != null) {
                this.gamesList = new ArrayList();
                this.appList = new ArrayList();
                this.allList = new ArrayList();
                this.quickplayList = new ArrayList();
                for (Title title : allTitleHistory.getTitles()) {
                    if (this.quickplayList.size() < 9 && title.IsLaunchableOnConsole()) {
                        if (title.getTitleId() == XLEConstants.ZUNE_TITLE_ID) {
                            this.quickplayList.addAll(createXboxVideoMusicTitles(title));
                        } else {
                            this.quickplayList.add(title);
                        }
                    }
                    if (title.IsGame()) {
                        this.allList.add(title);
                        this.gamesList.add(title);
                    } else if (title.IsApplication()) {
                        this.appList.add(title);
                        this.allList.add(title);
                    }
                }
            }
            this.lastRefreshTime = new Date();
        }
        this.isLoading = false;
        notifyObservers(new AsyncResult(new UpdateData(UpdateType.RecentsData, true), this, asyncResult.getException()));
    }

    private static ArrayList<Title> createXboxVideoMusicTitles(Title sourceTitle) {
        ArrayList<Title> xboxVideoMusicTitles = new ArrayList(2);
        Title xboxVideoTitle = new Title(sourceTitle);
        xboxVideoTitle.name = XBOX_VIDEO_TITLE_STRING;
        xboxVideoTitle.setIsXboxVideo(true);
        xboxVideoMusicTitles.add(xboxVideoTitle);
        if (MeProfileModel.getModel().getIsXboxMusicSupported()) {
            Title xboxMusicTitle = new Title(sourceTitle);
            xboxMusicTitle.name = XBOX_MUSIC_TITLE_STRING;
            xboxMusicTitle.setIsXboxMusic(true);
            xboxVideoMusicTitles.add(xboxMusicTitle);
        }
        return xboxVideoMusicTitles;
    }
}
