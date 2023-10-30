package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.CompareGameInfo;
import com.microsoft.xbox.service.model.GameModel;
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
import com.microsoft.xbox.xle.app.activity.CompareAchievementsActivity;
import com.microsoft.xbox.xle.app.adapter.AdapterFactory;
import java.net.URI;
import java.util.ArrayList;
import java.util.EnumSet;

public class CompareGamesActivityViewModel extends ViewModelBase {
    private String compareGamertag;
    private ArrayList<CompareGameInfo> compareGamesList;
    private GameModel gameModel;
    private MeProfileModel meProfileModel;
    private ArrayList<GameInfo> modelMeGamesList;
    private ArrayList<GameInfo> modelYouGamesList;
    private ListState viewModelState;
    private YouProfileModel youProfileModel;

    public CompareGamesActivityViewModel(String compareGamertag) {
        this(compareGamertag, false);
    }

    public CompareGamesActivityViewModel(String compareGamertag, boolean onlyProcessExceptionsAndShowToastsWhenActive) {
        boolean z = true;
        super(true, onlyProcessExceptionsAndShowToastsWhenActive);
        this.viewModelState = ListState.LoadingState;
        this.adapter = AdapterFactory.getInstance().getCompareGamesAdapter(this);
        String str = "Compare gamertag should not be empty.";
        if (compareGamertag == null || compareGamertag.length() <= 0) {
            z = false;
        }
        XLEAssert.assertTrue(str, z);
        this.compareGamertag = compareGamertag;
    }

    public void onRehydrate() {
        this.adapter = AdapterFactory.getInstance().getCompareGamesAdapter(this);
    }

    public String getMeGamerScore() {
        return this.meProfileModel.getGamerscore();
    }

    public String getYouGamerScore() {
        return this.youProfileModel.getGamerscore();
    }

    public URI getMeGamerpicUri() {
        return this.meProfileModel.getGamerPicUri();
    }

    public URI getYouGamerpicUri() {
        return this.youProfileModel.getGamerPicUri();
    }

    public String getYouGamerTag() {
        return this.youProfileModel.getGamertag();
    }

    public String getMeTotalGamesPlayed() {
        return String.valueOf(this.gameModel.getMeTotalGamesPlayed());
    }

    public String getYouTotalGamesPlayed() {
        return String.valueOf(this.gameModel.getYouTotalGamesPlayed());
    }

    public ArrayList<CompareGameInfo> getGames() {
        return this.compareGamesList;
    }

    public ListState getViewModelState() {
        return this.viewModelState;
    }

    public boolean isBusy() {
        return this.gameModel.getIsLoading() || this.meProfileModel.getIsLoading() || this.youProfileModel.getIsLoading();
    }

    private void updateViewModelState() {
        ListState newState = ListState.LoadingState;
        if (this.compareGamesList == null) {
            newState = ListState.LoadingState;
        } else if (this.compareGamesList.size() == 0) {
            newState = ListState.NoContentState;
        } else {
            newState = ListState.ValidContentState;
            if (this.gameModel.getHasMoreData()) {
                this.compareGamesList.add(new CompareGameInfo());
            }
        }
        if (this.viewModelState != newState) {
            this.viewModelState = newState;
        }
    }

    public void updateOverride(AsyncResult<UpdateData> asyncResult) {
        switch (((UpdateData) asyncResult.getResult()).getUpdateType()) {
            case GameData:
                if (asyncResult.getException() == null) {
                    if (((UpdateData) asyncResult.getResult()).getIsFinal()) {
                        mergeGameLists();
                        updateViewModelState();
                        break;
                    }
                } else if (this.compareGamesList == null) {
                    this.viewModelState = ListState.ErrorState;
                    break;
                }
                break;
        }
        this.adapter.updateView();
    }

    protected void onUpdateFinished() {
        int toastRid = 0;
        if (checkErrorCode(UpdateType.GameData, XLEErrorCode.FAILED_TO_GET_GAMES)) {
            toastRid = R.string.toast_games_error;
        } else if (checkErrorCode(UpdateType.MeProfileData, XLEErrorCode.FAILED_TO_GET_ME_PROFILE)) {
            toastRid = R.string.toast_profile_error;
        } else if (checkErrorCode(UpdateType.YouProfileData, XLEErrorCode.FAILED_TO_GET_YOU_PROFILE)) {
            toastRid = R.string.toast_profile_error;
        }
        if (toastRid > 0) {
            if (getViewModelState() == ListState.ValidContentState) {
                showError(toastRid);
            } else {
                this.viewModelState = ListState.ErrorState;
                this.adapter.updateView();
            }
        }
        super.onUpdateFinished();
    }

    private void mergeGameLists() {
        boolean replace = (this.modelMeGamesList == this.gameModel.getMeGames() || this.modelYouGamesList == this.gameModel.getYouGames()) ? false : true;
        if (replace) {
            this.modelMeGamesList = this.gameModel.getMeGames();
            this.modelYouGamesList = this.gameModel.getYouGames();
        }
        ArrayList<CompareGameInfo> mergedList = mergeGameLists(this.modelMeGamesList, this.modelYouGamesList);
        if (mergedList != null) {
            if (replace || this.compareGamesList == null) {
                this.compareGamesList = new ArrayList();
            } else {
                this.compareGamesList.clear();
            }
            this.compareGamesList.addAll(mergedList);
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.util.ArrayList<com.microsoft.xbox.service.model.CompareGameInfo> mergeGameLists(java.util.ArrayList<com.microsoft.xbox.service.model.serialization.GameInfo> r17, java.util.ArrayList<com.microsoft.xbox.service.model.serialization.GameInfo> r18) {
        /*
        r1 = 0;
        if (r17 == 0) goto L_0x00ff;
    L_0x0003:
        if (r18 == 0) goto L_0x00ff;
    L_0x0005:
        r1 = new java.util.ArrayList;
        r1.<init>();
        r7 = r17.size();
        r10 = r18.size();
        if (r10 != 0) goto L_0x0030;
    L_0x0014:
        r5 = r17.iterator();
    L_0x0018:
        r12 = r5.hasNext();
        if (r12 == 0) goto L_0x002e;
    L_0x001e:
        r6 = r5.next();
        r6 = (com.microsoft.xbox.service.model.serialization.GameInfo) r6;
        r12 = new com.microsoft.xbox.service.model.CompareGameInfo;
        r13 = 0;
        r12.<init>(r6, r13);
        r1.add(r12);
        goto L_0x0018;
    L_0x002e:
        r2 = r1;
    L_0x002f:
        return r2;
    L_0x0030:
        if (r7 != 0) goto L_0x004e;
    L_0x0032:
        r5 = r18.iterator();
    L_0x0036:
        r12 = r5.hasNext();
        if (r12 == 0) goto L_0x004c;
    L_0x003c:
        r9 = r5.next();
        r9 = (com.microsoft.xbox.service.model.serialization.GameInfo) r9;
        r12 = new com.microsoft.xbox.service.model.CompareGameInfo;
        r13 = 0;
        r12.<init>(r13, r9);
        r1.add(r12);
        goto L_0x0036;
    L_0x004c:
        r2 = r1;
        goto L_0x002f;
    L_0x004e:
        r11 = 0;
        r8 = 0;
    L_0x0050:
        if (r11 < r10) goto L_0x0054;
    L_0x0052:
        if (r8 >= r7) goto L_0x00ff;
    L_0x0054:
        if (r11 != r10) goto L_0x006d;
    L_0x0056:
        r4 = r8;
    L_0x0057:
        if (r4 >= r7) goto L_0x00ff;
    L_0x0059:
        r3 = new com.microsoft.xbox.service.model.CompareGameInfo;
        r0 = r17;
        r12 = r0.get(r4);
        r12 = (com.microsoft.xbox.service.model.serialization.GameInfo) r12;
        r13 = 0;
        r3.<init>(r12, r13);
        r1.add(r3);
        r4 = r4 + 1;
        goto L_0x0057;
    L_0x006d:
        if (r8 != r7) goto L_0x0086;
    L_0x006f:
        r4 = r11;
    L_0x0070:
        if (r4 >= r10) goto L_0x00ff;
    L_0x0072:
        r3 = new com.microsoft.xbox.service.model.CompareGameInfo;
        r13 = 0;
        r0 = r18;
        r12 = r0.get(r4);
        r12 = (com.microsoft.xbox.service.model.serialization.GameInfo) r12;
        r3.<init>(r13, r12);
        r1.add(r3);
        r4 = r4 + 1;
        goto L_0x0070;
    L_0x0086:
        r0 = r18;
        r12 = r0.get(r11);
        r12 = (com.microsoft.xbox.service.model.serialization.GameInfo) r12;
        r13 = r12.Id;
        r0 = r17;
        r12 = r0.get(r8);
        r12 = (com.microsoft.xbox.service.model.serialization.GameInfo) r12;
        r15 = r12.Id;
        r12 = (r13 > r15 ? 1 : (r13 == r15 ? 0 : -1));
        if (r12 != 0) goto L_0x00bb;
    L_0x009e:
        r3 = new com.microsoft.xbox.service.model.CompareGameInfo;
        r0 = r17;
        r12 = r0.get(r8);
        r12 = (com.microsoft.xbox.service.model.serialization.GameInfo) r12;
        r0 = r18;
        r13 = r0.get(r11);
        r13 = (com.microsoft.xbox.service.model.serialization.GameInfo) r13;
        r3.<init>(r12, r13);
        r1.add(r3);
        r11 = r11 + 1;
        r8 = r8 + 1;
        goto L_0x0050;
    L_0x00bb:
        r0 = r18;
        r12 = r0.get(r11);
        r12 = (com.microsoft.xbox.service.model.serialization.GameInfo) r12;
        r13 = r12.LastPlayed;
        r0 = r17;
        r12 = r0.get(r8);
        r12 = (com.microsoft.xbox.service.model.serialization.GameInfo) r12;
        r12 = r12.LastPlayed;
        r12 = r13.compareTo(r12);
        if (r12 <= 0) goto L_0x00ea;
    L_0x00d5:
        r3 = new com.microsoft.xbox.service.model.CompareGameInfo;
        r13 = 0;
        r0 = r18;
        r12 = r0.get(r11);
        r12 = (com.microsoft.xbox.service.model.serialization.GameInfo) r12;
        r3.<init>(r13, r12);
        r1.add(r3);
        r11 = r11 + 1;
        goto L_0x0050;
    L_0x00ea:
        r3 = new com.microsoft.xbox.service.model.CompareGameInfo;
        r0 = r17;
        r12 = r0.get(r8);
        r12 = (com.microsoft.xbox.service.model.serialization.GameInfo) r12;
        r13 = 0;
        r3.<init>(r12, r13);
        r1.add(r3);
        r8 = r8 + 1;
        goto L_0x0050;
    L_0x00ff:
        r2 = r1;
        goto L_0x002f;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.microsoft.xbox.xle.viewmodel.CompareGamesActivityViewModel.mergeGameLists(java.util.ArrayList, java.util.ArrayList):java.util.ArrayList<com.microsoft.xbox.service.model.CompareGameInfo>");
    }

    public void navigateToCompareAchievements(GameInfo game) {
        XLEAssert.assertNotNull("Game should not be null.", game);
        XLEGlobalData.getInstance().setSelectedGamertag(this.compareGamertag);
        XLEGlobalData.getInstance().setSelectedGame(game);
        XLELog.Info("GamesActivityViewModel", String.format("Navigating to compare achievements for gamertags=%s,%s, titleid=0x%x", new Object[]{this.meProfileModel.getGamertag(), this.youProfileModel.getGamertag(), Long.valueOf(game.Id)}));
        NavigateTo(CompareAchievementsActivity.class);
    }

    public void load(boolean forceRefresh) {
        setUpdateTypesToCheck(EnumSet.of(UpdateType.GameData, UpdateType.MeProfileData, UpdateType.YouProfileData));
        this.gameModel.load(forceRefresh);
        this.meProfileModel.load(forceRefresh);
        this.youProfileModel.load(forceRefresh);
    }

    protected void onStartOverride() {
        this.gameModel = GameModel.getCompareModel(MeProfileModel.getModel().getGamertag(), this.compareGamertag);
        this.meProfileModel = MeProfileModel.getModel();
        this.youProfileModel = YouProfileModel.getModel(this.compareGamertag);
        this.gameModel.addObserver(this);
        this.meProfileModel.addObserver(this);
        this.youProfileModel.addObserver(this);
    }

    protected void onStopOverride() {
        this.gameModel.removeObserver(this);
        this.meProfileModel.removeObserver(this);
        this.youProfileModel.removeObserver(this);
        this.gameModel = null;
        this.meProfileModel = null;
        this.youProfileModel = null;
    }
}
