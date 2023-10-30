package com.microsoft.xbox.xle.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View.MeasureSpec;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.xle.app.XLEApplication;

public class XLEHandleImeRootView extends XLERootView {
    private static final int KEY_BOARD_SHOW_HEIGHT = (XLEApplication.Resources.getDimensionPixelSize(R.dimen.applicationBarHeight) * 2);
    private HandleImeInterface handleImeInterface;

    public interface HandleImeInterface {
        void onDismissKeyboard();

        void onShowKeyboard();
    }

    public XLEHandleImeRootView(Context context) {
        super(context);
    }

    public XLEHandleImeRootView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setHandleImeInterface(HandleImeInterface handleImeInterface) {
        if (this.handleImeInterface != null && handleImeInterface == null) {
            this.handleImeInterface.onDismissKeyboard();
        }
        this.handleImeInterface = handleImeInterface;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int diffHeight = MeasureSpec.getSize(heightMeasureSpec) - getHeight();
        if (Math.abs(diffHeight) > KEY_BOARD_SHOW_HEIGHT) {
            if (diffHeight > 0) {
                if (this.handleImeInterface != null) {
                    this.handleImeInterface.onDismissKeyboard();
                }
            } else if (this.handleImeInterface != null) {
                this.handleImeInterface.onShowKeyboard();
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
