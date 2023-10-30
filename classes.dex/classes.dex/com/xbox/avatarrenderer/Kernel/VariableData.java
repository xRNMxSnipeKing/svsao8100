package com.xbox.avatarrenderer.Kernel;

import com.xbox.avatarrenderer.Core2Renderer;
import com.xbox.avatarrenderer.Core2Renderer.VARIABLE_TYPE;
import com.xbox.avatarrenderer.Vector3;
import com.xbox.avatarrenderer.WrapperBase;

public class VariableData extends WrapperBase {
    private native int nativeVariableDataGetBool(int i, int i2);

    private native float nativeVariableDataGetFloat(int i, int i2);

    private native String nativeVariableDataGetGuid(int i, int i2);

    private native int nativeVariableDataGetInt(int i, int i2);

    private native String nativeVariableDataGetText(int i, int i2);

    private native int nativeVariableDataGetType(int i, int i2);

    private native Vector3 nativeVariableDataGetVector3(int i, int i2);

    private native int nativeVariableDataInvalidate(int i, int i2);

    private native int nativeVariableDataSet(int i, int i2, int i3);

    private native void nativeVariableDataSetBool(int i, int i2, int i3);

    private native void nativeVariableDataSetFloat(int i, int i2, float f);

    private native void nativeVariableDataSetGuid(int i, int i2, String str);

    private native void nativeVariableDataSetInt(int i, int i2, int i3);

    private native int nativeVariableDataSetText(int i, int i2, String str);

    private native void nativeVariableDataSetVector3(int i, int i2, Vector3 vector3);

    public VariableData(Core2Renderer core2, int index) {
        super(core2, index);
    }

    public VARIABLE_TYPE getType() {
        VARIABLE_TYPE ty = VARIABLE_TYPE.INVALID;
        if (this.m_core2 == null) {
            return ty;
        }
        int iType = nativeVariableDataGetType(this.m_core2.GetInstanceID(), this.m_iInstanceID);
        if (iType < 0 || iType >= VARIABLE_TYPE.values().length) {
            return ty;
        }
        return VARIABLE_TYPE.values()[iType];
    }

    public String getText() {
        if (this.m_core2 != null) {
            return nativeVariableDataGetText(this.m_core2.GetInstanceID(), this.m_iInstanceID);
        }
        return null;
    }

    public int setText(String txt) {
        if (this.m_core2 != null) {
            return nativeVariableDataSetText(this.m_core2.GetInstanceID(), this.m_iInstanceID, txt);
        }
        return -1;
    }

    public String getGuid() {
        if (this.m_core2 != null) {
            return nativeVariableDataGetGuid(this.m_core2.GetInstanceID(), this.m_iInstanceID);
        }
        return null;
    }

    public void setGuid(String txt) {
        if (this.m_core2 != null) {
            nativeVariableDataSetGuid(this.m_core2.GetInstanceID(), this.m_iInstanceID, txt);
        }
    }

    public Vector3 getVector3() {
        if (this.m_core2 != null) {
            return nativeVariableDataGetVector3(this.m_core2.GetInstanceID(), this.m_iInstanceID);
        }
        return null;
    }

    public void setVector3(Vector3 txt) {
        if (this.m_core2 != null) {
            nativeVariableDataSetVector3(this.m_core2.GetInstanceID(), this.m_iInstanceID, txt);
        }
    }

    public int setInvalidate() {
        if (this.m_core2 != null) {
            return nativeVariableDataInvalidate(this.m_core2.GetInstanceID(), this.m_iInstanceID);
        }
        return -1;
    }

    public int set(VariableData v) {
        if (this.m_core2 == null || v == null) {
            return -1;
        }
        return nativeVariableDataSet(this.m_core2.GetInstanceID(), this.m_iInstanceID, v.getInstanceID());
    }

    public int getInt() {
        if (this.m_core2 != null) {
            return nativeVariableDataGetInt(this.m_core2.GetInstanceID(), this.m_iInstanceID);
        }
        return 0;
    }

    public void setInt(int val) {
        if (this.m_core2 != null) {
            nativeVariableDataSetInt(this.m_core2.GetInstanceID(), this.m_iInstanceID, val);
        }
    }

    public float getFloat() {
        if (this.m_core2 != null) {
            return nativeVariableDataGetFloat(this.m_core2.GetInstanceID(), this.m_iInstanceID);
        }
        return 0.0f;
    }

    public void setFloat(float val) {
        if (this.m_core2 != null) {
            nativeVariableDataSetFloat(this.m_core2.GetInstanceID(), this.m_iInstanceID, val);
        }
    }

    public Boolean getBool() {
        boolean z = false;
        Boolean val = Boolean.valueOf(false);
        if (this.m_core2 == null) {
            return val;
        }
        if (nativeVariableDataGetBool(this.m_core2.GetInstanceID(), this.m_iInstanceID) != 0) {
            z = true;
        }
        return Boolean.valueOf(z);
    }

    public void setBool(Boolean val) {
        if (this.m_core2 != null) {
            nativeVariableDataSetBool(this.m_core2.GetInstanceID(), this.m_iInstanceID, val.booleanValue() ? 1 : 0);
        }
    }
}
