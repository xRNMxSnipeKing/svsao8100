package com.microsoft.xbox.avatar.model;

import com.xbox.avatarrenderer.Vector3;
import java.util.ArrayList;

public abstract class AvatarViewVM {
    protected ArrayList<AvatarViewActorVM> actors = new ArrayList();
    protected Vector3 cameraPos;

    public abstract void onSceneBegin();

    public void registerActor(AvatarViewActorVM actor) {
        int index = this.actors.size();
        this.actors.add(actor);
        actor.setViewToSignalOnShadowtarVisible(this);
        actor.setNotifyInitializedCallback(new Runnable() {
            public void run() {
                AvatarViewVM.this.checkIfAllInitialized();
            }
        });
        actor.initializeActorSceneData(index);
    }

    public void onSceneEnd() {
        for (int i = 0; i < this.actors.size(); i++) {
            ((AvatarViewActorVM) this.actors.get(i)).onSceneEnd();
        }
    }

    public void initializeViewSpecificData(Vector3 cameraPos) {
        this.cameraPos = cameraPos;
    }

    public void onDestroy() {
        for (int i = 0; i < this.actors.size(); i++) {
            ((AvatarViewActorVM) this.actors.get(i)).onDestroy();
        }
    }

    public void setAllAvatarsToShadowtar() {
        AvatarRendererModel.getInstance().purgeScene();
        for (int i = 0; i < this.actors.size(); i++) {
            ((AvatarViewActorVM) this.actors.get(i)).setToShadowtar();
        }
    }

    private void checkIfAllInitialized() {
        int i = 0;
        while (i < this.actors.size()) {
            if (((AvatarViewActorVM) this.actors.get(i)).getIsInInitializedState()) {
                i++;
            } else {
                return;
            }
        }
        for (i = 0; i < this.actors.size(); i++) {
            ((AvatarViewActorVM) this.actors.get(i)).setToEntering();
        }
    }
}
