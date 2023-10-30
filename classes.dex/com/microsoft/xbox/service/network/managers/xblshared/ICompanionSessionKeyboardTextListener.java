package com.microsoft.xbox.service.network.managers.xblshared;

import com.microsoft.xbox.service.model.KeyboardText;

public interface ICompanionSessionKeyboardTextListener {
    void OnGetKeyboardTextResponse(KeyboardText keyboardText);
}
