package com.microsoft.xbox.smartglass.canvas.components;

import com.microsoft.xbox.service.model.smartglass.TouchFrame;
import com.microsoft.xbox.service.model.smartglass.TouchPoint;
import com.microsoft.xbox.service.network.managers.xblshared.CompanionSession;
import com.microsoft.xbox.smartglass.canvas.CanvasComponent;
import com.microsoft.xbox.smartglass.canvas.CanvasEvent;
import com.microsoft.xbox.smartglass.canvas.CanvasView;
import com.microsoft.xbox.smartglass.canvas.json.JsonCompleteRequest;
import com.microsoft.xbox.smartglass.canvas.json.JsonTouchFrame;
import com.microsoft.xbox.smartglass.canvas.json.JsonTouchPoint;
import com.microsoft.xbox.toolkit.XLELog;
import java.util.ArrayList;
import org.json.JSONObject;

public class Touch implements CanvasComponent {
    private static final String COMPONENT_NAME = "Touch";
    private static final String SENDTOUCHFRAME_METHOD = "SendTouchFrame";
    private CanvasView _canvas;

    public Touch(CanvasView canvas) {
        this._canvas = canvas;
    }

    public void stopComponent() {
    }

    public void invoke(String methodName, int id, Object arguments) {
        if (methodName.equals(SENDTOUCHFRAME_METHOD) && arguments != null) {
            sendTouchFrame(id, (JSONObject) arguments);
        }
    }

    public JSONObject getCurrentState() {
        return null;
    }

    public void sendTouchFrame(int id, JSONObject jsonObject) {
        try {
            if (this._canvas.validateSession(true, id)) {
                JsonTouchFrame touchFrame = new JsonTouchFrame(jsonObject);
                TouchFrame jFrame = new TouchFrame();
                ArrayList<TouchPoint> touchPoints = new ArrayList();
                for (JsonTouchPoint jsonPoint : touchFrame.getTouchPoints()) {
                    TouchPoint point = new TouchPoint();
                    point.id = jsonPoint.getId();
                    point.action = jsonPoint.getAction();
                    point.xval = (float) jsonPoint.getX();
                    point.yval = (float) jsonPoint.getY();
                    touchPoints.add(point);
                }
                jFrame.timestamp = (long) touchFrame.getTimeStamp();
                jFrame.points = (TouchPoint[]) touchPoints.toArray(new TouchPoint[0]);
                XLELog.Diagnostic(COMPONENT_NAME, "Sending touch frame with timestamp = " + jFrame.timestamp);
                CompanionSession.getInstance().SendTitleTouchFrame(jFrame);
                this._canvas.completeRequest(new JsonCompleteRequest(id, CanvasEvent.Done, null));
            }
        } catch (Exception exception) {
            XLELog.Diagnostic(COMPONENT_NAME, "Exception occurred: " + exception.getLocalizedMessage());
        }
    }
}
