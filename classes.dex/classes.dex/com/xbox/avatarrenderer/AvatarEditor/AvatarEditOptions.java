package com.xbox.avatarrenderer.AvatarEditor;

import com.xbox.avatarrenderer.Core2Renderer;
import com.xbox.avatarrenderer.WrapperBase;

public class AvatarEditOptions extends WrapperBase {
    private native int nativeAvatarEditOptionsGetOption(int i, int i2, int i3);

    private native int nativeAvatarEditOptionsGetOptionsCount(int i, int i2);

    public AvatarEditOptions(Core2Renderer core2, int index) {
        super(core2, index);
    }

    public int getOptionsCount() {
        if (this.m_core2 != null) {
            return nativeAvatarEditOptionsGetOptionsCount(this.m_core2.GetInstanceID(), this.m_iInstanceID);
        }
        return 0;
    }

    public AvatarEditOption getOption(int idx) {
        if (this.m_core2 == null) {
            return null;
        }
        int iInstOut = nativeAvatarEditOptionsGetOption(this.m_core2.GetInstanceID(), this.m_iInstanceID, idx);
        if (iInstOut >= 0) {
            return new AvatarEditOption(this.m_core2, iInstOut);
        }
        return null;
    }
}
