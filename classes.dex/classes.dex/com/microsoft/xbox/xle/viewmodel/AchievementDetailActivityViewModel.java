package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.avatar.model.AvatarViewActorVM;
import com.microsoft.xbox.avatar.model.AvatarViewActorVMDefault;
import com.microsoft.xbox.avatar.model.AvatarViewVM;
import com.microsoft.xbox.avatar.model.AvatarViewVMDefault;
import com.microsoft.xbox.service.model.AchievementItem;
import com.microsoft.xbox.service.model.AchievementItem.AchievementAnimState;
import com.microsoft.xbox.service.model.AchievementModel;
import com.microsoft.xbox.service.model.AvatarManifestModel;
import com.microsoft.xbox.service.model.MeProfileModel;
import com.microsoft.xbox.service.model.UpdateData;
import com.microsoft.xbox.service.model.UpdateType;
import com.microsoft.xbox.service.model.serialization.Achievement;
import com.microsoft.xbox.service.model.serialization.GameInfo;
import com.microsoft.xbox.service.model.serialization.XLEAvatarManifest;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.xle.app.adapter.AchievementDetailActivityAdapter;
import java.util.EnumSet;
import java.util.LinkedHashMap;

public class AchievementDetailActivityViewModel extends ViewModelBase {
    private AchievementItem achievement;
    private AchievementAnimState achievementAnimState;
    private int achievementAnimStateVersion;
    private String achievementKey;
    private AchievementModel achievementModel;
    private AvatarViewActorVMDefault actorVM = null;
    private AvatarManifestModel avatarModel;
    private AvatarViewVM avatarViewVM = null;
    private String gameTitle;
    private long titleId;

    public AchievementDetailActivityViewModel() {
        boolean z = true;
        GameInfo game = XLEGlobalData.getInstance().getSelectedGame();
        String achievementKey = XLEGlobalData.getInstance().getSelectedAchievementKey();
        this.adapter = new AchievementDetailActivityAdapter(this);
        XLEAssert.assertNotNull("Game should not be null.", game);
        String str = "Gamertag should not be empty.";
        boolean z2 = MeProfileModel.getModel().getGamertag() != null && MeProfileModel.getModel().getGamertag().length() > 0;
        XLEAssert.assertTrue(str, z2);
        String str2 = "Achievement key should not be empty.";
        if (achievementKey == null || achievementKey.length() <= 0) {
            z = false;
        }
        XLEAssert.assertTrue(str2, z);
        this.gameTitle = game.Name;
        this.titleId = game.Id;
        this.achievementKey = achievementKey;
        this.achievementAnimState = AchievementAnimState.NONE;
        this.achievementAnimStateVersion = 0;
    }

    public void onRehydrate() {
        this.adapter = new AchievementDetailActivityAdapter(this);
    }

    public String getGameTitle() {
        return this.gameTitle;
    }

    public AchievementItem getAchievement() {
        return this.achievement;
    }

    public boolean isBusy() {
        return this.achievementModel.getIsLoading() || this.avatarModel.getIsLoading();
    }

    protected void onStartOverride() {
        this.achievementModel = AchievementModel.getMeModel(MeProfileModel.getModel().getGamertag(), this.titleId);
        this.achievementModel.addObserver(this);
        this.avatarModel = AvatarManifestModel.getPlayerModel();
        this.avatarModel.addObserver(this);
        this.avatarViewVM = new AvatarViewVMDefault();
        this.actorVM = new AvatarViewActorVMDefault();
        this.avatarViewVM.registerActor(this.actorVM);
    }

    protected void onStopOverride() {
        this.achievementModel.removeObserver(this);
        this.avatarModel.removeObserver(this);
        this.achievementModel = null;
        this.avatarModel = null;
        this.avatarViewVM.onDestroy();
        this.actorVM = null;
        this.avatarViewVM = null;
    }

    public void updateOverride(AsyncResult<UpdateData> asyncResult) {
        switch (((UpdateData) asyncResult.getResult()).getUpdateType()) {
            case AchievementData:
                if (asyncResult.getException() == null && ((UpdateData) asyncResult.getResult()).getIsFinal()) {
                    if (this.achievementModel.getMeAchievements() != null) {
                        LinkedHashMap<String, Achievement> achievementList = this.achievementModel.getMeAchievementsMap();
                        if (achievementList != null && achievementList.containsKey(this.achievementKey)) {
                            this.achievement = new AchievementItem((Achievement) achievementList.get(this.achievementKey));
                        }
                        XLEAssert.assertNotNull("Could not find achievement", this.achievement);
                    } else {
                        this.achievement = null;
                    }
                    this.achievementAnimState = this.achievement.getAchievementAnimState();
                    this.achievementAnimStateVersion++;
                    break;
                }
        }
        this.actorVM.initializeAchievementAnim(getManifest(), getAchievementAnimState(), getAchievementAnimStateVersion(), 0.0f);
        this.adapter.updateView();
    }

    protected void onUpdateFinished() {
        if (checkErrorCode(UpdateType.AchievementData, XLEErrorCode.FAILED_TO_GET_ACHIEVEMENTS)) {
            showError(R.string.toast_achievements_error);
        }
        super.onUpdateFinished();
    }

    public void load(boolean forceRefresh) {
        setUpdateTypesToCheck(EnumSet.of(UpdateType.AchievementData));
        this.achievementModel.load(forceRefresh);
        this.avatarModel.load(forceRefresh);
    }

    public XLEAvatarManifest getManifest() {
        return this.avatarModel.getManifest();
    }

    public AchievementAnimState getAchievementAnimState() {
        return this.achievementAnimState;
    }

    public int getAchievementAnimStateVersion() {
        return this.achievementAnimStateVersion;
    }

    public AvatarViewActorVM getActorVM() {
        return this.actorVM;
    }

    public AvatarViewVM getAvatarViewVM() {
        return this.avatarViewVM;
    }
}
