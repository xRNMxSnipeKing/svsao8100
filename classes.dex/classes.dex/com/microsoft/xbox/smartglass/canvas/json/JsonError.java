package com.microsoft.xbox.smartglass.canvas.json;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonError extends JSONObject {
    public JsonError(String message) {
        if (message != null) {
            try {
                put("message", message);
            } catch (JSONException e) {
            }
        }
    }
}
