package com.microsoft.xbox.service.network.managers.xblshared;

import com.microsoft.xbox.service.model.MediaTitleState;

public interface ICompanionSessionMediaTitleStateListener {
    void onMediaTitleStateUpdated(MediaTitleState mediaTitleState);
}
