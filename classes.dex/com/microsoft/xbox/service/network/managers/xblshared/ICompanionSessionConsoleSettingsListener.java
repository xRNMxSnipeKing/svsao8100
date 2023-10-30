package com.microsoft.xbox.service.network.managers.xblshared;

import com.microsoft.xbox.service.model.ConsoleSettings;

public interface ICompanionSessionConsoleSettingsListener {
    void OnGetConsoleSettingsResponse(ConsoleSettings consoleSettings);
}
