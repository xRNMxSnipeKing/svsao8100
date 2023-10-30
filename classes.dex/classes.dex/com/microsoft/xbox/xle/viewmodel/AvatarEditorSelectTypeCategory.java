package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.xbox.avatar.model.AvatarEditorModel;
import com.microsoft.xbox.avatar.view.AvatarEditorOption;
import com.microsoft.xbox.avatar.view.AvatarEditorOptionCategory;
import com.microsoft.xbox.avatar.view.AvatarEditorSelectTitleCatalog;
import com.microsoft.xbox.toolkit.XboxApplication;

public class AvatarEditorSelectTypeCategory extends AvatarEditorSelectType {
    private final int categoryMask;

    public AvatarEditorSelectTypeCategory(int categoryMask) {
        this.categoryMask = categoryMask;
    }

    public int getCategoryMask() {
        return this.categoryMask;
    }

    public AvatarEditorOption[] getSelectButtons() {
        switch (this.categoryMask) {
            case AvatarEditorModel.AVATAREDIT_OPTION_MASK_COLORS_MALE /*-16777216*/:
                return new AvatarEditorOption[]{new AvatarEditorOptionCategory(AvatarEditorModel.AVATAREDIT_OPTION_COLOR_HAIR), new AvatarEditorOptionCategory(AvatarEditorModel.AVATAREDIT_OPTION_COLOR_EYE), new AvatarEditorOptionCategory(AvatarEditorModel.AVATAREDIT_OPTION_COLOR_SKIN), new AvatarEditorOptionCategory(AvatarEditorModel.AVATAREDIT_OPTION_COLOR_EYEBROW), new AvatarEditorOptionCategory(AvatarEditorModel.AVATAREDIT_OPTION_COLOR_LIP), new AvatarEditorOptionCategory(AvatarEditorModel.AVATAREDIT_OPTION_COLOR_EYE_SHADOW), new AvatarEditorOptionCategory(AvatarEditorModel.AVATAREDIT_OPTION_COLOR_FACIAL_HAIR), new AvatarEditorOptionCategory(AvatarEditorModel.AVATAREDIT_OPTION_COLOR_FACIAL_FEATURE)};
            case AvatarEditorModel.AVATAREDIT_OPTION_MASK_STYLE_ACCESSORIES /*7936*/:
                return new AvatarEditorOption[]{new AvatarEditorOptionCategory(AvatarEditorModel.AVATAREDIT_OPTION_GLASSES), new AvatarEditorOptionCategory(AvatarEditorModel.AVATAREDIT_OPTION_EARRINGS), new AvatarEditorOptionCategory(1024), new AvatarEditorOptionCategory(AvatarEditorModel.AVATAREDIT_OPTION_RINGS), new AvatarEditorOptionCategory(256)};
            case AvatarEditorModel.AVATAREDIT_OPTION_MASK_FEATURES_EYES_AND_EYEBROWS /*24576*/:
                return new AvatarEditorOption[]{new AvatarEditorOptionCategory(AvatarEditorModel.AVATAREDIT_OPTION_FEATURES_EYES), new AvatarEditorOptionCategory(AvatarEditorModel.AVATAREDIT_OPTION_FEATURES_EYEBROWS)};
            case AvatarEditorModel.AVATAREDIT_OPTION_MASK_FEATURES_FACIAL_FEATURES_MALE /*196608*/:
                return new AvatarEditorOption[]{new AvatarEditorOptionCategory(AvatarEditorModel.AVATAREDIT_OPTION_FEATURES_FACIAL_FEATURES), new AvatarEditorOptionCategory(AvatarEditorModel.AVATAREDIT_OPTION_FEATURES_FACIAL_HAIR)};
            case AvatarEditorModel.AVATAREDIT_OPTION_MASK_FEATURES_CHIN_AND_MOUTH /*1081344*/:
                return new AvatarEditorOption[]{new AvatarEditorOptionCategory(1048576), new AvatarEditorOptionCategory(AvatarEditorModel.AVATAREDIT_OPTION_FEATURES_MOUTH)};
            case AvatarEditorModel.AVATAREDIT_OPTION_MASK_FEATURES_FEMALE /*3858440*/:
                return new AvatarEditorOption[]{new AvatarEditorOptionCategory(AvatarEditorModel.AVATAREDIT_OPTION_MASK_FEATURES_EYES_AND_EYEBROWS), new AvatarEditorOptionCategory(8), new AvatarEditorOptionCategory(AvatarEditorModel.AVATAREDIT_OPTION_FEATURES_EARS), new AvatarEditorOptionCategory(AvatarEditorModel.AVATAREDIT_OPTION_FEATURES_NOSE), new AvatarEditorOptionCategory(AvatarEditorModel.AVATAREDIT_OPTION_MASK_FEATURES_CHIN_AND_MOUTH), new AvatarEditorOptionCategory(AvatarEditorModel.AVATAREDIT_OPTION_FEATURES_FACIAL_FEATURES), new AvatarEditorOptionCategory(1), new AvatarEditorOptionCategory(AvatarEditorModel.AVATAREDIT_OPTION_MASK_COLORS_FEMALE)};
            case AvatarEditorModel.AVATAREDIT_OPTION_MASK_FEATURES_MALE /*3923976*/:
                return new AvatarEditorOption[]{new AvatarEditorOptionCategory(AvatarEditorModel.AVATAREDIT_OPTION_MASK_FEATURES_EYES_AND_EYEBROWS), new AvatarEditorOptionCategory(8), new AvatarEditorOptionCategory(AvatarEditorModel.AVATAREDIT_OPTION_FEATURES_EARS), new AvatarEditorOptionCategory(AvatarEditorModel.AVATAREDIT_OPTION_FEATURES_NOSE), new AvatarEditorOptionCategory(AvatarEditorModel.AVATAREDIT_OPTION_MASK_FEATURES_CHIN_AND_MOUTH), new AvatarEditorOptionCategory(AvatarEditorModel.AVATAREDIT_OPTION_MASK_FEATURES_FACIAL_FEATURES_MALE), new AvatarEditorOptionCategory(1), new AvatarEditorOptionCategory(AvatarEditorModel.AVATAREDIT_OPTION_MASK_COLORS_MALE)};
            case AvatarEditorModel.AVATAREDIT_OPTION_MASK_STYLE /*12591092*/:
                return new AvatarEditorOption[]{new AvatarEditorOptionCategory(128), new AvatarEditorOptionCategory(16), new AvatarEditorOptionCategory(AvatarEditorModel.AVATAREDIT_OPTION_DRESS_UP), new AvatarEditorOptionCategory(AvatarEditorModel.AVATAREDIT_OPTION_MASK_STYLE_ACCESSORIES), new AvatarEditorOptionCategory(32), new AvatarEditorOptionCategory(64), new AvatarEditorOptionCategory(4), new AvatarEditorOptionCategory(AvatarEditorModel.AVATAREDIT_OPTION_AWARDS)};
            case AvatarEditorModel.AVATAREDIT_OPTION_MASK_COLORS_FEMALE /*2130706432*/:
                return new AvatarEditorOption[]{new AvatarEditorOptionCategory(AvatarEditorModel.AVATAREDIT_OPTION_COLOR_HAIR), new AvatarEditorOptionCategory(AvatarEditorModel.AVATAREDIT_OPTION_COLOR_EYE), new AvatarEditorOptionCategory(AvatarEditorModel.AVATAREDIT_OPTION_COLOR_SKIN), new AvatarEditorOptionCategory(AvatarEditorModel.AVATAREDIT_OPTION_COLOR_EYEBROW), new AvatarEditorOptionCategory(AvatarEditorModel.AVATAREDIT_OPTION_COLOR_LIP), new AvatarEditorOptionCategory(AvatarEditorModel.AVATAREDIT_OPTION_COLOR_EYE_SHADOW), new AvatarEditorOptionCategory(AvatarEditorModel.AVATAREDIT_OPTION_COLOR_FACIAL_FEATURE)};
            default:
                return AvatarEditorModel.getInstance().getOptions(this.categoryMask);
        }
    }

    public String getTag() {
        return AvatarEditorSelectTitleCatalog.getTag(this.categoryMask);
    }

    public String getTitle() {
        return XboxApplication.Resources.getString(AvatarEditorSelectTitleCatalog.getTitle(this.categoryMask));
    }

    public String getDescription() {
        return null;
    }

    public int getCameraCategoryType() {
        return this.categoryMask;
    }
}
