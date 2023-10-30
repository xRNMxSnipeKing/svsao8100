package com.xbox.avatarrenderer.AvatarEditor;

import com.xbox.avatarrenderer.Core2Renderer;
import com.xbox.avatarrenderer.WrapperBase;

public class AvatarEditOption extends WrapperBase {
    static final String[] subXML = new String[]{"", "&", "<", ">", "\"", "'"};
    static final String[] targetXML = new String[]{"&#xD;", "&amp;", "&lt;", "&gt;", "&quot;", "&apos;"};

    private native String nativeAvatarEditOptionGetAssetGuid(int i, int i2);

    private native String nativeAvatarEditOptionGetColor(int i, int i2);

    private native int nativeAvatarEditOptionGetColorOptions(int i, int i2);

    private native String nativeAvatarEditOptionGetDescription(int i, int i2);

    private native String nativeAvatarEditOptionGetImageUrl(int i, int i2, int i3);

    private native int nativeAvatarEditOptionGetIsAward(int i, int i2);

    private native int nativeAvatarEditOptionGetIsColor(int i, int i2);

    private native int nativeAvatarEditOptionGetIsColorableAsset(int i, int i2);

    private native int nativeAvatarEditOptionGetIsEnabled(int i, int i2);

    private native int nativeAvatarEditOptionGetIsMarketPlaceAsset(int i, int i2);

    private native int nativeAvatarEditOptionGetIsSelected(int i, int i2);

    private native String nativeAvatarEditOptionGetTitle(int i, int i2);

    public AvatarEditOption(Core2Renderer core2, int index) {
        super(core2, index);
    }

    public String getAssetGuid() {
        if (this.m_core2 == null || this.m_core2 == null) {
            return null;
        }
        return nativeAvatarEditOptionGetAssetGuid(this.m_core2.GetInstanceID(), this.m_iInstanceID);
    }

    String substituteXMLCanonicals(String str) {
        int nSubs = targetXML.length;
        int i = 0;
        while (i < nSubs) {
            if (str != null && str.contains(targetXML[i])) {
                str = str.replace(targetXML[i], subXML[i]);
            }
            i++;
        }
        return str;
    }

    public String getTitle() {
        if (this.m_core2 != null) {
            return substituteXMLCanonicals(nativeAvatarEditOptionGetTitle(this.m_core2.GetInstanceID(), this.m_iInstanceID));
        }
        return null;
    }

    public String getDescription() {
        if (this.m_core2 != null) {
            return substituteXMLCanonicals(nativeAvatarEditOptionGetDescription(this.m_core2.GetInstanceID(), this.m_iInstanceID));
        }
        return null;
    }

    public String getImageUrl(int resolution) {
        if (this.m_core2 != null) {
            return nativeAvatarEditOptionGetImageUrl(this.m_core2.GetInstanceID(), this.m_iInstanceID, resolution);
        }
        return null;
    }

    public AvatarEditOptions getColorOptions() {
        if (this.m_core2 == null) {
            return null;
        }
        int iInstOut = nativeAvatarEditOptionGetColorOptions(this.m_core2.GetInstanceID(), this.m_iInstanceID);
        if (iInstOut >= 0) {
            return new AvatarEditOptions(this.m_core2, iInstOut);
        }
        return null;
    }

    public Boolean getIsEnabled() {
        boolean z = false;
        if (!(this.m_core2 == null || nativeAvatarEditOptionGetIsEnabled(this.m_core2.GetInstanceID(), this.m_iInstanceID) == 0)) {
            z = true;
        }
        return Boolean.valueOf(z);
    }

    public Boolean getIsSelected() {
        boolean z = false;
        if (!(this.m_core2 == null || nativeAvatarEditOptionGetIsSelected(this.m_core2.GetInstanceID(), this.m_iInstanceID) == 0)) {
            z = true;
        }
        return Boolean.valueOf(z);
    }

    public Boolean getIsAward() {
        boolean z = false;
        if (!(this.m_core2 == null || nativeAvatarEditOptionGetIsAward(this.m_core2.GetInstanceID(), this.m_iInstanceID) == 0)) {
            z = true;
        }
        return Boolean.valueOf(z);
    }

    public Boolean getIsMarketPlaceAsset() {
        boolean z = false;
        if (!(this.m_core2 == null || nativeAvatarEditOptionGetIsMarketPlaceAsset(this.m_core2.GetInstanceID(), this.m_iInstanceID) == 0)) {
            z = true;
        }
        return Boolean.valueOf(z);
    }

    public Boolean getIsColorableAsset() {
        boolean z = false;
        if (!(this.m_core2 == null || nativeAvatarEditOptionGetIsColorableAsset(this.m_core2.GetInstanceID(), this.m_iInstanceID) == 0)) {
            z = true;
        }
        return Boolean.valueOf(z);
    }

    public Boolean getIsColor() {
        boolean z = false;
        if (!(this.m_core2 == null || nativeAvatarEditOptionGetIsColor(this.m_core2.GetInstanceID(), this.m_iInstanceID) == 0)) {
            z = true;
        }
        return Boolean.valueOf(z);
    }

    public int getColor(int[] colorsOut3) {
        if (this.m_core2 == null || colorsOut3 == null || colorsOut3.length < 3) {
            return -1;
        }
        String strColors3 = nativeAvatarEditOptionGetColor(this.m_core2.GetInstanceID(), this.m_iInstanceID);
        if (strColors3 == null || strColors3.length() != 24) {
            return -1;
        }
        colorsOut3[0] = (int) Long.parseLong(strColors3.substring(0, 8), 16);
        colorsOut3[1] = (int) Long.parseLong(strColors3.substring(8, 16), 16);
        colorsOut3[2] = (int) Long.parseLong(strColors3.substring(16, 24), 16);
        return 0;
    }
}
