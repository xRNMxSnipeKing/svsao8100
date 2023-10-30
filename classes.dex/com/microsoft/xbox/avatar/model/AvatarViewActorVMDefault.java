package com.microsoft.xbox.avatar.model;

import android.view.MotionEvent;
import com.microsoft.xbox.avatar.view.AvatarAnimationCatalog;
import com.microsoft.xbox.avatar.view.XLEAvatarAnimationAction;
import com.microsoft.xbox.service.model.AchievementItem.AchievementAnimState;
import com.microsoft.xbox.service.model.serialization.XLEAvatarManifest;
import com.microsoft.xbox.service.network.managers.xblshared.CompanionSession;
import com.microsoft.xbox.toolkit.FixedSizeLinkedList;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLELog;
import com.xbox.avatarrenderer.ASSET_COLOR_TABLE;
import com.xbox.avatarrenderer.AvatarEditor.AvatarEditorEditEvent;
import com.xbox.avatarrenderer.Core2Callbacks;
import com.xbox.avatarrenderer.Core2Renderer.ANIMATION_CHAINING_MODE;
import com.xbox.avatarrenderer.Core2Renderer.VARIABLE_SCOPE;
import com.xbox.avatarrenderer.Kernel.AvatarManifest;
import com.xbox.avatarrenderer.Kernel.AvatarManifest.AVATAR_BODY_TYPE;
import com.xbox.avatarrenderer.Kernel.AvatarManifestEditor;
import com.xbox.avatarrenderer.Kernel.KernelScriptingHelper;
import com.xbox.avatarrenderer.Kernel.ScriptException;
import com.xbox.avatarrenderer.Vector3;
import java.util.ArrayList;
import java.util.Random;

public class AvatarViewActorVMDefault extends AvatarViewActorVM implements Core2Callbacks {
    private static final float BLEND_IN_TIME = 0.5f;
    private static final float BLEND_OUT_TIME = 0.5f;
    private static final float FALL_ANIM_DOWN_TIME = 5.0f;
    private static String PROP_VAR_LIST_NAME = "PropList";
    private static String PROP_VAR_NAME = "PropAnim";
    private static int staticAvatarIndex = 0;
    private String[] achievementConfusedAnimations;
    private ArrayList<String> achievementCryAnimations;
    private AchievementAnimState achievementState = AchievementAnimState.NONE;
    private int achievementStateVersion = 0;
    private int actorIndex;
    private int align = 1;
    private FixedSizeLinkedList<String> animationHistory = new FixedSizeLinkedList(20);
    private String avatarName;
    private Vector3 avatarPos;
    private Vector3 avatarRot;
    private ASSET_COLOR_TABLE carryableColorTable = null;
    private String carryableGuid = null;
    private ArrayList<String> fidgetAnimations;
    private boolean hasCarryable = false;
    private boolean hideAvatarOnFinish = false;
    private boolean idleProp;
    private XLEAvatarManifest manifest = null;
    private Runnable mottoShowCallback = null;
    private Runnable notifyInitializedCallback = null;
    private boolean postingEmote;
    private Random random = new Random();
    private ArrayList<String> runInAnimations;
    private ArrayList<String> runOutAnimations;
    private Runnable shadowtarVisibilityChangedCallback = null;
    private boolean showPropFirst;
    private float sleepTime = 0.0f;
    private AvatarViewAnimationState state = AvatarViewAnimationState.SCENE_NOT_READY;
    private int touchAnimationIndex = 0;
    private ArrayList<String> touchAnimations;
    private AvatarViewVM viewToSignalOnShadowtarVisible = null;
    private boolean wasPreviouslyPlayingProp = false;

    private enum AvatarViewAnimationState {
        SCENE_NOT_READY,
        SCENE_READY,
        INITIALIZING,
        INITIALIZED,
        ENTERING,
        IDLE,
        EXITING,
        FIDGET_EMOTE,
        TOUCH_EMOTE,
        PLAY_ACHIEVEMENT_ANIMATION,
        REMOVING_OLD_AVATAR,
        FALL_ANIM_FALLING,
        FALL_ANIM_DOWN,
        FALL_ANIM_RISING,
        SHADOWTAR,
        PROP
    }

    public void initializeActorSceneData(int index) {
        this.actorIndex = index;
    }

    public void initializeActorSpecificData(int align, Vector3 avatarPos, Vector3 avatarRot, boolean idleProp) {
        staticAvatarIndex++;
        this.avatarName = String.format("avatarViewActor%d_%d", new Object[]{Integer.valueOf(this.actorIndex), Integer.valueOf(staticAvatarIndex)});
        this.align = align;
        this.avatarPos = avatarPos;
        this.avatarRot = avatarRot;
        this.idleProp = idleProp;
    }

    public boolean getIsLoaded() {
        switch (this.state) {
            case SCENE_NOT_READY:
            case SCENE_READY:
            case INITIALIZING:
            case INITIALIZED:
            case ENTERING:
            case SHADOWTAR:
                return false;
            default:
                return true;
        }
    }

    public void clearLastPlayedAnimations() {
        this.animationHistory.clear();
    }

    public boolean getIsMale() {
        if (this.manifest == null || this.manifest.Manifest == null || AvatarRendererModel.getInstance().getCore2Model().createManifestFromHex(this.manifest.Manifest).getGender() == AVATAR_BODY_TYPE.MALE) {
            return true;
        }
        return false;
    }

    public String[] getLastPlayedAnimations() {
        return (String[]) this.animationHistory.toArray(new String[0]);
    }

    public AvatarManifestEditor getManifestEditor() {
        if (this.manifest == null || this.manifest.Manifest == null) {
            return null;
        }
        AvatarManifest avManifest = AvatarRendererModel.getInstance().getCore2Model().createManifestFromHex(this.manifest.Manifest);
        if (avManifest != null) {
            return AvatarRendererModel.getInstance().getCore2Model().createAvatarManifestEditor(avManifest);
        }
        return null;
    }

    public void initializeAchievementAnim(XLEAvatarManifest newManifest, AchievementAnimState achievementAnimState, int version, float sleepTime) {
        if (!AvatarRendererModel.getInstance().isDestroyed() && newManifest != null && achievementAnimState != AchievementAnimState.NONE) {
            switch (this.state) {
                case SCENE_NOT_READY:
                    this.manifest = newManifest;
                    this.achievementState = achievementAnimState;
                    this.achievementStateVersion = version;
                    this.sleepTime = sleepTime;
                    return;
                case SCENE_READY:
                case SHADOWTAR:
                    this.manifest = newManifest;
                    this.achievementState = achievementAnimState;
                    this.achievementStateVersion = version;
                    this.sleepTime = sleepTime;
                    onStateChange(AvatarViewAnimationState.INITIALIZING);
                    return;
                case INITIALIZING:
                case INITIALIZED:
                case ENTERING:
                    this.achievementState = achievementAnimState;
                    this.achievementStateVersion = version;
                    return;
                case IDLE:
                case PLAY_ACHIEVEMENT_ANIMATION:
                case FIDGET_EMOTE:
                    this.achievementState = achievementAnimState;
                    if (this.achievementStateVersion != version) {
                        this.achievementStateVersion = version;
                        onStateChange(AvatarViewAnimationState.PLAY_ACHIEVEMENT_ANIMATION);
                        return;
                    }
                    return;
                default:
                    return;
            }
        }
    }

    public void setManifest(XLEAvatarManifest newManifest) {
        boolean z = true;
        if (!AvatarRendererModel.getInstance().isDestroyed()) {
            boolean z2;
            if (AvatarRendererModel.getInstance().getCore2Model() != null) {
                z2 = true;
            } else {
                z2 = false;
            }
            XLEAssert.assertTrue(z2);
            if (Thread.currentThread() != ThreadManager.UIThread) {
                z = false;
            }
            XLEAssert.assertTrue(z);
            if (newManifest != null) {
                boolean loadNewManifest = false;
                boolean deleteOldAvatar = false;
                switch (this.state) {
                    case SCENE_NOT_READY:
                        this.manifest = newManifest;
                        break;
                    case SCENE_READY:
                    case SHADOWTAR:
                        loadNewManifest = true;
                        break;
                    default:
                        if (!ManifestsEqual(this.manifest, newManifest)) {
                            loadNewManifest = true;
                            deleteOldAvatar = true;
                            break;
                        }
                        break;
                }
                if (loadNewManifest) {
                    this.manifest = newManifest;
                    if (deleteOldAvatar) {
                        onStateChange(AvatarViewAnimationState.REMOVING_OLD_AVATAR);
                    } else {
                        onStateChange(AvatarViewAnimationState.INITIALIZING);
                    }
                }
            }
        }
    }

    public void setShowPropFirst(boolean showPropFirst) {
        this.showPropFirst = showPropFirst;
    }

    private static boolean ManifestsEqual(XLEAvatarManifest lhs, XLEAvatarManifest rhs) {
        boolean z = true;
        if (lhs == rhs) {
            return true;
        }
        if (lhs == null || rhs == null) {
            return false;
        }
        if (lhs == null || rhs == null) {
            z = false;
        }
        XLEAssert.assertTrue(z);
        return JavaUtil.stringsEqualCaseInsensitive(lhs.Manifest, rhs.Manifest);
    }

    private void onStateChange(AvatarViewAnimationState newState) {
        this.state = newState;
        if (!AvatarRendererModel.getInstance().isDestroyed()) {
            XLELog.Info(this.avatarName, "Set state to " + this.state);
            KernelScriptingHelper helper;
            switch (AnonymousClass4.$SwitchMap$com$microsoft$xbox$avatar$model$AvatarViewActorVMDefault$AvatarViewAnimationState[this.state.ordinal()]) {
                case 1:
                case 2:
                    return;
                case 3:
                    XLEAssert.assertTrue(this.manifest != null);
                    if (this.manifest.Manifest == null) {
                        onStateChange(AvatarViewAnimationState.SHADOWTAR);
                        return;
                    } else {
                        runInitialScriptNotShadowtar();
                        return;
                    }
                case 4:
                    runInitializedScript();
                    return;
                case 5:
                    helper = buildKernelScriptingHelper();
                    AvatarScriptCatalog.getInstance().addEnterScript(helper, this, this.avatarName, this.runInAnimations, this.avatarPos, this.avatarRot, this.sleepTime, 0.5f);
                    helper.execute();
                    return;
                case 6:
                    runShadowtarScript();
                    return;
                case 7:
                    helper = buildKernelScriptingHelper();
                    AvatarScriptCatalog.getInstance().addIdleAnimationScript(helper, this, this.avatarName, 0.5f, 0.5f, ANIMATION_CHAINING_MODE.ANIMATION_FINISHED);
                    helper.execute();
                    postEmote();
                    return;
                case 8:
                    runAchievementAnimationScript();
                    return;
                case 9:
                    runFidgetEmoteScript();
                    return;
                case 10:
                    runExitScript();
                    return;
                case 11:
                    runTouchEmoteScript();
                    return;
                case CompanionSession.LRCERROR_TOO_MANY_CLIENTS /*12*/:
                    runRemoveOldAvatar();
                    return;
                case CompanionSession.LRCERROR_EXPIRED_COMMAND /*13*/:
                    runFallAnimFallingScript();
                    return;
                case CompanionSession.LRCERROR_NO_EXCLUSIVE /*14*/:
                    runFallAnimDownScript();
                    return;
                case 15:
                    runFallAnimRisingScript();
                    return;
                case 16:
                    runPropAnimationScript();
                    return;
                default:
                    throw new UnsupportedOperationException();
            }
        }
    }

    private void postEmote() {
        if (!this.postingEmote) {
            this.postingEmote = true;
            int emoteDelayMs = this.random.nextInt(20000) + 10000;
            final int sceneIndexOnPost = AvatarRendererModel.getInstance().getSceneIndex();
            ThreadManager.UIThreadPostDelayed(new Runnable() {
                public void run() {
                    if (AvatarRendererModel.getInstance().getSceneIndex() == sceneIndexOnPost && AvatarViewActorVMDefault.this.state == AvatarViewAnimationState.IDLE) {
                        AvatarViewActorVMDefault.this.onStateChange(AvatarViewAnimationState.FIDGET_EMOTE);
                    }
                }
            }, (long) emoteDelayMs);
        }
    }

    public void animate(XLEAvatarAnimationAction action, int transitionMs) {
        this.hideAvatarOnFinish = false;
        switch (action) {
            case Exit:
                this.hideAvatarOnFinish = true;
                switch (AnonymousClass4.$SwitchMap$com$microsoft$xbox$avatar$model$AvatarViewActorVMDefault$AvatarViewAnimationState[this.state.ordinal()]) {
                    case 7:
                    case 8:
                    case 9:
                    case 11:
                    case 16:
                        onStateChange(AvatarViewAnimationState.EXITING);
                        return;
                    default:
                        XLELog.Error(this.avatarName, "Ignored transition from " + this.state + " to AvatarViewAnimationState.EXITING");
                        return;
                }
            default:
                return;
        }
    }

    public void onFinishAnimation() {
        if (this.hideAvatarOnFinish) {
            runHideScript();
        }
    }

    public void hitboxOnClick() {
        switch (this.state) {
            case IDLE:
            case FIDGET_EMOTE:
            case TOUCH_EMOTE:
                this.wasPreviouslyPlayingProp = false;
                onStateChange(AvatarViewAnimationState.TOUCH_EMOTE);
                return;
            case PROP:
                this.wasPreviouslyPlayingProp = true;
                onStateChange(AvatarViewAnimationState.TOUCH_EMOTE);
                return;
            default:
                return;
        }
    }

    public void hitboxOnTouch(MotionEvent event) {
    }

    public void shakeFall() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        switch (AnonymousClass4.$SwitchMap$com$microsoft$xbox$avatar$model$AvatarViewActorVMDefault$AvatarViewAnimationState[this.state.ordinal()]) {
            case 7:
            case 9:
            case 11:
            case 16:
                onStateChange(AvatarViewAnimationState.FALL_ANIM_FALLING);
                return;
            case CompanionSession.LRCERROR_NO_EXCLUSIVE /*14*/:
                onStateChange(AvatarViewAnimationState.FALL_ANIM_RISING);
                return;
            default:
                return;
        }
    }

    public void setViewToSignalOnShadowtarVisible(AvatarViewVM view) {
        this.viewToSignalOnShadowtarVisible = view;
    }

    public void setShadowtarVisibilityChangedCallback(Runnable callback) {
        this.shadowtarVisibilityChangedCallback = callback;
    }

    public void setMottoShowCallback(Runnable callback) {
        this.mottoShowCallback = callback;
    }

    public void setToShadowtar() {
        if (this.state != AvatarViewAnimationState.SHADOWTAR) {
            onStateChange(AvatarViewAnimationState.SHADOWTAR);
        }
    }

    public void setNotifyInitializedCallback(Runnable r) {
        this.notifyInitializedCallback = r;
    }

    public void setToEntering() {
        XLEAssert.assertTrue(this.state == AvatarViewAnimationState.INITIALIZED);
        onStateChange(AvatarViewAnimationState.ENTERING);
    }

    public boolean getIsInInitializedState() {
        return this.state == AvatarViewAnimationState.INITIALIZED;
    }

    private KernelScriptingHelper buildKernelScriptingHelper() {
        boolean z;
        boolean z2 = true;
        KernelScriptingHelper helper = AvatarRendererModel.getInstance().getCore2Model().createKernelScriptingHelper();
        if (helper != null) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        if (helper.setExceptionHandler(this) != 0) {
            z2 = false;
        }
        XLEAssert.assertTrue(z2);
        return helper;
    }

    private void runHideScript() {
        KernelScriptingHelper helper = buildKernelScriptingHelper();
        if (helper.setLocalTransform(this.avatarName, new Vector3(0.0f, 0.0f, 0.0f), new Vector3(0.0f, 0.0f, 0.0f), new Vector3(0.0f, 0.0f, 0.0f)) >= 0) {
            helper.execute();
        }
    }

    private void runExitScript() {
        KernelScriptingHelper helper = buildKernelScriptingHelper();
        if (AvatarScriptCatalog.getInstance().playAnimationInternal(helper, this, this.avatarName, (String) this.runOutAnimations.get(this.random.nextInt(this.runOutAnimations.size())), 0.5f, 0.5f, ANIMATION_CHAINING_MODE.REPLACE) >= 0 && helper.addNotifier(AvatarScriptCatalog.NOTIFY_EXITED_SCREEN, this) >= 0) {
            helper.execute();
        }
    }

    private void runPropAnimationScript() {
        KernelScriptingHelper helper = buildKernelScriptingHelper();
        if (AvatarScriptCatalog.getInstance().playPropAnimationInternal(helper, this, this.avatarName, this.carryableGuid, this.carryableColorTable, PROP_VAR_NAME, PROP_VAR_LIST_NAME, 0.5f) >= 0 && helper.addNotifier(AvatarScriptCatalog.NOTIFY_PROP, this) >= 0) {
            helper.execute();
        }
    }

    private void runTouchEmoteScript() {
        if (this.wasPreviouslyPlayingProp && JavaUtil.stringsEqualCaseInsensitive((String) this.touchAnimations.get(this.touchAnimationIndex), this.carryableGuid)) {
            this.touchAnimationIndex = (this.touchAnimationIndex + 1) % this.touchAnimations.size();
        }
        String animation = (String) this.touchAnimations.get(this.touchAnimationIndex);
        this.touchAnimationIndex = (this.touchAnimationIndex + 1) % this.touchAnimations.size();
        if (JavaUtil.stringsEqualCaseInsensitive(animation, this.carryableGuid)) {
            onStateChange(AvatarViewAnimationState.PROP);
            return;
        }
        KernelScriptingHelper helper = buildKernelScriptingHelper();
        if (AvatarScriptCatalog.getInstance().playAnimationInternal(helper, this, this.avatarName, animation, 0.5f, 0.5f, ANIMATION_CHAINING_MODE.REPLACE) >= 0 && helper.addNotifier(AvatarScriptCatalog.NOTIFY_TOUCH_EMOTE, this) >= 0) {
            helper.execute();
        }
    }

    private void runAchievementAnimationScript() {
        KernelScriptingHelper helper = buildKernelScriptingHelper();
        switch (this.achievementState) {
            case EARNED:
                if (AvatarScriptCatalog.getInstance().playAnimationInternal(helper, this, this.avatarName, AvatarAnimationCatalog.ACHIEVEMENT_EMOTE_CELEBRATE[this.random.nextInt(AvatarAnimationCatalog.ACHIEVEMENT_EMOTE_CELEBRATE.length)], 0.5f, 0.5f, ANIMATION_CHAINING_MODE.REPLACE) < 0) {
                    return;
                }
                break;
            case UNEARNED:
                if (AvatarScriptCatalog.getInstance().playAnimationInternal(helper, this, this.avatarName, (String) this.achievementCryAnimations.get(this.random.nextInt(this.achievementCryAnimations.size())), 0.5f, 0.5f, ANIMATION_CHAINING_MODE.REPLACE) < 0) {
                    return;
                }
                break;
            case SECRET:
                if (AvatarScriptCatalog.getInstance().playAnimationInternal(helper, this, this.avatarName, this.achievementConfusedAnimations[this.random.nextInt(this.achievementConfusedAnimations.length)], 0.5f, 0.5f, ANIMATION_CHAINING_MODE.REPLACE) < 0) {
                    return;
                }
                break;
            default:
                XLEAssert.assertTrue(false);
                break;
        }
        if (helper.addNotifier(AvatarScriptCatalog.NOTIFY_RUN_ACHIEVEMENT_ANIMATION, this) >= 0) {
            helper.execute();
        }
    }

    private void runFidgetEmoteScript() {
        this.postingEmote = false;
        boolean animateProp = false;
        if (this.hasCarryable && this.carryableGuid != null && this.idleProp) {
            animateProp = this.random.nextInt(10) < 4;
        }
        if (animateProp) {
            onStateChange(AvatarViewAnimationState.PROP);
            return;
        }
        KernelScriptingHelper helper = buildKernelScriptingHelper();
        if (AvatarScriptCatalog.getInstance().playAnimationInternal(helper, this, this.avatarName, (String) this.fidgetAnimations.get(this.random.nextInt(this.fidgetAnimations.size())), 0.5f, 0.5f, ANIMATION_CHAINING_MODE.REPLACE) >= 0 && helper.addNotifier(AvatarScriptCatalog.NOTIFY_FIDGET_EMOTE, this) >= 0) {
            helper.execute();
        }
    }

    private void runInitialScriptNotShadowtar() {
        updateShadowtarVisibility();
        this.postingEmote = false;
        switch (this.align) {
            case 1:
                this.runInAnimations = this.achievementState == AchievementAnimState.NONE ? AvatarAnimationCatalog.RUN_IN_ANIMATIONS_LEFT : AvatarAnimationCatalog.RUN_IN_ANIMATION_LEFT_GENTLY;
                this.runOutAnimations = AvatarAnimationCatalog.RUN_OUT_ANIMATIONS_LEFT;
                break;
            case 2:
                this.runInAnimations = this.achievementState == AchievementAnimState.NONE ? AvatarAnimationCatalog.RUN_IN_ANIMATIONS_RIGHT : AvatarAnimationCatalog.RUN_IN_ANIMATION_RIGHT_GENTLY;
                this.runOutAnimations = AvatarAnimationCatalog.RUN_OUT_ANIMATIONS_RIGHT;
                break;
        }
        this.touchAnimations = new ArrayList(AvatarAnimationCatalog.TOUCH_EMOTE_ANIMATION);
        KernelScriptingHelper helper = buildKernelScriptingHelper();
        AvatarManifest avManifest = AvatarRendererModel.getInstance().getCore2Model().createManifestFromHex(this.manifest.Manifest);
        if (avManifest.getGender() == AVATAR_BODY_TYPE.MALE) {
            this.fidgetAnimations = AvatarAnimationCatalog.IDLE_FIDGET_ANIMATIONS_MALE;
            this.achievementCryAnimations = AvatarAnimationCatalog.ACHIEVEMENT_EMOTE_CRY_MALE;
            this.achievementConfusedAnimations = AvatarAnimationCatalog.ACHIEVEMENT_EMOTE_CONFUSED_MALE;
        } else {
            this.fidgetAnimations = AvatarAnimationCatalog.IDLE_FIDGET_ANIMATIONS_FEMALE;
            this.achievementCryAnimations = AvatarAnimationCatalog.ACHIEVEMENT_EMOTE_CRY_FEMALE;
            this.achievementConfusedAnimations = AvatarAnimationCatalog.ACHIEVEMENT_EMOTE_CONFUSED_FEMALE;
        }
        if (helper.loadAnimation(AvatarAnimationCatalog.IDLE_ANIMATION) >= 0) {
            this.hasCarryable = avManifest.getHasCarryable().booleanValue();
            if (this.hasCarryable) {
                this.carryableGuid = avManifest.getCarryableGuid();
                this.carryableColorTable = null;
                if (helper.declareVariable(PROP_VAR_NAME, VARIABLE_SCOPE.THIS) < 0) {
                    return;
                }
                if (this.carryableGuid != null && helper.loadCarryable(this.carryableGuid, this.carryableColorTable, PROP_VAR_NAME) < 0) {
                    return;
                }
            }
            int i = 0;
            while (i < this.fidgetAnimations.size()) {
                if (helper.loadAnimation((String) this.fidgetAnimations.get(i)) >= 0) {
                    i++;
                } else {
                    return;
                }
            }
            i = 0;
            while (i < this.touchAnimations.size()) {
                if (helper.loadAnimation((String) this.touchAnimations.get(i)) >= 0) {
                    i++;
                } else {
                    return;
                }
            }
            if (this.hasCarryable && this.carryableGuid != null) {
                this.touchAnimations.add(0, this.carryableGuid);
            }
            i = 0;
            while (i < this.runInAnimations.size()) {
                if (helper.loadAnimation((String) this.runInAnimations.get(i)) >= 0) {
                    i++;
                } else {
                    return;
                }
            }
            i = 0;
            while (i < this.runOutAnimations.size()) {
                if (helper.loadAnimation((String) this.runOutAnimations.get(i)) >= 0) {
                    i++;
                } else {
                    return;
                }
            }
            i = 0;
            while (i < this.achievementCryAnimations.size()) {
                if (helper.loadAnimation((String) this.achievementCryAnimations.get(i)) >= 0) {
                    i++;
                } else {
                    return;
                }
            }
            i = 0;
            while (i < AvatarAnimationCatalog.ACHIEVEMENT_EMOTE_CELEBRATE.length) {
                if (helper.loadAnimation(AvatarAnimationCatalog.ACHIEVEMENT_EMOTE_CELEBRATE[i]) >= 0) {
                    i++;
                } else {
                    return;
                }
            }
            i = 0;
            while (i < this.achievementConfusedAnimations.length) {
                if (helper.loadAnimation(this.achievementConfusedAnimations[i]) >= 0) {
                    i++;
                } else {
                    return;
                }
            }
            if (helper.loadAnimation(AvatarAnimationCatalog.AVATAR_FALL_ANIMATION_FALL) >= 0 && helper.loadAnimation(AvatarAnimationCatalog.AVATAR_FALL_ANIMATION_STAND) >= 0 && AvatarScriptCatalog.getInstance().addAvatar(helper, avManifest, this.avatarName) >= 0 && helper.addNotifier(AvatarScriptCatalog.NOTIFY_INITIALIZED, this) >= 0) {
                helper.execute();
            }
        }
    }

    private void runShadowtarScript() {
        updateShadowtarVisibility();
    }

    private void runRemoveOldAvatar() {
        AvatarRendererModel.getInstance().purgeScene();
        onStateChange(AvatarViewAnimationState.INITIALIZING);
    }

    private void runInitializedScript() {
        if (this.notifyInitializedCallback != null) {
            this.notifyInitializedCallback.run();
        }
    }

    private void runFallAnimFallingScript() {
        KernelScriptingHelper helper = buildKernelScriptingHelper();
        if (AvatarScriptCatalog.getInstance().playAnimationInternal(helper, this, this.avatarName, AvatarAnimationCatalog.AVATAR_FALL_ANIMATION_FALL, 0.5f, 0.5f, ANIMATION_CHAINING_MODE.REPLACE) >= 0 && helper.addNotifier(AvatarScriptCatalog.NOTIFY_FALL_ANIM_FALL_COMPLETE, this) >= 0) {
            helper.execute();
        }
    }

    private void runFallAnimDownScript() {
        KernelScriptingHelper helper = buildKernelScriptingHelper();
        if (helper.sleep(FALL_ANIM_DOWN_TIME) >= 0 && helper.addNotifier(AvatarScriptCatalog.NOTIFY_FALL_ANIM_DOWN_COMPLETE, this) >= 0) {
            helper.execute();
        }
    }

    private void runFallAnimRisingScript() {
        KernelScriptingHelper helper = buildKernelScriptingHelper();
        if (AvatarScriptCatalog.getInstance().playAnimationInternal(helper, this, this.avatarName, AvatarAnimationCatalog.AVATAR_FALL_ANIMATION_STAND, 0.5f, 0.5f, ANIMATION_CHAINING_MODE.REPLACE) >= 0 && helper.addNotifier(AvatarScriptCatalog.NOTIFY_FALL_ANIM_RISING_COMPLETE, this) >= 0) {
            helper.execute();
        }
    }

    public void onNotify(final int iContext) {
        final int sceneIndexOnPost = AvatarRendererModel.getInstance().getSceneIndex();
        ThreadManager.UIThreadPost(new Runnable() {
            public void run() {
                if (AvatarRendererModel.getInstance().getSceneIndex() == sceneIndexOnPost) {
                    switch (iContext) {
                        case 256:
                            if (AvatarViewActorVMDefault.this.state != AvatarViewAnimationState.ENTERING) {
                                return;
                            }
                            if (AvatarViewActorVMDefault.this.achievementState == AchievementAnimState.NONE) {
                                if (AvatarViewActorVMDefault.this.mottoShowCallback != null) {
                                    AvatarViewActorVMDefault.this.mottoShowCallback.run();
                                }
                                if (AvatarViewActorVMDefault.this.showPropFirst && AvatarViewActorVMDefault.this.hasCarryable && AvatarViewActorVMDefault.this.carryableGuid != null) {
                                    AvatarViewActorVMDefault.this.onStateChange(AvatarViewAnimationState.PROP);
                                    return;
                                } else {
                                    AvatarViewActorVMDefault.this.onStateChange(AvatarViewAnimationState.IDLE);
                                    return;
                                }
                            }
                            AvatarViewActorVMDefault.this.onStateChange(AvatarViewAnimationState.PLAY_ACHIEVEMENT_ANIMATION);
                            return;
                        case AvatarScriptCatalog.NOTIFY_EXITED_SCREEN /*257*/:
                            if (AvatarViewActorVMDefault.this.state == AvatarViewAnimationState.EXITING) {
                                AvatarViewActorVMDefault.this.onStateChange(AvatarViewAnimationState.SCENE_NOT_READY);
                                return;
                            }
                            return;
                        case AvatarScriptCatalog.NOTIFY_INITIALIZED /*258*/:
                            if (AvatarViewActorVMDefault.this.state == AvatarViewAnimationState.INITIALIZING) {
                                AvatarViewActorVMDefault.this.onStateChange(AvatarViewAnimationState.INITIALIZED);
                                return;
                            }
                            return;
                        case AvatarScriptCatalog.NOTIFY_FIDGET_EMOTE /*259*/:
                            if (AvatarViewActorVMDefault.this.state == AvatarViewAnimationState.FIDGET_EMOTE) {
                                AvatarViewActorVMDefault.this.onStateChange(AvatarViewAnimationState.IDLE);
                                return;
                            }
                            return;
                        case AvatarScriptCatalog.NOTIFY_TOUCH_EMOTE /*260*/:
                            if (AvatarViewActorVMDefault.this.state == AvatarViewAnimationState.TOUCH_EMOTE) {
                                AvatarViewActorVMDefault.this.onStateChange(AvatarViewAnimationState.IDLE);
                                return;
                            }
                            return;
                        case AvatarScriptCatalog.NOTIFY_RUN_ACHIEVEMENT_ANIMATION /*261*/:
                            if (AvatarViewActorVMDefault.this.state == AvatarViewAnimationState.PLAY_ACHIEVEMENT_ANIMATION) {
                                AvatarViewActorVMDefault.this.onStateChange(AvatarViewAnimationState.IDLE);
                                return;
                            }
                            return;
                        case AvatarScriptCatalog.NOTIFY_IDLE_ANIMATION /*262*/:
                            if (AvatarViewActorVMDefault.this.state == AvatarViewAnimationState.IDLE) {
                                AvatarViewActorVMDefault.this.onStateChange(AvatarViewAnimationState.IDLE);
                                return;
                            }
                            return;
                        case AvatarScriptCatalog.NOTIFY_FALL_ANIM_FALL_COMPLETE /*265*/:
                            if (AvatarViewActorVMDefault.this.state == AvatarViewAnimationState.FALL_ANIM_FALLING) {
                                AvatarViewActorVMDefault.this.onStateChange(AvatarViewAnimationState.FALL_ANIM_DOWN);
                                return;
                            }
                            return;
                        case AvatarScriptCatalog.NOTIFY_FALL_ANIM_DOWN_COMPLETE /*272*/:
                            if (AvatarViewActorVMDefault.this.state == AvatarViewAnimationState.FALL_ANIM_DOWN) {
                                AvatarViewActorVMDefault.this.onStateChange(AvatarViewAnimationState.FALL_ANIM_RISING);
                                return;
                            }
                            return;
                        case AvatarScriptCatalog.NOTIFY_FALL_ANIM_RISING_COMPLETE /*273*/:
                            if (AvatarViewActorVMDefault.this.state == AvatarViewAnimationState.FALL_ANIM_RISING) {
                                AvatarViewActorVMDefault.this.onStateChange(AvatarViewAnimationState.IDLE);
                                return;
                            }
                            return;
                        case AvatarScriptCatalog.NOTIFY_PROP /*274*/:
                            if (AvatarViewActorVMDefault.this.state == AvatarViewAnimationState.PROP) {
                                AvatarViewActorVMDefault.this.onStateChange(AvatarViewAnimationState.IDLE);
                                return;
                            }
                            return;
                        default:
                            AvatarViewActorVMDefault.this.animationHistory.push(AvatarScriptCatalog.getInstance().getCurrentlyPlayingAnimation(AvatarViewActorVMDefault.this.avatarName, iContext));
                            return;
                    }
                }
            }
        });
    }

    public void invokeAvatarEditorEditEvent(AvatarEditorEditEvent aeee) {
    }

    public void invokeScriptException(ScriptException scriptException) {
        final int errorCode = scriptException.getErrorCode();
        final int sceneIndexOnPost = AvatarRendererModel.getInstance().getSceneIndex();
        ThreadManager.UIThreadPost(new Runnable() {
            public void run() {
                if (AvatarRendererModel.getInstance().getSceneIndex() == sceneIndexOnPost) {
                    XLELog.Info(AvatarViewActorVMDefault.this.avatarName, "Script exception while in state: " + AvatarViewActorVMDefault.this.state);
                    XLELog.Info(AvatarViewActorVMDefault.this.avatarName, "Script exception error code: " + errorCode);
                    AvatarViewActorVMDefault.this.onStateChange(AvatarViewAnimationState.SHADOWTAR);
                }
            }
        });
    }

    public void deviceIsCapableCallback(int i) {
    }

    public boolean getIsShadowtarVisible() {
        return this.state == AvatarViewAnimationState.SHADOWTAR;
    }

    private void updateShadowtarVisibility() {
        if (this.shadowtarVisibilityChangedCallback != null) {
            this.shadowtarVisibilityChangedCallback.run();
        }
        if (getIsShadowtarVisible() && this.viewToSignalOnShadowtarVisible != null) {
            this.viewToSignalOnShadowtarVisible.setAllAvatarsToShadowtar();
        }
    }

    public void onSceneBegin() {
        if (this.state != AvatarViewAnimationState.SCENE_NOT_READY) {
            return;
        }
        if (this.manifest != null) {
            onStateChange(AvatarViewAnimationState.INITIALIZING);
        } else {
            onStateChange(AvatarViewAnimationState.SCENE_READY);
        }
    }

    public void onSceneEnd() {
        onStateChange(AvatarViewAnimationState.SCENE_NOT_READY);
    }

    public void onDestroy() {
        super.onDestroy();
        setShadowtarVisibilityChangedCallback(null);
        setMottoShowCallback(null);
    }
}
