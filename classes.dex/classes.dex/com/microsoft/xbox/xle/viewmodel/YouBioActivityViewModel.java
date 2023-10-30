package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.MeProfileModel;
import com.microsoft.xbox.service.model.UpdateData;
import com.microsoft.xbox.service.model.UpdateType;
import com.microsoft.xbox.service.model.YouProfileModel;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.xle.app.adapter.AdapterFactory;
import java.util.EnumSet;

public class YouBioActivityViewModel extends PivotViewModelBase {
    private YouProfileModel profileModel;
    private String youGamertag;

    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$microsoft$xbox$service$model$UpdateType = new int[UpdateType.values().length];

        static {
            try {
                $SwitchMap$com$microsoft$xbox$service$model$UpdateType[UpdateType.YouProfileData.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
        }
    }

    public YouBioActivityViewModel(String youGamertag) {
        this.adapter = AdapterFactory.getInstance().getYouBioAdapter(this);
        String str = "You gamertag must not be empty.";
        boolean z = youGamertag != null && youGamertag.length() > 0;
        XLEAssert.assertTrue(str, z);
        this.youGamertag = youGamertag;
    }

    public void onRehydrate() {
        this.adapter = AdapterFactory.getInstance().getYouBioAdapter(this);
    }

    public String getGamertag() {
        return this.profileModel.getGamertag();
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

    public boolean isBusy() {
        return this.profileModel.getIsLoading();
    }

    public void updateOverride(AsyncResult<UpdateData> asyncResult) {
        int i = AnonymousClass1.$SwitchMap$com$microsoft$xbox$service$model$UpdateType[((UpdateData) asyncResult.getResult()).getUpdateType().ordinal()];
        this.adapter.updateView();
    }

    protected void onUpdateFinished() {
        if (checkErrorCode(UpdateType.YouProfileData, XLEErrorCode.FAILED_TO_GET_YOU_PROFILE)) {
            showError(R.string.toast_profile_error);
        }
        super.onUpdateFinished();
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
