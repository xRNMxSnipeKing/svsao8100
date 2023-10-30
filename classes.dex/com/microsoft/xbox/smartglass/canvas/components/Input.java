package com.microsoft.xbox.smartglass.canvas.components;

import com.microsoft.xbox.service.network.managers.xblshared.CompanionSession;
import com.microsoft.xbox.smartglass.canvas.CanvasComponent;
import com.microsoft.xbox.smartglass.canvas.CanvasEvent;
import com.microsoft.xbox.smartglass.canvas.CanvasView;
import com.microsoft.xbox.smartglass.canvas.json.JsonCompleteRequest;
import org.json.JSONObject;

public class Input implements CanvasComponent {
    private static final String COMPONENT_NAME = "Input";
    private static final String SEND_METHOD = "Send";
    private CanvasView _canvas;

    public static native int nativeMapCanvasKeyToControlKey(int i);

    public Input(CanvasView canvas) {
        this._canvas = canvas;
    }

    public void stopComponent() {
    }

    public void invoke(String methodName, int id, Object arguments) {
        if (methodName.equals(SEND_METHOD) && arguments != null) {
            send(id, (Integer) arguments);
        }
    }

    public JSONObject getCurrentState() {
        return null;
    }

    public void send(int id, Integer canvasKey) {
        if (this._canvas.validateSession(false, id)) {
            CompanionSession.getInstance().SendControlCommandWithMediaCenterSupport(nativeMapCanvasKeyToControlKey(canvasKey.intValue()));
            this._canvas.completeRequest(new JsonCompleteRequest(id, CanvasEvent.Done, null));
        }
    }
}
