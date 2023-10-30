package com.microsoft.xbox.avatar.model;

public class AvatarClosetSpinAnimation {
    private String animationGuid;
    private float sleepToApplyTime;

    public AvatarClosetSpinAnimation(String guid, float sleep) {
        this.animationGuid = guid;
        this.sleepToApplyTime = sleep;
    }

    public String getAnimationGuid() {
        return this.animationGuid;
    }

    public float getAnimationSleepTime() {
        return this.sleepToApplyTime;
    }
}
