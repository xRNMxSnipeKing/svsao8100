package com.microsoft.xbox.service.model.smartglass;

import com.microsoft.xbox.toolkit.CryptoUtil;
import com.microsoft.xbox.toolkit.Ready;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLELog;
import com.microsoft.xbox.toolkit.network.XLEThreadPool;

public class SmartGlassXBLTextInputControllerViewModel {
    private static SmartGlassXBLTextInputControllerViewModel instance = new SmartGlassXBLTextInputControllerViewModel();
    private XBLTextInputState currentState = null;
    private XBLText currentText = null;
    private Runnable onKeyboardStateChangedRunnable = null;
    private Runnable onTextChangedRunnable = null;

    public native void nativeGetCurrentKeyboardState(XBLTextInputState xBLTextInputState);

    public native void nativeGetCurrentText(XBLText xBLText);

    public native void nativeOnCancelButtonClick();

    public native void nativeOnOKButtonClick();

    public native void nativeSetText(byte[] bArr, long j, long j2, XBLText xBLText);

    public static SmartGlassXBLTextInputControllerViewModel getInstance() {
        return instance;
    }

    public XBLText SetText(final XBLText input) {
        final Ready ready = new Ready();
        final XBLText output = new XBLText();
        XLEThreadPool.nativeOperationsThreadPool.run(new Runnable() {
            public void run() {
                SmartGlassXBLTextInputControllerViewModel.this.nativeSetText(CryptoUtil.BEUTF16(input.text), input.selectionIndex, input.selectionLength, output);
                ready.setReady();
            }
        });
        ready.waitForReady();
        return output;
    }

    public void OnOKButtonClick() {
        final Ready ready = new Ready();
        XLEThreadPool.nativeOperationsThreadPool.run(new Runnable() {
            public void run() {
                SmartGlassXBLTextInputControllerViewModel.this.nativeOnOKButtonClick();
                ready.setReady();
            }
        });
        ready.waitForReady();
    }

    public void OnCancelButtonClick() {
        final Ready ready = new Ready();
        XLEThreadPool.nativeOperationsThreadPool.run(new Runnable() {
            public void run() {
                SmartGlassXBLTextInputControllerViewModel.this.nativeOnCancelButtonClick();
                ready.setReady();
            }
        });
        ready.waitForReady();
    }

    public XBLText getText() {
        return this.currentText;
    }

    public XBLTextInputState getTextInputState() {
        return this.currentState;
    }

    public void updateText() {
        XLELog.Diagnostic("SmartGlassXLBTextInputControllerView", "updateText");
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        final XBLText text = new XBLText();
        final Ready ready = new Ready();
        XLEThreadPool.nativeOperationsThreadPool.run(new Runnable() {
            public void run() {
                SmartGlassXBLTextInputControllerViewModel.this.nativeGetCurrentText(text);
                ready.setReady();
            }
        });
        ready.waitForReady();
        this.currentText = text;
        if (instance.onTextChangedRunnable != null) {
            instance.onTextChangedRunnable.run();
        }
    }

    public void updateKeyboardState() {
        XLELog.Diagnostic("SmartGlassXLBTextInputControllerView", "updateKeyboardState");
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        final XBLTextInputState state = new XBLTextInputState();
        final Ready ready = new Ready();
        XLEThreadPool.nativeOperationsThreadPool.run(new Runnable() {
            public void run() {
                SmartGlassXBLTextInputControllerViewModel.this.nativeGetCurrentKeyboardState(state);
                ready.setReady();
            }
        });
        ready.waitForReady();
        this.currentState = state;
        if (instance.onKeyboardStateChangedRunnable != null) {
            instance.onKeyboardStateChangedRunnable.run();
        }
        updateText();
    }

    public static void OnTextChanged() {
        ThreadManager.UIThreadPost(new Runnable() {
            public void run() {
                SmartGlassXBLTextInputControllerViewModel.instance.updateText();
            }
        });
    }

    public static void OnKeyboardStateChanged() {
        ThreadManager.UIThreadPost(new Runnable() {
            public void run() {
                SmartGlassXBLTextInputControllerViewModel.instance.updateKeyboardState();
            }
        });
    }

    public void setOnTextChangedRunnable(Runnable r) {
        this.onTextChangedRunnable = r;
    }

    public void setOnKeyboardStateChangedRunnable(Runnable r) {
        this.onKeyboardStateChangedRunnable = r;
    }
}
