package com.xbox.avatarrenderer.AvatarEditor;

import com.xbox.avatarrenderer.Core2Renderer;
import com.xbox.avatarrenderer.Core2Renderer.ANIMATION_CHAINING_MODE;
import com.xbox.avatarrenderer.Core2Renderer.AVATAREDITOR_CAMERA_POSE;
import com.xbox.avatarrenderer.Kernel.Command;
import com.xbox.avatarrenderer.Kernel.Story;
import com.xbox.avatarrenderer.WrapperBase;

public class AvatarEditorFunctionSet extends WrapperBase {
    private native int nativeAvatarEditorFunctionSetAddCommand(int i, int i2, int i3);

    private native int nativeAvatarEditorFunctionSetAddNotification(int i, int i2, int i3);

    private native int nativeAvatarEditorFunctionSetAnimateCameraToComponent(int i, int i2, String str, float f, float f2, float f3, float f4);

    private native int nativeAvatarEditorFunctionSetAnimateCameraToPose(int i, int i2, int i3, float f, float f2, float f3, float f4);

    private native int nativeAvatarEditorFunctionSetBeginApply(int i, int i2, int i3, int i4, int i5);

    private native float nativeAvatarEditorFunctionSetCalculateLookAtFov(int i, int i2, int i3);

    private native int nativeAvatarEditorFunctionSetCreateParallelStory(int i, int i2);

    private native int nativeAvatarEditorFunctionSetEndApply(int i, int i2, int i3, int i4);

    private native int nativeAvatarEditorFunctionSetGetPrimaryStory(int i, int i2);

    private native int nativeAvatarEditorFunctionSetPlayAnimation(int i, int i2, String str, float f, float f2, int i3, String str2);

    public AvatarEditorFunctionSet(Core2Renderer core2, int index) {
        super(core2, index);
    }

    public int animateCameraToPose(AVATAREDITOR_CAMERA_POSE poseId, float animationLength, float acceleration, float blendInLength, float blendOutLength) {
        if (this.m_core2 == null || poseId == null) {
            return -1;
        }
        return nativeAvatarEditorFunctionSetAnimateCameraToPose(this.m_core2.GetInstanceID(), this.m_iInstanceID, poseId.ordinal(), animationLength, acceleration, blendInLength, blendOutLength);
    }

    public int animateCameraToComponent(String componentGuid, float animationLength, float acceleration, float blendInLength, float blendOutLength) {
        if (this.m_core2 != null) {
            return nativeAvatarEditorFunctionSetAnimateCameraToComponent(this.m_core2.GetInstanceID(), this.m_iInstanceID, componentGuid, animationLength, acceleration, blendInLength, blendOutLength);
        }
        return -1;
    }

    public int addNotification(int tag) {
        if (this.m_core2 != null) {
            return nativeAvatarEditorFunctionSetAddNotification(this.m_core2.GetInstanceID(), this.m_iInstanceID, tag);
        }
        return -1;
    }

    public Story getPrimaryStory() {
        if (this.m_core2 == null) {
            return null;
        }
        int iStory = nativeAvatarEditorFunctionSetGetPrimaryStory(this.m_core2.GetInstanceID(), this.m_iInstanceID);
        if (iStory >= 0) {
            return new Story(this.m_core2, iStory);
        }
        return null;
    }

    public Story createParallelStory() {
        if (this.m_core2 == null) {
            return null;
        }
        int iStory = nativeAvatarEditorFunctionSetCreateParallelStory(this.m_core2.GetInstanceID(), this.m_iInstanceID);
        if (iStory >= 0) {
            return new Story(this.m_core2, iStory);
        }
        return null;
    }

    public int addCommand(Command command) {
        if (this.m_core2 == null || command == null) {
            return -1;
        }
        return nativeAvatarEditorFunctionSetAddCommand(this.m_core2.GetInstanceID(), this.m_iInstanceID, command.getInstanceID());
    }

    public float calculateLookAtFov(AVATAREDITOR_CAMERA_POSE poseId) {
        if (this.m_core2 != null) {
            return nativeAvatarEditorFunctionSetCalculateLookAtFov(this.m_core2.GetInstanceID(), this.m_iInstanceID, poseId.ordinal());
        }
        return 0.7f;
    }

    public int beginApply(AvatarEditOption option, Boolean bRemoveOccluders, int notificationTag) {
        if (this.m_core2 == null || option == null) {
            return -1;
        }
        return nativeAvatarEditorFunctionSetBeginApply(this.m_core2.GetInstanceID(), this.m_iInstanceID, option.getInstanceID(), bRemoveOccluders.booleanValue() ? 1 : 0, notificationTag);
    }

    public int beginApply(AvatarEditOption option) {
        return beginApply(option, Boolean.valueOf(false), 0);
    }

    public int endApply(AvatarEditOption option, int notificationTag) {
        if (this.m_core2 == null || option == null) {
            return -1;
        }
        return nativeAvatarEditorFunctionSetEndApply(this.m_core2.GetInstanceID(), this.m_iInstanceID, option.getInstanceID(), notificationTag);
    }

    public int playAnimation(String animationGuid, float blendInDuration, float blendOutDuration, ANIMATION_CHAINING_MODE chainingMode, String eventHandler) {
        if (this.m_core2 == null || animationGuid == null) {
            return -1;
        }
        return nativeAvatarEditorFunctionSetPlayAnimation(this.m_core2.GetInstanceID(), this.m_iInstanceID, animationGuid, blendInDuration, blendOutDuration, chainingMode != null ? chainingMode.getInt() : ANIMATION_CHAINING_MODE.IMMEDIATE.getInt(), eventHandler);
    }
}
