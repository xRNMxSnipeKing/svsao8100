package com.microsoft.xbox.service.network.managers.xblshared;

import com.microsoft.xbox.service.model.ActiveTitleInfo;

public interface ICompanionSessionActiveTitleInfoListener {
    void OnGetActiveTitleInfoResponse(ActiveTitleInfo activeTitleInfo);
}
