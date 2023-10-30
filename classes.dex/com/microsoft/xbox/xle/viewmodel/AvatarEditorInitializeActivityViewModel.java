package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.avatar.model.AvatarEditorModel;
import com.microsoft.xbox.avatar.model.AvatarViewActorVM;
import com.microsoft.xbox.avatar.model.AvatarViewVM;
import com.microsoft.xbox.service.model.AvatarClosetModel;
import com.microsoft.xbox.service.model.AvatarManifestModel;
import com.microsoft.xbox.service.model.UpdateData;
import com.microsoft.xbox.service.model.UpdateType;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.app.activity.AvatarEditorMainActivity;
import com.microsoft.xbox.xle.app.activity.AvatarEditorNewAvatarGenderActivity;
import com.microsoft.xbox.xle.app.adapter.AvatarEditorInitializeAdapter;
import java.util.EnumSet;

public class AvatarEditorInitializeActivityViewModel extends ViewModelBase {
    private LoadState screenState;

    private enum ExitNavigationOption {
        NONE,
        POP_SCREEN,
        AVATAR_CHOOSE_NEW,
        AVATAR_MAIN
    }

    public enum LoadState {
        LOADING_STEP_1,
        LOADING_STEP_2,
        SUCCESS,
        ERROR
    }

    public boolean isBusy() {
        return false;
    }

    public LoadState getScreenState() {
        return this.screenState;
    }

    public AvatarEditorInitializeActivityViewModel() {
        super(false, false);
        this.adapter = new AvatarEditorInitializeAdapter(this);
    }

    public void onRehydrate() {
        this.adapter = new AvatarEditorInitializeAdapter(this);
    }

    public String getBlockingStatusText() {
        return XLEApplication.Instance.getString(R.string.loading);
    }

    public boolean isBlockingBusy() {
        return (this.screenState == LoadState.SUCCESS || this.screenState == LoadState.ERROR) ? false : true;
    }

    public void load(boolean forceRefresh) {
        onStateChanged(LoadState.LOADING_STEP_1);
    }

    protected void onStartOverride() {
        AvatarEditorModel.getInstance().addObserver(this);
        AvatarManifestModel.getPlayerModel().addObserver(this);
        AvatarClosetModel.getPlayerModel().addObserver(this);
        AvatarClosetModel.getStockModel().addObserver(this);
    }

    protected void onStopOverride() {
        AvatarEditorModel.getInstance().removeObserver(this);
        AvatarManifestModel.getPlayerModel().removeObserver(this);
        AvatarClosetModel.getPlayerModel().removeObserver(this);
        AvatarClosetModel.getStockModel().removeObserver(this);
    }

    public void onResume() {
        if (this.screenState == LoadState.ERROR) {
            showRetryPopup();
        }
        super.onResume();
    }

    public void updateOverride(AsyncResult<UpdateData> asyncResult) {
        this.adapter.updateView();
    }

    protected void onUpdateFinished() {
        final boolean typesToCheckHadAnyErrors = updateTypesToCheckHadAnyErrors();
        super.onUpdateFinished();
        ThreadManager.UIThreadPost(new Runnable() {
            public void run() {
                switch (AvatarEditorInitializeActivityViewModel.this.screenState) {
                    case LOADING_STEP_1:
                        if (typesToCheckHadAnyErrors) {
                            XLELog.Error("AvatarEditorInitializeViewModel", "Loading Step 1 Error");
                            AvatarEditorInitializeActivityViewModel.this.onStateChanged(LoadState.ERROR);
                            return;
                        }
                        AvatarEditorInitializeActivityViewModel.this.onStateChanged(LoadState.LOADING_STEP_2);
                        return;
                    case LOADING_STEP_2:
                        if (typesToCheckHadAnyErrors) {
                            XLELog.Error("AvatarEditorInitializeViewModel", "Loading Step 2 Error");
                            AvatarEditorInitializeActivityViewModel.this.onStateChanged(LoadState.ERROR);
                            return;
                        }
                        AvatarEditorInitializeActivityViewModel.this.onStateChanged(LoadState.SUCCESS);
                        return;
                    default:
                        return;
                }
            }
        });
    }

    private void onStateChanged(LoadState state) {
        if (state != this.screenState) {
            this.screenState = state;
            ExitNavigationOption exitNavigationOption = ExitNavigationOption.NONE;
            switch (this.screenState) {
                case LOADING_STEP_1:
                    XLELog.Info("AvatarEditorInitialize", "LOADING_STATE_1");
                    setUpdateTypesToCheck(EnumSet.of(UpdateType.AvatarManifestLoad, UpdateType.AvatarClosetData, UpdateType.AvatarStockClosetData));
                    XLEGlobalData.getInstance().setAvatarEditorCrashed(false);
                    XLEGlobalData.getInstance().setAvatarEditorAssetApplyError(false);
                    AvatarEditorModel.getInstance().avatarEditorClearScene();
                    AvatarManifestModel.getPlayerModel().load(true);
                    AvatarClosetModel.getPlayerModel().load(true);
                    AvatarClosetModel.getStockModel().load(false);
                    break;
                case LOADING_STEP_2:
                    XLELog.Info("AvatarEditorInitialize", "LOADING_STATE_2");
                    setUpdateTypesToCheck(EnumSet.of(UpdateType.AvatarEditorInitialize));
                    XLEApplication.getMainActivity().resetAvatarViewFloat();
                    AvatarEditorModel.getInstance().avatarEditorInitialize();
                    break;
                case SUCCESS:
                    XLELog.Info("AvatarEditorInitialize", "SUCCESS");
                    if (!AvatarEditorModel.getInstance().isShadowtar()) {
                        exitNavigationOption = ExitNavigationOption.AVATAR_MAIN;
                        break;
                    } else {
                        exitNavigationOption = ExitNavigationOption.AVATAR_CHOOSE_NEW;
                        break;
                    }
                case ERROR:
                    XLELog.Info("AvatarEditorInitialize", "ERROR");
                    showRetryPopup();
                    break;
            }
            XLELog.Info("AvatarEditorInitializeActivityViewModel", "state changed to " + this.screenState.toString());
            this.adapter.updateView();
            switch (exitNavigationOption) {
                case AVATAR_CHOOSE_NEW:
                    NavigateTo(AvatarEditorNewAvatarGenderActivity.class, false);
                    return;
                case AVATAR_MAIN:
                    NavigateTo(AvatarEditorMainActivity.class, false);
                    return;
                case POP_SCREEN:
                    goBack();
                    return;
                default:
                    return;
            }
        }
    }

    public AvatarViewVM getAvatarViewVM() {
        return AvatarEditorModel.getInstance().getAvatarViewVM();
    }

    public AvatarViewActorVM getAvatarActorVM() {
        return AvatarEditorModel.getInstance().getAvatarActorVM();
    }

    private void showRetryPopup() {
        Runnable retryHandler = new Runnable() {
            public void run() {
                AvatarEditorInitializeActivityViewModel.this.onStateChanged(LoadState.LOADING_STEP_1);
            }
        };
        Runnable cancelHandler = new Runnable() {
            public void run() {
                AvatarEditorInitializeActivityViewModel.this.goBack();
            }
        };
        showOkCancelDialog(XboxApplication.Resources.getString(R.string.avatar_editor_failed), XboxApplication.Resources.getString(R.string.retry), retryHandler, XboxApplication.Resources.getString(R.string.Cancel), cancelHandler);
    }
}
