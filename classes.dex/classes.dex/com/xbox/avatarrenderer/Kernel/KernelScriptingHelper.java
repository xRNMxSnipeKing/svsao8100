package com.xbox.avatarrenderer.Kernel;

import com.xbox.avatarrenderer.ASSET_COLOR_TABLE;
import com.xbox.avatarrenderer.Core2Callbacks;
import com.xbox.avatarrenderer.Core2Renderer;
import com.xbox.avatarrenderer.Core2Renderer.ANIMATION_CHAINING_MODE;
import com.xbox.avatarrenderer.Core2Renderer.SEQUENCED_ANIMATION_MODE;
import com.xbox.avatarrenderer.Core2Renderer.VARIABLE_SCOPE;
import com.xbox.avatarrenderer.Kernel.AvatarManifest.AVATAR_BODY_TYPE;
import com.xbox.avatarrenderer.Vector3;
import com.xbox.avatarrenderer.WrapperBase;

public class KernelScriptingHelper extends WrapperBase {
    private static final float DEFAULT_BLENDIN_LENGTH = 0.25f;
    private static final float DEFAULT_BLENDOUT_LENGTH = 0.25f;

    private native int nativeKernelSHAddAnimation(int i, int i2, String str, String str2, float f, float f2);

    private native int nativeKernelSHAddCarryable(int i, int i2, String str, String str2, float f, float f2);

    private native int nativeKernelSHAddCommand(int i, int i2, int i3);

    private native int nativeKernelSHAddNotifier(int i, int i2, int i3, int i4);

    private native int nativeKernelSHAddObjectToScene(int i, int i2, String str, String str2);

    private native int nativeKernelSHCameraLookAt(int i, int i2, String str, String str2, float f, float f2, float f3, float f4, int i3, String str3);

    private native int nativeKernelSHChangeAvatarShape(int i, int i2, String str, String str2);

    private native int nativeKernelSHChangeAvatarShape2(int i, int i2, String str, int i3);

    private native int nativeKernelSHClearVariable(int i, int i2, String str);

    private native int nativeKernelSHCreateAvatarObject(int i, int i2, String str, String str2);

    private native int nativeKernelSHCreateAvatarObject2(int i, int i2, int i3, String str);

    private native int nativeKernelSHCreateCamera(int i, int i2, String str);

    private native int nativeKernelSHCreateRandomAvatar(int i, int i2, int i3, String str);

    private native int nativeKernelSHDeclareVariable(int i, int i2, String str, int i3);

    private native int nativeKernelSHExecute(int i, int i2);

    private native int nativeKernelSHExecuteAndReturnStoryThread(int i, int i2);

    private native int nativeKernelSHExecuteSynchronously(int i, int i2);

    private native int nativeKernelSHFindVariableDeclaration(int i, int i2, String str);

    private native int nativeKernelSHGetVariableDeclaration(int i, int i2, String str);

    private native int nativeKernelSHLoadAnimation(int i, int i2, String str);

    private native int nativeKernelSHLoadAvatarManifest(int i, int i2, String str, String str2);

    private native int nativeKernelSHLoadCarryable(int i, int i2, String str, int i3, int i4, int i5, int i6, String str2);

    private native int nativeKernelSHLoadCarryableAnimation(int i, int i2, String str);

    private native int nativeKernelSHLoadScene(int i, int i2, String str, String str2);

    private native int nativeKernelSHLoadSceneWithStream(int i, int i2, byte[] bArr, String str);

    private native int nativeKernelSHMergeScene(int i, int i2, String str);

    private native int nativeKernelSHPlayAnimation(int i, int i2, String str, String str2, float f, float f2, int i3, String str3);

    private native int nativeKernelSHPlayAnimationList(int i, int i2, String str, String str2, int i3, float f, int i4, String str3);

    private native int nativeKernelSHRemoveObjectFromScene(int i, int i2, String str, int i3);

    private native int nativeKernelSHResetSignal(int i, int i2, String str);

    private native int nativeKernelSHSetActiveCamera(int i, int i2, String str, String str2);

    private native int nativeKernelSHSetCameraFieldOfView(int i, int i2, String str, float f);

    private native int nativeKernelSHSetExceptionHandler(int i, int i2, int i3);

    private native int nativeKernelSHSetLocalPosition(int i, int i2, String str, float f, float f2, float f3);

    private native int nativeKernelSHSetLocalTransform(int i, int i2, String str, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9);

    private native int nativeKernelSHSetLocalXform(int i, int i2, String str, String str2, float f, float f2, float f3, float f4, float f5, float f6);

    private native int nativeKernelSHSetSignal(int i, int i2, String str);

    private native int nativeKernelSHSetVariable(int i, int i2, String str, int i3);

    private native int nativeKernelSHSleep(int i, int i2, float f);

    private native int nativeKernelSHSmoothSetLocalTransform(int i, int i2, String str, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9, float f10, float f11, float f12, int i3, String str2);

    private native int nativeKernelSHSynchronize(int i, int i2, int i3, String str);

    private native int nativeKernelSHWaitSignal(int i, int i2, String str);

    public KernelScriptingHelper(Core2Renderer core2, int index) {
        super(core2, index);
    }

    public int declareVariable(String name, VARIABLE_SCOPE scope) {
        if (this.m_core2 == null || scope == null) {
            return -1;
        }
        return nativeKernelSHDeclareVariable(this.m_core2.GetInstanceID(), this.m_iInstanceID, name, scope.getInt());
    }

    public int loadAnimation(String guid) {
        if (this.m_core2 != null) {
            return nativeKernelSHLoadAnimation(this.m_core2.GetInstanceID(), this.m_iInstanceID, guid);
        }
        return -1;
    }

    public int addAnimation(String name, String guid, float blendOutDuration, float weight) {
        if (this.m_core2 == null || this.m_iInstanceID < 0) {
            return -1;
        }
        return nativeKernelSHAddAnimation(this.m_core2.GetInstanceID(), this.m_iInstanceID, name, guid, blendOutDuration, weight);
    }

    public int addAnimation(String name, String guid, float blendOutDuration) {
        return addAnimation(name, guid, blendOutDuration, 1.0f);
    }

    public int addAnimation(String name, String guid) {
        return addAnimation(name, guid, 0.25f, 1.0f);
    }

    public int createRandomAvatar(AVATAR_BODY_TYPE bodyType, String resultManifest) {
        if (this.m_core2 == null || bodyType == null || this.m_iInstanceID < 0) {
            return -1;
        }
        return nativeKernelSHCreateRandomAvatar(this.m_core2.GetInstanceID(), this.m_iInstanceID, bodyType.getInt(), resultManifest);
    }

    public int createAvatarObject(String manifestVariableName, String resultVariableName) {
        if (this.m_core2 == null || this.m_iInstanceID < 0) {
            return -1;
        }
        return nativeKernelSHCreateAvatarObject(this.m_core2.GetInstanceID(), this.m_iInstanceID, manifestVariableName, resultVariableName);
    }

    public int createAvatarObject(AvatarManifest manifest, String resultVariableName) {
        if (this.m_core2 == null || this.m_iInstanceID < 0 || manifest == null) {
            return -1;
        }
        return nativeKernelSHCreateAvatarObject2(this.m_core2.GetInstanceID(), this.m_iInstanceID, manifest.getInstanceID(), resultVariableName);
    }

    public int addObjectToScene(String objectVariableName, String objectPath) {
        if (this.m_core2 == null || this.m_iInstanceID < 0) {
            return -1;
        }
        return nativeKernelSHAddObjectToScene(this.m_core2.GetInstanceID(), this.m_iInstanceID, objectVariableName, objectPath);
    }

    public int playAnimationList(String objectName, String playlistVariableName, SEQUENCED_ANIMATION_MODE playMode, float blendInDuration, ANIMATION_CHAINING_MODE chainingMode, String eventHandler) {
        if (this.m_core2 == null || this.m_iInstanceID < 0 || playMode == null || chainingMode == null) {
            return -1;
        }
        return nativeKernelSHPlayAnimationList(this.m_core2.GetInstanceID(), this.m_iInstanceID, objectName, playlistVariableName, playMode.getInt(), blendInDuration, chainingMode.getInt(), eventHandler);
    }

    public int execute() {
        if (this.m_core2 == null || this.m_iInstanceID < 0) {
            return -1;
        }
        return nativeKernelSHExecute(this.m_core2.GetInstanceID(), this.m_iInstanceID);
    }

    public int loadAvatarManifest(String gamertag, String resultManifest) {
        if (this.m_core2 == null || this.m_iInstanceID < 0) {
            return -1;
        }
        return nativeKernelSHLoadAvatarManifest(this.m_core2.GetInstanceID(), this.m_iInstanceID, gamertag, resultManifest);
    }

    public int playAnimation(String objectName, String animationVariableNameOrGuid, float blendInDuration, float blendOutDuration) {
        return playAnimation(objectName, animationVariableNameOrGuid, blendInDuration, blendOutDuration, ANIMATION_CHAINING_MODE.REPLACE, null);
    }

    public int playAnimation(String objectName, String animationVariableNameOrGuid, float blendInDuration, float blendOutDuration, ANIMATION_CHAINING_MODE chainingMode) {
        return playAnimation(objectName, animationVariableNameOrGuid, blendInDuration, blendOutDuration, chainingMode, null);
    }

    public int playAnimation(String objectName, String animationVariableNameOrGuid, float blendInDuration, float blendOutDuration, ANIMATION_CHAINING_MODE chainingMode, String eventHandler) {
        if (this.m_core2 == null || this.m_iInstanceID < 0) {
            return -1;
        }
        return nativeKernelSHPlayAnimation(this.m_core2.GetInstanceID(), this.m_iInstanceID, objectName, animationVariableNameOrGuid, blendInDuration, blendOutDuration, chainingMode.getInt(), eventHandler);
    }

    public int loadCarryable(String carryableGuid, ASSET_COLOR_TABLE colorTable, String carryableVariableName) {
        int i = 0;
        if (this.m_core2 == null) {
            return -1;
        }
        int i2;
        int i3;
        int GetInstanceID = this.m_core2.GetInstanceID();
        int i4 = this.m_iInstanceID;
        int i5 = colorTable != null ? 1 : 0;
        if (colorTable != null) {
            i2 = colorTable.color1;
        } else {
            i2 = 0;
        }
        if (colorTable != null) {
            i3 = colorTable.color2;
        } else {
            i3 = 0;
        }
        if (colorTable != null) {
            i = colorTable.color3;
        }
        return nativeKernelSHLoadCarryable(GetInstanceID, i4, carryableGuid, i5, i2, i3, i, carryableVariableName);
    }

    public int loadScene(String sceneAssetId, String sceneOutVariableName) {
        if (this.m_core2 != null) {
            return nativeKernelSHLoadScene(this.m_core2.GetInstanceID(), this.m_iInstanceID, sceneAssetId, sceneOutVariableName);
        }
        return -1;
    }

    public int mergeScene(String sceneVariableName) {
        if (this.m_core2 != null) {
            return nativeKernelSHMergeScene(this.m_core2.GetInstanceID(), this.m_iInstanceID, sceneVariableName);
        }
        return -1;
    }

    public int addCarryable(String playlistVariableName, String animationVariableName, float blendOutDuration, float weight) {
        if (this.m_core2 != null) {
            return nativeKernelSHAddCarryable(this.m_core2.GetInstanceID(), this.m_iInstanceID, playlistVariableName, animationVariableName, blendOutDuration, weight);
        }
        return -1;
    }

    public int addCarryable(String playlistVariableName, String animationVariableName, float blendOutDuration) {
        return addCarryable(playlistVariableName, animationVariableName, blendOutDuration, 1.0f);
    }

    public int addCarryable(String playlistVariableName, String animationVariableName) {
        return addCarryable(playlistVariableName, animationVariableName, 0.25f, 1.0f);
    }

    public int removeObjectFromScene(String objectPath, Boolean bDoDestroy) {
        if (this.m_core2 == null) {
            return -1;
        }
        return nativeKernelSHRemoveObjectFromScene(this.m_core2.GetInstanceID(), this.m_iInstanceID, objectPath, bDoDestroy.booleanValue() ? 1 : 0);
    }

    public int createCamera(String resultVariableName) {
        if (this.m_core2 != null) {
            return nativeKernelSHCreateCamera(this.m_core2.GetInstanceID(), this.m_iInstanceID, resultVariableName);
        }
        return -1;
    }

    public int loadCarryableAnimation(String carryableId) {
        if (this.m_core2 != null) {
            return nativeKernelSHLoadCarryableAnimation(this.m_core2.GetInstanceID(), this.m_iInstanceID, carryableId);
        }
        return -1;
    }

    public int resetSignal(String signalVariableName) {
        if (this.m_core2 != null) {
            return nativeKernelSHResetSignal(this.m_core2.GetInstanceID(), this.m_iInstanceID, signalVariableName);
        }
        return -1;
    }

    public int setSignal(String signalVariableName) {
        if (this.m_core2 != null) {
            return nativeKernelSHSetSignal(this.m_core2.GetInstanceID(), this.m_iInstanceID, signalVariableName);
        }
        return -1;
    }

    public int waitSignal(String signalVariableName) {
        if (this.m_core2 != null) {
            return nativeKernelSHWaitSignal(this.m_core2.GetInstanceID(), this.m_iInstanceID, signalVariableName);
        }
        return -1;
    }

    public int synchronize(int threadsCount, String signalVariableName) {
        if (this.m_core2 != null) {
            return nativeKernelSHSynchronize(this.m_core2.GetInstanceID(), this.m_iInstanceID, threadsCount, signalVariableName);
        }
        return -1;
    }

    public int clearVariable(String variableName) {
        if (this.m_core2 != null) {
            return nativeKernelSHClearVariable(this.m_core2.GetInstanceID(), this.m_iInstanceID, variableName);
        }
        return -1;
    }

    public int sleep(float sleepTime) {
        if (this.m_core2 != null) {
            return nativeKernelSHSleep(this.m_core2.GetInstanceID(), this.m_iInstanceID, sleepTime);
        }
        return -1;
    }

    public int changeAvatarShape(String avatarSceneEntityName, String newAvatarVariableName) {
        if (this.m_core2 != null) {
            return nativeKernelSHChangeAvatarShape(this.m_core2.GetInstanceID(), this.m_iInstanceID, avatarSceneEntityName, newAvatarVariableName);
        }
        return -1;
    }

    public int setLocalPosition(String objectName, Vector3 position) {
        if (this.m_core2 == null || position == null) {
            return -1;
        }
        return nativeKernelSHSetLocalPosition(this.m_core2.GetInstanceID(), this.m_iInstanceID, objectName, position.x, position.y, position.z);
    }

    public int setLocalTransform(String objectName, Vector3 position, Vector3 rotation, Vector3 scale) {
        if (position == null) {
            Vector3 vector3 = new Vector3(0.0f, 0.0f, 0.0f);
        }
        if (rotation == null) {
            vector3 = new Vector3(0.0f, 0.0f, 0.0f);
        }
        if (scale == null) {
            vector3 = new Vector3(1.0f, 1.0f, 1.0f);
        }
        if (this.m_core2 == null) {
            return -1;
        }
        return nativeKernelSHSetLocalTransform(this.m_core2.GetInstanceID(), this.m_iInstanceID, objectName, position.x, position.y, position.z, rotation.x, rotation.y, rotation.z, scale.x, scale.y, scale.z);
    }

    public int setLocalTransform(String objectName, String pPositionVariable, Vector3 rotation, Vector3 scale) {
        if (rotation == null) {
            Vector3 vector3 = new Vector3(0.0f, 0.0f, 0.0f);
        }
        if (scale == null) {
            vector3 = new Vector3(1.0f, 1.0f, 1.0f);
        }
        if (this.m_core2 == null || pPositionVariable == null) {
            return -1;
        }
        return nativeKernelSHSetLocalXform(this.m_core2.GetInstanceID(), this.m_iInstanceID, objectName, pPositionVariable, rotation.x, rotation.y, rotation.z, scale.x, scale.y, scale.z);
    }

    public int setVariable(String variableName, VariableData variableData) {
        if (this.m_core2 == null || variableData == null) {
            return -1;
        }
        return nativeKernelSHSetVariable(this.m_core2.GetInstanceID(), this.m_iInstanceID, variableName, variableData.getInstanceID());
    }

    public int changeAvatarShape(String avatarSceneEntityName, VariableData newAvatarObject) {
        if (this.m_core2 == null || newAvatarObject == null) {
            return -1;
        }
        return nativeKernelSHChangeAvatarShape2(this.m_core2.GetInstanceID(), this.m_iInstanceID, avatarSceneEntityName, newAvatarObject.getInstanceID());
    }

    public VariableDeclaration findVariableDeclaration(String name) {
        if (this.m_core2 == null) {
            return null;
        }
        int iVD = nativeKernelSHFindVariableDeclaration(this.m_core2.GetInstanceID(), this.m_iInstanceID, name);
        if (iVD >= 0) {
            return new VariableDeclaration(this.m_core2, iVD);
        }
        return null;
    }

    public VariableDeclaration getVariableDeclaration(String name) {
        if (this.m_core2 == null) {
            return null;
        }
        int iVD = nativeKernelSHGetVariableDeclaration(this.m_core2.GetInstanceID(), this.m_iInstanceID, name);
        if (iVD >= 0) {
            return new VariableDeclaration(this.m_core2, iVD);
        }
        return null;
    }

    public int cameraLookAt(String avatarName, String cameraPoseConfiguration, float animationLength, float acceleration, float blendInDuration, float blendOutDuration, ANIMATION_CHAINING_MODE chainingMode, String eventHandler) {
        if (this.m_core2 == null) {
            return -1;
        }
        return nativeKernelSHCameraLookAt(this.m_core2.GetInstanceID(), this.m_iInstanceID, avatarName, cameraPoseConfiguration, animationLength, acceleration, blendInDuration, blendOutDuration, chainingMode.getInt(), eventHandler);
    }

    public int cameraLookAt(String avatarName, String cameraPoseConfiguration, float animationLength, float acceleration, float blendInDuration, float blendOutDuration, ANIMATION_CHAINING_MODE chainingMode) {
        return cameraLookAt(avatarName, cameraPoseConfiguration, animationLength, acceleration, blendInDuration, blendOutDuration, chainingMode, null);
    }

    public int cameraLookAt(String avatarName, String cameraPoseConfiguration, float animationLength, float acceleration, float blendInDuration, float blendOutDuration) {
        return cameraLookAt(avatarName, cameraPoseConfiguration, animationLength, acceleration, blendInDuration, blendOutDuration, ANIMATION_CHAINING_MODE.REPLACE, null);
    }

    public int cameraLookAt(String avatarName, String cameraPoseConfiguration, float animationLength, float acceleration, float blendInDuration) {
        return cameraLookAt(avatarName, cameraPoseConfiguration, animationLength, acceleration, blendInDuration, 0.25f, ANIMATION_CHAINING_MODE.REPLACE, null);
    }

    public int cameraLookAt(String avatarName, String cameraPoseConfiguration, float animationLength, float acceleration) {
        return cameraLookAt(avatarName, cameraPoseConfiguration, animationLength, acceleration, 0.25f, 0.25f, ANIMATION_CHAINING_MODE.REPLACE, null);
    }

    public int cameraLookAt(String avatarName, String cameraPoseConfiguration, float animationLength) {
        return cameraLookAt(avatarName, cameraPoseConfiguration, animationLength, 0.0f, 0.25f, 0.25f, ANIMATION_CHAINING_MODE.REPLACE, null);
    }

    public int cameraLookAt(String avatarName, String cameraPoseConfiguration) {
        return cameraLookAt(avatarName, cameraPoseConfiguration, 1.0f, 0.0f, 0.25f, 0.25f, ANIMATION_CHAINING_MODE.REPLACE, null);
    }

    public int addNotifier(int iContext, Core2Callbacks cb) {
        if (cb == null) {
            return -1;
        }
        return nativeKernelSHAddNotifier(this.m_core2.GetInstanceID(), this.m_iInstanceID, iContext, this.m_core2.registerCallBack(cb));
    }

    public int addCommand(Command command) {
        if (this.m_core2 == null || command == null) {
            return -1;
        }
        return nativeKernelSHAddCommand(this.m_core2.GetInstanceID(), this.m_iInstanceID, command.getInstanceID());
    }

    public int executeAndReturnStoryThread() {
        if (this.m_core2 != null) {
            return nativeKernelSHExecuteAndReturnStoryThread(this.m_core2.GetInstanceID(), this.m_iInstanceID);
        }
        return -1;
    }

    public int executeSynchronously() {
        if (this.m_core2 != null) {
            return nativeKernelSHExecuteSynchronously(this.m_core2.GetInstanceID(), this.m_iInstanceID);
        }
        return -1;
    }

    public int loadScene(byte[] streamIn, String sceneOutVariableName) {
        if (this.m_core2 != null) {
            return nativeKernelSHLoadSceneWithStream(this.m_core2.GetInstanceID(), this.m_iInstanceID, streamIn, sceneOutVariableName);
        }
        return -1;
    }

    public int setCameraFieldOfView(String CameraName, float fov) {
        if (this.m_core2 != null) {
            return nativeKernelSHSetCameraFieldOfView(this.m_core2.GetInstanceID(), this.m_iInstanceID, CameraName, fov);
        }
        return -1;
    }

    public int setActiveCamera(String CameraName, String viewName) {
        if (this.m_core2 != null) {
            return nativeKernelSHSetActiveCamera(this.m_core2.GetInstanceID(), this.m_iInstanceID, CameraName, viewName);
        }
        return -1;
    }

    public int setExceptionHandler(Core2Callbacks cb) {
        if (cb == null) {
            return -1;
        }
        return nativeKernelSHSetExceptionHandler(this.m_core2.GetInstanceID(), this.m_iInstanceID, this.m_core2.registerCallBack(cb));
    }

    public int smoothSetLocalTransform(String objectName, Vector3 position, Vector3 rotation, Vector3 scale) {
        return smoothSetLocalTransform(objectName, position, rotation, scale, 0.01f, 1.0f, 0.25f, ANIMATION_CHAINING_MODE.REPLACE, null);
    }

    public int smoothSetLocalTransform(String objectName, Vector3 position, Vector3 rotation, Vector3 scale, float length) {
        return smoothSetLocalTransform(objectName, position, rotation, scale, length, 1.0f, 0.25f, ANIMATION_CHAINING_MODE.REPLACE, null);
    }

    public int smoothSetLocalTransform(String objectName, Vector3 position, Vector3 rotation, Vector3 scale, float length, float acceleration) {
        return smoothSetLocalTransform(objectName, position, rotation, scale, length, acceleration, 0.25f, ANIMATION_CHAINING_MODE.REPLACE, null);
    }

    public int smoothSetLocalTransform(String objectName, Vector3 position, Vector3 rotation, Vector3 scale, float length, float acceleration, float blendOutDuration) {
        return smoothSetLocalTransform(objectName, position, rotation, scale, length, acceleration, blendOutDuration, ANIMATION_CHAINING_MODE.REPLACE, null);
    }

    public int smoothSetLocalTransform(String objectName, Vector3 position, Vector3 rotation, Vector3 scale, float length, float acceleration, float blendOutDuration, ANIMATION_CHAINING_MODE chainingMode) {
        return smoothSetLocalTransform(objectName, position, rotation, scale, length, acceleration, blendOutDuration, chainingMode, null);
    }

    public int smoothSetLocalTransform(String objectName, Vector3 position, Vector3 rotation, Vector3 scale, float length, float acceleration, float blendOutDuration, ANIMATION_CHAINING_MODE chainingMode, String eventHandler) {
        if (position == null) {
            Vector3 vector3 = new Vector3(0.0f, 0.0f, 0.0f);
        }
        if (rotation == null) {
            vector3 = new Vector3(0.0f, 0.0f, 0.0f);
        }
        if (scale == null) {
            vector3 = new Vector3(1.0f, 1.0f, 1.0f);
        }
        if (this.m_core2 == null) {
            return -1;
        }
        return nativeKernelSHSmoothSetLocalTransform(this.m_core2.GetInstanceID(), this.m_iInstanceID, objectName, position.x, position.y, position.z, rotation.x, rotation.y, rotation.z, scale.x, scale.y, scale.z, length, acceleration, blendOutDuration, chainingMode.getInt(), eventHandler);
    }
}
