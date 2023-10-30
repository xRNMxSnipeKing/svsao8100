package com.microsoft.xbox.avatar.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.avatar.model.AvatarRendererModel;
import com.microsoft.xbox.avatar.model.AvatarViewVM;
import com.microsoft.xbox.toolkit.BackgroundThreadWaitor;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.toolkit.ui.ScreenLayout;
import com.xbox.avatarrenderer.Core2View;
import com.xbox.avatarrenderer.Vector3;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class AvatarViewEditor extends RelativeLayout {
    private static final float ASPECT_RATIO = 0.8f;
    public static final int AVATAR_ALIGN_CENTER = 0;
    public static final int AVATAR_ALIGN_LEFT = 1;
    public static final int AVATAR_ALIGN_RIGHT = 2;
    private static final int AVATAR_ANIMATION_TRANSITION_MS = 700;
    private static final int BACKGROUND_THREAD_WAIT_TIMEOUT = 5000;
    private static boolean needDummyViewGLThreadBoot = true;
    protected ArrayList<AvatarViewActor> actors;
    private boolean actorsReady;
    protected int align;
    private AvatarViewVM avatarViewVM;
    private int bootGLThreadVersion;
    protected Vector3 cameraPos;
    private Core2View core2View;
    private boolean glThreadAlive;
    private int hitboxId;
    private LayoutParams layoutParams;
    private boolean layoutReady;
    private boolean lockAspectRatio;
    private Runnable onAnimationCompleted;

    public AvatarViewEditor(Context context) {
        super(context);
        this.layoutParams = null;
        this.glThreadAlive = false;
        this.actorsReady = false;
        this.layoutReady = false;
        this.align = 1;
        this.cameraPos = new Vector3(0.0f, 0.0f, 0.0f);
        this.actors = new ArrayList();
        this.onAnimationCompleted = null;
        this.bootGLThreadVersion = 0;
        this.cameraPos = new Vector3(0.0f, 0.0f, 0.0f);
        this.align = 1;
        this.hitboxId = -1;
        this.lockAspectRatio = false;
    }

    public AvatarViewEditor(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.layoutParams = null;
        this.glThreadAlive = false;
        this.actorsReady = false;
        this.layoutReady = false;
        this.align = 1;
        this.cameraPos = new Vector3(0.0f, 0.0f, 0.0f);
        this.actors = new ArrayList();
        this.onAnimationCompleted = null;
        this.bootGLThreadVersion = 0;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AvatarView);
        this.cameraPos = new Vector3(a.getFloat(2, 0.0f), a.getFloat(3, 0.0f), a.getFloat(4, 0.0f));
        this.align = a.getInt(1, 1);
        this.hitboxId = a.getResourceId(0, -1);
        this.lockAspectRatio = a.getBoolean(5, true);
        a.recycle();
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        for (int i = 0; i < getChildCount(); i++) {
            this.actors.add((AvatarViewActor) getChildAt(i));
        }
        this.actorsReady = true;
        createViewIfReady(false);
    }

    public void addActor(AvatarViewActor actor) {
        this.actors.add(actor);
        this.actorsReady = true;
        createViewIfReady(false);
    }

    public void setAvatarViewVM(AvatarViewVM viewVM) {
        if (this.avatarViewVM != viewVM) {
            XLEAssert.assertTrue(viewVM != null);
            this.avatarViewVM = viewVM;
            this.avatarViewVM.initializeViewSpecificData(this.cameraPos);
        }
        createViewIfReady(false);
    }

    public void startDummyViewGLThread() {
        if (needDummyViewGLThreadBoot) {
            this.layoutParams = new LayoutParams(-1, -1);
            this.layoutReady = true;
            this.actorsReady = true;
            AvatarRendererModel.getInstance().purgeScene();
            innerBootGLThread(false);
        }
        needDummyViewGLThreadBoot = false;
    }

    public static void setNeedDummyViewGLThreadBoot() {
        needDummyViewGLThreadBoot = true;
    }

    public AvatarViewVM getAvatarViewVM() {
        return this.avatarViewVM;
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        boolean z = false;
        super.onSizeChanged(w, h, oldw, oldh);
        this.layoutParams = null;
        if (this.lockAspectRatio) {
            boolean wOk;
            int desiredW = (int) (((float) h) * ASPECT_RATIO);
            int desiredH = (int) (((float) w) / ASPECT_RATIO);
            if (desiredW <= w) {
                wOk = true;
            } else {
                wOk = false;
            }
            boolean hOk;
            if (desiredH <= h) {
                hOk = true;
            } else {
                hOk = false;
            }
            if (wOk || hOk) {
                z = true;
            }
            XLEAssert.assertTrue(z);
            if (wOk) {
                this.layoutParams = new LayoutParams(desiredW, -1);
            } else {
                this.layoutParams = new LayoutParams(-1, desiredH);
            }
        } else {
            this.layoutParams = new LayoutParams(-1, -1);
        }
        switch (this.align) {
            case 0:
                this.layoutParams.addRule(14);
                break;
            case 1:
                this.layoutParams.addRule(9);
                break;
            case 2:
                this.layoutParams.addRule(11);
                break;
        }
        this.layoutParams.addRule(12);
        this.layoutReady = true;
        createViewIfReady(true);
        post(new Runnable() {
            public void run() {
                AvatarViewEditor.this.requestLayout();
            }
        });
    }

    protected void onDetachedFromWindow() {
        endGLThread();
        this.avatarViewVM = null;
        ScreenLayout.removeViewAndWorkaroundAndroidLeaks(this.core2View);
        this.core2View = null;
    }

    private void createViewIfReady(boolean reboot) {
        if (this.avatarViewVM != null && this.layoutReady && this.actorsReady) {
            bootGLThread(reboot);
        }
    }

    private void endGLThread() {
        this.bootGLThreadVersion++;
        if (this.avatarViewVM != null) {
            this.avatarViewVM.onSceneEnd();
        }
        this.glThreadAlive = false;
    }

    public void setHitBox(int hitboxId) {
        View hitboxView = XboxApplication.MainActivity.findViewById(hitboxId);
        if (hitboxView != null) {
            hitboxView.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    AvatarViewEditor.this.hitboxOnClick();
                }
            });
            hitboxView.setOnTouchListener(new OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    AvatarViewEditor.this.hitboxOnTouch(event);
                    return false;
                }
            });
        }
    }

    private void innerBootGLThread(boolean reboot) {
        if (reboot) {
            endGLThread();
        }
        if (!this.glThreadAlive) {
            setHitBox(this.hitboxId);
            this.core2View = new Core2View(XboxApplication.Instance);
            for (int i = getChildCount() - 1; i >= 0; i--) {
                if (getChildAt(i) instanceof Core2View) {
                    removeViewAt(i);
                } else {
                    XLEAssert.assertTrue(getChildAt(i) instanceof AvatarViewActor);
                }
            }
            XLEAssert.assertTrue(this.layoutParams != null);
            addView(this.core2View, this.layoutParams);
            if (AvatarRendererModel.getInstance().getCore2Model() != null) {
                this.core2View.initialize(getContext(), AvatarRendererModel.getInstance().getCore2Model(), Boolean.valueOf(true), Boolean.valueOf(AvatarRendererModel.getInstance().getAntiAlias()));
            }
            this.glThreadAlive = true;
            onGLThreadStart();
        }
    }

    private void bootGLThread(final boolean reboot) {
        final WeakReference<AvatarViewEditor> thisweakptr = new WeakReference(this);
        final int currentVersion = this.bootGLThreadVersion + 1;
        this.bootGLThreadVersion = currentVersion;
        new Thread(new Runnable() {
            public void run() {
                BackgroundThreadWaitor.getInstance().waitForReady(5000);
                ThreadManager.UIThreadPost(new Runnable() {
                    public void run() {
                        AvatarViewEditor thisptr = (AvatarViewEditor) thisweakptr.get();
                        if (thisptr != null && AvatarViewEditor.this.bootGLThreadVersion == currentVersion) {
                            thisptr.innerBootGLThread(reboot);
                        }
                    }
                });
            }
        }).start();
    }

    protected void onGLThreadStart() {
        if (this.avatarViewVM != null) {
            this.avatarViewVM.onSceneBegin();
        }
    }

    public void onPause() {
        if (this.core2View != null) {
            this.core2View.onPause();
        }
    }

    public void onResume() {
        if (this.core2View != null) {
            this.core2View.onResume();
        }
    }

    public void setOnAnimationCompletedRunnable(Runnable r) {
        this.onAnimationCompleted = r;
    }

    public void animate(XLEAvatarAnimationAction action) {
        for (int i = 0; i < this.actors.size(); i++) {
            ((AvatarViewActor) this.actors.get(i)).animate(action, AVATAR_ANIMATION_TRANSITION_MS);
        }
        ThreadManager.UIThreadPostDelayed(new Runnable() {
            public void run() {
                for (int i = 0; i < AvatarViewEditor.this.actors.size(); i++) {
                    ((AvatarViewActor) AvatarViewEditor.this.actors.get(i)).onFinishAnimation();
                }
                AvatarViewEditor.this.onFinishAnimation();
            }
        }, 700);
    }

    protected void hitboxOnClick() {
        for (int i = 0; i < this.actors.size(); i++) {
            ((AvatarViewActor) this.actors.get(i)).hitboxOnClick();
        }
    }

    protected void hitboxOnTouch(MotionEvent event) {
        for (int i = 0; i < this.actors.size(); i++) {
            ((AvatarViewActor) this.actors.get(i)).hitboxOnTouch(event);
        }
    }

    private void onFinishAnimation() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        if (this.onAnimationCompleted != null) {
            this.onAnimationCompleted.run();
            this.onAnimationCompleted = null;
        }
    }

    public boolean getIsLoaded() {
        for (int i = 0; i < this.actors.size(); i++) {
            if (!((AvatarViewActor) this.actors.get(i)).getIsLoaded()) {
                return false;
            }
        }
        return this.actorsReady;
    }

    public void forceRenderFrame() {
        if (this.core2View != null && !AvatarRendererModel.getInstance().getGLThreadRunning()) {
            this.core2View.requestRender();
        }
    }
}
