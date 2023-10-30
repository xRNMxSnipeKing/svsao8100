package com.microsoft.xbox.smartglass.canvas.components;

import com.microsoft.xbox.smartglass.canvas.CanvasComponent;
import com.microsoft.xbox.smartglass.canvas.CanvasEvent;
import com.microsoft.xbox.smartglass.canvas.CanvasView;
import com.microsoft.xbox.smartglass.canvas.json.JsonCompleteRequest;
import com.microsoft.xbox.smartglass.canvas.json.JsonError;
import com.microsoft.xbox.smartglass.canvas.json.JsonLocationCurrentState;
import com.microsoft.xbox.toolkit.XLELog;
import org.json.JSONObject;

public class Location implements CanvasComponent {
    private static final String COMPONENT_NAME = "Location";
    private static final String START_METHOD = "Start";
    private static final String STOP_METHOD = "Stop";
    private CanvasView _canvas;
    private Boolean _isActive = Boolean.valueOf(false);

    public Location(CanvasView canvas) {
        this._canvas = canvas;
    }

    public void stopComponent() {
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
            return new JsonLocationCurrentState(Boolean.valueOf(false), this._isActive);
        } catch (Exception exception) {
            XLELog.Diagnostic(COMPONENT_NAME, "Exception occurred: " + exception.getLocalizedMessage());
            return null;
        }
    }

    private void start(int id, JSONObject jsonObject) {
        this._canvas.completeRequest(new JsonCompleteRequest(id, CanvasEvent.Error, new JsonError("Location is not supported in this release.")));
    }

    private void stop(int id) {
        this._canvas.completeRequest(new JsonCompleteRequest(id, CanvasEvent.Error, new JsonError("Location is not supported in this release.")));
    }
}
