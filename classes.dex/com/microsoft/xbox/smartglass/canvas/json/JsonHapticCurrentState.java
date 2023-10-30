package com.microsoft.xbox.smartglass.canvas.json;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonHapticCurrentState extends JSONObject {
    public JsonHapticCurrentState(Boolean hasHaptic, Boolean isActive) throws JSONException {
        put("hasHaptic", hasHaptic);
        put("isActive", isActive);
    }
}
