package com.microsoft.xbox.avatar.model;

import com.xbox.avatarrenderer.Vector3;

public class AvatarPositionCatalog {
    private static AvatarPosition[] positionData = new AvatarPosition[]{new AvatarPosition(-0.058202d, 0.0d, -0.02227d, 0.474137753d, -0.021022d, 0.0d), new AvatarPosition(0.0d, -0.559795d, 0.0d, 0.474137753d, -0.40702d, -1.01913d), new AvatarPosition(0.134677d, -0.35697d, -0.019159d, 0.474137753d, -0.380767d, -1.176682d), new AvatarPosition(0.0d, -1.046294d, 0.0d, 0.474137753d, -0.427073d, -1.169648d), new AvatarPosition(0.0d, -1.024629d, 0.0d, 0.474137753d, -0.416466d, -1.129985d), new AvatarPosition(0.059434d, -0.166752d, -0.01958d, 0.474137753d, -0.412483d, -1.240277d), new AvatarPosition(0.059434d, -0.166752d, -0.01958d, 0.474137753d, -0.412483d, -1.240277d), new AvatarPosition(0.0d, -0.191352d, 0.0d, 0.474137753d, -0.393396d, -0.954294d), new AvatarPosition(0.0d, -0.468548d, 0.0d, 0.474137753d, -0.021022d, 0.495782d), new AvatarPosition(0.039005d, -0.028605d, -0.01929d, 0.474137753d, -0.389398d, -1.161231d), new AvatarPosition(0.039005d, -0.028605d, -0.01929d, 0.474137753d, -0.389398d, -1.161231d), new AvatarPosition(0.0d, -0.321689d, 0.0d, 0.474137753d, -0.159133d, -0.845112d), new AvatarPosition(0.031832d, -0.177963d, 0.0d, 0.474137753d, 0.261957d, -0.956476d), new AvatarPosition(-0.040137d, -0.33274d, 0.046627d, 0.474137753d, 0.349881d, -0.83432d), new AvatarPosition(0.0d, -0.831693d, 0.0d, 0.474137753d, -0.459625d, -1.116506d), new AvatarPosition(0.0d, 0.0d, 0.0d, 0.474137753d, -0.021022d, 0.495782d), new AvatarPosition(0.0d, -0.468548d, 0.0d, 0.474137753d, -0.021022d, 0.0d), new AvatarPosition(0.0d, -0.831693d, 0.0d, 0.474137753d, -0.419807d, -1.116506d), new AvatarPosition(0.0d, -0.15d, 0.0d, 0.474137753d, -0.418856d, -1.116506d), new AvatarPosition(0.031832d, 0.940619d, 0.0d, 0.474137753d, 0.161957d, -0.956476d), new AvatarPosition(0.031832d, 0.940619d, 0.0d, 0.474137753d, 0.161957d, -0.956476d), new AvatarPosition(0.031832d, -1.230619d, 0.0d, 0.474137753d, 0.161957d, -0.956476d), new AvatarPosition(0.031832d, 0.0d, 0.0d, 0.474137753d, 0.161957d, -0.956476d), new AvatarPosition(0.031832d, -2.289999d, 0.0d, 0.474137753d, 0.261957d, -0.956476d), new AvatarPosition(0.031832d, -1.999999d, 0.0d, 0.474137753d, 0.261957d, -0.956476d), new AvatarPosition(0.031832d, 0.0d, 0.0d, 0.474137753d, 0.261957d, -0.956476d), new AvatarPosition(0.0d, 0.0d, 0.0d, 0.8d, -0.021022d, 0.0d), new AvatarPosition(0.0d, 0.0d, 0.0d, 0.8d, -0.021022d, 0.0d)};

    public enum AvatarCameraPositionType {
        Body,
        Hair,
        Chin,
        Ear,
        Nose,
        Lips,
        Eyes,
        Eyebrows,
        Awardables,
        Features_Beards,
        Features_Skin,
        Tops,
        Bottoms,
        Shoes,
        Headgear,
        Carryables,
        Costumes,
        Accessories_Glasses,
        Accessories_Earrings,
        Accessories_Gloves,
        Accessories_Wrist_Right,
        Accessories_Wrist_Left,
        Accessories_Wrist_Center,
        Accessories_Ring_Right,
        Accessories_Ring_Left,
        Accessories_Ring_Center,
        BodyResize,
        Main,
        Count
    }

    public static class AvatarPosition {
        private Vector3 pos;
        private Vector3 rot;

        public AvatarPosition(double rotX, double rotY, double rotZ, double posX, double posY, double posZ) {
            this.rot = new Vector3((float) rotX, (float) rotY, (float) rotZ);
            this.pos = new Vector3((float) posX, (float) posY, (float) posZ);
        }

        public Vector3 getRot() {
            return this.rot;
        }

        public Vector3 getPos() {
            return this.pos;
        }
    }

    public static AvatarPosition getAvatarPosition(int categoryMask, String assetId) {
        return getAvatarPosition(getAvatarPositionType(categoryMask, assetId));
    }

    public static AvatarCameraPositionType getAvatarPositionType(int categoryMask, String assetId) {
        if (categoryMask == AvatarEditorModel.AVATAREDIT_OPTION_AWARDS) {
            categoryMask = XLEManifestUtils.assetGuidToAvatarEditorModelCategory(assetId);
        }
        switch (categoryMask) {
            case AvatarEditorModel.AVATAREDIT_OPTION_COLOR_FACIAL_HAIR /*-2147483648*/:
            case AvatarEditorModel.AVATAREDIT_OPTION_FEATURES_FACIAL_HAIR /*65536*/:
                return AvatarCameraPositionType.Features_Beards;
            case 1:
                return AvatarCameraPositionType.Body;
            case 4:
                return AvatarCameraPositionType.Carryables;
            case 8:
            case AvatarEditorModel.AVATAREDIT_OPTION_COLOR_HAIR /*134217728*/:
                return AvatarCameraPositionType.Hair;
            case 16:
                return AvatarCameraPositionType.Tops;
            case 32:
                return AvatarCameraPositionType.Bottoms;
            case 64:
                return AvatarCameraPositionType.Shoes;
            case 128:
                return AvatarCameraPositionType.Headgear;
            case 256:
                return AvatarCameraPositionType.Accessories_Gloves;
            case AvatarEditorModel.AVATAREDIT_OPTION_GLASSES /*512*/:
                return AvatarCameraPositionType.Accessories_Glasses;
            case 1024:
                return AvatarCameraPositionType.Accessories_Wrist_Center;
            case AvatarEditorModel.AVATAREDIT_OPTION_EARRINGS /*2048*/:
                return AvatarCameraPositionType.Accessories_Earrings;
            case AvatarEditorModel.AVATAREDIT_OPTION_RINGS /*4096*/:
                return AvatarCameraPositionType.Accessories_Ring_Center;
            case AvatarEditorModel.AVATAREDIT_OPTION_FEATURES_EYES /*8192*/:
            case AvatarEditorModel.AVATAREDIT_OPTION_COLOR_EYE /*33554432*/:
            case AvatarEditorModel.AVATAREDIT_OPTION_COLOR_EYE_SHADOW /*67108864*/:
                return AvatarCameraPositionType.Eyes;
            case AvatarEditorModel.AVATAREDIT_OPTION_FEATURES_EYEBROWS /*16384*/:
            case AvatarEditorModel.AVATAREDIT_OPTION_COLOR_EYEBROW /*536870912*/:
                return AvatarCameraPositionType.Eyebrows;
            case AvatarEditorModel.AVATAREDIT_OPTION_FEATURES_MOUTH /*32768*/:
            case AvatarEditorModel.AVATAREDIT_OPTION_COLOR_LIP /*1073741824*/:
                return AvatarCameraPositionType.Lips;
            case AvatarEditorModel.AVATAREDIT_OPTION_FEATURES_FACIAL_FEATURES /*131072*/:
            case AvatarEditorModel.AVATAREDIT_OPTION_COLOR_FACIAL_FEATURE /*268435456*/:
                return AvatarCameraPositionType.Features_Skin;
            case AvatarEditorModel.AVATAREDIT_OPTION_FEATURES_NOSE /*524288*/:
                return AvatarCameraPositionType.Nose;
            case 1048576:
                return AvatarCameraPositionType.Chin;
            case AvatarEditorModel.AVATAREDIT_OPTION_FEATURES_EARS /*2097152*/:
                return AvatarCameraPositionType.Ear;
            case AvatarEditorModel.AVATAREDIT_OPTION_DRESS_UP /*8388608*/:
                return AvatarCameraPositionType.Costumes;
            case AvatarEditorModel.AVATAREDIT_OPTION_COLOR_SKIN /*16777216*/:
                return AvatarCameraPositionType.Costumes;
            default:
                throw new UnsupportedOperationException();
        }
    }

    public static AvatarPosition getAvatarPosition(AvatarCameraPositionType type) {
        return positionData[type.ordinal()];
    }
}
