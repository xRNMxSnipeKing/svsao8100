package com.microsoft.xbox.xle.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import com.microsoft.smartglass.R;
import com.microsoft.xbox.toolkit.XLEGestureOverlayProcessor;
import com.microsoft.xbox.toolkit.XLEGestureOverlayProcessor.GestureType;
import com.microsoft.xbox.toolkit.XLEGestureOverlayProcessor.OnGestureRunnable;
import com.microsoft.xbox.toolkit.system.SystemUtil;
import com.microsoft.xbox.toolkit.ui.ButtonStateHandler.ButtonStateHandlerRunnable;
import com.microsoft.xbox.toolkit.ui.XLEButton;

public class SmartGlassController extends RelativeLayout {
    private static final int DEAD_ZONE_RADIUS = SystemUtil.DIPtoPixels(35.0f);
    private XLEButton closeButton = null;
    private XLEButton dpadB = null;
    private View dpadBGradient = null;
    private XLEButton dpadGuide = null;
    private XLEButton dpadX = null;
    private View dpadXGradient = null;
    private XLEButton dpadY = null;
    private View dpadYGradient = null;
    private FrameLayout gestureOverlay = null;
    private XLEGestureOverlayProcessor gestureProcessor = new XLEGestureOverlayProcessor(DEAD_ZONE_RADIUS);
    private Runnable onCloseButton = null;
    private Runnable onDpadB = null;
    private Runnable onDpadGuide = null;
    private Runnable onDpadX = null;
    private Runnable onDpadY = null;
    private OnGestureRunnable onGesture = null;
    private Runnable onTouchDown = null;

    public SmartGlassController(Context context, AttributeSet attrs) {
        super(context, attrs);
        ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R.layout.smart_glass_controller, this, true);
        this.dpadGuide = (XLEButton) findViewById(R.id.dpadGuide);
        if (this.dpadGuide != null) {
            this.dpadGuide.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (SmartGlassController.this.onDpadGuide != null) {
                        SmartGlassController.this.onDpadGuide.run();
                    }
                }
            });
        }
        this.dpadB = (XLEButton) findViewById(R.id.dpadB);
        this.dpadB.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (SmartGlassController.this.onDpadB != null) {
                    SmartGlassController.this.onDpadB.run();
                }
            }
        });
        this.dpadX = (XLEButton) findViewById(R.id.dpadX);
        this.dpadX.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (SmartGlassController.this.onDpadX != null) {
                    SmartGlassController.this.onDpadX.run();
                }
            }
        });
        this.dpadY = (XLEButton) findViewById(R.id.dpadY);
        this.dpadY.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (SmartGlassController.this.onDpadY != null) {
                    SmartGlassController.this.onDpadY.run();
                }
            }
        });
        this.closeButton = (XLEButton) findViewById(R.id.close_button);
        if (this.closeButton != null) {
            this.closeButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (SmartGlassController.this.onCloseButton != null) {
                        SmartGlassController.this.onCloseButton.run();
                    }
                }
            });
        }
        this.dpadBGradient = findViewById(R.id.dpadBGradient);
        this.dpadXGradient = findViewById(R.id.dpadXGradient);
        this.dpadYGradient = findViewById(R.id.dpadYGradient);
        this.dpadB.setPressedStateRunnable(new ButtonStateHandlerRunnable() {
            public void onPressStateChanged(boolean state) {
                SmartGlassController.this.dpadBGradient.setVisibility(state ? 0 : 8);
            }
        });
        this.dpadX.setPressedStateRunnable(new ButtonStateHandlerRunnable() {
            public void onPressStateChanged(boolean state) {
                SmartGlassController.this.dpadXGradient.setVisibility(state ? 0 : 8);
            }
        });
        this.dpadY.setPressedStateRunnable(new ButtonStateHandlerRunnable() {
            public void onPressStateChanged(boolean state) {
                SmartGlassController.this.dpadYGradient.setVisibility(state ? 0 : 8);
            }
        });
        this.gestureOverlay = (FrameLayout) findViewById(R.id.smart_glass_gesture_overlay_view);
        this.gestureOverlay.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getActionMasked() == 0 && SmartGlassController.this.onTouchDown != null) {
                    SmartGlassController.this.onTouchDown.run();
                }
                return SmartGlassController.this.gestureProcessor.onTouch(event);
            }
        });
        this.gestureProcessor.setOnSendGestureEvent(new OnGestureRunnable() {
            public void onGesture(GestureType type) {
                if (SmartGlassController.this.onGesture != null) {
                    SmartGlassController.this.onGesture.onGesture(type);
                }
            }
        });
    }

    public void setOnDpadGuide(Runnable r) {
        this.onDpadGuide = r;
    }

    public void setOnDpadB(Runnable r) {
        this.onDpadB = r;
    }

    public void setOnDpadX(Runnable r) {
        this.onDpadX = r;
    }

    public void setOnDpadY(Runnable r) {
        this.onDpadY = r;
    }

    public void setOnCloseButton(Runnable r) {
        this.onCloseButton = r;
    }

    public void setOnSendGestureEvent(OnGestureRunnable r) {
        this.onGesture = r;
    }

    public void setOnTouchDown(Runnable r) {
        this.onTouchDown = r;
    }

    public void onPause() {
        if (this.gestureProcessor != null) {
            this.gestureProcessor.cancel();
        }
    }
}
