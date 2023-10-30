package com.microsoft.xbox.smartglass.canvas.json;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonGyroscope extends JSONObject {
    public JsonGyroscope(float x, float y, float z) throws JSONException {
        put("x", (double) x);
        put("y", (double) y);
        put("z", (double) z);
    }
}
