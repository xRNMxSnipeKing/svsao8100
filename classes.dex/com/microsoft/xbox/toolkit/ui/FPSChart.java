package com.microsoft.xbox.toolkit.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import com.microsoft.xbox.avatar.model.AvatarEditorModel;

public class FPSChart extends SurfaceView implements Callback {
    private static final int HEIGHT = 60;
    private static final int SAFE_FPS = 30;
    private static final int WIDTH = 120;
    private static int[] fpsFrames = new int[WIDTH];
    private static int fpsFramesIndex = 0;
    private Paint paint;
    private SurfaceHolder surfaceHolder;
    private Thread thread;
    private boolean threadRunning;

    static {
        for (int i = 0; i < fpsFrames.length; i++) {
            fpsFrames[i] = 0;
        }
    }

    public static void addFPS(int fps) {
        fpsFramesIndex = (fpsFramesIndex + 1) % fpsFrames.length;
        fpsFrames[fpsFramesIndex] = fps;
    }

    public FPSChart(Context context) {
        super(context);
        this.surfaceHolder = null;
        this.paint = null;
        this.thread = null;
        this.threadRunning = true;
        this.surfaceHolder = getHolder();
        this.surfaceHolder.addCallback(this);
        this.paint = new Paint();
        this.thread = new Thread(new Runnable() {
            public void run() {
                while (FPSChart.this.threadRunning) {
                    if (FPSChart.this.surfaceHolder.getSurface().isValid()) {
                        Canvas c = FPSChart.this.surfaceHolder.lockCanvas();
                        FPSChart.this.drawCanvas(c);
                        FPSChart.this.surfaceHolder.unlockCanvasAndPost(c);
                        try {
                            Thread.sleep(33);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            }
        });
        this.thread.setPriority(1);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(WIDTH, 60);
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        this.threadRunning = false;
        try {
            this.thread.join();
        } catch (InterruptedException e) {
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        this.threadRunning = true;
        this.thread.start();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    public void onDraw(Canvas canvas) {
        Canvas c = this.surfaceHolder.lockCanvas(null);
        drawCanvas(c);
        this.surfaceHolder.unlockCanvasAndPost(c);
    }

    private void drawCanvas(Canvas c) {
        this.paint.setStyle(Style.FILL);
        this.paint.setColor(AvatarEditorModel.AVATAREDIT_OPTION_MASK_COLORS_MALE);
        c.drawRect(0.0f, 0.0f, 119.0f, 59.0f, this.paint);
        this.paint.setColor(-65536);
        c.drawRect(0.0f, 29.0f, 119.0f, 59.0f, this.paint);
        int frameIndex = fpsFramesIndex;
        this.paint.setColor(-16711936);
        for (int i = 0; i < fpsFrames.length; i++) {
            c.drawRect((float) i, (float) Math.max(59 - fpsFrames[frameIndex], 0), (float) (i + 1), 100.0f, this.paint);
            frameIndex++;
            if (frameIndex == fpsFrames.length) {
                frameIndex = 0;
            }
        }
    }
}
