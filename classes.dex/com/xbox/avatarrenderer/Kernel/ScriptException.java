package com.xbox.avatarrenderer.Kernel;

import com.xbox.avatarrenderer.Core2Renderer;
import com.xbox.avatarrenderer.WrapperBase;

public class ScriptException extends WrapperBase {
    private native int nativeScriptExceptionGetErrorCode(int i, int i2);

    private native float nativeScriptExceptionGetRemainingTime(int i, int i2);

    public ScriptException(Core2Renderer core2, int index) {
        super(core2, index);
    }

    public int getErrorCode() {
        if (this.m_core2 != null) {
            return nativeScriptExceptionGetErrorCode(this.m_core2.GetInstanceID(), this.m_iInstanceID);
        }
        return -1;
    }

    public float getRemainingTime() {
        if (this.m_core2 != null) {
            return nativeScriptExceptionGetRemainingTime(this.m_core2.GetInstanceID(), this.m_iInstanceID);
        }
        return 0.0f;
    }
}
