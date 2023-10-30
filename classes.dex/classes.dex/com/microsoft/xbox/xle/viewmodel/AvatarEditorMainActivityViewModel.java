package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.avatar.model.AvatarEditorModel;
import com.microsoft.xbox.avatar.model.AvatarEditorModel.CameraType;
import com.microsoft.xbox.avatar.model.AvatarViewActorVM;
import com.microsoft.xbox.avatar.model.AvatarViewVM;
import com.microsoft.xbox.service.model.UpdateData;
import com.microsoft.xbox.service.model.UpdateType;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.SoundManager;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.microsoft.xbox.xle.app.XboxMobileOmnitureTracking;
import com.microsoft.xbox.xle.app.activity.AvatarEditorNewAvatarGenderActivity;
import com.microsoft.xbox.xle.app.activity.AvatarEditorSelectActivity;
import com.microsoft.xbox.xle.app.adapter.AvatarEditorMainAdapter;
import java.util.EnumSet;

public class AvatarEditorMainActivityViewModel extends AvatarEditorViewModelBase {
    public AvatarEditorMainActivityViewModel() {
        this.adapter = new AvatarEditorMainAdapter(this);
    }

    public void onRehydrate() {
        this.adapter = new AvatarEditorMainAdapter(this);
    }

    public void load(boolean forceRefresh) {
    }

    public boolean isBusy() {
        return false;
    }

    public boolean isBlockingBusy() {
        return AvatarEditorModel.getInstance().isBlocking();
    }

    protected void onStartOverride() {
        AvatarEditorModel.getInstance().addObserver(this);
        AvatarEditorModel.getInstance().setCamera(CameraType.CAMERA_TYPE_MAIN, 0, null);
        AvatarEditorModel.getInstance().warpToIdle(true, false);
    }

    protected void onStopOverride() {
        AvatarEditorModel.getInstance().removeObserver(this);
    }

    public void updateOverride(AsyncResult<UpdateData> asyncResult) {
        boolean goBack = false;
        switch (((UpdateData) asyncResult.getResult()).getUpdateType()) {
            case AvatarEditorSave:
                if (((UpdateData) asyncResult.getResult()).getIsFinal() && asyncResult.getException() == null) {
                    SoundManager.getInstance().playSound(R.raw.avatartransition);
                    goBack = true;
                    break;
                }
        }
        this.adapter.updateView();
        if (goBack) {
            exitAvatarEditor();
        }
    }

    protected void onUpdateFinished() {
        if (checkErrorCode(UpdateType.AvatarEditorSave, XLEErrorCode.FAILED_TO_SAVE_AVATAR)) {
            Runnable retryHandler = new Runnable() {
                public void run() {
                    AvatarEditorMainActivityViewModel.this.saveAvatar();
                }
            };
            showOkCancelDialog(XboxApplication.Resources.getString(R.string.toast_avatar_editor_save_error), XboxApplication.Resources.getString(R.string.retry), retryHandler, XboxApplication.Resources.getString(R.string.Cancel), null);
        }
        super.onUpdateFinished();
    }

    public AvatarViewVM getAvatarViewVM() {
        return AvatarEditorModel.getInstance().getAvatarViewVM();
    }

    public AvatarViewActorVM getAvatarActorVM() {
        return AvatarEditorModel.getInstance().getAvatarActorVM();
    }

    public void promptForNewAvatar() {
        Runnable ok = new Runnable() {
            public void run() {
                AvatarEditorMainActivityViewModel.this.navigateToNewAvatar();
            }
        };
        Runnable cancel = new Runnable() {
            public void run() {
            }
        };
        showOkCancelDialog(XboxApplication.Resources.getString(R.string.avatar_editor_new_avatar), XboxApplication.Resources.getString(R.string.avatar_editor_new_avatar_are_you_sure), XboxApplication.Resources.getString(R.string.OK), ok, XboxApplication.Resources.getString(R.string.Cancel), cancel);
    }

    public void saveAvatar() {
        setUpdateTypesToCheck(EnumSet.of(UpdateType.AvatarEditorSave));
        AvatarEditorModel.getInstance().saveData();
        XboxMobileOmnitureTracking.TrackAvatarEditSave();
    }

    public void cancelAvatar() {
        if (isSaveButtonEnabled()) {
            showOkCancelDialog(XLEApplication.Resources.getString(R.string.dialog_areyousure_title), XLEApplication.Resources.getString(R.string.avatar_editor_discard_are_you_sure), XLEApplication.Resources.getString(R.string.OK), new Runnable() {
                public void run() {
                    AvatarEditorMainActivityViewModel.this.exitAvatarEditor();
                }
            }, XLEApplication.Resources.getString(R.string.Cancel), null);
            return;
        }
        exitAvatarEditor();
    }

    private void exitAvatarEditor() {
        goBack();
    }

    public void navigateToNewAvatar() {
        XboxMobileOmnitureTracking.TrackAvatarEditReset();
        NavigateTo(AvatarEditorNewAvatarGenderActivity.class);
    }

    public void navigateToStyle() {
        XLEGlobalData.getInstance().setSelectedMenu(new AvatarEditorSelectTypeCategory(AvatarEditorModel.AVATAREDIT_OPTION_MASK_STYLE));
        NavigateTo(AvatarEditorSelectActivity.class);
    }

    public void navigateToFeatures() {
        if (isMale()) {
            XLEGlobalData.getInstance().setSelectedMenu(new AvatarEditorSelectTypeCategory(AvatarEditorModel.AVATAREDIT_OPTION_MASK_FEATURES_MALE));
        } else {
            XLEGlobalData.getInstance().setSelectedMenu(new AvatarEditorSelectTypeCategory(AvatarEditorModel.AVATAREDIT_OPTION_MASK_FEATURES_FEMALE));
        }
        NavigateTo(AvatarEditorSelectActivity.class);
    }

    public boolean isMale() {
        return AvatarEditorModel.getInstance().isMale();
    }

    public boolean isSaveButtonEnabled() {
        return AvatarEditorModel.getInstance().isModified();
    }

    public void onBackButtonPressed() {
        cancelAvatar();
    }
}
