package com.microsoft.xbox.smartglass.canvas.components;

import com.microsoft.xbox.service.model.MediaTitleState;
import com.microsoft.xbox.service.network.managers.xblshared.CompanionSession;
import com.microsoft.xbox.service.network.managers.xblshared.ICompanionSessionMediaTitleStateListener;
import com.microsoft.xbox.smartglass.canvas.CanvasComponent;
import com.microsoft.xbox.smartglass.canvas.CanvasEvent;
import com.microsoft.xbox.smartglass.canvas.CanvasView;
import com.microsoft.xbox.smartglass.canvas.json.JsonCompleteRequest;
import com.microsoft.xbox.smartglass.canvas.json.JsonError;
import com.microsoft.xbox.smartglass.canvas.json.JsonMediaState;
import com.microsoft.xbox.toolkit.XLELog;
import org.json.JSONException;
import org.json.JSONObject;

public class Media implements CanvasComponent, ICompanionSessionMediaTitleStateListener {
    private static final String COMPONENT_NAME = "Media";
    private static final String GETSTATE_METHOD = "GetState";
    private static final String SEEK_METHOD = "Seek";
    private CanvasView _canvas;
    private JsonMediaState _currentMediaState;

    public Media(CanvasView canvas) {
        this._canvas = canvas;
        CompanionSession.getInstance().addCompanionSessionMediaTitleStateListener(this);
        try {
            this._currentMediaState = new JsonMediaState(new MediaTitleState());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void stopComponent() {
        CompanionSession.getInstance().removeCompanionSessionMediaTitleStateListener(this);
    }

    public void invoke(String methodName, int id, Object arguments) {
        if (methodName.equals(GETSTATE_METHOD)) {
            getState(id);
        } else if (methodName.equals(SEEK_METHOD)) {
            seek(id, arguments.toString());
        }
    }

    public JSONObject getCurrentState() {
        if (this._currentMediaState == null) {
            try {
                this._currentMediaState = new JsonMediaState();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return this._currentMediaState;
    }

    public void getState(int id) {
        if (this._canvas.validateSession(false, id)) {
            MediaTitleState mediaState = CompanionSession.getInstance().getCurrentMediaState();
            if (mediaState == null) {
                mediaState = new MediaTitleState();
            }
            try {
                this._currentMediaState = new JsonMediaState(mediaState);
                this._canvas.completeRequest(new JsonCompleteRequest(id, CanvasEvent.Done, this._currentMediaState));
            } catch (JSONException exception) {
                this._canvas.completeRequest(new JsonCompleteRequest(id, CanvasEvent.Error, new JsonError(exception.getLocalizedMessage())));
            }
        }
    }

    public void seek(int id, String seekPositionTicks) {
        if (this._canvas.validateSession(false, id)) {
            try {
                CompanionSession.getInstance().SendSeekControlCommand(Long.parseLong(seekPositionTicks.trim()));
                this._canvas.completeRequest(new JsonCompleteRequest(id, CanvasEvent.Done, null));
            } catch (NumberFormatException exception) {
                this._canvas.completeRequest(new JsonCompleteRequest(id, CanvasEvent.Error, new JsonError(exception.getLocalizedMessage())));
            }
        }
    }

    public void onMediaTitleStateUpdated(MediaTitleState mediaState) {
        if (this._canvas.validateSession(false, -1)) {
            try {
                this._currentMediaState = new JsonMediaState(mediaState);
                this._canvas.on(CanvasEvent.MediaState, this._currentMediaState);
            } catch (JSONException exception) {
                XLELog.Diagnostic(COMPONENT_NAME, "Exception occurred: " + exception.getLocalizedMessage());
            }
        }
    }
}
