package com.microsoft.xbox.avatar.model;

import android.view.MotionEvent;
import com.microsoft.xbox.avatar.view.XLEAvatarAnimationAction;
import com.xbox.avatarrenderer.Core2Callbacks;
import com.xbox.avatarrenderer.Kernel.AvatarManifestEditor;
import com.xbox.avatarrenderer.Vector3;

public abstract class AvatarViewActorVM implements Core2Callbacks {
    public static final int ANIMATION_HISTORY_SIZE = 20;

    public abstract void animate(XLEAvatarAnimationAction xLEAvatarAnimationAction, int i);

    public abstract void clearLastPlayedAnimations();

    public abstract boolean getIsLoaded();

    public abstract boolean getIsMale();

    public abstract String[] getLastPlayedAnimations();

    public abstract AvatarManifestEditor getManifestEditor();

    public abstract void hitboxOnClick();

    public abstract void hitboxOnTouch(MotionEvent motionEvent);

    public abstract void initializeActorSceneData(int i);

    public abstract void initializeActorSpecificData(int i, Vector3 vector3, Vector3 vector32, boolean z);

    public abstract void onFinishAnimation();

    public abstract void onSceneBegin();

    public abstract void onSceneEnd();

    public abstract void setNotifyInitializedCallback(Runnable runnable);

    public abstract void setShadowtarVisibilityChangedCallback(Runnable runnable);

    public abstract void setToEntering();

    public abstract void setToShadowtar();

    public abstract void setViewToSignalOnShadowtarVisible(AvatarViewVM avatarViewVM);

    public boolean getIsShadowtarVisible() {
        return false;
    }

    public boolean getIsInInitializedState() {
        return false;
    }

    public void onDestroy() {
        AvatarRendererModel.getInstance().getCore2Model().unregisterCallBack(this);
    }
}
