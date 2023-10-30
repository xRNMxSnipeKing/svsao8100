package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.MeProfileModel;
import com.microsoft.xbox.service.model.UpdateData;
import com.microsoft.xbox.service.model.UpdateType;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.xle.app.activity.EditProfileActivity;
import com.microsoft.xbox.xle.app.adapter.FullProfileActivityAdapter;
import java.net.URI;
import java.util.EnumSet;

public class FullProfileActivityViewModel extends ViewModelBase {
    private MeProfileModel profileModel;

    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$microsoft$xbox$service$model$UpdateType = new int[UpdateType.values().length];

        static {
            try {
                $SwitchMap$com$microsoft$xbox$service$model$UpdateType[UpdateType.MeProfileData.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
        }
    }

    public FullProfileActivityViewModel() {
        this.adapter = new FullProfileActivityAdapter(this);
    }

    public void onRehydrate() {
        this.adapter = new FullProfileActivityAdapter(this);
    }

    public String getGamertag() {
        return this.profileModel.getGamertag();
    }

    public String getGamerscore() {
        return this.profileModel.getGamerscore();
    }

    public boolean getIsGold() {
        return this.profileModel.getIsGold();
    }

    public String getName() {
        return this.profileModel.getName();
    }

    public String getMotto() {
        return this.profileModel.getMotto();
    }

    public String getLocation() {
        return this.profileModel.getLocation();
    }

    public String getBio() {
        return this.profileModel.getBio();
    }

    public URI getGamerPicUri() {
        return this.profileModel.getGamerPicUri();
    }

    public boolean isBusy() {
        return this.profileModel.getIsLoading();
    }

    public void updateOverride(AsyncResult<UpdateData> asyncResult) {
        int i = AnonymousClass1.$SwitchMap$com$microsoft$xbox$service$model$UpdateType[((UpdateData) asyncResult.getResult()).getUpdateType().ordinal()];
        this.adapter.updateView();
    }

    protected void onUpdateFinished() {
        if (checkErrorCode(UpdateType.MeProfileData, XLEErrorCode.FAILED_TO_GET_ME_PROFILE)) {
            showError(R.string.toast_profile_error);
        }
        super.onUpdateFinished();
    }

    public void navigateToEditProfile() {
        NavigateTo(EditProfileActivity.class);
    }

    public void load(boolean forceRefresh) {
        setUpdateTypesToCheck(EnumSet.of(UpdateType.MeProfileData));
        this.profileModel.load(forceRefresh);
    }

    protected void onStartOverride() {
        this.profileModel = MeProfileModel.getModel();
        this.profileModel.addObserver(this);
    }

    protected void onStopOverride() {
        this.profileModel.removeObserver(this);
        this.profileModel = null;
    }
}
