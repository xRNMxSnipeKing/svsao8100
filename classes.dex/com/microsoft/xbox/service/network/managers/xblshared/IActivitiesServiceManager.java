package com.microsoft.xbox.service.network.managers.xblshared;

import com.microsoft.xbox.service.model.edsv2.EDSV2ActivityItem;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaItem;
import com.microsoft.xbox.toolkit.XLEException;
import java.util.ArrayList;

public interface IActivitiesServiceManager {
    ArrayList<EDSV2ActivityItem> getActivities(EDSV2MediaItem eDSV2MediaItem) throws XLEException;

    EDSV2ActivityItem getActivityDetail(EDSV2ActivityItem eDSV2ActivityItem, EDSV2MediaItem eDSV2MediaItem) throws XLEException;
}
