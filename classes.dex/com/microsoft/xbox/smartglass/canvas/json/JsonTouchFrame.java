package com.microsoft.xbox.smartglass.canvas.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonTouchFrame extends JSONObject {
    private int timeStamp;
    private JsonTouchPoint[] touchPoints;

    public JsonTouchFrame(JSONObject jsonObject) throws JSONException {
        this.timeStamp = jsonObject.getInt("timeStamp");
        JSONArray jsonTouchPoints = jsonObject.getJSONArray("touchPoints");
        int touchPointsCount = jsonTouchPoints.length();
        this.touchPoints = new JsonTouchPoint[touchPointsCount];
        for (int i = 0; i < touchPointsCount; i++) {
            this.touchPoints[i] = new JsonTouchPoint((JSONObject) jsonTouchPoints.get(i));
        }
    }

    public int getTimeStamp() {
        return this.timeStamp;
    }

    public JsonTouchPoint[] getTouchPoints() {
        return this.touchPoints;
    }
}
