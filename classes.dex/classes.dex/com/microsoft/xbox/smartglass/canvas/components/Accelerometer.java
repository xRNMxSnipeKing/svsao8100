package com.microsoft.xbox.smartglass.canvas.components;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import com.microsoft.xbox.service.model.edsv2.EDSV2MediaType;
import com.microsoft.xbox.service.network.managers.xblshared.CompanionSession;
import com.microsoft.xbox.smartglass.canvas.CanvasComponent;
import com.microsoft.xbox.smartglass.canvas.CanvasEvent;
import com.microsoft.xbox.smartglass.canvas.CanvasView;
import com.microsoft.xbox.smartglass.canvas.json.JsonAccelerometer;
import com.microsoft.xbox.smartglass.canvas.json.JsonAccelerometerCurrentState;
import com.microsoft.xbox.smartglass.canvas.json.JsonAccelerometerStartParameters;
import com.microsoft.xbox.smartglass.canvas.json.JsonCompleteRequest;
import com.microsoft.xbox.smartglass.canvas.json.JsonError;
import com.microsoft.xbox.toolkit.XLELog;
import org.json.JSONException;
import org.json.JSONObject;

public class Accelerometer implements CanvasComponent, SensorEventListener {
    private static final String COMPONENT_NAME = "Accelerometer";
    private static final float GRAVITY_ALPHA = 0.8f;
    private static final String START_METHOD = "Start";
    private static final String STOP_METHOD = "Stop";
    private final int MINIMUM_SAMPLE_RATE = 20;
    private final Sensor _accelerometer;
    private CanvasView _canvas;
    private float[] _gravity = new float[]{0.0f, 0.0f, 0.0f};
    private boolean _hasAccelerometer;
    private boolean _isActive;
    private float[] _linearAccel = new float[]{0.0f, 0.0f, 0.0f};
    private long _nextEventTimeInMilliseconds = 200;
    private final SensorManager _sensorManager;
    private JsonAccelerometerStartParameters _startParameters = null;

    public Accelerometer(CanvasView canvas) {
        this._canvas = canvas;
        this._sensorManager = (SensorManager) canvas.getContext().getSystemService("sensor");
        this._accelerometer = this._sensorManager.getDefaultSensor(1);
        this._isActive = false;
        this._hasAccelerometer = this._sensorManager.registerListener(this, this._accelerometer, 3);
        this._sensorManager.unregisterListener(this);
    }

    public void stopComponent() {
        if (this._sensorManager != null) {
            XLELog.Diagnostic("CanvasView", "Listener unregistered for accelerometer component");
            this._sensorManager.unregisterListener(this);
        }
    }

    public void invoke(String methodName, int id, Object arguments) {
        if (methodName.equals(START_METHOD)) {
            start(id, (JSONObject) arguments);
        } else if (methodName.equals(STOP_METHOD)) {
            stop(id);
        }
    }

    public JSONObject getCurrentState() {
        try {
            return new JsonAccelerometerCurrentState(Boolean.valueOf(this._hasAccelerometer), Boolean.valueOf(this._isActive));
        } catch (Exception exception) {
            XLELog.Diagnostic(COMPONENT_NAME, "Exception occurred: " + exception.getLocalizedMessage());
            return null;
        }
    }

    public void start(int id, JSONObject jsonObject) {
        if (this._hasAccelerometer && !this._isActive) {
            try {
                this._startParameters = new JsonAccelerometerStartParameters(jsonObject);
                this._sensorManager.registerListener(this, this._accelerometer, Math.max(this._startParameters.getSampleRateMilliseconds(), 20) * EDSV2MediaType.MEDIATYPE_MOVIE);
                this._isActive = true;
            } catch (Exception exception) {
                this._canvas.completeRequest(new JsonCompleteRequest(id, CanvasEvent.Error, new JsonError(exception.getLocalizedMessage())));
            }
        }
        this._canvas.completeRequest(new JsonCompleteRequest(id, CanvasEvent.Done, null));
    }

    public void stop(int id) {
        this._sensorManager.unregisterListener(this);
        this._isActive = false;
        this._startParameters = null;
        this._canvas.completeRequest(new JsonCompleteRequest(id, CanvasEvent.Done, null));
    }

    public void onAccuracyChanged(Sensor arg0, int arg1) {
        XLELog.Diagnostic(COMPONENT_NAME, "OnAccuracyChanged triggered");
    }

    public void onSensorChanged(SensorEvent event) {
        if (System.currentTimeMillis() >= this._nextEventTimeInMilliseconds && this._isActive && event != null && event.sensor != null && event.values != null && event.sensor.getType() == 1) {
            for (int i = 0; i < this._gravity.length; i++) {
                this._gravity[i] = (GRAVITY_ALPHA * this._gravity[i]) + (0.19999999f * event.values[i]);
                this._linearAccel[i] = event.values[i] - this._gravity[i];
            }
            if (this._startParameters.getSendToConsole() && this._canvas.validateSession(true, -1)) {
                CompanionSession.getInstance().SendTitleAccelerometerData(this._linearAccel[0], this._linearAccel[1], this._linearAccel[2]);
            }
            if (this._startParameters.getSendToCanvas() || !this._startParameters.getSendToConsole()) {
                try {
                    this._canvas.on(CanvasEvent.Accelerometer, new JsonAccelerometer(this._linearAccel[0], this._linearAccel[1], this._linearAccel[2]));
                    this._nextEventTimeInMilliseconds = System.currentTimeMillis() + ((long) this._startParameters.getSampleRateMilliseconds());
                } catch (JSONException exception) {
                    XLELog.Diagnostic(COMPONENT_NAME, "Exception occurred: " + exception.getLocalizedMessage());
                }
            }
        }
    }
}
