package com.microsoft.xbox.xle.viewmodel;

import android.widget.Button;
import com.microsoft.xbox.avatar.model.AvatarEditorModel;
import com.microsoft.xbox.avatar.model.AvatarEditorModel.CameraType;
import com.microsoft.xbox.avatar.model.AvatarViewActorVM;
import com.microsoft.xbox.avatar.model.AvatarViewVM;
import com.microsoft.xbox.service.model.UpdateData;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.xle.app.activity.AvatarEditorBodyActivity;
import com.microsoft.xbox.xle.app.adapter.AvatarEditorBodyAdapter;

public class AvatarEditorBodyActivityViewModel extends AvatarEditorViewModelBase {
    private AvatarEditorBodyActivity activity;
    private Button colorButton;
    private LoadState screenState;

    public enum LoadState {
        LOADING,
        COMMITING
    }

    public AvatarEditorBodyActivityViewModel() {
        this.colorButton = null;
        this.adapter = new AvatarEditorBodyAdapter(this);
    }

    public void onRehydrate() {
        this.adapter = new AvatarEditorBodyAdapter(this);
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
        AvatarEditorModel.getInstance().setCamera(CameraType.CAMERA_TYPE_BODY, 0, null);
        AvatarEditorModel.getInstance().warpToIdle(true, true);
    }

    public void onBackButtonPressed() {
        editorCommit();
    }

    public void editorCommit() {
        XLEAssert.assertTrue(!AvatarEditorModel.getInstance().isBlocking());
        this.screenState = LoadState.COMMITING;
        AvatarEditorModel.getInstance().commitOption();
    }

    private void editorInternal(Runnable r) {
        this.screenState = LoadState.LOADING;
        r.run();
    }

    public void editorTall() {
        editorInternal(new Runnable() {
            public void run() {
                AvatarEditorModel.getInstance().applyBodyTall();
            }
        });
    }

    public void editorSmall() {
        editorInternal(new Runnable() {
            public void run() {
                AvatarEditorModel.getInstance().applyBodySmall();
            }
        });
    }

    public void editorThin() {
        editorInternal(new Runnable() {
            public void run() {
                AvatarEditorModel.getInstance().applyBodyThin();
            }
        });
    }

    public void editorFat() {
        editorInternal(new Runnable() {
            public void run() {
                AvatarEditorModel.getInstance().applyBodyFat();
            }
        });
    }

    public void editorNormal() {
        editorInternal(new Runnable() {
            public void run() {
                AvatarEditorModel.getInstance().applyBodyNormal();
            }
        });
    }

    public boolean editorThinEnabled() {
        return AvatarEditorModel.getInstance().canApplyBodyThin();
    }

    public boolean editorFatEnabled() {
        return AvatarEditorModel.getInstance().canApplyBodyFat();
    }

    public boolean editorTallEnabled() {
        return AvatarEditorModel.getInstance().canApplyBodyTall();
    }

    public boolean editorSmallEnabled() {
        return AvatarEditorModel.getInstance().canApplyBodySmall();
    }

    protected void onStopOverride() {
        AvatarEditorModel.getInstance().removeObserver(this);
    }

    public void updateOverride(AsyncResult<UpdateData> asyncResult) {
        boolean goBack = false;
        switch (((UpdateData) asyncResult.getResult()).getUpdateType()) {
            case AvatarEditorLoadedAsset:
                boolean success = asyncResult.getException() == null;
                switch (this.screenState) {
                    case LOADING:
                        XLEAssert.assertTrue(success);
                        break;
                    case COMMITING:
                        XLEAssert.assertTrue(success);
                        goBack = true;
                        break;
                    default:
                        break;
                }
        }
        this.adapter.updateView();
        if (goBack) {
            goBack();
        }
    }

    public boolean getColorableAsset() {
        if (XLEGlobalData.getInstance().getSelectedAsset() == null) {
            return false;
        }
        return XLEGlobalData.getInstance().getSelectedAsset().isColorable();
    }

    public AvatarViewVM getAvatarViewVM() {
        return AvatarEditorModel.getInstance().getAvatarViewVM();
    }

    public AvatarViewActorVM getAvatarActorVM() {
        return AvatarEditorModel.getInstance().getAvatarActorVM();
    }
}
