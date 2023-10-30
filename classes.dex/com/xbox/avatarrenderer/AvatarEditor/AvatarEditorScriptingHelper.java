package com.xbox.avatarrenderer.AvatarEditor;

import com.xbox.avatarrenderer.Core2Renderer;
import com.xbox.avatarrenderer.Kernel.AvatarManifest;
import com.xbox.avatarrenderer.WrapperBase;

public class AvatarEditorScriptingHelper extends WrapperBase {
    private native int nativeAvatarEditorSHAttachAvatar(int i, int i2, String str, String str2, int i3);

    private native int nativeAvatarEditorSHAttachAvatar2(int i, int i2, String str, int i3, int i4);

    private native int nativeAvatarEditorSHDetachAvatar(int i, int i2, String str);

    public AvatarEditorScriptingHelper(Core2Renderer core2, int index) {
        super(core2, index);
    }

    public int attachAvatar(String avatarName, String manifestVarName, AvatarEditor avatarEditor) {
        int i = -1;
        if (this.m_core2 == null) {
            return -1;
        }
        int GetInstanceID = this.m_core2.GetInstanceID();
        int i2 = this.m_iInstanceID;
        if (avatarEditor != null) {
            i = avatarEditor.getInstanceID();
        }
        return nativeAvatarEditorSHAttachAvatar(GetInstanceID, i2, avatarName, manifestVarName, i);
    }

    public int attachAvatar(String avatarName, AvatarManifest avatarManifest, AvatarEditor avatarEditor) {
        int i = -1;
        if (this.m_core2 == null || avatarManifest == null) {
            return -1;
        }
        int GetInstanceID = this.m_core2.GetInstanceID();
        int i2 = this.m_iInstanceID;
        int instanceID = avatarManifest.getInstanceID();
        if (avatarEditor != null) {
            i = avatarEditor.getInstanceID();
        }
        return nativeAvatarEditorSHAttachAvatar2(GetInstanceID, i2, avatarName, instanceID, i);
    }

    public int detachAvatar(String avatarName) {
        if (this.m_core2 != null) {
            return nativeAvatarEditorSHDetachAvatar(this.m_core2.GetInstanceID(), this.m_iInstanceID, avatarName);
        }
        return -1;
    }
}
