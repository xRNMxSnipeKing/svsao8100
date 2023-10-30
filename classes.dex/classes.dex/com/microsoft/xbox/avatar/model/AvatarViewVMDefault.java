package com.microsoft.xbox.avatar.model;

import com.microsoft.xbox.toolkit.ThreadManager;
import com.xbox.avatarrenderer.AvatarEditor.AvatarEditorEditEvent;
import com.xbox.avatarrenderer.Core2Callbacks;
import com.xbox.avatarrenderer.Kernel.KernelScriptingHelper;
import com.xbox.avatarrenderer.Kernel.ScriptException;
import com.xbox.avatarrenderer.Vector3;

public class AvatarViewVMDefault extends AvatarViewVM implements Core2Callbacks {
    private static final String CAMERA_NAME = "avatarViewCamera";

    public void onSceneBegin() {
        AvatarRendererModel.getInstance().purgeScene();
        KernelScriptingHelper helper = AvatarRendererModel.getInstance().getCore2Model().createKernelScriptingHelper();
        if (helper != null && helper.createCamera(CAMERA_NAME) >= 0 && helper.addObjectToScene(CAMERA_NAME, CAMERA_NAME) >= 0 && helper.setLocalTransform(CAMERA_NAME, this.cameraPos, new Vector3(0.0f, 0.0f, 0.0f), new Vector3(1.0f, 1.0f, 1.0f)) >= 0 && helper.setCameraFieldOfView(CAMERA_NAME, 0.7853982f) >= 0 && helper.setActiveCamera(CAMERA_NAME, null) >= 0 && helper.addNotifier(AvatarScriptCatalog.NOTIFY_VIEW_CAMERA_READY, this) >= 0) {
            helper.execute();
        }
    }

    private void finishOnSceneBegin() {
        for (int i = 0; i < this.actors.size(); i++) {
            ((AvatarViewActorVM) this.actors.get(i)).onSceneBegin();
        }
    }

    public void invokeAvatarEditorEditEvent(AvatarEditorEditEvent aeee) {
    }

    public void invokeScriptException(ScriptException scriptException) {
    }

    public void onNotify(final int iContext) {
        final int sceneIndexOnPost = AvatarRendererModel.getInstance().getSceneIndex();
        ThreadManager.UIThreadPost(new Runnable() {
            public void run() {
                if (AvatarRendererModel.getInstance().getSceneIndex() == sceneIndexOnPost) {
                    switch (iContext) {
                        case AvatarScriptCatalog.NOTIFY_VIEW_CAMERA_READY /*263*/:
                            AvatarViewVMDefault.this.finishOnSceneBegin();
                            return;
                        default:
                            return;
                    }
                }
            }
        });
    }

    public void onDestroy() {
        super.onDestroy();
        AvatarRendererModel.getInstance().getCore2Model().unregisterCallBack(this);
    }
}
