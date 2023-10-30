package com.xbox.avatarrenderer.Kernel;

import com.xbox.avatarrenderer.ASSET_COLOR_TABLE;
import com.xbox.avatarrenderer.Core2Renderer;
import com.xbox.avatarrenderer.Core2Renderer.AVATAR_DYNAMIC_COLOR_TYPE;
import com.xbox.avatarrenderer.Kernel.AvatarManifest.AVATAR_BODY_TYPE;
import com.xbox.avatarrenderer.WrapperBase;

public class AvatarManifestEditor extends WrapperBase {
    public static final int AVATAR_ASSET_POSITION_BACK = 32;
    public static final int AVATAR_ASSET_POSITION_DOWN = 8;
    public static final int AVATAR_ASSET_POSITION_FRONT = 16;
    public static final int AVATAR_ASSET_POSITION_LEFT = 1;
    public static final int AVATAR_ASSET_POSITION_RIGHT = 2;
    public static final int AVATAR_ASSET_POSITION_UNKNOWN = 0;
    public static final int AVATAR_ASSET_POSITION_UP = 4;

    private native int nativeAvatarManifestEditorGetAssetPosition(int i, int i2, String str);

    private native int nativeAvatarManifestEditorGetAvatarBodyType(int i, int i2);

    private native int nativeAvatarManifestEditorGetAvatarColor(int i, int i2, int i3);

    private native int nativeAvatarManifestEditorGetDressDefaultClothes(int i, int i2);

    private native int nativeAvatarManifestEditorGetEyeShadowsEnabled(int i, int i2);

    private native float nativeAvatarManifestEditorGetHeightFactor(int i, int i2);

    private native int nativeAvatarManifestEditorGetManifest(int i, int i2);

    private native float nativeAvatarManifestEditorGetWeightFactor(int i, int i2);

    private native int nativeAvatarManifestEditorIsAssetPresent(int i, int i2, String str, int i3, int i4, int i5, int i6);

    private native int nativeAvatarManifestEditorIsReplacementTexturePresent(int i, int i2, String str);

    public AvatarManifestEditor(Core2Renderer core2, int index) {
        super(core2, index);
    }

    public float getWeightFactor() {
        if (this.m_core2 != null) {
            return nativeAvatarManifestEditorGetWeightFactor(this.m_core2.GetInstanceID(), this.m_iInstanceID);
        }
        return 0.0f;
    }

    public float getHeightFactor() {
        if (this.m_core2 != null) {
            return nativeAvatarManifestEditorGetHeightFactor(this.m_core2.GetInstanceID(), this.m_iInstanceID);
        }
        return 0.0f;
    }

    public Boolean getDressDefaultClothes() {
        boolean z = false;
        if (!(this.m_core2 == null || nativeAvatarManifestEditorGetDressDefaultClothes(this.m_core2.GetInstanceID(), this.m_iInstanceID) == 0)) {
            z = true;
        }
        return Boolean.valueOf(z);
    }

    public AvatarManifest getManifest() {
        if (this.m_core2 == null) {
            return null;
        }
        int iIndex = nativeAvatarManifestEditorGetManifest(this.m_core2.GetInstanceID(), this.m_iInstanceID);
        if (iIndex >= 0) {
            return new AvatarManifest(this.m_core2, iIndex);
        }
        return null;
    }

    public AVATAR_BODY_TYPE getAvatarBodyType() {
        AVATAR_BODY_TYPE typeOut = AVATAR_BODY_TYPE.UNKNOWN;
        if (this.m_core2 == null) {
            return typeOut;
        }
        int iIndex = nativeAvatarManifestEditorGetAvatarBodyType(this.m_core2.GetInstanceID(), this.m_iInstanceID);
        if (iIndex < 0 || iIndex >= AVATAR_BODY_TYPE.values().length) {
            return typeOut;
        }
        return AVATAR_BODY_TYPE.values()[iIndex];
    }

    public Boolean getEyeShadowsEnabled() {
        boolean z = false;
        if (!(this.m_core2 == null || nativeAvatarManifestEditorGetEyeShadowsEnabled(this.m_core2.GetInstanceID(), this.m_iInstanceID) == 0)) {
            z = true;
        }
        return Boolean.valueOf(z);
    }

    public int getAvatarColor(AVATAR_DYNAMIC_COLOR_TYPE type) {
        if (this.m_core2 == null || type == null) {
            return 0;
        }
        return nativeAvatarManifestEditorGetAvatarColor(this.m_core2.GetInstanceID(), this.m_iInstanceID, type.ordinal());
    }

    public Boolean isAssetPresent(String assetGuid, ASSET_COLOR_TABLE customColors) {
        boolean z = false;
        if (this.m_core2 != null) {
            int i;
            int i2;
            int i3;
            int i4;
            int GetInstanceID = this.m_core2.GetInstanceID();
            int i5 = this.m_iInstanceID;
            if (customColors != null) {
                i = 1;
            } else {
                i = 0;
            }
            if (customColors != null) {
                i2 = customColors.color1;
            } else {
                i2 = 0;
            }
            if (customColors != null) {
                i3 = customColors.color2;
            } else {
                i3 = 0;
            }
            if (customColors != null) {
                i4 = customColors.color3;
            } else {
                i4 = 0;
            }
            if (nativeAvatarManifestEditorIsAssetPresent(GetInstanceID, i5, assetGuid, i, i2, i3, i4) != 0) {
                z = true;
            }
        }
        return Boolean.valueOf(z);
    }

    public Boolean isReplacementTexturePresent(String ReplacementTextureGuid) {
        boolean z = false;
        if (!(this.m_core2 == null || nativeAvatarManifestEditorIsReplacementTexturePresent(this.m_core2.GetInstanceID(), this.m_iInstanceID, ReplacementTextureGuid) == 0)) {
            z = true;
        }
        return Boolean.valueOf(z);
    }

    public int getAssetPosition(String assetGuid) {
        if (this.m_core2 != null) {
            return nativeAvatarManifestEditorGetAssetPosition(this.m_core2.GetInstanceID(), this.m_iInstanceID, assetGuid);
        }
        return -1;
    }
}
