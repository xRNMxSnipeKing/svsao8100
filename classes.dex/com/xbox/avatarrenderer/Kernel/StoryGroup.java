package com.xbox.avatarrenderer.Kernel;

import com.xbox.avatarrenderer.Core2Renderer;
import com.xbox.avatarrenderer.WrapperBase;

public class StoryGroup extends WrapperBase {
    private native int nativeStoryGroupAddStory(int i, int i2, int i3);

    private native int nativeStoryGroupDeclareVariable(int i, int i2, String str, int i3);

    private native int nativeStoryGroupExecute(int i, int i2);

    private native int nativeStoryGroupGetStoriesCount(int i, int i2);

    private native int nativeStoryGroupGetStory(int i, int i2, int i3);

    public StoryGroup(Core2Renderer core2, int index) {
        super(core2, index);
    }

    public int getStoriesCount() {
        if (this.m_core2 != null) {
            return nativeStoryGroupGetStoriesCount(this.m_core2.GetInstanceID(), this.m_iInstanceID);
        }
        return -1;
    }

    public Story getStory(int idx) {
        if (this.m_core2 == null) {
            return null;
        }
        int iStoryID = nativeStoryGroupGetStory(this.m_core2.GetInstanceID(), this.m_iInstanceID, idx);
        if (iStoryID >= 0) {
            return new Story(this.m_core2, iStoryID);
        }
        return null;
    }

    public int addStory(Story story) {
        if (this.m_core2 != null) {
            return nativeStoryGroupAddStory(this.m_core2.GetInstanceID(), this.m_iInstanceID, story.getInstanceID());
        }
        return -1;
    }

    public int execute() {
        if (this.m_core2 != null) {
            return nativeStoryGroupExecute(this.m_core2.GetInstanceID(), this.m_iInstanceID);
        }
        return -1;
    }

    public int declareVariable(String variableName, VariableData variableData) {
        if (this.m_core2 == null || variableData == null) {
            return -1;
        }
        return nativeStoryGroupDeclareVariable(this.m_core2.GetInstanceID(), this.m_iInstanceID, variableName, variableData.getInstanceID());
    }
}
