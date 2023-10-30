package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.MeProfileModel;
import com.microsoft.xbox.service.model.UpdateData;
import com.microsoft.xbox.service.model.UpdateType;
import com.microsoft.xbox.service.model.YouProfileModel;
import com.microsoft.xbox.service.model.serialization.GameInfo;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.network.ListState;
import com.microsoft.xbox.xle.app.XboxMobileOmnitureTracking;
import com.microsoft.xbox.xle.app.activity.CompareAchievementsActivity;
import com.microsoft.xbox.xle.app.adapter.AdapterFactory;
import java.util.ArrayList;
import java.util.EnumSet;

public class RecentGamesActivityViewModel extends PivotViewModelBase {
    private YouProfileModel profileModel;
    private ListState viewModelState;
    private String youGamertag;

    public RecentGamesActivityViewModel(String youGamertag) {
        this.viewModelState = ListState.LoadingState;
        this.adapter = AdapterFactory.getInstance().getRecentGamesAdapter(this);
        String str = "You gamertag must not be empty.";
        boolean z = youGamertag != null && youGamertag.length() > 0;
        XLEAssert.assertTrue(str, z);
        this.youGamertag = youGamertag;
    }

    public void onRehydrate() {
        this.adapter = AdapterFactory.getInstance().getRecentGamesAdapter(this);
    }

    public ArrayList<GameInfo> getGames() {
        return this.profileModel.getRecentGames();
    }

    public ListState getViewModelState() {
        return this.viewModelState;
    }

    public boolean isBusy() {
        return this.profileModel.getIsLoading();
    }

    private void updateViewModelState() {
        ListState newState = ListState.LoadingState;
        if (getGames() == null) {
            newState = ListState.LoadingState;
        } else if (getGames().size() == 0) {
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
            case YouProfileData:
                if (asyncResult.getException() == null) {
                    if (((UpdateData) asyncResult.getResult()).getIsFinal()) {
                        updateViewModelState();
                        break;
                    }
                } else if (getGames() == null) {
                    this.viewModelState = ListState.ErrorState;
                    break;
                }
                break;
        }
        this.adapter.updateView();
    }

    protected void onUpdateFinished() {
        if (checkErrorCode(UpdateType.YouProfileData, XLEErrorCode.FAILED_TO_GET_YOU_PROFILE)) {
            if (getViewModelState() == ListState.ValidContentState) {
                showError(R.string.toast_profile_error);
            } else {
                this.viewModelState = ListState.ErrorState;
                this.adapter.updateView();
            }
        }
        super.onUpdateFinished();
    }

    public void navigateToCompareAchievements(GameInfo game) {
        XLEAssert.assertNotNull("Game should not be null.", game);
        XLEGlobalData.getInstance().setSelectedGamertag(this.profileModel.getGamertag());
        XLEGlobalData.getInstance().setSelectedGame(game);
        XboxMobileOmnitureTracking.TrackCompareGame(game.Name);
        XLELog.Info("RecentGamesActivityViewModel", String.format("Navigating to compare achievements for gamertag=%s, titleid=0x%x", new Object[]{this.profileModel.getGamertag(), Long.valueOf(game.Id)}));
        NavigateTo(CompareAchievementsActivity.class);
    }

    public void load(boolean forceRefresh) {
        setUpdateTypesToCheck(EnumSet.of(UpdateType.YouProfileData));
        this.profileModel.load(forceRefresh);
    }

    protected void onStartOverride() {
        String str = "MeProfileModel should have been loaded.";
        boolean z = MeProfileModel.getModel().getGamertag() != null && MeProfileModel.getModel().getGamertag().length() > 0;
        XLEAssert.assertTrue(str, z);
        this.profileModel = YouProfileModel.getModel(this.youGamertag);
        this.profileModel.addObserver(this);
    }

    protected void onStopOverride() {
        this.profileModel.removeObserver(this);
        this.profileModel = null;
    }
}
