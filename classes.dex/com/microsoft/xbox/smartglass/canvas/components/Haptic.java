package com.microsoft.xbox.smartglass.canvas.components;

import android.os.Vibrator;
import android.provider.Settings.System;
import com.microsoft.xbox.smartglass.canvas.CanvasComponent;
import com.microsoft.xbox.smartglass.canvas.CanvasEvent;
import com.microsoft.xbox.smartglass.canvas.CanvasView;
import com.microsoft.xbox.smartglass.canvas.json.JsonCompleteRequest;
import com.microsoft.xbox.smartglass.canvas.json.JsonHapticCurrentState;
import com.microsoft.xbox.toolkit.XLELog;
import org.json.JSONObject;

public class Haptic implements CanvasComponent {
    private static final String COMPONENT_NAME = "Haptic";
    private static final String START_METHOD = "Start";
    private static final String STOP_METHOD = "Stop";
    private CanvasView _canvas;
    private Vibrator _hapticService;
    private boolean _hasHaptic;
    private boolean _isActive = false;

    public Haptic(CanvasView canvas) {
        boolean z;
        this._canvas = canvas;
        this._hapticService = (Vibrator) canvas.getContext().getSystemService("vibrator");
        if (System.getInt(canvas.getContext().getContentResolver(), "haptic_feedback_enabled", 0) == 1) {
            z = true;
        } else {
            z = false;
        }
        this._hasHaptic = z;
    }

    public void stopComponent() {
    }

    public void invoke(String methodName, int id, Object arguments) {
        if (methodName.equals(START_METHOD)) {
            start(id);
        } else if (methodName.equals(STOP_METHOD)) {
            stop(id);
        }
    }

    public JSONObject getCurrentState() {
        boolean z = true;
        try {
            if (System.getInt(this._canvas.getContext().getContentResolver(), "haptic_feedback_enabled", 0) != 1) {
                z = false;
            }
            this._hasHaptic = z;
            return new JsonHapticCurrentState(Boolean.valueOf(this._hasHaptic), Boolean.valueOf(this._isActive));
        } catch (Exception exception) {
            XLELog.Diagnostic(COMPONENT_NAME, "Exception occurred: " + exception.getLocalizedMessage());
            return null;
        }
    }

    public void start(int id) {
        if (this._hasHaptic && this._hapticService != null) {
            this._hapticService.vibrate(400);
            this._isActive = true;
        }
        this._canvas.completeRequest(new JsonCompleteRequest(id, CanvasEvent.Done, null));
    }

    public void stop(int id) {
        this._isActive = false;
        this._canvas.completeRequest(new JsonCompleteRequest(id, CanvasEvent.Done, null));
    }
}
