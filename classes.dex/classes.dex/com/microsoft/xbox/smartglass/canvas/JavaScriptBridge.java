package com.microsoft.xbox.smartglass.canvas;

import com.microsoft.xbox.smartglass.canvas.json.JsonCanvasClientInfo;
import com.microsoft.xbox.smartglass.canvas.json.JsonCompleteRequest;
import com.microsoft.xbox.smartglass.canvas.json.JsonError;
import com.microsoft.xbox.smartglass.canvas.json.JsonScriptNotify;

public class JavaScriptBridge {
    private final String CanvasClientInfoClassName = "Canvas";
    private final String CanvasClientInfoMethodName = "setClientInfo";
    private CanvasView _canvas;

    public JavaScriptBridge(CanvasView canvas) {
        this._canvas = canvas;
    }

    public void Notify(String invokeString) {
        try {
            JsonScriptNotify scriptNotify = new JsonScriptNotify(invokeString);
            if (scriptNotify.getClassName().equals("Canvas") && scriptNotify.getMethodName().equals("setClientInfo")) {
                this._canvas.clientInfo = new JsonCanvasClientInfo(scriptNotify.getArgs().toString());
                this._canvas.completeRequest(new JsonCompleteRequest(scriptNotify.getId(), CanvasEvent.Done, null));
                return;
            }
            CanvasComponent component = this._canvas.getComponent(scriptNotify.getClassName());
            if (component != null) {
                component.invoke(scriptNotify.getMethodName(), scriptNotify.getId(), scriptNotify.getArgs());
            }
        } catch (Exception e) {
            this._canvas.on(CanvasEvent.Error, new JsonError(e.getLocalizedMessage()));
        }
    }
}
