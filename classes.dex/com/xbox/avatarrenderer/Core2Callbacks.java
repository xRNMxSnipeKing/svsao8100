package com.xbox.avatarrenderer;

import com.xbox.avatarrenderer.AvatarEditor.AvatarEditorEditEvent;
import com.xbox.avatarrenderer.Kernel.ScriptException;

public interface Core2Callbacks {
    void invokeAvatarEditorEditEvent(AvatarEditorEditEvent avatarEditorEditEvent);

    void invokeScriptException(ScriptException scriptException);

    void onNotify(int i);
}
