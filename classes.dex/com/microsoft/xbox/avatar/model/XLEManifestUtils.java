package com.microsoft.xbox.avatar.model;

import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEMath;

public class XLEManifestUtils {
    private static final int XLE_AVATAR_MANIFEST_EDITOR_CARRYABLE = 4096;
    private static final int XLE_AVATAR_MANIFEST_EDITOR_COSTUME = 8388608;
    private static final int XLE_AVATAR_MANIFEST_EDITOR_EARRINGS = 1024;
    private static final int XLE_AVATAR_MANIFEST_EDITOR_GLASSES = 256;
    private static final int XLE_AVATAR_MANIFEST_EDITOR_GLOVES = 128;
    private static final int XLE_AVATAR_MANIFEST_EDITOR_HAT = 64;
    private static final int XLE_AVATAR_MANIFEST_EDITOR_RING = 2048;
    private static final int XLE_AVATAR_MANIFEST_EDITOR_SHIRT = 8;
    private static final int XLE_AVATAR_MANIFEST_EDITOR_SHOES = 32;
    private static final int XLE_AVATAR_MANIFEST_EDITOR_TROUSERS = 16;
    private static final int XLE_AVATAR_MANIFEST_EDITOR_WRISTWEAR = 512;

    public static boolean isRingOrWristwear(String assetGuidStr) {
        if ((Integer.parseInt(assetGuidStr.substring(0, 8), 16) & 2560) != 0) {
            return true;
        }
        return false;
    }

    public static int assetGuidToAvatarEditorModelCategory(String assetGuidStr) {
        switch (getDefactoStyleCategory(assetGuidStr)) {
            case 8:
                return 16;
            case 16:
                return 32;
            case 32:
                return 64;
            case 64:
                return 128;
            case 128:
                return 256;
            case 256:
                return 512;
            case 512:
                return 1024;
            case 1024:
                return 2048;
            case 2048:
                return 4096;
            case 4096:
                return 4;
            case 8388608:
                return 8388608;
            default:
                throw new UnsupportedOperationException();
        }
    }

    private static int getDefactoStyleCategory(String assetGuidStr) {
        boolean valid = false;
        int assetMask = Integer.parseInt(assetGuidStr.substring(0, 8), 16);
        if ((assetMask & 8388608) == 8388608 || (assetMask & 24) == 24) {
            assetMask = 8388608;
        }
        int categoryMask = XLEMath.int32LowBit(assetMask & 8396792);
        if (categoryMask != 0 && XLEMath.isPowerOf2(categoryMask)) {
            valid = true;
        }
        XLEAssert.assertTrue("Category should be valid", valid);
        return categoryMask;
    }
}
