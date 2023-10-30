package com.microsoft.xbox.smartglass.canvas.json;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonAccelerometer extends JSONObject {
    public JsonAccelerometer(float x, float y, float z) throws JSONException {
        put("x", (double) x);
        put("y", (double) y);
        put("z", (double) z);
    }
}
