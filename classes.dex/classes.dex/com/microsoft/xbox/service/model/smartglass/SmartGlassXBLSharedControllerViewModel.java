package com.microsoft.xbox.service.model.smartglass;

import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.network.XLEThreadPool;

public class SmartGlassXBLSharedControllerViewModel {
    public static final int XBLSGControl_BROWSER = 2;
    public static final int XBLSGControl_GESTURE = 1;
    public static final int XBLSGControl_TEXT = 3;
    public static final int XBLSGControl_UNKNOWN = 0;
    private static SmartGlassXBLSharedControllerViewModel instance = new SmartGlassXBLSharedControllerViewModel();
    private boolean canBrowserControlBeActive;
    private boolean canGestureControlBeActive;
    private boolean canTextControlBeActive;
    private boolean initialized = false;
    private boolean isRemoteOpenedByAnotherUser;
    private Runnable onControllerStateChangedRunnable = null;
    private int preferredControl;
    private SmartGlassXBLTextInputControllerViewModel textInputViewModel = SmartGlassXBLTextInputControllerViewModel.getInstance();

    public native void nativeDealloc();

    public native void nativeInit();

    public native void nativeSetCurrentControllerState(int i);

    public native void nativeStart();

    public native void nativeStop();

    public static SmartGlassXBLSharedControllerViewModel getInstance() {
        return instance;
    }

    private SmartGlassXBLSharedControllerViewModel() {
    }

    public boolean getCanGestureControlBeActive() {
        return this.canGestureControlBeActive;
    }

    public boolean getCanBrowserControlBeActive() {
        return this.canBrowserControlBeActive;
    }

    public boolean getCanTextControlBeActive() {
        return this.canTextControlBeActive;
    }

    public boolean getIsRemoteOpenedByAnotherUser() {
        return this.isRemoteOpenedByAnotherUser;
    }

    public int getPreferredControl() {
        return this.preferredControl;
    }

    public SmartGlassXBLTextInputControllerViewModel getTextInputViewModel() {
        return this.textInputViewModel;
    }

    public boolean isInitialized() {
        return this.initialized;
    }

    public void init() {
        this.initialized = true;
        XLEThreadPool.nativeOperationsThreadPool.run(new Runnable() {
            public void run() {
                SmartGlassXBLSharedControllerViewModel.this.nativeInit();
            }
        });
    }

    public void dealloc() {
        this.initialized = false;
        XLEThreadPool.nativeOperationsThreadPool.run(new Runnable() {
            public void run() {
                SmartGlassXBLSharedControllerViewModel.this.nativeDealloc();
            }
        });
    }

    public void start() {
        XLEThreadPool.nativeOperationsThreadPool.run(new Runnable() {
            public void run() {
                SmartGlassXBLSharedControllerViewModel.this.nativeStart();
            }
        });
    }

    public void stop() {
        XLEThreadPool.nativeOperationsThreadPool.run(new Runnable() {
            public void run() {
                SmartGlassXBLSharedControllerViewModel.this.nativeStop();
            }
        });
    }

    public void setOnControllerStateChangedRunnable(Runnable r) {
        this.onControllerStateChangedRunnable = r;
    }

    public void setCurrentControllerState(int nativeState) {
        nativeSetCurrentControllerState(nativeState);
    }

    public static void onControllerStateChanged(boolean canGestureControlBeActive, boolean canBrowserControlBeActive, boolean canTextControlBeActive, boolean isRemoteOpenedByAnotherUser, int preferredControl) {
        final boolean z = canGestureControlBeActive;
        final boolean z2 = canBrowserControlBeActive;
        final boolean z3 = canTextControlBeActive;
        final int i = preferredControl;
        final boolean z4 = isRemoteOpenedByAnotherUser;
        ThreadManager.UIThreadPost(new Runnable() {
            public void run() {
                SmartGlassXBLSharedControllerViewModel.getInstance().canGestureControlBeActive = z;
                SmartGlassXBLSharedControllerViewModel.getInstance().canBrowserControlBeActive = z2;
                SmartGlassXBLSharedControllerViewModel.getInstance().canTextControlBeActive = z3;
                SmartGlassXBLSharedControllerViewModel.getInstance().preferredControl = i;
                SmartGlassXBLSharedControllerViewModel.getInstance().isRemoteOpenedByAnotherUser = z4;
                if (SmartGlassXBLSharedControllerViewModel.instance.onControllerStateChangedRunnable != null) {
                    SmartGlassXBLSharedControllerViewModel.instance.onControllerStateChangedRunnable.run();
                }
            }
        });
    }
}
