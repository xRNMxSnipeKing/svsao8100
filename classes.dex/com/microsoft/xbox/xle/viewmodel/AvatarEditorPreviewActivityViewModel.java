package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.xbox.avatar.model.AvatarEditorModel;
import com.microsoft.xbox.avatar.model.AvatarEditorModel.CameraType;
import com.microsoft.xbox.avatar.model.AvatarViewActorVM;
import com.microsoft.xbox.avatar.model.AvatarViewVM;
import com.microsoft.xbox.avatar.view.AvatarEditorOption;
import com.microsoft.xbox.avatar.view.AvatarEditorOptionAsset;
import com.microsoft.xbox.service.model.MeProfileModel;
import com.microsoft.xbox.service.model.UpdateData;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.ui.NavigationManager;
import com.microsoft.xbox.xle.app.activity.AvatarEditorMainActivity;
import com.microsoft.xbox.xle.app.activity.AvatarEditorSelectActivity;
import com.microsoft.xbox.xle.app.adapter.AvatarEditorPreviewAdapter;

public class AvatarEditorPreviewActivityViewModel extends AvatarEditorViewModelBase {
    private boolean animationInvalidated;
    private String assetTitle;
    private AvatarEditorOption pendingAsset;
    private String pendingAssetGuid;
    private int pendingCameraCategoryType;
    private String previewScreenTitle;
    private LoadState screenState;
    private boolean showAssetTitle;
    private AvatarEditorSelectType type;

    public enum LoadState {
        NONE,
        LOADING,
        COMMITING,
        REVERTING
    }

    public AvatarEditorPreviewActivityViewModel() {
        this.pendingCameraCategoryType = 0;
        this.pendingAsset = null;
        this.showAssetTitle = false;
        this.animationInvalidated = false;
        this.type = XLEGlobalData.getInstance().getSelectedMenu();
        this.pendingAsset = XLEGlobalData.getInstance().getSelectedAsset();
        this.adapter = new AvatarEditorPreviewAdapter(this);
    }

    public void onRehydrate() {
        this.adapter = new AvatarEditorPreviewAdapter(this);
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
        setCurrentSelectType();
        this.previewScreenTitle = this.type.getTitle().toUpperCase();
        this.pendingCameraCategoryType = this.type.getCameraCategoryType();
        this.showAssetTitle = true;
        if ((this.type instanceof AvatarEditorSelectTypeCategory) && (this.type.getCategoryMask() & AvatarEditorModel.AVATAREDIT_OPTION_MASK_FEATURES_MALE) != 0 && MeProfileModel.getModel().isLegalLocaleJapanese()) {
            this.showAssetTitle = false;
        }
        this.assetTitle = this.pendingAsset.getAssetTitle();
        this.pendingAssetGuid = null;
        if (this.pendingAsset instanceof AvatarEditorOptionAsset) {
            this.pendingAssetGuid = ((AvatarEditorOptionAsset) this.pendingAsset).getAssetGuid();
        }
        AvatarEditorModel.getInstance().setCamera(CameraType.CAMERA_TYPE_PREVIEW, this.pendingCameraCategoryType, this.pendingAssetGuid);
        if (XLEGlobalData.getInstance().getAvatarEditorPreviewNeedsApply()) {
            XLEGlobalData.getInstance().setAvatarEditorPreviewNeedsApply(false);
            AvatarEditorModel.getInstance().warpToIdle(false, true);
            this.screenState = LoadState.LOADING;
            AvatarEditorModel.getInstance().applyOption(this.pendingAsset);
        } else {
            this.screenState = LoadState.NONE;
        }
        if (this.animationInvalidated) {
            this.animationInvalidated = false;
            AvatarEditorModel.getInstance().warpToIdle(true, true);
        }
    }

    public void onBackButtonPressed() {
        XLEAssert.assertTrue(!AvatarEditorModel.getInstance().isBlocking());
        editorRevert();
    }

    public void editorCommit() {
        XLEAssert.assertTrue(!AvatarEditorModel.getInstance().isBlocking());
        this.screenState = LoadState.COMMITING;
        AvatarEditorModel.getInstance().commitOption();
    }

    public void editorRevert() {
        XLEAssert.assertTrue(!AvatarEditorModel.getInstance().isBlocking());
        this.screenState = LoadState.REVERTING;
        AvatarEditorModel.getInstance().revertOption();
    }

    public void navigateToSelectColor() {
        boolean z = false;
        if (!AvatarEditorModel.getInstance().wouldPreemptClosetSpinAnimation()) {
            boolean z2;
            if (AvatarEditorModel.getInstance().isBlocking()) {
                z2 = false;
            } else {
                z2 = true;
            }
            XLEAssert.assertTrue(z2);
            if (this.pendingAsset != null) {
                z = true;
            }
            XLEAssert.assertTrue(z);
            XLEAssert.assertTrue(this.pendingAsset instanceof AvatarEditorOptionAsset);
            this.animationInvalidated = true;
            XLEGlobalData.getInstance().setSelectedMenu(new AvatarEditorSelectTypeAssetColors(this.type.getCategoryMask(), (AvatarEditorOptionAsset) this.pendingAsset));
            NavigateTo(AvatarEditorSelectActivity.class);
        }
    }

    protected void onStopOverride() {
        AvatarEditorModel.getInstance().removeObserver(this);
    }

    public void updateOverride(AsyncResult<UpdateData> asyncResult) {
        boolean z;
        boolean z2 = true;
        boolean goBack = false;
        boolean goBackCommit = false;
        switch (((UpdateData) asyncResult.getResult()).getUpdateType()) {
            case AvatarEditorLoadedAsset:
                boolean success;
                if (asyncResult.getException() == null) {
                    success = true;
                } else {
                    success = false;
                }
                switch (this.screenState) {
                    case LOADING:
                        if (!success) {
                            XLEGlobalData.getInstance().setAvatarEditorAssetApplyError(true);
                            goBack = true;
                            break;
                        }
                        break;
                    case COMMITING:
                        XLEAssert.assertTrue(success);
                        goBackCommit = true;
                        break;
                    case REVERTING:
                        XLEAssert.assertTrue(success);
                        goBack = true;
                        break;
                    default:
                        break;
                }
        }
        this.adapter.updateView();
        if (goBack) {
            if (AvatarEditorModel.getInstance().isBlocking()) {
                z = false;
            } else {
                z = true;
            }
            XLEAssert.assertTrue(z);
            goBack();
        }
        if (goBackCommit) {
            if (AvatarEditorModel.getInstance().isBlocking()) {
                z = false;
            } else {
                z = true;
            }
            XLEAssert.assertTrue(z);
            if (this.type instanceof AvatarEditorSelectTypeNewAvatar) {
                try {
                    if (NavigationManager.getInstance().IsScreenOnStack(AvatarEditorMainActivity.class)) {
                        NavigationManager.getInstance().PopScreens(3);
                    } else {
                        NavigationManager.getInstance().PopScreensAndReplace(3, AvatarEditorMainActivity.class);
                    }
                } catch (XLEException e) {
                    XLELog.Error("AvatarEditorPreviewActivityViewModel", "Error popping screens");
                    return;
                }
            }
            NavigationManager.getInstance().PopScreens(NavigationManager.getInstance().CountPopsToScreen(AvatarEditorMainActivity.class) - 1);
            if (AvatarEditorModel.getInstance().isBlocking()) {
                z2 = false;
            }
            XLEAssert.assertTrue(z2);
        }
    }

    public boolean getColorableAsset() {
        if (this.pendingAsset == null) {
            return false;
        }
        return this.pendingAsset.isColorable();
    }

    public AvatarViewVM getAvatarViewVM() {
        return AvatarEditorModel.getInstance().getAvatarViewVM();
    }

    public AvatarViewActorVM getAvatarActorVM() {
        return AvatarEditorModel.getInstance().getAvatarActorVM();
    }

    public String getScreenTitle() {
        return this.previewScreenTitle;
    }

    public String getScreenAssetTitle() {
        if (this.showAssetTitle) {
            return this.assetTitle;
        }
        return "";
    }

    private void setCurrentSelectType() {
        XLEAssert.assertTrue(this.type != null);
        XLEGlobalData.getInstance().setSelectedMenu(this.type);
        XLEGlobalData.getInstance().setSelectedAsset(this.pendingAsset);
    }
}
