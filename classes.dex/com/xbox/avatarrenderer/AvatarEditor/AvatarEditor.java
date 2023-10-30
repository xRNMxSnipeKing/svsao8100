package com.xbox.avatarrenderer.AvatarEditor;

import com.xbox.avatarrenderer.Core2Callbacks;
import com.xbox.avatarrenderer.Core2Renderer;
import com.xbox.avatarrenderer.Kernel.AvatarManifest;
import com.xbox.avatarrenderer.Kernel.Story;
import com.xbox.avatarrenderer.Kernel.StoryGroup;
import com.xbox.avatarrenderer.WrapperBase;

public class AvatarEditor extends WrapperBase {
    private native int nativeAvatarEditorApply(int i, int i2, int i3, int i4, int i5);

    private native int nativeAvatarEditorAttachAvatar(int i, int i2, String str, int i3);

    private native int nativeAvatarEditorCancel(int i, int i2, String str);

    private native int nativeAvatarEditorCreateEditableAvatar(int i, int i2, String str, String str2, int i3);

    private native int nativeAvatarEditorCreateEditableAvatar2(int i, int i2, String str, int i3, int i4);

    private native int nativeAvatarEditorDestroy(int i, int i2);

    private native int nativeAvatarEditorDetachAvatar(int i, int i2, String str);

    private native int nativeAvatarEditorGetEditOptions(int i, int i2, String str, int i3);

    private native int nativeAvatarEditorGetEditStory(int i, int i2, String str);

    private native String nativeAvatarEditorGetHexManifest(int i, int i2, String str);

    private native int nativeAvatarEditorGetManifest(int i, int i2, String str);

    private native int nativeAvatarEditorInitializeDynamicAssets(int i, int i2, String str, byte[] bArr);

    private native int nativeAvatarEditorInitializeStockAssets(int i, int i2, byte[] bArr);

    private native int nativeAvatarEditorIsAvatarEditable(int i, int i2, String str);

    private native int nativeAvatarEditorSetEventHandler(int i, int i2, int i3);

    private native int nativeAvatarEditorSetManifest(int i, int i2, String str, int i3, int i4);

    public AvatarEditor(Core2Renderer core2, int index) {
        super(core2, index);
    }

    public int createEditableAvatar(String avatarName, String gamerTag, Story story) {
        int i = -1;
        if (this.m_core2 == null) {
            return -1;
        }
        int GetInstanceID = this.m_core2.GetInstanceID();
        int i2 = this.m_iInstanceID;
        if (story != null) {
            i = story.getInstanceID();
        }
        return nativeAvatarEditorCreateEditableAvatar(GetInstanceID, i2, avatarName, gamerTag, i);
    }

    public Boolean isAvatarEditable(String avatarName) {
        boolean z = false;
        if (!(this.m_core2 == null || avatarName == null || nativeAvatarEditorIsAvatarEditable(this.m_core2.GetInstanceID(), this.m_iInstanceID, avatarName) == 0)) {
            z = true;
        }
        return Boolean.valueOf(z);
    }

    public int createEditableAvatar(String avatarName, AvatarManifest manifest, Story story) {
        int i = -1;
        if (this.m_core2 == null || manifest == null) {
            return -1;
        }
        int GetInstanceID = this.m_core2.GetInstanceID();
        int i2 = this.m_iInstanceID;
        int instanceID = manifest.getInstanceID();
        if (story != null) {
            i = story.getInstanceID();
        }
        return nativeAvatarEditorCreateEditableAvatar2(GetInstanceID, i2, avatarName, instanceID, i);
    }

    public int attachAvatar(String avatarName, AvatarManifest manifest) {
        if (this.m_core2 == null || manifest == null) {
            return -1;
        }
        return nativeAvatarEditorAttachAvatar(this.m_core2.GetInstanceID(), this.m_iInstanceID, avatarName, manifest.getInstanceID());
    }

    public int initializeDynamicAssets(String avatarName, byte[] data) {
        if (this.m_core2 != null) {
            return nativeAvatarEditorInitializeDynamicAssets(this.m_core2.GetInstanceID(), this.m_iInstanceID, avatarName, data);
        }
        return -1;
    }

    public int initializeStockAssets(byte[] data) {
        if (this.m_core2 != null) {
            return nativeAvatarEditorInitializeStockAssets(this.m_core2.GetInstanceID(), this.m_iInstanceID, data);
        }
        return -1;
    }

    public AvatarEditOptions getEditOptions(String avatarName, int optionsMask) {
        if (this.m_core2 == null) {
            return null;
        }
        int iInstOut = nativeAvatarEditorGetEditOptions(this.m_core2.GetInstanceID(), this.m_iInstanceID, avatarName, optionsMask);
        if (iInstOut >= 0) {
            return new AvatarEditOptions(this.m_core2, iInstOut);
        }
        return null;
    }

    public AvatarManifest getManifest(String avatarName) {
        if (this.m_core2 == null) {
            return null;
        }
        int iInstOut = nativeAvatarEditorGetManifest(this.m_core2.GetInstanceID(), this.m_iInstanceID, avatarName);
        if (iInstOut >= 0) {
            return new AvatarManifest(this.m_core2, iInstOut);
        }
        return null;
    }

    public int setManifest(String avatarName, AvatarManifest avatarManifest, int notificationTag) {
        if (this.m_core2 == null || avatarManifest == null) {
            return -1;
        }
        return nativeAvatarEditorSetManifest(this.m_core2.GetInstanceID(), this.m_iInstanceID, avatarName, avatarManifest.getInstanceID(), notificationTag);
    }

    public String getHexManifest(String avatarName) {
        if (this.m_core2 != null) {
            return nativeAvatarEditorGetHexManifest(this.m_core2.GetInstanceID(), this.m_iInstanceID, avatarName);
        }
        return null;
    }

    public int apply(AvatarEditOption option) {
        return apply(option, Boolean.valueOf(false), 0);
    }

    public int apply(AvatarEditOption option, Boolean bRemoveOccluders, int notificationTag) {
        if (this.m_core2 == null || option == null) {
            return -1;
        }
        return nativeAvatarEditorApply(this.m_core2.GetInstanceID(), this.m_iInstanceID, option.getInstanceID(), bRemoveOccluders.booleanValue() ? 1 : 0, notificationTag);
    }

    public StoryGroup getEditStory(String avatarName) {
        if (this.m_core2 == null) {
            return null;
        }
        int iStoryGroup = nativeAvatarEditorGetEditStory(this.m_core2.GetInstanceID(), this.m_iInstanceID, avatarName);
        if (iStoryGroup >= 0) {
            return new StoryGroup(this.m_core2, iStoryGroup);
        }
        return null;
    }

    public int setEventHandler(Core2Callbacks cb) {
        if (this.m_core2 == null || cb == null) {
            return -1;
        }
        return nativeAvatarEditorSetEventHandler(this.m_core2.GetInstanceID(), this.m_iInstanceID, this.m_core2.registerCallBack(cb));
    }

    public int detachAvatar(String avatarName) {
        if (this.m_core2 != null) {
            return nativeAvatarEditorDetachAvatar(this.m_core2.GetInstanceID(), this.m_iInstanceID, avatarName);
        }
        return -1;
    }

    public int destroy() {
        if (this.m_core2 != null) {
            return nativeAvatarEditorDestroy(this.m_core2.GetInstanceID(), this.m_iInstanceID);
        }
        return -1;
    }

    public int cancel(String avatarName) {
        if (this.m_core2 != null) {
            return nativeAvatarEditorCancel(this.m_core2.GetInstanceID(), this.m_iInstanceID, avatarName);
        }
        return -1;
    }
}
