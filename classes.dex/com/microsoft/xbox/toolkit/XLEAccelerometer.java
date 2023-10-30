package com.microsoft.xbox.toolkit;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.SystemClock;

public class XLEAccelerometer implements SensorEventListener {
    private static final float GRAVITY_BLEND_ALPHA = 0.9f;
    private static final int SHAKE_MS = 500;
    private static final float X_ACCEL_EPSILON = 4.0f;
    private float[] accel = new float[]{0.0f, 0.0f, 0.0f};
    private Sensor accelSensor;
    private float[] gravity = new float[]{0.0f, 0.0f, 0.0f};
    private long lastMs = 0;
    private int lastSignX = 0;
    private SensorManager sensorManager;
    private int shakeCount = 0;
    private Runnable shakeUpdatedRunnable = null;

    public XLEAccelerometer(XboxApplication application) {
        this.sensorManager = (SensorManager) application.getSystemService("sensor");
        this.accelSensor = this.sensorManager.getDefaultSensor(1);
    }

    public void onPause() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        this.sensorManager.unregisterListener(this);
    }

    public void onResume() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        this.sensorManager.registerListener(this, this.accelSensor, 2);
    }

    public void setShakeUpdatedRunnable(Runnable r) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        this.shakeUpdatedRunnable = r;
    }

    public int getShakeCount() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        return this.shakeCount;
    }

    public void clearShakes() {
        boolean z;
        if (Thread.currentThread() == ThreadManager.UIThread) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        this.shakeCount = 0;
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
        boolean happenedInTime = false;
        if (event != null && event.sensor != null && event.values != null) {
            switch (event.sensor.getType()) {
                case 1:
                    int signX;
                    for (int i = 0; i < this.gravity.length; i++) {
                        this.gravity[i] = (this.gravity[i] * GRAVITY_BLEND_ALPHA) + (event.values[i] * 0.100000024f);
                        this.accel[i] = event.values[i] - this.gravity[i];
                    }
                    float xAcc = this.accel[0];
                    if (Math.abs(xAcc) < X_ACCEL_EPSILON) {
                        signX = 0;
                    } else {
                        signX = (int) Math.signum(xAcc);
                    }
                    if (signX != 0 && signX != this.lastSignX) {
                        if (SystemClock.uptimeMillis() - this.lastMs < 500) {
                            happenedInTime = true;
                        }
                        if (happenedInTime) {
                            this.shakeCount++;
                            callShakeUpdatedRunnable();
                        } else {
                            this.shakeCount = 1;
                        }
                        this.lastMs = SystemClock.uptimeMillis();
                        this.lastSignX = signX;
                        return;
                    }
                    return;
                default:
                    return;
            }
        }
    }

    private void callShakeUpdatedRunnable() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        if (this.shakeUpdatedRunnable != null) {
            this.shakeUpdatedRunnable.run();
        }
    }
}
