package com.microsoft.xbox.smartglass.canvas.components;

import android.os.Build;
import com.microsoft.xbox.service.network.managers.xblshared.CompanionSession;
import com.microsoft.xbox.service.network.managers.xblshared.ICompanionSessionDeviceJoinedListener;
import com.microsoft.xbox.service.network.managers.xblshared.ICompanionSessionStateListener;
import com.microsoft.xbox.service.network.managers.xblshared.ICompanionSessionTitleChannelStateListener;
import com.microsoft.xbox.service.network.managers.xblshared.ICompanionSessionTitleListener;
import com.microsoft.xbox.smartglass.canvas.CanvasComponent;
import com.microsoft.xbox.smartglass.canvas.CanvasEvent;
import com.microsoft.xbox.smartglass.canvas.CanvasView;
import com.microsoft.xbox.smartglass.canvas.TaskTracker;
import com.microsoft.xbox.smartglass.canvas.json.JsonClientsUpdated;
import com.microsoft.xbox.smartglass.canvas.json.JsonCompleteRequest;
import com.microsoft.xbox.smartglass.canvas.json.JsonConnectionStateChanged;
import com.microsoft.xbox.smartglass.canvas.json.JsonError;
import com.microsoft.xbox.smartglass.canvas.json.JsonInformationCurrentState;
import com.microsoft.xbox.smartglass.canvas.json.JsonLaunchTitleParameters;
import com.microsoft.xbox.smartglass.canvas.json.JsonTitleChanged;
import com.microsoft.xbox.smartglass.canvas.json.JsonTitleChannelChanged;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.XLELog;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.json.JSONObject;

public class Information implements CanvasComponent, ICompanionSessionStateListener, ICompanionSessionTitleChannelStateListener, ICompanionSessionTitleListener, ICompanionSessionDeviceJoinedListener {
    private static final String COMPONENT_NAME = "Information";
    private static final String GETUSERGAMERCARD_METHOD = "GetUserGamercard";
    private static final String GETUSERINFO_METHOD = "GetUserInfo";
    private static final String LAUNCHTITLE_METHOD = "LaunchTitle";
    private final int MAX_USER_COUNT = 4;
    private CanvasView _canvas;
    private List<String> _currentUserDisplayNames;
    private boolean _isConnected;
    private boolean _isPhysicalDevice;
    private boolean _isTitleChannelEstablished;
    private boolean _isUsingLocalConnection;
    private TaskTracker _tracker;

    public Information(CanvasView canvas) {
        boolean z;
        boolean z2 = true;
        this._canvas = canvas;
        this._isPhysicalDevice = !isEmulator();
        if (CompanionSession.getInstance().getCurrentCapability() == 3) {
            z = true;
        } else {
            z = false;
        }
        this._isUsingLocalConnection = z;
        if (CompanionSession.getInstance().getCurrentSessionState() == 2) {
            z = true;
        } else {
            z = false;
        }
        this._isConnected = z;
        if (CompanionSession.getInstance().getCurrentTitleChannelState() != 2) {
            z2 = false;
        }
        this._isTitleChannelEstablished = z2;
        this._currentUserDisplayNames = new ArrayList();
        this._tracker = new TaskTracker();
        CompanionSession.getInstance().addCompanionSessionStateListener(this);
        CompanionSession.getInstance().addCompanionSessionTitleChannelStateListener(this);
        CompanionSession.getInstance().addCompanionSessionTitleListener(this);
        CompanionSession.getInstance().addCompanionSessionDeviceJoinedListener(this);
    }

    public void stopComponent() {
        this._tracker.cancelAllTasks();
        CompanionSession.getInstance().removeCompanionSessionStateListener(this);
        CompanionSession.getInstance().removeCompanionSessionTitleChannelStateListener(this);
        CompanionSession.getInstance().removeCompanionSessionTitleListener(this);
        CompanionSession.getInstance().removeCompanionSessionDeviceJoinedListener(this);
    }

    private boolean UpdateConnectionStateChanged() {
        boolean isConnected;
        boolean isUsingLocalConnection;
        boolean isChanged;
        if (CompanionSession.getInstance().getCurrentSessionState() == 2) {
            isConnected = true;
        } else {
            isConnected = false;
        }
        if (CompanionSession.getInstance().getCurrentCapability() == 3) {
            isUsingLocalConnection = true;
        } else {
            isUsingLocalConnection = false;
        }
        if (this._isConnected != isConnected) {
            isChanged = true;
        } else {
            isChanged = false;
        }
        this._isConnected = isConnected;
        this._isUsingLocalConnection = isUsingLocalConnection;
        return isChanged;
    }

    private boolean UpdateTitleChannelStateChanged(boolean isTitleChannelEstablished) {
        boolean isChanged = this._isTitleChannelEstablished != isTitleChannelEstablished;
        this._isTitleChannelEstablished = isTitleChannelEstablished;
        return isChanged;
    }

    public void onSessionStateChanged(int newSessionState, XLEException xleException) {
        if (xleException == null && UpdateConnectionStateChanged()) {
            try {
                this._canvas.on(CanvasEvent.ConnectionStateChanged, new JsonConnectionStateChanged(Boolean.valueOf(this._isConnected), Boolean.valueOf(this._isUsingLocalConnection)));
            } catch (Exception exception) {
                XLELog.Diagnostic(COMPONENT_NAME, "Exception occurred: " + exception.getLocalizedMessage());
            }
        }
    }

    public void onTitleChanged(long oldTitleId, long newTitleId) {
        try {
            this._canvas.on(CanvasEvent.TitleChanged, new JsonTitleChanged(newTitleId));
        } catch (Exception exception) {
            XLELog.Diagnostic(COMPONENT_NAME, "Exception occurred: " + exception.getLocalizedMessage());
        }
    }

    public void onDeviceJoined(int clientId, int deviceType, String userDisplayNames) {
        String[] arrayConsoleUsers = userDisplayNames.split(",", 4);
        if (arrayConsoleUsers.length > 0) {
            List<String> disconnectedUsers = new ArrayList(this._currentUserDisplayNames);
            this._currentUserDisplayNames = Arrays.asList(arrayConsoleUsers);
            disconnectedUsers.removeAll(this._currentUserDisplayNames);
            try {
                this._canvas.on(CanvasEvent.ClientsUpdated, new JsonClientsUpdated(this._currentUserDisplayNames, disconnectedUsers));
            } catch (Exception exception) {
                XLELog.Diagnostic(COMPONENT_NAME, "Exception occurred: " + exception.getLocalizedMessage());
            }
        }
    }

    private void launchTitle(int id, JSONObject launchParameters) {
        try {
            if (this._canvas.validateSession(false, id)) {
                JsonLaunchTitleParameters params = new JsonLaunchTitleParameters(launchParameters);
                if (!CanvasView.IsSmartGlassStudioRunning) {
                    List<Integer> allowedTitleIds = this._canvas.getAllowedTitleIds();
                    if (allowedTitleIds == null || !allowedTitleIds.contains(Integer.valueOf(params.getTitleId()))) {
                        this._canvas.completeRequest(new JsonCompleteRequest(id, CanvasEvent.Error, new JsonError("The current activity is not allowed to launch the specified title.")));
                        return;
                    }
                }
                CompanionSession.getInstance().LaunchTitleRaw((long) params.getTitleId(), params.getLaunchParameters());
                this._canvas.completeRequest(new JsonCompleteRequest(id, CanvasEvent.Done, null));
            }
        } catch (Exception exception) {
            XLELog.Diagnostic(COMPONENT_NAME, "Exception occurred: " + exception.getLocalizedMessage());
            this._canvas.completeRequest(new JsonCompleteRequest(id, CanvasEvent.Error, new JsonError(exception.getLocalizedMessage())));
        }
    }

    public void invoke(String methodName, int id, Object arguments) {
        if (methodName.equals(LAUNCHTITLE_METHOD)) {
            launchTitle(id, (JSONObject) arguments);
        } else if (methodName.equals(GETUSERINFO_METHOD)) {
            new UserInfoRequest(this._canvas, this._tracker).sendRequest(id);
        } else if (methodName.equals(GETUSERGAMERCARD_METHOD)) {
            new UserGamercardRequest(this._canvas, this._tracker).sendRequest(id, (String) arguments);
        }
    }

    private boolean isEmulator() {
        return Build.DEVICE.equals("sdk");
    }

    public JSONObject getCurrentState() {
        try {
            return new JsonInformationCurrentState(CompanionSession.getInstance().getCurrentTitleId(), this._isPhysicalDevice, this._isUsingLocalConnection, this._isConnected, this._isTitleChannelEstablished, this._canvas.getLegalLocale());
        } catch (Exception exception) {
            XLELog.Diagnostic(COMPONENT_NAME, "Exception occurred: " + exception.getLocalizedMessage());
            return null;
        }
    }

    public void onTitleChannelStateChanged(int newState, XLEException xleException) {
        if (xleException == null) {
            if (UpdateTitleChannelStateChanged(newState == 2)) {
                try {
                    this._canvas.on(CanvasEvent.TitleChannelChanged, new JsonTitleChannelChanged(this._isTitleChannelEstablished));
                } catch (Exception ex) {
                    XLELog.Diagnostic(COMPONENT_NAME, "Exception occurred: " + ex.getLocalizedMessage());
                }
            }
        }
    }
}
