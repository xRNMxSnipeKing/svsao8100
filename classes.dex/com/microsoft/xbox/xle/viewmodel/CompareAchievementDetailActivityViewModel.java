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
import com.microsoft.xbox.xle.app.adapter.CompareAchievementDetailActivityAdapter;
import java.util.EnumSet;
import java.util.LinkedHashMap;

public class CompareAchievementDetailActivityViewModel extends ViewModelBase {
    private int achievementAnimStateVersion;
    private String achievementKey;
    private AchievementModel achievementModel;
    private AvatarViewVM avatarViewVM;
    private String compareGamertag;
    private String gameTitle;
    private AchievementItem meAchievement;
    private AchievementAnimState meAchievementAnimState;
    private AvatarViewActorVMDefault meActorVM;
    private AvatarManifestModel meAvatarModel;
    private long titleId;
    private AchievementAnimState youAchievementAnimState;
    private AvatarViewActorVMDefault youActorVM;
    private AvatarManifestModel youAvatarModel;

    public CompareAchievementDetailActivityViewModel(String compareGamertag, GameInfo game, String achievementKey) {
        boolean z = true;
        this.avatarViewVM = null;
        this.meActorVM = null;
        this.youActorVM = null;
        this.adapter = new CompareAchievementDetailActivityAdapter(this);
        XLEAssert.assertNotNull("Game should not be null.", game);
        String str = "Gamertag should not be empty.";
        boolean z2 = MeProfileModel.getModel().getGamertag() != null && MeProfileModel.getModel().getGamertag().length() > 0;
        XLEAssert.assertTrue(str, z2);
        str = "Compare gamertag should not be empty.";
        if (compareGamertag == null || compareGamertag.length() <= 0) {
            z2 = false;
        } else {
            z2 = true;
        }
        XLEAssert.assertTrue(str, z2);
        String str2 = "Achievement index should not be empty.";
        if (achievementKey == null || achievementKey.length() <= 0) {
            z = false;
        }
        XLEAssert.assertTrue(str2, z);
        this.gameTitle = game.Name;
        this.compareGamertag = compareGamertag;
        this.titleId = game.Id;
        this.achievementKey = achievementKey;
        this.meAchievementAnimState = AchievementAnimState.NONE;
        this.achievementAnimStateVersion = 0;
    }

    public void onRehydrate() {
        this.adapter = new CompareAchievementDetailActivityAdapter(this);
    }

    public String getGameTitle() {
        return this.gameTitle;
    }

    public AchievementItem getAchievement() {
        return this.meAchievement;
    }

    public boolean isBusy() {
        return this.achievementModel.getIsLoading();
    }

    protected void onStartOverride() {
        this.meAvatarModel = AvatarManifestModel.getPlayerModel();
        this.youAvatarModel = AvatarManifestModel.getGamerModel(this.compareGamertag);
        this.achievementModel = AchievementModel.getCompareModel(MeProfileModel.getModel().getGamertag(), this.compareGamertag, this.titleId);
        this.achievementModel.addObserver(this);
        this.meAvatarModel.addObserver(this);
        this.youAvatarModel.addObserver(this);
        this.avatarViewVM = new AvatarViewVMDefault();
        this.meActorVM = new AvatarViewActorVMDefault();
        this.youActorVM = new AvatarViewActorVMDefault();
        this.avatarViewVM.registerActor(this.meActorVM);
        this.avatarViewVM.registerActor(this.youActorVM);
    }

    protected void onStopOverride() {
        this.achievementModel.removeObserver(this);
        this.meAvatarModel.removeObserver(this);
        this.youAvatarModel.removeObserver(this);
        this.achievementModel = null;
        this.meAvatarModel = null;
        this.youAvatarModel = null;
        this.avatarViewVM.onDestroy();
        this.avatarViewVM = null;
        this.meActorVM = null;
        this.youActorVM = null;
    }

    public void updateOverride(AsyncResult<UpdateData> asyncResult) {
        switch (((UpdateData) asyncResult.getResult()).getUpdateType()) {
            case AchievementData:
                if (asyncResult.getException() == null && ((UpdateData) asyncResult.getResult()).getIsFinal()) {
                    this.meAchievement = getAchievement(this.achievementModel.getMeAchievementsMap(), this.achievementKey);
                    this.meAchievementAnimState = this.meAchievement.getAchievementAnimState();
                    AchievementItem youAchievement = getAchievement(this.achievementModel.getYouAchievementsMap(), this.achievementKey);
                    this.youAchievementAnimState = youAchievement != null ? youAchievement.getAchievementAnimState() : AchievementAnimState.UNEARNED;
                    this.achievementAnimStateVersion++;
                    break;
                }
        }
        this.meActorVM.initializeAchievementAnim(getMeManifest(), getMeAchievementAnimState(), getAchievementAnimStateVersion(), 0.0f);
        this.youActorVM.initializeAchievementAnim(getYouManifest(), getYouAchievementAnimState(), getAchievementAnimStateVersion(), 1.0f);
        this.adapter.updateView();
    }

    private static AchievementItem getAchievement(LinkedHashMap<String, Achievement> achievementList, String achievementKey) {
        if (achievementList == null || !achievementList.containsKey(achievementKey)) {
            return null;
        }
        return new AchievementItem((Achievement) achievementList.get(achievementKey));
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
        this.meAvatarModel.load(forceRefresh);
    }

    public XLEAvatarManifest getMeManifest() {
        return this.meAvatarModel.getManifest();
    }

    public XLEAvatarManifest getYouManifest() {
        return this.youAvatarModel.getManifest();
    }

    public AchievementAnimState getMeAchievementAnimState() {
        return this.meAchievementAnimState;
    }

    public AchievementAnimState getYouAchievementAnimState() {
        return this.youAchievementAnimState;
    }

    public int getAchievementAnimStateVersion() {
        return this.achievementAnimStateVersion;
    }

    public AvatarViewVM getAvatarViewVM() {
        return this.avatarViewVM;
    }

    public AvatarViewActorVM getMeActorVM() {
        return this.meActorVM;
    }

    public AvatarViewActorVM getYouActorVM() {
        return this.youActorVM;
    }
}
