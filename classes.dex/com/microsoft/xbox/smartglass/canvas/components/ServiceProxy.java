package com.microsoft.xbox.smartglass.canvas.components;

import com.microsoft.xbox.smartglass.canvas.CanvasComponent;
import com.microsoft.xbox.smartglass.canvas.CanvasEvent;
import com.microsoft.xbox.smartglass.canvas.CanvasView;
import com.microsoft.xbox.smartglass.canvas.TaskTracker;
import com.microsoft.xbox.smartglass.canvas.json.JsonCompleteRequest;
import com.microsoft.xbox.smartglass.canvas.json.JsonError;
import com.microsoft.xbox.smartglass.canvas.json.JsonServiceRequest;
import com.microsoft.xbox.smartglass.canvas.json.JsonServiceRequestResponse;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.Observable;
import java.util.Observer;
import org.json.JSONObject;

public class ServiceProxy implements CanvasComponent {
    private static final String SENDREQUEST_METHOD = "SendRequest";
    private static CookieManager _cookieManager = new CookieManager();
    private CanvasView _canvas;
    private TaskTracker _tracker = new TaskTracker();

    static {
        _cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_NONE);
        CookieHandler.setDefault(_cookieManager);
    }

    public ServiceProxy(CanvasView canvas) {
        this._canvas = canvas;
    }

    public void stopComponent() {
        this._tracker.cancelAllTasks();
    }

    public void invoke(String methodName, int id, Object arguments) {
        if (methodName.equals(SENDREQUEST_METHOD)) {
            sendRequest(id, (JSONObject) arguments);
        }
    }

    public JSONObject getCurrentState() {
        return null;
    }

    private void sendRequest(int id, JSONObject jsonObject) {
        try {
            ServiceRequest request = new ServiceRequest(this._canvas, false, id, new JsonServiceRequest(jsonObject), this._tracker);
            request.addObserver(new Observer() {
                public void update(Observable observable, Object data) {
                    ServiceRequest request = (ServiceRequest) observable;
                    try {
                        ServiceProxy.this._canvas.completeRequest(new JsonCompleteRequest(request._response.requestId, CanvasEvent.Done, new JsonServiceRequestResponse(request._response.response, request._response.headers)));
                    } catch (Exception exception) {
                        ServiceProxy.this._canvas.completeRequest(new JsonCompleteRequest(request._response.requestId, CanvasEvent.Error, new JsonError(exception.getLocalizedMessage())));
                    }
                }
            });
            request.runAsync();
        } catch (Exception exception) {
            this._canvas.completeRequest(new JsonCompleteRequest(id, CanvasEvent.Error, new JsonError(exception.getLocalizedMessage())));
        }
    }
}
