package com.xbox.avatarrenderer.Kernel;

import com.xbox.avatarrenderer.Core2Renderer;
import com.xbox.avatarrenderer.WrapperBase;

public class Command extends WrapperBase {
    private native int nativeCommandExecute(int i, int i2, float f, int i3, float[] fArr);

    private native String nativeCommandGetClassName(int i, int i2);

    private native int nativeCommandSetParameter(int i, int i2, int i3, int i4);

    public Command(Core2Renderer core2, int index) {
        super(core2, index);
    }

    public int execute(float timeStep, StoryThread thread, float[] timestepRemainingOut) {
        int i = -1;
        if (this.m_core2 == null) {
            return -1;
        }
        int GetInstanceID = this.m_core2.GetInstanceID();
        int i2 = this.m_iInstanceID;
        if (thread != null) {
            i = thread.getInstanceID();
        }
        return nativeCommandExecute(GetInstanceID, i2, timeStep, i, timestepRemainingOut);
    }

    public int setParameter(int parameterIndex, VariableDeclaration declaration) {
        int i = -1;
        if (this.m_core2 == null) {
            return -1;
        }
        int GetInstanceID = this.m_core2.GetInstanceID();
        int i2 = this.m_iInstanceID;
        if (declaration != null) {
            i = declaration.getInstanceID();
        }
        return nativeCommandSetParameter(GetInstanceID, i2, parameterIndex, i);
    }

    public String GetClassName() {
        if (this.m_core2 != null) {
            return nativeCommandGetClassName(this.m_core2.GetInstanceID(), this.m_iInstanceID);
        }
        return null;
    }
}
