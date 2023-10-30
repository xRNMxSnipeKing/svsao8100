package com.microsoft.xbox.service.model.sls;

import com.microsoft.xbox.toolkit.XLELog;

public class ConsolePresenceInfo {
    private String mediaId;
    private long titleId;
    private String titleIdString;

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public void setTitleIdString(String titleIdString) {
        this.titleIdString = titleIdString;
        this.titleId = 0;
        try {
            this.titleId = Long.parseLong(this.titleIdString);
        } catch (Exception e) {
            XLELog.Diagnostic("PresenceInfo", "failed to parse titleid " + titleIdString);
        }
    }

    public String getMediaId() {
        return this.mediaId;
    }

    public long getTitleId() {
        return this.titleId;
    }

    public boolean getIsOnline() {
        return this.titleId != 0;
    }
}
