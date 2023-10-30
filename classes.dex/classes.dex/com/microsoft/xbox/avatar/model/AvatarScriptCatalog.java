package com.microsoft.xbox.avatar.model;

import com.microsoft.xbox.avatar.view.AvatarAnimationCatalog;
import com.microsoft.xbox.toolkit.InvertibleHashMap;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLELog;
import com.xbox.avatarrenderer.ASSET_COLOR_TABLE;
import com.xbox.avatarrenderer.Core2Callbacks;
import com.xbox.avatarrenderer.Core2Renderer.ANIMATION_CHAINING_MODE;
import com.xbox.avatarrenderer.Core2Renderer.SEQUENCED_ANIMATION_MODE;
import com.xbox.avatarrenderer.Core2Renderer.VARIABLE_SCOPE;
import com.xbox.avatarrenderer.Kernel.AvatarManifest;
import com.xbox.avatarrenderer.Kernel.KernelScriptingHelper;
import com.xbox.avatarrenderer.Vector3;
import java.util.ArrayList;
import java.util.Random;

public class AvatarScriptCatalog {
    public static final int NOTIFY_ENTERED_SCREEN = 256;
    public static final int NOTIFY_EXITED_SCREEN = 257;
    public static final int NOTIFY_FALL_ANIM_DOWN_COMPLETE = 272;
    public static final int NOTIFY_FALL_ANIM_FALL_COMPLETE = 265;
    public static final int NOTIFY_FALL_ANIM_RISING_COMPLETE = 273;
    public static final int NOTIFY_FIDGET_EMOTE = 259;
    public static final int NOTIFY_IDLE_ANIMATION = 262;
    public static final int NOTIFY_INITIALIZED = 258;
    public static final int NOTIFY_PROP = 274;
    public static final int NOTIFY_PROP_ANIMATION_COMPLETE = 264;
    public static final int NOTIFY_RUN_ACHIEVEMENT_ANIMATION = 261;
    public static final int NOTIFY_SCRIPT_COMPLETED = 1610612736;
    public static final int NOTIFY_START_ANIMATION = 1879048192;
    public static final int NOTIFY_TOUCH_EMOTE = 260;
    public static final int NOTIFY_VIEW_CAMERA_READY = 263;
    private static int animationGuidCounter = 0;
    private static InvertibleHashMap<String, Integer> animationGuidToInt = new InvertibleHashMap();
    private static AvatarScriptCatalog instance = new AvatarScriptCatalog();
    private Random random = new Random();

    public static AvatarScriptCatalog getInstance() {
        return instance;
    }

    private int getIntForAnimationGuid(String animationGuid) {
        if (!animationGuidToInt.containsKey(animationGuid)) {
            animationGuidCounter++;
            animationGuidToInt.put(animationGuid, Integer.valueOf(animationGuidCounter));
        }
        return ((Integer) animationGuidToInt.getUsingKey(animationGuid)).intValue();
    }

    public int addAnimationStartEvent(KernelScriptingHelper helper, Core2Callbacks callback, String animationGuid) {
        return helper.addNotifier(NOTIFY_START_ANIMATION + getIntForAnimationGuid(animationGuid), callback);
    }

    public int playAnimationInternal(KernelScriptingHelper helper, Core2Callbacks callback, String avatarName, String animationName, float blendInTime, float blendOutTime, ANIMATION_CHAINING_MODE mode) {
        int hr = addAnimationStartEvent(helper, callback, animationName);
        return hr < 0 ? hr : helper.playAnimation(avatarName, animationName, blendInTime, blendOutTime, mode);
    }

    public int playPropAnimationInternal(KernelScriptingHelper helper, Core2Callbacks callback, String avatarName, String carryableGuid, ASSET_COLOR_TABLE carryableColorTable, String PROP_VAR_NAME, String PROP_VAR_LIST_NAME, float BLEND_IN_TIME) {
        XLEAssert.assertTrue(carryableGuid != null);
        int hr = helper.declareVariable(PROP_VAR_NAME, VARIABLE_SCOPE.THIS);
        if (hr < 0) {
            return hr;
        }
        hr = helper.declareVariable(PROP_VAR_LIST_NAME, VARIABLE_SCOPE.THIS);
        if (hr < 0) {
            return hr;
        }
        hr = helper.loadCarryable(carryableGuid, carryableColorTable, PROP_VAR_NAME);
        if (hr < 0) {
            return hr;
        }
        hr = helper.addCarryable(PROP_VAR_LIST_NAME, PROP_VAR_NAME, 1.0f, 1.0f);
        if (hr < 0) {
            return hr;
        }
        hr = addAnimationStartEvent(helper, callback, carryableGuid);
        if (hr < 0) {
            return hr;
        }
        hr = helper.playAnimationList(avatarName, PROP_VAR_LIST_NAME, SEQUENCED_ANIMATION_MODE.PLAYONCE, BLEND_IN_TIME, ANIMATION_CHAINING_MODE.REPLACE, null);
        if (hr < 0) {
            return hr;
        }
        return hr;
    }

    public String getCurrentlyPlayingAnimation(String avatarName, int iContext) {
        if (iContext >= NOTIFY_START_ANIMATION) {
            String rv = (String) animationGuidToInt.getUsingValue(Integer.valueOf(iContext - NOTIFY_START_ANIMATION));
            XLELog.Info(avatarName, "Started animation: " + rv);
            return rv;
        }
        throw new UnsupportedOperationException();
    }

    public void addEnterScript(KernelScriptingHelper helper, Core2Callbacks callback, String avatarName, ArrayList<String> runInAnimations, Vector3 avatarPos, Vector3 avatarRot, float sleepTime, float BLEND_OUT_TIME) {
        if (helper.setExceptionHandler(callback) >= 0 && helper.sleep(sleepTime) >= 0) {
            int hr = helper.setLocalTransform(avatarName, avatarPos, avatarRot, new Vector3(1.0f, 1.0f, 1.0f));
            if (hr >= 0) {
                KernelScriptingHelper kernelScriptingHelper = helper;
                Core2Callbacks core2Callbacks = callback;
                String str = avatarName;
                float f = BLEND_OUT_TIME;
                getInstance().playAnimationInternal(kernelScriptingHelper, core2Callbacks, str, (String) runInAnimations.get(this.random.nextInt(runInAnimations.size())), 0.0f, f, ANIMATION_CHAINING_MODE.REPLACE);
                if (hr >= 0 && helper.addNotifier(256, callback) < 0) {
                }
            }
        }
    }

    public int addIdleAnimationScript(KernelScriptingHelper helper, Core2Callbacks callback, String avatarName, float BLEND_IN_TIME, float BLEND_OUT_TIME, ANIMATION_CHAINING_MODE chainingMode) {
        int hr = playAnimationInternal(helper, callback, avatarName, AvatarAnimationCatalog.IDLE_ANIMATION, BLEND_IN_TIME, BLEND_OUT_TIME, chainingMode);
        if (hr < 0) {
            return hr;
        }
        return helper.addNotifier(NOTIFY_IDLE_ANIMATION, callback);
    }

    public int addAvatar(KernelScriptingHelper helper, AvatarManifest manifest, String name) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        int hr = helper.createAvatarObject(manifest, name);
        if (hr < 0) {
            return hr;
        }
        hr = helper.addObjectToScene(name, name);
        if (hr < 0) {
            return hr;
        }
        hr = helper.setLocalTransform(name, new Vector3(0.0f, 0.0f, 0.0f), new Vector3(0.0f, 0.0f, 0.0f), new Vector3(0.0f, 0.0f, 0.0f));
        if (hr < 0) {
            return hr;
        }
        return hr;
    }

    public int removeAvatar(KernelScriptingHelper helper, String name) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        return helper.removeObjectFromScene(name, Boolean.valueOf(true));
    }

    public void addShowAvatarScript(KernelScriptingHelper helper) {
        XLEAssert.assertNotNull(helper);
        XLEAssert.assertTrue(helper.setLocalTransform(AvatarEditorModel.AVATAR_NAME, new Vector3(0.0f, 0.0f, 0.0f), new Vector3(0.0f, 0.0f, 0.0f), new Vector3(1.0f, 1.0f, 1.0f)) == 0);
    }
}
