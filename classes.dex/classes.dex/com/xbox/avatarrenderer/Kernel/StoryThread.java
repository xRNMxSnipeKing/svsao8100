package com.xbox.avatarrenderer.Kernel;

import com.xbox.avatarrenderer.Core2Renderer;
import com.xbox.avatarrenderer.WrapperBase;

public class StoryThread extends WrapperBase {
    private native int nativeStoryThreadDestroy(int i, int i2);

    private native int nativeStoryThreadExecute(int i, int i2);

    private native int nativeStoryThreadGetCurrentCommand(int i, int i2);

    private native int nativeStoryThreadGetSynchronous(int i, int i2);

    private native int nativeStoryThreadGetVariable(int i, int i2, int i3);

    private native int nativeStoryThreadGetVariableByName(int i, int i2, String str);

    private native int nativeStoryThreadSetExceptionHandler(int i, int i2, int i3);

    private native int nativeStoryThreadSleep(int i, int i2);

    private native int nativeStoryThreadTerminate(int i, int i2, int i3, float f);

    private native int nativeStoryThreadUpdate(int i, int i2, float f);

    private native int nativeStoryThreadWakeUp(int i, int i2, float f);

    public StoryThread(Core2Renderer core2, int index) {
        super(core2, index);
    }

    public int wakeUp(float remainingTime) {
        if (this.m_core2 != null) {
            return nativeStoryThreadWakeUp(this.m_core2.GetInstanceID(), this.m_iInstanceID, remainingTime);
        }
        return -1;
    }

    public int sleep() {
        if (this.m_core2 != null) {
            return nativeStoryThreadSleep(this.m_core2.GetInstanceID(), this.m_iInstanceID);
        }
        return -1;
    }

    public int terminate(int hRes, float remainingTime) {
        if (this.m_core2 != null) {
            return nativeStoryThreadTerminate(this.m_core2.GetInstanceID(), this.m_iInstanceID, hRes, remainingTime);
        }
        return -1;
    }

    public int destroy() {
        if (this.m_core2 != null) {
            return nativeStoryThreadDestroy(this.m_core2.GetInstanceID(), this.m_iInstanceID);
        }
        return -1;
    }

    public int update(float timeStep) {
        if (this.m_core2 != null) {
            return nativeStoryThreadUpdate(this.m_core2.GetInstanceID(), this.m_iInstanceID, timeStep);
        }
        return -1;
    }

    public int execute() {
        if (this.m_core2 != null) {
            return nativeStoryThreadExecute(this.m_core2.GetInstanceID(), this.m_iInstanceID);
        }
        return -1;
    }

    public VariableData getVariable(VariableDeclaration declaration) {
        if (this.m_core2 == null || declaration == null) {
            return null;
        }
        int iVD = nativeStoryThreadGetVariable(this.m_core2.GetInstanceID(), this.m_iInstanceID, declaration.getInstanceID());
        if (iVD >= 0) {
            return new VariableData(this.m_core2, iVD);
        }
        return null;
    }

    public VariableData getVariableByName(String varName) {
        if (this.m_core2 == null) {
            return null;
        }
        int iVD = nativeStoryThreadGetVariableByName(this.m_core2.GetInstanceID(), this.m_iInstanceID, varName);
        if (iVD >= 0) {
            return new VariableData(this.m_core2, iVD);
        }
        return null;
    }

    public int setExceptionHandler(EventHandler eventHandler) {
        if (this.m_core2 == null || eventHandler == null) {
            return -1;
        }
        return nativeStoryThreadSetExceptionHandler(this.m_core2.GetInstanceID(), this.m_iInstanceID, eventHandler.getInstanceID());
    }

    public Command getCurrentCommand() {
        if (this.m_core2 == null) {
            return null;
        }
        int iC = nativeStoryThreadGetCurrentCommand(this.m_core2.GetInstanceID(), this.m_iInstanceID);
        if (iC >= 0) {
            return new Command(this.m_core2, iC);
        }
        return null;
    }

    public Boolean getSynchronous() {
        boolean z = false;
        if (!(this.m_core2 == null || nativeStoryThreadGetSynchronous(this.m_core2.GetInstanceID(), this.m_iInstanceID) == 0)) {
            z = true;
        }
        return Boolean.valueOf(z);
    }
}
