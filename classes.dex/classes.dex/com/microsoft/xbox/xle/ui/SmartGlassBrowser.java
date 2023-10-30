package com.microsoft.xbox.xle.ui;

import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaType;
import com.microsoft.xbox.service.model.smartglass.ScrollPoint;
import com.microsoft.xbox.service.model.smartglass.TouchFrame;
import com.microsoft.xbox.service.model.smartglass.TouchPoint;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.system.SystemUtil;
import com.microsoft.xbox.toolkit.ui.CustomTypefaceEditText;
import com.microsoft.xbox.toolkit.ui.EditTextContainer;
import com.microsoft.xbox.toolkit.ui.XLEButton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

public class SmartGlassBrowser extends RelativeLayout {
    private static final float BROWSER_SCROLL_A = 0.8f;
    private static final float BROWSER_SCROLL_B = 6.0E-4f;
    private static final float BROWSER_SCROLL_C = 477.0f;
    private static final float BROWSER_SCROLL_D = 1.5f;
    private static final int BROWSER_SCROLL_UPDATE_MS = 64;
    private static final float MAZAA_SCREEN_INCHES = 3.25f;
    private int SCROLL_ONLY_THRESHOLD_PIXELS = SystemUtil.DIPtoPixels(10.0f);
    private XLEButton backButton;
    private Runnable backButtonRunnable = null;
    private View browserClear;
    private View browserRefresh;
    private View browserStop;
    private Runnable browserStopRefreshRunnable = null;
    private XLEButton closeButton = null;
    private CustomTypefaceEditText editView;
    private EditTextContainer editViewContainer;
    private boolean isScrolling = false;
    private boolean isTouching = false;
    private boolean isloading = false;
    private Runnable onCloseButton = null;
    private float scrollGestureLastX = 0.0f;
    private float scrollGestureLastY = 0.0f;
    private boolean scrollOnlyGesture = false;
    private View scrollView;
    private Timer timer = new Timer();
    private TouchEventListener touchListener = null;
    private long touchMsPerFrame = 0;
    private HashMap<Integer, BrowserTouchPoint> touchPoints = new HashMap();
    private SmartGlassBrowserTouchPanel touchView;
    private Runnable urlDoneRunnable = null;
    private View webhubButton;
    private Runnable webhubButtonRunnable = null;

    private static class BrowserTouchPoint {
        public float lastX;
        public float lastY;

        private BrowserTouchPoint() {
        }
    }

    public interface TouchEventListener {
        void onScrollEvent(ScrollPoint scrollPoint);

        void onTouchEvent(TouchFrame touchFrame);
    }

    public SmartGlassBrowser(Context context, AttributeSet attrs) {
        super(context, attrs);
        ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R.layout.smart_glass_browser, this, true);
        this.editView = (CustomTypefaceEditText) findViewById(R.id.smart_glass_browser_url);
        this.editViewContainer = (EditTextContainer) findViewById(R.id.smart_glass_browser_edit_view_container);
        this.backButton = (XLEButton) findViewById(R.id.smart_glass_browser_back);
        this.webhubButton = findViewById(R.id.smart_glass_browser_webhub);
        this.browserStop = findViewById(R.id.smart_glass_browser_stop);
        this.browserRefresh = findViewById(R.id.smart_glass_browser_refresh);
        this.browserClear = findViewById(R.id.smart_glass_browser_reset_button);
        this.touchView = (SmartGlassBrowserTouchPanel) findViewById(R.id.smart_glass_browser_touch_view);
        this.scrollView = findViewById(R.id.smart_glass_browser_scroll_view);
        this.closeButton = (XLEButton) findViewById(R.id.close_button);
        this.editView.setContainer(this.editViewContainer);
        this.editViewContainer.addChild(this.backButton);
        this.editViewContainer.addChild(this.browserStop);
        this.editViewContainer.addChild(this.browserRefresh);
        this.editViewContainer.addChild(this.browserClear);
        if (this.closeButton != null) {
            this.editViewContainer.addChild(this.closeButton);
            this.closeButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (SmartGlassBrowser.this.onCloseButton != null) {
                        SmartGlassBrowser.this.onCloseButton.run();
                    }
                }
            });
        }
        this.editView.setInputType(524305);
        this.editView.setSelectAllOnFocus(true);
        this.editView.setOnFocusChangeListener(new OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                SmartGlassBrowser.this.updateStopRefreshClearVisibility();
            }
        });
        this.editView.setOnEditorActionListener(new OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean doneButton;
                if (actionId == 6) {
                    doneButton = true;
                } else {
                    doneButton = false;
                }
                boolean enterButton = false;
                if (!(event == null || event.isCanceled())) {
                    enterButton = event.getAction() == 1 && event.getKeyCode() == 66;
                }
                if (doneButton || enterButton) {
                    if (SmartGlassBrowser.this.urlDoneRunnable != null) {
                        SmartGlassBrowser.this.urlDoneRunnable.run();
                    }
                    ThreadManager.UIThreadPost(new Runnable() {
                        public void run() {
                            SmartGlassBrowser.this.editViewContainer.unfocusText();
                        }
                    });
                }
                return false;
            }
        });
        this.backButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (SmartGlassBrowser.this.backButtonRunnable != null) {
                    SmartGlassBrowser.this.backButtonRunnable.run();
                }
            }
        });
        this.webhubButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (SmartGlassBrowser.this.webhubButtonRunnable != null) {
                    SmartGlassBrowser.this.webhubButtonRunnable.run();
                }
            }
        });
        this.browserStop.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (SmartGlassBrowser.this.browserStopRefreshRunnable != null) {
                    SmartGlassBrowser.this.browserStopRefreshRunnable.run();
                }
            }
        });
        this.browserRefresh.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (SmartGlassBrowser.this.browserStopRefreshRunnable != null) {
                    SmartGlassBrowser.this.browserStopRefreshRunnable.run();
                }
            }
        });
        this.browserClear.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (SmartGlassBrowser.this.editView != null) {
                    SmartGlassBrowser.this.editView.setText("");
                }
            }
        });
        this.touchView.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (SmartGlassBrowser.this.touchListener == null) {
                    return false;
                }
                ArrayList<TouchPoint> points = new ArrayList();
                int id;
                BrowserTouchPoint pt;
                switch (event.getActionMasked()) {
                    case 0:
                        id = event.getPointerId(0);
                        SmartGlassBrowser.this.touchPoints.clear();
                        pt = new BrowserTouchPoint();
                        pt.lastX = SmartGlassBrowser.this.generateRawX(event, 0, SmartGlassBrowser.this.touchView);
                        pt.lastY = SmartGlassBrowser.this.generateRawY(event, 0, SmartGlassBrowser.this.touchView);
                        SmartGlassBrowser.this.touchPoints.put(Integer.valueOf(id), pt);
                        points.add(SmartGlassBrowser.this.buildTouchPoint(pt.lastX, pt.lastY, id, 1));
                        SmartGlassBrowser.this.enableTouchMode();
                        break;
                    case 1:
                    case 3:
                        id = event.getPointerId(0);
                        if (SmartGlassBrowser.this.touchPoints.containsKey(Integer.valueOf(id))) {
                            points.add(SmartGlassBrowser.this.buildTouchPoint(SmartGlassBrowser.this.generateRawX(event, 0, SmartGlassBrowser.this.touchView), SmartGlassBrowser.this.generateRawY(event, 0, SmartGlassBrowser.this.touchView), id, 4));
                        }
                        SmartGlassBrowser.this.touchPoints.clear();
                        SmartGlassBrowser.this.isTouching = false;
                        SmartGlassBrowser.this.timer.cancel();
                        break;
                    case 2:
                        for (int i = 0; i < event.getPointerCount(); i++) {
                            id = event.getPointerId(i);
                            if (SmartGlassBrowser.this.touchPoints.containsKey(Integer.valueOf(id))) {
                                ((BrowserTouchPoint) SmartGlassBrowser.this.touchPoints.get(Integer.valueOf(id))).lastX = SmartGlassBrowser.this.generateRawX(event, i, SmartGlassBrowser.this.touchView);
                                ((BrowserTouchPoint) SmartGlassBrowser.this.touchPoints.get(Integer.valueOf(id))).lastY = SmartGlassBrowser.this.generateRawY(event, i, SmartGlassBrowser.this.touchView);
                            }
                        }
                        break;
                    case 5:
                        int downidx = (event.getAction() & 65280) >> 8;
                        int downid = event.getPointerId(downidx);
                        if (SmartGlassBrowser.this.touchPoints.size() == 1) {
                            BrowserTouchPoint down = new BrowserTouchPoint();
                            down.lastX = SmartGlassBrowser.this.generateRawX(event, downidx, SmartGlassBrowser.this.touchView);
                            down.lastY = SmartGlassBrowser.this.generateRawY(event, downidx, SmartGlassBrowser.this.touchView);
                            SmartGlassBrowser.this.touchPoints.put(Integer.valueOf(downid), down);
                            points.add(SmartGlassBrowser.this.buildTouchPoint(down.lastX, down.lastY, downid, 1));
                            int otherid = ((Integer) SmartGlassBrowser.this.touchPoints.keySet().iterator().next()).intValue();
                            BrowserTouchPoint other = (BrowserTouchPoint) SmartGlassBrowser.this.touchPoints.get(Integer.valueOf(otherid));
                            points.add(SmartGlassBrowser.this.buildTouchPoint(other.lastX, other.lastY, otherid, 2));
                            break;
                        }
                        break;
                    case 6:
                        if (SmartGlassBrowser.this.touchPoints.containsKey(Integer.valueOf(event.getPointerId((event.getAction() & 65280) >> 8))) && SmartGlassBrowser.this.touchPoints.size() == 2) {
                            for (Integer intValue : SmartGlassBrowser.this.touchPoints.keySet()) {
                                id = intValue.intValue();
                                pt = (BrowserTouchPoint) SmartGlassBrowser.this.touchPoints.get(Integer.valueOf(id));
                                points.add(SmartGlassBrowser.this.buildTouchPoint(pt.lastX, pt.lastY, id, 4));
                            }
                            SmartGlassBrowser.this.touchPoints.clear();
                            break;
                        }
                }
                if (points != null && points.size() > 0) {
                    TouchFrame frame = new TouchFrame();
                    frame.points = (TouchPoint[]) points.toArray(new TouchPoint[0]);
                    frame.timestamp = SystemClock.uptimeMillis();
                    SmartGlassBrowser.this.touchListener.onTouchEvent(frame);
                }
                return true;
            }
        });
        this.scrollView.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                boolean z = false;
                if (SmartGlassBrowser.this.touchListener == null) {
                    return false;
                }
                switch (event.getActionMasked()) {
                    case 0:
                        SmartGlassBrowser.this.scrollOnlyGesture = false;
                        SmartGlassBrowser.this.scrollGestureLastX = SmartGlassBrowser.this.generateRawX(event, 0, SmartGlassBrowser.this.scrollView);
                        SmartGlassBrowser.this.scrollGestureLastY = SmartGlassBrowser.this.generateRawY(event, 0, SmartGlassBrowser.this.scrollView);
                        int id = event.getPointerId(0);
                        SmartGlassBrowser.this.touchPoints.clear();
                        BrowserTouchPoint pt = new BrowserTouchPoint();
                        pt.lastX = SmartGlassBrowser.this.scrollGestureLastX;
                        pt.lastY = SmartGlassBrowser.this.scrollGestureLastY;
                        SmartGlassBrowser.this.touchPoints.put(Integer.valueOf(id), pt);
                        SmartGlassBrowser.this.enableScrollingMode();
                        return true;
                    case 1:
                        SmartGlassBrowser.this.updateScrollPts(event);
                        SmartGlassBrowser.this.updateScroll(true);
                        SmartGlassBrowser.this.cancelScrollingMode();
                        return true;
                    case 2:
                        SmartGlassBrowser.this.updateScrollPts(event);
                        return true;
                    case 3:
                        SmartGlassBrowser.this.cancelScrollingMode();
                        return true;
                    case 5:
                        if (SmartGlassBrowser.this.scrollOnlyGesture) {
                            return true;
                        }
                        int downidx = (event.getAction() & 65280) >> 8;
                        int downid = event.getPointerId(downidx);
                        if (SmartGlassBrowser.this.touchPoints.size() == 1) {
                            z = true;
                        }
                        XLEAssert.assertTrue(z);
                        BrowserTouchPoint down = new BrowserTouchPoint();
                        down.lastX = SmartGlassBrowser.this.generateRawX(event, downidx, SmartGlassBrowser.this.scrollView);
                        down.lastY = SmartGlassBrowser.this.generateRawY(event, downidx, SmartGlassBrowser.this.scrollView);
                        SmartGlassBrowser.this.touchPoints.put(Integer.valueOf(downid), down);
                        SmartGlassBrowser.this.cancelScrollingMode();
                        SmartGlassBrowser.this.migrateGestureToTouch();
                        return true;
                    default:
                        return true;
                }
            }
        });
        updateStopRefreshVisibility(false);
    }

    private float generateRawX(MotionEvent event, int index, View view) {
        int[] origin = new int[]{0, 0};
        view.getLocationOnScreen(origin);
        return event.getX(index) + ((float) origin[0]);
    }

    private float generateRawY(MotionEvent event, int index, View view) {
        int[] origin = new int[]{0, 0};
        view.getLocationOnScreen(origin);
        return event.getY(index) + ((float) origin[1]);
    }

    private TouchPoint buildTouchPoint(float x, float y, int id, int touchAction) {
        float f = 0.0f;
        TouchPoint pt = new TouchPoint();
        pt.xval = SystemUtil.getScreenWidth() == 0 ? 0.0f : x / ((float) SystemUtil.getScreenWidth());
        if (SystemUtil.getScreenHeight() != 0) {
            f = y / ((float) SystemUtil.getScreenHeight());
        }
        pt.yval = f;
        pt.id = id + EDSV2MediaType.MEDIATYPE_MOVIE;
        pt.action = touchAction;
        return pt;
    }

    private float acceleratedBrowserScrollMath(float dyinches) {
        if (Math.abs(dyinches) > 0.0f) {
            return (float) (((double) (BROWSER_SCROLL_A * Math.signum(dyinches))) * ((6.000000284984708E-4d * Math.pow((double) Math.abs(BROWSER_SCROLL_C * dyinches), 1.5d)) + ((double) Math.abs(dyinches))));
        }
        return 0.0f;
    }

    private ScrollPoint buildScrollPoint(float x, float y) {
        ScrollPoint pt = new ScrollPoint();
        float outyin = acceleratedBrowserScrollMath((y - this.scrollGestureLastY) / SystemUtil.getYDPI());
        pt.dx = 0.0d;
        pt.dy = (double) ((outyin / SystemUtil.getScreenHeightInches()) * (MAZAA_SCREEN_INCHES / SystemUtil.getScreenHeightInches()));
        this.scrollGestureLastX = x;
        this.scrollGestureLastY = y;
        return pt;
    }

    public void setBackButtonRunnable(Runnable backButtonRunnable) {
        this.backButtonRunnable = backButtonRunnable;
    }

    public void setWebhubButtonRunnable(Runnable r) {
        this.webhubButtonRunnable = r;
    }

    public void setBrowserStopRefreshRunnable(Runnable browserStopRefreshRunnable) {
        this.browserStopRefreshRunnable = browserStopRefreshRunnable;
    }

    public void setUrlDoneRunnable(Runnable r) {
        this.urlDoneRunnable = r;
    }

    public void setPanelTouchListener(TouchEventListener listener) {
        this.touchListener = listener;
    }

    public void setOnCloseButton(Runnable r) {
        this.onCloseButton = r;
    }

    private void migrateGestureToTouch() {
        boolean z;
        if (this.touchListener != null) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        ArrayList<TouchPoint> points = new ArrayList();
        if (this.touchPoints.size() == 2) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        Iterator<Integer> idi = this.touchPoints.keySet().iterator();
        int id0 = ((Integer) idi.next()).intValue();
        int id1 = ((Integer) idi.next()).intValue();
        BrowserTouchPoint pt0 = (BrowserTouchPoint) this.touchPoints.get(Integer.valueOf(id0));
        BrowserTouchPoint pt1 = (BrowserTouchPoint) this.touchPoints.get(Integer.valueOf(id1));
        points.add(buildTouchPoint(pt0.lastX, pt0.lastY, id0, 1));
        TouchFrame frame = new TouchFrame();
        frame.points = (TouchPoint[]) points.toArray(new TouchPoint[0]);
        frame.timestamp = SystemClock.uptimeMillis();
        this.touchListener.onTouchEvent(frame);
        points.clear();
        points.add(buildTouchPoint(pt0.lastX, pt0.lastY, id0, 2));
        points.add(buildTouchPoint(pt1.lastX, pt1.lastY, id1, 1));
        frame = new TouchFrame();
        frame.points = (TouchPoint[]) points.toArray(new TouchPoint[0]);
        frame.timestamp = SystemClock.uptimeMillis();
        this.touchListener.onTouchEvent(frame);
        this.touchView.setIntercept(true);
        enableTouchMode();
    }

    private void enableScrollingMode() {
        XLEAssert.assertTrue(!this.isTouching);
        this.timer.cancel();
        TimerTask scrolltimertask = new TimerTask() {
            public void run() {
                ThreadManager.UIThreadPost(new Runnable() {
                    public void run() {
                        SmartGlassBrowser.this.updateScroll(false);
                    }
                });
            }
        };
        this.timer = new Timer();
        this.timer.schedule(scrolltimertask, 0, 64);
        this.isScrolling = true;
    }

    private void cancelScrollingMode() {
        if (this.isScrolling) {
            this.isScrolling = false;
            this.timer.cancel();
        }
    }

    private void enableTouchMode() {
        XLEAssert.assertTrue(!this.isScrolling);
        this.isTouching = true;
        this.timer.cancel();
        this.timer = new Timer();
        this.timer.schedule(new TimerTask() {
            public void run() {
                ThreadManager.UIThreadPost(new Runnable() {
                    public void run() {
                        if (SmartGlassBrowser.this.isTouching) {
                            ArrayList<TouchPoint> points = new ArrayList();
                            for (Integer intValue : SmartGlassBrowser.this.touchPoints.keySet()) {
                                int key = intValue.intValue();
                                points.add(SmartGlassBrowser.this.buildTouchPoint(((BrowserTouchPoint) SmartGlassBrowser.this.touchPoints.get(Integer.valueOf(key))).lastX, ((BrowserTouchPoint) SmartGlassBrowser.this.touchPoints.get(Integer.valueOf(key))).lastY, key, 2));
                            }
                            if (points != null && points.size() > 0) {
                                TouchFrame frame = new TouchFrame();
                                frame.points = (TouchPoint[]) points.toArray(new TouchPoint[0]);
                                frame.timestamp = SystemClock.uptimeMillis();
                                if (SmartGlassBrowser.this.touchListener != null) {
                                    SmartGlassBrowser.this.touchListener.onTouchEvent(frame);
                                }
                            }
                        }
                    }
                });
            }
        }, 0, this.touchMsPerFrame);
    }

    public void updateStopRefreshVisibility(boolean loading) {
        this.isloading = loading;
        updateStopRefreshClearVisibility();
    }

    public void updateStopRefreshClearVisibility() {
        int i = 0;
        if (this.editView == null || !this.editView.hasFocus()) {
            int i2;
            View view = this.browserStop;
            if (this.isloading) {
                i2 = 0;
            } else {
                i2 = 8;
            }
            view.setVisibility(i2);
            View view2 = this.browserRefresh;
            if (this.isloading) {
                i = 8;
            }
            view2.setVisibility(i);
            this.browserClear.setVisibility(8);
            return;
        }
        this.browserStop.setVisibility(8);
        this.browserRefresh.setVisibility(8);
        this.browserClear.setVisibility(0);
    }

    public void setUrl(String newurl) {
        this.editView.setText(newurl);
    }

    public String getUrl() {
        return this.editView.getText().toString();
    }

    public void setTouchMsPerFrame(int ms) {
        this.touchMsPerFrame = (long) ms;
    }

    private void updateScrollPts(MotionEvent event) {
        for (int i = 0; i < event.getPointerCount(); i++) {
            int id = event.getPointerId(i);
            if (this.touchPoints.containsKey(Integer.valueOf(id))) {
                ((BrowserTouchPoint) this.touchPoints.get(Integer.valueOf(id))).lastX = generateRawX(event, i, this.scrollView);
                ((BrowserTouchPoint) this.touchPoints.get(Integer.valueOf(id))).lastY = generateRawY(event, i, this.scrollView);
            }
        }
    }

    private void updateScroll(boolean forceUpdate) {
        if (this.isScrolling) {
            ScrollPoint point = null;
            XLEAssert.assertTrue(this.touchPoints.size() == 1);
            for (Integer intValue : this.touchPoints.keySet()) {
                BrowserTouchPoint pt = (BrowserTouchPoint) this.touchPoints.get(Integer.valueOf(intValue.intValue()));
                if (this.scrollOnlyGesture || forceUpdate) {
                    point = buildScrollPoint(pt.lastX, pt.lastY);
                } else {
                    float dx = pt.lastX - this.scrollGestureLastX;
                    float dy = pt.lastY - this.scrollGestureLastY;
                    if ((dx * dx) + (dy * dy) > ((float) (this.SCROLL_ONLY_THRESHOLD_PIXELS * this.SCROLL_ONLY_THRESHOLD_PIXELS))) {
                        this.scrollOnlyGesture = true;
                    }
                }
            }
            if (point == null) {
                return;
            }
            if ((Math.abs(point.dx) > 0.0d || Math.abs(point.dy) > 0.0d) && this.touchListener != null) {
                this.touchListener.onScrollEvent(point);
            }
        }
    }
}
