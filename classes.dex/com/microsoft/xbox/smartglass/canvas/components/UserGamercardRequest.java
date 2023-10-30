package com.microsoft.xbox.smartglass.canvas.components;

import com.microsoft.xbox.service.network.managers.xblshared.CompanionSession;
import com.microsoft.xbox.smartglass.canvas.CanvasEvent;
import com.microsoft.xbox.smartglass.canvas.CanvasView;
import com.microsoft.xbox.smartglass.canvas.TaskTracker;
import com.microsoft.xbox.smartglass.canvas.json.JsonCompleteRequest;
import com.microsoft.xbox.smartglass.canvas.json.JsonError;
import com.microsoft.xbox.smartglass.canvas.json.JsonServiceRequest;
import com.microsoft.xbox.smartglass.canvas.json.JsonUserGamercard;
import java.util.Observable;
import java.util.Observer;
import org.json.JSONObject;

public class UserGamercardRequest {
    private final String SERVICES_PATH_FORMAT = "users/xuid(%s)/gamercard";
    private final String SERVICES_PROFILE_SUBDOMAIN = "profile";
    private CanvasView _canvas;
    private TaskTracker _tracker;

    public UserGamercardRequest(CanvasView canvas, TaskTracker tracker) {
        this._canvas = canvas;
        this._tracker = tracker;
    }

    public void sendRequest(int id, String xuid) {
        try {
            String servicesUrl = CompanionSession.getInstance().environment.getServicesEndpointUri("profile", String.format("users/xuid(%s)/gamercard", new Object[]{xuid}));
            JSONObject jsonSettings = new JSONObject();
            jsonSettings.put("method", "GET");
            jsonSettings.put("url", servicesUrl);
            jsonSettings.put("sendUserToken", true);
            jsonSettings.put("audienceUri", CompanionSession.getInstance().environment.getXliveXstsAudienceUri());
            ServiceRequest request = new ServiceRequest(this._canvas, true, id, new JsonServiceRequest(jsonSettings), this._tracker);
            request.addObserver(new Observer() {
                public void update(Observable observable, Object data) {
                    ServiceRequest request = (ServiceRequest) observable;
                    try {
                        UserGamercardRequest.this._canvas.completeRequest(new JsonCompleteRequest(request._response.requestId, CanvasEvent.Done, new JsonUserGamercard(request._response.response)));
                    } catch (Exception exception) {
                        UserGamercardRequest.this._canvas.completeRequest(new JsonCompleteRequest(request._response.requestId, CanvasEvent.Error, new JsonError(exception.getLocalizedMessage())));
                    }
                }
            });
            request.runAsync();
        } catch (Exception exception) {
            this._canvas.completeRequest(new JsonCompleteRequest(id, CanvasEvent.Error, new JsonError(exception.getLocalizedMessage())));
        }
    }
}
