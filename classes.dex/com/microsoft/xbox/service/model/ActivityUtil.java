package com.microsoft.xbox.service.model;

import com.microsoft.xbox.service.model.edsv2.EDSV2ActivityItem;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaType;

public class ActivityUtil {
    public static EDSV2ActivityItem getDefaultActivity(ActivitySummaryModel appActivityModel, ActivitySummaryModel mediaActivityModel, int mediaType, long titleId) {
        EDSV2ActivityItem defaultActivity = mediaActivityModel == null ? null : mediaActivityModel.getFirstHeroActivityForProvider(titleId);
        if (defaultActivity != null) {
            return defaultActivity;
        }
        defaultActivity = appActivityModel == null ? null : appActivityModel.getFirstHeroActivityForProvider(titleId);
        if (defaultActivity != null) {
            return defaultActivity;
        }
        return null;
    }

    public static boolean isValidMediaTypeForActivity(int type) {
        switch (type) {
            case EDSV2MediaType.MEDIATYPE_ALBUM /*1006*/:
            case EDSV2MediaType.MEDIATYPE_TRACK /*1007*/:
                return false;
            default:
                return true;
        }
    }
}
