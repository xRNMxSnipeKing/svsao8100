package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.xbox.avatar.model.AvatarRendererModel;
import com.microsoft.xbox.avatar.view.AvatarViewEditor;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.anim.MAAS;
import com.microsoft.xbox.toolkit.anim.MAAS.MAASAnimationType;
import com.microsoft.xbox.toolkit.anim.XLEAnimation;
import com.microsoft.xbox.xle.anim.XLEMAASAnimationPackageNavigationManager;
import java.util.ArrayList;

public abstract class AdapterBaseWithAvatar extends AdapterBaseNormal {
    protected AvatarViewEditor avatarView;
    private boolean pauseResumeDuringNavigationLifecycle;

    public AdapterBaseWithAvatar() {
        this(true);
    }

    public AdapterBaseWithAvatar(boolean pauseResumeDuringNavigationLifecycle) {
        this.avatarView = null;
        this.pauseResumeDuringNavigationLifecycle = true;
        this.pauseResumeDuringNavigationLifecycle = pauseResumeDuringNavigationLifecycle;
    }

    public void onPause() {
        super.onPause();
        XLEAssert.assertTrue(this.avatarView != null);
        if (this.avatarView != null && this.pauseResumeDuringNavigationLifecycle) {
            this.avatarView.onPause();
        }
    }

    public void onResume() {
        super.onResume();
        XLEAssert.assertTrue(this.avatarView != null);
        if (this.avatarView != null && this.pauseResumeDuringNavigationLifecycle) {
            this.avatarView.onResume();
        }
    }

    public void onSetActive() {
        super.onSetActive();
        AvatarRendererModel.getInstance().setGLThreadRunningScreen(true);
    }

    public void onSetInactive() {
        super.onSetInactive();
        AvatarRendererModel.getInstance().setGLThreadRunningScreen(false);
    }

    public ArrayList<XLEAnimation> getAnimateIn(boolean goingBack) {
        XLEAssert.assertNotNull(this.avatarView);
        ArrayList<XLEAnimation> animations = super.getAnimateIn(goingBack);
        if (animations != null) {
            XLEMAASAnimationPackageNavigationManager anim = (XLEMAASAnimationPackageNavigationManager) MAAS.getInstance().getAnimation("AvatarView");
            if (anim != null) {
                XLEAnimation avatarViewAnimation = anim.compile(MAASAnimationType.ANIMATE_IN, goingBack, this.avatarView);
                if (avatarViewAnimation != null) {
                    animations.add(avatarViewAnimation);
                }
            }
        }
        return animations;
    }

    public ArrayList<XLEAnimation> getAnimateOut(boolean goingBack) {
        XLEAssert.assertNotNull(this.avatarView);
        ArrayList<XLEAnimation> animations = super.getAnimateOut(goingBack);
        if (animations != null) {
            XLEMAASAnimationPackageNavigationManager anim = (XLEMAASAnimationPackageNavigationManager) MAAS.getInstance().getAnimation("AvatarView");
            if (anim != null) {
                XLEAnimation avatarViewAnimation = anim.compile(MAASAnimationType.ANIMATE_OUT, goingBack, this.avatarView);
                if (avatarViewAnimation != null) {
                    animations.add(avatarViewAnimation);
                }
            }
        }
        return animations;
    }

    public void onStart() {
        MAAS.getInstance().getAnimation("AvatarView");
        super.onStart();
    }
}
