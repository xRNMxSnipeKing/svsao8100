package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.AchievementItem;
import com.microsoft.xbox.service.model.AchievementModel;
import com.microsoft.xbox.service.model.MeProfileModel;
import com.microsoft.xbox.service.model.QuickplayModel;
import com.microsoft.xbox.service.model.UpdateData;
import com.microsoft.xbox.service.model.UpdateType;
import com.microsoft.xbox.service.model.edsv2.EDSV2GameDetailModel;
import com.microsoft.xbox.service.model.edsv2.EDSV2GameMediaItem;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaItemModel;
import com.microsoft.xbox.service.model.serialization.Achievement;
import com.microsoft.xbox.service.model.serialization.GameInfo;
import com.microsoft.xbox.service.model.sls.Title;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.toolkit.network.ListState;
import com.microsoft.xbox.xle.app.activity.AchievementDetailActivity;
import com.microsoft.xbox.xle.app.adapter.AdapterFactory;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.Iterator;

public class AchievementsActivityViewModel extends PivotViewModelBase {
    private ArrayList<AchievementItem> achievementList;
    private AchievementModel achievementModel;
    private EDSV2GameMediaItem gameDetailItem;
    private EDSV2GameDetailModel gameDetailModel;
    private long gameId;
    private URI gameImageUri;
    private String gameName;
    private int gameTotalAchievements;
    private Date lastPlayedDate;
    private ArrayList<Achievement> modelAchievementList;
    private ListState viewModelState;

    public AchievementsActivityViewModel() {
        this.viewModelState = ListState.LoadingState;
        this.achievementList = new ArrayList();
        this.adapter = AdapterFactory.getInstance().getAchievementsAdapter(this);
        String str = "Gamertag should not be empty.";
        boolean z = MeProfileModel.getModel().getGamertag() != null && MeProfileModel.getModel().getGamertag().length() > 0;
        XLEAssert.assertTrue(str, z);
        this.gameDetailItem = new EDSV2GameMediaItem(XLEGlobalData.getInstance().getSelectedMediaItemData());
        this.gameId = this.gameDetailItem.getTitleId();
        ArrayList<Title> myRecentGameList = QuickplayModel.getInstance().getGamesQuickplayList();
        if (myRecentGameList != null) {
            Iterator i$ = myRecentGameList.iterator();
            while (i$.hasNext()) {
                Title myGame = (Title) i$.next();
                if (myGame.getTitleId() == this.gameId) {
                    this.gameDetailItem = new EDSV2GameMediaItem(myGame);
                    break;
                }
            }
        }
        this.gameName = this.gameDetailItem.getTitle();
        this.gameImageUri = this.gameDetailItem.getImageUrl();
        this.lastPlayedDate = this.gameDetailItem.getLastPlayedDate();
    }

    public void onRehydrate() {
        this.adapter = AdapterFactory.getInstance().getAchievementsAdapter(this);
    }

    public ArrayList<AchievementItem> getAchievements() {
        return this.achievementList;
    }

    public ListState getViewModelState() {
        return this.viewModelState;
    }

    public String getGameTitle() {
        return this.gameName;
    }

    public URI getGameTileUri() {
        return this.gameImageUri;
    }

    public String getGameAchievementText() {
        if (this.gameId == 0) {
            return "";
        }
        if (XLEGlobalData.getInstance().getIsTablet()) {
            return String.format(XboxApplication.Resources.getString(R.string.achievements_earned), new Object[]{Integer.valueOf(this.achievementModel.getMeTotalAchievementsEarned()), Integer.valueOf(this.achievementModel.getTotalPossibleAchievements())});
        }
        return String.format("%d/%d", new Object[]{Integer.valueOf(this.achievementModel.getMeTotalAchievementsEarned()), Integer.valueOf(this.achievementModel.getTotalPossibleAchievements())});
    }

    public String getGameScoreText() {
        if (this.gameId == 0) {
            return "";
        }
        if (XLEGlobalData.getInstance().getIsTablet()) {
            return String.format(XboxApplication.Resources.getString(R.string.game_score), new Object[]{Integer.valueOf(this.achievementModel.getMeGamerscore()), Integer.valueOf(this.achievementModel.getTotalPossibleGamerscore())});
        }
        return String.format("%d/%d", new Object[]{Integer.valueOf(this.achievementModel.getMeGamerscore()), Integer.valueOf(this.achievementModel.getTotalPossibleGamerscore())});
    }

    public int getGameEarnedPercentage() {
        if (this.achievementModel.getTotalPossibleAchievements() != 0) {
            return (int) ((((float) this.achievementModel.getMeTotalAchievementsEarned()) / ((float) this.achievementModel.getTotalPossibleAchievements())) * 100.0f);
        }
        return 0;
    }

    public Date getGameLastPlayedDate() {
        return this.lastPlayedDate;
    }

    public boolean shouldShowAchievementsHeader() {
        if (this.gameId != 0) {
            ArrayList<Title> myRecentGameList = QuickplayModel.getInstance().getGamesQuickplayList();
            if (myRecentGameList != null) {
                Iterator i$ = myRecentGameList.iterator();
                while (i$.hasNext()) {
                    if (((Title) i$.next()).getTitleId() == this.gameId) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean getHasAchievements() {
        return this.gameTotalAchievements > 0;
    }

    public boolean isBusy() {
        if (this.gameId == 0) {
            return this.gameDetailModel.getIsLoading();
        }
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
            this.adapter.updateView();
        }
    }

    protected void onStartOverride() {
        if (this.gameId == 0) {
            this.gameDetailModel = (EDSV2GameDetailModel) EDSV2MediaItemModel.getModel(this.gameDetailItem);
            this.gameDetailModel.addObserver(this);
        } else {
            this.achievementModel = AchievementModel.getMeModel(MeProfileModel.getModel().getGamertag(), this.gameId);
            this.achievementModel.addObserver(this);
        }
        QuickplayModel.getInstance().addObserver(this);
    }

    protected void onStopOverride() {
        if (this.achievementModel != null) {
            this.achievementModel.removeObserver(this);
            this.achievementModel = null;
        }
        if (this.gameDetailModel != null) {
            this.gameDetailModel.removeObserver(this);
            this.gameDetailModel = null;
        }
        QuickplayModel.getInstance().removeObserver(this);
    }

    public void updateOverride(AsyncResult<UpdateData> asyncResult) {
        switch (((UpdateData) asyncResult.getResult()).getUpdateType()) {
            case MediaItemDetail:
                if (this.gameId == 0 && ((UpdateData) asyncResult.getResult()).getIsFinal()) {
                    if (asyncResult.getException() == null) {
                        this.gameId = this.gameDetailModel.getTitleId();
                        this.gameImageUri = this.gameDetailModel.getImageUrl();
                        this.gameName = this.gameDetailModel.getTitle();
                        if (this.gameId == 0) {
                            this.viewModelState = ListState.ErrorState;
                        }
                        if (this.gameId != 0 && this.achievementModel == null) {
                            this.achievementModel = AchievementModel.getMeModel(MeProfileModel.getModel().getGamertag(), this.gameId);
                            setUpdateTypesToCheck(EnumSet.of(UpdateType.AchievementData));
                            this.achievementModel.addObserver(this);
                            this.achievementModel.load(false);
                            break;
                        }
                    }
                    this.viewModelState = ListState.ErrorState;
                    break;
                }
            case AchievementData:
                if (asyncResult.getException() == null) {
                    if (!(!((UpdateData) asyncResult.getResult()).getIsFinal() || this.achievementModel.getMeAchievements() == null || this.modelAchievementList == this.achievementModel.getMeAchievements())) {
                        this.modelAchievementList = this.achievementModel.getMeAchievements();
                        this.achievementList = new ArrayList();
                        Iterator i$ = this.modelAchievementList.iterator();
                        while (i$.hasNext()) {
                            this.achievementList.add(new AchievementItem((Achievement) i$.next()));
                        }
                        this.gameTotalAchievements = this.achievementList.size();
                        updateViewModelState();
                        break;
                    }
                } else if (this.achievementModel.getMeAchievements() == null) {
                    this.viewModelState = ListState.ErrorState;
                    break;
                }
                break;
        }
        this.adapter.updateView();
    }

    protected void onUpdateFinished() {
        if (checkErrorCode(UpdateType.MediaItemDetail, XLEErrorCode.FAILED_TO_GET_GAME_DETAILS)) {
            this.viewModelState = ListState.ErrorState;
            showError(R.string.toast_game_detail_error);
        } else if (checkErrorCode(UpdateType.AchievementData, XLEErrorCode.FAILED_TO_GET_ACHIEVEMENTS)) {
            if (getViewModelState() == ListState.ValidContentState) {
                showError(R.string.toast_achievements_error);
            } else {
                this.viewModelState = ListState.ErrorState;
                this.adapter.updateView();
            }
        }
        super.onUpdateFinished();
    }

    public void navigateToAchievement(String achievementKey) {
        GameInfo gameInfo = new GameInfo();
        gameInfo.Id = this.gameId;
        gameInfo.Name = this.gameName;
        gameInfo.ImageUri = this.gameImageUri;
        XLEGlobalData.getInstance().setSelectedGame(gameInfo);
        XLEGlobalData.getInstance().setSelectedAchievementKey(achievementKey);
        XLELog.Info("AchievementsActivityViewModel", String.format("Navigating to achievement %s for gamertag=%s, titleid=0x%x", new Object[]{achievementKey, MeProfileModel.getModel().getGamertag(), Long.valueOf(this.gameId)}));
        NavigateTo(AchievementDetailActivity.class);
    }

    public void load(boolean forceRefresh) {
        if (this.gameId == 0) {
            setUpdateTypesToCheck(EnumSet.of(UpdateType.MediaItemDetail));
            this.gameDetailModel.load(forceRefresh);
        } else {
            setUpdateTypesToCheck(EnumSet.of(UpdateType.AchievementData));
            this.achievementModel.load(forceRefresh);
        }
        QuickplayModel.getInstance().load(false);
    }
}
