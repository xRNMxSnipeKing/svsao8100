package com.microsoft.xbox.toolkit.ui.pivot;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import com.microsoft.xbox.toolkit.BackgroundThreadWaitor;
import com.microsoft.xbox.toolkit.BackgroundThreadWaitor.WaitType;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XboxApplication;
import com.microsoft.xbox.toolkit.anim.XLEAnimationPackage;
import com.microsoft.xbox.toolkit.system.SystemUtil;
import com.microsoft.xbox.toolkit.ui.ScreenLayout;
import java.lang.ref.WeakReference;

public class Pivot extends RelativeLayout {
    private static final int ANIMATION_MS = 150;
    private static final int DISTANCE_TO_MAKE_DIRECTION_DECISION = SystemUtil.DIPtoPixels(5.0f);
    private static final float MIN_PANE_DISPLACEMENT_TO_ROTATE = 0.25f;
    private static final float MIN_VEL_TO_FLICK = 500.0f;
    private static final int NO_TOUCH_ID = -1;
    private static final int TOUCH_BLOCK_TIMEOUT_MS = 30000;
    private PivotBodyAnimation animIn = null;
    private boolean animating = false;
    private PivotBody body = null;
    private int currentPaneIndex = 0;
    private int currentTouchId = -1;
    private int flickDebt = 0;
    private PivotFlickState flickState = PivotFlickState.PIVOT_FLICK_NONE;
    private boolean interceptingDecision = false;
    private Runnable onCurrentPivotPaneChanged = null;
    private long prevTime = 0;
    private float prevX = 0.0f;
    private float prevY = 0.0f;
    private boolean scrolling = false;
    private Runnable scrollingRunner = null;
    float touchX0 = -1.0f;
    float touchY0 = -1.0f;

    private enum PivotFlickState {
        PIVOT_FLICK_NONE,
        PIVOT_FLICK_LEFT,
        PIVOT_FLICK_RIGHT
    }

    public Pivot(Context context) {
        super(context);
        throw new UnsupportedOperationException();
    }

    public Pivot(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void initialize() {
        this.body = new PivotBody(getContext());
        ScreenLayout[] panes = getInitialPanes();
        removeAllViews();
        this.body.initialize(panes);
        LayoutParams bodyParams = new LayoutParams(-2, -1);
        bodyParams.addRule(10);
        bodyParams.addRule(12);
        addView(this.body, bodyParams);
        setScrolling(false);
    }

    protected ScreenLayout[] getInitialPanes() {
        ScreenLayout[] panes = new ScreenLayout[getChildCount()];
        for (int i = 0; i < getChildCount(); i++) {
            panes[i] = (ScreenLayout) getChildAt(i);
        }
        return panes;
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        initialize();
    }

    protected void addPivotPane(ScreenLayout screen, int index) {
        this.body.addPivotPane(screen, index);
    }

    protected ScreenLayout removePivotPane(int index) {
        return this.body.removePivotPane(index);
    }

    private boolean handleTouch(MotionEvent ev) {
        float dt = (float) (ev.getEventTime() - this.prevTime);
        this.prevTime = ev.getEventTime();
        int action = ev.getAction();
        switch (action & 255) {
            case 1:
            case 3:
                this.currentTouchId = -1;
                onTouchRelease();
                break;
            case 2:
                onTouchMove(ev, ev.findPointerIndex(this.currentTouchId), dt);
                break;
            case 6:
                int index = (65280 & action) >> 8;
                if (ev.getPointerId(index) == this.currentTouchId) {
                    this.currentTouchId = -1;
                    onTouchMove(ev, index, dt);
                    onTouchRelease();
                    break;
                }
                break;
        }
        return true;
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercepting = false;
        this.currentTouchId = ev.getPointerId(0);
        int index = ev.findPointerIndex(this.currentTouchId);
        float x = ev.getX(index);
        float y = ev.getY(index);
        if (ev.getAction() == 0) {
            this.touchX0 = x;
            this.touchY0 = y;
            this.interceptingDecision = false;
            onTouchDown(1.0f);
        }
        if (ev.getAction() == 2 || ev.getAction() == 0) {
            this.prevX = x;
            this.prevY = y;
        }
        if (!this.interceptingDecision) {
            float totalX = x - this.touchX0;
            float totalY = y - this.touchY0;
            if (((int) ((totalX * totalX) + (totalY * totalY))) > DISTANCE_TO_MAKE_DIRECTION_DECISION * DISTANCE_TO_MAKE_DIRECTION_DECISION) {
                if (Math.abs(totalY) < Math.abs(totalX)) {
                    intercepting = true;
                } else {
                    intercepting = false;
                }
                this.interceptingDecision = true;
            }
        }
        return intercepting;
    }

    public boolean onTouchEvent(MotionEvent ev) {
        if (this.body.size() > 1) {
            return handleTouch(ev);
        }
        return super.onTouchEvent(ev);
    }

    private void onTouchDown(float dt) {
        updateFlickState(0.0f, dt);
    }

    private void onTouchMove(MotionEvent ev, int index, float dt) {
        if (index != -1) {
            float x = ev.getX(index);
            float y = ev.getY(index);
            float dx = x - this.prevX;
            pivotTranslate(-dx, y - this.prevY);
            updateFlickState(dx, dt);
            this.prevX = x;
            this.prevY = y;
        }
    }

    private void onTouchRelease() {
        if (!this.animating) {
            switch (this.flickState) {
                case PIVOT_FLICK_LEFT:
                    this.flickDebt = -1;
                    break;
                case PIVOT_FLICK_RIGHT:
                    this.flickDebt = 1;
                    break;
                case PIVOT_FLICK_NONE:
                    if (((float) getPanningDelta()) >= ((float) XboxApplication.MainActivity.getScreenWidth()) * -0.25f) {
                        if (((float) getPanningDelta()) > ((float) XboxApplication.MainActivity.getScreenWidth()) * MIN_PANE_DISPLACEMENT_TO_ROTATE) {
                            this.flickDebt = 1;
                            break;
                        }
                    }
                    this.flickDebt = -1;
                    break;
                    break;
            }
        }
        if ((this.flickDebt > 0 && this.currentPaneIndex >= this.body.size() - 1) || (this.flickDebt < 0 && this.currentPaneIndex <= 0)) {
            this.flickDebt = 0;
        }
        if (!this.animating) {
            animateFlickIfNecessary();
        }
        if (!this.animating) {
            animateCancelIfNecessary();
        }
    }

    public boolean getIsScrolling() {
        return this.scrolling;
    }

    public void setOnScrollingChangedRunnable(Runnable runner) {
        this.scrollingRunner = runner;
    }

    private void setScrolling(boolean enabled) {
        if (this.scrolling != enabled) {
            this.scrolling = enabled;
            if (this.scrolling) {
                BackgroundThreadWaitor.getInstance().setBlocking(WaitType.PivotScroll, TOUCH_BLOCK_TIMEOUT_MS);
            } else {
                BackgroundThreadWaitor.getInstance().clearBlocking(WaitType.PivotScroll);
            }
            if (this.scrollingRunner != null) {
                this.scrollingRunner.run();
            }
        }
    }

    private void animateFlickIfNecessary() {
        boolean z = true;
        XLEAssert.assertTrue(!this.animating);
        if (this.flickDebt != 0) {
            boolean z2;
            if (Math.abs(this.flickDebt) == 1) {
                z2 = true;
            } else {
                z2 = false;
            }
            XLEAssert.assertTrue(z2);
            this.currentPaneIndex += this.flickDebt;
            if (this.currentPaneIndex < 0 || this.currentPaneIndex >= this.body.size()) {
                z = false;
            }
            XLEAssert.assertTrue(z);
            animateOutInternal(this.flickDebt);
            this.flickDebt = 0;
        }
    }

    private void animateCancelIfNecessary() {
        XLEAssert.assertTrue(!this.animating);
        if (getPanningDelta() != 0) {
            animateCancelInternal();
        } else {
            onPivotAnimationEnd(false);
        }
    }

    private void updateFlickState(float dx, float dt) {
        if ((dx < 0.0f && getPanningDelta() > 0) || (dx > 0.0f && getPanningDelta() < 0)) {
            dx = 0.0f;
        }
        float pixelsPerSecond = dx / (dt / 1000.0f);
        this.flickState = PivotFlickState.PIVOT_FLICK_NONE;
        if (pixelsPerSecond > MIN_VEL_TO_FLICK) {
            this.flickState = PivotFlickState.PIVOT_FLICK_LEFT;
        } else if (pixelsPerSecond < -500.0f) {
            this.flickState = PivotFlickState.PIVOT_FLICK_RIGHT;
        }
    }

    private void pivotTranslate(float dx, float dy) {
        setScrolling(true);
        if (!this.animating) {
            this.body.setScrollX((int) (((float) this.body.getScrollX()) + dx));
            invalidate();
        }
    }

    private int getPanningDelta() {
        return this.body.getScrollX() - (XboxApplication.MainActivity.getScreenWidth() * this.currentPaneIndex);
    }

    private void onPivotAnimationEnd(boolean indexChanged) {
        this.animating = false;
        XLEAssert.assertTrue(this.flickDebt == 0);
        if (indexChanged) {
            setCurrentPivotPaneIndex(this.currentPaneIndex);
        }
        setScrolling(false);
    }

    private void animateOutInternal(int animateDirection) {
        XLEAssert.assertTrue(!this.animating);
        this.animating = true;
        this.animIn = new PivotBodyAnimation(this.body, (float) this.body.getScrollX(), (float) (this.currentPaneIndex * XboxApplication.MainActivity.getScreenWidth()));
        this.animIn.setInterpolator(new LinearInterpolator());
        this.animIn.setAnimationEndPostRunnable(new Runnable() {
            public void run() {
                Pivot.this.onPivotAnimationEnd(true);
            }
        });
        this.animIn.setDuration(150);
        this.body.startAnimation(this.animIn);
    }

    private void animateCancelInternal() {
        boolean z;
        boolean z2 = false;
        if (this.animating) {
            z = false;
        } else {
            z = true;
        }
        XLEAssert.assertTrue(z);
        this.animating = true;
        float startX = (float) this.body.getScrollX();
        float endX = (float) (this.currentPaneIndex * XboxApplication.MainActivity.getScreenWidth());
        if (this.animIn == null || this.animIn.hasEnded()) {
            z2 = true;
        }
        XLEAssert.assertTrue(z2);
        this.animIn = new PivotBodyAnimation(this.body, startX, endX);
        this.animIn.setDuration(150);
        this.animIn.setInterpolator(new LinearInterpolator());
        this.animIn.setAnimationEndPostRunnable(new Runnable() {
            public void run() {
                Pivot.this.onPivotAnimationEnd(false);
            }
        });
        this.body.startAnimation(this.animIn);
    }

    public void setCurrentPivotPaneIndex(int newPane) {
        this.currentPaneIndex = newPane;
        this.body.setActivePivotPane(this.currentPaneIndex);
        this.body.setScrollX(XboxApplication.MainActivity.getScreenWidth() * this.currentPaneIndex);
        if (this.onCurrentPivotPaneChanged != null) {
            this.onCurrentPivotPaneChanged.run();
        }
    }

    public int getCurrentPivotPaneIndex() {
        return this.currentPaneIndex;
    }

    public ScreenLayout getCurrentPivotPane() {
        if (this.body != null) {
            return this.body.getPivotPane(this.currentPaneIndex);
        }
        return null;
    }

    public int getTotalPaneCount() {
        return this.body.size();
    }

    public void setOnCurrentPivotPaneChangedRunnable(Runnable runnable) {
        this.onCurrentPivotPaneChanged = runnable;
    }

    public void onCreate() {
        this.body.onCreate();
    }

    public void onStart() {
        this.body.onStart();
    }

    public void onStop() {
        this.body.onStop();
    }

    public void onPause() {
        resetAnimation();
        this.body.onPause();
    }

    public void onResume() {
        resetAnimation();
        this.body.onResume();
    }

    public void onAnimateInStarted() {
        this.body.onAnimateInStarted();
    }

    public void onAnimateInCompleted() {
        final WeakReference<PivotBody> bodyWeakPtr = new WeakReference(this.body);
        BackgroundThreadWaitor.getInstance().postRunnableAfterReady(new Runnable() {
            public void run() {
                PivotBody bodyPtr = (PivotBody) bodyWeakPtr.get();
                if (bodyPtr != null) {
                    bodyPtr.onAnimateInCompleted();
                }
            }
        });
    }

    private void resetAnimation() {
        if (this.animIn != null) {
            this.animIn.cancel();
            this.animIn = null;
        }
        onPivotAnimationEnd(false);
    }

    public void onTombstone() {
        this.body.onTombstone();
    }

    public void onRehydrate() {
        this.body.onRehydrate();
    }

    public void onSetActive(int pivotPaneIndex) {
        this.currentPaneIndex = pivotPaneIndex;
        onSetActive();
    }

    public void onSetActive() {
        setCurrentPivotPaneIndex(this.currentPaneIndex);
    }

    public void onSetInactive() {
        this.body.setInactive();
    }

    public void onDestroy() {
        this.body.onDestroy();
    }

    public XLEAnimationPackage getAnimateIn(boolean goingBack) {
        return this.body.getPivotPane(this.currentPaneIndex).getAnimateIn(goingBack);
    }

    public XLEAnimationPackage getAnimateOut(boolean goingBack) {
        return this.body.getPivotPane(this.currentPaneIndex).getAnimateOut(goingBack);
    }

    public void adjustBottomMargin(int bottomMargin) {
        this.body.adjustBottomMargin(bottomMargin);
    }

    public void resetBottomMargin() {
        this.body.resetBottomMargin();
    }

    public int getIndexOfScreen(Class<? extends ScreenLayout> screenClass) {
        return this.body.getIndexOfScreen(screenClass);
    }

    public void switchToPivotPane(int newIndex) {
        if (!this.animating && newIndex >= 0 && newIndex != this.currentPaneIndex) {
            this.currentPaneIndex = newIndex;
            this.body.setScrollX((int) ((float) (this.currentPaneIndex * XboxApplication.MainActivity.getScreenWidth())));
            setCurrentPivotPaneIndex(this.currentPaneIndex);
        }
    }
}
