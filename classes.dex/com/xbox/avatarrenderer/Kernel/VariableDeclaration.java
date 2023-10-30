package com.xbox.avatarrenderer.Kernel;

import com.xbox.avatarrenderer.Core2Renderer;
import com.xbox.avatarrenderer.Core2Renderer.VARIABLE_SCOPE;
import com.xbox.avatarrenderer.WrapperBase;

public class VariableDeclaration extends WrapperBase {
    private native int nativeVariableDeclarationGetDefaultValue(int i, int i2);

    private native int nativeVariableDeclarationGetIndex(int i, int i2);

    private native String nativeVariableDeclarationGetName(int i, int i2);

    private native int nativeVariableDeclarationGetScope(int i, int i2);

    private native int nativeVariableDeclarationSetDefaultValue(int i, int i2, int i3);

    private native int nativeVariableDeclarationSetName(int i, int i2, String str);

    public VariableDeclaration(Core2Renderer core2, int index) {
        super(core2, index);
    }

    public String getName() {
        if (this.m_core2 != null) {
            return nativeVariableDeclarationGetName(this.m_core2.GetInstanceID(), this.m_iInstanceID);
        }
        return null;
    }

    public int setName(String name) {
        if (this.m_core2 != null) {
            return nativeVariableDeclarationSetName(this.m_core2.GetInstanceID(), this.m_iInstanceID, name);
        }
        return -1;
    }

    public int getIndex() {
        if (this.m_core2 != null) {
            return nativeVariableDeclarationGetIndex(this.m_core2.GetInstanceID(), this.m_iInstanceID);
        }
        return -1;
    }

    public VARIABLE_SCOPE getScope() {
        VARIABLE_SCOPE scope = VARIABLE_SCOPE.CONSTANT;
        if (this.m_core2 == null) {
            return scope;
        }
        int iScope = nativeVariableDeclarationGetIndex(this.m_core2.GetInstanceID(), this.m_iInstanceID);
        if (iScope < 0 || iScope >= VARIABLE_SCOPE.values().length) {
            return scope;
        }
        return VARIABLE_SCOPE.values()[iScope];
    }

    public VariableData getDefaultValue() {
        if (this.m_core2 == null) {
            return null;
        }
        int iVD = nativeVariableDeclarationGetDefaultValue(this.m_core2.GetInstanceID(), this.m_iInstanceID);
        if (iVD >= 0) {
            return new VariableData(this.m_core2, iVD);
        }
        return null;
    }

    public int setDefaultValue(VariableData data) {
        if (this.m_core2 == null) {
            return -1;
        }
        return nativeVariableDeclarationSetDefaultValue(this.m_core2.GetInstanceID(), this.m_iInstanceID, data != null ? data.getInstanceID() : -1);
    }
}
