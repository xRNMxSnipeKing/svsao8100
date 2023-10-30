package com.microsoft.xbox.avatar.model;

import android.view.MotionEvent;
import com.microsoft.xbox.avatar.model.AvatarEditorModel.CameraType;
import com.microsoft.xbox.avatar.model.AvatarPositionCatalog.AvatarCameraPositionType;
import com.microsoft.xbox.avatar.model.AvatarPositionCatalog.AvatarPosition;
import com.microsoft.xbox.avatar.view.AvatarAnimationCatalog;
import com.microsoft.xbox.avatar.view.AvatarAnimationCatalog.AvatarClosetSpinAnimationType;
import com.microsoft.xbox.avatar.view.AvatarEditorOptionNewAvatar;
import com.microsoft.xbox.avatar.view.XLEAvatarAnimationAction;
import com.microsoft.xbox.service.model.AvatarClosetModel;
import com.microsoft.xbox.service.model.AvatarManifestModel;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaType;
import com.microsoft.xbox.service.model.serialization.XLEAvatarManifest;
import com.microsoft.xbox.service.network.managers.xblshared.CompanionSession;
import com.microsoft.xbox.toolkit.FixedSizeLinkedList;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.xle.app.XLEApplication;
import com.xbox.avatarrenderer.AvatarEditor.AvatarEditOption;
import com.xbox.avatarrenderer.AvatarEditor.AvatarEditOptions;
import com.xbox.avatarrenderer.AvatarEditor.AvatarEditor;
import com.xbox.avatarrenderer.AvatarEditor.AvatarEditorEditEvent;
import com.xbox.avatarrenderer.AvatarEditor.AvatarEditorFunctionSet;
import com.xbox.avatarrenderer.AvatarEditor.AvatarEditorScriptingHelper;
import com.xbox.avatarrenderer.Core2Callbacks;
import com.xbox.avatarrenderer.Core2Renderer.ANIMATION_CHAINING_MODE;
import com.xbox.avatarrenderer.Core2Renderer.AvatarEditorEventContext;
import com.xbox.avatarrenderer.Kernel.AvatarManifest;
import com.xbox.avatarrenderer.Kernel.AvatarManifestEditor;
import com.xbox.avatarrenderer.Kernel.KernelScriptingHelper;
import com.xbox.avatarrenderer.Kernel.ScriptException;
import com.xbox.avatarrenderer.Vector3;
import java.util.ArrayList;

public class AvatarViewActorVMEditor extends AvatarViewActorVM implements Core2Callbacks {
    private static final float BLEND_IN_TIME = 0.5f;
    private static final float BLEND_OUT_TIME = 0.5f;
    private static final int NOTIFY_APPLY = 768;
    private static final int NOTIFY_AVATAR_EDITOR_INITIALIZED = 513;
    private static final int NOTIFY_BEGIN_APPLY = 769;
    private static final int NOTIFY_END_APPLY = 770;
    private static final int NOTIFY_ENTERED_SCREEN = 256;
    private static final int NOTIFY_SET_MANIFEST = 771;
    private final float AVATAR_ROT_EPSILON = 0.03141593f;
    private AfterAssetApplyAnimation afterApplyAnimation = AfterAssetApplyAnimation.IDLE;
    private FixedSizeLinkedList<String> animationHistory = new FixedSizeLinkedList(20);
    private AvatarEditorAnimationState animationState = AvatarEditorAnimationState.OFFSCREEN;
    private AvatarManifest applyingManifest = null;
    private AvatarEditorModelState applyingModelState;
    private AvatarEditOption applyingOption = null;
    private AvatarEditorFunctionSet applyingOptionFunctionSet = null;
    private AvatarClosetSpinAnimation applyingOptionSpinAnimation = null;
    private AvatarEditor avatarEditor = null;
    private Vector3 avatarPos = new Vector3(0.0f, 0.0f, 0.0f);
    private Vector3 avatarRot = new Vector3(0.0f, 0.0f, 0.0f);
    private float cumulativeUserAvatarRot = 0.0f;
    private final int delayMS = 33;
    private boolean isAvatarAttached = false;
    private long lastRotationTime = 0;
    private AvatarEditorModelState manifestEditState = AvatarEditorModelState.UNINITIALIZED;
    private Runnable notifyInitializationError = null;
    private NotifyLoadedAsset notifyLoadedAsset = null;
    private Runnable notifyLoadedUserData = null;
    private Runnable notifyLoadingAsset = null;
    private Runnable notifyRuntimeCatastrophicError = null;
    private AvatarEditOption optionBodyFat = null;
    private AvatarEditOption optionBodyNormal = null;
    private AvatarEditOption optionBodySmall = null;
    private AvatarEditOption optionBodyTall = null;
    private AvatarEditOption optionBodyThin = null;
    private AvatarEditOption optionCommit = null;
    private AvatarEditOption optionPlayProp = null;
    private AvatarEditOption optionReset = null;
    private AvatarEditOption optionRevert = null;
    private float prevTouchX;
    private float prevTouchY;
    private ArrayList<String> runInAnimations;
    private ArrayList<String> runOutAnimations;

    private enum AfterAssetApplyAnimation {
        IDLE,
        PLAY_PROP,
        HIDE
    }

    private enum AvatarEditorAnimationState {
        OFFSCREEN,
        ENTERING,
        IDLE,
        PROP,
        EDITOR_STATE_MACHINE_IS_PLAYING_CLOSET_SPIN
    }

    private enum AvatarEditorModelState {
        UNINITIALIZED,
        LOADING_AVATAR_EDITOR,
        LOADING_USER_CLOSET,
        LOADED_USER_DATA,
        INITIALIZATION_ERROR,
        RUNTIME_CATASTROPHIC_ERROR,
        NOT_EDITING,
        ASSET_APPLY_LOADING_ANIMATING,
        ASSET_APPLY_LOADING_NO_ANIMATION,
        ASSET_APPLY_ANIMATING,
        ASSET_APPLY_CANCELLING,
        ASSET_APPLY_CANCELLING_AND_COMMITING_CANCEL_STAGE,
        ASSET_APPLY_CANCELLING_AND_COMMITING_APPLY_STAGE,
        ASSET_APPLY_CANCELLING_AND_COMMITING_COMMIT_STAGE,
        ASSET_APPLY_FAILED,
        ASSET_APPLY_SUCCEEDED,
        ASSET_APPLY_CANCEL_SUCCEEDED,
        MANIFEST_APPLY_LOADING
    }

    private enum BeforeAssetApplyAnimation {
        NONE,
        HIDE
    }

    public interface NotifyLoadedAsset {
        void run(XLEException xLEException);
    }

    public void animate(XLEAvatarAnimationAction action, int transitionMs) {
    }

    public void hitboxOnClick() {
    }

    public void hitboxOnTouch(MotionEvent event) {
        switch (event.getAction() & 255) {
            case 0:
                this.prevTouchX = event.getX();
                this.prevTouchY = event.getY();
                return;
            case 2:
                float dx = event.getX() - this.prevTouchX;
                float dy = event.getY() - this.prevTouchY;
                this.prevTouchX = event.getX();
                this.prevTouchY = event.getY();
                switch (this.animationState) {
                    case OFFSCREEN:
                    case ENTERING:
                        return;
                    default:
                        this.cumulativeUserAvatarRot += dx * (-6.2831855f / ((float) XboxApplication.MainActivity.getScreenWidth()));
                        updateSpinPosition();
                        return;
                }
            default:
                return;
        }
    }

    public void initializeActorSceneData(int index) {
        this.avatarEditor = AvatarRendererModel.getInstance().getCore2Model().createAvatarEditor();
    }

    public void initializeActorSpecificData(int align, Vector3 avatarPos, Vector3 avatarRot, boolean idleProp) {
    }

    public void onFinishAnimation() {
    }

    public void clearLastPlayedAnimations() {
        this.animationHistory.clear();
    }

    public String[] getLastPlayedAnimations() {
        return (String[]) this.animationHistory.toArray(new String[0]);
    }

    public boolean getIsLoaded() {
        return this.manifestEditState == AvatarEditorModelState.NOT_EDITING && this.animationState == AvatarEditorAnimationState.IDLE;
    }

    public boolean getIsMale() {
        return AvatarEditorModel.getInstance().isMale();
    }

    public AvatarManifestEditor getManifestEditor() {
        AvatarManifest manifest = this.avatarEditor.getManifest(AvatarEditorModel.AVATAR_NAME);
        if (manifest == null) {
            return null;
        }
        return AvatarRendererModel.getInstance().getCore2Model().createAvatarManifestEditor(manifest);
    }

    private void switchToAnimation(AvatarEditorAnimationState newState, boolean immediate) {
        if (this.animationState != newState) {
            onAnimStateChange(newState, immediate);
        }
    }

    private void onAnimStateChange(AvatarEditorAnimationState newState, boolean immediate) {
        boolean z;
        boolean z2 = true;
        if (Thread.currentThread() == ThreadManager.UIThread) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        switch (this.manifestEditState) {
            case INITIALIZATION_ERROR:
            case RUNTIME_CATASTROPHIC_ERROR:
                newState = AvatarEditorAnimationState.OFFSCREEN;
                break;
        }
        AvatarEditorAnimationState oldState = this.animationState;
        this.animationState = newState;
        XLELog.Info("AvatarEditorModel", "Set animation state to " + this.animationState);
        if (!AvatarRendererModel.getInstance().isDestroyed()) {
            boolean prependShowAvatar;
            KernelScriptingHelper helper = AvatarRendererModel.getInstance().getCore2Model().createKernelScriptingHelper();
            helper.setExceptionHandler(this);
            if (this.animationState == AvatarEditorAnimationState.OFFSCREEN || oldState != AvatarEditorAnimationState.OFFSCREEN) {
                prependShowAvatar = false;
            } else {
                prependShowAvatar = true;
            }
            if (prependShowAvatar) {
                addShowAvatarScript(helper);
            }
            switch (this.animationState) {
                case OFFSCREEN:
                    if (prependShowAvatar) {
                        z2 = false;
                    }
                    XLEAssert.assertTrue(z2);
                    addHideAvatarScript(helper);
                    break;
                case ENTERING:
                    AvatarScriptCatalog.getInstance().addEnterScript(helper, this, AvatarEditorModel.AVATAR_NAME, this.runInAnimations, this.avatarPos, this.avatarRot, 0.0f, 0.5f);
                    break;
                case EDITOR_STATE_MACHINE_IS_PLAYING_CLOSET_SPIN:
                    break;
                case IDLE:
                    switch (oldState) {
                        case ENTERING:
                            if (!immediate) {
                                AvatarScriptCatalog.getInstance().addIdleAnimationScript(helper, this, AvatarEditorModel.AVATAR_NAME, 0.5f, 0.5f, ANIMATION_CHAINING_MODE.ANIMATION_FINISHED);
                                break;
                            }
                            AvatarScriptCatalog.getInstance().addIdleAnimationScript(helper, this, AvatarEditorModel.AVATAR_NAME, 0.0f, 0.5f, ANIMATION_CHAINING_MODE.REPLACE);
                            break;
                        case EDITOR_STATE_MACHINE_IS_PLAYING_CLOSET_SPIN:
                            AvatarScriptCatalog.getInstance().addIdleAnimationScript(helper, this, AvatarEditorModel.AVATAR_NAME, 0.5f, 0.5f, ANIMATION_CHAINING_MODE.ANIMATION_FINISHED);
                            break;
                        default:
                            AvatarScriptCatalog.getInstance().addIdleAnimationScript(helper, this, AvatarEditorModel.AVATAR_NAME, 0.5f, 0.5f, ANIMATION_CHAINING_MODE.REPLACE);
                            break;
                    }
                case PROP:
                    addPropAnimationScript(helper);
                    break;
                default:
                    XLEAssert.assertTrue(false);
                    break;
            }
            helper.execute();
        }
    }

    public boolean getIsModified() {
        return !JavaUtil.stringsEqualCaseInsensitive(AvatarManifestModel.getPlayerModel().getManifest().Manifest, this.avatarEditor.getHexManifest(AvatarEditorModel.AVATAR_NAME));
    }

    private void onStateChange(AvatarEditorModelState newState) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        switch (this.manifestEditState) {
            case INITIALIZATION_ERROR:
            case RUNTIME_CATASTROPHIC_ERROR:
                return;
            default:
                this.manifestEditState = newState;
                XLELog.Info("AvatarEditorModel", "Set editor state to " + this.manifestEditState);
                if (!AvatarRendererModel.getInstance().isDestroyed()) {
                    switch (AnonymousClass5.$SwitchMap$com$microsoft$xbox$avatar$model$AvatarViewActorVMEditor$AvatarEditorModelState[this.manifestEditState.ordinal()]) {
                        case 1:
                            notifyInitializationError();
                            onStateChange(AvatarEditorModelState.NOT_EDITING);
                            return;
                        case 2:
                            notifyRuntimeCatastrophicError();
                            return;
                        case 3:
                        case EDSV2MediaType.MEDIATYPE_XBOX360GAMECONTENT /*18*/:
                            return;
                        case 4:
                            if (runAvatarEditorInitializeScript() < 0) {
                                onStateChange(AvatarEditorModelState.INITIALIZATION_ERROR);
                                return;
                            }
                            return;
                        case 5:
                            if (loadUserClosetIntoAvatarEditor(AvatarClosetModel.getStockModel().getClosetData(), AvatarClosetModel.getPlayerModel().getClosetData()) < 0) {
                                onStateChange(AvatarEditorModelState.INITIALIZATION_ERROR);
                                return;
                            } else {
                                onStateChange(AvatarEditorModelState.LOADED_USER_DATA);
                                return;
                            }
                        case 6:
                            notifyLoadedUserData();
                            if (AvatarEditorModel.getInstance().isShadowtar()) {
                                switchToAnimation(AvatarEditorAnimationState.OFFSCREEN, false);
                            } else {
                                switchToAnimation(AvatarEditorAnimationState.ENTERING, false);
                            }
                            onStateChange(AvatarEditorModelState.NOT_EDITING);
                            return;
                        case 7:
                            runAvatarApplyLoad();
                            return;
                        case 8:
                            runAvatarApplyLoadNoAnimation();
                            return;
                        case 9:
                            switchToAnimation(AvatarEditorAnimationState.EDITOR_STATE_MACHINE_IS_PLAYING_CLOSET_SPIN, false);
                            runAvatarApplyAnimate();
                            return;
                        case 10:
                            runAvatarApplyFailed();
                            return;
                        case 11:
                            runAvatarApplySucceeded();
                            return;
                        case CompanionSession.LRCERROR_TOO_MANY_CLIENTS /*12*/:
                            runAvatarApplyCancelSucceeded();
                            return;
                        case CompanionSession.LRCERROR_EXPIRED_COMMAND /*13*/:
                            runAvatarApplyCancel();
                            return;
                        case CompanionSession.LRCERROR_NO_EXCLUSIVE /*14*/:
                            runAvatarApplyCancelAndCommitCancelStage();
                            return;
                        case 15:
                            runAvatarApplyCancelAndCommitApplyStage();
                            return;
                        case 16:
                            runAvatarApplyCancelAndCommitCommitStage();
                            return;
                        case CompanionSession.LRCERROR_TITLECHANNEL_EXISTS /*17*/:
                            switchToAnimation(AvatarEditorAnimationState.OFFSCREEN, false);
                            runManifestApplyLoad();
                            return;
                        default:
                            XLEAssert.assertTrue(false);
                            return;
                    }
                }
                return;
        }
    }

    public AvatarEditor getAvatarEditor() {
        return this.avatarEditor;
    }

    public void avatarEditorClearScene() {
        this.manifestEditState = AvatarEditorModelState.UNINITIALIZED;
    }

    public boolean getBlocking() {
        switch (AnonymousClass5.$SwitchMap$com$microsoft$xbox$avatar$model$AvatarViewActorVMEditor$AvatarEditorModelState[this.manifestEditState.ordinal()]) {
            case 7:
            case 8:
            case CompanionSession.LRCERROR_EXPIRED_COMMAND /*13*/:
            case CompanionSession.LRCERROR_NO_EXCLUSIVE /*14*/:
            case 15:
            case 16:
            case CompanionSession.LRCERROR_TITLECHANNEL_EXISTS /*17*/:
                return true;
            default:
                return false;
        }
    }

    public void avatarEditorInitialize() {
        if (!AvatarRendererModel.getInstance().isDestroyed()) {
            onStateChange(AvatarEditorModelState.LOADING_AVATAR_EDITOR);
        }
    }

    private int runAvatarEditorInitializeScript() {
        KernelScriptingHelper helper = AvatarRendererModel.getInstance().getCore2Model().createKernelScriptingHelper();
        if (helper == null) {
            return -1;
        }
        int hr = helper.setExceptionHandler(this);
        if (hr < 0) {
            return hr;
        }
        AvatarEditorScriptingHelper editorScriptingHelper = AvatarRendererModel.getInstance().getCore2Model().createAvatarEditorScriptingHelper(helper);
        if (editorScriptingHelper == null) {
            return -1;
        }
        String hexManifest;
        XLEAvatarManifest manifestData = AvatarManifestModel.getPlayerModel().getManifest();
        if (AvatarEditorModel.getInstance().isShadowtar()) {
            hexManifest = AvatarEditorOptionNewAvatar.getStockAvatarManifest();
        } else {
            hexManifest = manifestData.Manifest;
        }
        AvatarManifest avManifest = AvatarRendererModel.getInstance().getCore2Model().createManifestFromHex(hexManifest);
        hr = AvatarScriptCatalog.getInstance().addAvatar(helper, avManifest, AvatarEditorModel.AVATAR_NAME);
        if (hr < 0) {
            return hr;
        }
        this.animationState = AvatarEditorAnimationState.OFFSCREEN;
        hr = helper.setLocalTransform(AvatarEditorModel.AVATAR_NAME, new Vector3(0.0f, 0.0f, 0.0f), new Vector3(0.0f, 0.0f, 0.0f), new Vector3(0.0f, 0.0f, 0.0f));
        if (hr < 0) {
            return hr;
        }
        this.isAvatarAttached = true;
        hr = editorScriptingHelper.attachAvatar(AvatarEditorModel.AVATAR_NAME, avManifest, this.avatarEditor);
        if (hr < 0) {
            return hr;
        }
        hr = this.avatarEditor.setEventHandler(this);
        if (hr < 0) {
            return hr;
        }
        this.runInAnimations = AvatarAnimationCatalog.RUN_IN_ANIMATIONS_RIGHT;
        this.runOutAnimations = AvatarAnimationCatalog.RUN_OUT_ANIMATIONS_RIGHT;
        hr = helper.loadAnimation(AvatarAnimationCatalog.IDLE_ANIMATION);
        if (hr < 0) {
            return hr;
        }
        int i;
        for (i = 0; i < this.runInAnimations.size(); i++) {
            hr = helper.loadAnimation((String) this.runInAnimations.get(i));
            if (hr < 0) {
                return hr;
            }
        }
        for (i = 0; i < this.runOutAnimations.size(); i++) {
            hr = helper.loadAnimation((String) this.runOutAnimations.get(i));
            if (hr < 0) {
                return hr;
            }
        }
        for (String loadAnimation : AvatarAnimationCatalog.CLOSET_SPIN_ANIMATIONS) {
            hr = helper.loadAnimation(loadAnimation);
            if (hr < 0) {
                return hr;
            }
        }
        hr = helper.addNotifier(NOTIFY_AVATAR_EDITOR_INITIALIZED, this);
        if (hr < 0) {
            return hr;
        }
        hr = helper.execute();
        if (hr < 0) {
            return hr;
        }
        return 0;
    }

    private void addShowAvatarScript(KernelScriptingHelper helper) {
        XLEAssert.assertTrue(helper.setLocalTransform(AvatarEditorModel.AVATAR_NAME, this.avatarPos, this.avatarRot, new Vector3(1.0f, 1.0f, 1.0f)) == 0);
    }

    private void addPropAnimationScript(KernelScriptingHelper helper) {
        AvatarManifest manifest = this.avatarEditor.getManifest(AvatarEditorModel.AVATAR_NAME);
        if (manifest.getHasCarryable().booleanValue()) {
            AvatarScriptCatalog.getInstance().playPropAnimationInternal(helper, this, AvatarEditorModel.AVATAR_NAME, manifest.getCarryableGuid(), null, "PropAnim", "PropList", 0.5f);
        }
        helper.addNotifier(AvatarScriptCatalog.NOTIFY_PROP_ANIMATION_COMPLETE, this);
    }

    private void addHideAvatarScript(KernelScriptingHelper helper) {
        boolean z = true;
        XLEAssert.assertTrue(helper.setLocalTransform(AvatarEditorModel.AVATAR_NAME, this.avatarPos, this.avatarRot, new Vector3(0.0f, 0.0f, 0.0f)) == 0);
        if (AvatarScriptCatalog.getInstance().playAnimationInternal(helper, this, AvatarEditorModel.AVATAR_NAME, AvatarAnimationCatalog.IDLE_ANIMATION, 0.0f, 0.5f, ANIMATION_CHAINING_MODE.REPLACE) != 0) {
            z = false;
        }
        XLEAssert.assertTrue(z);
    }

    public void warpToIdle(boolean overrideOffscreen, boolean overrideEntering) {
        boolean warp = true;
        if (!overrideOffscreen && this.animationState == AvatarEditorAnimationState.OFFSCREEN) {
            warp = false;
        }
        if (!overrideEntering && this.animationState == AvatarEditorAnimationState.ENTERING) {
            warp = false;
        }
        if (warp) {
            switchToAnimation(AvatarEditorAnimationState.IDLE, true);
        }
    }

    public void warpToOffscreen() {
        switchToAnimation(AvatarEditorAnimationState.OFFSCREEN, true);
    }

    public int loadUserClosetIntoAvatarEditor(byte[] stockData, byte[] closetData) {
        int hr = this.avatarEditor.initializeStockAssets(stockData);
        if (hr < 0) {
            return hr;
        }
        hr = this.avatarEditor.initializeDynamicAssets(AvatarEditorModel.AVATAR_NAME, closetData);
        if (hr < 0) {
            return hr;
        }
        AvatarEditOptions specialOptions = this.avatarEditor.getEditOptions(AvatarEditorModel.AVATAR_NAME, 2);
        this.optionPlayProp = specialOptions.getOption(0);
        this.optionReset = specialOptions.getOption(1);
        this.optionRevert = specialOptions.getOption(2);
        this.optionCommit = specialOptions.getOption(3);
        AvatarEditOptions bodyOptions = this.avatarEditor.getEditOptions(AvatarEditorModel.AVATAR_NAME, 1);
        this.optionBodyTall = bodyOptions.getOption(0);
        this.optionBodySmall = bodyOptions.getOption(1);
        this.optionBodyFat = bodyOptions.getOption(2);
        this.optionBodyThin = bodyOptions.getOption(3);
        this.optionBodyNormal = bodyOptions.getOption(4);
        return 0;
    }

    public void onNotify(final int iContext) {
        final int sceneIndexOnPost = AvatarRendererModel.getInstance().getSceneIndex();
        ThreadManager.UIThreadPost(new Runnable() {
            public void run() {
                if (AvatarRendererModel.getInstance().getSceneIndex() == sceneIndexOnPost) {
                    switch (iContext) {
                        case 256:
                            if (AvatarViewActorVMEditor.this.animationState == AvatarEditorAnimationState.ENTERING) {
                                AvatarViewActorVMEditor.this.onAnimStateChange(AvatarEditorAnimationState.IDLE, false);
                                return;
                            }
                            return;
                        case AvatarScriptCatalog.NOTIFY_IDLE_ANIMATION /*262*/:
                            if (AvatarViewActorVMEditor.this.animationState == AvatarEditorAnimationState.IDLE) {
                                AvatarViewActorVMEditor.this.onAnimStateChange(AvatarEditorAnimationState.IDLE, false);
                                return;
                            }
                            return;
                        case AvatarScriptCatalog.NOTIFY_PROP_ANIMATION_COMPLETE /*264*/:
                            if (AvatarViewActorVMEditor.this.animationState == AvatarEditorAnimationState.PROP) {
                                AvatarViewActorVMEditor.this.onAnimStateChange(AvatarEditorAnimationState.PROP, false);
                                return;
                            }
                            return;
                        case AvatarViewActorVMEditor.NOTIFY_AVATAR_EDITOR_INITIALIZED /*513*/:
                            AvatarViewActorVMEditor.this.onStateChange(AvatarEditorModelState.LOADING_USER_CLOSET);
                            return;
                        default:
                            AvatarViewActorVMEditor.this.animationHistory.push(AvatarScriptCatalog.getInstance().getCurrentlyPlayingAnimation(AvatarEditorModel.AVATAR_NAME, iContext));
                            return;
                    }
                }
            }
        });
    }

    public void invokeAvatarEditorEditEvent(AvatarEditorEditEvent aeee) {
        final int errorCode = aeee.getErrorCode();
        final int tag = aeee.getTag();
        final AvatarEditorEventContext eventContext = aeee.getEventContext();
        final int sceneIndexOnPost = AvatarRendererModel.getInstance().getSceneIndex();
        ThreadManager.UIThreadPost(new Runnable() {
            public void run() {
                if (AvatarRendererModel.getInstance().getSceneIndex() == sceneIndexOnPost) {
                    boolean error;
                    if (errorCode < 0) {
                        error = true;
                    } else {
                        error = false;
                    }
                    XLELog.Diagnostic("AvatarEditorModel", String.format("editor: error %d, tag %d, eventContext: %s", new Object[]{Integer.valueOf(errorCode), Integer.valueOf(tag), eventContext.toString()}));
                    switch (AnonymousClass5.$SwitchMap$com$microsoft$xbox$avatar$model$AvatarViewActorVMEditor$AvatarEditorModelState[AvatarViewActorVMEditor.this.manifestEditState.ordinal()]) {
                        case 7:
                            if (error) {
                                AvatarViewActorVMEditor.this.onStateChange(AvatarEditorModelState.ASSET_APPLY_FAILED);
                                return;
                            } else if (tag == AvatarViewActorVMEditor.NOTIFY_BEGIN_APPLY && eventContext == AvatarEditorEventContext.AVATAR_PRELOADED) {
                                AvatarViewActorVMEditor.this.onStateChange(AvatarEditorModelState.ASSET_APPLY_ANIMATING);
                                return;
                            } else {
                                return;
                            }
                        case 8:
                            if (error) {
                                AvatarViewActorVMEditor.this.onStateChange(AvatarEditorModelState.ASSET_APPLY_FAILED);
                                return;
                            } else if (tag == AvatarViewActorVMEditor.NOTIFY_APPLY && eventContext == AvatarEditorEventContext.AVATAR_UPDATED) {
                                AvatarViewActorVMEditor.this.onStateChange(AvatarEditorModelState.ASSET_APPLY_SUCCEEDED);
                                return;
                            } else {
                                return;
                            }
                        case 9:
                            if (error) {
                                AvatarViewActorVMEditor.this.onStateChange(AvatarEditorModelState.ASSET_APPLY_FAILED);
                                return;
                            } else if (tag == AvatarViewActorVMEditor.NOTIFY_END_APPLY && eventContext == AvatarEditorEventContext.AVATAR_UPDATED) {
                                AvatarViewActorVMEditor.this.onStateChange(AvatarEditorModelState.ASSET_APPLY_SUCCEEDED);
                                return;
                            } else {
                                return;
                            }
                        case CompanionSession.LRCERROR_EXPIRED_COMMAND /*13*/:
                            if (error) {
                                if (eventContext != AvatarEditorEventContext.AVATAR_UPDATE_CANCELED) {
                                    AvatarViewActorVMEditor.this.onStateChange(AvatarEditorModelState.ASSET_APPLY_FAILED);
                                    return;
                                }
                                return;
                            } else if (eventContext == AvatarEditorEventContext.AVATAR_UPDATED) {
                                AvatarViewActorVMEditor.this.onStateChange(AvatarEditorModelState.ASSET_APPLY_CANCEL_SUCCEEDED);
                                return;
                            } else {
                                return;
                            }
                        case CompanionSession.LRCERROR_NO_EXCLUSIVE /*14*/:
                            if (error) {
                                if (eventContext != AvatarEditorEventContext.AVATAR_UPDATE_CANCELED) {
                                    AvatarViewActorVMEditor.this.onStateChange(AvatarEditorModelState.ASSET_APPLY_FAILED);
                                    return;
                                }
                                return;
                            } else if (eventContext == AvatarEditorEventContext.AVATAR_UPDATED) {
                                AvatarViewActorVMEditor.this.onStateChange(AvatarEditorModelState.ASSET_APPLY_CANCELLING_AND_COMMITING_APPLY_STAGE);
                                return;
                            } else {
                                return;
                            }
                        case 15:
                            if (error) {
                                AvatarViewActorVMEditor.this.onStateChange(AvatarEditorModelState.ASSET_APPLY_FAILED);
                                return;
                            } else if (tag == AvatarViewActorVMEditor.NOTIFY_APPLY && eventContext == AvatarEditorEventContext.AVATAR_UPDATED) {
                                AvatarViewActorVMEditor.this.onStateChange(AvatarEditorModelState.ASSET_APPLY_CANCELLING_AND_COMMITING_COMMIT_STAGE);
                                return;
                            } else {
                                return;
                            }
                        case CompanionSession.LRCERROR_TITLECHANNEL_EXISTS /*17*/:
                            if (error) {
                                AvatarViewActorVMEditor.this.onStateChange(AvatarEditorModelState.ASSET_APPLY_FAILED);
                                return;
                            } else if (tag == AvatarViewActorVMEditor.NOTIFY_SET_MANIFEST && eventContext == AvatarEditorEventContext.AVATAR_UPDATED) {
                                AvatarViewActorVMEditor.this.onStateChange(AvatarEditorModelState.ASSET_APPLY_SUCCEEDED);
                                return;
                            } else {
                                return;
                            }
                        case EDSV2MediaType.MEDIATYPE_XBOX360GAMECONTENT /*18*/:
                            return;
                        default:
                            XLELog.Error("AvatarEditorModel", "Shouldn't be in state: " + AvatarViewActorVMEditor.this.manifestEditState);
                            throw new UnsupportedOperationException();
                    }
                }
            }
        });
    }

    public void invokeScriptException(ScriptException scriptException) {
        final int errorCode = scriptException.getErrorCode();
        final int sceneIndexOnPost = AvatarRendererModel.getInstance().getSceneIndex();
        ThreadManager.UIThreadPost(new Runnable() {
            public void run() {
                if (AvatarRendererModel.getInstance().getSceneIndex() == sceneIndexOnPost) {
                    XLELog.Warning("AvatarEditorModel", "Script exception while in editor state: " + AvatarViewActorVMEditor.this.manifestEditState);
                    XLELog.Warning("AvatarEditorModel", "Script exception error code: " + errorCode);
                    switch (AvatarViewActorVMEditor.this.manifestEditState) {
                        case INITIALIZATION_ERROR:
                        case LOADING_AVATAR_EDITOR:
                        case LOADING_USER_CLOSET:
                        case LOADED_USER_DATA:
                            AvatarViewActorVMEditor.this.onStateChange(AvatarEditorModelState.INITIALIZATION_ERROR);
                            return;
                        default:
                            AvatarViewActorVMEditor.this.onStateChange(AvatarEditorModelState.RUNTIME_CATASTROPHIC_ERROR);
                            return;
                    }
                }
            }
        });
    }

    public void revertOption() {
        switch (AnonymousClass5.$SwitchMap$com$microsoft$xbox$avatar$model$AvatarViewActorVMEditor$AvatarEditorModelState[this.manifestEditState.ordinal()]) {
            case 9:
                onStateChange(AvatarEditorModelState.ASSET_APPLY_CANCELLING);
                return;
            case EDSV2MediaType.MEDIATYPE_XBOX360GAMECONTENT /*18*/:
                internalApplyOptionNotAnimated(this.optionRevert, BeforeAssetApplyAnimation.HIDE, AfterAssetApplyAnimation.HIDE);
                return;
            default:
                throw new UnsupportedOperationException();
        }
    }

    public void commitOption() {
        switch (AnonymousClass5.$SwitchMap$com$microsoft$xbox$avatar$model$AvatarViewActorVMEditor$AvatarEditorModelState[this.manifestEditState.ordinal()]) {
            case 9:
                onStateChange(AvatarEditorModelState.ASSET_APPLY_CANCELLING_AND_COMMITING_CANCEL_STAGE);
                return;
            case 16:
            case EDSV2MediaType.MEDIATYPE_XBOX360GAMECONTENT /*18*/:
                internalApplyOptionNotAnimated(this.optionCommit, BeforeAssetApplyAnimation.NONE, AfterAssetApplyAnimation.IDLE);
                return;
            default:
                return;
        }
    }

    public boolean wouldPreemptClosetSpinAnimation() {
        return this.manifestEditState != AvatarEditorModelState.NOT_EDITING;
    }

    public void applyBodyTall() {
        internalApplyOptionNotAnimated(this.optionBodyTall, BeforeAssetApplyAnimation.NONE, AfterAssetApplyAnimation.IDLE);
    }

    public void applyBodySmall() {
        internalApplyOptionNotAnimated(this.optionBodySmall, BeforeAssetApplyAnimation.NONE, AfterAssetApplyAnimation.IDLE);
    }

    public void applyBodyThin() {
        internalApplyOptionNotAnimated(this.optionBodyThin, BeforeAssetApplyAnimation.NONE, AfterAssetApplyAnimation.IDLE);
    }

    public void applyBodyFat() {
        internalApplyOptionNotAnimated(this.optionBodyFat, BeforeAssetApplyAnimation.NONE, AfterAssetApplyAnimation.IDLE);
    }

    public void applyBodyNormal() {
        internalApplyOptionNotAnimated(this.optionBodyNormal, BeforeAssetApplyAnimation.NONE, AfterAssetApplyAnimation.IDLE);
    }

    public boolean canApplyBodyThin() {
        return this.optionBodyThin.getIsEnabled().booleanValue();
    }

    public boolean canApplyBodyFat() {
        return this.optionBodyFat.getIsEnabled().booleanValue();
    }

    public boolean canApplyBodyTall() {
        return this.optionBodyTall.getIsEnabled().booleanValue();
    }

    public boolean canApplyBodySmall() {
        return this.optionBodySmall.getIsEnabled().booleanValue();
    }

    public void internalApplyOption(AvatarEditOption option, AvatarClosetSpinAnimationType spinAnimationType) {
        AvatarClosetSpinAnimation spinAnimation = AvatarAnimationCatalog.CLOSET_SPIN_ANIMATION_DESCRIPTORS[spinAnimationType.ordinal()];
        if (spinAnimation == null) {
            internalApplyOptionNotAnimated(option, BeforeAssetApplyAnimation.NONE, spinAnimationType == AvatarClosetSpinAnimationType.Prop ? AfterAssetApplyAnimation.PLAY_PROP : AfterAssetApplyAnimation.IDLE);
        } else {
            internalApplyOptionAnimated(option, spinAnimation);
        }
    }

    private boolean shouldReplaceOccluders(String guid) {
        return !XLEManifestUtils.isRingOrWristwear(guid);
    }

    private void runAvatarApplyLoad() {
        boolean replaceOccluders = shouldReplaceOccluders(this.applyingOption.getAssetGuid());
        this.applyingOptionFunctionSet = AvatarRendererModel.getInstance().getCore2Model().createAvatarEditorFunctionSet(this.avatarEditor, AvatarEditorModel.AVATAR_NAME);
        this.applyingOptionFunctionSet.beginApply(this.applyingOption, Boolean.valueOf(replaceOccluders), NOTIFY_BEGIN_APPLY);
        notifyLoadingAsset();
    }

    private void runAvatarApplyLoadNoAnimation() {
        this.avatarEditor.apply(this.applyingOption, Boolean.valueOf(shouldReplaceOccluders(this.applyingOption.getAssetGuid())), NOTIFY_APPLY);
        this.applyingOption = null;
        notifyLoadingAsset();
    }

    private void runAvatarApplyFailed() {
        onStateChange(AvatarEditorModelState.NOT_EDITING);
        notifyLoadedAsset(new XLEException(XLEErrorCode.FAILED_TO_GET_AVATAR));
    }

    private void runAvatarApplySucceeded() {
        switch (this.afterApplyAnimation) {
            case IDLE:
                switchToAnimation(AvatarEditorAnimationState.IDLE, false);
                break;
            case PLAY_PROP:
                switchToAnimation(AvatarEditorAnimationState.PROP, false);
                break;
            case HIDE:
                onAnimStateChange(AvatarEditorAnimationState.OFFSCREEN, true);
                break;
        }
        onStateChange(AvatarEditorModelState.NOT_EDITING);
        notifyLoadedAsset(null);
    }

    private void runAvatarApplyCancelSucceeded() {
        switchToAnimation(AvatarEditorAnimationState.OFFSCREEN, true);
        onStateChange(AvatarEditorModelState.NOT_EDITING);
        notifyLoadedAsset(null);
    }

    private void runAvatarApplyCancel() {
        this.avatarEditor.cancel(AvatarEditorModel.AVATAR_NAME);
        notifyLoadingAsset();
    }

    private void runAvatarApplyCancelAndCommitCancelStage() {
        this.avatarEditor.cancel(AvatarEditorModel.AVATAR_NAME);
        notifyLoadingAsset();
    }

    private void runAvatarApplyCancelAndCommitApplyStage() {
        boolean z;
        if (this.applyingOption != null) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        this.avatarEditor.apply(this.applyingOption, Boolean.valueOf(false), NOTIFY_APPLY);
        notifyLoadingAsset();
    }

    private void runAvatarApplyCancelAndCommitCommitStage() {
        commitOption();
    }

    private void runAvatarApplyAnimate() {
        boolean z;
        if (this.applyingOptionSpinAnimation != null) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        String spinAnimationGuid = this.applyingOptionSpinAnimation.getAnimationGuid();
        float spinAnimationSleepTime = this.applyingOptionSpinAnimation.getAnimationSleepTime();
        float f = 0.5f;
        this.applyingOptionFunctionSet.playAnimation(spinAnimationGuid, 0.5f, f, ANIMATION_CHAINING_MODE.REPLACE, "temp1");
        KernelScriptingHelper helper = AvatarRendererModel.getInstance().getCore2Model().createKernelScriptingHelper(this.avatarEditor.getEditStory(AvatarEditorModel.AVATAR_NAME).getStory(0));
        AvatarScriptCatalog.getInstance().addAnimationStartEvent(helper, this, spinAnimationGuid);
        helper.sleep(spinAnimationSleepTime);
        this.applyingOptionFunctionSet.endApply(this.applyingOption, NOTIFY_END_APPLY);
        this.applyingOptionSpinAnimation = null;
        notifyLoadingAsset();
    }

    public void runManifestApplyLoad() {
        this.avatarEditor.setManifest(AvatarEditorModel.AVATAR_NAME, this.applyingManifest, NOTIFY_SET_MANIFEST);
        this.applyingManifest = null;
        notifyLoadingAsset();
    }

    private void internalApplyOptionAnimated(AvatarEditOption option, AvatarClosetSpinAnimation spinAnimation) {
        boolean z = true;
        XLELog.Info("AvatarEditorModel", "internalApplyOptionAnimated");
        XLEAssert.assertTrue(spinAnimation != null);
        if (this.manifestEditState != AvatarEditorModelState.NOT_EDITING) {
            z = false;
        }
        XLEAssert.assertTrue(z);
        this.applyingOptionSpinAnimation = spinAnimation;
        this.applyingOption = option;
        this.afterApplyAnimation = AfterAssetApplyAnimation.IDLE;
        this.applyingModelState = AvatarEditorModelState.ASSET_APPLY_LOADING_ANIMATING;
        onStateChange(this.applyingModelState);
    }

    private void internalApplyOptionNotAnimated(AvatarEditOption option, BeforeAssetApplyAnimation beforeAnimation, AfterAssetApplyAnimation afterAnimation) {
        XLELog.Info("AvatarEditorModel", "internalApplyOptionNotAnimated");
        boolean z = this.manifestEditState == AvatarEditorModelState.NOT_EDITING || this.manifestEditState == AvatarEditorModelState.ASSET_APPLY_CANCELLING_AND_COMMITING_COMMIT_STAGE;
        XLEAssert.assertTrue(z);
        this.applyingOption = option;
        this.afterApplyAnimation = afterAnimation;
        this.applyingModelState = AvatarEditorModelState.ASSET_APPLY_LOADING_NO_ANIMATION;
        switch (beforeAnimation) {
            case HIDE:
                onAnimStateChange(AvatarEditorAnimationState.OFFSCREEN, true);
                break;
        }
        onStateChange(this.applyingModelState);
    }

    public void internalApplyManifest(AvatarManifest avManifest) {
        XLELog.Info("AvatarEditorModel", "internalApplyManifest");
        XLEAssert.assertTrue(this.manifestEditState == AvatarEditorModelState.NOT_EDITING);
        this.applyingManifest = avManifest;
        this.afterApplyAnimation = AfterAssetApplyAnimation.IDLE;
        this.applyingModelState = AvatarEditorModelState.MANIFEST_APPLY_LOADING;
        onStateChange(this.applyingModelState);
    }

    public void onSceneBegin() {
    }

    public void onSceneEnd() {
    }

    public void shutdownIfNecessary() {
        if (this.isAvatarAttached) {
            KernelScriptingHelper helper = AvatarRendererModel.getInstance().getCore2Model().createKernelScriptingHelper();
            if (helper != null) {
                AvatarEditorScriptingHelper editorScriptingHelper = AvatarRendererModel.getInstance().getCore2Model().createAvatarEditorScriptingHelper(helper);
                if (editorScriptingHelper != null) {
                    int hr = editorScriptingHelper.detachAvatar(AvatarEditorModel.AVATAR_NAME);
                    if (hr < 0) {
                        XLELog.Warning("AvatarEditorModel", "warning, couldn't detach cleanly: " + hr);
                    }
                    helper.executeSynchronously();
                    if (hr < 0) {
                        XLELog.Warning("AvatarEditorModel", "warning, couldn't detach cleanly: " + hr);
                    }
                }
            }
        }
        this.isAvatarAttached = false;
        super.onDestroy();
    }

    public void setCamera(CameraType type, int closetCategory, String assetId) {
        AvatarCameraPositionType currentPositionType;
        this.cumulativeUserAvatarRot = 0.0f;
        switch (type) {
            case CAMERA_TYPE_BODY:
                currentPositionType = AvatarCameraPositionType.BodyResize;
                break;
            case CAMERA_TYPE_MAIN:
                currentPositionType = AvatarCameraPositionType.Main;
                break;
            case CAMERA_TYPE_PREVIEW:
                currentPositionType = AvatarPositionCatalog.getAvatarPositionType(closetCategory, assetId);
                break;
            default:
                throw new UnsupportedOperationException();
        }
        Vector3 cameraPos = new Vector3(0.5f, 0.75f, -2.0f);
        AvatarPosition currentPosition = AvatarPositionCatalog.getAvatarPosition(currentPositionType);
        this.avatarPos = new Vector3(currentPosition.getPos().x, currentPosition.getPos().y, (XLEApplication.Instance.getIsTablet() ? 0.5f : 0.0f) + currentPosition.getPos().z);
        this.avatarRot = new Vector3(currentPosition.getRot().x, currentPosition.getRot().y, currentPosition.getRot().z);
        String CAMERA_NAME = "avatarViewCamera";
        KernelScriptingHelper helper = AvatarRendererModel.getInstance().getCore2Model().createKernelScriptingHelper();
        helper.setExceptionHandler(this);
        if (helper.createCamera("avatarViewCamera") >= 0 && helper.addObjectToScene("avatarViewCamera", "avatarViewCamera") >= 0 && helper.setLocalTransform("avatarViewCamera", cameraPos, new Vector3(0.0f, 0.0f, 0.0f), new Vector3(1.0f, 1.0f, 1.0f)) >= 0 && helper.setCameraFieldOfView("avatarViewCamera", 0.7853982f) >= 0 && helper.setActiveCamera("avatarViewCamera", null) >= 0) {
            Vector3 scale;
            if (this.animationState == AvatarEditorAnimationState.OFFSCREEN) {
                scale = new Vector3(0.0f, 0.0f, 0.0f);
            } else {
                scale = new Vector3(1.0f, 1.0f, 1.0f);
            }
            if (helper.setLocalTransform(AvatarEditorModel.AVATAR_NAME, this.avatarPos, this.avatarRot, scale) >= 0) {
                helper.execute();
            }
        }
    }

    public float getCurrentRotation() {
        return this.avatarRot.y;
    }

    private void updateSpinPosition() {
        long elapsedMs = System.currentTimeMillis() - this.lastRotationTime;
        this.lastRotationTime = System.currentTimeMillis();
        float elapsedTime = ((float) Math.min(elapsedMs, 33)) / 1000.0f;
        Vector3 vector3;
        if (Math.abs(this.cumulativeUserAvatarRot) > 0.03141593f) {
            float decay = this.cumulativeUserAvatarRot * (Math.min(6.0f * elapsedTime, 0.1f) * 2.0f);
            vector3 = this.avatarRot;
            vector3.y += decay;
            this.cumulativeUserAvatarRot -= decay;
            ThreadManager.UIThreadPostDelayed(new Runnable() {
                public void run() {
                    AvatarViewActorVMEditor.this.updateSpinPosition();
                }
            }, 33);
        } else {
            vector3 = this.avatarRot;
            vector3.y += this.cumulativeUserAvatarRot;
            this.cumulativeUserAvatarRot = 0.0f;
        }
        KernelScriptingHelper helper = AvatarRendererModel.getInstance().getCore2Model().createKernelScriptingHelper();
        helper.setLocalTransform(AvatarEditorModel.AVATAR_NAME, this.avatarPos, this.avatarRot, new Vector3(1.0f, 1.0f, 1.0f));
        helper.execute();
    }

    public void setShadowtarVisibilityChangedCallback(Runnable r) {
    }

    public void setViewToSignalOnShadowtarVisible(AvatarViewVM viewVM) {
    }

    public void setToShadowtar() {
    }

    public void setNotifyInitializedCallback(Runnable r) {
    }

    public void setToEntering() {
    }

    public void setNotifyLoadedUserData(Runnable r) {
        this.notifyLoadedUserData = r;
    }

    public void setNotifyInitializationError(Runnable r) {
        this.notifyInitializationError = r;
    }

    public void setNotifyRuntimeCatastrophicError(Runnable r) {
        this.notifyRuntimeCatastrophicError = r;
    }

    public void setNotifyLoadingAsset(Runnable r) {
        this.notifyLoadingAsset = r;
    }

    public void setNotifyLoadedAsset(NotifyLoadedAsset r) {
        this.notifyLoadedAsset = r;
    }

    private void notifyLoadedUserData() {
        this.notifyLoadedUserData.run();
    }

    private void notifyInitializationError() {
        this.notifyInitializationError.run();
    }

    private void notifyRuntimeCatastrophicError() {
        this.notifyRuntimeCatastrophicError.run();
    }

    private void notifyLoadingAsset() {
        this.notifyLoadingAsset.run();
    }

    private void notifyLoadedAsset(XLEException e) {
        this.notifyLoadedAsset.run(e);
    }
}
