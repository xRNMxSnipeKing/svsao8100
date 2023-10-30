package com.microsoft.xbox.smartglass.canvas.json;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonConnectionStateChanged extends JSONObject {
    public JsonConnectionStateChanged(Boolean isConnected, Boolean isUsingLocalConnection) throws JSONException {
        put("isUsingLocalConnection", isUsingLocalConnection);
        put("isConnected", isConnected);
    }
}
