package com.microsoft.xbox.avatar.view;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.avatar.model.AvatarEditorModel;
import com.microsoft.xbox.toolkit.XboxApplication;

public class AvatarEditorSelectTitleCatalog {
    public static int getTitle(int categoryMask) {
        switch (categoryMask) {
            case AvatarEditorModel.AVATAREDIT_OPTION_COLOR_FACIAL_HAIR /*-2147483648*/:
                return R.string.avatar_category_facialhaircolor;
            case AvatarEditorModel.AVATAREDIT_OPTION_MASK_COLORS_MALE /*-16777216*/:
                return R.string.avatar_category_colors;
            case 1:
                return R.string.avatar_category_bodysize;
            case 4:
                return R.string.avatar_category_props;
            case 8:
                return R.string.avatar_category_hair;
            case 16:
                return R.string.avatar_category_tops;
            case 32:
                return R.string.avatar_category_bottoms;
            case 64:
                return R.string.avatar_category_shoes;
            case 128:
                return R.string.avatar_category_headwear;
            case 256:
                return R.string.avatar_category_gloves;
            case AvatarEditorModel.AVATAREDIT_OPTION_GLASSES /*512*/:
                return R.string.avatar_category_glasses;
            case 1024:
                return R.string.avatar_category_wristwear;
            case AvatarEditorModel.AVATAREDIT_OPTION_EARRINGS /*2048*/:
                return R.string.avatar_category_earrings;
            case AvatarEditorModel.AVATAREDIT_OPTION_RINGS /*4096*/:
                return R.string.avatar_category_rings;
            case AvatarEditorModel.AVATAREDIT_OPTION_MASK_STYLE_ACCESSORIES /*7936*/:
                return R.string.avatar_category_accessories;
            case AvatarEditorModel.AVATAREDIT_OPTION_FEATURES_EYES /*8192*/:
                return R.string.avatar_category_eyes;
            case AvatarEditorModel.AVATAREDIT_OPTION_FEATURES_EYEBROWS /*16384*/:
                return R.string.avatar_category_eyebrows;
            case AvatarEditorModel.AVATAREDIT_OPTION_MASK_FEATURES_EYES_AND_EYEBROWS /*24576*/:
                return R.string.avatar_category_eyes_and_eyebrows;
            case AvatarEditorModel.AVATAREDIT_OPTION_FEATURES_MOUTH /*32768*/:
                return R.string.avatar_category_mouth;
            case AvatarEditorModel.AVATAREDIT_OPTION_FEATURES_FACIAL_HAIR /*65536*/:
                return R.string.avatar_category_facialhair;
            case AvatarEditorModel.AVATAREDIT_OPTION_FEATURES_FACIAL_FEATURES /*131072*/:
                return R.string.avatar_category_facialfeatures;
            case AvatarEditorModel.AVATAREDIT_OPTION_MASK_FEATURES_FACIAL_FEATURES_MALE /*196608*/:
                return R.string.avatar_category_face;
            case AvatarEditorModel.AVATAREDIT_OPTION_FEATURES_NOSE /*524288*/:
                return R.string.avatar_category_nose;
            case 1048576:
                return R.string.avatar_category_chin;
            case AvatarEditorModel.AVATAREDIT_OPTION_MASK_FEATURES_CHIN_AND_MOUTH /*1081344*/:
                return R.string.avatar_category_chin_and_mouth;
            case AvatarEditorModel.AVATAREDIT_OPTION_FEATURES_EARS /*2097152*/:
                return R.string.avatar_category_ears;
            case AvatarEditorModel.AVATAREDIT_OPTION_MASK_FEATURES_FEMALE /*3858440*/:
            case AvatarEditorModel.AVATAREDIT_OPTION_MASK_FEATURES_MALE /*3923976*/:
                return R.string.avatar_category_features;
            case AvatarEditorModel.AVATAREDIT_OPTION_AWARDS /*4194304*/:
                return R.string.avatar_category_awards;
            case AvatarEditorModel.AVATAREDIT_OPTION_DRESS_UP /*8388608*/:
                return R.string.avatar_category_dressup;
            case AvatarEditorModel.AVATAREDIT_OPTION_MASK_STYLE /*12591092*/:
                return R.string.avatar_category_style;
            case AvatarEditorModel.AVATAREDIT_OPTION_COLOR_SKIN /*16777216*/:
                return R.string.avatar_category_skincolor;
            case AvatarEditorModel.AVATAREDIT_OPTION_COLOR_EYE /*33554432*/:
                return R.string.avatar_category_eyecolor;
            case AvatarEditorModel.AVATAREDIT_OPTION_COLOR_EYE_SHADOW /*67108864*/:
                return R.string.avatar_category_eyeshadow;
            case AvatarEditorModel.AVATAREDIT_OPTION_COLOR_HAIR /*134217728*/:
                return R.string.avatar_category_haircolor;
            case AvatarEditorModel.AVATAREDIT_OPTION_COLOR_FACIAL_FEATURE /*268435456*/:
                return R.string.avatar_category_facialfeaturecolor;
            case AvatarEditorModel.AVATAREDIT_OPTION_COLOR_EYEBROW /*536870912*/:
                return R.string.avatar_category_eyebrowcolor;
            case AvatarEditorModel.AVATAREDIT_OPTION_COLOR_LIP /*1073741824*/:
                return R.string.avatar_category_lipcolor;
            case AvatarEditorModel.AVATAREDIT_OPTION_MASK_COLORS_FEMALE /*2130706432*/:
                return R.string.avatar_category_colors;
            default:
                throw new UnsupportedOperationException();
        }
    }

    public static String getRemoveTitle(int categoryMask) {
        return String.format("%s %s", new Object[]{XboxApplication.Resources.getString(R.string.avatar_title_remove), XboxApplication.Resources.getString(getTitle(categoryMask))});
    }

    public static String getTag(int categoryMask) {
        switch (categoryMask) {
            case AvatarEditorModel.AVATAREDIT_OPTION_COLOR_FACIAL_HAIR /*-2147483648*/:
                return "AVATAREDIT_OPTION_COLOR_FACIAL_HAIR";
            case AvatarEditorModel.AVATAREDIT_OPTION_MASK_COLORS_MALE /*-16777216*/:
                return "AVATAREDIT_OPTION_MASK_COLORS_MALE";
            case 1:
                return "AVATAREDIT_OPTION_BODY";
            case 4:
                return "AVATAREDIT_OPTION_PROPS";
            case 8:
                return "AVATAREDIT_OPTION_FEATURES_HAIRSTYLES";
            case 16:
                return "AVATAREDIT_OPTION_TOPS";
            case 32:
                return "AVATAREDIT_OPTION_BOTTOMS";
            case 64:
                return "AVATAREDIT_OPTION_SHOES";
            case 128:
                return "AVATAREDIT_OPTION_STYLE_HEADWEAR";
            case 256:
                return "AVATAREDIT_OPTION_GLOVES";
            case AvatarEditorModel.AVATAREDIT_OPTION_GLASSES /*512*/:
                return "AVATAREDIT_OPTION_GLASSES";
            case 1024:
                return "AVATAREDIT_OPTION_WRISTWEAR";
            case AvatarEditorModel.AVATAREDIT_OPTION_EARRINGS /*2048*/:
                return "AVATAREDIT_OPTION_EARRINGS";
            case AvatarEditorModel.AVATAREDIT_OPTION_RINGS /*4096*/:
                return "AVATAREDIT_OPTION_RINGS";
            case AvatarEditorModel.AVATAREDIT_OPTION_MASK_STYLE_ACCESSORIES /*7936*/:
                return "AVATAREDIT_OPTION_MASK_STYLE_ACCESSORIES";
            case AvatarEditorModel.AVATAREDIT_OPTION_FEATURES_EYES /*8192*/:
                return "AVATAREDIT_OPTION_FEATURES_EYES";
            case AvatarEditorModel.AVATAREDIT_OPTION_FEATURES_EYEBROWS /*16384*/:
                return "AVATAREDIT_OPTION_FEATURES_EYEBROWS";
            case AvatarEditorModel.AVATAREDIT_OPTION_MASK_FEATURES_EYES_AND_EYEBROWS /*24576*/:
                return "AVATAREDIT_OPTION_MASK_FEATURES_EYES_AND_EYEBROWS";
            case AvatarEditorModel.AVATAREDIT_OPTION_FEATURES_MOUTH /*32768*/:
                return "AVATAREDIT_OPTION_FEATURES_MOUTH";
            case AvatarEditorModel.AVATAREDIT_OPTION_FEATURES_FACIAL_HAIR /*65536*/:
                return "AVATAREDIT_OPTION_FEATURES_FACIAL_HAIR";
            case AvatarEditorModel.AVATAREDIT_OPTION_FEATURES_FACIAL_FEATURES /*131072*/:
                return "AVATAREDIT_OPTION_FEATURES_FACIAL_FEATURES";
            case AvatarEditorModel.AVATAREDIT_OPTION_MASK_FEATURES_FACIAL_FEATURES_MALE /*196608*/:
                return "AVATAREDIT_OPTION_MASK_FEATURES_FACIAL_FEATURES_MALE";
            case AvatarEditorModel.AVATAREDIT_OPTION_FEATURES_NOSE /*524288*/:
                return "AVATAREDIT_OPTION_FEATURES_NOSE";
            case 1048576:
                return "AVATAREDIT_OPTION_FEATURES_CHIN";
            case AvatarEditorModel.AVATAREDIT_OPTION_MASK_FEATURES_CHIN_AND_MOUTH /*1081344*/:
                return "AVATAREDIT_OPTION_MASK_FEATURES_CHIN_AND_MOUTH";
            case AvatarEditorModel.AVATAREDIT_OPTION_FEATURES_EARS /*2097152*/:
                return "AVATAREDIT_OPTION_FEATURES_EARS";
            case AvatarEditorModel.AVATAREDIT_OPTION_MASK_FEATURES_FEMALE /*3858440*/:
                return "AVATAREDIT_OPTION_MASK_FEATURES_FEMALE";
            case AvatarEditorModel.AVATAREDIT_OPTION_MASK_FEATURES_MALE /*3923976*/:
                return "AVATAREDIT_OPTION_MASK_FEATURES_MALE";
            case AvatarEditorModel.AVATAREDIT_OPTION_AWARDS /*4194304*/:
                return "AVATAREDIT_OPTION_AWARDS";
            case AvatarEditorModel.AVATAREDIT_OPTION_DRESS_UP /*8388608*/:
                return "AVATAREDIT_OPTION_DRESS_UP";
            case AvatarEditorModel.AVATAREDIT_OPTION_MASK_STYLE /*12591092*/:
                return "AVATAREDIT_OPTION_MASK_STYLE";
            case AvatarEditorModel.AVATAREDIT_OPTION_COLOR_SKIN /*16777216*/:
                return "AVATAREDIT_OPTION_COLOR_SKIN";
            case AvatarEditorModel.AVATAREDIT_OPTION_COLOR_EYE /*33554432*/:
                return "AVATAREDIT_OPTION_COLOR_EYE";
            case AvatarEditorModel.AVATAREDIT_OPTION_COLOR_EYE_SHADOW /*67108864*/:
                return "AVATAREDIT_OPTION_COLOR_EYE_SHADOW";
            case AvatarEditorModel.AVATAREDIT_OPTION_COLOR_HAIR /*134217728*/:
                return "AVATAREDIT_OPTION_COLOR_HAIR";
            case AvatarEditorModel.AVATAREDIT_OPTION_COLOR_FACIAL_FEATURE /*268435456*/:
                return "AVATAREDIT_OPTION_COLOR_FACIAL_FEATURE";
            case AvatarEditorModel.AVATAREDIT_OPTION_COLOR_EYEBROW /*536870912*/:
                return "AVATAREDIT_OPTION_COLOR_EYEBROW";
            case AvatarEditorModel.AVATAREDIT_OPTION_COLOR_LIP /*1073741824*/:
                return "AVATAREDIT_OPTION_COLOR_LIP";
            case AvatarEditorModel.AVATAREDIT_OPTION_MASK_COLORS_FEMALE /*2130706432*/:
                return "AVATAREDIT_OPTION_MASK_COLORS_FEMALE";
            default:
                throw new UnsupportedOperationException();
        }
    }
}
