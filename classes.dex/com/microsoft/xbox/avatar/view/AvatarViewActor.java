package com.microsoft.xbox.avatar.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.avatar.model.AvatarViewActorVM;
import com.xbox.avatarrenderer.Kernel.AvatarManifestEditor;
import com.xbox.avatarrenderer.Vector3;

public class AvatarViewActor extends View {
    public static final int AVATAR_RUN_IN_LEFT = 1;
    public static final int AVATAR_RUN_IN_RIGHT = 2;
    private AvatarViewActorVM actorVM;
    private int align;
    private Vector3 avatarPos;
    private Vector3 avatarRot;
    private boolean idleProp;

    public AvatarViewActor(Context context) {
        super(context);
        this.actorVM = null;
        this.avatarPos = new Vector3(0.0f, 0.0f, 0.0f);
        this.avatarRot = new Vector3(0.0f, 0.0f, 0.0f);
        this.align = 1;
        this.idleProp = false;
    }

    public AvatarViewActor(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.actorVM = null;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AvatarViewActor);
        this.avatarPos = new Vector3(a.getFloat(1, 0.0f), a.getFloat(2, 0.0f), a.getFloat(3, 0.0f));
        this.avatarRot = new Vector3(a.getFloat(4, 0.0f), a.getFloat(5, 0.0f), a.getFloat(6, 0.0f));
        this.align = a.getInt(0, 1);
        this.idleProp = a.getBoolean(7, false);
    }

    public void setActorVM(AvatarViewActorVM actorVM) {
        if (this.actorVM != actorVM) {
            this.actorVM = actorVM;
            this.actorVM.initializeActorSpecificData(this.align, this.avatarPos, this.avatarRot, this.idleProp);
        }
    }

    public void animate(XLEAvatarAnimationAction action, int transitionMs) {
        this.actorVM.animate(action, transitionMs);
    }

    public void hitboxOnClick() {
        this.actorVM.hitboxOnClick();
    }

    public void hitboxOnTouch(MotionEvent event) {
        this.actorVM.hitboxOnTouch(event);
    }

    public void onFinishAnimation() {
        this.actorVM.onFinishAnimation();
    }

    public String[] getLastPlayedAnimations() {
        return this.actorVM.getLastPlayedAnimations();
    }

    public void clearLastPlayedAnimations() {
        this.actorVM.clearLastPlayedAnimations();
    }

    public boolean getIsLoaded() {
        return this.actorVM.getIsLoaded();
    }

    public boolean getIsMale() {
        return this.actorVM.getIsMale();
    }

    public boolean getIsShadowtarVisible() {
        return this.actorVM.getIsShadowtarVisible();
    }

    public AvatarManifestEditor getManifestEditor() {
        return this.actorVM.getManifestEditor();
    }
}
