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
import com.microsoft.xbox.smartglass.canvas.json.JsonCompleteRequest;
import com.microsoft.xbox.smartglass.canvas.json.JsonError;
import com.microsoft.xbox.smartglass.canvas.json.JsonGyroscope;
import com.microsoft.xbox.smartglass.canvas.json.JsonGyroscopeCurrentState;
import com.microsoft.xbox.smartglass.canvas.json.JsonGyroscopeStartParameters;
import com.microsoft.xbox.toolkit.XLELog;
import org.json.JSONException;
import org.json.JSONObject;

public class Gyroscope implements CanvasComponent, SensorEventListener {
    private static final String COMPONENT_NAME = "Gyroscope";
    private static final String START_METHOD = "Start";
    private static final String STOP_METHOD = "Stop";
    private final int MINIMUM_SAMPLE_RATE = 20;
    private CanvasView _canvas;
    private final Sensor _gyroscope;
    private boolean _hasGyroscope;
    private boolean _isActive;
    private long _nextEventTimeInMilliseconds = 200;
    private final SensorManager _sensorManager;
    private JsonGyroscopeStartParameters _startParameters = null;

    public Gyroscope(CanvasView canvas) {
        this._canvas = canvas;
        this._sensorManager = (SensorManager) canvas.getContext().getSystemService("sensor");
        this._gyroscope = this._sensorManager.getDefaultSensor(4);
        this._isActive = false;
        this._hasGyroscope = this._sensorManager.registerListener(this, this._gyroscope, 3);
        this._sensorManager.unregisterListener(this);
    }

    public void stopComponent() {
        if (this._sensorManager != null) {
            XLELog.Diagnostic("CanvasView", "Listener unregistered for gyroscope component");
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
            return new JsonGyroscopeCurrentState(Boolean.valueOf(this._hasGyroscope), Boolean.valueOf(this._isActive));
        } catch (Exception exception) {
            XLELog.Diagnostic(COMPONENT_NAME, "Exception occurred: " + exception.getLocalizedMessage());
            return null;
        }
    }

    public void start(int id, JSONObject jsonObject) {
        if (this._hasGyroscope && !this._isActive) {
            try {
                this._startParameters = new JsonGyroscopeStartParameters(jsonObject);
                this._sensorManager.registerListener(this, this._gyroscope, Math.max(this._startParameters.getSampleRateMilliseconds(), 20) * EDSV2MediaType.MEDIATYPE_MOVIE);
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
        if (System.currentTimeMillis() >= this._nextEventTimeInMilliseconds && this._isActive && event != null && event.sensor != null && event.values != null && event.sensor.getType() == 4) {
            if (this._startParameters.getSendToConsole() && this._canvas.validateSession(true, -1)) {
                CompanionSession.getInstance().SendTitleGyroscopeData(event.values[0], event.values[1], event.values[2]);
            }
            if (this._startParameters.getSendToCanvas() || !this._startParameters.getSendToConsole()) {
                try {
                    this._canvas.on(CanvasEvent.Gyroscope, new JsonGyroscope(event.values[0], event.values[1], event.values[2]));
                    this._nextEventTimeInMilliseconds = System.currentTimeMillis() + ((long) this._startParameters.getSampleRateMilliseconds());
                } catch (JSONException exception) {
                    XLELog.Diagnostic(COMPONENT_NAME, "Exception occurred: " + exception.getLocalizedMessage());
                }
            }
        }
    }
}
