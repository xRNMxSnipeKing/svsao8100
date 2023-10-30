package com.microsoft.xbox.avatar.model;

import com.microsoft.xbox.avatar.model.AvatarViewActorVMEditor.NotifyLoadedAsset;
import com.microsoft.xbox.avatar.view.AvatarEditorOption;
import com.microsoft.xbox.avatar.view.AvatarEditorOptionAsset;
import com.microsoft.xbox.avatar.view.AvatarEditorOptionAssetNonstock;
import com.microsoft.xbox.avatar.view.AvatarEditorOptionAssetStock;
import com.microsoft.xbox.avatar.view.AvatarEditorOptionColor;
import com.microsoft.xbox.avatar.view.AvatarEditorOptionNewAvatar;
import com.microsoft.xbox.service.model.AvatarClosetModel;
import com.microsoft.xbox.service.model.AvatarManifestModel;
import com.microsoft.xbox.service.model.UpdateData;
import com.microsoft.xbox.service.model.UpdateType;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.XLEMath;
import com.microsoft.xbox.toolkit.XLEObservable;
import com.microsoft.xbox.toolkit.XLEObserver;
import com.xbox.avatarrenderer.AvatarEditor.AvatarEditOption;
import com.xbox.avatarrenderer.AvatarEditor.AvatarEditOptions;
import com.xbox.avatarrenderer.Kernel.AvatarManifest;
import com.xbox.avatarrenderer.Kernel.AvatarManifest.AVATAR_BODY_TYPE;
import com.xbox.avatarrenderer.Kernel.AvatarManifestEditor;

public class AvatarEditorModel extends XLEObservable<UpdateData> {
    public static final int AVATAREDIT_OPTION_AWARDS = 4194304;
    public static final int AVATAREDIT_OPTION_BODY = 1;
    public static final int AVATAREDIT_OPTION_BOTTOMS = 32;
    public static final int AVATAREDIT_OPTION_COLOR_EYE = 33554432;
    public static final int AVATAREDIT_OPTION_COLOR_EYEBROW = 536870912;
    public static final int AVATAREDIT_OPTION_COLOR_EYE_SHADOW = 67108864;
    public static final int AVATAREDIT_OPTION_COLOR_FACIAL_FEATURE = 268435456;
    public static final int AVATAREDIT_OPTION_COLOR_FACIAL_HAIR = Integer.MIN_VALUE;
    public static final int AVATAREDIT_OPTION_COLOR_HAIR = 134217728;
    public static final int AVATAREDIT_OPTION_COLOR_LIP = 1073741824;
    public static final int AVATAREDIT_OPTION_COLOR_SKIN = 16777216;
    public static final int AVATAREDIT_OPTION_DRESS_UP = 8388608;
    public static final int AVATAREDIT_OPTION_EARRINGS = 2048;
    public static final int AVATAREDIT_OPTION_FEATURES_CHIN = 1048576;
    public static final int AVATAREDIT_OPTION_FEATURES_EARS = 2097152;
    public static final int AVATAREDIT_OPTION_FEATURES_EYEBROWS = 16384;
    public static final int AVATAREDIT_OPTION_FEATURES_EYES = 8192;
    public static final int AVATAREDIT_OPTION_FEATURES_FACIAL_FEATURES = 131072;
    public static final int AVATAREDIT_OPTION_FEATURES_FACIAL_HAIR = 65536;
    public static final int AVATAREDIT_OPTION_FEATURES_HAIRSTYLES = 8;
    public static final int AVATAREDIT_OPTION_FEATURES_MOUTH = 32768;
    public static final int AVATAREDIT_OPTION_FEATURES_NOSE = 524288;
    public static final int AVATAREDIT_OPTION_GLASSES = 512;
    public static final int AVATAREDIT_OPTION_GLOVES = 256;
    public static final int AVATAREDIT_OPTION_HEADWEAR = 128;
    public static final int AVATAREDIT_OPTION_MASK_ALL = -1;
    public static final int AVATAREDIT_OPTION_MASK_COLORS_FEMALE = 2130706432;
    public static final int AVATAREDIT_OPTION_MASK_COLORS_MALE = -16777216;
    public static final int AVATAREDIT_OPTION_MASK_FEATURES_CHIN_AND_MOUTH = 1081344;
    public static final int AVATAREDIT_OPTION_MASK_FEATURES_EYES_AND_EYEBROWS = 24576;
    public static final int AVATAREDIT_OPTION_MASK_FEATURES_FACIAL_FEATURES_MALE = 196608;
    public static final int AVATAREDIT_OPTION_MASK_FEATURES_FEMALE = 3858440;
    public static final int AVATAREDIT_OPTION_MASK_FEATURES_MALE = 3923976;
    public static final int AVATAREDIT_OPTION_MASK_STYLE = 12591092;
    public static final int AVATAREDIT_OPTION_MASK_STYLE_ACCESSORIES = 7936;
    public static final int AVATAREDIT_OPTION_MENU = 2;
    public static final int AVATAREDIT_OPTION_PROPS = 4;
    public static final int AVATAREDIT_OPTION_RINGS = 4096;
    public static final int AVATAREDIT_OPTION_SHOES = 64;
    public static final int AVATAREDIT_OPTION_TOPS = 16;
    public static final int AVATAREDIT_OPTION_WRISTWEAR = 1024;
    public static final String AVATAR_NAME = "editoravatar";
    private static final int NOTIFY_AVATAR_EDITOR_INITIALIZED = 513;
    public static final String REMOVE_GUID = "00000000-0000-0000-0000-000000000000";
    private static AvatarEditorModel instance = null;
    private AvatarViewActorVMEditor avatarActorVM = new AvatarViewActorVMEditor();
    private AvatarViewVM avatarViewVM = new AvatarViewVMEditor();
    private AvatarDataObserver dataObserver = new AvatarDataObserver();

    public enum CameraType {
        CAMERA_TYPE_MAIN,
        CAMERA_TYPE_BODY,
        CAMERA_TYPE_PREVIEW
    }

    private class AvatarDataObserver implements XLEObserver<UpdateData> {
        private AvatarDataObserver() {
        }

        public void update(AsyncResult<UpdateData> asyncResult) {
            switch (((UpdateData) asyncResult.getResult()).getUpdateType()) {
                case AvatarManifestSave:
                    if (((UpdateData) asyncResult.getResult()).getIsFinal()) {
                        AvatarManifestModel.getPlayerModel().removeObserver(this);
                        AvatarEditorModel.this.notifyObservers(new AsyncResult(new UpdateData(UpdateType.AvatarEditorSave, true), this, asyncResult.getException()));
                        return;
                    }
                    return;
                default:
                    return;
            }
        }
    }

    private AvatarEditorModel() {
        final AvatarEditorModel thisptr = this;
        this.avatarActorVM.setNotifyLoadedUserData(new Runnable() {
            public void run() {
                AvatarEditorModel.this.notifyObservers(new AsyncResult(new UpdateData(UpdateType.AvatarEditorInitialize, true), thisptr, null));
            }
        });
        this.avatarActorVM.setNotifyInitializationError(new Runnable() {
            public void run() {
                AvatarEditorModel.this.notifyObservers(new AsyncResult(new UpdateData(UpdateType.AvatarEditorInitialize, true), thisptr, new XLEException(XLEErrorCode.FAILED_TO_LOAD_AVATAR_EDITOR)));
            }
        });
        this.avatarActorVM.setNotifyRuntimeCatastrophicError(new Runnable() {
            public void run() {
                AvatarEditorModel.this.notifyObservers(new AsyncResult(new UpdateData(UpdateType.AvatarEditorLoadedAsset, true), thisptr, new XLEException(XLEErrorCode.FAILED_UNEXPECTED_CORE2_RUNTIME_ERROR)));
            }
        });
        this.avatarActorVM.setNotifyLoadingAsset(new Runnable() {
            public void run() {
                AvatarEditorModel.this.notifyObservers(new AsyncResult(new UpdateData(UpdateType.AvatarEditorLoadingAsset, true), thisptr, null));
            }
        });
        this.avatarActorVM.setNotifyLoadedAsset(new NotifyLoadedAsset() {
            public void run(XLEException exception) {
                AvatarEditorModel.this.notifyObservers(new AsyncResult(new UpdateData(UpdateType.AvatarEditorLoadedAsset, true), this, exception));
            }
        });
        this.avatarViewVM.registerActor(this.avatarActorVM);
        XLEAssert.assertNotNull(this.avatarActorVM.getAvatarEditor());
    }

    public static AvatarEditorModel getInstance() {
        if (instance == null) {
            instance = new AvatarEditorModel();
        }
        return instance;
    }

    public boolean isBlocking() {
        return this.avatarActorVM.getBlocking() || AvatarManifestModel.getPlayerModel().getIsSaving();
    }

    private AvatarManifest getManifest() {
        if (this.avatarActorVM.getAvatarEditor() == null) {
            return null;
        }
        return this.avatarActorVM.getAvatarEditor().getManifest(AVATAR_NAME);
    }

    public AvatarManifestEditor getManifestEditor() {
        return this.avatarActorVM.getManifestEditor();
    }

    public String getManifestString() {
        boolean z = false;
        if (this.avatarActorVM.getAvatarEditor() == null) {
            return null;
        }
        String rv = this.avatarActorVM.getAvatarEditor().getHexManifest(AVATAR_NAME).substring(0, 2000);
        if (rv.length() == 2000) {
            z = true;
        }
        XLEAssert.assertTrue(z);
        return rv;
    }

    public boolean isMale() {
        AvatarManifest manifest = getManifest();
        if (manifest != null && manifest.getGender() == AVATAR_BODY_TYPE.MALE) {
            return true;
        }
        return false;
    }

    public boolean isModified() {
        if (this.avatarActorVM == null) {
            return false;
        }
        return this.avatarActorVM.getIsModified();
    }

    public boolean isCategoryEnabled(int categoryMask) {
        switch (categoryMask) {
            case AVATAREDIT_OPTION_COLOR_FACIAL_HAIR /*-2147483648*/:
            case AVATAREDIT_OPTION_COLOR_EYE /*33554432*/:
            case AVATAREDIT_OPTION_COLOR_HAIR /*134217728*/:
            case AVATAREDIT_OPTION_COLOR_FACIAL_FEATURE /*268435456*/:
            case AVATAREDIT_OPTION_COLOR_EYEBROW /*536870912*/:
            case AVATAREDIT_OPTION_COLOR_LIP /*1073741824*/:
                AvatarEditOptions options = this.avatarActorVM.getAvatarEditor().getEditOptions(AVATAR_NAME, categoryMask);
                if (options.getOptionsCount() > 0) {
                    return options.getOption(0).getIsEnabled().booleanValue();
                }
                break;
        }
        return true;
    }

    public void avatarEditorClearScene() {
        AvatarRendererModel.getInstance().purgeScene();
        this.avatarActorVM.avatarEditorClearScene();
    }

    public void avatarEditorInitialize() {
        XLEAssert.assertNotNull(AvatarManifestModel.getPlayerModel().getManifest());
        XLEAssert.assertNotNull(AvatarClosetModel.getPlayerModel().getClosetData());
        XLEAssert.assertNotNull(AvatarClosetModel.getStockModel().getClosetData());
        this.avatarActorVM.avatarEditorInitialize();
    }

    public void warpToIdle(boolean overrideOffscreen, boolean overrideEntering) {
        this.avatarActorVM.warpToIdle(overrideOffscreen, overrideEntering);
    }

    public void warpToOffscreen() {
        this.avatarActorVM.warpToOffscreen();
    }

    public void saveData() {
        boolean z;
        boolean z2 = true;
        if (Thread.currentThread() == ThreadManager.UIThread) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        if (isBlocking()) {
            z2 = false;
        }
        XLEAssert.assertTrue(z2);
        String newManifest = getManifestString();
        AvatarManifestModel.getPlayerModel().addObserver(this.dataObserver);
        AvatarManifestModel.getPlayerModel().save(newManifest);
        XLEAssert.assertTrue(isBlocking());
        notifyObservers(new AsyncResult(new UpdateData(UpdateType.AvatarEditorSave, false), this, null));
    }

    public AvatarViewVM getAvatarViewVM() {
        return this.avatarViewVM;
    }

    public AvatarViewActorVMEditor getAvatarActorVM() {
        return this.avatarActorVM;
    }

    public boolean isShadowtar() {
        return AvatarManifestModel.getPlayerModel().getManifest().Manifest == null;
    }

    public AvatarEditorOption[] getOptions(int categoryMask) {
        boolean z = categoryMask == AVATAREDIT_OPTION_COLOR_FACIAL_HAIR || XLEMath.isPowerOf2(categoryMask);
        XLEAssert.assertTrue(z);
        return wrapEditOptions(this.avatarActorVM.getAvatarEditor().getEditOptions(AVATAR_NAME, categoryMask), categoryMask);
    }

    public AvatarEditorOption[] getColorOptions(AvatarEditorOptionAsset option) {
        return wrapEditOptions(option.getColorOptions(), 0);
    }

    public void shutdownIfNecessary() {
        this.avatarActorVM.shutdownIfNecessary();
    }

    public void setCamera(CameraType type, int closetCategory, String assetId) {
        this.avatarActorVM.setCamera(type, closetCategory, assetId);
    }

    public void applyOption(AvatarEditorOption option) {
        if (option instanceof AvatarEditorOptionAsset) {
            AvatarEditorOptionAsset optionAsset = (AvatarEditorOptionAsset) option;
            this.avatarActorVM.internalApplyOption(optionAsset.getOption(), optionAsset.getClosetSpinAnimationType());
        } else if (option instanceof AvatarEditorOptionColor) {
            AvatarEditorOptionColor colorAsset = (AvatarEditorOptionColor) option;
            this.avatarActorVM.internalApplyOption(colorAsset.getOption(), colorAsset.getClosetSpinAnimationType());
        } else if (option instanceof AvatarEditorOptionNewAvatar) {
            this.avatarActorVM.internalApplyManifest(AvatarRendererModel.getInstance().getCore2Model().createManifestFromHex(((AvatarEditorOptionNewAvatar) option).getManifest()));
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public void revertOption() {
        this.avatarActorVM.revertOption();
    }

    public void commitOption() {
        this.avatarActorVM.commitOption();
    }

    public boolean wouldPreemptClosetSpinAnimation() {
        return this.avatarActorVM.wouldPreemptClosetSpinAnimation();
    }

    public void applyBodyTall() {
        this.avatarActorVM.applyBodyTall();
    }

    public void applyBodySmall() {
        this.avatarActorVM.applyBodySmall();
    }

    public void applyBodyThin() {
        this.avatarActorVM.applyBodyThin();
    }

    public void applyBodyFat() {
        this.avatarActorVM.applyBodyFat();
    }

    public void applyBodyNormal() {
        this.avatarActorVM.applyBodyNormal();
    }

    public boolean canApplyBodyThin() {
        return this.avatarActorVM.canApplyBodyThin();
    }

    public boolean canApplyBodyFat() {
        return this.avatarActorVM.canApplyBodyFat();
    }

    public boolean canApplyBodyTall() {
        return this.avatarActorVM.canApplyBodyTall();
    }

    public boolean canApplyBodySmall() {
        return this.avatarActorVM.canApplyBodySmall();
    }

    private static AvatarEditorOption[] wrapEditOptions(AvatarEditOptions rawOptions, int closetCategory) {
        AvatarEditorOption[] rv = new AvatarEditorOption[rawOptions.getOptionsCount()];
        for (int i = 0; i < rawOptions.getOptionsCount(); i++) {
            AvatarEditOption rawOption = rawOptions.getOption(i);
            if (rawOption != null) {
                if (rawOption.getIsColor().booleanValue()) {
                    rv[i] = new AvatarEditorOptionColor(rawOption);
                } else if (rawOption.getIsMarketPlaceAsset().booleanValue() || rawOption.getIsAward().booleanValue()) {
                    rv[i] = new AvatarEditorOptionAssetNonstock(rawOption, closetCategory);
                } else {
                    rv[i] = new AvatarEditorOptionAssetStock(rawOption, closetCategory);
                }
            }
        }
        return rv;
    }
}
