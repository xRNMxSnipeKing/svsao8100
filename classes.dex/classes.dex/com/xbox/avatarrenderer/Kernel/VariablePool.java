package com.xbox.avatarrenderer.Kernel;

import com.xbox.avatarrenderer.Core2Renderer;
import com.xbox.avatarrenderer.WrapperBase;

public class VariablePool extends WrapperBase {
    private native int nativeVariablePoolFindVariable(int i, int i2, String str);

    VariablePool(Core2Renderer core2, int index) {
        super(core2, index);
    }

    public VariableData findVariable(String variableName) {
        if (this.m_core2 == null) {
            return null;
        }
        int iVD = nativeVariablePoolFindVariable(this.m_core2.GetInstanceID(), this.m_iInstanceID, variableName);
        if (iVD >= 0) {
            return new VariableData(this.m_core2, iVD);
        }
        return null;
    }
}
