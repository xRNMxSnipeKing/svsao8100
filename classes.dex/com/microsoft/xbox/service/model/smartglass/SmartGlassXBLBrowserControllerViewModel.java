package com.microsoft.xbox.service.model.smartglass;

import com.microsoft.xbox.toolkit.Ready;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.network.XLEThreadPool;
import com.microsoft.xbox.toolkit.system.SystemUtil;

public class SmartGlassXBLBrowserControllerViewModel {
    public static final int INPUT_STYLE_DPAD = 2;
    public static final int INPUT_STYLE_TOUCH = 1;
    public static final int INPUT_STYLE_UNSPECIFIED = 0;
    public static final int LRC_TOUCH_ACTION_DOWN = 1;
    public static final int LRC_TOUCH_ACTION_MOVE = 2;
    public static final int LRC_TOUCH_ACTION_NONE = 0;
    public static final int LRC_TOUCH_ACTION_UP = 4;
    private static SmartGlassXBLBrowserControllerViewModel instance = new SmartGlassXBLBrowserControllerViewModel();
    private boolean boolResult;
    private String currentUrl;
    private Runnable onUrlChangedRunnable = null;
    private Runnable onUrlChangingRunnable = null;
    private String stringResult;

    private native void nativeBack();

    private native boolean nativeGetNavigationState();

    private native void nativeGetUrl();

    private native void nativeNavigate(String str);

    private native void nativeRefreshOrStop();

    private native void nativeSendAppInfo(float f, float f2);

    private native void nativeSendScrollFrame(ScrollPoint scrollPoint);

    private native void nativeSendTouchFrame(TouchFrame touchFrame);

    private native void nativeShowBrowserControls();

    public static SmartGlassXBLBrowserControllerViewModel getInstance() {
        return instance;
    }

    public void setOnUrlChangingRunnable(Runnable r) {
        this.onUrlChangingRunnable = r;
    }

    public void setOnUrlChangedRunnable(Runnable r) {
        this.onUrlChangedRunnable = r;
    }

    public String getCurrentUrl() {
        return this.currentUrl;
    }

    public void updateUrl() {
        final Ready ready = new Ready();
        XLEThreadPool.nativeOperationsThreadPool.run(new Runnable() {
            public void run() {
                SmartGlassXBLBrowserControllerViewModel.this.nativeGetUrl();
                ready.setReady();
            }
        });
        ready.waitForReady();
    }

    public boolean getIsNavigating() {
        final Ready ready = new Ready();
        XLEThreadPool.nativeOperationsThreadPool.run(new Runnable() {
            public void run() {
                SmartGlassXBLBrowserControllerViewModel.this.boolResult = SmartGlassXBLBrowserControllerViewModel.this.nativeGetNavigationState();
                ready.setReady();
            }
        });
        ready.waitForReady();
        return this.boolResult;
    }

    public void refreshOrStop() {
        final Ready ready = new Ready();
        XLEThreadPool.nativeOperationsThreadPool.run(new Runnable() {
            public void run() {
                SmartGlassXBLBrowserControllerViewModel.this.nativeRefreshOrStop();
                ready.setReady();
            }
        });
        ready.waitForReady();
    }

    public void back() {
        final Ready ready = new Ready();
        XLEThreadPool.nativeOperationsThreadPool.run(new Runnable() {
            public void run() {
                SmartGlassXBLBrowserControllerViewModel.this.nativeBack();
                ready.setReady();
            }
        });
        ready.waitForReady();
    }

    public void navigate(final String newurl) {
        final Ready ready = new Ready();
        XLEThreadPool.nativeOperationsThreadPool.run(new Runnable() {
            public void run() {
                SmartGlassXBLBrowserControllerViewModel.this.nativeNavigate(newurl);
                ready.setReady();
            }
        });
    }

    public void showBrowserControls() {
        final Ready ready = new Ready();
        XLEThreadPool.nativeOperationsThreadPool.run(new Runnable() {
            public void run() {
                SmartGlassXBLBrowserControllerViewModel.this.nativeShowBrowserControls();
                ready.setReady();
            }
        });
        ready.waitForReady();
    }

    public void sendTouchFrame(final TouchFrame frame) {
        final Ready ready = new Ready();
        XLEThreadPool.nativeOperationsThreadPool.run(new Runnable() {
            public void run() {
                SmartGlassXBLBrowserControllerViewModel.this.nativeSendTouchFrame(frame);
                ready.setReady();
            }
        });
        ready.waitForReady();
    }

    public void sendScrollFrame(final ScrollPoint frame) {
        final Ready ready = new Ready();
        XLEThreadPool.nativeOperationsThreadPool.run(new Runnable() {
            public void run() {
                SmartGlassXBLBrowserControllerViewModel.this.nativeSendScrollFrame(frame);
                ready.setReady();
            }
        });
        ready.waitForReady();
    }

    public void sendAppInfo() {
        final Ready ready = new Ready();
        XLEThreadPool.nativeOperationsThreadPool.run(new Runnable() {
            public void run() {
                SmartGlassXBLBrowserControllerViewModel.this.nativeSendAppInfo(SystemUtil.getScreenWidthInches(), SystemUtil.getScreenHeightInches());
                ready.setReady();
            }
        });
        ready.waitForReady();
    }

    public static void onUrlChanging(final String url) {
        ThreadManager.UIThreadPost(new Runnable() {
            public void run() {
                SmartGlassXBLBrowserControllerViewModel.instance.currentUrl = url;
                if (SmartGlassXBLBrowserControllerViewModel.instance.onUrlChangingRunnable != null) {
                    SmartGlassXBLBrowserControllerViewModel.instance.onUrlChangingRunnable.run();
                }
            }
        });
    }

    public static void onUrlChanged(final String url) {
        ThreadManager.UIThreadPost(new Runnable() {
            public void run() {
                SmartGlassXBLBrowserControllerViewModel.instance.currentUrl = url;
                if (SmartGlassXBLBrowserControllerViewModel.instance.onUrlChangedRunnable != null) {
                    SmartGlassXBLBrowserControllerViewModel.instance.onUrlChangedRunnable.run();
                }
            }
        });
    }
}
