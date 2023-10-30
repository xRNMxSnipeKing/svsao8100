package com.microsoft.xbox.smartglass.canvas.json;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonGyroscopeCurrentState extends JSONObject {
    public JsonGyroscopeCurrentState(Boolean hasGyroscope, Boolean isActive) throws JSONException {
        put("hasGyroscope", hasGyroscope);
        put("isActive", isActive);
    }
}
