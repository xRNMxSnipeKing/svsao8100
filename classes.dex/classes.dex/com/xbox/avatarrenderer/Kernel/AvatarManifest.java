package com.xbox.avatarrenderer.Kernel;

import com.xbox.avatarrenderer.ASSET_COLOR_TABLE;
import com.xbox.avatarrenderer.Core2Renderer;
import com.xbox.avatarrenderer.WrapperBase;

public class AvatarManifest extends WrapperBase {

    public enum AVATAR_BODY_TYPE {
        UNKNOWN(0),
        MALE(1),
        FEMALE(2),
        ALL(3),
        CARRYABLE_MODEL(4),
        MAKE_DWORD(Integer.MAX_VALUE);
        
        private final int val;

        private AVATAR_BODY_TYPE(int v) {
            this.val = v;
        }

        public int getInt() {
            return this.val;
        }
    }

    public enum SkeletonVersion {
        INVALID(0),
        _1(1),
        _2(2);
        
        private final int val;

        private SkeletonVersion(int v) {
            this.val = v;
        }

        public int getInt() {
            return this.val;
        }
    }

    public native int nativeAvatarManifestGetCarryableColorTable(int i, int i2, int[] iArr);

    public native String nativeAvatarManifestGetCarryableGuid(int i, int i2);

    public native int nativeAvatarManifestGetGender(int i, int i2);

    public native int nativeAvatarManifestGetHasCarryable(int i, int i2);

    public AvatarManifest(Core2Renderer core2, int index) {
        super(core2, index);
    }

    public AVATAR_BODY_TYPE getGender() {
        AVATAR_BODY_TYPE typeOut = AVATAR_BODY_TYPE.UNKNOWN;
        if (this.m_core2 == null) {
            return typeOut;
        }
        int iType = nativeAvatarManifestGetGender(this.m_core2.GetInstanceID(), this.m_iInstanceID);
        if (iType < 0 || iType >= AVATAR_BODY_TYPE.values().length) {
            return typeOut;
        }
        return AVATAR_BODY_TYPE.values()[iType];
    }

    public Boolean getHasCarryable() {
        boolean z = false;
        if (!(this.m_core2 == null || nativeAvatarManifestGetHasCarryable(this.m_core2.GetInstanceID(), this.m_iInstanceID) == 0)) {
            z = true;
        }
        return Boolean.valueOf(z);
    }

    public String getCarryableGuid() {
        if (this.m_core2 != null) {
            return nativeAvatarManifestGetCarryableGuid(this.m_core2.GetInstanceID(), this.m_iInstanceID);
        }
        return null;
    }

    public ASSET_COLOR_TABLE getCarryableColorTable() {
        if (this.m_core2 == null) {
            return null;
        }
        int[] colorsOut = new int[3];
        if (nativeAvatarManifestGetCarryableColorTable(this.m_core2.GetInstanceID(), this.m_iInstanceID, colorsOut) == 0) {
            return new ASSET_COLOR_TABLE(colorsOut[0], colorsOut[1], colorsOut[2]);
        }
        return null;
    }
}
