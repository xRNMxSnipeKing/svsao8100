package com.xbox.avatarrenderer.AvatarEditor;

import com.xbox.avatarrenderer.Core2Renderer;
import com.xbox.avatarrenderer.Core2Renderer.AvatarEditorEventContext;
import com.xbox.avatarrenderer.WrapperBase;

public class AvatarEditorEditEvent extends WrapperBase {
    private native String nativeAvatarEditorEditEventGetAvatarName(int i, int i2);

    private native int nativeAvatarEditorEditEventGetEditOption(int i, int i2);

    private native int nativeAvatarEditorEditEventGetErrorCode(int i, int i2);

    private native int nativeAvatarEditorEditEventGetEventContext(int i, int i2);

    private native int nativeAvatarEditorEditEventGetTag(int i, int i2);

    private native int nativeAvatarEditorEditEventGetUpdatesCounter(int i, int i2);

    public AvatarEditorEditEvent(Core2Renderer core2, int index) {
        super(core2, index);
    }

    public String getAvatarName() {
        if (this.m_core2 != null) {
            return nativeAvatarEditorEditEventGetAvatarName(this.m_core2.GetInstanceID(), this.m_iInstanceID);
        }
        return null;
    }

    public int getTag() {
        if (this.m_core2 != null) {
            return nativeAvatarEditorEditEventGetTag(this.m_core2.GetInstanceID(), this.m_iInstanceID);
        }
        return -1;
    }

    public int getUpdatesCounter() {
        if (this.m_core2 != null) {
            return nativeAvatarEditorEditEventGetUpdatesCounter(this.m_core2.GetInstanceID(), this.m_iInstanceID);
        }
        return -1;
    }

    public int getErrorCode() {
        if (this.m_core2 != null) {
            return nativeAvatarEditorEditEventGetErrorCode(this.m_core2.GetInstanceID(), this.m_iInstanceID);
        }
        return -1;
    }

    public AvatarEditOption getEditOption() {
        if (this.m_core2 == null) {
            return null;
        }
        int iAEO = nativeAvatarEditorEditEventGetEditOption(this.m_core2.GetInstanceID(), this.m_iInstanceID);
        if (iAEO >= 0) {
            return new AvatarEditOption(this.m_core2, iAEO);
        }
        return null;
    }

    public AvatarEditorEventContext getEventContext() {
        AvatarEditorEventContext context = AvatarEditorEventContext.UNDEFINED;
        if (this.m_core2 == null) {
            return context;
        }
        int iContext = nativeAvatarEditorEditEventGetEventContext(this.m_core2.GetInstanceID(), this.m_iInstanceID);
        if (iContext < 0 || iContext >= AvatarEditorEventContext.values().length) {
            return context;
        }
        return AvatarEditorEventContext.values()[iContext];
    }
}
