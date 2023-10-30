package com.microsoft.xbox.smartglass.canvas.components;

import com.microsoft.xbox.service.network.managers.xblshared.CompanionSession;
import com.microsoft.xbox.service.network.managers.xblshared.ICompanionSessionJsonTitleMessageListener;
import com.microsoft.xbox.smartglass.canvas.CanvasComponent;
import com.microsoft.xbox.smartglass.canvas.CanvasEvent;
import com.microsoft.xbox.smartglass.canvas.CanvasView;
import com.microsoft.xbox.smartglass.canvas.json.JsonCompleteRequest;
import com.microsoft.xbox.toolkit.XLELog;
import org.json.JSONObject;

public class Messaging implements CanvasComponent, ICompanionSessionJsonTitleMessageListener {
    private static final String COMPONENT_NAME = "Messaging";
    private static final String SENDMESSAGE_METHOD = "SendMessage";
    private CanvasView _canvas;

    public Messaging(CanvasView canvas) {
        this._canvas = canvas;
        CompanionSession.getInstance().addCompanionSessionJsonTitleMessageListener(this);
    }

    public void stopComponent() {
        CompanionSession.getInstance().removeCompanionSessionJsonTitleMessageListener(this);
    }

    public void invoke(String methodName, int id, Object arguments) {
        if (methodName.equals(SENDMESSAGE_METHOD)) {
            sendMessage(id, (String) arguments);
        }
    }

    public JSONObject getCurrentState() {
        return null;
    }

    private void sendMessage(int id, String message) {
        if (this._canvas.validateSession(true, id)) {
            CompanionSession.getInstance().SendTitleMessage(message);
            this._canvas.completeRequest(new JsonCompleteRequest(id, CanvasEvent.Done, null));
        }
    }

    public void onJsonTitleMessage(String message) {
        if (this._canvas.validateSession(true, -1)) {
            XLELog.Diagnostic(COMPONENT_NAME, "JSON Title Message received: " + message);
            this._canvas.on(CanvasEvent.Received, message);
        }
    }
}
