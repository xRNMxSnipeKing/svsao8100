package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.AchievementModel;
import com.microsoft.xbox.service.model.CompareAchievementInfo;
import com.microsoft.xbox.service.model.CompareGameInfo;
import com.microsoft.xbox.service.model.MeProfileModel;
import com.microsoft.xbox.service.model.UpdateData;
import com.microsoft.xbox.service.model.UpdateType;
import com.microsoft.xbox.service.model.YouProfileModel;
import com.microsoft.xbox.service.model.serialization.Achievement;
import com.microsoft.xbox.service.model.serialization.GameInfo;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.network.ListState;
import com.microsoft.xbox.xle.app.XboxMobileOmnitureTracking;
import com.microsoft.xbox.xle.app.activity.CompareAchievementDetailActivity;
import com.microsoft.xbox.xle.app.adapter.AdapterFactory;
import java.net.URI;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class CompareAchievementsActivityViewModel extends ViewModelBase {
    private AchievementModel achievementModel;
    private ArrayList<CompareAchievementInfo> compareAchievementList;
    private String compareGamertag;
    private GameInfo game;
    private URI meGamerpicUri;
    private ArrayList<Achievement> modelMeAchievementList;
    private ArrayList<Achievement> modelYouAchievementList;
    private CompareGameInfo selectedCompareGameInfo;
    private ListState viewModelState;
    private URI youGamerpicUri;

    public CompareAchievementsActivityViewModel(String compareGamertag, GameInfo game) {
        boolean z = true;
        this.viewModelState = ListState.LoadingState;
        this.compareAchievementList = new ArrayList();
        this.adapter = AdapterFactory.getInstance().getCompareAchievementsAdapter(this);
        String str = "Me gamertag should not be empty";
        boolean z2 = MeProfileModel.getModel().getGamertag() != null && MeProfileModel.getModel().getGamertag().length() > 0;
        XLEAssert.assertTrue(str, z2);
        String str2 = "Compare gamertag should not be empty.";
        if (compareGamertag == null || compareGamertag.length() <= 0) {
            z = false;
        }
        XLEAssert.assertTrue(str2, z);
        XLEAssert.assertNotNull("Game should not be null", game);
        this.compareGamertag = compareGamertag;
        this.game = game;
        this.meGamerpicUri = MeProfileModel.getModel().getGamerPicUri();
        this.youGamerpicUri = YouProfileModel.getModel(this.compareGamertag).getGamerPicUri();
        this.selectedCompareGameInfo = XLEGlobalData.getInstance().getSelectedCompareGameInfo();
    }

    public void onRehydrate() {
        this.adapter = AdapterFactory.getInstance().getCompareAchievementsAdapter(this);
    }

    public String getMeGamerScore() {
        return Integer.toString(this.achievementModel.getMeGamerscore());
    }

    public String getYouGamerScore() {
        return Integer.toString(this.achievementModel.getYouGamerscore());
    }

    public URI getMeGamerpicUri() {
        return this.meGamerpicUri;
    }

    public URI getYouGamerpicUri() {
        return this.youGamerpicUri;
    }

    public String getCompareGamerTag() {
        return this.compareGamertag;
    }

    public String getMeGamerScoreWithTotalText() {
        return this.selectedCompareGameInfo == null ? null : this.selectedCompareGameInfo.getMeGamerscoreWithTotal();
    }

    public String getYouGamerScoreWithTotalText() {
        return this.selectedCompareGameInfo == null ? null : this.selectedCompareGameInfo.getYouGamerscoreWithTotal();
    }

    public String getMeGamerAchievementsPercentText() {
        return this.selectedCompareGameInfo == null ? null : this.selectedCompareGameInfo.getMeAchievementsEarnedPercent();
    }

    public String getYouGamerAchievementsPercentText() {
        return this.selectedCompareGameInfo == null ? null : this.selectedCompareGameInfo.getYouAchievementsEarnedPercent();
    }

    public String getMeGamerAchievementsWithTotalText() {
        return this.selectedCompareGameInfo == null ? null : this.selectedCompareGameInfo.getMeAchievementsEarnedWithTotal();
    }

    public String getYouGamerAchievementsWithTotalText() {
        return this.selectedCompareGameInfo == null ? null : this.selectedCompareGameInfo.getYouAchievementsEarnedWithTotal();
    }

    public int getMeGamerAchievementsPercentValue() {
        return this.selectedCompareGameInfo == null ? 0 : this.selectedCompareGameInfo.getMeAchievementsEarnedPercentValue();
    }

    public int getYouGamerAchievementsPercentValue() {
        return this.selectedCompareGameInfo == null ? 0 : this.selectedCompareGameInfo.getYouAchievementsEarnedPercentValue();
    }

    public ArrayList<CompareAchievementInfo> getAchievements() {
        return this.compareAchievementList;
    }

    public String getGameTitle() {
        return this.game.Name;
    }

    public URI getGameTileUri() {
        return this.game.ImageUri;
    }

    public String getGameScoreText() {
        return Integer.toString(this.game.TotalPossibleGamerscore >= this.achievementModel.getTotalPossibleGamerscore() ? this.game.TotalPossibleGamerscore : this.achievementModel.getTotalPossibleGamerscore());
    }

    public ListState getViewModelState() {
        return this.viewModelState;
    }

    public boolean isBusy() {
        return this.achievementModel.getIsLoading();
    }

    private void updateViewModelState() {
        ListState newState = ListState.LoadingState;
        if (this.achievementModel.getMeAchievements() == null) {
            newState = ListState.LoadingState;
        } else if (this.achievementModel.getMeAchievements().size() == 0) {
            newState = ListState.NoContentState;
        } else {
            newState = ListState.ValidContentState;
        }
        if (this.viewModelState != newState) {
            this.viewModelState = newState;
        }
    }

    public void updateOverride(AsyncResult<UpdateData> asyncResult) {
        switch (((UpdateData) asyncResult.getResult()).getUpdateType()) {
            case AchievementData:
                if (asyncResult.getException() == null) {
                    if (((UpdateData) asyncResult.getResult()).getIsFinal()) {
                        mergeAchievementLists();
                        updateViewModelState();
                        break;
                    }
                } else if (this.achievementModel.getMeAchievements() == null || this.achievementModel.getYouAchievements() == null) {
                    this.viewModelState = ListState.ErrorState;
                    break;
                }
                break;
        }
        this.adapter.updateView();
    }

    private void mergeAchievementLists() {
        if (this.modelMeAchievementList != this.achievementModel.getMeAchievements() && this.modelYouAchievementList != this.achievementModel.getYouAchievements()) {
            this.modelMeAchievementList = this.achievementModel.getMeAchievements();
            this.modelYouAchievementList = this.achievementModel.getYouAchievements();
            ArrayList<CompareAchievementInfo> newAchievementList = mergeAchievementLists(this.modelMeAchievementList, this.modelYouAchievementList);
            if (newAchievementList != null) {
                this.compareAchievementList = new ArrayList();
                this.compareAchievementList.addAll(newAchievementList);
            }
        }
    }

    public static ArrayList<CompareAchievementInfo> mergeAchievementLists(ArrayList<Achievement> meAchievements, ArrayList<Achievement> youAchievements) {
        ArrayList<CompareAchievementInfo> compareAchievementList = null;
        if (meAchievements != null) {
            boolean blocked = youAchievements == null || (meAchievements.size() > 0 && youAchievements.size() == 0);
            compareAchievementList = new ArrayList();
            Iterator i$;
            if (blocked) {
                XLELog.Diagnostic("CompareAchievementsActivityViewModel", "You achievements are blocked.");
                i$ = meAchievements.iterator();
                while (i$.hasNext()) {
                    compareAchievementList.add(new CompareAchievementInfo((Achievement) i$.next(), null));
                }
            } else {
                Achievement meAchievement;
                Achievement youAchievement;
                HashMap<String, Achievement> meAchievementMap = new HashMap(meAchievements.size());
                HashMap<String, Achievement> youAchievementMap = new HashMap(youAchievements.size());
                i$ = meAchievements.iterator();
                while (i$.hasNext()) {
                    meAchievement = (Achievement) i$.next();
                    meAchievementMap.put(meAchievement.Key, meAchievement);
                }
                i$ = youAchievements.iterator();
                while (i$.hasNext()) {
                    youAchievement = (Achievement) i$.next();
                    youAchievementMap.put(youAchievement.Key, youAchievement);
                }
                HashSet<String> addedKeys = new HashSet();
                i$ = youAchievements.iterator();
                while (i$.hasNext()) {
                    youAchievement = (Achievement) i$.next();
                    if (!addedKeys.contains(youAchievement.Key)) {
                        if (youAchievement.IsEarned) {
                            meAchievement = (Achievement) meAchievementMap.get(youAchievement.Key);
                            XLEAssert.assertNotNull("Missing achievement from service", meAchievement);
                            addedKeys.add(youAchievement.Key);
                            compareAchievementList.add(new CompareAchievementInfo(meAchievement, youAchievement));
                        } else {
                            Iterator i$2 = meAchievements.iterator();
                            while (i$2.hasNext()) {
                                meAchievement = (Achievement) i$2.next();
                                if (!addedKeys.contains(meAchievement.Key)) {
                                    Achievement youAchievement2 = (Achievement) youAchievementMap.get(meAchievement.Key);
                                    XLEAssert.assertNotNull("Missing achievement from service", youAchievement2);
                                    addedKeys.add(meAchievement.Key);
                                    compareAchievementList.add(new CompareAchievementInfo(meAchievement, youAchievement2));
                                }
                            }
                        }
                    }
                }
            }
        }
        return compareAchievementList;
    }

    protected void onUpdateFinished() {
        if (checkErrorCode(UpdateType.AchievementData, XLEErrorCode.FAILED_TO_GET_ACHIEVEMENTS)) {
            if (getViewModelState() == ListState.ValidContentState) {
                showError(R.string.toast_achievements_error);
            } else {
                this.viewModelState = ListState.ErrorState;
                this.adapter.updateView();
            }
        }
        super.onUpdateFinished();
    }

    public void navigateToCompareSingleAchievement(String achievementKey) {
        String str = "Achievement key should not be empty.";
        boolean z = achievementKey != null && achievementKey.length() > 0;
        XLEAssert.assertTrue(str, z);
        XLEGlobalData.getInstance().setSelectedAchievementKey(achievementKey);
        XLEGlobalData.getInstance().setSelectedGame(this.game);
        XLEGlobalData.getInstance().setSelectedGamertag(this.compareGamertag);
        XboxMobileOmnitureTracking.TrackCompareAchievement(this.game.Name);
        XLELog.Info("CompareAchievementsActivityViewModel", String.format("Navigating to compare single achievement for gamertags=%s,%s, titleid=0x%x, achievementkey=%s", new Object[]{MeProfileModel.getModel().getGamertag(), this.compareGamertag, Long.valueOf(this.game.Id), achievementKey}));
        NavigateTo(CompareAchievementDetailActivity.class);
    }

    public void load(boolean forceRefresh) {
        setUpdateTypesToCheck(EnumSet.of(UpdateType.AchievementData));
        this.achievementModel.load(forceRefresh);
    }

    protected void onStartOverride() {
        this.achievementModel = AchievementModel.getCompareModel(MeProfileModel.getModel().getGamertag(), this.compareGamertag, this.game.Id);
        this.achievementModel.addObserver(this);
    }

    protected void onStopOverride() {
        this.achievementModel.removeObserver(this);
        this.achievementModel = null;
    }
}
