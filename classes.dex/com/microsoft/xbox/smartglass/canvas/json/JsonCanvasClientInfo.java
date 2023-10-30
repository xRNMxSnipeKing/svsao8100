package com.microsoft.xbox.smartglass.canvas.json;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonCanvasClientInfo extends JSONObject {
    private String clientVersion = getString("clientVersion");

    public JsonCanvasClientInfo(String jsonString) throws JSONException {
        super(jsonString);
    }

    public String getClientVersion() {
        return this.clientVersion;
    }
}
