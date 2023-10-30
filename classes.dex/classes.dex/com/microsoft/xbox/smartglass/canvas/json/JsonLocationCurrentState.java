package com.microsoft.xbox.smartglass.canvas.json;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonLocationCurrentState extends JSONObject {
    public JsonLocationCurrentState(Boolean hasLocation, Boolean isActive) throws JSONException {
        put("hasLocation", hasLocation);
        put("isActive", isActive);
    }
}
