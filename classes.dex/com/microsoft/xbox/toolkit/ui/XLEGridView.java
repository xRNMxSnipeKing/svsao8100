package com.microsoft.xbox.toolkit.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View.MeasureSpec;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.LayoutAnimationController;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import com.microsoft.xbox.avatar.model.AvatarEditorModel;
import com.microsoft.xbox.toolkit.BackgroundThreadWaitor;
import com.microsoft.xbox.toolkit.BackgroundThreadWaitor.WaitType;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLELog;

public class XLEGridView extends GridView {
    private static final int LAYOUT_BLOCK_TIMEOUT_MS = 500;
    private static final int SCROLL_BLOCK_TIMEOUT_MS = 30000;
    private boolean blocking = false;
    private boolean firstTouchAfterNonBlocking = false;
    private AnimationListener listViewAnimationListener = new AnimationListener() {
        public void onAnimationStart(Animation animation) {
            XLEGridView.this.onLayoutAnimationStart();
        }

        public void onAnimationRepeat(Animation animation) {
        }

        public void onAnimationEnd(Animation animation) {
            XLEGridView.this.onLayoutAnimationEnd();
        }
    };

    public XLEGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setSoundEffectsEnabled(false);
        setSelector(17170445);
        setOnScrollListener(new OnScrollListener() {
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == 0) {
                    XLELog.Diagnostic("MVHFPS", "Scroll idle");
                    BackgroundThreadWaitor.getInstance().clearBlocking(WaitType.ListScroll);
                    return;
                }
                XLELog.Diagnostic("MVHFPS", "Scrolling");
                BackgroundThreadWaitor.getInstance().setBlocking(WaitType.ListScroll, XLEGridView.SCROLL_BLOCK_TIMEOUT_MS);
            }

            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
    }

    public boolean getBlocking() {
        return this.blocking;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        XLEAssert.assertTrue(MeasureSpec.getMode(heightMeasureSpec) != AvatarEditorModel.AVATAREDIT_OPTION_COLOR_FACIAL_HAIR);
    }

    public void setBlocking(boolean value) {
        boolean z = this.blocking && !value;
        this.firstTouchAfterNonBlocking = z;
        if (this.blocking != value) {
            this.blocking = value;
            if (this.blocking) {
                BackgroundThreadWaitor.getInstance().setBlocking(WaitType.ListLayout, 500);
            } else {
                BackgroundThreadWaitor.getInstance().clearBlocking(WaitType.ListLayout);
            }
        }
    }

    public void setLayoutAnimation(LayoutAnimationController controller) {
        super.setLayoutAnimation(controller);
        setLayoutAnimationListener(this.listViewAnimationListener);
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (this.blocking) {
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    public boolean onTouchEvent(MotionEvent ev) {
        if (this.blocking) {
            return true;
        }
        if (this.firstTouchAfterNonBlocking) {
            if ((ev.getAction() & 255) == 2) {
                ev.setAction(0);
            }
            this.firstTouchAfterNonBlocking = false;
        }
        return super.onTouchEvent(ev);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        super.setOnItemClickListener(TouchUtil.createOnItemClickListener(listener));
    }

    private void onLayoutAnimationStart() {
        XLELog.Diagnostic("XLEGridView", "LayoutAnimation start");
        setLayerType(2, null);
        setBlocking(true);
    }

    private void onLayoutAnimationEnd() {
        XLELog.Diagnostic("XLEGridView", "LayoutAnimation end");
        setBlocking(false);
        setLayoutAnimation(null);
        setLayerType(0, null);
    }
}
