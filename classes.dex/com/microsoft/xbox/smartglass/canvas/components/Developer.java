package com.microsoft.xbox.smartglass.canvas.components;

import com.microsoft.xbox.service.model.ActiveTitleInfo;
import com.microsoft.xbox.service.model.ConsoleSettings;
import com.microsoft.xbox.service.model.KeyboardText;
import com.microsoft.xbox.service.model.MediaTitleState;
import com.microsoft.xbox.service.network.managers.xblshared.CompanionSession;
import com.microsoft.xbox.service.network.managers.xblshared.ICompanionSessionActiveTitleInfoListener;
import com.microsoft.xbox.service.network.managers.xblshared.ICompanionSessionConsoleSettingsListener;
import com.microsoft.xbox.service.network.managers.xblshared.ICompanionSessionJsonTitleMessageListener;
import com.microsoft.xbox.service.network.managers.xblshared.ICompanionSessionKeyboardTextListener;
import com.microsoft.xbox.service.network.managers.xblshared.ICompanionSessionMediaTitleStateListener;
import com.microsoft.xbox.service.network.managers.xblshared.ICompanionSessionStateListener;
import com.microsoft.xbox.service.network.managers.xblshared.ICompanionSessionTitleListener;
import com.microsoft.xbox.smartglass.canvas.CanvasComponent;
import com.microsoft.xbox.smartglass.canvas.CanvasEvent;
import com.microsoft.xbox.smartglass.canvas.CanvasView;
import com.microsoft.xbox.smartglass.canvas.CanvasView.IActivity;
import com.microsoft.xbox.smartglass.canvas.json.JsonCompleteRequest;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.XLELog;
import org.json.JSONObject;

public class Developer implements CanvasComponent, ICompanionSessionStateListener, ICompanionSessionMediaTitleStateListener, ICompanionSessionTitleListener, ICompanionSessionJsonTitleMessageListener, ICompanionSessionConsoleSettingsListener, ICompanionSessionKeyboardTextListener, ICompanionSessionActiveTitleInfoListener {
    private CanvasView _canvas;
    private String _lastTextGotten;

    public Developer(CanvasView canvas) {
        this._canvas = canvas;
        CompanionSession.getInstance().addCompanionSessionStateListener(this);
        CompanionSession.getInstance().addCompanionSessionTitleListener(this);
        CompanionSession.getInstance().addCompanionSessionMediaTitleStateListener(this);
        CompanionSession.getInstance().addCompanionSessionJsonTitleMessageListener(this);
        CompanionSession.getInstance().addCompanionSessionKeyboardTextListener(this);
        CompanionSession.getInstance().addCompanionSessionConsoleSettingsListener(this);
        CompanionSession.getInstance().addCompanionSessionActiveTitleInfoListener(this);
    }

    public void stopComponent() {
        CompanionSession.getInstance().removeCompanionSessionStateListener(this);
        CompanionSession.getInstance().removeCompanionSessionTitleListener(this);
        CompanionSession.getInstance().removeCompanionSessionMediaTitleStateListener(this);
        CompanionSession.getInstance().removeCompanionSessionJsonTitleMessageListener(this);
        CompanionSession.getInstance().removeCompanionSessionKeyboardTextListener(this);
        CompanionSession.getInstance().removeCompanionSessionConsoleSettingsListener(this);
        CompanionSession.getInstance().removeCompanionSessionActiveTitleInfoListener(this);
    }

    public void onSessionStateChanged(int newSessionState, XLEException exception) {
        XLELog.Info("CompanionSession", "Developer Comp: onSessionStateChanged");
    }

    public void onMediaTitleStateUpdated(MediaTitleState mediaState) {
        XLELog.Info("CompanionSession", "Developer Comp: onMediaTitleStateUpdated" + mediaState);
    }

    public void onTitleChanged(long oldTitleId, long newTitleId) {
        XLELog.Info("CompanionSession", "Developer Comp: onTitleChanged, Old title = " + oldTitleId + ".  New Title = " + newTitleId);
    }

    public void onJsonTitleMessage(String message) {
        XLELog.Info("CompanionSession", "Developer Comp: onJsonTitleMessage: " + message);
    }

    public void OnGetKeyboardTextResponse(KeyboardText keyboardText) {
        this._lastTextGotten = keyboardText.TextString;
        XLELog.Info("CompanionSession", "Developer Comp: OnGetKeyboardTextResponse: " + keyboardText.TextString);
        logToDevice("Receiving Response for GetText Request: {" + keyboardText.TextString + "}");
    }

    public void OnGetConsoleSettingsResponse(ConsoleSettings settings) {
        XLELog.Info("CompanionSession", "Developer Comp: OnGetConsoleSettingsResponse: locale = " + settings.Locale);
        logToDevice("Receiving Response for GetConsoleSettings Request:");
        logToDevice("    Locale=" + settings.Locale);
        logToDevice("    FlashVersion=" + settings.FlashVersion);
        logToDevice("    LiveTvProvider=" + settings.LiveTvProvider);
    }

    public void OnGetActiveTitleInfoResponse(ActiveTitleInfo activeTitleInfo) {
        XLELog.Info("CompanionSession", "Developer Comp: OnGetActiveTitleInfoResponse:" + activeTitleInfo.TitleId);
        logToDevice("Receiving Response for GetActiveTitleInfo Request:");
        logToDevice("    TitleId=" + activeTitleInfo.TitleId);
        logToDevice("    Port=" + activeTitleInfo.Port);
        logToDevice("    Enabled=" + activeTitleInfo.Enabled);
    }

    private void login(int requestId, String envString) {
        if (this._canvas != null) {
            IActivity activity = this._canvas.getActivity();
            if (activity != null) {
                activity.setEnvironment(envString);
            }
            this._canvas.loadUrl("https://login.live-int.com/oauth20_authorize.srf?client_id=0000000068046BE4&redirect_uri=https://login.live-int.com/oauth20_desktop.srf&response_type=token&display=touch&scope=service::live.rtm.vint.xbox.com::MBI_SSL");
        }
    }

    private void setText(int requestId, String text) {
        if (this._canvas.validateSession(false, requestId)) {
            logToDevice("Sending setText Request");
            if (this._lastTextGotten != null) {
                CompanionSession.getInstance().SetText(text, this._lastTextGotten);
            }
            this._canvas.completeRequest(new JsonCompleteRequest(requestId, CanvasEvent.Done, null));
        }
    }

    private void connect(int requestId) {
        logToDevice("Sending connect Request");
        CompanionSession.getInstance().Connect();
        this._canvas.completeRequest(new JsonCompleteRequest(requestId, CanvasEvent.Done, null));
    }

    private void disconnect(int requestId) {
        if (this._canvas.validateSession(false, requestId)) {
            logToDevice("Sending disconnect Request");
            CompanionSession.getInstance().Disconnect();
            this._canvas.completeRequest(new JsonCompleteRequest(requestId, CanvasEvent.Done, null));
        }
    }

    private void acquireExclusiveMode(int requestId) {
        if (this._canvas.validateSession(false, requestId)) {
            logToDevice("Sending acquireExclusiveMode Request");
            CompanionSession.getInstance().AcquireExclusiveMode();
            this._canvas.completeRequest(new JsonCompleteRequest(requestId, CanvasEvent.Done, null));
        }
    }

    private void releaseExclusiveMode(int requestId) {
        if (this._canvas.validateSession(false, requestId)) {
            logToDevice("Sending releaseExclusiveMode Request");
            CompanionSession.getInstance().ReleaseExclusiveMode();
            this._canvas.completeRequest(new JsonCompleteRequest(requestId, CanvasEvent.Done, null));
        }
    }

    private void getConsoleSettings(int requestId) {
        if (this._canvas.validateSession(false, requestId)) {
            logToDevice("Sending getConsoleSettings Request");
            CompanionSession.getInstance().GetConsoleSettings();
            this._canvas.completeRequest(new JsonCompleteRequest(requestId, CanvasEvent.Done, null));
        }
    }

    private void getActiveTitleInfo(int requestId) {
        if (this._canvas.validateSession(false, requestId)) {
            logToDevice("Sending getActiveTitleInfo Request");
            CompanionSession.getInstance().GetActiveTitleInfo();
            this._canvas.completeRequest(new JsonCompleteRequest(requestId, CanvasEvent.Done, null));
        }
    }

    private void getText(int requestId) {
        if (this._canvas.validateSession(false, requestId)) {
            logToDevice("Sending getText Request");
            CompanionSession.getInstance().GetText();
            this._canvas.completeRequest(new JsonCompleteRequest(requestId, CanvasEvent.Done, null));
        }
    }

    private void establishTitleChannel(int requestId) {
        if (this._canvas.validateSession(false, requestId)) {
            logToDevice("Sending establishTitleChannel Request");
            CompanionSession.getInstance().EstablishTitleChannel();
            this._canvas.completeRequest(new JsonCompleteRequest(requestId, CanvasEvent.Done, null));
        }
    }

    private void log(int requestId, String logText) {
        XLELog.Info("CompanionSession", "Log from the Canvas: " + logText);
    }

    public void invoke(String component, int requestId, Object arguments) {
        if (component.equals("Login")) {
            login(requestId, (String) arguments);
        } else if (component.equals("Connect")) {
            connect(requestId);
        } else if (component.equals("Disconnect")) {
            disconnect(requestId);
        } else if (component.equals("AcquireExclusiveMode")) {
            acquireExclusiveMode(requestId);
        } else if (component.equals("ReleaseExclusiveMode")) {
            releaseExclusiveMode(requestId);
        } else if (component.equals("GetConsoleSettings")) {
            getConsoleSettings(requestId);
        } else if (component.equals("GetActiveTitleInfo")) {
            getActiveTitleInfo(requestId);
        } else if (component.equals("GetText")) {
            getText(requestId);
        } else if (component.equals("SetText")) {
            setText(requestId, (String) arguments);
        } else if (component.equals("EstablishTitleChannel")) {
            establishTitleChannel(requestId);
        } else if (component.equals("Log")) {
            log(requestId, (String) arguments);
        }
    }

    public JSONObject getCurrentState() {
        return null;
    }

    public void logToDevice(String msg) {
        if (this._canvas != null) {
            this._canvas.on("log", "{\"msg\": \"" + msg + "\"}");
        }
    }
}
