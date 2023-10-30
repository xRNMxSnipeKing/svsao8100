package com.microsoft.xbox.smartglass.canvas.components;

import android.content.Intent;
import android.net.Uri;
import com.microsoft.xbox.smartglass.canvas.CanvasComponent;
import com.microsoft.xbox.smartglass.canvas.CanvasEvent;
import com.microsoft.xbox.smartglass.canvas.CanvasView;
import com.microsoft.xbox.smartglass.canvas.json.JsonBrowserParameters;
import com.microsoft.xbox.smartglass.canvas.json.JsonCompleteRequest;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLELog;
import java.util.HashSet;
import org.json.JSONException;
import org.json.JSONObject;

public class Browser implements CanvasComponent {
    private static final String COMPONENT_NAME = "Browser";
    private static final String START_METHOD = "Launch";
    private static HashSet<String> _allowedScheme = getAllowedSchemes();
    private CanvasView _canvas;

    public Browser(CanvasView canvas) {
        this._canvas = canvas;
    }

    private static HashSet<String> getAllowedSchemes() {
        HashSet<String> schemes = new HashSet(2);
        schemes.add("http");
        schemes.add("https");
        return schemes;
    }

    public void stopComponent() {
    }

    private void launch(final Uri url, int id) {
        if (_allowedScheme.contains(url.getScheme())) {
            this._canvas.completeRequest(new JsonCompleteRequest(id, CanvasEvent.Done, null));
            ThreadManager.UIThreadPostDelayed(new Runnable() {
                public void run() {
                    Browser.this._canvas.getContext().startActivity(new Intent("android.intent.action.VIEW", url));
                }
            }, 500);
            return;
        }
        XLELog.Warning(COMPONENT_NAME, "The url has an unsupported scheme");
        this._canvas.completeRequest(new JsonCompleteRequest(id, CanvasEvent.Error, null));
    }

    public void invoke(String methodName, int id, Object argument) {
        if (methodName.equals("Launch") && (argument instanceof JSONObject)) {
            try {
                launch(new JsonBrowserParameters((JSONObject) argument).getUri(), id);
            } catch (JSONException e) {
                XLELog.Warning(COMPONENT_NAME, "No valid url was passed into Browser component");
                this._canvas.completeRequest(new JsonCompleteRequest(id, CanvasEvent.Error, null));
            }
        }
    }

    public JSONObject getCurrentState() {
        return null;
    }
}
