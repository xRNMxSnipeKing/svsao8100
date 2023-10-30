package com.microsoft.xbox.avatar.model;

import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.toolkit.system.SystemUtil;
import com.xbox.avatarrenderer.Core2Renderer;

public class AvatarRendererModel {
    public static final boolean TRANSLUCENT = true;
    private static AvatarRendererModel instance = new AvatarRendererModel();
    private Core2Renderer core2Model;
    private boolean glThreadRunningAnimationDesired = true;
    private boolean glThreadRunningScreenDesired = true;
    private boolean isDestroyed = false;
    private boolean isInitialized = false;
    private boolean pendingDisableWebAccess = false;
    private int sceneIndex = 0;

    public static AvatarRendererModel getInstance() {
        return instance;
    }

    private AvatarRendererModel() {
        boolean z = true;
        if (Thread.currentThread() != ThreadManager.UIThread) {
            z = false;
        }
        XLEAssert.assertTrue(z);
        this.core2Model = new Core2Renderer();
    }

    public boolean didNativeLibraryLoad() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        return this.core2Model.didNativeLibraryLoad();
    }

    public boolean initializeOOBE() {
        boolean z;
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        if (this.isInitialized) {
            z = false;
        } else {
            z = true;
        }
        XLEAssert.assertTrue(z);
        this.isInitialized = true;
        XLEAssert.assertNotNull(this.core2Model);
        if (XboxApplication.AssetManager == null || XboxApplication.Instance.getCacheDir() == null || XboxApplication.Instance.getCacheDir().getAbsolutePath() == null) {
            return false;
        }
        int rv = this.core2Model.initialize(XboxApplication.AssetManager, XboxApplication.Instance.getCacheDir().getAbsolutePath());
        if (this.pendingDisableWebAccess) {
            this.pendingDisableWebAccess = false;
            TEST_disableWebAccess(true);
        }
        if (rv < 0) {
            return false;
        }
        return true;
    }

    private void initializeNoOOBE() {
        boolean z;
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        if (this.isInitialized) {
            z = false;
        } else {
            z = true;
        }
        XLEAssert.assertTrue(z);
        this.isInitialized = true;
        this.core2Model.initialize(XboxApplication.AssetManager, XboxApplication.Instance.getCacheDir().getAbsolutePath());
        if (this.pendingDisableWebAccess) {
            this.pendingDisableWebAccess = false;
            TEST_disableWebAccess(true);
        }
    }

    public boolean getAntiAlias() {
        return !SystemUtil.isHDScreen();
    }

    public void TEST_disableWebAccess(boolean disable) {
    }

    public Core2Renderer getCore2Model() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        if (!this.isInitialized) {
            initializeNoOOBE();
        }
        XLEAssert.assertTrue(this.isInitialized);
        return this.core2Model;
    }

    public void testTearDown() {
    }

    public boolean isDestroyed() {
        return this.isDestroyed;
    }

    public void purgeScene() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        this.sceneIndex++;
        if (getCore2Model() != null) {
            getCore2Model().purgeScene();
        }
    }

    public int getSceneIndex() {
        return this.sceneIndex;
    }

    public void setGLThreadRunningScreen(boolean running) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        this.glThreadRunningScreenDesired = running;
        updateGLThreadRunning();
    }

    public void setGLThreadRunningAnimation(boolean running) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        this.glThreadRunningAnimationDesired = running;
        updateGLThreadRunning();
    }

    public void glThreadRunningReset() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        this.glThreadRunningScreenDesired = true;
        this.glThreadRunningAnimationDesired = true;
        updateGLThreadRunning();
    }

    public boolean getGLThreadRunning() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        if (!this.isInitialized || this.isDestroyed) {
            return false;
        }
        return getCore2Model().getGLThreadRunning();
    }

    public void updateGLThreadRunning() {
        if (this.isInitialized && !this.isDestroyed) {
            Core2Renderer core2Model = getCore2Model();
            boolean z = this.glThreadRunningScreenDesired && this.glThreadRunningAnimationDesired;
            core2Model.setGLThreadRunning(z);
        }
    }

    public int getFPS() {
        return (int) getCore2Model().getFrameRate();
    }
}
