package com.microsoft.xbox.avatar.view;

import com.microsoft.xbox.avatar.model.AvatarClosetSpinAnimation;
import com.microsoft.xbox.avatar.model.AvatarEditorModel;
import java.util.ArrayList;
import java.util.Arrays;

public class AvatarAnimationCatalog {
    public static String[] ACHIEVEMENT_EMOTE_CELEBRATE = new String[]{"00400000-0006-0003-0001-00004d53144f", "00400000-0006-0003-0002-00004d53144f", "00400000-0006-0003-0003-00004d53144f"};
    public static String[] ACHIEVEMENT_EMOTE_CONFUSED_FEMALE = new String[]{"00400000-0006-0002-0004-00004d53144f"};
    public static String[] ACHIEVEMENT_EMOTE_CONFUSED_MALE = new String[]{"00400000-0006-0001-0004-00004d53144f"};
    public static ArrayList<String> ACHIEVEMENT_EMOTE_CRY_FEMALE = null;
    public static ArrayList<String> ACHIEVEMENT_EMOTE_CRY_MALE = null;
    public static String AVATAR_FALL_ANIMATION_FALL = "00400000-0008-0003-0001-00004d53144f";
    public static String AVATAR_FALL_ANIMATION_STAND = "00400000-0008-0003-0002-00004d53144f";
    public static String[] CLOSET_SPIN_ANIMATIONS = new String[]{"00400000-0007-0003-0000-00004d53144f", "00400000-0007-0003-0002-00004d53144f", "00400000-0007-0003-0003-00004d53144f", "00400000-0007-0003-0005-00004d53144f"};
    public static AvatarClosetSpinAnimation[] CLOSET_SPIN_ANIMATION_DESCRIPTORS = new AvatarClosetSpinAnimation[]{null, null, new AvatarClosetSpinAnimation(CLOSET_SPIN_ANIMATIONS[0], 1.0f), new AvatarClosetSpinAnimation(CLOSET_SPIN_ANIMATIONS[1], 0.8f), new AvatarClosetSpinAnimation(CLOSET_SPIN_ANIMATIONS[2], 0.8f), new AvatarClosetSpinAnimation(CLOSET_SPIN_ANIMATIONS[3], 1.23f)};
    public static final String IDLE_ANIMATION = "00400000-0003-0003-0001-00004d53144f";
    public static ArrayList<String> IDLE_FIDGET_ANIMATIONS_FEMALE;
    public static ArrayList<String> IDLE_FIDGET_ANIMATIONS_MALE;
    public static final ArrayList<String> RUN_IN_ANIMATIONS_LEFT;
    public static final ArrayList<String> RUN_IN_ANIMATIONS_RIGHT;
    public static final ArrayList<String> RUN_IN_ANIMATION_LEFT_GENTLY = new ArrayList(Arrays.asList(new String[]{"00400000-0001-0003-0201-00004d53144f"}));
    public static final ArrayList<String> RUN_IN_ANIMATION_RIGHT_GENTLY;
    public static final ArrayList<String> RUN_OUT_ANIMATIONS_LEFT;
    public static final ArrayList<String> RUN_OUT_ANIMATIONS_RIGHT;
    public static ArrayList<String> TOUCH_EMOTE_ANIMATION = new ArrayList(Arrays.asList(new String[]{"00400000-0005-0003-0001-00004d53144f", "00400000-0005-0003-0002-00004d53144f", "00400000-0005-0003-0003-00004d53144f"}));

    public enum AvatarClosetSpinAnimationType {
        None,
        Prop,
        Spin,
        LeftHand,
        RightHand,
        Snap
    }

    static {
        String[] RUN_IN_ANIMATION_RIGHT_GENTLY_ONLY = new String[]{"00400000-0001-0003-0301-00004d53144f"};
        RUN_IN_ANIMATION_RIGHT_GENTLY = new ArrayList(Arrays.asList(RUN_IN_ANIMATION_RIGHT_GENTLY_ONLY));
        String[] FIDGET_ANIMATIONS_NEUTRAL_ONLY = new String[]{"00400000-0004-0003-0002-00004d53144f", "00400000-0004-0003-0003-00004d53144f", "00400000-0004-0003-0004-00004d53144f", "00400000-0004-0003-0005-00004d53144f", "00400000-0004-0003-0006-00004d53144f", "00400000-0004-0003-0007-00004d53144f", "00400000-0004-0003-0008-00004d53144f", "00400000-0004-0003-0009-00004d53144f", "00400000-0004-0003-0010-00004d53144f", "00400000-0004-0003-0011-00004d53144f", "00400000-0004-0003-0012-00004d53144f", "00400000-0004-0003-0013-00004d53144f", "00400000-0004-0003-0014-00004d53144f", "00400000-0004-0003-0015-00004d53144f", "00400000-0004-0003-0016-00004d53144f", "00400000-0004-0003-0017-00004d53144f"};
        String[] FIDGET_ANIMATIONS_MALE_ONLY = new String[]{"00400000-0004-0001-0101-00004d53144f", "00400000-0004-0001-0102-00004d53144f"};
        String[] FIDGET_ANIMATIONS_FEMALE_ONLY = new String[]{"00400000-0004-0002-0201-00004d53144f", "00400000-0004-0002-0202-00004d53144f"};
        IDLE_FIDGET_ANIMATIONS_MALE = MergeLists(FIDGET_ANIMATIONS_NEUTRAL_ONLY, FIDGET_ANIMATIONS_MALE_ONLY);
        IDLE_FIDGET_ANIMATIONS_FEMALE = MergeLists(FIDGET_ANIMATIONS_NEUTRAL_ONLY, FIDGET_ANIMATIONS_FEMALE_ONLY);
        String[] RUN_IN_ANIMATIONS_NEUTRAL_ONLY = new String[0];
        String[] RUN_IN_ANIMATIONS_LEFT_ONLY = new String[0];
        String[] RUN_IN_ANIMATIONS_RIGHT_ONLY = new String[0];
        RUN_IN_ANIMATIONS_LEFT = MergeLists(RUN_IN_ANIMATION_LEFT_GENTLY_ONLY, RUN_IN_ANIMATIONS_NEUTRAL_ONLY, RUN_IN_ANIMATIONS_LEFT_ONLY);
        RUN_IN_ANIMATIONS_RIGHT = MergeLists(RUN_IN_ANIMATION_RIGHT_GENTLY_ONLY, RUN_IN_ANIMATIONS_NEUTRAL_ONLY, RUN_IN_ANIMATIONS_RIGHT_ONLY);
        String[] RUN_OUT_ANIMATIONS_NEUTRAL_ONLY = new String[0];
        String[] RUN_OUT_ANIMATIONS_LEFT_ONLY = new String[]{"00400000-0002-0003-0201-00004d53144f", "00400000-0002-0003-0202-00004d53144f"};
        String[] RUN_OUT_ANIMATIONS_RIGHT_ONLY = new String[]{"00400000-0002-0003-0301-00004d53144f", "00400000-0002-0003-0302-00004d53144f"};
        RUN_OUT_ANIMATIONS_LEFT = MergeLists(RUN_OUT_ANIMATIONS_NEUTRAL_ONLY, RUN_OUT_ANIMATIONS_LEFT_ONLY);
        RUN_OUT_ANIMATIONS_RIGHT = MergeLists(RUN_OUT_ANIMATIONS_NEUTRAL_ONLY, RUN_OUT_ANIMATIONS_RIGHT_ONLY);
        String[] ACHIEVEMENT_EMOTE_CRY_NEUTRAL_ONLY = new String[]{"00400000-0006-0003-0005-00004d53144f"};
        String[] ACHIEVEMENT_EMOTE_CRY_MALE_ONLY = new String[]{"00400000-0006-0001-0006-00004d53144f"};
        String[] ACHIEVEMENT_EMOTE_CRY_FEMALE_ONLY = new String[]{"00400000-0006-0002-0006-00004d53144f"};
        ACHIEVEMENT_EMOTE_CRY_FEMALE = MergeLists(ACHIEVEMENT_EMOTE_CRY_NEUTRAL_ONLY, ACHIEVEMENT_EMOTE_CRY_FEMALE_ONLY);
        ACHIEVEMENT_EMOTE_CRY_MALE = MergeLists(ACHIEVEMENT_EMOTE_CRY_NEUTRAL_ONLY, ACHIEVEMENT_EMOTE_CRY_MALE_ONLY);
    }

    private static <T> ArrayList<T> MergeLists(T[]... lists) {
        ArrayList<T> rv = new ArrayList();
        for (T[] list : lists) {
            rv.addAll(Arrays.asList(list));
        }
        return rv;
    }

    public static AvatarClosetSpinAnimationType getAvatarClosetSpinAnimationTypeForCategory(int assetCategory) {
        switch (assetCategory) {
            case 4:
                return AvatarClosetSpinAnimationType.Prop;
            case 8:
                return AvatarClosetSpinAnimationType.Snap;
            case 16:
            case 32:
            case 64:
            case 128:
            case AvatarEditorModel.AVATAREDIT_OPTION_GLASSES /*512*/:
            case AvatarEditorModel.AVATAREDIT_OPTION_DRESS_UP /*8388608*/:
                return AvatarClosetSpinAnimationType.Spin;
            default:
                return AvatarClosetSpinAnimationType.None;
        }
    }
}
