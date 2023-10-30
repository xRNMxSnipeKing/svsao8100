package com.microsoft.xbox.toolkit.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.LayoutAnimationController;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.microsoft.xbox.avatar.model.AvatarEditorModel;
import com.microsoft.xbox.toolkit.BackgroundThreadWaitor;
import com.microsoft.xbox.toolkit.BackgroundThreadWaitor.WaitType;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLELog;
import java.util.LinkedList;

public class XLEListView extends ListView {
    private static final int LAYOUT_BLOCK_TIMEOUT_MS = 500;
    private static final int SCROLL_BLOCK_TIMEOUT_MS = 30000;
    private boolean blocking = false;
    private LayoutAnimationController desiredLayoutAnimation = null;
    private boolean firstTouchAfterNonBlocking = false;
    private AnimationListener listViewAnimationListener = new AnimationListener() {
        public void onAnimationStart(Animation animation) {
            XLEListView.this.onLayoutAnimationStart();
        }

        public void onAnimationRepeat(Animation animation) {
        }

        public void onAnimationEnd(Animation animation) {
            XLEListView.this.onLayoutAnimationEnd();
        }
    };
    private boolean needLayoutAnimation = false;
    private boolean pendingNotifyDataSetChanged = false;

    public XLEListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setSoundEffectsEnabled(false);
        setVerticalFadingEdgeEnabled(false);
        setSelector(17170445);
        OverScrollUtil.removeOverScrollFooter(this);
        setOnScrollListener(new OnScrollListener() {
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == 0) {
                    XLELog.Diagnostic("MVHFPS", "Scroll idle");
                    BackgroundThreadWaitor.getInstance().clearBlocking(WaitType.ListScroll);
                    return;
                }
                XLELog.Diagnostic("MVHFPS", "Scrolling");
                BackgroundThreadWaitor.getInstance().setBlocking(WaitType.ListScroll, XLEListView.SCROLL_BLOCK_TIMEOUT_MS);
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

    public LinkedList<View> getAllVisibleViews() {
        LinkedList<View> rv = new LinkedList();
        int startIndex = getFirstVisiblePosition();
        int endIndex = getLastVisiblePosition();
        for (int i = startIndex; i <= endIndex; i++) {
            rv.add(getChildAt(i));
        }
        return rv;
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

    public void onDataUpdated() {
        super.setLayoutAnimation(null);
        XLELog.Diagnostic("XLEListView", "onDataUpdate");
        this.needLayoutAnimation = true;
        startLayoutAnimationIfReady();
    }

    public void notifyDataSetChanged() {
        if (this.blocking) {
            this.pendingNotifyDataSetChanged = true;
            return;
        }
        ListAdapter adapter = getAdapter();
        if (adapter instanceof ArrayAdapter) {
            ((ArrayAdapter) adapter).notifyDataSetChanged();
        }
        XLELog.Diagnostic("XLEListView", "notifyDataSetChanged");
        startLayoutAnimationIfReady();
    }

    public void startLayoutAnimationIfReady() {
        boolean z = false;
        if (this.needLayoutAnimation && this.desiredLayoutAnimation != null && this.listViewAnimationListener != null) {
            this.needLayoutAnimation = false;
            String str = "We should try to set layout animation only once";
            if (getLayoutAnimation() == null) {
                z = true;
            }
            XLEAssert.assertTrue(str, z);
            super.setLayoutAnimation(this.desiredLayoutAnimation);
            setLayoutAnimationListener(this.listViewAnimationListener);
        }
    }

    public void setLayoutAnimation(LayoutAnimationController controller) {
        XLELog.Diagnostic("XLEListView", "setLayoutAnimation");
        super.setLayoutAnimation(null);
        this.desiredLayoutAnimation = controller;
        startLayoutAnimationIfReady();
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

    public void setOnItemClickListenerWithoutSound(OnItemClickListener listener) {
        super.setOnItemClickListener(listener);
    }

    private void onLayoutAnimationStart() {
        XLELog.Diagnostic("XLEListView", "LayoutAnimation start");
        setLayerType(2, null);
        setBlocking(true);
    }

    private void onLayoutAnimationEnd() {
        XLELog.Diagnostic("XLEListView", "LayoutAnimation end");
        setBlocking(false);
        setLayoutAnimation(null);
        if (this.pendingNotifyDataSetChanged) {
            notifyDataSetChanged();
            this.pendingNotifyDataSetChanged = false;
        }
        setLayerType(0, null);
    }
}
