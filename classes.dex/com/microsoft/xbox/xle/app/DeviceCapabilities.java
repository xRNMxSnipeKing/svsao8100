package com.microsoft.xbox.xle.app;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Vibrator;
import android.provider.Settings.System;
import com.microsoft.xbox.smartglass.canvas.CanvasComponents;
import com.microsoft.xbox.smartglass.canvas.CanvasEvent;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLELog;
import java.util.EnumSet;

public class DeviceCapabilities {
    private static DeviceCapabilities instance = new DeviceCapabilities();
    private EnumSet<CanvasComponents> availableCapabilities = EnumSet.noneOf(CanvasComponents.class);
    private SensorListener sensorListener = new SensorListener();

    private class SensorListener implements SensorEventListener {
        private SensorListener() {
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        public void onSensorChanged(SensorEvent event) {
        }
    }

    private DeviceCapabilities() {
    }

    public static DeviceCapabilities getInstance() {
        return instance;
    }

    public void onResume() {
        updateDeviceCapabilities();
    }

    public boolean checkDeviceRequirements(int requiredCapabilities) {
        boolean success = true;
        if (JavaUtil.containsFlag(requiredCapabilities, CanvasComponents.Accelerometer.getValue()) && !this.availableCapabilities.contains(CanvasComponents.Accelerometer)) {
            XLELog.Diagnostic("DeviceCapabilities", "Requires accelerometer but device doesn't support it");
            success = false;
        }
        if (JavaUtil.containsFlag(requiredCapabilities, CanvasComponents.Gyroscope.getValue()) && !this.availableCapabilities.contains(CanvasComponents.Gyroscope)) {
            XLELog.Diagnostic("DeviceCapabilities", "Requires gyroscope but device doesn't support it");
            success = false;
        }
        if (JavaUtil.containsFlag(requiredCapabilities, CanvasComponents.Haptic.getValue()) && !this.availableCapabilities.contains(CanvasComponents.Haptic)) {
            XLELog.Diagnostic("DeviceCapabilities", "Requires haptic but device doesn't support it");
            success = false;
        }
        if (!JavaUtil.containsFlag(requiredCapabilities, CanvasComponents.Location.getValue()) || hasLocationSupport()) {
            return success;
        }
        XLELog.Diagnostic("DeviceCapabilities", "Requires location but device doesn't support it");
        return false;
    }

    public boolean hasAccelerometerSupport() {
        return hasSensorSupport(1, "android.hardware.sensor.accelerometer");
    }

    public boolean hasGyroscopeSupport() {
        return hasSensorSupport(4, "android.hardware.sensor.gyroscope");
    }

    private boolean hasSensorSupport(int sensorType, String packageFeatureName) {
        boolean z;
        if (Thread.currentThread() == ThreadManager.UIThread) {
            z = true;
        } else {
            z = false;
        }
        XLEAssert.assertTrue(z);
        SensorManager sensorManager = (SensorManager) XLEApplication.Instance.getSystemService("sensor");
        Sensor sensor = sensorManager.getDefaultSensor(sensorType);
        if (sensor == null) {
            XLELog.Diagnostic("DeviceCapabilities", "Sensor is not available for this device: " + packageFeatureName);
            return false;
        } else if (XLEApplication.Instance.getPackageManager().hasSystemFeature(packageFeatureName)) {
            boolean success = sensorManager.registerListener(this.sensorListener, sensor, 2);
            sensorManager.unregisterListener(this.sensorListener);
            if (success) {
                XLELog.Diagnostic("DeviceCapabilities", "Sensor is available for this device: " + packageFeatureName);
                return true;
            }
            XLELog.Diagnostic("DeviceCapabilities", "Failed to register sensor listener: " + packageFeatureName);
            return false;
        } else {
            XLELog.Diagnostic("DeviceCapabilities", "Feature is not available for this application package: " + packageFeatureName);
            return false;
        }
    }

    public boolean hasLocationSupport() {
        try {
            if (XLEApplication.Instance.getPackageManager().hasSystemFeature("android.hardware.location")) {
                boolean enabled;
                LocationManager locationManager = (LocationManager) XLEApplication.Instance.getSystemService(CanvasEvent.Location);
                if (locationManager.isProviderEnabled("gps") || locationManager.isProviderEnabled("network")) {
                    enabled = true;
                } else {
                    enabled = false;
                }
                if (enabled) {
                    XLELog.Diagnostic("DeviceCapabilities", "Location service is available for this device.");
                    return true;
                }
                XLELog.Diagnostic("DeviceCapabilities", "Location service is not enabled.");
                return false;
            }
            XLELog.Diagnostic("DeviceCapabilities", "Location feature is not available for this application package.");
            return false;
        } catch (Exception ex) {
            XLELog.Diagnostic("DeviceCapabilities", "Error occurred while testing for location support: " + ex.toString());
            return false;
        }
    }

    public boolean hasHapticSupport() {
        try {
            if (((Vibrator) XLEApplication.Instance.getSystemService("vibrator")).hasVibrator()) {
                boolean hapticEnabled;
                if (System.getInt(XLEApplication.Instance.getContentResolver(), "haptic_feedback_enabled", 0) == 1) {
                    hapticEnabled = true;
                } else {
                    hapticEnabled = false;
                }
                if (hapticEnabled) {
                    XLELog.Diagnostic("DeviceCapabilities", "Haptic feature is available for this device.");
                    return true;
                }
                XLELog.Diagnostic("DeviceCapabilities", "Haptic feature is not available for this device.");
                return false;
            }
            XLELog.Diagnostic("DeviceCapabilities", "Device can't vibrate.");
            return false;
        } catch (Exception ex) {
            XLELog.Diagnostic("DeviceCapabilities", "Error occurred while testing for haptic support: " + ex.toString());
            return false;
        }
    }

    private void updateDeviceCapabilities() {
        this.availableCapabilities = EnumSet.noneOf(CanvasComponents.class);
        if (hasAccelerometerSupport()) {
            this.availableCapabilities.add(CanvasComponents.Accelerometer);
        }
        if (hasGyroscopeSupport()) {
            this.availableCapabilities.add(CanvasComponents.Gyroscope);
        }
        if (hasHapticSupport()) {
            this.availableCapabilities.add(CanvasComponents.Haptic);
        }
        if (hasLocationSupport()) {
            this.availableCapabilities.add(CanvasComponents.Location);
        }
    }
}
