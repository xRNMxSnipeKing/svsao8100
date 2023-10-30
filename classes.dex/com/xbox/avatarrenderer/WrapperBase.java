package com.xbox.avatarrenderer;

public class WrapperBase {
    protected Core2Renderer m_core2 = null;
    protected int m_iInstanceID = -1;

    public WrapperBase(Core2Renderer core2, int index) {
        this.m_core2 = core2;
        this.m_iInstanceID = index;
    }

    public int getInstanceID() {
        return this.m_iInstanceID;
    }
}
