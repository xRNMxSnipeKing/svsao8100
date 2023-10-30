package com.microsoft.xbox.avatar.view;

import com.microsoft.smartglass.R;
import com.microsoft.xbox.avatar.model.AvatarEditorModel;
import com.microsoft.xbox.avatar.view.AvatarEditorOption.AvatarEditorOptionDisplayType;
import com.microsoft.xbox.toolkit.XboxApplication;

public class AvatarEditorOptionCategory extends AvatarEditorOption {
    private int categoryMask = 0;
    private int idFemale = -1;
    private int idFemaleDisabled = -1;
    private int idMale = -1;
    private int idMaleDisabled = -1;
    private boolean isEnabled = false;
    private String title = null;

    public AvatarEditorOptionCategory(int categoryMask) {
        super(-1, AvatarEditorOptionDisplayType.RESOURCE);
        this.categoryMask = categoryMask;
        setResourceId(categoryMask);
        boolean isMale = AvatarEditorModel.getInstance().isMale();
        this.isEnabled = AvatarEditorModel.getInstance().isCategoryEnabled(categoryMask);
        if (this.isEnabled) {
            if (isMale) {
                this.id = this.idMale;
            } else {
                this.id = this.idFemale;
            }
        } else if (isMale) {
            this.id = this.idMaleDisabled;
        } else {
            this.id = this.idFemaleDisabled;
        }
    }

    public int getCategoryMask() {
        return this.categoryMask;
    }

    public String getButtonTitle() {
        return this.title;
    }

    private void setResourceId(int categoryMask) {
        switch (categoryMask) {
            case AvatarEditorModel.AVATAREDIT_OPTION_COLOR_FACIAL_HAIR /*-2147483648*/:
                this.idMale = R.drawable.editor_facialhair;
                this.idMaleDisabled = R.drawable.editor_facialhair_unselected;
                break;
            case AvatarEditorModel.AVATAREDIT_OPTION_MASK_COLORS_MALE /*-16777216*/:
                this.idMale = R.drawable.editor_mcolor;
                this.idFemale = -1;
                break;
            case 1:
                this.idMale = R.drawable.editor_mbody;
                this.idFemale = R.drawable.editor_body;
                break;
            case 4:
                this.idMale = R.drawable.editor_props;
                this.idFemale = R.drawable.editor_props;
                break;
            case 8:
                this.idMale = R.drawable.editor_hair;
                this.idFemale = R.drawable.editor_hair;
                break;
            case 16:
                this.idMale = R.drawable.editor_mtops;
                this.idFemale = R.drawable.editor_tops;
                break;
            case 32:
                this.idMale = R.drawable.editor_mbottoms;
                this.idFemale = R.drawable.editor_bottoms;
                break;
            case 64:
                this.idMale = R.drawable.editor_shoes;
                this.idFemale = R.drawable.editor_shoes;
                break;
            case 128:
                this.idMale = R.drawable.editor_mhat;
                this.idFemale = R.drawable.editor_hat;
                break;
            case 256:
                this.idMale = R.drawable.editor_mgloves;
                this.idFemale = R.drawable.editor_gloves;
                break;
            case AvatarEditorModel.AVATAREDIT_OPTION_GLASSES /*512*/:
                this.idMale = R.drawable.editor_glasses;
                this.idFemale = R.drawable.editor_glasses;
                break;
            case 1024:
                this.idMale = R.drawable.editor_watch;
                this.idFemale = R.drawable.editor_watch;
                break;
            case AvatarEditorModel.AVATAREDIT_OPTION_EARRINGS /*2048*/:
                this.idMale = R.drawable.editor_earring2;
                this.idFemale = R.drawable.editor_earring2;
                break;
            case AvatarEditorModel.AVATAREDIT_OPTION_RINGS /*4096*/:
                this.idMale = R.drawable.editor_ring;
                this.idFemale = R.drawable.editor_ring;
                break;
            case AvatarEditorModel.AVATAREDIT_OPTION_MASK_STYLE_ACCESSORIES /*7936*/:
                this.idMale = R.drawable.editor_style;
                this.idFemale = R.drawable.editor_style;
                break;
            case AvatarEditorModel.AVATAREDIT_OPTION_FEATURES_EYES /*8192*/:
                this.idMale = R.drawable.editor_meye;
                this.idFemale = R.drawable.editor_eye;
                break;
            case AvatarEditorModel.AVATAREDIT_OPTION_FEATURES_EYEBROWS /*16384*/:
                this.idMale = R.drawable.editor_eyebrow;
                this.idFemale = R.drawable.editor_eyebrow;
                break;
            case AvatarEditorModel.AVATAREDIT_OPTION_MASK_FEATURES_EYES_AND_EYEBROWS /*24576*/:
                this.idMale = R.drawable.editor_meye;
                this.idFemale = R.drawable.editor_meye;
                break;
            case AvatarEditorModel.AVATAREDIT_OPTION_FEATURES_MOUTH /*32768*/:
                this.idMale = R.drawable.editor_mmouth;
                this.idFemale = R.drawable.editor_mouth;
                break;
            case AvatarEditorModel.AVATAREDIT_OPTION_FEATURES_FACIAL_HAIR /*65536*/:
                this.idMale = R.drawable.editor_facialhair;
                this.idMaleDisabled = R.drawable.editor_facialhair_unselected;
                break;
            case AvatarEditorModel.AVATAREDIT_OPTION_FEATURES_FACIAL_FEATURES /*131072*/:
                this.idMale = R.drawable.editor_mfeatures;
                this.idFemale = R.drawable.editor_features;
                break;
            case AvatarEditorModel.AVATAREDIT_OPTION_MASK_FEATURES_FACIAL_FEATURES_MALE /*196608*/:
                this.idMale = R.drawable.editor_facialhair;
                this.idFemale = -1;
                break;
            case AvatarEditorModel.AVATAREDIT_OPTION_FEATURES_NOSE /*524288*/:
                this.idMale = R.drawable.editor_nose;
                this.idFemale = R.drawable.editor_nose;
                break;
            case 1048576:
                this.idMale = R.drawable.editor_chin;
                this.idFemale = R.drawable.editor_chin;
                break;
            case AvatarEditorModel.AVATAREDIT_OPTION_MASK_FEATURES_CHIN_AND_MOUTH /*1081344*/:
                this.idMale = R.drawable.editor_chin;
                this.idFemale = R.drawable.editor_chin;
                break;
            case AvatarEditorModel.AVATAREDIT_OPTION_FEATURES_EARS /*2097152*/:
                this.idMale = R.drawable.editor_ear;
                this.idFemale = R.drawable.editor_ear;
                break;
            case AvatarEditorModel.AVATAREDIT_OPTION_AWARDS /*4194304*/:
                this.idMale = R.drawable.editor_awards;
                this.idFemale = R.drawable.editor_awards;
                break;
            case AvatarEditorModel.AVATAREDIT_OPTION_DRESS_UP /*8388608*/:
                this.idMale = R.drawable.editor_mdressup;
                this.idFemale = R.drawable.editor_dressup;
                break;
            case AvatarEditorModel.AVATAREDIT_OPTION_COLOR_SKIN /*16777216*/:
                this.idMale = R.drawable.editor_skincolor;
                this.idFemale = R.drawable.editor_skincolor;
                break;
            case AvatarEditorModel.AVATAREDIT_OPTION_COLOR_EYE /*33554432*/:
                this.idMale = R.drawable.editor_meye;
                this.idFemale = R.drawable.editor_eye;
                this.idMaleDisabled = R.drawable.editor_meye_unselected;
                this.idFemaleDisabled = R.drawable.editor_eye_unselected;
                break;
            case AvatarEditorModel.AVATAREDIT_OPTION_COLOR_EYE_SHADOW /*67108864*/:
                this.idMale = R.drawable.editor_eyeshadow;
                this.idFemale = R.drawable.editor_eyeshadow;
                break;
            case AvatarEditorModel.AVATAREDIT_OPTION_COLOR_HAIR /*134217728*/:
                this.idMale = R.drawable.editor_hair;
                this.idFemale = R.drawable.editor_hair;
                this.idMaleDisabled = R.drawable.editor_hair_unselected;
                this.idFemaleDisabled = R.drawable.editor_hair_unselected;
                break;
            case AvatarEditorModel.AVATAREDIT_OPTION_COLOR_FACIAL_FEATURE /*268435456*/:
                this.idMale = R.drawable.editor_mfeatures;
                this.idFemale = R.drawable.editor_features;
                this.idMaleDisabled = R.drawable.editor_mfeatures_unselected;
                this.idFemaleDisabled = R.drawable.editor_features_unselected;
                break;
            case AvatarEditorModel.AVATAREDIT_OPTION_COLOR_EYEBROW /*536870912*/:
                this.idMale = R.drawable.editor_eyebrow;
                this.idFemale = R.drawable.editor_eyebrow;
                this.idMaleDisabled = R.drawable.editor_eyebrow_unselected;
                this.idFemaleDisabled = R.drawable.editor_eyebrow_unselected;
                break;
            case AvatarEditorModel.AVATAREDIT_OPTION_COLOR_LIP /*1073741824*/:
                this.idMale = R.drawable.editor_mmouth;
                this.idFemale = R.drawable.editor_mouth;
                this.idMaleDisabled = R.drawable.editor_mmouth_unselected;
                this.idFemaleDisabled = R.drawable.editor_mouth_unselected;
                break;
            case AvatarEditorModel.AVATAREDIT_OPTION_MASK_COLORS_FEMALE /*2130706432*/:
                this.idMale = -1;
                this.idFemale = R.drawable.editor_color;
                break;
            default:
                throw new UnsupportedOperationException();
        }
        this.title = XboxApplication.Resources.getString(AvatarEditorSelectTitleCatalog.getTitle(categoryMask));
    }

    public boolean isEnabled() {
        return this.isEnabled;
    }
}
