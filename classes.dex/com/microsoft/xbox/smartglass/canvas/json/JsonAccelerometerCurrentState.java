package com.microsoft.xbox.smartglass.canvas.json;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonAccelerometerCurrentState extends JSONObject {
    public JsonAccelerometerCurrentState(Boolean hasAccelerometer, Boolean isActive) throws JSONException {
        put("hasAccelerometer", hasAccelerometer);
        put("isActive", isActive);
    }
}
